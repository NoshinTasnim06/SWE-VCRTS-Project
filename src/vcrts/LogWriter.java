package vcrts;

import java.io.*;

public class LogWriter {

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

    /* METHOD: logOwner(String ownerId, String firstName, String lastName)
     * Make public static. Format owner info into comma-separated text and pass to writeToFile().
     */

    /* METHOD: logVehicle(String ownerId, String make, String brand, String year, String license, String residencyTime)
     * Make public static. Format vehicle info into comma-separated text and pass to writeToFile().
     */

    /* METHOD: logJob(String clientId, String jobName, String duration, String deadline)
     * Make public static. Format job info into comma-separated text and pass to writeToFile().
     */

    /* METHOD: writeToFile(String filename, String data)
     * Make public static. Append string line to specified file using FileWriter and PrintWriter.
     */

}