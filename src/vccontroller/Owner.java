package vccontroller;

import java.util.*;

public class Owner extends Person {
	private String ownerID;
	private double paymentEarned;
	private List<Vehicle> vehicleList;

	// ---------------------------------------------------------------------
	// Creates a new vehicle owner.
	public Owner(String ownerID, String fname, String lname) {
		super(fname, lname);
		this.ownerID = ownerID;
		this.paymentEarned = 0;
		this.vehicleList = new ArrayList<>();
	}

	// ---------------------------------------------------------------------
	// Returns the owner's unique ID.
	public String getOwnerID() {
		return ownerID;
	}
	
	// ---------------------------------------------------------------------
	// Returns the list of vehicles registered by the owner. 
	public List<Vehicle> getVehicleList() {
		return vehicleList;
	}

	// ---------------------------------------------------------------------
	// Returns the owners earnings.
	public double getPaymentEarned() {
		return paymentEarned;
	}

	// ---------------------------------------------------------------------
	// Registers a new vehicle and adds it to the owner's vehicle list. 
	public Vehicle registerVehicle(String make, String model, int year, String licensePlate, int ramGB, int cpuCores, int storageGB, int residencyTime) {
		String vehicleId = ownerID + "-" + (vehicleList.size() + 1);
		
		Vehicle vehicle = new Vehicle(vehicleId,make, model, year, licensePlate, ramGB, cpuCores, storageGB, residencyTime);

		vehicleList.add(vehicle);
		return vehicle;
	}

	// ---------------------------------------------------------------------
	// Removes a registered vehicle from the ownder's vehicle list using its ID.
	public void removeVehicle(String vehicleID) {
		vehicleList.removeIf(vehicle -> vehicle.getVehicleID().equals(vehicleID));
	}

	// ---------------------------------------------------------------------
	// Adds an amount to the owner's total earnings. 
	public void addEarnings(double amount) {
		if (amount >= 0) {
			paymentEarned += amount;
		}
	}
	
}//End of class