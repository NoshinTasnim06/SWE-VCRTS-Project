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
		if (jobNameField.getText().trim().length() < 0) //state error
			return false;
		try
		{
			int t = 0;
			int t2 = 0;
			t = Integer.parseInt(durationField.getText());
			if (!deadlineField.getText().trim().equals(""))
				t2 = Integer.parseInt(deadlineField.getText());
			if (t <= 0 || t2 <= 0)
				return false;
		}
		catch (NumberFormatException error)
		{
			return false;
		}
		return true;
	}
	
	public void clearFields()
	{
		jobNameField.setText("");
		durationField.setText("");
		deadlineField.setText("");
	}
	
	public void handleSubmit()
	{
		String title = jobNameField.getText();
		int time = Integer.parseInt(durationField.getText());
		int deadline = Integer.parseInt(deadlineField.getText());
		//log
		//logJob(acc.getId(),title,time,deadline);
		clearFields();
		jobSubmitStatus.setText("Job Submitted successfully!");
		//ui format dash
		JPanel p = new JPanel();
		JLabel l = new JLabel(title);
		JLabel l2 = new JLabel(durationField.getText());
		JLabel l3 = new JLabel(deadlineField.getText());
		JLabel status = new JLabel("Pending");
		JButton cancel = new JButton("X");
		p.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		p.setBackground(Color.GRAY); //pad, format
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		p.add(l);
		p.add(l2);
		p.add(l3);
		p.add(status);
		p.add(Box.createHorizontalGlue());
		p.add(cancel);
		JPanel dash = (JPanel) newJob.getParent().getParent(); //return location of dashboard
		dash.add(p);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				dash.remove(cancel);
				dash.revalidate();
				dash.repaint();
			}
		});
	}
}