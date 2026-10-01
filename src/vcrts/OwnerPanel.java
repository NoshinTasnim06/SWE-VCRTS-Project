/* Project:  VCRTS
 * Class:    OwnerPanel.java
 * Author:   Saffah Azeem
 * Date:     September 29, 2026
 * This program provides the form vehicle owners use to register their vehicle
 * and offer its idle computing power, checking every field before saving it.
*/

package vcrts;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.time.Year;

public class OwnerPanel extends JPanel {

	private static final Color ERROR_COLOR = new Color(200, 30, 30);
	private static final Border ERROR_BORDER = BorderFactory.createLineBorder(ERROR_COLOR, 2);
	private static final int FIELD_WIDTH = 20;
	private static final int MIN_VEHICLE_YEAR = 1900;

	private VCRTSFrame frame;
	private JTextField ownerIdField;
	private JTextField ownerFNameField;
	private JTextField ownerLNameField;
	private JTextField vehicleMakeField;
	private JTextField vehicleBrandField;
	private JTextField vehicleYearField;
	private JTextField vehicleLicenseField;
	private JTextField residencyTimeField;
	private JButton submitButton;
	private JButton backButton;

	// Inline error labels, one under each field, hidden until a check fails
	private JLabel ownerIdError;
	private JLabel ownerFNameError;
	private JLabel ownerLNameError;
	private JLabel vehicleMakeError;
	private JLabel vehicleBrandError;
	private JLabel vehicleYearError;
	private JLabel vehicleLicenseError;
	private JLabel residencyTimeError;

	private Border defaultBorder;
	private int nextRow = 0;

	// ---------------------------------------------------------------------
	// This constructor creates the fields and buttons, connects the button
	// actions, and arranges everything on the panel.
	public OwnerPanel(VCRTSFrame frame) {
		this.frame = frame;

		ownerIdField = new JTextField(FIELD_WIDTH);
		ownerFNameField = new JTextField(FIELD_WIDTH);
		ownerLNameField = new JTextField(FIELD_WIDTH);
		vehicleMakeField = new JTextField(FIELD_WIDTH);
		vehicleBrandField = new JTextField(FIELD_WIDTH);
		vehicleYearField = new JTextField(FIELD_WIDTH);
		vehicleLicenseField = new JTextField(FIELD_WIDTH);
		residencyTimeField = new JTextField(FIELD_WIDTH);
		defaultBorder = ownerIdField.getBorder();

		ownerIdError = createErrorLabel("Owner ID is required");
		ownerFNameError = createErrorLabel("First name is required");
		ownerLNameError = createErrorLabel("Last name is required");
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

		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				clearFields();
				frame.showPanel("HOME"); // TODO: match VCRTSFrame's method for the home screen
			}
		});

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("Register Your Vehicle"));

		addFieldRow("Owner ID:", ownerIdField, ownerIdError);
		addFieldRow("First Name:", ownerFNameField, ownerFNameError);
		addFieldRow("Last Name:", ownerLNameField, ownerLNameError);
		addFieldRow("Vehicle Make:", vehicleMakeField, vehicleMakeError);
		addFieldRow("Vehicle Brand:", vehicleBrandField, vehicleBrandError);
		addFieldRow("Vehicle Year:", vehicleYearField, vehicleYearError);
		addFieldRow("License Plate:", vehicleLicenseField, vehicleLicenseError);
		addFieldRow("Residency Time (hours):", residencyTimeField, residencyTimeError);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		buttonPanel.add(submitButton);
		buttonPanel.add(backButton);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.gridy = nextRow;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(8, 8, 4, 8);
		add(buttonPanel, constraints);
	}

	// ---------------------------------------------------------------------
	// This method adds one row holding a label and its text field, with the
	// field's hidden error label placed on the row below.
	private void addFieldRow(String labelText, JTextField field, JLabel errorLabel) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(4, 8, 0, 8);

		constraints.gridx = 0;
		constraints.gridy = nextRow;
		add(new JLabel(labelText), constraints);

		constraints.gridx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		add(field, constraints);

		constraints.gridy = nextRow + 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0;
		constraints.insets = new Insets(0, 8, 4, 8);
		add(errorLabel, constraints);

		nextRow += 2;
	}

	// ---------------------------------------------------------------------
	// This method creates a small red error label that starts out hidden.
	private JLabel createErrorLabel(String message) {
		JLabel errorLabel = new JLabel(message);
		errorLabel.setForeground(ERROR_COLOR);
		errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 11f));
		errorLabel.setVisible(false);
		return errorLabel;
	}

	// ---------------------------------------------------------------------
	// This method checks that every field is filled in and that the year and
	// residency time hold valid numbers. It returns true if all checks pass.
	private boolean validateFields() {

		/* Every check runs, instead of stopping at the first failure, so the
		 * user sees all of the problems on the form at the same time.
		 */
		boolean allValid = true;
		allValid &= checkField(ownerIdField, ownerIdError, !isBlank(ownerIdField));
		allValid &= checkField(ownerFNameField, ownerFNameError, !isBlank(ownerFNameField));
		allValid &= checkField(ownerLNameField, ownerLNameError, !isBlank(ownerLNameField));
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
	// This method shows or hides one field's red border and error label, and
	// returns whether the field passed its check.
	private boolean checkField(JTextField field, JLabel errorLabel, boolean passed) {
		field.setBorder(passed ? defaultBorder : ERROR_BORDER);
		errorLabel.setVisible(!passed);
		revalidate();
		repaint();
		return passed;
	}

	// ---------------------------------------------------------------------
	// This method returns true if a text field is empty or only has spaces.
	private boolean isBlank(JTextField field) {
		return field.getText().trim().isEmpty();
	}

	// ---------------------------------------------------------------------
	// This method returns true if the text is a whole-number year between
	// 1900 and next year (so new models can be registered).
	private boolean isValidYear(String text) {
		try {
			int year = Integer.parseInt(text.trim());
			return year >= MIN_VEHICLE_YEAR && year <= Year.now().getValue() + 1;
		} catch (NumberFormatException exception) {
			return false;
		}
	}

	// ---------------------------------------------------------------------
	// This method returns true if the text is a real number greater than 0.
	private boolean isPositiveNumber(String text) {
		try {
			double hours = Double.parseDouble(text.trim());
			return hours > 0 && !Double.isInfinite(hours) && !Double.isNaN(hours);
		} catch (NumberFormatException exception) {
			return false;
		}
	}

	// ---------------------------------------------------------------------
	// This method saves the owner and vehicle through LogWriter when every
	// field is valid, shows a confirmation popup, and clears the form.
	private void handleSubmit() {
		if (!validateFields()) {
			return; // red errors stay visible until the user fixes them
		}

		String ownerId = ownerIdField.getText().trim();
		String firstName = ownerFNameField.getText().trim();
		String lastName = ownerLNameField.getText().trim();
		String make = vehicleMakeField.getText().trim();
		String brand = vehicleBrandField.getText().trim();
		String year = vehicleYearField.getText().trim();
		String license = vehicleLicenseField.getText().trim();
		String residencyTime = residencyTimeField.getText().trim();

		try {
			// TODO: match the final LogWriter method signatures
			LogWriter.logOwner(ownerId, firstName, lastName);
			LogWriter.logVehicle(ownerId, make, brand, year, license, residencyTime);
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
	// This method empties every text field and removes all error markings.
	private void clearFields() {
		JTextField[] fields = { ownerIdField, ownerFNameField, ownerLNameField, vehicleMakeField,
				vehicleBrandField, vehicleYearField, vehicleLicenseField, residencyTimeField };
		JLabel[] errorLabels = { ownerIdError, ownerFNameError, ownerLNameError, vehicleMakeError,
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
