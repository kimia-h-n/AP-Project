package org.example.divar.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import org.example.divar.SwitchStage;
import org.example.divar.component.ChatList;
import org.example.divar.component.MessageSection;
import org.example.divar.model.Conversation;
import org.example.divar.model.Message;
import org.example.divar.util.AppContext;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.divar.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ChatController {

    @FXML private ListView<Conversation> chatsList;
    @FXML private Label chatHeaderLabel;
    @FXML private VBox messagesContainer;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;

    private Conversation selectedConversation = null;

    @FXML
    public void initialize() {

        loadTargetChatsList();

        chatsList.setCellFactory(param -> new ListCell<>() {
            @Override
            // یک متد آماده درون ListCell است که وقتی برای اولین بار لود میکنیم باید حتما اورراید کنیم
            // اگر empty برابر true باشه یعنی این ردیف خالیه و میتونه یه چت داخلش قرار بگیره
            protected void updateItem(Conversation item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(new ChatList(item));
                }
            }
        });

        MultipleSelectionModel<Conversation> selectionModel = chatsList.getSelectionModel();
        ReadOnlyObjectProperty<Conversation> selectedItemProp = selectionModel.selectedItemProperty();
        selectedItemProp.addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadSelectedConversation(newSelection);
            }
        });

        boolean hasChats = !chatsList.getItems().isEmpty();
        if (hasChats) {
            selectionModel.select(0);
        }
    }

    public void openConversation(Conversation conversation) {
        if (!chatsList.getItems().contains(conversation)) {
            chatsList.getItems().add(conversation);
        }
        chatsList.getSelectionModel().select(conversation);
    }

    private void loadTargetChatsList() {
        String currentUsername = SessionManager.getCurrentUsername();

        if (currentUsername == null) return;

        List<Conversation> conversations = new ArrayList<>();

        List<Conversation> allConversations = AppContext.getConversationService().getConversations();

        for (Conversation conversation : allConversations) {
            if (conversation.getBuyerUsername().equals(currentUsername) ||
                    conversation.getSellerUsername().equals(currentUsername)) {

                conversations.add(conversation);
            }
        }

        chatsList.getItems().setAll(conversations);
    }


    private void ShowMessageInUI(Message message) {
        MessageSection messageUI = new MessageSection(message);
        messagesContainer.getChildren().add(messageUI);
    }

    private void loadSelectedConversation(Conversation conversation) {
        selectedConversation = conversation;

        String myUsername = SessionManager.getCurrentUsername();
        String otherUsername = conversation.getBuyerUsername().equals(myUsername)
                ? conversation.getSellerUsername()
                : conversation.getBuyerUsername();

        String displayName = AppContext.getUserService().getNameByUsername(otherUsername);
        chatHeaderLabel.setText(displayName + " آگهی: " + conversation.getAdvertisement().getTitle());

        messagesContainer.getChildren().clear();
        for (Message message : conversation.getMessages()) {
            ShowMessageInUI(message);
        }

        chatScrollPane.setVvalue(1.0);
    }


    @FXML
    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty() && selectedConversation != null) {
            String myUsername = SessionManager.getCurrentUsername();

            String otherUsername = selectedConversation.getBuyerUsername().equals(myUsername)
                    ? selectedConversation.getSellerUsername()
                    : selectedConversation.getBuyerUsername();

            Message newMessage = new Message(myUsername, otherUsername, text);
            selectedConversation.addMessage(newMessage);
            ShowMessageInUI(newMessage);

            messageField.clear();

            chatsList.refresh();

            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }


    @FXML
    private void goBack() {
        SwitchStage.goBack();
    }

    @FXML
    private void goToProfile() {
        SwitchStage.switchToProfile();
    }
}


