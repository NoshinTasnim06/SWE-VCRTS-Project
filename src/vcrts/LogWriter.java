/* Project: Vehicular Cloud Real Time System - Milestone 2: GUI
 * Class: LogWriter.java
 * Author: Alisen Lam
 * Date: October 3, 2026
 * This LogWriter class records client, owner, vehicle, and job information in text files.
 * It adds a timestamp to each record and allows users to be searched by their ID.
 */

package vcrts;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogWriter {

	private static final String CLIENTS_FILE = "clients.txt";
	private static final String OWNERS_FILE = "owners.txt";
	private static final String VEHICLES_FILE = "vehicles.txt";
	private static final String JOBS_FILE = "jobs.txt";
	private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //-----------------------------------------------------
    // Records client information in the client data file.
	public static synchronized void logClient(String clientId, String firstName, String lastName) {
		if (new File(CLIENTS_FILE).length() == 0) {
	        writeHeader(CLIENTS_FILE, "Client ID, First Name, Last Name");
	    }
		String data = clientId + ", " + firstName + ", " + lastName;
		writeToFile(CLIENTS_FILE, data);
	}

    //-----------------------------------------------------
    // Records owner information in the owner data file.
	public static synchronized void logOwner(String ownerId, String firstName, String lastName){
		if (new File(OWNERS_FILE).length() == 0) {
	        writeHeader(OWNERS_FILE, "Owner ID, First Name, Last Name");
	    }
		String data = ownerId + ", " + firstName + ", " + lastName;
		writeToFile(OWNERS_FILE, data);
	}

    //-----------------------------------------------------
    // Records vehicle information in the vehicle data file.
	public static synchronized void logVehicle(String ownerId, String make, String brand, String year, String license, String residencyTime){
		if (new File(VEHICLES_FILE).length() == 0) {
	        writeHeader(VEHICLES_FILE, "Owner ID, Make, Brand, Year, License, Residency Time");
	    }
		String data = ownerId + ", " + make + ", " + brand + ", " + year + ", " + license + ", " + residencyTime;
		writeToFile(VEHICLES_FILE, data);
	} 

    //-----------------------------------------------------
    // Records job information in the job data file.
	public static synchronized void logJob(String clientId, String jobName, String duration, String deadline){
		if (new File(JOBS_FILE).length() == 0) {
	        writeHeader(JOBS_FILE, "Client ID, Job Name, Duration, Deadline");
	    }
		String data = clientId + ", " + jobName + ", " + duration + ", " + deadline;
		writeToFile(JOBS_FILE, data);
	}

    //-----------------------------------------------------
    // Writes the header to the specified file.
	public static synchronized void writeHeader(String filename, String header){
		try(PrintWriter addHeader = new PrintWriter(new FileWriter(filename, true))){
			addHeader.println("Timestamp, "  + header);
		}catch (IOException exception){
			System.out.println("Unable to write header to " + filename);
			exception.printStackTrace();
		}
	}


    //-----------------------------------------------------
    // Writes the provided data to the specified file with a timestamp.
    // New records are appended to preserve existing data.
	public static synchronized void writeToFile(String filename, String data){
		String timestamp = LocalDateTime.now().format(TIMESTAMP);
		try(PrintWriter addData = new PrintWriter(new FileWriter(filename, true))){
			addData.println(timestamp + ", " +data);
		}catch (IOException exception){
			System.out.println("Unable to write to " + filename);
			exception.printStackTrace();
		}
	}

    //-----------------------------------------------------
    // Searches the client or owner records for a user with a specified username.
        public static String[] findUser(String userType, String username){
		String file;

		if(userType.equals("Owner")){
			file = OWNERS_FILE;
		}else if(userType.equals("Client")){
			file = CLIENTS_FILE;
		}else{
			return null;
		}

		if(new File(file).length() == 0){
			return null;
		}

		try(BufferedReader reader = new BufferedReader (new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(", ");

				if(parts.length >= 4 && parts[1].equals(username)){
					return parts; //timestamp, clientId/ownerId, fname, lname
				}
			}
		}catch(IOException exception){
			System.out.println("Error: Unable to read from " + file);
			exception.printStackTrace();
		}

		return null;
	}

}//End of class


