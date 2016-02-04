/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

import java.net.*;
import java.io.*;
import javafx.application.Platform;

/**
 *
 * @author admin
 */
enum ConnexionClient {
    CLIENT_BLENDER_GENERAL, //0
    CLIENT_BLENDER_ROBOT, //1
    CLIENT_MAYA_GENERAL, //2
    CLIENT_MAYA_ROBOT, //3
    CLIENT_RAPID, //4 
    CLIENT_FRAMEBUFFER, //5
    CLIENT_NONE //n/a
}

public class ConnexionSock extends Thread {

    Socket socket;
    int clientNumber;
    ServerNode parent;
    ConnexionNode display;
    ConnexionClient clientType = ConnexionClient.CLIENT_NONE;

    private boolean terminate = false;
    int hz = 15;

    public ConnexionSock(ServerNode parent, ConnexionNode display, Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.parent = parent;
        this.display = display;
        this.display.sock = this;
        log("New connection with client# " + clientNumber + " at " + socket);
        start();
    }

    /**
     * Services this thread's client by first sending the client a welcome
     * message then repeatedly reading strings and sending back the capitalized
     * version of the string.
     */
    @Override
    public void run() {
        try {

            // Decorate the streams so we can send characters
            // and not just bytes.  Ensure output is flushed
            // after every newline.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send a welcome message to the client.
            String input = in.readLine();
            String headerText = "";
            switch (input.charAt(0)) {
                case '0':
                    headerText = "Blender General";
                    clientType = ConnexionClient.CLIENT_BLENDER_GENERAL;
                    hz = 15;
                    break;
                case '1':
                    headerText = "Blender Robot";
                    clientType = ConnexionClient.CLIENT_MAYA_GENERAL;
                    hz = 30;
                    break;
                case '2':
                    headerText = "Maya General";
                    clientType = ConnexionClient.CLIENT_RAPID;
                    hz = 15;
                    break;
                case '3':
                    headerText = "Maya Robot";
                    clientType = ConnexionClient.CLIENT_RAPID;
                    hz = 30;
                    break;
                case '4':
                    headerText = "RAPID";
                    clientType = ConnexionClient.CLIENT_RAPID;
                    hz = 30;
                    break;
                case '5':
                    headerText = "Frame Buffer";
                    clientType = ConnexionClient.CLIENT_FRAMEBUFFER;
                    hz = -1;
                    break;
                default:
                    headerText = "Err";
                    clientType = ConnexionClient.CLIENT_NONE;
                    break;
            }

            out.println(headerText + " Client # " + clientNumber + ".");
            final String header = headerText;

            Platform.runLater(() -> {
                display.replyGreenStatus();
                display.setHeaderText(header);
                display.setPortIPText(socket.getLocalSocketAddress().toString(), "" + socket.getPort());
            });
            // Get messages from the client, line by line; return them
            // capitalized
            while (!terminate && !Thread.currentThread().isInterrupted()) {
                input = in.readLine();
                //log(input);

                if (input != null && input.equals(".")) {
                    terminate = true;
                }

                switch (clientType) {
                    case CLIENT_BLENDER_GENERAL:
                        handleBlenderGeneralInput(input, out);
                        break;
                    case CLIENT_BLENDER_ROBOT:
                        handleBlenderRobotInput(input, out);
                        break;
                    case CLIENT_MAYA_GENERAL:
                        handleMayaGeneralInput(input, out);
                        break;
                    case CLIENT_MAYA_ROBOT:
                        handleMayaRobotInput(input, out);
                        break;
                    case CLIENT_RAPID:
                        handleRapidRobotInput(input, out);
                        break;
                    case CLIENT_FRAMEBUFFER:
                        handleFrameBuffer(input, out);
                        break;
                    case CLIENT_NONE:
                        System.out.println("ClIENT_NONE encountered - communication protocol was likely violated");
                        break;
                }
                if (hz > 0) {
                    try {

                        long millis = Math.round(1000 / hz);
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
                //out.println(":");
            }

            in.readLine(); //this clears the buffer before sending the termination message, otherwise client still tries to send data
            out.println(";"); //';' is the stop character used to termiante a socket

        } catch (IOException e) {
            log("Error handling client# " + clientNumber + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            log("Connection with client# " + clientNumber + " closed");
            Platform.runLater(() -> {
                parent.socks.remove(this);
                parent.updateSocks();
            });
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void handleBlenderGeneralInput(String input, PrintWriter out) {
        String[] parse = input.split(",");
        boolean strDataValid = true;
        String clientOut = "S" + "a";
        for (String s : parse) {
            if (!s.matches("[0-9]+")) {
                strDataValid = false;
            }
        }
        if (strDataValid) {
            final String output = "Current Frame: " + parse[2]
                    + "\nStart Frame: " + parse[0]
                    + "\nEnd Frame: " + parse[1]
                    + "\nFPS: "+ parse[3];
            MediaMaster mm = MediaMaster.getInstance();
            mm.setFrameStart(Integer.parseInt(parse[0]));
            mm.setFrameEnd(Integer.parseInt(parse[1]));
            mm.fps = Integer.parseInt(parse[3]);
            Platform.runLater(() -> {
                display.nodeText.setText(output);
            });
            if (mm.modified) {
                clientOut += "CF" + mm.frameCurrent;
                mm.modified = false;
            } else {
                mm.setFrameCurrent(Integer.parseInt(parse[2]));
            }
            if (mm.mediaControls.dlState == DownloadState.REQUEST)
            {
                clientOut += "-FB"; //-FB tells client to start a framebuffer client and run the framebuffering sequence
                mm.mediaControls.dlState = DownloadState.DOWNLOADING;
            }
        }

        out.println(clientOut);
    }

    private void handleBlenderRobotInput(String input, PrintWriter out) {
        String[] parse = input.split("[\\[\\]]");
        if (parse[1].matches("[0-9-\\.,]*")) {
            String[] angles = parse[1].split(",");

            final String output = "Robot Index: " + parse[0]
                    + "\n Axis 1: " + angles[0]
                    + "\n Axis 2: " + angles[1]
                    + "\n Axis 3: " + angles[2]
                    + "\n Axis 4: " + angles[3]
                    + "\n Axis 5: " + angles[4]
                    + "\n Axis 6: " + angles[5];
            Platform.runLater(() -> {
                display.nodeText.setText(output);
            });
        }

        out.println(":");
    }

    private void handleMayaGeneralInput(String input, PrintWriter out) {
        handleBlenderGeneralInput(input, out);
    }

    private void handleMayaRobotInput(String input, PrintWriter out) {
        handleBlenderRobotInput(input, out);
    }

    private void handleRapidRobotInput(String input, PrintWriter out) {
        Platform.runLater(() -> {
            display.nodeText.setText("Robot Connected: " + input);
        });

        out.println("::");
    }

    private void handleFrameBuffer(String input, PrintWriter out) {
        String[] parse = input.split("[\\[\\]]");
        if (parse[1].matches("[0-9-\\.,]*")) {
            String[] angles = parse[1].split(",");
            
            RobAxes newFrame = new RobAxes(Double.parseDouble(angles[0]), 
                    Double.parseDouble(angles[1]), Double.parseDouble(angles[2]), 
                    Double.parseDouble(angles[3]),Double.parseDouble(angles[4]), 
                    Double.parseDouble(angles[5]));
            
            MediaMaster.getInstance().addFrameToBuffer(newFrame);
            
            final String output = "Robot Index: " + parse[0]
                    + "\n Axis 1: " + angles[0]
                    + "\n Axis 2: " + angles[1]
                    + "\n Axis 3: " + angles[2]
                    + "\n Axis 4: " + angles[3]
                    + "\n Axis 5: " + angles[4]
                    + "\n Axis 6: " + angles[5];
            Platform.runLater(() -> {
                display.nodeText.setText(output);
            });
        }
    }

    public void terminate() {
        terminate = true;
    }
}
