/* Project: Vehicular Cloud Real Time System - Milestone 2: GUI
 * Class: LogWriter.java
 * Author: Alisen Lam
 * Date: October 3, 2026
 * This LogWriter class records client, owner, vehicle, and job information in text files.
 * It adds a timestamp to each record and allows users to be searched by their ID.
 */

package vcrts;

import vccontroller.Job;
import vccontroller.Client;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class stores and retrieves all VCRTS records (clients, owners, vehicles
 * and jobs) in their text files. Every record is one line of comma separated
 * values that starts with a timestamp and the record ID.
 */
public class LogWriter {

	private static final String CLIENTS_FILE = "clients.txt";
	private static final String OWNERS_FILE = "owners.txt";
	private static final String VEHICLES_FILE = "vehicles.txt";
	private static final String JOBS_FILE = "jobs.txt";
	private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	// Column that holds the record ID in every file, and the column that holds
	// the owning user's ID in the vehicle and job files
	private static final int ID_COLUMN = 1;
	private static final int USER_COLUMN = 2;

	// Number of columns in a complete vehicle record and a complete job record
	private static final int VEHICLE_COLUMNS = 12;
	private static final int JOB_COLUMNS = 7;

	//-----------------------------------------------------
	// Keeps only the records with at least the given number of columns, so rows
	// saved by an older version of the program are skipped instead of causing errors.
	private static List<String[]> completeRecords(List<String[]> records, int columns) {
		records.removeIf(record -> record.length < columns);
		return records;
	}

	//-----------------------------------------------------
	// Returns the data file that stores the given type of user, or null if the type is unknown.
	private static String userFile(String userType) {
		if (userType.equals("Owner")) {
			return OWNERS_FILE;
		}
		if (userType.equals("Client")) {
			return CLIENTS_FILE;
		}
		return null;
	}

	//-----------------------------------------------------
	// Records client or owner information in the matching user data file.
	public static synchronized void logUser(String userType, String userId, String firstName, String lastName) {
		String file = userFile(userType);
		if (file != null) {
			logRecord(file, userType + " ID, First Name, Last Name", userId, firstName, lastName);
		}
	}

	//-----------------------------------------------------
	// Searches the client or owner records for a user with a specified username.
	public static String[] findUser(String userType, String username) {
		String file = userFile(userType);
		if (file == null) {
			return null;
		}
		List<String[]> matches = findRecords(file, ID_COLUMN, username);
		return matches.isEmpty() ? null : matches.get(0); // timestamp, clientId/ownerId, fname, lname
	}

	// Returns this owner's vehicle records.
	// Columns: 0 timestamp, 1 vehicle ID, 2 owner ID, 3 make, 4 model, 5 year,
	// 6 license, 7 RAM, 8 CPU, 9 storage, 10 residency, 11 status
	public static synchronized List<String[]> getVehiclesFor(String ownerId) {
		return completeRecords(findRecords(VEHICLES_FILE, USER_COLUMN, ownerId), VEHICLE_COLUMNS);
	}

	//-----------------------------------------------------
	// Records vehicle information in the vehicle data file.
	public static synchronized String logVehicle(String ownerId, String make, String model, String year,
	                                             String license, String ramGB, String cpuCores,
	                                             String storageGB, String residencyTime) {
		String vehicleId = ownerId + "-" + (countRecords(VEHICLES_FILE, ownerId) + 1);
		logRecord(VEHICLES_FILE,
				"Vehicle ID, Owner ID, Make, Model, Year, License, RAM GB, CPU Cores, Storage GB, Residency Time, Status",
				vehicleId, ownerId, make, model, year, license, ramGB, cpuCores, storageGB, residencyTime, "Available");
		return vehicleId;
	}

	// Returns this client's job records.
	// Columns: 0 timestamp, 1 job ID, 2 client ID, 3 name, 4 duration, 5 deadline, 6 importance
	public static synchronized List<String[]> getJobsFor(String clientId) {
		return completeRecords(findRecords(JOBS_FILE, USER_COLUMN, clientId), JOB_COLUMNS);
	}

	// Returns the job records of every client, in the order they were submitted.
	public static synchronized List<String[]> getAllJobs() {
		return completeRecords(readRecords(JOBS_FILE), JOB_COLUMNS);
	}

	//-----------------------------------------------------
	// Records job information in the job data file.
	public static synchronized String logJob(String clientId, String jobName, String duration,
	                                         String deadline, String importance) {
		String jobId = clientId + "-" + (countRecords(JOBS_FILE, clientId) + 1);
		logRecord(JOBS_FILE, "Job ID, Client ID, Job Name, Duration, Deadline, Importance, Status",
				jobId, clientId, jobName, duration, deadline, importance, "QUEUED");
		return jobId;
	}
	
	//-----------------------------------------------------
	// Builds a Job object from one saved job record (ID, name, duration, deadline and priority).
	public static Job toJob(String[] record) {
	    return new Job(record[1], record[3], Integer.parseInt(record[4]),
	            parseDeadline(record[5]), Integer.parseInt(record[6]));
	}

	//-----------------------------------------------------
	// Adds one saved job record (name, duration, deadline and priority) to the given client
	// by submitting it through the Client class, so the job knows which client it belongs to.
	public static Job addSavedJob(Client client, String[] record) {
	    Job job = client.submitJob(record[3], Integer.parseInt(record[4]), parseDeadline(record[5]));
	    job.setPriority(Integer.parseInt(record[6]));
	    return job;
	}

	//-----------------------------------------------------
	// Changes the saved status of one job (for example to CANCELLED) and rewrites jobs.txt.
	public static synchronized void updateJobStatus(String jobId, String newStatus) {
		File file = new File(JOBS_FILE);
		if (!file.exists()) {
			return;
		}

		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				if (parts.length >= JOB_COLUMNS && parts[ID_COLUMN].equals(jobId)) {
					parts = Arrays.copyOf(parts, JOB_COLUMNS + 1); // adds the Status column if missing
					parts[JOB_COLUMNS] = newStatus;
					line = String.join(", ", parts);
				}
				lines.add(line);
			}
		} catch (IOException exception) {
			System.out.println("Error: Unable to read from " + JOBS_FILE);
			exception.printStackTrace();
			return;
		}

		try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
			for (String savedLine : lines) {
				writer.println(savedLine);
			}
		} catch (IOException exception) {
			System.out.println("Unable to write to " + JOBS_FILE);
			exception.printStackTrace();
		}
	}

	//-----------------------------------------------------
	// Converts a deadline to the text stored in the job file, using "None" when no deadline was set.
	public static String formatDeadline(LocalDateTime deadline) {
		return deadline == null ? "None" : deadline.format(TIMESTAMP);
	}

	//-----------------------------------------------------
	// Converts the deadline text stored in the job file back to a date and time (null for "None").
	public static LocalDateTime parseDeadline(String text) {
		return text.equals("None") ? null : LocalDateTime.parse(text, TIMESTAMP);
	}

	//-----------------------------------------------------
	// Writes one record to a file, adding the header first if the file is empty.
	private static void logRecord(String filename, String header, String... fields) {
		File file = new File(filename);
		if (!file.exists() || file.length() == 0) {
			writeHeader(filename, header);
		}
		writeToFile(filename, String.join(", ", fields));
	}

	//-----------------------------------------------------
	// Reads every record of a file (skipping the header line) as an array of columns.
	private static synchronized List<String[]> readRecords(String filename) {
		List<String[]> records = new ArrayList<>();
		File file = new File(filename);
		if (!file.exists() || file.length() == 0) {
			return records;
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			reader.readLine(); // skip the header line
			String line;
			while ((line = reader.readLine()) != null) {
				records.add(line.split(", "));
			}
		} catch (IOException exception) {
			System.out.println("Error: Unable to read from " + filename);
			exception.printStackTrace();
		}
		return records;
	}

	//-----------------------------------------------------
	// Returns the records of a file whose given column equals the given value.
	private static List<String[]> findRecords(String filename, int column, String value) {
		List<String[]> matches = new ArrayList<>();
		for (String[] record : readRecords(filename)) {
			if (record.length > column && record[column].equals(value)) {
				matches.add(record);
			}
		}
		return matches;
	}

	//-----------------------------------------------------
	// Counts how many records in a file belong to the given user.
	private static int countRecords(String filename, String userId) {
		return findRecords(filename, USER_COLUMN, userId).size();
	}

	//-----------------------------------------------------
	// Writes the header to the specified file.
	public static synchronized void writeHeader(String filename, String header) {
		try (PrintWriter addHeader = new PrintWriter(new FileWriter(filename, true))) {
			addHeader.println("Timestamp, " + header);
		} catch (IOException exception) {
			System.out.println("Unable to write header to " + filename);
			exception.printStackTrace();
		}
	}

	//-----------------------------------------------------
	// Writes the provided data to the specified file with a timestamp.
	// New records are appended to preserve existing data.
	public static synchronized void writeToFile(String filename, String data) {
		String timestamp = LocalDateTime.now().format(TIMESTAMP);
		try (PrintWriter addData = new PrintWriter(new FileWriter(filename, true))) {
			addData.println(timestamp + ", " + data);
		} catch (IOException exception) {
			System.out.println("Unable to write to " + filename);
			exception.printStackTrace();
		}
	}

	// Changes the residency time and/or status of one vehicle (pass null to leave a value alone)
	public static synchronized void updateVehicle(String vehicleId, String newResidency, String newStatus) {
		File file = new File(VEHICLES_FILE);
		if (!file.exists()) {
			return;
		}

		// Read every line, changing the matching vehicle's values as it is found
		List<String> lines = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");
				if (parts.length >= 12 && parts[ID_COLUMN].equals(vehicleId)) {
					if (newResidency != null) {
						parts[10] = newResidency;
					}
					if (newStatus != null) {
						parts[11] = newStatus;
					}
					line = String.join(", ", parts);
				}
				lines.add(line);
			}
		} catch (IOException exception) {
			System.out.println("Error: Unable to read from " + VEHICLES_FILE);
			exception.printStackTrace();
			return;
		}

		// Write all lines back, replacing the old file contents
		try (PrintWriter writer = new PrintWriter(new FileWriter(file, false))) {
			for (String savedLine : lines) {
				writer.println(savedLine);
			}
		} catch (IOException exception) {
			System.out.println("Unable to write to " + VEHICLES_FILE);
			exception.printStackTrace();
		}
	}
}
