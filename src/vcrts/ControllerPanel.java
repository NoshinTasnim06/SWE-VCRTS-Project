package vcrts;

import vccontroller.Job;
import vccontroller.VCController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the VC Controller interface of the VCRTS application. The requests screen
 * lists the jobs and vehicles waiting for approval, and lets the controller accept or decline
 * them. The schedule screen lists every accepted job and, on request, shows the priority
 * schedule with each job's waiting and completion time and the average waiting time. The
 * schedule is recomputed over all saved jobs each time, since every job is assumed to arrive
 * at time zero.
 */
public class ControllerPanel extends BasePanel {

    private VCController controller;
    private DefaultTableModel tableModel;
    private static DefaultTableModel jobPendingTable;
    private static DefaultTableModel vehiclePendingTable;
    private JTable pendingJobs;
    private JTable pendingVehicles;
    private JLabel averageLabel = new JLabel("Average waiting time: -");
    private static final String CONTROLLER_PASSWORD = "vcadmin";

    // Position of the hidden last column that remembers which window sent the request
    private static final int JOB_CONNECTION = 5;
    private static final int VEHICLE_CONNECTION = 9;
    
    // ---------------------------------------------------------------------
    // This method builds the requests screen: a section for pending job requests and a
    // section for pending vehicle requests, each with its own Accept and Decline buttons.
    public JPanel requestDash()
    {
    	JPanel p = new JPanel(new BorderLayout(10, 10));
    	p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
    	JLabel title = new JLabel("Pending Requests", SwingConstants.CENTER);
    	title.setFont(BasePanel.TITLE_FONT);
    	
    	jobPendingTable = createTableModel(new String[] {"ClientID", "Job Name",
    			"Duration (min)", "Deadline", "Priority", "Connection"}, -1);
    	vehiclePendingTable = createTableModel(new String[] {"OwnerID", "Make", "Model",
    			"Year", "License Plate","RAM (GB)", "CPU Cores", "Storage (GB)",
    			"Residency (hrs)", "Connection"}, -1);
    	
    	// The Connection column stays in the data but is hidden from the screen
    	pendingJobs = new JTable(jobPendingTable);
    	pendingVehicles = new JTable(vehiclePendingTable);
    	pendingJobs.removeColumn(pendingJobs.getColumnModel().getColumn(JOB_CONNECTION));
    	pendingVehicles.removeColumn(pendingVehicles.getColumnModel().getColumn(VEHICLE_CONNECTION));
    	
    	JPanel tables = new JPanel(new GridLayout(2, 1, 10, 10));
    	tables.add(requestSection("Job Requests", pendingJobs, true));
    	tables.add(requestSection("Vehicle Requests", pendingVehicles, false));
    	
    	p.add(title, BorderLayout.NORTH);
    	p.add(tables, BorderLayout.CENTER);
    	return p;
    }
    
    // This method builds one section of the requests screen: a titled table with its own
    // Accept and Decline buttons underneath
    private JPanel requestSection(String heading, JTable table, boolean isJob)
    {
    	JButton accept = new JButton("Accept");
    	JButton decline = new JButton("Decline");
    	JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	buttons.add(accept);
    	buttons.add(decline);
    	
    	JPanel section = new JPanel(new BorderLayout());
    	section.setBorder(BorderFactory.createTitledBorder(heading));
    	section.add(new JScrollPane(table), BorderLayout.CENTER);
    	section.add(buttons, BorderLayout.SOUTH);
    	
    	accept.addActionListener(e -> answerRequest(true, isJob));
    	decline.addActionListener(e -> answerRequest(false, isJob));
    	return section;
    }
    
    // ---------------------------------------------------------------------
    // This method answers the selected request in the job or vehicle table. An accepted
    // request is saved through LogWriter, a declined one is dropped. Either way the row is
    // removed and the window that sent the request is told the result.
    private void answerRequest(boolean accept, boolean isJob)
    {
    	int row = isJob ? pendingJobs.getSelectedRow() : pendingVehicles.getSelectedRow();
    	if (row < 0)
    	{
    		JOptionPane.showMessageDialog(this, "Select a request in the list first.");
    		return;
    	}
    	
    	String type;
    	String id;
    	String connection;
    	if (isJob)
    	{
    		type = "Job";
    		String[] r = rowOf(jobPendingTable, row);
    		connection = r[JOB_CONNECTION];
    		if (accept)
    			id = LogWriter.logJob(r[0], r[1], r[2], r[3], r[4]);
    		else
    			id = r[1]; // a declined job never gets an ID, so use its name
    		jobPendingTable.removeRow(row);
    	}
    	else
    	{
    		type = "Vehicle";
    		String[] r = rowOf(vehiclePendingTable, row);
    		connection = r[VEHICLE_CONNECTION];
    		if (accept)
    			id = LogWriter.logVehicle(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
    		else
    			id = r[1] + " " + r[2]; // a declined vehicle never gets an ID, so use make and model
    		vehiclePendingTable.removeRow(row);
    	}
    	
    	ControllerRunnable sender = ControllerServer.getClient(connection);
    	if (sender != null)
    	{
    		sender.reply(type, accept, id);
    	}
    }
    
    // Returns one row of a table as an array of text
    private String[] rowOf(DefaultTableModel model, int row)
    {
    	String[] values = new String[model.getColumnCount()];
    	for (int i = 0; i < values.length; i++)
    	{
    		values[i] = model.getValueAt(row, i).toString();
    	}
    	return values;
    }
    
    // ---------------------------------------------------------------------
    // This method adds a request from a task client or vehicle owner window to the matching
    // pending table and shows a popup. Requests are built by ClientConnection:
    // JOB|clientId|name|duration|deadline|priority
    // or VEHICLE|ownerId|make|model|year|license|RAM|CPU|storage|residency
    public synchronized static void serverRequest(String hostName, String data)
    {
    	String[] dataPart = data.split("\\|");
    	if (dataPart[0].equals(ClientConnection.JOB_REQUEST) && dataPart.length == 6)
    	{
    		SwingUtilities.invokeLater(() -> {
    			jobPendingTable.addRow(new Object[]{dataPart[1], dataPart[2], dataPart[3],
    					dataPart[4], dataPart[5], hostName});
    			JOptionPane.showMessageDialog(null, "New job request from " + dataPart[1] + ".",
    					"New Request", JOptionPane.INFORMATION_MESSAGE);
    		});
    	}
    	else if (dataPart[0].equals(ClientConnection.VEHICLE_REQUEST) && dataPart.length == 10)
    	{
    		SwingUtilities.invokeLater(() -> {
    			vehiclePendingTable.addRow(new Object[]{dataPart[1], dataPart[2], dataPart[3],
    					dataPart[4], dataPart[5], dataPart[6], dataPart[7], dataPart[8],
    					dataPart[9], hostName});
    			JOptionPane.showMessageDialog(null, "New vehicle request from " + dataPart[1] + ".",
    					"New Request", JOptionPane.INFORMATION_MESSAGE);
    		});
    	}
    }
    
    // ---------------------------------------------------------------------
    // This constructor builds the schedule screen, wires its button, and makes
    // the job list reload every time the screen is shown.
    public ControllerPanel(VCController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("VC Controller", SwingConstants.CENTER);
        title.setFont(BasePanel.TITLE_FONT);

        JButton calculateButton = new JButton("Calculate Completion Time");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttons.add(calculateButton);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);

        tableModel = createTableModel(
                new String[]{"Order", "Job ID", "Job Name", "Priority",
                        "Duration (min)", "Waiting (min)", "Completion (min)"}, -1);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        add(averageLabel, BorderLayout.SOUTH);

        calculateButton.addActionListener(e -> showSchedule());

        // Reload the saved jobs every time this screen is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                showJobs();
            }
        });
    }

    // Reads every saved job from jobs.txt, gives them to the controller as its queue,
    // and returns them in the order they were submitted
    private List<Job> loadJobs() {
        List<Job> jobs = new ArrayList<>();
        for (String[] record : LogWriter.getAllJobs()) {
            jobs.add(LogWriter.toJob(record));
        }
        controller.setJobs(jobs);
        return jobs;
    }

    // Lists all saved jobs from every account, before any times are calculated
    private void showJobs() {
        tableModel.setRowCount(0);
        for (Job job : loadJobs()) {
            tableModel.addRow(new Object[]{"-", job.getJobID(), job.getJobName(),
                    job.getPriority(), job.getDuration(), "-", "-"});
        }
        averageLabel.setText("Average waiting time: -");
    }

    // Asks the controller for the priority schedule of all saved jobs and shows it in the table
    private void showSchedule() {
        loadJobs();
        tableModel.setRowCount(0);
        int order = 1;
        for (Job job : controller.computeSchedule()) {
            tableModel.addRow(new Object[]{order++, job.getJobID(), job.getJobName(),
                    job.getPriority(), job.getDuration(),
                    job.getWaitingMinutes(), job.getCompletionMinutes()});
        }
        int totalSeconds = (int) Math.round(controller.getAverageWaitingMinutes() * 60);
        averageLabel.setText(String.format("Average waiting time: %d min %d sec",
                totalSeconds / 60, totalSeconds % 60));
    }

    // Asks for the controller password in a small dialog and returns whether it was correct
    public boolean askPassword() {
        JPasswordField field = new JPasswordField(15);
        int choice = JOptionPane.showConfirmDialog(this, field, "Enter the VC Controller password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return choice == JOptionPane.OK_OPTION
                && new String(field.getPassword()).equals(CONTROLLER_PASSWORD);
    }
}