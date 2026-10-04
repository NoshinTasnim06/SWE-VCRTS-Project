package vcrts;

import javax.swing.*;
import java.awt.*;

/**
 * This class serves as the main application frame for the Vehicular Cloud Resource Trading System (VCRTS),
 * initializing the window dimensions and managing screen navigation between the Home, Owner, and Client views 
 * using a CardLayout manager.
 */
public class VCRTSFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    // ---------------------------------------------------------------------
    // This constructor configures the application window properties, instantiates 
    // the primary view panels while passing a reference to this frame, 
    // and displays the initial home screen.
    public VCRTSFrame() {
        setTitle("Vehicular Cloud Resource Trading System");
        setSize(BasePanel.FRAME_WIDTH, BasePanel.FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize CardLayout and the parent container panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Instantiate child view panels passing this frame reference to enable screen switching
        HomePanel homePanel = new HomePanel(this);
        OwnerPanel ownerPanel = new OwnerPanel(this);
        ClientPanel clientPanel = new ClientPanel(this);

        // Register child view panels with unique string keys inside the container
        mainPanel.add(homePanel, "Home");
        mainPanel.add(ownerPanel, "Owner");
        mainPanel.add(clientPanel, "Client");

        // Add the container panel to the window frame content pane
        add(mainPanel);

        // Transition the view to display the home screen upon launch
        showPanel("Home");

        setVisible(true);
    }

    // ---------------------------------------------------------------------
    // This method transitions the active display view by requesting the CardLayout manager 
    // to bring the panel associated with the given key to the foreground.
    public void showPanel(String name) {
        if (cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, name);
        }
    }
}