/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 *
 * @author admin
 */
public class ConnexionNode extends AnchorPane {

    @FXML
    Label infoBoxHeader;
    @FXML
    Circle connectionInd;
    @FXML
    Label connectionPortIP;

    @FXML
    ScrollPane nodeScroll;

    @FXML
    Label nodeText;

    ConnexionSock sock;
    

    boolean minimized = false;
    double heightBeforeMinim;

    public ConnexionNode() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLConnectNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        nodeText.setText("");

        infoBoxHeader.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                infoBoxHeader.setPrefWidth(infoBoxHeader.getText().length() * 8); // why 7? Totally trial number.
            }
        });

    }

    @FXML
    public void closeConnexion(ActionEvent event) {
        //System.out.println("closeButton");
        sock.interrupt();
        sock.terminate();
        
        //force quit the thread after 2 seconds of no response - means the socket was stuck waiting for input so we have to force-close the socket;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            try {
                sock.socket.close();
            } catch (IOException s) {
                System.out.println(s);
            } finally {
                System.out.println("terminated connection with client");
                sock.terminate();
            }

        }

    }

    @FXML
    public void minimizePanel(ActionEvent event) {
        //System.out.println("minimize");
        heightBeforeMinim = getBoundsInParent().getHeight();
        minimized = !minimized;

        if (minimized) {
            setPrefHeight(32);
        } else {
            setPrefHeight(heightBeforeMinim);
        }

        nodeScroll.setVisible(!minimized);
        connectionPortIP.setVisible(!minimized);
    }

    public void setHeaderText(String in) {
        infoBoxHeader.setText(in);

    }

    public void setPortIPText(String ip, String port) {
        connectionPortIP.setText(ip + " : " + port);
    }

    public void setPortIPText(String txt) {
        connectionPortIP.setText(txt);
    }

    public void replyGreenStatus() {
        connectionInd.setFill(Color.LIGHTGREEN);
        FadeTransition ft = new FadeTransition(Duration.millis(200), connectionInd);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public void replyBlankStatus() {
        connectionPortIP.setText("");
        connectionInd.setFill(Color.web("#CACACA"));
    }

}
