package vccontroller;

import java.util.*;

/* Need to implement Or correct
 * + assignJob(job: Job): void
 * + triggerCheckpoint(job: Job): void
 * + recruitVehicle(job: Job): Vehicle
 * + restartJob(job: Job, vehicle: Vehicle, cp: Checkpoint): void
 * + removeJob(job: Job, status: String): void
 * + handleDeparture(vehicle: Vehicle): void
 * + getCompletedJobs(): List<Job>
 * + calculateJobCost(job: Job): double
 */

public class VCController {
	private int redundancyLevel;
	private List<Vehicle> availableVehicles;
	private PriorityQueue<Job> jobQueue;
	private double paymentRate;
	private List<Job> jobsInProgress;
	private List<Checkpoint> checkpoints;
	private Server server;

	private Map<String, Client> clients;
	private Map<String, Owner> owners;

	public VCController(double paymentRate, Server server) {
		this.redundancyLevel = 1;
		this.paymentRate = paymentRate;
		this.server = server;

		availableVehicles = new ArrayList<>();
		jobQueue = new PriorityQueue<>();
		jobsInProgress = new ArrayList<>();
		checkpoints = new ArrayList<>();
		clients = new HashMap<>();
		owners = new HashMap<>();
	}

	public Client findClient(String clientID) {
		return clients.get(clientID);
	}

	public Owner findOwner(String ownerID) {
		return owners.get(ownerID);
	}

	public void addJobToQueue(Job job) {
		if (job != null && !job.getStatus().equals("CANCELLED")) {
			jobQueue.offer(job);
		}
	}

	public void addVehicleToList(Vehicle vehicle) {
		if (vehicle != null && vehicle.isAvailable() && !availableVehicles.contains(vehicle)) {
			availableVehicles.add(vehicle);
		}
	}

	public void setRedundancyLevel(int level) {
		if (level < 1) {
			throw new IllegalArgumentException("Redundancy must be at least 1.");
		}

		redundancyLevel = level;
	}

	public List<Job> getJobsInProgress() {
		return new ArrayList<>(jobsInProgress);
	}

	public List<Job> getCompletedJobs() {
		// Connect to server?
		return new ArrayList<>();
	}

	public double calculateJobCost(Job job) {
		//Are we calculating by duration or by progress and resources?
		return job.getDuration() * paymentRate;
	}

	public void nextJob() {
		Job job = jobQueue.peek();

		if (job != null) {
			// assign job
		}
	}
}