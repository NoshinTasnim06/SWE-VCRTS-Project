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

    /* CONSTRUCTOR: OwnerPanel(VCRTSFrame frame)
     * Initialize text fields and buttons, set up action listeners for submit and back, 
     * and arrange components in layout.
     */

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