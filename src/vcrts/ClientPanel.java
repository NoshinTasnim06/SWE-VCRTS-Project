package vcrts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ClientPanel extends JPanel {

    /* ATTRIBUTES:
     * Make all private: appFrame (VCRTSFrame), clientIdField, firstNameField, 
     * lastNameField, jobNameField, durationField, deadlineField (all JTextField), 
     * submitButton, backButton (all JButton)
     */
	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private CardLayout cardLayout;
	private JPanel mainContainer;
	private VCRTSFrame frame;
	//private ClientAccount acc;
	private JLabel user = new JLabel("Username:");
	private JLabel password = new JLabel("Password:");
	private JTextField userField = new JTextField(30);
	private JTextField passwordField = new JTextField(30);
	private JButton switchHome;
	private JButton signIn;
	private JTextField clientId;
	private JTextField clientLNameField;
	private JTextField clientFNameField;
	private JLabel currJobs = new JLabel("Current Jobs");
	//private JLabel history;
	private JLabel logout = new JLabel("Logout");
	private JButton newJob = new JButton("+ New Job");
	private JLabel jobName = new JLabel("Job Title:");
	private JLabel jobTime = new JLabel("Estimate Job Duration in Minutes:");
	private JLabel jobDeadline = new JLabel("Job Deadline in Hours:"); // optional,if none is set, queue
	private JButton submitButton = new JButton("Submit");
	private JButton backButton = new JButton("Back");
	
	private JTextField jobNameField;
	private JTextField durationField; //min
	private JTextField deadlineField; //hr
	private JLabel jobSubmitStatus;
	JPanel login;
	JPanel register;
	JPanel clientUI;
	JPanel newJobUI;
	
	public ClientPanel(VCRTSFrame frame) { //call creates
		this.frame = frame;
		frame.setTitle("Client");
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);
		frame.add(mainContainer); //add to login?
		newJobUI = jobForm();
		login = new JPanel(new BorderLayout());
		register = register();
		JLabel title = new JLabel("VCRTS Client Sign In");
		JPanel body = new JPanel();
		JPanel form = new JPanel();
		JPanel formOptions = new JPanel();
		JLabel loginError = new JLabel("");
		form.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		formFormat(user,userField,form, c, 0 ,0);
		formFormat(password,passwordField,form,c,0,2);

		JPanel signUpPanel = new JPanel();
		JLabel signUp = new JLabel("New?");
		JButton switchRegister = new JButton("<html><a href=''>Sign Up</a></html>");
		switchRegister.setBorderPainted(false);
		switchRegister.setContentAreaFilled(false);
		switchRegister.setFocusPainted(false);
		switchRegister.setMargin(new Insets(5,0,5,0));
		signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.X_AXIS));
		signUpPanel.add(signUp);
		signUpPanel.add(switchRegister);
		
		JPanel wrap = new JPanel();
		wrap.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		wrap.add(signUpPanel);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		switchHome = new JButton("Back");
		signIn = new JButton("Sign In");
		buttonPanel.add(switchHome);
		buttonPanel.add(signIn);
		
		formOptions.setLayout(new BoxLayout(formOptions, BoxLayout.X_AXIS));
		formOptions.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		formOptions.setAlignmentX(CENTER_ALIGNMENT);
		formOptions.add(wrap);
		formOptions.add(Box.createRigidArea(new Dimension(40,0)));
		formOptions.add(buttonPanel);
		
		//may need to edit
		JPanel error = new JPanel();
		JPanel e2 = new JPanel();
		error.setLayout(new BoxLayout(error, BoxLayout.X_AXIS));
		error.setAlignmentX(CENTER_ALIGNMENT);
		e2.setLayout(new FlowLayout(FlowLayout.CENTER));
		error.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		error.add(e2);
		e2.add(Box.createHorizontalGlue());
		e2.add(loginError);
		loginError.setPreferredSize(new Dimension(250,25));
		
		title.setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		login.add(title,BorderLayout.NORTH);
		body.add(form);
		body.add(error);
		body.add(formOptions);
		login.add(body, BorderLayout.CENTER);
		
		//register button action missing
		
		switchHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.showPanel("Home");
			}
		});
		switchRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainContainer,"registerUI");
			}
		});
		
		signIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (userField.getText().length() > 0 &&
							passwordField.getText().length() > 0)
					{
						String u = userField.getText().trim();
						String p = passwordField.getText().trim();
						//ClientAccount a = getAccount(u,p);
						//clientUI = clientDash(a);
						//cardLayout.show(mainContainer, "clientUI");
						loginError.setText("");
					}
					throw new NoSuchElementException("");
				}
				catch (NoSuchElementException error) {
					if (userField.getText().length() > 0 &&
							passwordField.getText().length() > 0)
						loginError.setText("Account couldn't be found");
					else
						loginError.setText("Invalid fields exist");
				}
			}
		});
		
		mainContainer.add(login, "loginUI");
		mainContainer.add(register, "registerUI");
		//mainContainer.add(clientUI, "clientUI");
		mainContainer.add(newJobUI, "newJobUI");
		cardLayout.show(mainContainer, "registerUI");
		//setSize(FRAME_WIDTH,FRAME_HEIGHT); 
	}
	
	public void formFormat (JLabel l, JTextField t, JPanel a, GridBagConstraints c, int row, int col) {
		c.insets = new Insets(10,10,10,10);
		c.gridx = row;
		c.gridy = col;
		a.add(l,c);
		c.gridx = row+1;
		c.gridy = col;
		c.fill = c.HORIZONTAL;
		a.add(t,c);
	}
	
	public JPanel register() {
		JPanel p = new JPanel();
		JLabel title = new JLabel("VCRTS Client Sign Up");
		JPanel body = new JPanel();
		JLabel user = new JLabel("Username:");
		JLabel pass = new JLabel("Password:");
		JLabel fname = new JLabel("First Name:");
		JLabel lname = new JLabel("Last Name:");
		JLabel status = new JLabel("");
		JTextField userField = new JTextField(30);
		JTextField passField = new JTextField(30);
		JTextField fnameField = new JTextField(30);
		JTextField lnameField = new JTextField(30);
		JButton back = new JButton("Back");
		JButton submit = new JButton("Submit");
		
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
		
		JPanel wrap = new JPanel();
		wrap.setLayout(new BoxLayout(wrap, BoxLayout.X_AXIS));
		wrap.setAlignmentX(RIGHT_ALIGNMENT);
		wrap.setBorder(BorderFactory.createEmptyBorder(20,20,50,20));
		wrap.add(back);
		wrap.add(Box.createRigidArea(new Dimension(20,0)));
		wrap.add(submit);
		
		status.setHorizontalAlignment(SwingConstants.CENTER);
		status.setBorder(BorderFactory.createEmptyBorder(30,20,20,20));
		
		JPanel wrap2 = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		formFormat(fname,fnameField,wrap2,c,0,0);
		formFormat(lname,lnameField,wrap2,c,0,2);
		formFormat(user,userField,wrap2,c,0,4);
		formFormat(pass,passField,wrap2,c,0,6);
		
		p.setLayout(new BorderLayout());
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.setAlignmentX(CENTER_ALIGNMENT);
		body.add(wrap2);
		body.add(status);
		body.add(wrap);
		p.add(title, BorderLayout.NORTH);
		p.add(body, BorderLayout.CENTER);
		
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(mainContainer,"loginUI");
				if (!submit.isEnabled())
					submit.setEnabled(true);
			}
		});
		
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				try {
					if (userField.getText().length() != 0 && passwordField.getText().length() != 0 
					&& fnameField.getText().length() != 0 && lnameField.getText().length() != 0)
					{
						String username = userField.getText();
						String password = password.getText();
						String first = userField.getText().trim();
						String last = userField.getText().trim();
						if (username.includes("") || password.includes(""))
							throw
						new ClientAccount(username, password, first, last);
						submit.setEnabled(false);
						status.setText("Account successfully created! Log in through the sign in.");
					}
					throw
				}
				catch () {
					
				}
				*/
				//log
			}
		});
		
		return p;
	}
	
	public JPanel clientDash(/*ClientAccount acc*/)
	{
		//this.acc = acc;
		JPanel p = new JPanel(new BorderLayout());
		JPanel header = new JPanel();
		header.setBackground(Color.GRAY);
		header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
		header.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		header.add(Box.createHorizontalGlue());
		header.add(currJobs);
		header.add(Box.createRigidArea(new Dimension(30,0)));
		header.add(logout);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,10,20,20));
		buttonPanel.add(newJob);
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.add(buttonPanel);
		p.add(header, BorderLayout.NORTH);
		p.add(body, BorderLayout.CENTER);
		//p.add(id, BorderLayout.PAGE_END);
		newJob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(mainContainer, "newJobUI");
			}
		});
		return p;
	}
	
	public JPanel jobForm() { //jobform arrange
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		//clientFNameField = new JTextField(30);
		jobNameField = new JTextField(30);
		durationField = new JTextField(4);
		deadlineField = new JTextField(3);
		jobSubmitStatus = new JLabel();
		p.add(jobName);
		p.add(jobNameField);
		p.add(jobTime);
		p.add(durationField);
		p.add(jobDeadline); 
		p.add(deadlineField);
		p.add(jobSubmitStatus);
		p.add(submitPanel());
		return p;
	}
	
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
				jobSubmitStatus.setText("");
				cardLayout.show(mainContainer,"clientUI");
			}
		});

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//verify
				if (validateFields())
				{
					handleSubmit();
				}
				else
					jobSubmitStatus.setText("Error");
			}
		});
		return p;
	}
	
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
     * and LogWriter.logJob(), show pop up, and call clearFields().
     */

    /* METHOD: clearFields()
     * Reset all text fields to empty strings.
     */

}