package vccontroller;

import java.util.*;
import java.time.LocalDateTime;

public class Checkpoint {
	private String checkpointID;
	private LocalDateTime timestamp;
	private String vehicleID;
	private String jobID;
	private double progressSnapshot;
	private LocalDateTime latestUpdate;

	public Checkpoint(Vehicle vehicle, Job job) {
		this.checkpointID = UUID.randomUUID().toString();
		this.timestamp = LocalDateTime.now();
		this.vehicleID = vehicle.getVehicleID();
		this.jobID = job.getJobID();
		this.progressSnapshot = job.getProgress();
		this.latestUpdate = timestamp;
	}

	public String getJobID() {
		return jobID;
	}

	public double getProgressSnapshot() {
		return progressSnapshot;
	}

	public String getCheckpointID() {
		return checkpointID;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public LocalDateTime getLatestUpdate() {
		return latestUpdate;
	}
	
	public void setLatestUpdate(LocalDateTime time) {
		latestUpdate = time;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	public void copyTo(Vehicle vehicle) {
		vehicle.restoreCheckpoint(this);
	}
}