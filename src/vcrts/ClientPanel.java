package vcrts;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import javax.swing.table.DefaultTableModel;
import vccontroller.Client;
import vccontroller.Job;

/**
 * This class provides the task client interface of the VCRTS application. It lets a
 * client sign in, view the jobs they have submitted, and submit a new job. Every job is
 * saved through the LogWriter utility and the client's job list is reloaded from the
 * saved records each time the client signs in.
 */
public class ClientPanel extends BasePanel {

	private DefaultTableModel jobListModel;
	private CardLayout cardLayout;
	private JPanel mainContainer;

	private JTextField jobNameField;
	private JTextField durationField; //min
	private JTextField deadlineField; //hr
	private JLabel jobNameError;
	private JLabel durationError;
	private JLabel deadlineError;

	private JComboBox<Integer> importanceBox;
	private JLabel clientIdLabel = new JLabel("Client:");
	private Client client;
	private JTable jobTable;

	// ---------------------------------------------------------------------
	// This constructor creates the login, dashboard and job form panels and
	// registers them in a card layout so the screens can be switched.
	public ClientPanel(VCRTSFrame frame) {
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);

		// After a successful login, load the client's jobs and open the dashboard
		mainContainer.add(createLoginPanel("Client",
						() -> {
							openClient();
							clientIdLabel.setText("Client: " + userId);
							refreshJobList();
							cardLayout.show(mainContainer, "dashUI");
						},
						() -> frame.showPanel("Home")),
				"loginUI");
		mainContainer.add(clientDash(), "dashUI");
		mainContainer.add(jobForm(), "newJobUI");

		setLayout(new BorderLayout());
		add(mainContainer);
		cardLayout.show(mainContainer, "loginUI");
	}

	// structures newJobUI
	public JPanel jobForm() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createTitledBorder("Submit a Job"));
		jobNameField = new JTextField(FIELD_WIDTH);
		durationField = new JTextField(FIELD_WIDTH);
		deadlineField = new JTextField(FIELD_WIDTH);
		importanceBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
		jobNameError = createErrorLabel("Job title is required (no commas)");
		durationError = createErrorLabel("Must be a whole number greater than 0");
		deadlineError = createErrorLabel("Must be a whole number greater than 0, or leave blank");

		// The deadline is optional, if none is set the job is simply queued
		nextRow = 0;
		addFieldRow(p, "Job Title:", jobNameField, jobNameError);
		addFieldRow(p, "Estimate Job Duration in Minutes:", durationField, durationError);
		addFieldRow(p, "Job Deadline in Hours:", deadlineField, deadlineError);
		addComboRow(p, "Importance (1-5):", importanceBox);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = nextRow;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.EAST;
		p.add(submitPanel(), c);
		return p;
	}

	// adds a label and a dropdown on one row of a form
	private void addComboRow(JPanel form, String labelText, JComboBox<?> box) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(4, 8, 4, 8);
		c.gridx = 0;
		c.gridy = nextRow;
		form.add(new JLabel(labelText), c);
		c.gridx = 1;
		form.add(box, c);
		nextRow += 1;
	}

	// structures the dashboard: header, add job button and the list of the client's jobs
	public JPanel clientDash() {
		JPanel p = new JPanel(new BorderLayout());

		// Add future buttons (e.g. Payment) after newJobButton; nothing else moves
		JButton logout = new JButton("Logout");
		JButton newJobButton = new JButton("Add Job");
		JButton cancelJobButton = new JButton("Cancel Job");
		p.add(createDashTop(clientIdLabel, logout, newJobButton, cancelJobButton), BorderLayout.NORTH);

		jobListModel = createTableModel(
				new String[]{"Job ID", "Job Name", "Duration (min)", "Deadline", "Importance", "Status"}, -1);
		
		jobTable = new JTable(jobListModel);
		p.add(new JScrollPane(jobTable), BorderLayout.CENTER);

		newJobButton.addActionListener(e -> cardLayout.show(mainContainer, "newJobUI"));
		cancelJobButton.addActionListener(e -> cancelJob());
		
		logout.addActionListener(e -> {
			userId = null;
			cardLayout.show(mainContainer, "loginUI");
		});

		return p;
	}

	// Reloads the dashboard list with this client's submitted jobs
	private void refreshJobList() {
		jobListModel.setRowCount(0);
		for (Job job : client.getJobs()) {
			jobListModel.addRow(new Object[]{job.getJobID(), job.getJobName(), job.getDuration(),
					LogWriter.formatDeadline(job.getDeadline()), job.getPriority(), job.getStatus()});
		}
	}

	//structures bottom buttons of newJobUI
	public JPanel submitPanel() {
		JButton backButton = new JButton("Back");
		JButton submitButton = new JButton("Submit");

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
		p.add(Box.createHorizontalGlue());
		p.add(backButton);
		p.add(Box.createRigidArea(new Dimension(15, 0)));
		p.add(submitButton);

		backButton.addActionListener(e -> {
			clearFields();
			cardLayout.show(mainContainer, "dashUI");
		});
		submitButton.addActionListener(e -> handleSubmit());
		return p;
	}

	//checks fields
	public boolean validateFields() {
		boolean allValid = true;

		allValid &= checkField(jobNameField, jobNameError, isValidText(jobNameField));
		allValid &= checkField(durationField, durationError,
				isPositiveInteger(durationField.getText()));
		allValid &= checkField(deadlineField, deadlineError,
				isBlank(deadlineField) || isPositiveInteger(deadlineField.getText()));
		return allValid;
	}

	//empties the job form and hides its errors
	public void clearFields() {
		jobNameField.setText("");
		durationField.setText("");
		deadlineField.setText("");
		checkField(jobNameField, jobNameError, true);
		checkField(durationField, durationError, true);
		checkField(deadlineField, deadlineError, true);

		importanceBox.setSelectedIndex(0);
	}

	// validates the form, creates the job, saves it and returns to the dashboard
	public void handleSubmit() {
		if (!validateFields()) {
			return;
		}

		// Read the validated entries
		String title = jobNameField.getText().trim();
		int duration = Integer.parseInt(durationField.getText().trim());
		int importance = (Integer) importanceBox.getSelectedItem();

		// The deadline is entered in hours from now, so turn it into a date and time
		LocalDateTime deadline = null;
		if (!isBlank(deadlineField)) {
			deadline = LocalDateTime.now().plusHours(Integer.parseInt(deadlineField.getText().trim()));
		}

		// Send the job to the Client class and set its priority
		Job job = client.submitJob(title, duration, deadline);
		job.setPriority(importance);

		// Store the job in jobs.txt
		LogWriter.logJob(userId, title, String.valueOf(duration),
				LogWriter.formatDeadline(deadline), String.valueOf(importance));

		JOptionPane.showMessageDialog(this,
				"Job " + job.getJobID() + " submitted successfully!",
				"Success", JOptionPane.INFORMATION_MESSAGE);
		clearFields();
		refreshJobList();
		cardLayout.show(mainContainer, "dashUI");
	}

	// Creates the Client object for the user who just logged in and reloads
	// their saved jobs from jobs.txt, so new job IDs continue after the saved ones
	private void openClient() {
		String[] saved = LogWriter.findUser("Client", userId);   // timestamp, id, first, last
		client = new Client(userId, saved[2], saved[3]);
		for (String[] record : LogWriter.getJobsFor(userId)) {
			client.getJobs().add(LogWriter.toJob(record));
		}
	}
	
	// Updates display for a canceled job
	private void cancelJob() {
		int row = jobTable.getSelectedRow();
		if (row < 0) {
			JOptionPane.showMessageDialog(this, "Select a job in the list first.");
			return;
		}

		String jobID = jobListModel.getValueAt(row, 0).toString();

		int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this job?",
				"Confirm Cancellation", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			for (Job job : client.getJobs()) {
				if (job.getJobID().equals(jobID)) {

					job.setStatus("CANCELLED");
					refreshJobList();

					JOptionPane.showMessageDialog(this, "Job cancelled sucessfully!");
					return;
				}
			}
		}
	}
}