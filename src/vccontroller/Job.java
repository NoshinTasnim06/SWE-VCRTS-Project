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

	// ---------------------------------------------------------------------
	// Creates new job with default priority and QUEUED status.
	public Job(String jobID, String jobName, int duration, LocalDateTime deadline) {
		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.status = "QUEUED";
		this.progress = 0;
		this.submissionTime = LocalDateTime.now();
	}

	// ---------------------------------------------------------------------
	// Creates a new job with a specified priority.
	public Job(String jobID, String jobName, int duration, LocalDateTime deadline, int priority) {
		this(jobID, jobName, duration, deadline);
		this.priority = priority;
	}

	// ---------------------------------------------------------------------
	// Returns the job's unique Id.
	public String getJobID() {
		return jobID;
	}

	// ---------------------------------------------------------------------
	// Returns the job's name.
	public String getJobName() {
		return jobName;
	}

	// ---------------------------------------------------------------------
	// Returns the estimated duration of the job.
	public int getDuration() {
		return duration;
	}

	// ---------------------------------------------------------------------
	// Returns the jobs deadline.
	public LocalDateTime getDeadline() {
		return deadline;
	}

	// ---------------------------------------------------------------------
	// Returns the current job status.
	public String getStatus() {
		return status;
	}

	// ---------------------------------------------------------------------
	// Returns the current completion percentage.
	public double getProgress() {
		return progress;
	}

	// ---------------------------------------------------------------------
	// Returns the time when the job was submitted.
	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}

	// ---------------------------------------------------------------------
	// Returns the time when the job was completed. 
	public LocalDateTime getCompletionTime() {
		return completionTime;
	}

	// ---------------------------------------------------------------------
	// Returns the Client who submitted the job.
	public Client getClient() {
		return client;
	}

	// ---------------------------------------------------------------------
	// Returns the job's priority level.
	public int getPriority() {
		return priority;
	}

	// ---------------------------------------------------------------------
	// Returns the time the job has spent waiting.
	public int getWaitingMinutes() {
		return waitingMinutes;
	}

	// ---------------------------------------------------------------------
	// Returns the time taken to complete the job.
	public int getCompletionMinutes() {
		return completionMinutes;
	}

	// ---------------------------------------------------------------------
	// Updates the job's waiting time in minutes.
	public void setWaitingMinutes(int minutes) {
		waitingMinutes = minutes;
	}

	// ---------------------------------------------------------------------
	// Updates the job's completion time in minutes.
	public void setCompletionMinutes(int minutes) {
		completionMinutes = minutes;
	}

	// ---------------------------------------------------------------------
	// Changes the job's priority level 
	public void setPriority(int priority) {
		this.priority = priority;
	}

	// ---------------------------------------------------------------------
	// Updates the job's progress and change its status.
	// Completion time is updated for completed jobs with progress 100.
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
	
	// ---------------------------------------------------------------------
	// Manually updates the job's status and adjust progress and completion time if marked as completed.
	public void setStatus(String status) {
		this.status = status;

		if ("COMPLETED".equals(status)) {
			progress = 100;
			completionTime = LocalDateTime.now();
		}
	}

	// ---------------------------------------------------------------------
	// Compare jobs by priority, deadline, and submission time. 
	@Override
	public int compareTo(Job other) {
		if (priority != other.priority) {
			return Integer.compare(priority, other.priority);   // lower number first
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
