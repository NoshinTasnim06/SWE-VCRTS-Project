/* Project:  VCRTS

 * Class:    OwnerPanel.java
 * Author:   Saffah Azeem
 * Date:     September 29, 2026
 * This program provides the form vehicle owners use to register their vehicle
 * and offer its idle computing power, checking every field before saving it.
 */

package vcrts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;


public class OwnerPanel extends BasePanel {

    private static final int MIN_VEHICLE_YEAR = 1900;

    private VCRTSFrame frame;
    private JTextField vehicleMakeField;
    private JTextField vehicleBrandField;
    private JTextField vehicleYearField;
    private JTextField vehicleLicenseField;
    private JTextField residencyTimeField;
    private JButton submitButton;
    private JButton backButton;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JLabel vehicleMakeError;
    private JLabel vehicleBrandError;
    private JLabel vehicleYearError;
    private JLabel vehicleLicenseError;
    private JLabel residencyTimeError;

    // ---------------------------------------------------------------------
    // This constructor initializes the vehicle owner panel by constructing
    // the input form with error labels, configuring navigation buttons,
    // establishing the card layout container for switching between login
    // and registration views, and showing the initial login card.
    public OwnerPanel(VCRTSFrame frame) {
        this.frame = frame;

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register Your Vehicle"));

        vehicleMakeField = new JTextField(FIELD_WIDTH);
        vehicleBrandField = new JTextField(FIELD_WIDTH);
        vehicleYearField = new JTextField(FIELD_WIDTH);
        vehicleLicenseField = new JTextField(FIELD_WIDTH);
        residencyTimeField = new JTextField(FIELD_WIDTH);

        vehicleMakeError = createErrorLabel("Vehicle make is required");
        vehicleBrandError = createErrorLabel("Vehicle brand is required");
        vehicleYearError = createErrorLabel("Must enter a valid 4-digit year");
        vehicleLicenseError = createErrorLabel("License plate is required");
        residencyTimeError = createErrorLabel("Must enter a valid duration greater than 0");

        submitButton = new JButton("Submit");
        backButton = new JButton("Back");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleSubmit();
            }
        });

        // Reset input fields, clear session state, and transition back
        // to the login view when the back button is clicked
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                clearFields();
                userId = null;
                cardLayout.show(mainContainer, "login");
            }
        });

        nextRow = 0;

        addFieldRow(form,"Vehicle Make:", vehicleMakeField, vehicleMakeError);
        addFieldRow(form,"Vehicle Brand:", vehicleBrandField, vehicleBrandError);
        addFieldRow(form, "Vehicle Year:", vehicleYearField, vehicleYearError);
        addFieldRow(form, "License Plate:", vehicleLicenseField, vehicleLicenseError);
        addFieldRow(form, "Residency Time (hours):", residencyTimeField, residencyTimeError);

        // Group action buttons together in a horizontal flow layout and align them on the
        // grid underneath the input fields
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = nextRow;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(8, 8, 4, 8);
        form.add(buttonPanel, constraints);

        // Assemble the card layout container to handle transitions between the owner login
        // panel and registration form
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createLoginPanel("Owner",
                        () -> cardLayout.show(mainContainer, "form"),
                        () -> frame.showPanel("Home")),
                "login");
        mainContainer.add(form, "form");

        setLayout(new BorderLayout());
        add(mainContainer);
        cardLayout.show(mainContainer, "login");
    }

    // ------------------------------------------------------------------------------------
    // This method evaluates all form fields against validation criteria, updating
    // visual error states on each field and returning whether every field passed inspection.
    private boolean validateFields() {
        boolean allValid = true;

        allValid &= checkField(vehicleMakeField, vehicleMakeError, !isBlank(vehicleMakeField));
        allValid &= checkField(vehicleBrandField, vehicleBrandError, !isBlank(vehicleBrandField));
        allValid &= checkField(vehicleYearField, vehicleYearError,
                isValidYear(vehicleYearField.getText()));
        allValid &= checkField(vehicleLicenseField, vehicleLicenseError, !isBlank(vehicleLicenseField));
        allValid &= checkField(residencyTimeField, residencyTimeError,
                isPositiveNumber(residencyTimeField.getText()));
        return allValid;
    }

    // ---------------------------------------------------------------------
    // This method verifies that an entered year string parses into an integer
    // within an acceptable historical and near-future year range.
    private boolean isValidYear(String text) {
        try {
            int year = Integer.parseInt(text.trim());
            return year >= MIN_VEHICLE_YEAR && year <= Year.now().getValue() + 1;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // This method processes form submission by validating inputs, attempting
    // to save vehicle data via the LogWriter utility, displaying confirmation or error alerts, and resetting form fields upon success.
    private void handleSubmit() {
        if (!validateFields()) {
            return;
        }

        String make = vehicleMakeField.getText().trim();
        String brand = vehicleBrandField.getText().trim();
        String year = vehicleYearField.getText().trim();
        String license = vehicleLicenseField.getText().trim();
        String residencyTime = residencyTimeField.getText().trim();

        try {
            // Log vehicle details to persistent storage under the current user ID
            LogWriter.logVehicle(userId, make, brand, year, license, residencyTime);
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this,
                    "Could not save registration: " + exception.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Vehicle registered successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
    }

    // ---------------------------------------------------------------------
    // This method clears all text entries across input fields, restores default
    // component borders, hides active error messages, and refreshes the panel interface.
    private void clearFields() {
        JTextField[] fields = {vehicleMakeField,
                vehicleBrandField, vehicleYearField, vehicleLicenseField, residencyTimeField };
        JLabel[] errorLabels = {vehicleMakeError,
                vehicleBrandError, vehicleYearError, vehicleLicenseError, residencyTimeError };

        for (JTextField field : fields) {
            field.setText("");
            field.setBorder(defaultBorder);
        }

        for (JLabel errorLabel : errorLabels) {
            errorLabel.setVisible(false);
        }

        revalidate();
        repaint();
    }

}