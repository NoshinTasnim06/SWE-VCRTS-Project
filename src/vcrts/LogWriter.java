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
	public static void logClient(String clientId, String firstName, String lastName) {
		String data = clientId + ", " + firstName + ", " + lastName;
		writeToFile(CLIENTS_FILE, data);
	}

    //-----------------------------------------------------
    // Records owner information in the owner data file.
	public static void logOwner(String ownerId, String firstName, String lastName){
		String data = ownerId + ", " + firstName + ", " + lastName;
		writeToFile(OWNERS_FILE, data);
	}
    //-----------------------------------------------------
    // Records vehicle information in the vehicle data file.
	public static void logVehicle(String ownerId, String make, String brand, String year, String license, String residencyTime){
		String data = ownerId + ", " + make + ", " + brand + ", " + year + ", " + license + ", " + residencyTime;
		writeToFile(VEHICLES_FILE, data);
	} 

    //-----------------------------------------------------
    // Records job information in the job data file.
	public static void logJob(String clientId, String jobName, String duration, String deadline){
		String data = clientId + ", " + jobName + ", " + duration + ", " + deadline;
		writeToFile(JOBS_FILE, data);
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

                try(BufferedReader reader = new BufferedReader (new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                                String[] parts = line.split(", ");
                                if(parts.length >= 4 && parts[1].equals(username)){
                                        return parts; //timestamp, clientId/ownerId, fname, lname
                                }
                        }
                }catch(IOException exception){
                        //File does not exist yet. No one has registered.       
                }

                return null;
        }

}//End of class


