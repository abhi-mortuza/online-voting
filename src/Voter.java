import java.sql.*;
public class Voter {
	private static Connection connect;
	private static Statement statement;
	private static ResultSet resultSet;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			 //Class.forName("com.mysql.jdbc.Driver");
	         // Setup the connection with the DB
	         connect = DriverManager
	                 .getConnection("jdbc:mysql://localhost/online_voting?"
	                         + "user=admin&password=admin");

	         // Statements allow to issue SQL queries to the database
	         statement = connect.createStatement();
	         // Result set get the result of the SQL query
	         resultSet = statement
	                 .executeQuery("select * from online_voting.users");
	         while(resultSet.next()) {
	        	 System.out.println("we came here");
	        	System.out.println(resultSet.getString("last_name")); 
	        	 
	         }
		} catch(Exception e) {
			System.err.println(e);
		}

	}

}
