package vcrts;

import javax.swing.*;

/*
 * This class serves as the main entry point for the Vehicular Cloud Resource Trading System 
 * (VCRTS) application, responsible for starting up the program and launching the primary
 * user interface.
 */
public class VCRTSMain {

    // ---------------------------------------------------------------------
    // This method launches the application by scheduling the instantiation of 
	// the main VCRTSFrame on the Swing Event Dispatch Thread to ensure thread-safe user 
	// interface initialization.
	
    public static void main(String[] args) {
        SwingUtilities.invokeLater(VCRTSFrame::new);
    }
}