package org.example.divar;

public class Application extends javafx.application.Application {
    @Override
    public void start(javafx.stage.Stage stage) {

        SwitchStage.setStage(stage);
        stage.setWidth(1100);
        stage.setHeight(800);
        stage.setTitle("Divar");

        SwitchStage.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}