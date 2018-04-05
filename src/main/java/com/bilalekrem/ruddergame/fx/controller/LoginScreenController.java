package com.bilalekrem.ruddergame.fx.controller;

import com.bilalekrem.ruddergame.fx.RudderGameApp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LoginScreenController {

    @FXML
    TextArea txtUsername;
    @FXML
    Button btnSubmit;

    private RudderGameApp app;

    @FXML
    private void initialize() {
        btnSubmit.setOnMouseClicked(e->{
            app.showGameBoard(txtUsername.getText());
        });
    }

    public void setApplication(RudderGameApp app) {
        this.app = app;
    }

}