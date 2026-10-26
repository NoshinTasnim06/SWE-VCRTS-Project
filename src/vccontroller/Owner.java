package vccontroller;

import java.util.*;

public class Owner extends Person {
	private String ownerID;
	private double paymentEarned;
	private List<Vehicle> vehicleList;

	public Owner(String ownerID, String fname, String lname) {
		super(fname, lname);
		this.ownerID = ownerID;
		this.paymentEarned = 0;
		this.vehicleList = new ArrayList<>();
	}

	public String getOwnerID() {
		return ownerID;
	}

	public List<Vehicle> getVehicleList() {
		return vehicleList;
	}

	public double getPaymentEarned() {
		return paymentEarned;
	}

	//If combining with GUI, this may not be necessary.
	public Vehicle registerVehicle(String make, String model, int year, String licensePlate, int ramGB, int cpuCores, int storageGB, int residencyTime) {
		String vehicleId = UUID.randomUUID().toString();
		
		Vehicle vehicle = new Vehicle(vehicleId,make, model, year, licensePlate, ramGB, cpuCores, storageGB, residencyTime);

		vehicleList.add(vehicle);
		return vehicle;
	}

	public void removeVehicle(String vehicleID) {
		vehicleList.removeIf(vehicle -> vehicle.getVehicleID().equals(vehicleID));
	}

	public void addEarnings(double amount) {
		if (amount >= 0) {
			paymentEarned += amount;
		}
	}
}//End of class