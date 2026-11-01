package vccontroller;

import java.time.LocalDateTime;

public class Job implements Comparable<Job> {
	private String jobID;
	private String jobName;
	private int duration;
	private LocalDateTime deadline;
	private String status;
	private double progress;
	private LocalDateTime submissionTime;
	private LocalDateTime completionTime;
	private Client client;

	private int priority = 1;
	private int waitingMinutes;
	private int completionMinutes;

	public Job(String jobID, String jobName, int duration, LocalDateTime deadline) {
		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.status = "QUEUED";
		this.progress = 0;
		this.submissionTime = LocalDateTime.now();
	}

	public Job(String jobID, String jobName, int duration, LocalDateTime deadline, int priority) {
		this(jobID, jobName, duration, deadline);
		this.priority = priority;
	}

	public String getJobID() {
		return jobID;
	}

	public String getJobName() {
		return jobName;
	}

	public int getDuration() {
		return duration;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public String getStatus() {
		return status;
	}

	public double getProgress() {
		return progress;
	}

	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}

	public LocalDateTime getCompletionTime() {
		return completionTime;
	}

	public Client getClient() {
		return client;
	}

	public int getPriority() { return priority; }
	public int getWaitingMinutes() { return waitingMinutes; }
	public int getCompletionMinutes() { return completionMinutes; }
	public void setWaitingMinutes(int minutes) { waitingMinutes = minutes; }
	public void setCompletionMinutes(int minutes) { completionMinutes = minutes; }
	public void setPriority(int priority) { this.priority = priority; }

	public void updateProgress(double percent) {
		if (percent >= 100)
			progress = 100;
		else
			progress = percent;

		if (progress == 100) {
			status = "COMPLETED";
			completionTime = LocalDateTime.now();
		} else if (!status.equals("CANCELLED")) {
			status = "IN_PROGRESS";
		}
	}

	public void setStatus(String status) {
		this.status = status;

		if ("COMPLETED".equals(status)) {
			progress = 100;
			completionTime = LocalDateTime.now();
		}
	}

	@Override
	public int compareTo(Job other) {
		if (priority != other.priority) {
			return Integer.compare(other.priority, priority);   // higher number first
		}
		if (deadline != null && other.deadline != null) {
			int byDeadline = deadline.compareTo(other.deadline);
			if (byDeadline != 0) {
				return byDeadline;
			}
		} else if (deadline == null && other.deadline != null) {
			return 1;
		} else if (deadline != null) {
			return -1;
		}
		return submissionTime.compareTo(other.submissionTime);
	}
}
