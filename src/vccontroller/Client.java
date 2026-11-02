package vccontroller;

import java.time.LocalDateTime;
import java.util.*;

public class Client extends Person {
	private String clientID;
	private double paymentDue;
	private List<Job> jobList;

	// ---------------------------------------------------------------------
	// Creates a new client.
	public Client(String clientID, String fname, String lname) {
		super(fname, lname);
		this.clientID = clientID;
		this.paymentDue = 0;
		this.jobList = new ArrayList<>();
	}
	
	// ---------------------------------------------------------------------
	// Returns the client's unique ID.
	public String getClientID() {
		return clientID;
	}

	// ---------------------------------------------------------------------
	// Returns the list of jobs submitted by the client.
	public List<Job> getJobs() {
		return jobList;
	}

	// ---------------------------------------------------------------------
	// Returns total payment due.
	public double getPaymentDue() {
		return paymentDue;
	}

	// ---------------------------------------------------------------------
	// Creates and adds a new job to the client's job list.
	public Job submitJob(String jobName, int duration, LocalDateTime deadline) {
		String jobId = clientID + "-" + (jobList.size() + 1);
		Job job = new Job(jobId, jobName, duration, deadline);
		jobList.add(job);
		return job;
	}

	// ---------------------------------------------------------------------
	// Finds a job by its ID and returns its current status.
	public String viewJobStatus(String jobID) {
		for (Job job : jobList) {
			if (job.getJobID().equals(jobID)) {
				return job.getStatus();
			}
		}
		return "Job not found";
	}

	// ---------------------------------------------------------------------
	// Cancels a job if it exists and has not been completed.
	public void cancelJob(String jobID) {
		for (Job job : jobList) {
			if (job.getJobID().equals(jobID)) {
				if (!job.getStatus().equals("COMPLETED")) {
					job.setStatus("CANCELLED");
				}
				return;
			}
		}
	}

	// ---------------------------------------------------------------------
	// Adds a payment amount to the clients balance. 
	public void addCharge(double amount) {
		if (amount >= 0) {
			paymentDue += amount;
		}
	}
	
}//End of class
