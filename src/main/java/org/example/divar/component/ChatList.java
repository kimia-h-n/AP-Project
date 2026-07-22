package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.divar.model.Conversation;
import org.example.divar.util.AppContext;
import org.example.divar.util.SessionManager;

public class ChatList extends VBox {

    @FXML private Label nameLabel;
    @FXML private Label lastMessageLabel;

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

            String displayName = AppContext.getUserService().getNameByUsername(otherUsername);

            nameLabel.setText(displayName + " (" + conversation.getAdvertisementTitle() + ")");
            lastMessageLabel.setText(conversation.getLastMessage());

        } catch (Exception e) {
            System.err.println("Error loading chat list item: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


