package vccontroller;

import java.util.*;
import java.time.LocalDateTime;

public class Checkpoint {
	private String checkpointID;
	private LocalDateTime timestamp;
	private byte[] stateImage;
	private String vehicleID;
	private String jobID;
	private int progressSnapshot;

	public Checkpoint(Vehicle vehicle, Job job) {
		this.checkpointID = UUID.randomUUID().toString();
		this.timestamp = LocalDateTime.now();
		this.vehicleID = vehicle.getVehicleID();
		this.jobID = job.getJobID();
		this.progressSnapshot = job.getProgress();

		//String state = ; ?
		
		// how to store as byte?
		// this.stateImage = ;
	}

	public byte[] getStateImage() {
		return stateImage;
	}

	public String getJobID() {
		return jobID;
	}

	public int getProgressSnapshot() {
		return progressSnapshot;
	}

	public String getCheckpointID() {
		return checkpointID;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public String getVehicleID() {
		return vehicleID;
	}

	public void copyTo(Vehicle vehicle) {
		vehicle.restoreCheckpoint(this);
	}
}