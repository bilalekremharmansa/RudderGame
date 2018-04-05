package com.bilalekrem.ruddergame.fx.controller;

import com.bilalekrem.ruddergame.fx.RudderGameApp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

public class LoginScreenController {

    @FXML
    TextArea txtUsername;
    @FXML
    Button btnSubmit;
    @FXML
    RadioButton rdMatchmaking;
    @FXML
    RadioButton rdFriend;


    private RudderGameApp app;

    @FXML
    private void initialize() {
        ToggleGroup group = new ToggleGroup();
        rdMatchmaking.setToggleGroup(group);
        rdFriend.setToggleGroup(group);

        rdMatchmaking.setSelected(true);

        btnSubmit.setOnMouseClicked(e->{
            boolean online;
            if(rdMatchmaking.isSelected()) {
                online = true;
            }else {
                online = false;
            }
            app.showGameBoard(txtUsername.getText(), online);
        });
    }

    public void setApplication(RudderGameApp app) {
        this.app = app;
    }

}