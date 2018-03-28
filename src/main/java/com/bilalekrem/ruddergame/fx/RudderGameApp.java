package com.bilalekrem.ruddergame.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.bilalekrem.ruddergame.fx.controller.RudderGameController;

public class RudderGameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        RudderGameController controller = new RudderGameController();
        controller.registerPlayer("Bilal");
        controller.registerPlayer("Ekrem");

        Scene scene = controller.loadScene();
        if (scene != null) {
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
