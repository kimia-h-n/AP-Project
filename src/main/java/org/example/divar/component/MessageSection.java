package org.example.divar.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.divar.model.Message;
import org.example.divar.util.SessionManager; // جایگزین AppContext برای احراز هویت
import java.time.format.DateTimeFormatter;

public class MessageSection extends HBox {

    @FXML private VBox messageBox;
    @FXML private Label textLabel;
    @FXML private Label timeLabel;

    public MessageSection(Message message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/divar/fxml/message.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

            textLabel.setText(message.getText());
            timeLabel.setText(message.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));

            String currentUsername = SessionManager.getCurrentUsername();

            boolean isMyMessage = message.getSenderUsername().equals(currentUsername);

            if (isMyMessage) {
                this.setAlignment(Pos.CENTER_RIGHT);
                messageBox.setStyle(messageBox.getStyle() + "-fx-background-color: #a62626; -fx-background-radius: 12px 12px 0px 12px;");
                textLabel.setStyle(textLabel.getStyle() + "-fx-text-fill: white;");
                timeLabel.setStyle("-fx-text-fill: #ffcccc;");
                messageBox.setAlignment(Pos.BOTTOM_LEFT);
            } else {
                this.setAlignment(Pos.CENTER_LEFT);
                messageBox.setStyle(messageBox.getStyle() + "-fx-background-color: #e8e8e8; -fx-background-radius: 12px 12px 12px 0px;");
                textLabel.setStyle(textLabel.getStyle() + "-fx-text-fill: #333333;");
                timeLabel.setStyle("-fx-text-fill: #888888;");
                messageBox.setAlignment(Pos.BOTTOM_RIGHT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}






