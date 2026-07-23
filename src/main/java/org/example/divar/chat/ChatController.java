package org.example.divar.chat;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.divar.SwitchStage;
import org.example.divar.chat.component.ChatList;
import org.example.divar.chat.component.MessageSection;
import org.example.divar.chat.model.Conversation;
import org.example.divar.chat.model.Message;
import org.example.divar.chat.service.ChatService;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ChatController {

    @FXML
    private ListView<Conversation> chatsList;

    @FXML
    private VBox messagesContainer;

    @FXML
    private TextField messageField;

    @FXML
    private Label chatHeaderLabel;

    @FXML
    private ScrollPane chatScrollPane;

    private ChatService chatService;
    private Long currentUserId;
    private Conversation selectedConversation;

    private CompletableFuture<Long> currentUserIdFuture;

    private volatile boolean disposed;

    private volatile boolean connecting;

    private volatile boolean connected;

    @FXML
    public void initialize() {
        System.out.println("CHAT CONTROLLER INITIALIZE");

        disposed = false;
        connecting = false;
        connected = false;

        chatService = AppContext.getChatService();

        configureChatsList();
        configureConversationSelection();
        configureMessageField();

        initializeUser();
        loadConversations();

        if (currentUserId != null) {
            connectToChatService();
        }
    }

    private void initializeUser() {
        try {
            String username = SessionManager.getCurrentUsername();
            if (username != null && !username.isBlank()) {
                String rawId = AppContext.getUserService().getUserProfile(username).getId();
                this.currentUserId = Long.parseLong(rawId);
            } else {
                showError("کاربر وارد نشده است.");
            }
        } catch (Exception e) {
            logError("خطا در بارگذاری اطلاعات کاربر", e);
            showError("خطا در احراز هویت");
        }
    }


    public void initializeChat(Conversation conversation) {
        selectedConversation = conversation;

        ensureCurrentUserIdAsync(() -> {
            if (disposed) {
                return;
            }
            addConversationIfAbsent(conversation);
            selectConversation(conversation);
            connectToChatService();
        });
    }

    private void configureChatsList() {

        chatsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Conversation conversation, boolean empty) {
                super.updateItem(conversation, empty);

                if (empty || conversation == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(new ChatList(conversation));
                }
            }
        });
    }

    private void configureConversationSelection() {
        if (chatsList == null) {
            System.err.println("WARNING: chatsList was not injected from FXML.");
            return;
        }

        chatsList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldConversation, newConversation) -> {
                    if (newConversation != null && !disposed) {
                        loadSelectedConversation(newConversation);
                    }
                });
    }

    private void configureMessageField() {
        if (messageField == null) {
            System.err.println("WARNING: messageField was not injected from FXML.");
            return;
        }

        messageField.setOnAction(event -> sendMessage());
    }

    private void ensureCurrentUserIdAsync(Runnable afterReady) {
        Objects.requireNonNull(afterReady, "afterReady cannot be null");

        if (disposed) {
            return;
        }

        if (currentUserId != null) {
            runOnFxThread(afterReady);
            return;
        }

        CompletableFuture<Long> future = getOrCreateCurrentUserIdFuture();

        future.thenAccept(userId -> runOnFxThread(() -> {
                    if (disposed) {
                        return;
                    }

                    currentUserId = userId;
                    afterReady.run();
                }))
                .exceptionally(error -> {
                    logError("CURRENT USER ID LOADING FAILED", error);

                    runOnFxThread(() -> {
                        if (!disposed) {
                            showError(
                                    "خطا در دریافت شناسه کاربر: "
                                            + error
                            );
                        }
                    });
                    return null;
                });
    }

    private synchronized CompletableFuture<Long> getOrCreateCurrentUserIdFuture() {
        if (currentUserIdFuture != null) {
            return currentUserIdFuture;
        }

        String username = SessionManager.getCurrentUsername();

        if (username == null || username.isBlank()) {
            currentUserIdFuture = CompletableFuture.failedFuture(
                    new IllegalStateException("Current username is null or blank")
            );
            return currentUserIdFuture;
        }

        System.out.println("Loading current user profile: " + username);

        currentUserIdFuture = CompletableFuture.supplyAsync(() -> {
            Object rawId = AppContext.getUserService()
                    .getUserProfile(username)
                    .getId();

            if (rawId == null) {
                throw new IllegalStateException("Current user profile ID is null");
            }

            return Long.parseLong(rawId.toString());
        });

        return currentUserIdFuture;
    }

    private void loadConversations() {
        if (disposed) {
            System.out.println("Disposed");
            return;
        }

        System.out.println("Loading conversations through REST...");

        CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Inside loadConversationsAsync");
                    List<Conversation> conversations =
                            AppContext.getConversationService().getConversations();

                    return conversations != null
                            ? conversations
                            : Collections.<Conversation>emptyList();
                })
                .thenAccept(conversations -> runOnFxThread(() -> {
                    if (disposed || chatsList == null) {
                        return;
                    }

                    System.out.println(
                            "Conversations loaded. count=" + conversations.size()
                    );

                    Conversation conversationToKeep = selectedConversation;
                    chatsList.getItems().setAll(conversations);

                    if (conversationToKeep != null) {
                        addConversationIfAbsent(conversationToKeep);
                        selectConversation(conversationToKeep);
                    }
                }))
                .exceptionally(error -> {
                    logError("CONVERSATIONS LOADING FAILED", error);

                    runOnFxThread(() -> {
                        if (!disposed) {
                            showError(
                                    "خطا در دریافت فهرست گفتگوها: "
                                            + error
                            );
                        }
                    });
                    return null;
                });
    }

    private void selectConversation(Conversation conversation) {
        if (conversation == null) {
            return;
        }

        if (chatsList == null) {
            loadSelectedConversation(conversation);
            return;
        }

        Conversation existingConversation = chatsList.getItems()
                .stream()
                .filter(item -> isSameConversation(item, conversation))
                .findFirst()
                .orElse(conversation);

        chatsList.getSelectionModel().select(existingConversation);


        if (!Objects.equals(
                chatsList.getSelectionModel().getSelectedItem(),
                existingConversation
        )) {
            loadSelectedConversation(existingConversation);
        }
    }

    private void addConversationIfAbsent(Conversation conversation) {
        if (chatsList == null || conversation == null) {
            return;
        }

        boolean alreadyExists = chatsList.getItems()
                .stream()
                .anyMatch(item -> isSameConversation(item, conversation));

        if (!alreadyExists) {
            chatsList.getItems().add(conversation);
        }
    }

    private void loadSelectedConversation(Conversation conversation) {
        if (conversation == null || disposed) {
            return;
        }

        selectedConversation = conversation;

        if (messagesContainer != null) {
            messagesContainer.getChildren().clear();
        }

        updateConversationHeader();

        List<Message> messages = conversation.getMessages();
        if (messages != null) {
            for (Message message : messages) {
                showMessageInUI(message);
            }
        }

        scrollToBottom();
    }

    private void updateConversationHeader() {
        if (chatHeaderLabel == null) {
            return;
        }

        if (selectedConversation == null) {
            chatHeaderLabel.setText(connected ? "سرویس چت متصل است" : "گفتگو");
            return;
        }

        chatHeaderLabel.setText(
                "گفتگو با " + getOtherUsername(selectedConversation)
        );
    }

    @FXML
    private void sendMessage() {
        if (disposed || messageField == null) {
            return;
        }

        String text = messageField.getText();
        if (text == null || text.isBlank()) {
            return;
        }

        if (chatService == null) {
            showError("سرویس چت مقداردهی نشده است.");
            return;
        }

        if (selectedConversation == null) {
            showError("ابتدا یک گفتگو را انتخاب کنید.");
            return;
        }

        if (currentUserId == null) {
            showError("شناسه کاربر هنوز دریافت نشده است.");
            return;
        }

        if (connecting) {
            showError("اتصال چت هنوز در حال برقرار شدن است.");
            return;
        }

        if (!connected) {
            showError("اتصال چت برقرار نیست؛ تلاش مجدد...");
            connectToChatService();
            return;
        }

        Long receiverId = getOtherUserId(selectedConversation);
        if (receiverId == null) {
            showError("شناسه طرف مقابل معتبر نیست.");
            return;
        }

        Message newMessage = new Message(
                currentUserId,
                receiverId,
                text.trim(),
                selectedConversation.getAdId()
        );

        selectedConversation.addMessage(newMessage);
        showMessageInUI(newMessage);
        messageField.clear();
        scrollToBottom();

        try {
            System.out.println(
                    "Sending message: senderId=" + currentUserId
                            + ", receiverId=" + receiverId
                            + ", adId=" + selectedConversation.getAdId()
            );

            chatService.sendMessage(newMessage);
            newMessage.setStatus("SENT");
        } catch (RuntimeException exception) {
            newMessage.setStatus("FAILED");
            logError("MESSAGE SENDING FAILED", exception);
            showError("ارسال پیام ناموفق بود: " + errorMessage(exception));
        }
    }

    private Long getOtherUserId(Conversation conversation) {
        if (conversation == null || currentUserId == null) {
            return null;
        }

        if (Objects.equals(currentUserId, conversation.getBuyerId())) {
            return conversation.getSellerId();
        }

        if (Objects.equals(currentUserId, conversation.getSellerId())) {
            return conversation.getBuyerId();
        }

        return null;
    }

    private synchronized void connectToChatService() {
        System.out.println(
                "connectToChatService called: disposed=" + disposed
                        + ", connecting=" + connecting
                        + ", connected=" + connected
                        + ", currentUserId=" + currentUserId
                        + ", chatService="
                        + (chatService == null
                        ? "null"
                        : chatService.getClass().getName())
        );

        if (disposed) {
            System.out.println("Connection skipped: controller is disposed.");
            return;
        }

        if (connecting) {
            System.out.println("Connection skipped: already connecting.");
            return;
        }

        if (connected) {
            System.out.println("Connection skipped: already connected.");
            return;
        }

        if (currentUserId == null) {
            System.out.println("Connection postponed: currentUserId is null.");
            return;
        }

        if (chatService == null) {
            IllegalStateException exception =
                    new IllegalStateException("ChatService is null");
            logError("CHAT CONNECTION CANNOT START", exception);
            showError("سرویس چت مقداردهی نشده است.");
            return;
        }

        connecting = true;
        connected = false;

        System.out.println(
                "=== STARTING CHAT CONNECTION ===\n"
                        + "userId=" + currentUserId + "\n"
                        + "service=" + chatService.getClass().getName()
        );

        try {
            chatService.connect(
                    currentUserId,
                    new ChatService.MessageListener() {
                        @Override
                        public void onMessage(Message message) {
                            System.out.println(
                                    "Live message received: senderId="
                                            + (message == null
                                            ? null
                                            : message.getSenderId())
                                            + ", receiverId="
                                            + (message == null
                                            ? null
                                            : message.getReceiverId())
                                            + ", adId="
                                            + (message == null
                                            ? null
                                            : message.getAdId())
                            );

                            runOnFxThread(() -> {
                                if (!disposed) {
                                    onLiveMessageReceived(message);
                                }
                            });
                        }

                        @Override
                        public void onConnected() {
                            connecting = false;
                            connected = true;

                            System.out.println(
                                    "=== CHAT CONNECTION ESTABLISHED ===\n"
                                            + "userId=" + currentUserId
                            );

                            runOnFxThread(() -> {
                                if (!disposed) {
                                    updateConversationHeader();
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable error) {
                            connecting = false;
                            connected = false;

                            logError("CHAT CONNECTION ERROR", error);

                            runOnFxThread(() -> {
                                if (!disposed) {
                                    showError(
                                            "خطا در اتصال به سرویس چت: "
                                                    + error.getMessage()
                                    );
                                }
                            });
                        }
                    }
            );

            System.out.println(
                    "ChatService.connect(...) returned without a synchronous exception."
            );
        } catch (RuntimeException exception) {
            connecting = false;
            connected = false;

            logError("CHAT CONNECT CALL FAILED SYNCHRONOUSLY", exception);

            runOnFxThread(() -> {
                if (!disposed) {
                    showError(
                            "برقراری اتصال چت ناموفق بود: "
                                    + exception.getClass().getSimpleName()
                                    + " - "
                                    + errorMessage(exception)
                    );
                }
            });
        }
    }

    private void onLiveMessageReceived(Message receivedMessage) {
        if (disposed
                || receivedMessage == null
                || currentUserId == null
                || chatsList == null) {
            return;
        }

        if (!Objects.equals(currentUserId, receivedMessage.getReceiverId())) {
            System.err.println(
                    "Ignoring message: receiverId does not match currentUserId."
            );
            return;
        }

        Conversation targetConversation = chatsList.getItems()
                .stream()
                .filter(conversation ->
                        belongsToConversation(receivedMessage, conversation)
                )
                .findFirst()
                .orElse(null);

        if (targetConversation == null) {
            System.out.println("Target conversation is null!");
            loadConversations();
            return;
        }

        if (!containsMessage(targetConversation, receivedMessage)) {
            targetConversation.addMessage(receivedMessage);
        }

        if (selectedConversation != null
                && isSameConversation(selectedConversation, targetConversation)) {
            showMessageInUI(receivedMessage);
            scrollToBottom();
        }

        chatsList.refresh();
    }

    private boolean belongsToConversation(
            Message message,
            Conversation conversation
    ) {
        if (message == null || conversation == null) {
            return false;
        }

        Long senderId = message.getSenderId();
        Long receiverId = message.getReceiverId();
        Long buyerId = conversation.getBuyerId();
        Long sellerId = conversation.getSellerId();

        boolean directDirection =
                Objects.equals(senderId, buyerId)
                        && Objects.equals(receiverId, sellerId);

        boolean reverseDirection =
                Objects.equals(senderId, sellerId)
                        && Objects.equals(receiverId, buyerId);

        boolean sameParticipants = directDirection || reverseDirection;

        boolean sameAd = Objects.equals(message.getAdId(), conversation.getAdId());

        return sameParticipants && sameAd;
    }

    private boolean isSameConversation(
            Conversation first,
            Conversation second
    ) {
        if (first == second) {
            return true;
        }

        if (first == null || second == null) {
            return false;
        }

        boolean sameDirection =
                Objects.equals(first.getBuyerId(), second.getBuyerId())
                        && Objects.equals(
                        first.getSellerId(),
                        second.getSellerId()
                );

        boolean reverseDirection =
                Objects.equals(first.getBuyerId(), second.getSellerId())
                        && Objects.equals(
                        first.getSellerId(),
                        second.getBuyerId()
                );

        boolean sameParticipants = sameDirection || reverseDirection;
        boolean sameAd = Objects.equals(first.getAdId(), second.getAdId());

        return sameParticipants && sameAd;
    }

    private boolean containsMessage(
            Conversation conversation,
            Message candidate
    ) {
        if (conversation == null
                || candidate == null
                || conversation.getMessages() == null) {
            return false;
        }

        if (candidate.getId() == null) {
            return false;
        }

        return conversation.getMessages()
                .stream()
                .anyMatch(existing ->
                        existing != null
                                && Objects.equals(
                                existing.getId(),
                                candidate.getId()
                        )
                );
    }

    private void showMessageInUI(Message message) {
        if (message == null || messagesContainer == null) {
            return;
        }

        Label messageLabel = new Label(message.getText());
        messageLabel.setWrapText(true);

        boolean isMyMessage = currentUserId != null
                && Objects.equals(currentUserId, message.getSenderId());

        messageLabel.getStyleClass().add(
                isMyMessage ? "my-message" : "their-message"
        );
        MessageSection messageUI = new MessageSection(message, currentUserId);
        messagesContainer.getChildren().add(messageUI);
    }

    private void scrollToBottom() {
        runOnFxThread(() -> {
            if (!disposed && chatScrollPane != null) {
                chatScrollPane.setVvalue(1.0);
            }
        });
    }

    private String getOtherUsername(Conversation conversation) {
        if (conversation == null || currentUserId == null) {
            return "-";
        }

        if (Objects.equals(currentUserId, conversation.getBuyerId())) {
            return defaultString(conversation.getSellerUsername());
        }

        if (Objects.equals(currentUserId, conversation.getSellerId())) {
            return defaultString(conversation.getBuyerUsername());
        }

        return "-";
    }

    private String defaultString(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private void logError(String title, Throwable throwable) {
        System.err.println("[CHAT ERROR] " + title + ": " + throwable.getMessage());
        throwable.printStackTrace(System.err);
    }

    private String errorMessage(Throwable throwable) {
        if (throwable == null
                || throwable.getMessage() == null
                || throwable.getMessage().isBlank()) {
            return "بدون توضیح";
        }

        return throwable.getMessage();
    }


    private void runOnFxThread(Runnable action) {
        if (action == null) {
            return;
        }

        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }


    //Make sure connection is free
    public void cleanup() {
        if (disposed) {
            return;
        }

        System.out.println("=== CHAT CONTROLLER CLEANUP ===");

        disposed = true;
        connecting = false;
        connected = false;

        if (chatService != null) {
            try {
                chatService.disconnect();
                System.out.println("ChatService disconnected successfully.");
            } catch (RuntimeException exception) {
                logError("CHAT DISCONNECT FAILED", exception);
            }
        }
    }

    @FXML
    private void goBack() {
        cleanup();
        SwitchStage.goBack();
    }

    @FXML
    private void goToProfile() {
        cleanup();
        SwitchStage.switchToProfile();
    }

    private void showError(String text) {
        if (chatHeaderLabel != null) {
            chatHeaderLabel.setText(text);
        }

        System.err.println("CHAT UI ERROR: " + text);
    }
}
