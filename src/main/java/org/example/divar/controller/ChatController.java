package org.example.divar.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import org.example.divar.SwitchStage;
import org.example.divar.component.ChatList;
import org.example.divar.component.MessageSection;
import org.example.divar.model.Conversation;
import org.example.divar.model.Message;
import org.example.divar.service.ChatSocketService;
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

    private ChatSocketService chatSocketService = null;

    @FXML
    public void initialize() {
        loadTargetChatsList();

        connectToChatSocket();

        chatsList.setCellFactory(param -> new ListCell<>() {
            @Override
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

            // منطق وب‌سوکت جدید برای فرستادن پیام به سرور
            boolean myUsernameIsBuyer = selectedConversation.getBuyerUsername().equals(myUsername);
            Long otherId = myUsernameIsBuyer
                    ? selectedConversation.getSellerId()
                    : selectedConversation.getBuyerId();

            if (otherId != null && chatSocketService != null) {
                chatSocketService.sendLiveMessage(otherId, text);
            }

            messageField.clear();
            chatsList.refresh();
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        }
    }

    private void connectToChatSocket() {
        String myUsername = SessionManager.getCurrentUsername();
        if (myUsername == null) return;

        try {
            String myId = AppContext.getUserService().getUserProfile(myUsername).getId();
            long myUserId = Long.parseLong(myId);

            chatSocketService = new ChatSocketService(myUserId, this);
            chatSocketService.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLiveMessageReceived(long senderId, long receiverId, String text) {
        String myUsername = SessionManager.getCurrentUsername();

        for (Conversation conversation : chatsList.getItems()) {
            boolean myUsernameIsBuyer = conversation.getBuyerUsername().equals(myUsername);
            Long otherId = myUsernameIsBuyer ? conversation.getSellerId() : conversation.getBuyerId();

            if (otherId != null && otherId == senderId) {
                String otherUsername = myUsernameIsBuyer
                        ? conversation.getSellerUsername()
                        : conversation.getBuyerUsername();

                Message newMessage = new Message(otherUsername, myUsername, text);
                conversation.addMessage(newMessage);

                if (conversation == selectedConversation) {
                    ShowMessageInUI(newMessage);
                    chatScrollPane.setVvalue(1.0);
                }
                chatsList.refresh();
                return;
            }
        }
    }

    @FXML
    private void goBack() {
        if (chatSocketService != null) {
            chatSocketService.close();
        }
        SwitchStage.goBack();
    }

    @FXML
    private void goToProfile() {
        if (chatSocketService != null) {
            chatSocketService.close();
        }
        SwitchStage.switchToProfile();
    }
}


