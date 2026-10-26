package vccontroller;

import java.time.LocalDateTime;

public class Job implements Comparable<Job> {
	private String jobID;
	private String jobName;
	private int duration;
	private LocalDateTime deadline;
	private String status;
	private int progress;
	private LocalDateTime submissionTime;
	private LocalDateTime completionTime;
	private Client client;

	public Job(String jobID, String jobName, int duration, LocalDateTime deadline) {
		if (duration <= 0) {
			throw new IllegalArgumentException("Duration must be positive.");
		}

		this.jobID = jobID;
		this.jobName = jobName;
		this.duration = duration;
		this.deadline = deadline;
		this.status = "QUEUED";
		this.progress = 0;
		this.submissionTime = LocalDateTime.now();
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

	public int getProgress() {
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

	public void updateProgress(int percent) {
		if (percent < 0 || percent > 100) {
			throw new IllegalArgumentException("Progress must be between 0 and 100.");
		}

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
		if (deadline == null && other.deadline == null) {
			return submissionTime.compareTo(other.submissionTime);
		}
		if (deadline == null)
			return 1;
		if (other.deadline == null)
			return -1;

		return deadline.compareTo(other.deadline);
	}
}
