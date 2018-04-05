package com.bilalekrem.ruddergame.fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.bilalekrem.ruddergame.App;
import com.bilalekrem.ruddergame.fx.controller.LoginScreenController;
import com.bilalekrem.ruddergame.fx.controller.RudderGameController;
import com.bilalekrem.ruddergame.net.Client;

public class RudderGameApp extends Application {

    private Client client;
    public Stage primaryStage;

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 16825; 

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("fx/view/LoginScreen.fxml"));
            AnchorPane p = loader.load();

            LoginScreenController controller = loader.<LoginScreenController>getController();
            controller.setApplication(this);

            Scene scene = new Scene(p);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {

            // handle exception..
        }
    }

    public void showGameBoard(String name) {       
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("fx/view/RudderGame.fxml"));
            SplitPane p = loader.load();
            Scene scene = new Scene(p);
            primaryStage.setScene(scene);

            boolean online =true;
            RudderGameController controller = loader.<RudderGameController>getController();
            controller.setApplication(this);
            controller.start(name, online);
            primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @return the client
     */
    public Client clientInstance() {
        if(client == null) client = new Client(IP_ADDRESS, PORT);
        return client;
    }

}
