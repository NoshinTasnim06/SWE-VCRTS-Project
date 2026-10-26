package vccontroller;

import java.time.LocalDateTime;
import java.util.*;

public class Client extends Person {
	private String clientID;
	private double paymentDue;
	private List<Job> jobList;

	public Client(String clientID, String fname, String lname) {
		super(fname, lname);
		this.clientID = clientID;
		this.paymentDue = 0;
		this.jobList = new ArrayList<>();
	}

	public String getClientID() {
		return clientID;
	}

	public List<Job> getJobs() {
		return jobList;
	}

	public double getPaymentDue() {
		return paymentDue;
	}

	//Adjust jobId to be clientId+jobId extension
	public Job submitJob(String jobName, int duration, LocalDateTime deadline) {
		String jobId = UUID.randomUUID().toString(); 											
		Job job = new Job(jobId, jobName, duration, deadline);

		jobList.add(job);
		return job;
	}

	public String viewJobStatus(String jobID) {
		for (Job job : jobList) {
			if (job.getJobID().equals(jobID)) {
				return job.getStatus();
			}
		}
		return "Job not found";
	}

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

	public void addCharge(double amount) {
		if (amount >= 0) {
			paymentDue += amount;
		}
	}
}//End of class
