package vcrts;

import javax.swing.*;
import vccontroller.Server;
import vccontroller.VCController;

/*
 * This class serves as the main entry point for the VC Controller program. It opens the
 * controller window (a Requests tab and a Schedule tab) and starts the server that
 * waits for task client and vehicle owner windows to connect.
 */
public class ControllerMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VCController controller = new VCController(0.10, new Server());
            ControllerPanel controllerPanel = new ControllerPanel(controller);

            // Only open the controller if the password is correct
            if (!controllerPanel.askPassword()) {
                System.exit(0);
            }

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Requests", controllerPanel.requestDash());
            tabs.addTab("Schedule", controllerPanel);

            JFrame frame = new JFrame("VC Controller");
            frame.setSize(BasePanel.FRAME_WIDTH, BasePanel.FRAME_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.add(tabs);
            frame.setVisible(true);

            // Start listening only after the request tables exist
            new Thread(new ControllerServer()).start();
        });
    }
}