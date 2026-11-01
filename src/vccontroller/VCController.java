package vccontroller;

import java.util.*;
import java.time.*;

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

/**
 * This class is the central controller of the Vehicular Cloud. It keeps the queue of
 * waiting jobs, the pool of available vehicles and the saved checkpoints, assigns jobs
 * to vehicles, handles vehicles leaving, and computes the priority schedule (waiting
 * and completion time of each job) shown on the controller screen.
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
	private Map<String, Vehicle> vehicles;
	private Map<Vehicle, Job> runningJobs;

	// ---------------------------------------------------------------------
	// This constructor creates an empty controller with the given payment rate
	// and the server that stores completed jobs.
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
		vehicles = new HashMap<>();
		runningJobs = new HashMap<>();
	}

	// ---------------------------------------------------------------------
	// These methods look up a registered client, owner or vehicle by its ID.
	public Client findClient(String clientID) {
		return clients.get(clientID);
	}

	public Owner findOwner(String ownerID) {
		return owners.get(ownerID);
	}

	public Vehicle findVehicle(String vehicleID) {
		return vehicles.get(vehicleID);
	}

	// ---------------------------------------------------------------------
	// This method adds a job to the waiting queue unless it was cancelled.
	public void addJobToQueue(Job job) {
		if (job != null && !job.getStatus().equals("CANCELLED")) {
			jobQueue.offer(job);
		}
	}

	// ---------------------------------------------------------------------
	// This method replaces the waiting queue with the given jobs, so the queue
	// can be rebuilt from the saved job records without adding any job twice.
	public void setJobs(List<Job> jobs) {
		jobQueue.clear();
		for (Job job : jobs) {
			addJobToQueue(job);
		}
	}

	// ---------------------------------------------------------------------
	// This method registers a vehicle with the controller and, if it is
	// available, adds it to the pool of vehicles that can take jobs.
	public void addVehicleToList(Vehicle vehicle) {
		if (vehicle == null) {
			return;
		}
		if (vehicle.isAvailable() && !availableVehicles.contains(vehicle)) {
			availableVehicles.add(vehicle);
		}
		vehicles.put(vehicle.getVehicleID(), vehicle);
	}

	// ---------------------------------------------------------------------
	// This method sets how many vehicles run the same job at once.
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

	// ---------------------------------------------------------------------
	// This method calculates what a job costs, based on its duration and the payment rate.
	public double calculateJobCost(Job job) {
		//Are we calculating by duration or by progress and resources?
		return job.getDuration() * paymentRate;
	}

	// ---------------------------------------------------------------------
	// This method assigns the job at the front of the queue to a vehicle.
	public void nextJob() {
		Job job = jobQueue.peek();
		if (job != null) {
			assignJob(job);
		}
	}

	// ---------------------------------------------------------------------
	// This method starts a job on a recruited vehicle, or continues it from its
	// saved checkpoint if one already exists.
	public void assignJob(Job job) {
		Vehicle v = recruitVehicle(job);
		if (v == null)
			return;

		// If the job already has a checkpoint, continue from it instead of starting over
		for (Checkpoint c : checkpoints) {
			if (c.getJobID().equals(job.getJobID())) {
				restartJob(job, v, c);
				return;
			}
		}

		// Otherwise save a first checkpoint and start the job
		checkpoints.add(v.createCheckpoint(job));
		v.runJob(job);
		job.setStatus("IN_PROGRESS");
		jobsInProgress.add(job);
		runningJobs.put(v,job);
	}

	// prioritize vehicle with enough available time
	public Vehicle recruitVehicle(Job job) {
		if (availableVehicles.isEmpty())
			return null;

		// Prefer a vehicle that is known to stay long enough to finish the job
		for (Vehicle curr : availableVehicles) {
			if (curr.getDepartureTime() != null
					&& job.getDuration() <= Duration.between(LocalDateTime.now(), curr.getDepartureTime()).toMinutes()) {
				availableVehicles.remove(curr);
				return curr;
			}
		}

		// Otherwise take the first available vehicle
		return availableVehicles.remove(0);
	}

	// ---------------------------------------------------------------------
	// This method continues a job on a new vehicle from a saved checkpoint.
	public void restartJob(Job job, Vehicle vehicle, Checkpoint cp) {
		if (job == null || vehicle == null || cp == null) {
			throw new IllegalArgumentException("Job, vehicle and checkpoint are all required.");
		}
		if (!cp.getJobID().equals(job.getJobID())) {
			throw new IllegalArgumentException("Checkpoint does not belong to this job.");
		}
		if (job.getStatus().equals("COMPLETED") || job.getStatus().equals("CANCELLED")) {
			throw new IllegalStateException("Only unfinished jobs can be restarted.");
		}
		if (!vehicle.isAvailable()) {
			throw new IllegalStateException("Vehicle is not available.");
		}

		// Give the new vehicle the saved state (this marks the vehicle as busy)
		cp.copyTo(vehicle);

		// Work done after the checkpoint was lost, so the job resumes from the
		// checkpoint's progress.
		if (cp.getProgressSnapshot() < 100) {
			job.updateProgress(cp.getProgressSnapshot());
		}
		job.setStatus("IN_PROGRESS");

		vehicle.runJob(job);
		jobQueue.remove(job);
		runningJobs.put(vehicle, job);
		if (!jobsInProgress.contains(job)) {
			jobsInProgress.add(job);
		}
	}

	// ---------------------------------------------------------------------
	// This method takes a job out of the system once it is completed, cancelled
	// or failed. Every vehicle running it has its data erased and is made
	// available again, and the job's checkpoints are discarded. Completed jobs
	// are also sent to the server and their cost is charged to the client.
	public void removeJob(Job job, String status) {
		if (job == null || status == null) {
			throw new IllegalArgumentException("Job and status are required.");
		}
		String finalStatus = status.trim().toUpperCase();
		if (!finalStatus.equals("COMPLETED") && !finalStatus.equals("CANCELLED")
				&& !finalStatus.equals("FAILED")) {
			throw new IllegalArgumentException("Status must be COMPLETED, CANCELLED or FAILED.");
		}

		job.setStatus(finalStatus);
		jobQueue.remove(job);
		jobsInProgress.remove(job);

		// Free every vehicle that was running this job. With redundancy there
		// can be more than one.
		Iterator<Map.Entry<Vehicle, Job>> entries = runningJobs.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<Vehicle, Job> entry = entries.next();
			if (entry.getValue() == job) {
				Vehicle vehicle = entry.getKey();
				vehicle.eraseData();
				addVehicleToList(vehicle);
				entries.remove();
			}
		}

		// The job is finished, so its saved states are no longer needed
		checkpoints.removeIf(cp -> cp.getJobID().equals(job.getJobID()));

		// Completed jobs are stored on the server and charged to the client
		if (finalStatus.equals("COMPLETED")) {
			server.storeCompletedJob(job);
			if (job.getClient() != null) {
				job.getClient().addCharge(calculateJobCost(job));
			}
		}
	}

	// ---------------------------------------------------------------------
	// This method prepares a vehicle to leave the lot. If it is running a job,
	// a checkpoint is saved first. When no other vehicle is still running that
	// job, the job restarts from the checkpoint on another available vehicle,
	// or goes back in the queue if none is free. The vehicle's data is erased
	// before it departs.
	public void handleDeparture(Vehicle vehicle) {
		if (vehicle == null) {
			throw new IllegalArgumentException("Vehicle is required.");
		}

		Job job = runningJobs.remove(vehicle);
		availableVehicles.remove(vehicle);

		if (job != null) {
			Checkpoint cp = vehicle.createCheckpoint(job);
			checkpoints.add(cp);

			// With redundancy, another vehicle may still be running the same
			// job, in which case the job simply continues there
			if (!runningJobs.containsValue(job)) {
				Vehicle replacement = recruitVehicle(job);
				if (replacement != null) {
					restartJob(job, replacement, cp);
				} else {
					// No vehicle is free: keep the checkpoint and wait in the queue
					jobsInProgress.remove(job);
					job.setStatus("QUEUED");
					jobQueue.offer(job);
				}
			}
		}

		// Erase the vehicle's copy of the computation, then let it leave
		vehicle.eraseData();
		vehicle.depart();
	}

	// Orders the queued jobs by priority and fills in each job's waiting and
	// completion time in minutes. All jobs are assumed to arrive at time zero.
	public List<Job> computeSchedule() {
		List<Job> ordered = new ArrayList<>(jobQueue);
		Collections.sort(ordered);

		// Each job waits for the total duration of every job scheduled before it
		int clock = 0;
		for (Job job : ordered) {
			job.setWaitingMinutes(clock);
			clock += job.getDuration();
			job.setCompletionMinutes(clock);
		}
		return ordered;
	}

	// Average of the waiting times from computeSchedule()
	public double getAverageWaitingMinutes() {
		List<Job> ordered = computeSchedule();
		if (ordered.isEmpty()) {
			return 0;
		}

		double total = 0;
		for (Job job : ordered) {
			total += job.getWaitingMinutes();
		}
		return total / ordered.size();
	}
	
	public void triggerCheckpoint(Job job)
	{
		LocalDateTime currTime = LocalDateTime.now();
		double progress = job.getProgress();
		for (Checkpoint c : checkpoints)
		{
			if (c.getJobID().equals(job.getJobID()))
			{
				progress += Math.round((Duration.between(c.getTimestamp(), currTime).toMinutes() / (job.getDuration()*1.0)) * 10000)
						/100.0;
				job.updateProgress(progress);
				if (job.getProgress() == 100)
				{
					removeJob(job, "COMPLETED");
					return;
				}
			}
		}
	}
}