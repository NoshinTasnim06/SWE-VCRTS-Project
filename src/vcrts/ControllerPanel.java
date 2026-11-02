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
 * This class provides the VC Controller interface of the VCRTS application. It lists every
 * job saved by every client and, on request, shows the priority schedule with each job's
 * waiting and completion time and the average waiting time. The schedule is recomputed
 * over all saved jobs each time, since every job is assumed to arrive at time zero.
 */
public class ControllerPanel extends BasePanel {

    private VCController controller;
    private DefaultTableModel tableModel;
    private JLabel averageLabel = new JLabel("Average waiting time: -");
    private static final String CONTROLLER_PASSWORD = "vcadmin";

    // ---------------------------------------------------------------------
    // This constructor builds the controller screen, wires its buttons, and makes
    // the job list reload every time the screen is shown.
    public ControllerPanel(VCRTSFrame frame, VCController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("VC Controller", SwingConstants.CENTER);
        title.setFont(BasePanel.TITLE_FONT);

        JButton calculateButton = new JButton("Calculate Completion Time");
        JButton backButton = new JButton("Back");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttons.add(calculateButton);
        buttons.add(backButton);

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
        backButton.addActionListener(e -> frame.showPanel("Home"));

        // Hide the screen and ask for the password every time it is shown; go back Home if it is wrong
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                setContentVisible(false);
                if (askPassword()) {
                    showJobs();
                    setContentVisible(true);
                } else {
                    frame.showPanel("Home");
                }
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
        averageLabel.setText(String.format("Average waiting time: %.2f minutes",
                controller.getAverageWaitingMinutes()));
    }

    // Asks for the controller password in a small dialog and returns whether it was correct
    // Shows or hides everything on this screen (title, buttons, table and average label)
    private void setContentVisible(boolean visible) {
        for (Component component : getComponents()) {
            component.setVisible(visible);
        }
    }

    // Asks for the controller password in a small dialog and returns whether it was correct
    private boolean askPassword() {
        JPasswordField field = new JPasswordField(15);
        int choice = JOptionPane.showConfirmDialog(this, field, "Enter the VC Controller password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return choice == JOptionPane.OK_OPTION
                && new String(field.getPassword()).equals(CONTROLLER_PASSWORD);
    }
}