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
	private static final int FRAME_WIDTH = 800;
	private static final int FRAME_HEIGHT = 600;
	private CardLayout cardLayout;
	private JPanel mainContainer;
	private VCRTSFrame frame;
	private JTextField clientIdField;
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
	
	//private int clientIdField;
	private JTextField jobNameField;
	private JTextField durationField; //min
	private JTextField deadlineField; //hr
	private JTextField jobStatus;
	JPanel clientUI;
	JPanel newJobUI;
	
	public ClientPanel(VCRTSFrame frame) { //call creates
		this.frame = frame;
		frame.setTitle("Client");
		cardLayout = new CardLayout();
		mainContainer = new JPanel(cardLayout);
		frame.add(mainContainer);
		clientUI = new JPanel(new BorderLayout());
		newJobUI = jobForm();
		newJob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(mainContainer, "newJobUI");
			}
		});
		JPanel header = new JPanel();
		header.setBackground(Color.GRAY);
		header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
		header.setBorder(BorderFactory.createEmptyBorder(10,20,20,20));
		header.add(Box.createHorizontalGlue());
		header.add(currJobs);
		header.add(Box.createRigidArea(new Dimension(30,0)));
		header.add(logout);
		currJobs.setAlignmentX(RIGHT_ALIGNMENT);
		logout.setAlignmentX(RIGHT_ALIGNMENT);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15,10,20,20));
		buttonPanel.add(newJob);
		newJob.setAlignmentX(RIGHT_ALIGNMENT);
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.add(buttonPanel);
		clientUI.add(header, BorderLayout.NORTH);
		clientUI.add(body, BorderLayout.CENTER);
		//clientUI.add(id, BorderLayout.PAGE_END);
		mainContainer.add(clientUI, "clientUI");
		mainContainer.add(newJobUI, "newJobUI");
		cardLayout.show(mainContainer, "clientUI");
		//setSize(FRAME_WIDTH,FRAME_HEIGHT); 
	}
	
	public JTextField createTextField(JTextField f, int size) {
		
		f = new JTextField(size); //parse later
		return f;
	}
	
	
	public JPanel jobForm() { //jobform arrange
		JPanel p = new JPanel();
		//clientFNameField = new JTextField(30);
		jobNameField = new JTextField(30);
		durationField = new JTextField(4);
		deadlineField = new JTextField(3);
		p.add(jobName);
		p.add(jobNameField);
		p.add(jobTime);
		p.add(jobDeadline); 
		p.add(submitPanel());
		return p;
	}
	public JPanel submitPanel()
	{
		JPanel p = new JPanel();
		p.add(backButton);
		p.add(submitButton);
		
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				cardLayout.show(mainContainer,"clientUI");
			}
		});

		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//verify
				if (true)
				{
					jobStatus.setText("Job Submitted");
					submitButton.hide();
				}
				else
					jobStatus.setText("Error");
			}
		});
		return p;
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