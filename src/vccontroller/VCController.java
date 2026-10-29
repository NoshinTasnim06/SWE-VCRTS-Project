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
	
	public void assignJob(Job job)
	{
		Vehicle v = null;
		if (availableVehicles.size() == 0)
			return;
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
		//job.getDuration() < limit
		checkpoints.add(v.createCheckpoint(job));
		v.runJob(job);
		job.setStatus("ASSIGNED");
		jobsInProgress.add(job);
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
	
	/*
	public boolean nextFreeVehicle(Job job, int remainingJobTime)
	{
		String jobID = job.getJobID();
		for (Checkpoint c: checkpoints)
		{
			if (c.getJobID().equals(jobID) &&
			Duration.between(LocalDateTime.now(),findVehicle(c.getVehicleID()).getDepartureTime()).toMinutes()
			> remainingJobTime) //find departure given vehicleID
				return true;
				
		}
		return false;
	}*/
	
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
						&& (remainingJobTime < time || time == -1)) //find departure given vehicleID
					time = remainingJobTime;
			}
		}
		return time;
	}
	
	public Vehicle findVehicle(String vehicleID) {
		return vehicles.get(vehicleID);
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
}