/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 *
 * @author admin
 */

enum SockState {
    SOCK_CLOSED, SOCK_OPEN, SOCK_CONNECTED
}

public class ServerNode extends AnchorPane {

    @FXML
    Label infoBoxHeader;
    @FXML
    Circle connectionInd;
    @FXML
    Label connectionPortIP;
    @FXML
    ScrollPane subContentScroll;
    @FXML
    HBox subContentBox;


    ArrayList<ConnexionSock> socks = new ArrayList<>();
    int port = 6969;

    ShortServ serv;
    SockState state = SockState.SOCK_CLOSED;

    public ServerNode() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLServerNode.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);


        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }


        infoBoxHeader.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                infoBoxHeader.setPrefWidth(infoBoxHeader.getText().length() * 8); // why 7? Totally trial number.
            }
        });

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

    public void beginServer(int port) {
        this.port = port;
        serv = new ShortServ(this);
        serv.start();
        state = SockState.SOCK_OPEN;
    }

    public void endServer() {
        serv.terminate();
        serv = null;
        state = SockState.SOCK_CLOSED;
    }

    public void replyGreenStatus() {
        connectionPortIP.setText("Open on port: " + port);
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

    public void updateSocks() {
        subContentBox.getChildren().clear();
        /*
        if (!socks.isEmpty()) {
            setPrefHeight(210);
        } else {
            setPrefHeight(160);
        }
        */
        for (ConnexionSock s : socks) {
            subContentBox.getChildren().add(s.display);
        }
    }

    private class ShortServ extends Thread {

        ServerNode parent;
        private boolean terminate = false;
        ServerSocket listener;

        public ShortServ(ServerNode parent) {
            this.parent = parent;
            this.setDaemon(true);
        }

        public void terminate() {
            terminate = true;
            try {
                listener.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        @Override
        public void run() {
            try {
                listener = new ServerSocket(port);
                Platform.runLater(() -> {
                    replyGreenStatus();
                });
                System.out.println("The server is running on port: " + port);

                while (!terminate) {
                    Socket client = listener.accept();
                    Platform.runLater(() -> {
                        socks.add(new ConnexionSock(parent, new ConnexionNode(), client, socks.size() + 1));
                        updateSocks();
                    });
                }

            } catch (IOException e) {
                System.out.println(e);
            } finally {
                System.out.println("server terminated");

                for (ConnexionSock s : socks) {
                    s.terminate();
                }

                Platform.runLater(() -> {
                    replyBlankStatus();
                });
            }

        }

    }
}
