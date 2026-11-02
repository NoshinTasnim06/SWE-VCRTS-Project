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
import java.time.Year;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;

/**
 * This class provides the vehicle owner interface of the VCRTS application. It lets
 * an owner sign in, view their registered vehicles, register a new vehicle, change a
 * vehicle's availability, and update a vehicle's residency time. All entries are
 * saved through the LogWriter utility.
 */
public class OwnerPanel extends BasePanel {

    private static final int MIN_VEHICLE_YEAR = 1900;

    // Position of each form field inside the fields and errors arrays (same order as the form)
    private static final int MAKE = 0, MODEL = 1, YEAR = 2, LICENSE = 3,
            RESIDENCY = 4, RAM = 5, CPU = 6, STORAGE = 7;

    private static final String[] LABELS = {"Vehicle Make:", "Vehicle Model:", "Vehicle Year:",
            "License Plate:", "Residency Time (hours):", "RAM (GB):", "CPU Cores:", "Storage (GB):"};

    private static final String[] ERRORS = {"Vehicle make is required (no commas)",
            "Vehicle model is required (no commas)", "Must enter a valid 4-digit year",
            "License plate is required (no commas)", "Must be a whole number greater than 0",
            "Must be a whole number greater than 0", "Must be a whole number greater than 0",
            "Must be a whole number greater than 0"};

    private final JTextField[] fields = new JTextField[LABELS.length];
    private final JLabel[] errors = new JLabel[LABELS.length];

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JLabel ownerIdLabel = new JLabel("Owner:");
    private DefaultTableModel vehicleModel;
    private JTable vehicleTable;

    // ---------------------------------------------------------------------
    // This constructor initializes the vehicle owner panel by constructing
    // the input form with error labels, configuring navigation buttons,
    // establishing the card layout container for switching between login,
    // dashboard and registration views, and showing the initial login card.
    public OwnerPanel(VCRTSFrame frame) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register Your Vehicle"));

        // Create each input field with its error label and add them to the form as rows
        nextRow = 0;
        for (int i = 0; i < LABELS.length; i++) {
            fields[i] = new JTextField(FIELD_WIDTH);
            errors[i] = createErrorLabel(ERRORS[i]);
            addFieldRow(form, LABELS[i], fields[i], errors[i]);
        }

        // Reset input fields and transition back to the dashboard when the back button is clicked
        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back");
        submitButton.addActionListener(event -> handleSubmit());
        backButton.addActionListener(event -> {
            clearFields();
            cardLayout.show(mainContainer, "dash");
        });

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
        // panel, the dashboard and the registration form
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.add(createLoginPanel("Owner",
                        () -> {
                            ownerIdLabel.setText("Owner: " + userId);
                            refreshVehicles();
                            cardLayout.show(mainContainer, "dash");
                        },
                        () -> frame.showPanel("Home")),
                "login");
        mainContainer.add(form, "form");
        mainContainer.add(ownerDash(), "dash");

        setLayout(new BorderLayout());
        add(mainContainer);
        cardLayout.show(mainContainer, "login");
    }

    // -------------------------------------------------------------------------------
    // This method returns the trimmed text of one form field.
    private String text(int field) {
        return fields[field].getText().trim();
    }

    // -------------------------------------------------------------------------------
    // This method evaluates all form fields against validation criteria, updating
    // visual error states on each field and returning whether every field passed inspection.
    private boolean validateFields() {
        boolean[] passed = new boolean[fields.length];
        passed[MAKE] = isValidText(fields[MAKE]);
        passed[MODEL] = isValidText(fields[MODEL]);
        passed[YEAR] = isValidYear(text(YEAR));
        passed[LICENSE] = isValidText(fields[LICENSE]);
        passed[RESIDENCY] = isPositiveInteger(text(RESIDENCY));
        passed[RAM] = isPositiveInteger(text(RAM));
        passed[CPU] = isPositiveInteger(text(CPU));
        passed[STORAGE] = isPositiveInteger(text(STORAGE));

        // Show or clear the error state of every field, without stopping at the first failure
        boolean allValid = true;
        for (int i = 0; i < fields.length; i++) {
            allValid &= checkField(fields[i], errors[i], passed[i]);
        }
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
    // This method processes form submission by validating inputs, saving the
    // vehicle data via the LogWriter utility, displaying a confirmation alert,
    // and resetting form fields.
    private void handleSubmit() {
        if (!validateFields()) {
            return;
        }

        // Log vehicle details to persistent storage under the current user ID
        String vehicleId = LogWriter.logVehicle(userId, text(MAKE), text(MODEL), text(YEAR),
                text(LICENSE), text(RAM), text(CPU), text(STORAGE), text(RESIDENCY));

        JOptionPane.showMessageDialog(this,
                "Vehicle " + vehicleId + " registered successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        refreshVehicles();
        cardLayout.show(mainContainer, "dash");
    }

    // ---------------------------------------------------------------------
    // This method clears all text entries across input fields, restores default
    // component borders, and hides active error messages.
    private void clearFields() {
        for (int i = 0; i < fields.length; i++) {
            fields[i].setText("");
            checkField(fields[i], errors[i], true);
        }
    }

    // The owner's home screen: header, buttons, and the list of their vehicles
    private JPanel ownerDash() {
        JPanel p = new JPanel(new BorderLayout());

        JButton logout = new JButton("Logout");
        JButton addVehicleButton = new JButton("Register Vehicle");
        JButton residencyButton = new JButton("Update Residency");
        p.add(createDashTop(ownerIdLabel, logout, addVehicleButton, residencyButton), BorderLayout.NORTH);

        // Only the Status column (column 9) can be edited
        vehicleModel = createTableModel(
                new String[]{"Vehicle ID", "Make", "Model", "Year", "License Plate",
                        "RAM (GB)", "CPU Cores", "Storage (GB)", "Residency (hrs)", "Status"}, 9);
        vehicleTable = new JTable(vehicleModel);

        // Status column is a dropdown
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Unavailable"});
        vehicleTable.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(statusBox));

        // When the owner picks a new status, save it to vehicles.txt
        vehicleModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 9 && e.getFirstRow() >= 0) {
                String vehicleId = vehicleModel.getValueAt(e.getFirstRow(), 0).toString();
                String status = vehicleModel.getValueAt(e.getFirstRow(), 9).toString();
                LogWriter.updateVehicle(vehicleId, null, status);
            }
        });
        p.add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // Wire the dashboard buttons to their screens and actions
        addVehicleButton.addActionListener(e -> cardLayout.show(mainContainer, "form"));
        residencyButton.addActionListener(e -> updateResidency());
        logout.addActionListener(e -> {
            userId = null;
            cardLayout.show(mainContainer, "login");
        });

        return p;
    }

    // Reloads the list with this owner's saved vehicles
    private void refreshVehicles() {
        vehicleModel.setRowCount(0);
        for (String[] v : LogWriter.getVehiclesFor(userId)) {
            vehicleModel.addRow(new Object[]{v[1], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11]});
        }
    }

    // Changes only the residency time of the selected vehicle
    private void updateResidency() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a vehicle in the list first.");
            return;
        }

        // Ask for the new residency time and make sure it is a valid number
        String vehicleId = vehicleModel.getValueAt(row, 0).toString();
        String input = JOptionPane.showInputDialog(this, "New residency time (hours):");
        if (input == null) {
            return; // the owner cancelled
        }
        if (!isPositiveInteger(input)) {
            JOptionPane.showMessageDialog(this, "Enter a whole number greater than 0.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save the new residency time and reload the list
        LogWriter.updateVehicle(vehicleId, input.trim(), null);
        refreshVehicles();
    }
}