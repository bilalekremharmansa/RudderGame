package com.bilalekrem.ruddergame.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.bilalekrem.ruddergame.App;

public class RudderGameApp extends Application {

    @Override
    public void start(Stage primaryStage) {
    	try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("fx/view/RudderGame.fxml"));
            SplitPane p = loader.load();
            Scene scene = new Scene(p);
            primaryStage.setScene(scene);
            primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch 
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        launch(args);
    }

}
