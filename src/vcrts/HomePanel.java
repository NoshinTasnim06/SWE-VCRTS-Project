package vcrts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*
 * Project: SWE-VCRTS-Project
 * Class: HomePanel.java
 * Author: Afra Mashell
 * Date: September 30, 2026
 * This class creates the home screen for the VCRTS application.
 * It explains the purpose of the system and allows users to navigate
 * to either the vehicle owner screen or the client request screen.
 */

public class HomePanel extends JPanel {

    private VCRTSFrame frame;
    private JButton ownerButton;
    private JButton clientButton;

    //constructor creates and organizes the home screen.
    public HomePanel(VCRTSFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        //the title at the top of the home screen.
        JLabel titleLabel = new JLabel("Vehicular Cloud Resource Trading System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel(
                "Share vehicle computing resources or submit a computing job.");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        //Explains the two main options available to the user.
        JLabel informationLabel = new JLabel(
                "<html>"
                        + "<h3>Welcome to VCRTS</h3>"
                        + "<p>The system connects vehicle owners with users who need "
                        + "computing resources.</p>"
                        + "<br>"
                        + "<b>Vehicle Owners:</b>"
                        + "<ul>"
                        + "<li>Register your vehicle with the system.</li>"
                        + "<li>Provide unused computing resources while your vehicle is parked.</li>"
                        + "<li>Receive compensation based on vehicle usage.</li>"
                        + "</ul>"
                        + "<b>Task Clients:</b>"
                        + "<ul>"
                        + "<li>Submit a computational job to the system.</li>"
                        + "<li>Enter the required job information.</li>"
                        + "<li>Use available vehicle resources to complete the job.</li>"
                        + "</ul>"
                        + "</html>");

        informationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        informationLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel informationPanel = new JPanel(new BorderLayout());
        informationPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        informationPanel.add(informationLabel, BorderLayout.CENTER);

        add(informationPanel, BorderLayout.CENTER);

        //buttons that take the user to the correct screen.
        ownerButton = new JButton("I am a Vehicle Owner");
        clientButton = new JButton("I am a Task Client");

        ownerButton.setPreferredSize(new Dimension(190, 40));
        clientButton.setPreferredSize(new Dimension(190, 40));

        ownerButton.addActionListener(event -> {
            if (frame != null) {
                frame.showPanel("Owner");
            }
        });

        clientButton.addActionListener(event -> {
            if (frame != null) {
                frame.showPanel("Client");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(ownerButton);
        buttonPanel.add(clientButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

 // ---------------------------------------------------------------
 // This method is to to test the HomePanel by itself.
    public static void main(String[] args) {

     JFrame testFrame = new JFrame("HomePanel Test");

     testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     testFrame.setSize(800, 600);
     testFrame.setLocationRelativeTo(null);

     HomePanel homePanel = new HomePanel(null);

     testFrame.add(homePanel);
     testFrame.setVisible(true);
 }

}