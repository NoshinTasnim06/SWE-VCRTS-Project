package vcrts;

import java.io.*;

public class LogWriter {
	/* Todo's: add timestamp command
	 * Attempt to implement Thread Saftey
	 */
	
    /* ATTRIBUTES:
     * Make all private static final String: CLIENTS_FILE ("clients.txt"), OWNERS_FILE ("owners.txt"), 
     * VEHICLES_FILE ("vehicles.txt"), JOBS_FILE ("jobs.txt")
     */
	private static final String CLIENTS_FILE = "clients.txt";
	private static final String OWNERS_FILE = "owners.txt";
	private static final String VEHICLES_FILE = "vehicles.txt";
	private static final String JOBS_FILE = "jobs.txt";

    /* METHOD: logClient(String clientId, String firstName, String lastName)
     * Make public static. Format client info into comma-separated text and pass to writeToFile().
     */
	public static void logClient(String clientId, String firstName, String lastName) {
		String data = clientId + ", " + firstName + ", " + lastName;
		writeToFile(CLIENTS_FILE, data);
	}

    /* METHOD: logOwner(String ownerId, String firstName, String lastName)
     * Make public static. Format owner info into comma-separated text and pass to writeToFile().
     */
	public static void logOwner(String ownerId, String firstName, String lastName){
		String data = ownerId + ", " + firstName + ", " + lastName;
		writeToFile(OWNERS_FILE, data);
	}

    /* METHOD: logVehicle(String ownerId, String make, String brand, String year, String license, String residencyTime)
     * Make public static. Format vehicle info into comma-separated text and pass to writeToFile().
     */

	public static void logVehicle(String ownerId, String make, String brand, String year, String license, String residencyTime){
		String data = ownerId + ", " + make + ", " + brand + ", " + year + ", " + license + ", " + residencyTime;
		writeToFile(VEHICLES_FILE, data);
	} 

    /* METHOD: logJob(String clientId, String jobName, String duration, String deadline)
     * Make public static. Format job info into comma-separated text and pass to writeToFile().
     */
	public static void logJob(String clientId, String jobName, String duration, String deadline){
		String data = clientId + ", " + jobName + ", " + duration + ", " + deadline;
		writeToFile(JOBS_FILE, data);
	}

    /* METHOD: writeToFile(String filename, String data)
     * Make public static. Append string line to specified file using FileWriter and PrintWriter.
     * Keep previous records.
     */
	public static void writeToFile(String filename, String data){
		try(PrintWriter addData = new PrintWriter(new FileWriter(filename, true))){
			addData.println(data);
		}catch (IOException exception){
			System.out.println("Unable to write to " + filename);
			exception.printStackTrace();
		}		
	}

}//End of class

