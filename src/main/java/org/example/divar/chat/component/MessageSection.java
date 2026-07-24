package org.example.divar.chat.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.divar.chat.model.Message;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * This class is created to display each message in the chat view.
 * First it checks methods and variables to make sure they are not null, then loads the FXML file
 * and sets the message bubble style and color based on whether the message belongs to the current user or the other person.
 */
public class MessageSection extends HBox {

    @FXML
    private VBox messageBox;

    @FXML
    private Label textLabel;

    @FXML
    private Label timeLabel;

    private final Message message;
    private final Long currentUserId;

    /**
     * Constructor that takes the message object and the user ID.
     * Throws an exception if inputs are null, otherwise loads the layout.
     *
     * @param message the message object containing text and sender info
     * @param currentUserId the ID of the user who is currently logged in
     */
    public MessageSection(Message message, Long currentUserId) {
        this.message = Objects.requireNonNull(
                message,
                "message cannot be null"
        );

        this.currentUserId = Objects.requireNonNull(
                currentUserId,
                "currentUserId cannot be null"
        );

        loadFXML();
        initializeMessage();
    }

    /**
     * Loads the FXML file for the message bubble and sets this class as the controller.
     */
    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/divar/fxml/message.fxml")
        );

        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load message.fxml",
                    e
            );
        }
    }

    /**
     * Sets the text and time of the message.
     * Here it checks if the message sender is the current user or not to apply right-aligned or left-aligned styles.
     */
    private void initializeMessage() {
        textLabel.setText(message.getText());
        timeLabel.setText(message.getSentAt().atZone(ZoneId.systemDefault())
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm")));

        boolean isMyMessage = currentUserId.equals(message.getSenderId());

        if (isMyMessage) {
            applySentMessageStyle();
        } else {
            applyReceivedMessageStyle();
        }
    }

    /**
     * Sets the style for messages sent by the current user (right side with red/Divar color).
     */
    private void applySentMessageStyle() {
        setAlignment(Pos.CENTER_RIGHT);
        messageBox.setAlignment(Pos.BOTTOM_LEFT);

        messageBox.setStyle(
                "-fx-background-color: #a62626;" +
                        "-fx-background-radius: 12px 12px 0px 12px;"
        );

        textLabel.setStyle(
                "-fx-text-fill: white;"
        );
    }

    /**
     * Sets the style for messages received from the other person (left side with light gray color).
     */
    private void applyReceivedMessageStyle() {
        setAlignment(Pos.CENTER_LEFT);
        messageBox.setAlignment(Pos.BOTTOM_RIGHT);

        messageBox.setStyle(
                "-fx-background-color: #e8e8e8;" +
                        "-fx-background-radius: 12px 12px 12px 0px;"
        );

        textLabel.setStyle(
                "-fx-text-fill: #333333;"
        );
    }
}

