package vccontroller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public class DesignClassTests {

	// ---------------------------------------------------------------------
	// Check if Person name is correct
	@Test
	public void testPersonName() {
		Person p = new Person("John", "Smith");

		assertEquals("John Smith", p.getName());
	}

	// ---------------------------------------------------------------------
	// Check if Client Creation is correct
	@Test
	public void testClientCreation() {
		Client c = new Client("HarryPotter", "Harry", "Potter");

		assertEquals("HarryPotter", c.getClientID());
	}

	// ---------------------------------------------------------------------
	// Test Client submitting first job.
	// Check Job status and Job name.
	@Test
	public void testSubmitJob() {
		Client c = new Client("HermioneGranger", "Hermione", "Granger");

		Job job = c.submitJob("Phoenix", 30, LocalDateTime.now().plusHours(2));

		assertEquals("HermioneGranger-1", job.getJobID());
		assertEquals("Phoenix", job.getJobName());
		assertEquals("QUEUED", job.getStatus());
	}

	// ---------------------------------------------------------------------
	// Check if Client canceljob() works
	@Test
	public void testCancelJob() {
		Client c = new Client("HermioneGranger", "Hermione", "Granger");
		Job job = c.submitJob("Gryffindor", 20, LocalDateTime.now().plusHours(2));

		c.cancelJob(job.getJobID());

		assertEquals("CANCELLED", job.getStatus());
	}

	// ---------------------------------------------------------------------
	// Test updating job progress
	@Test
	public void testUpdateProgress() {
		Client c = new Client("HermioneGranger", "Hermione", "Granger");
		Job job = c.submitJob("Gryffindor", 20, LocalDateTime.now().plusHours(2));

		job.updateProgress(50);

		assertEquals(50, job.getProgress());
		assertEquals("IN_PROGRESS", job.getStatus());
	}

	// ---------------------------------------------------------------------
	// Test completing a job
	@Test
	public void testCompleteJob() {
		Client c = new Client("HermioneGranger", "Hermione", "Granger");
		Job job = c.submitJob("Gryffindor", 20, LocalDateTime.now().plusHours(2));

		job.updateProgress(100);

		assertEquals(100, job.getProgress(), 0.001);
		assertEquals("COMPLETED", job.getStatus());
		assertNotNull(job.getCompletionTime());
	}

	// ---------------------------------------------------------------------
	// Check if Owner Creation is correct
	@Test
	public void testOwnerCreation() {
		Owner o = new Owner("RonaldWeasley", "Ronald", "Weasley");

		assertEquals("RonaldWeasley", o.getOwnerID());
	}

	// ---------------------------------------------------------------------
	// Test registering a vehicle
	@Test
	public void testRegisterVehicle() {
		Owner o = new Owner("RonaldWeasley", "Ronald", "Weasley");

		Vehicle v = o.registerVehicle("Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);

		assertEquals("RonaldWeasley-1", v.getVehicleID());
		assertEquals(1, o.getVehicleList().size());
	}

	// ---------------------------------------------------------------------
	// Test removing a vehicle
	@Test
	public void testRemoveVehicle() {
		Owner o = new Owner("RonaldWeasley", "Ronald", "Weasley");
		Vehicle v = o.registerVehicle("Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);

		o.removeVehicle(v.getVehicleID());
		assertTrue(o.getVehicleList().isEmpty());
	}

	// ---------------------------------------------------------------------
	// Test vehicle availability
	@Test
	public void testVehicleAvailability() {
		Owner o = new Owner("RonaldWeasley", "Ronald", "Weasley");
		Vehicle v = o.registerVehicle("Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);

		assertTrue(v.isAvailable());
	}

	// ---------------------------------------------------------------------
	// Test assigning a job to a vehicle
	@Test
	public void testRunJob() {
		Vehicle v = new Vehicle("RonaldWeasley", "Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);
		Job job = new Job("HermioneGranger", "Gryffindor", 20, LocalDateTime.now().plusHours(2));

		v.runJob(job);

		assertEquals("BUSY", v.getStatus());
		assertEquals("IN_PROGRESS", job.getStatus());
	}

	// ---------------------------------------------------------------------
	// Test eraseData()
	@Test
	public void testEraseData() {
		Vehicle v = new Vehicle("RonaldWeasley", "Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);
		Job job = new Job("HermioneGranger", "Gryffindor", 20, LocalDateTime.now().plusHours(2));

		v.runJob(job);
		v.eraseData();

		assertEquals("AVAILABLE", v.getStatus());
		assertTrue(v.isAvailable());
	}

	// ---------------------------------------------------------------------
	// Test creating checkpoint
	@Test
	public void testCreateCheckpoint() {
		Vehicle v = new Vehicle("RonaldWeasley", "Ford", "Anglia", 1985, "7990TD", 16, 8, 500, 5);
		Job job = new Job("HermioneGranger", "Gryffindor", 20, LocalDateTime.now().plusHours(2));

		job.updateProgress(40);

		Checkpoint checkpoint = v.createCheckpoint(job);

		assertEquals("HermioneGranger", checkpoint.getJobID());
		assertEquals("RonaldWeasley", checkpoint.getVehicleID());
		assertEquals(40, checkpoint.getProgressSnapshot());
	}

	// ---------------------------------------------------------------------
	// Test Server class
	@Test
	public void testServerStoreJob() {
		Server server = new Server();
		Job job = new Job("HermioneGranger", "Gryffindor", 20, LocalDateTime.now().plusHours(2));
		server.storeCompletedJob(job);

		assertTrue(server.getCompletedJobs().contains(job));
	}
}
