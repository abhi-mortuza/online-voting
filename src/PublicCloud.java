import java.sql.*;
public class PublicCloud {

	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	//TODO Handle the authentication with the voter
	public PublicCloud(String userName,String userPassword) {
		//Make database connection first
		// TODO 
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost/online_voting?"+ "user=root&password=");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from online_voting.users");
			while(resultSet.next()) {
				System.out.println(resultSet.getString("user_name")); 

			}
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	//If authentication match give voter the public key to encrypt candidate ID
	//Give voter the candidate ID's

}
