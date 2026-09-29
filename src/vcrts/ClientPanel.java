package vcrts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientPanel extends JPanel {

    /* ATTRIBUTES:
     * Make all private: appFrame (VCRTSFrame), clientIdField, firstNameField, 
     * lastNameField, jobNameField, durationField, deadlineField (all JTextField), 
     * submitButton, backButton (all JButton)
     */

    /* CONSTRUCTOR: JobPanel(VCRTSFrame frame)
     * Initialize text fields and buttons, set up action listeners for submit 
     * and back, and arrange components in layout.
     */

    /* METHOD: validateFields()
     * Check if all fields are filled and numeric fields (duration, deadline) contain 
     * valid numbers. Return boolean.
     */

    /* METHOD: handleSubmit()
     * If validateFields() is true, extract input text, pass to LogWriter.logClient() 
     * and LogWriter.logJob(), show popup, and call clearFields().
     */

    /* METHOD: clearFields()
     * Reset all text fields to empty strings.
     */

}