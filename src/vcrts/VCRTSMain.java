package vcrts;

import javax.swing.*;

public class VCRTSMain {

    /* METHOD: main(String[] args)
     * Make public static void.
     * Launch application on the Event Dispatch Thread using SwingUtilities.invokeLater().
     * Inside run(), instantiate VCRTSFrame and set setVisible(true).
     */
	
	public static void main(String[] args) {
		VCRTSFrame frame = new VCRTSFrame();
		frame.setVisible(true);
		ClientPanel p = new ClientPanel(frame); //client testing
		
	}

}