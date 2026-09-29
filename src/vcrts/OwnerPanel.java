package vcrts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OwnerPanel extends JPanel {

    /* ATTRIBUTES:
     * Make all private: appFrame (VCRTSFrame), ownerIdField, firstNameField, 
     * lastNameField, makeField, brandField, yearField, licenseField, residencyTimeField 
     * (all JTextField), submitButton, backButton (all JButton)
     */
	private VCRTSFrame frame;
	private JTextField ownerIdField;
	private JTextField ownerLNameField;
	private JTextField ownerFNameField;
	private JTextField vehicleMakeField;
	private JTextField vehicleYearField;
	private JTextField vehicleBrandField;
	private JTextField vehicleLIcenseField;
	private JTextField residencyTImeField;
	private JButton submitButton;
	private JButton backButton;

    /* CONSTRUCTOR: OwnerPanel(VCRTSFrame frame)
     * Initialize text fields and buttons, set up action listeners for submit and back, 
     * and arrange components in layout.
     */
	public OwnerPanel(VCRTSFrame frame) {
		
	}
    /* METHOD: validateFields()
     * Check if all fields are filled and numeric fields (year, residencyTime) contain 
     * valid numbers. Return boolean.
     */

    /* METHOD: handleSubmit()
     * If validateFields() is true, extract input text, pass to LogWriter.logOwner() and 
     * LogWriter.logVehicle(), show popup, and call clearFields().
     */

    /* METHOD: clearFields()
     * Reset all text fields to empty strings.
     */

}