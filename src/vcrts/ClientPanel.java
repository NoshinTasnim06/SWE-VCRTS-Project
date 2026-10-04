package vcrts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ClientPanel extends BasePanel {

	private CardLayout cardLayout;
	private JPanel mainContainer;
	private VCRTSFrame frame;

	private JLabel jobName = new JLabel("Job Title:");
	private JLabel jobTime = new JLabel("Estimate Job Duration in Minutes:");
	private JLabel jobDeadline = new JLabel("Job Deadline in Hours:"); // optional,if none is set, queue
	private JButton submitButton = new JButton("Submit");
	private JButton backButton = new JButton("Back");
	
	private JTextField jobNameField;
	private JTextField durationField; //min
	private JTextField deadlineField; //hr
	private JLabel jobNameError;
    private JLabel durationError;
    private JLabel deadlineError;
    
	private JPanel login;
	private JPanel newJobUI;
	
	//creates panels to switch
	public ClientPanel(VCRTSFrame frame) {
		this.frame = frame;
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);
		newJobUI = jobForm();
		login = createLoginPanel("Client",
                () -> {cardLayout.show(mainContainer, "newJobUI");},
            () -> frame.showPanel("Home"));
		
		mainContainer.add(login, "loginUI");
        mainContainer.add(newJobUI, "newJobUI");

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
        jobNameError = createErrorLabel("Job title is required");
        durationError = createErrorLabel("Must enter a valid duration greater than 0");
        deadlineError = createErrorLabel("Must be a number greater than 0, or leave blank");

        nextRow = 0;
        addFieldRow(p, jobName.getText(), jobNameField, jobNameError);
        addFieldRow(p, jobTime.getText(), durationField, durationError);
        addFieldRow(p, jobDeadline.getText(), deadlineField, deadlineError);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = nextRow;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        p.add(submitPanel(), c);
        return p;
	}
	
	//structures bottom buttons of newJobUI
	public JPanel submitPanel()
	{
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		p.add(Box.createHorizontalGlue());
		p.add(backButton);
		p.add(Box.createRigidArea(new Dimension(15,0)));
		p.add(submitButton);
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				clearFields();
				userId = null;
				cardLayout.show(mainContainer,"loginUI");
			}
		});

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (validateFields())
				{
					handleSubmit();
				}
			}
		});
		return p;
	}
	
	//checks fields
	public boolean validateFields()
	{
		boolean allValid = true;

        allValid &= checkField(jobNameField, jobNameError, !isBlank(jobNameField));
        allValid &= checkField(durationField, durationError,
                isPositiveNumber(durationField.getText()));
        allValid &= checkField(deadlineField, deadlineError,
                isBlank(deadlineField) || isPositiveNumber(deadlineField.getText()));
        return allValid;
	}
	
	public void clearFields()
	{
		jobNameField.setText("");
		durationField.setText("");
		deadlineField.setText("");
		checkField(jobNameField, jobNameError, true);
        checkField(durationField, durationError, true);
        checkField(deadlineField, deadlineError, true);
	}
	
	public void handleSubmit()
	{
		String title = jobNameField.getText().trim();
        String time = durationField.getText().trim();
        String deadline = deadlineField.getText().trim();
        if (deadline.isEmpty())
            deadline = "None";

        LogWriter.logJob(userId, title, time, deadline);
        JOptionPane.showMessageDialog(this,
                "Job submitted successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
	}
}