package vccontroller;

import java.time.LocalDateTime;

public class Vehicle {
	private String vehicleID;
	private Owner owner;
	private String make;
	private String model;
	private int year;
	private String licensePlate;
	private int ramGB;
	private int cpuCores;
	private int storageGB;
	private int residencyTime;
	private LocalDateTime arrivalTime;
	private LocalDateTime departureTime;
	private String status;
	private Job currentJob;

	// ---------------------------------------------------------------------
	// Creates a new vehicle.
	public Vehicle(String vehicleID, String make, String model, int year, String licensePlate, int ramGB, int cpuCores,
			int storageGB, int residencyTime) {

		this.vehicleID = vehicleID;
		this.make = make;
		this.model = model;
		this.year = year;
		this.licensePlate = licensePlate;
		this.ramGB = ramGB;
		this.cpuCores = cpuCores;
		this.storageGB = storageGB;
		this.residencyTime = residencyTime;
		this.arrivalTime = LocalDateTime.now();
		this.status = "AVAILABLE";
	}

	// ---------------------------------------------------------------------
	// Returns the vehicle's unique ID.
	public String getVehicleID() {
		return vehicleID;
	}

	// ---------------------------------------------------------------------
	// Returns the owner of the vehicle.
	public Owner getOwner() {
		return owner;
	}
	
	// ---------------------------------------------------------------------
	// Returns the time when the vehicle departed.
	public LocalDateTime getDepartureTime() {
		return departureTime;
	}

	// ---------------------------------------------------------------------
	// Checks whether the vehicle is currently available for a job.
	public boolean isAvailable() {
		return status.equals("AVAILABLE");
	}

	// ---------------------------------------------------------------------
	// Returns the vehicle's current status.
	public String getStatus() {
		return status;
	}

	// ---------------------------------------------------------------------
	// Assigns a job to the vehicle and update status accordingly.
	public void runJob(Job job) {
		if (!isAvailable()) {
			throw new IllegalStateException("Vehicle is not available.");
		}

		status = "BUSY";
		currentJob = job;
		job.setStatus("IN_PROGRESS");
	}

	// ---------------------------------------------------------------------
	// Creates and returns a checkpoint for the current vehicle and job. 
	public Checkpoint createCheckpoint(Job job) {
		return new Checkpoint(this, job);
	}

	// ---------------------------------------------------------------------
	// Restore a vehicles state using the provided checkpoint.
	public void restoreCheckpoint(Checkpoint checkpoint) {
		if (checkpoint == null) {
			throw new IllegalArgumentException("Checkpoint cannot be null.");
		}

		status = "BUSY";
	}

	// ---------------------------------------------------------------------
	// Erases the vehicles stored data and resets its status to Available. 
	public void eraseData() {
		currentJob = null;
		status = "AVAILABLE";
	}

	// ---------------------------------------------------------------------
	// Records vehicles departure time and changes its status to DEPARTED.
	public void depart() {
		if (status.equals("BUSY")) {
			createCheckpoint(currentJob);
		}

		eraseData();
		status = "DEPARTED";
		departureTime = LocalDateTime.now();
	}
	
	// ---------------------------------------------------------------------
	// Manually marks the vehicles as available unless it has already departed. 
	public void markAvailable() {
		if (!status.equals("DEPARTED")) {
			status = "AVAILABLE";
		}
	}
	
}
