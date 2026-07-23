package org.example.divar.chat.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.divar.chat.model.Conversation;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;

public class ChatList extends VBox {

    @FXML
    private Label nameLabel;
    @FXML
    private Label lastMessageLabel;

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
