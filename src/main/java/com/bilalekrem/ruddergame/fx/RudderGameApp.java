package com.bilalekrem.ruddergame.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import com.bilalekrem.ruddergame.ClientApp;
import com.bilalekrem.ruddergame.ServerApp;
import com.bilalekrem.ruddergame.fx.controller.LoginScreenController;
import com.bilalekrem.ruddergame.fx.controller.RudderGameController;
import com.bilalekrem.ruddergame.net.Client;

public class RudderGameApp extends Application {

    private Client client;
    private Stage primaryStage;
    private Stage indicatorStage;

    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = ServerApp.PORT; 

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("fx/view/LoginScreen.fxml"));
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

    public void showDialog() {
        
        indicatorStage = new Stage();
        primaryStage.hide();

        ProgressIndicator progressIndicator = new ProgressIndicator();
        
        Scene scene = new Scene(progressIndicator);

        indicatorStage.setScene(scene);
        indicatorStage.initOwner(primaryStage);
        indicatorStage.initStyle(StageStyle.UNDECORATED);
        indicatorStage.initModality(Modality.APPLICATION_MODAL);
        
        indicatorStage.show();
    }

    public void closeDialog() {
        indicatorStage.close();
        primaryStage.show();
    }

    public void showGameBoard(String name, boolean online) {     
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ClientApp.class.getResource("fx/view/RudderGame.fxml"));
            SplitPane p = loader.load();
            Scene scene = new Scene(p);

            // online =true;
            RudderGameController controller = loader.<RudderGameController>getController();
            controller.setApplication(this);
            controller.start(name, online);

            primaryStage.setScene(scene);
            //primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void close() {
        this.primaryStage.close();
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
