package org.example.divar.chat.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.divar.chat.model.Conversation;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;

/**
 * Custom UI component representing a conversation list item within the chat panel.
 * This component resolves the correct participant's display name relative to the current session,
 * formats it alongside the associated advertisement title, and displays the latest message snippet.
 */
public class ChatList extends VBox {

    @FXML
    private Label nameLabel;
    @FXML
    private Label lastMessageLabel;

    /**
     * Initializes and populates the chat list component by loading its FXML layout,
     * determining the opposing party's username based on the active session,
     * fetching their full display name via service calls, and binding conversation metadata to the view labels.
     *
     * @param conversation the {@link Conversation} data object containing chat participants, ad title, and recent messages
     */
    public ChatList(Conversation conversation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/chat_list.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            String myUsername = SessionManager.getCurrentUsername();
            String otherUsername;

            if (conversation.getBuyerUsername().equals(myUsername)) {
                otherUsername = conversation.getSellerUsername();
            } else {
                otherUsername = conversation.getBuyerUsername();
            }

            String displayName;
            try {
                displayName = AppContext.getUserService().getNameByUsername(otherUsername);
            } catch (RuntimeException e) {
                System.err.println("Could not resolve display name for " + otherUsername + ": " + e.getMessage());
                displayName = otherUsername;
            }
            String adTitle = conversation.getAdvertisementTitle();
            String title = displayName + " (" + adTitle + ")";
            nameLabel.setText(title);
            lastMessageLabel.setText(conversation.getLastMessage());

        } catch (Exception e) {
            System.err.println("Error loading chat list item: " + e.getMessage());
        }
    }
}
