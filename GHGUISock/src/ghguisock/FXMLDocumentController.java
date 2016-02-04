/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author admin
 */
public class FXMLDocumentController implements Initializable {
    
    
    @FXML private Button socketOpenButton;
    @FXML private TextField ipvFourBox;
    @FXML private TextField portBox;
    @FXML private VBox mainVertical;
    @FXML private Button socketCloseButton;
    @FXML private AnchorPane mainPanel;
    
    ServerNode animPanel;
    ServerNode robotPanel;
    MediaNode timeline;
    HBox routeNodesBox;
    
    @FXML
    private void handleOpenSockets(ActionEvent event) {
        if (IPPort.validPort(portBox.getText()) && animPanel.state == SockState.SOCK_CLOSED && robotPanel.state == SockState.SOCK_CLOSED){
//            System.out.println("You clicked me!");
//            System.out.println(ipvFourBox.getText());
//            System.out.println(portBox.getText());

            //#FFCA66
            String s = socketOpenButton.getStyle().replaceAll("-fx-background-color:[^;]*", "-fx-background-color: LIGHTGREEN");
            socketOpenButton.setStyle(s);
            socketOpenButton.setText("Open");
            socketOpenButton.setDisable(true);
            
            socketCloseButton.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(200), socketCloseButton);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            
            portBox.setDisable(true);
            animPanel.beginServer(Integer.parseInt(portBox.getText()));
            robotPanel.beginServer(Integer.parseInt(portBox.getText())+1);
        }
    }
    
    @FXML
    private void handleCloseSockets(ActionEvent event){
        String s = socketOpenButton.getStyle().replaceAll("-fx-background-color:[^;]*", "-fx-background-color: #CACACA");
            socketOpenButton.setStyle(s);
            socketOpenButton.setText("Open Sockets");
            socketOpenButton.setDisable(false);
            
            FadeTransition ft = new FadeTransition(Duration.millis(200), socketCloseButton);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.play();
            ft.setOnFinished((ActionEvent e)->{
                socketCloseButton.setVisible(false);
            });
            
            portBox.setDisable(false);
            animPanel.endServer();
            robotPanel.endServer();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        animPanel = new ServerNode();
        mainVertical.getChildren().add(animPanel);
        animPanel.setHeaderText("Anim Socket");
        
        routeNodesBox = new HBox();
        routeNodesBox.setPrefHeight(32);
        mainVertical.getChildren().add(routeNodesBox);
        
        robotPanel = new ServerNode();
        mainVertical.getChildren().add(robotPanel);
        robotPanel.setHeaderText("Robot Socket");
        
        timeline = new MediaNode();
        mainPanel.getChildren().add(timeline);
        AnchorPane.setBottomAnchor(timeline, 0.0);
        AnchorPane.setLeftAnchor(timeline, 0.0);
        AnchorPane.setRightAnchor(timeline, 0.0);
        
        MediaMaster.getInstance().setMediaControls(timeline);
        
        portBox.setText("6969");
        
        ipvFourBox.textProperty().addListener((observable, oldValue, newValue) -> {
            /*
            Color c = Color.WHITE;
            String s = "0.0.0.0";
            if(IPPort.validIP(newValue))
            {
                c = Color.LIGHTGREEN;
                s = newValue;
            }
            else
            {
                c = Color.WHITE;
            }
            animPanel.serverSock.ipPort.setIP(s);
            robotPanel.serverSock.ipPort.setIP(s);
            ipvFourBox.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
            */
        });
        
        portBox.textProperty().addListener((observable, oldValue, newValue) -> {
            Color c = Color.WHITE;
            String s = "0";
            if(IPPort.validPort(newValue))
            {
                c = Color.LIGHTGREEN;
                s = newValue;
            }
            else
            {
                c = Color.WHITE;
            }
            //animPanel.sock.port = Integer.parseInt(s);
            //robotPanel.sock.port = Integer.parseInt(s);
            portBox.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        
    }    
    
}
