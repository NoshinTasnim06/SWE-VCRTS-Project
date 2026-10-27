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

	public String getVehicleID() {
		return vehicleID;
	}

	public Owner getOwner() {
		return owner;
	}

	public boolean isAvailable() {
		return status.equals("AVAILABLE");
	}

	public String getStatus() {
		return status;
	}

	public void runJob(Job job) {
		if (!isAvailable()) {
			throw new IllegalStateException("Vehicle is not available.");
		}

		status = "BUSY";
		job.setStatus("IN_PROGRESS");
	}

	public Checkpoint createCheckpoint(Job job) {
		return new Checkpoint(this, job);
	}

	public void restoreCheckpoint(Checkpoint checkpoint) {
		if (checkpoint == null) {
			throw new IllegalArgumentException("Checkpoint cannot be null.");
		}

		status = "BUSY";
	}

	public void eraseData() {
		// How are we erasing data?
		status = "AVAILABLE";
	}

	public void depart() {
		if (status.equals("BUSY")) {
			throw new IllegalStateException("Cannot depart while executing a job.");
		}

		eraseData();
		status = "DEPARTED";
		departureTime = LocalDateTime.now();
	}

	public void markAvailable() {
		if (!status.equals("DEPARTED")) {
			status = "AVAILABLE";
		}
	}
}
