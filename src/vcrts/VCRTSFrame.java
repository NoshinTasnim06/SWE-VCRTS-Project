package vcrts;

import javax.swing.*;
import java.awt.*;

public class VCRTSFrame extends JFrame {

    /* ATTRIBUTES:
     * Make all private: cardLayout (CardLayout), mainContainer (JPanel), homePanel (HomePanel), 
     * ownerPanel (OwnerPanel), jobPanel (JobPanel)
     */
	
	private CardLayout cardLayout;
	private JPanel mainContainer;
	private HomePanel homePanel;
	private OwnerPanel ownerPanel;
	private ClientPanel clientPanel;

    /* CONSTRUCTOR: VCRTSFrame()
     * Set window title, default close operation (EXIT_ON_CLOSE), and size (e.g., 600x400).
     * Initialize cardLayout and mainContainer with cardLayout.
     * Instantiate homePanel, ownerPanel, and jobPanel passing 'this'.
     * Add panels to mainContainer using cardLayout keys ("HOME", "OWNER", "JOB").
     * Add mainContainer to frame and call showPanel("HOME").
     */
	
	public VCRTSFrame() {
		setTitle("VCRTS");
		setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		cardLayout = new CardLayout();
		JPanel mainContainer = new JPanel(cardLayout);
		this.mainContainer = mainContainer;
		this.clientPanel = new ClientPanel(this);
		//this.ownerPanel = new OwnerPanel(this);
		//this.homePanel = new HomePanel(this);
		
		//mainContainer.add(homePanel, "Home");
		mainContainer.add(clientPanel, "Client");
		//mainContainer.add(ownerPanel, "Owner");
		
		this.add(mainContainer);
		//showPanel("Home");
		showPanel("Client");
		
		
	}

    /* METHOD: showPanel(String panelName)
     * Make public void.
     * Use cardLayout.show(mainContainer, panelName) to switch visible panel.
     */
	
	public void showPanel(String panelName) {
		cardLayout.show(mainContainer, panelName);
	}

}