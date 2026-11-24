package vcrts;

import java.sql.*;

public class SQLWriter {
	
	static Connection connection;
	static Statement statement;
	private static String url;
	private static String user = "";
	private static String password = "";
	
	public SQLWriter() {
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
	
	public void insert(String tableName, String... fields)
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
		
		if (tableName.equals("vehicle"))
			sql = "insert into vehicle () values (" + data + ")";
		
		try
		{
			statement.executeUpdate(sql);
		}
		catch (SQLException e)
		{
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	public void delete() {
		
	}
	
	public void setStatus() {
		
	}
	
	public void close() {
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
