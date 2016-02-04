/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author admin
 */

enum DownloadState
{
    IDLE,
    REQUEST,
    DOWNLOADING
}

public class MediaNode extends AnchorPane {

    @FXML
    AnchorPane timelinePane;
    @FXML
    Button playButton;
    @FXML
    Button downloadButton;
    @FXML
    Rectangle scrubber;
    @FXML
    Rectangle bufferInd;

    double scrubberTime = 0.0;
    DownloadState dlState = DownloadState.IDLE;

    ArrayList<ConnexionSock> activeClients = new ArrayList<>();

    public MediaNode() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLMediaControls.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        bufferInd.setWidth(0.0);

        timelinePane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("Mouse x: "+event.getX()+"\nMouse y: "+event.getY());
                scrubber.setX(Math.min(Math.max(0.0, event.getX()), timelinePane.getBoundsInParent().getWidth() - 3.0));
                scrubberTime = scrubber.getX() / (timelinePane.getBoundsInParent().getWidth() - 3.0);
                MediaMaster mm = MediaMaster.getInstance();
                mm.modified = true;
                mm.frameCurrent = (int) ((double) (mm.frameEnd - mm.frameStart) * scrubberTime) + mm.frameStart;
                //System.out.println(scrubberTime);
            }
        }
        );

        timelinePane.widthProperty().addListener((observable, oldValue, newValue) -> {
            setScrubberToTime();
            //System.out.println(newValue);
        });

    }

    @FXML
    public void resetTimeToZero(ActionEvent e) {
        MediaMaster mm = MediaMaster.getInstance();
        scrubberTime = 0.0;
        mm.modified = true;
        mm.frameCurrent = (int) ((double) (mm.frameEnd - mm.frameStart) * scrubberTime) + mm.frameStart; //needs to replaced in the fookin' MasterMaster
        setScrubberToTime();
        System.out.println("reset");
    }

    @FXML
    public void downloadAnimation(ActionEvent e) {
        if(dlState != DownloadState.DOWNLOADING && dlState == DownloadState.IDLE){
            dlState = DownloadState.REQUEST;
        }
    }

    public void setScrubberToTime() {
        scrubber.setX(scrubberTime * (timelinePane.getBoundsInParent().getWidth() - 3.0));
    }

    public void setScrubberToTime(double t) {
        scrubberTime = t;
        setScrubberToTime();
    }
    
    public void setBufferProgress(double t) {
        bufferInd.setWidth(t * timelinePane.getBoundsInParent().getWidth() - 3.0);
    }

    public void updateInfos() {
        MediaMaster mm = MediaMaster.getInstance();
        double diff = Math.max(1, mm.frameEnd - mm.frameStart);
        
        if (!mm.modified) {
            
            //System.out.println(diff);
            double pos = (mm.frameCurrent - mm.frameStart) / diff;
            setScrubberToTime(pos);
        }
        
        long len = mm.bufferedFrames.size();
        setBufferProgress(len/diff);
        
    }
}
