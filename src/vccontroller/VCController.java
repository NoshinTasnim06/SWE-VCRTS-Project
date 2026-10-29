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
	}

	public Client findClient(String clientID) {
		return clients.get(clientID);
	}

	public Owner findOwner(String ownerID) {
		return owners.get(ownerID);
	}
	
	public Vehicle findVehicle(String vehicleID) {
		return vehicles.get(vehicleID);
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
		vehicles.put(vehicle.getVehicleID(), vehicle);
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
			assignJob(job);
		}
	}
	
	public void assignJob(Job job)
	{
		Vehicle v = recruitVehicle(job);
		if (v == null)
			return;
		
		for (Checkpoint c : checkpoints)
		{
			if (c.getJobID() == job.getJobID())
			{
				restartJob(job, v, c);
				return;
			}
		}
		checkpoints.add(v.createCheckpoint(job));
		v.runJob(job);
		job.setStatus("IN_PROGRESS");
		jobsInProgress.add(job);
	}
	
	// prioritize vehicle with enough available time
	public Vehicle recruitVehicle(Job job)
	{
		Vehicle v = null;
		if (availableVehicles.size() == 0)
			return v;
		
		for (int i = 0; i < availableVehicles.size(); i++)
		{
			Vehicle curr = availableVehicles.get(i);
			if (job.getDuration() <= 
					Duration.between(LocalDateTime.now(),curr.getDepartureTime()).toMinutes())
			{
				v = availableVehicles.remove(i);
				break;
			}
		}
		
		if (v == null)
			v = availableVehicles.remove(0);
		return v;
	}
	
	public int getWaitTime(Job job)
	{
		int time = -1;
		PriorityQueue<Job> jobs = jobQueue;
		String jobID = job.getJobID();
		
		if (availableVehicles.size() == 0 && jobsInProgress.size() == 0)
			return time;
		if (jobs.size() <= availableVehicles.size())
			return 0;
		
		time = nextFreeVehicleTime();
		if (time == -1)
			return time; //no available future vehicles
		
		while (jobs.size() != 0)
		{
			Job curr = jobs.poll();
			if (curr.getJobID().equals(jobID))
				break;	
			time += curr.getDuration();
		}
		return time;
	}
	
	public int nextFreeVehicleTime()
	{
		int time = -1;
		for (Job j: jobsInProgress)
		{
			int remainingJobTime = (100 - j.getProgress()) * j.getDuration();
			for (Checkpoint c: checkpoints)
			{
				if (c.getJobID().equals(j.getJobID()) &&
						Duration.between(LocalDateTime.now(),findVehicle(c.getVehicleID()).getDepartureTime()).toMinutes()
						> remainingJobTime 
						&& (remainingJobTime < time || time == -1))
					time = remainingJobTime;
			}
		}
		return time;
	}
	
	public int getAverageWaitTime()
	{
		int time = -1;
		int count = jobQueue.size();
		if (count >= 0 && availableVehicles.size() == 0 && jobsInProgress.size() == 0)
		{
			if (count == 0)
				return 0;
			else
				return time;
		}
		if (count <= 1 && jobsInProgress.size() == 0 || (count <= availableVehicles.size()))
			return 0;
		
		time = nextFreeVehicleTime();
		if (time == -1)
			return time; //future vehicles are unavailable
		
		PriorityQueue<Job> jobs = jobQueue;
		int waitTime = time;
		for (int j = 0; j < jobs.size() - 1; j++)
		{
			waitTime += jobs.peek().getDuration();
			time += waitTime;
		}
		
		if (count == 0)
			return time; //no jobs waiting, jobs in queue
		else
			return time/count;
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

		availableVehicles.remove(vehicle);
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
				Vehicle replacement = findReplacementVehicle();

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

	// ---------------------------------------------------------------------
	// This method returns the first vehicle in the available list that is
	// free to take over a job, or null if there is none.
	private Vehicle findReplacementVehicle() {
		for (Vehicle candidate : availableVehicles) {
			if (candidate.isAvailable()) {
				return candidate;
			}
		}
		return null;
	}
}
