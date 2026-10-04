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

    // Stores the main application frame so this panel can request screen changes.
    private VCRTSFrame frame;

    // Navigation buttons for the two main user roles.
    private JButton ownerButton;
    private JButton clientButton;

    // ---------------------------------------------------------------------
    // This constructor builds and organizes the HomePanel interface and
    // stores a reference to the main frame for navigation between screens.
    public HomePanel(VCRTSFrame frame) {

        this.frame = frame;

        // Divide the home screen into a header, information area,
        // and navigation section.
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Create the title and subtitle displayed at the top of the screen.
        JLabel titleLabel = new JLabel("Vehicular Cloud Resource Trading System");
        titleLabel.setFont(BasePanel.TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel(
                "Share vehicle computing resources or submit a computing job.");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Stack the title and subtitle vertically.
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Display instructions explaining the two main user roles.
        // Basic HTML formatting is used for headings and bullet points.
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

        // Place the instructions inside their own panel for spacing and layout.
        JPanel informationPanel = new JPanel(new BorderLayout());
        informationPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        informationPanel.add(informationLabel, BorderLayout.CENTER);

        add(informationPanel, BorderLayout.CENTER);

        // Create navigation buttons for vehicle owners and task clients.
        ownerButton = new JButton("I am a Vehicle Owner");
        clientButton = new JButton("I am a Task Client");

        ownerButton.setPreferredSize(new Dimension(190, 40));
        clientButton.setPreferredSize(new Dimension(190, 40));

        // Ask VCRTSFrame to display the Owner screen when clicked.
        ownerButton.addActionListener(event -> frame.showPanel("Owner"));

        // Ask VCRTSFrame to display the Client screen when clicked.
        clientButton.addActionListener(event -> frame.showPanel("Client"));

        // Placed both navigation buttons next to each other and center them.
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 20, 10));

        buttonPanel.add(ownerButton);
        buttonPanel.add(clientButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}