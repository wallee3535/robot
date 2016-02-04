/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class MediaMaster {

    public int frameStart, frameEnd, frameCurrent = 0;
    public int fps = 30;
    public boolean modified = false;
    ArrayList<RobAxes> bufferedFrames = new ArrayList<>();
    public MediaNode mediaControls;

    public static MediaMaster instance = new MediaMaster();

    private MediaMaster() {

    }

    public static MediaMaster getInstance() {
        return instance;
    }

    public void setFrameStart(int in) {
        frameStart = in;
        mediaControls.updateInfos();
    }

    public void setFrameEnd(int in) {
        frameEnd = in;
        mediaControls.updateInfos();
    }

    public void setFrameCurrent(int in) {
        frameCurrent = in;
        mediaControls.updateInfos();
    }
    
    public void setMediaControls(MediaNode in)
    {
        mediaControls = in;
    }
    
    public void clearBufferedFrames()
    {
        bufferedFrames.clear();
    }
    
    public void addFrameToBuffer(RobAxes ra)
    {
        bufferedFrames.add(ra);
        mediaControls.updateInfos();
    }

}
