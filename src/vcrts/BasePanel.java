package vcrts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.BooleanSupplier;

/**
 * This class serves as the foundational user interface panel for the VCRTS application.
 * It defines shared layout constants and error styling, provides helper utilities for
 * validating user inputs, automates form layout construction using GridBagLayout,
 * builds the shared dashboard header and table pieces, and manages the complete
 * authentication interface for signing in or registering users.
 */
public class BasePanel extends JPanel {
    public static final Color ERROR_COLOR = new Color(200, 30, 30);
    public static final Border ERROR_BORDER = BorderFactory.createLineBorder(ERROR_COLOR, 2);
    public static final int FIELD_WIDTH = 20;
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 600;
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

    public Border defaultBorder = new JTextField().getBorder();
    public int nextRow = 0;
    public String userId;

    // ---------------------------------------------------------------------
    // This method checks whether a given text field contains no text or consists
    // entirely of white space.
    public boolean isBlank(JTextField field) {
        return field.getText().trim().isEmpty();
    }

    // ---------------------------------------------------------------------
    // This method validates that a string can be parsed into a whole number
    // that is strictly greater than zero.
    public boolean isPositiveInteger(String text) {
        try {
            return Integer.parseInt(text.trim()) > 0;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // This method checks that a text field is not blank and contains no commas,
    // since commas are used to separate the values saved in the data files.
    public boolean isValidText(JTextField field) {
        return !isBlank(field) && !field.getText().contains(",");
    }

    // ---------------------------------------------------------------------
    // This method constructs and configures a red error label with a
    // standardized small font, keeping it hidden until a validation error occurs.
    public JLabel createErrorLabel(String message) {
        JLabel errorLabel = new JLabel(message);
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 11f));
        errorLabel.setVisible(false);
        return errorLabel;
    }

    // ---------------------------------------------------------------------
    // This method updates the visual feedback of an input field by applying
    // an error border and displaying its error message when validation fails,
    // refreshing the display immediately.
    public boolean checkField(JTextField field, JLabel errorLabel, boolean passed) {
        field.setBorder(passed ? defaultBorder : ERROR_BORDER);
        errorLabel.setVisible(!passed);
        revalidate();
        repaint();
        return passed;
    }

    // ---------------------------------------------------------------------
    // This method adds a full entry row containing a text label and an input
    // field to a GridBagLayout form, while placing the field's hidden error
    // message directly beneath it and incrementing the row index.
    public void addFieldRow(JPanel form, String labelText, JTextField field, JLabel errorLabel) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(4, 8, 0, 8);

        // Place the text label in the first column at the current row
        constraints.gridx = 0;
        constraints.gridy = nextRow;
        form.add(new JLabel(labelText), constraints);

        // Place the text input field in the second column and allow it to
        // stretch horizontally across the panel
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        form.add(field, constraints);

        // Place the error label on the row directly beneath the input
        // field with adjusted padding
        constraints.gridy = nextRow + 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.insets = new Insets(0, 8, 4, 8);
        form.add(errorLabel, constraints);

        // Advance the row counter by two to prepare the layout for the next input field
        nextRow += 2;
    }

    // ---------------------------------------------------------------------
    // This method builds the top section of a dashboard: a gray header with the
    // signed-in user on the left and the logout button on the right, and a toolbar
    // underneath holding the given action buttons.
    public JPanel createDashTop(JLabel idLabel, JButton logout, JButton... toolbarButtons) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.GRAY);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        header.add(idLabel, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        for (JButton button : toolbarButtons) {
            toolbar.add(button);
        }

        JPanel top = new JPanel(new BorderLayout());
        top.add(header, BorderLayout.NORTH);
        top.add(toolbar, BorderLayout.CENTER);
        return top;
    }

    // ---------------------------------------------------------------------
    // This method creates an empty table model in which no cell can be edited,
    // except for the one column given (use -1 for a fully read-only table).
    public DefaultTableModel createTableModel(String[] columns, int editableColumn) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == editableColumn;
            }
        };
    }

    // ---------------------------------------------------------------------
    // This method creates and returns the complete login and registration
    // panel, wiring together form fields, submit actions, and backend
    // verification for both clients and vehicle owners.
    public JPanel createLoginPanel(String userType, Runnable onSuccess, Runnable onBack) {
        JTextField userField = new JTextField(FIELD_WIDTH);
        JTextField firstField = new JTextField(FIELD_WIDTH);
        JTextField lastField = new JTextField(FIELD_WIDTH);
        String userRequired = "Username is required (no commas)";
        JLabel userError = createErrorLabel(userRequired);
        JLabel firstError = createErrorLabel("First name is required (no commas)");
        JLabel lastError = createErrorLabel("Last name is required (no commas)");

        JPanel form = new JPanel(new GridBagLayout());
        nextRow = 0;
        addFieldRow(form, "Username:", userField, userError);
        addFieldRow(form, "First Name:", firstField, firstError);
        addFieldRow(form, "Last Name:", lastField, lastError);

        JButton backButton = new JButton("Back");
        JButton signUpButton = new JButton("Sign Up");
        JButton signInButton = new JButton("Sign In");
        JPanel buttons = new JPanel();
        buttons.add(backButton);
        buttons.add(signUpButton);
        buttons.add(signInButton);

        // Reset input fields to empty strings and clear any visible error borders
        Runnable clear = () -> {
            userField.setText("");
            firstField.setText("");
            lastField.setText("");
            checkField(userField, userError, true);
            checkField(firstField, firstError, true);
            checkField(lastField, lastError, true);
        };

        // Check all three fields together, marking each invalid one with its error
        BooleanSupplier validate = () -> {
            userError.setText(userRequired);
            boolean allValid = true;
            allValid &= checkField(userField, userError, isValidText(userField));
            allValid &= checkField(firstField, firstError, isValidText(firstField));
            allValid &= checkField(lastField, lastError, isValidText(lastField));
            return allValid;
        };

        backButton.addActionListener(event -> {
            clear.run();
            onBack.run();
        });

        // Validate form fields, check user credentials against stored log
        // records, and sign the user into the system
        signInButton.addActionListener(event -> {
            if (!validate.getAsBoolean()) {
                return;
            }

            String username = userField.getText().trim();
            String[] user = LogWriter.findUser(userType, username);
            boolean matches = user != null
                    && user[2].equalsIgnoreCase(firstField.getText().trim())
                    && user[3].equalsIgnoreCase(lastField.getText().trim());
            if (!matches) {
                userError.setText("No account matches these details");
                checkField(userField, userError, false);
                return;
            }

            userId = username;
            clear.run();
            onSuccess.run();
        });

        // Validate form inputs, ensure the username is not already registered,
        // save new user data to the system, and execute success callback
        signUpButton.addActionListener(event -> {
            if (!validate.getAsBoolean()) {
                return;
            }

            String username = userField.getText().trim();
            if (LogWriter.findUser(userType, username) != null) {
                userError.setText("Username already taken");
                checkField(userField, userError, false);
                return;
            }

            LogWriter.logUser(userType, username, firstField.getText().trim(), lastField.getText().trim());
            userId = username;
            clear.run();
            onSuccess.run();
        });

        // Assemble title header, input form, and action buttons into a main bordered panel
        JLabel title = new JLabel("VCRTS " + userType + " Sign In / Sign Up", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        JPanel login = new JPanel(new BorderLayout(10, 10));
        login.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        login.add(title, BorderLayout.NORTH);
        login.add(form, BorderLayout.CENTER);
        login.add(buttons, BorderLayout.SOUTH);
        return login;
    }
}