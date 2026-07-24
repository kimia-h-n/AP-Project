package org.example.divar.chat.service;

import org.example.divar.model.Advertisement;
import org.example.divar.chat.model.Conversation;
import org.example.divar.chat.model.Message;
import org.example.divar.util.ApiClient;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

/**
 * HTTP implementation of the {@link ConversationService} interface responsible for managing chat conversations,
 * synchronizing chat threads from the server, loading message histories, resolving user identifiers,
 * and handling conversation state between buyers and sellers.
 */
public class ConversationServiceHttp implements ConversationService {

    private final ArrayList<Conversation> conversations = new ArrayList<>();

    /**
     * Finds an existing local conversation matching the advertisement and participants,
     * or creates a new conversation, loads user IDs, fetches message history from the server,
     * and adds it to the active list.
     *
     * @param advertisement the advertisement associated with the chat
     * @param buyerUsername the username of the buyer
     * @param sellerUsername the username of the seller
     * @return the existing or newly created {@link Conversation} object
     */
    @Override
    public Conversation findOrCreateConversation(
            Advertisement advertisement,
            String buyerUsername,
            String sellerUsername
    ) {
        Objects.requireNonNull(advertisement, "advertisement cannot be null");
        Objects.requireNonNull(buyerUsername, "buyerUsername cannot be null");
        Objects.requireNonNull(sellerUsername, "sellerUsername cannot be null");

        Conversation existingConversation = findExistingConversation(advertisement, buyerUsername, sellerUsername);
        if (existingConversation != null) {
            return existingConversation;
        }

        Conversation conversation = new Conversation(advertisement, buyerUsername, sellerUsername);
        loadIdsAndHistory(conversation);

        conversations.add(conversation);
        return conversation;
    }

    /**
     * Retrieves all conversations for the current user, attempting to synchronize
     * the latest summaries and messages from the server first.
     *
     * @return an {@link ArrayList} of active {@link Conversation} items
     */
    @Override
    public ArrayList<Conversation> getConversations() {
        try {
            syncFromServer();
        } catch (RuntimeException e) {
            System.err.println("خطا در دریافت فهرست گفتگوها از سرور: " + e.getMessage());
        }
        return conversations;
    }

    /**
     * Synchronizes conversations from the server using the current session username
     * and user ID, fetching summary lists and merging new threads.
     */
    private void syncFromServer() {
        String myUsername = SessionManager.getCurrentUsername();
        if (myUsername == null || myUsername.isBlank()) {
            return;
        }

        long myUserId = loadUserId(myUsername);

        JSONArray summaries = ApiClient.getList("/api/v1/conversations/user/" + myUserId);

        for (int i = 0; i < summaries.length(); i++) {
            mergeSummary(myUserId, myUsername, summaries.getJSONObject(i));
        }
    }

    /**
     * Merges a conversation summary received from the server into the local cache,
     * checking for duplicates and loading its full message history.
     */
    private void mergeSummary(long myUserId, String myUsername, JSONObject summaryJson) {
        long contactId = summaryJson.getLong("contactId");

        Long adId = summaryJson.has("adId") && !summaryJson.isNull("adId")
                ? summaryJson.getLong("adId")
                : null;
        String adTitle = summaryJson.optString("adTitle", null);
        boolean alreadyKnown = conversations.stream()
                .anyMatch(c -> isSameThread(c, myUserId, contactId, adId));

        if (alreadyKnown) {
            System.out.println("***isAlready knows");
            return;
        }

        String contactUsername = summaryJson.optString("contactUsername", null);
        if (contactUsername == null || contactUsername.isBlank()) {
            System.err.println("گیرنده username ندارد");
            return;
        }

        Conversation conversation = new Conversation(adId, adTitle, myUsername, contactUsername);
        conversation.setBuyerId(myUserId);
        conversation.setSellerId(contactId);

        try {
            loadHistoryInto(conversation, myUserId, contactId);
        } catch (RuntimeException e) {
            System.err.println(
                    "خطا در دریافت تاریخچه‌ی گفتگو با contactId=" + contactId + ": " + e.getMessage()
            );
        }

        conversations.add(conversation);
    }

    /**
     * Checks if two conversation threads refer to the same participants and advertisement.
     */
    private boolean isSameThread(Conversation conversation, long userId, long otherId, Long adId) {
        boolean direct = Objects.equals(conversation.getBuyerId(), userId)
                && Objects.equals(conversation.getSellerId(), otherId);

        boolean reverse = Objects.equals(conversation.getBuyerId(), otherId)
                && Objects.equals(conversation.getSellerId(), userId);

        boolean sameParticipants = direct || reverse;
        boolean sameAd = Objects.equals(conversation.getAdId(), adId);

        return sameParticipants && sameAd;
    }

    /**
     * Searches the local list for an existing conversation matching the ad and participants.
     */
    private Conversation findExistingConversation(
            Advertisement advertisement,
            String buyerUsername,
            String sellerUsername
    ) {
        for (Conversation conversation : conversations) {

            boolean sameAdvertisement = Objects.equals(
                    conversation.getAdId(),
                    advertisement.getId()
            );

            boolean sameBuyer = Objects.equals(
                    conversation.getBuyerUsername(),
                    buyerUsername
            );

            boolean sameSeller = Objects.equals(
                    conversation.getSellerUsername(),
                    sellerUsername
            );

            if (sameAdvertisement && sameBuyer && sameSeller) {
                return conversation;
            }
        }
        return null;
    }

    /**
     * Resolves and populates buyer and seller user IDs, then loads their message history.
     */
    private void loadIdsAndHistory(Conversation conversation) {
        try {
            long buyerId = loadUserId(conversation.getBuyerUsername());
            long sellerId = loadUserId(conversation.getSellerUsername());

            conversation.setBuyerId(buyerId);
            conversation.setSellerId(sellerId);
            loadHistoryInto(conversation, buyerId, sellerId);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "خطا در دریافت اطلاعات گفتگو: " + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Fetches the message history between two users for a specific ad from the server
     * and parses each message into the conversation object.
     */
    private void loadHistoryInto(Conversation conversation, long user1Id, long user2Id) {
        StringBuilder path = new StringBuilder("/api/v1/conversations")
                .append("?senderId=").append(user1Id)
                .append("&receiverId=").append(user2Id);

        if (conversation.getAdId() != null) {
            path.append("&adId=").append(conversation.getAdId());
        }

        JSONArray history = ApiClient.getList(path.toString());

        for (int i = 0; i < history.length(); i++) {
            JSONObject messageJson = history.getJSONObject(i);
            conversation.addMessage(parseMessage(messageJson, conversation));
        }
    }

    /**
     * Fetches the numeric user ID corresponding to a given username via the user service.
     */
    private long loadUserId(String username) {
        String userId = AppContext.getUserService().getUserProfile(username).getId();
        if (userId == null || userId.isBlank()) {
            throw new IllegalStateException("User ID is missing for username: " + username);
        }
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid numeric user ID for username " + username + ": " + userId, exception);
        }
    }

    /**
     * Parses raw JSON message data into a domain {@link Message} object.
     */
    private Message parseMessage(JSONObject messageJson, Conversation conversation) {
        Long messageId = messageJson.has("id") && !messageJson.isNull("id")
                ? messageJson.getLong("id")
                : null;

        long senderId = messageJson.getLong("senderId");
        Instant sentAt = Instant.parse(messageJson.getString("sentAt"));

        long receiverId = resolveReceiverId(messageJson, senderId, conversation);
        String text = messageJson.optString("message", "");

        return new Message(messageId, senderId, receiverId, text, sentAt, conversation.getAdId());
    }

    /**
     * Resolves the receiver ID for a message, falling back to participant logic if missing in JSON.
     */
    private long resolveReceiverId(JSONObject messageJson, long senderId, Conversation conversation) {
        if (messageJson.has("receiverId") && !messageJson.isNull("receiverId")) {
            return messageJson.getLong("receiverId");
        }

        if (senderId == conversation.getBuyerId()) {
            return conversation.getSellerId();
        }

        if (senderId == conversation.getSellerId()) {
            return conversation.getBuyerId();
        }

        throw new IllegalArgumentException("Message sender " + senderId + " does not belong to conversation");
    }
}
