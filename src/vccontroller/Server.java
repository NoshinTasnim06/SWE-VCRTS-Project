package vccontroller;

import java.util.*;

public class Server {
	private List<Job> completedJobs;
	
	public Server() {
		completedJobs = new ArrayList<>();
	}
	
	public void storeCompletedJob(Job job) {
		completedJobs.add(job);
	}
	
	public List<Job> getCompletedJobs(){
		return new ArrayList<>(completedJobs);
	}
}
