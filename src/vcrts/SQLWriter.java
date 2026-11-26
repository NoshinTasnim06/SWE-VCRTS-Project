package vcrts;

import java.sql.*;

public class SQLWriter {
	
	static Connection connection;
	static Statement statement;
	private static String url;
	private static String user = "";
	private static String password = "";
	
	public SQLWriter()
	{
		try 
		{
			this.connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
		}
		catch (SQLException e)
		{
			System.out.println("Error:" + e.getMessage());
		}
	}
	
	public void insert(String type, String... fields)
	{
		StringBuilder data = new StringBuilder();
		String sql = "";
		for (String field : fields)
			try
			{
				
				Integer.parseInt(field);
				data.append(field).append(", ");
			}
			catch (NumberFormatException e)
			{
				data.append("'").append(field).append("'").append(", "); //formats strings
			}
		data.delete(data.length() - 2, data.length()); //remove extra comma
		
		if (type.equals("owner"))
			sql = "insert into owner ()";
		else if (type.equals("client"))
			sql = "insert into client ()";
		else if (type.equals("vehicle"))
			sql = "insert into vehicle ()";
		else
			sql = "insert into job ()";
		
		sql += "values (" + data + ")";
		executeStatement(sql);
	}
	
	public void delete() {
		
	}
	
	public void setVehicleTime(String id, int hours)
	{
		String sql = "";
		sql = "update vehicle set residency_time = " + hours + " where vehicleID = '" + id + "'";
		executeStatement(sql);
	}
	
	public void setVehicleStatus(String id, boolean available)
	{
		String sql = "";
		String status = "";
		if (available)
			status = "'avaiable'";
		else
			status = "'not'";
		sql = "update vehicle set vehicle_status = " + status + " where vehicleID = '" + id + "'";
		executeStatement(sql);
	}
	
	public void setJobStatus(String id, String status)
	{
		String sql = "";
		sql = "update job set job_status = '" + status + "' where jobID = '" + id + "'"; 
		executeStatement(sql);
	}
	
	public void executeStatement(String sql)
	{
		try {
			statement.execute(sql);
		}
		catch (SQLException e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void close()
	{
		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.getMessage();
		}
	}

}
