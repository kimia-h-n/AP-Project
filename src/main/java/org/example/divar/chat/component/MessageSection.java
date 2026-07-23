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

public class MessageSection extends HBox {

    @FXML
    private VBox messageBox;

    @FXML
    private Label textLabel;

    @FXML
    private Label timeLabel;

    private final Message message;
    private final Long currentUserId;

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

