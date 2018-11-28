import java.sql.*;
import java.util.ArrayList;
import java.math.BigInteger;

@SuppressWarnings("unused")
public class PublicCloud {

	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	private boolean userValid = false;
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
				if(resultSet.getString("user_name").equals(userName) && resultSet.getString("password").equals(userPassword)) {
					//username and password matched
					//TODO: Username password mathced so we will ask give user the candidate id's and public key for encryption
					this.userValid = true;
				}else {
					//User name didnt matched
					System.err.println("You aren't authorized to vote in the system");
					System.exit(0);//We will exit immediately
				}

			}
		} catch(Exception e) {
			System.err.println(e);
		}
	}
	//If authentication match give voter the public key to encrypt candidate ID
	//Give voter the candidate ID's
	
	public BigInteger getKpub() {
		if(!userValid) return null;
		try {
			resultSet = statement.executeQuery("SELECT `parameter_value` FROM online_voting.parameter WHERE `parameter_name` = 'kpub'");
			while(resultSet.next()) {
				return new BigInteger(resultSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public BigInteger getprimen() {
		if(!userValid) return null;
		try {
			resultSet = statement.executeQuery("SELECT `parameter_value` FROM online_voting.parameter WHERE `parameter_name` = 'prime_n'");
			while(resultSet.next()) {
				return new BigInteger(resultSet.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<BigInteger> getCandidateIds() {
		if(!userValid) return null;
		ArrayList<BigInteger> candidatesId = new ArrayList<BigInteger>();
		//BigInteger[] candidateIds = new BigInteger[100];
		try {
			resultSet = statement.executeQuery("SELECT `candidate_id` FROM online_voting.candidates");
			while(resultSet.next()) {
				candidatesId.add(new BigInteger(resultSet.getString(1)));
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		return candidatesId;
	}
	public String getSingleCandiateInformation(BigInteger candidateId) {
		if(!userValid) return null;
		String return_data = "";
		try {
			resultSet = statement.executeQuery("SELECT * FROM online_voting.candidates WHERE candidate_id="+candidateId.intValue());
			//Formating the id
			while(resultSet.next()) {
				return_data += "[ "+resultSet.getString("candidate_id")+" ] "+resultSet.getString("c_first_name")+" "+resultSet.getString("c_last_name")+"\n";
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return return_data;
	}
	
	public void setVotedcandidateId(BigInteger encryptedCandidateId) {
		if(!userValid) return;
		BigInteger previousEncryptedTotalVoteValue = null;
		BigInteger currentEncryptedTotalVoteValue;
		try {
			resultSet = statement.executeQuery("SELECT `parameter_value` FROM online_voting.parameter WHERE parameter_name='vote_condition'");
			//Formating the id
			while(resultSet.next()) {
				previousEncryptedTotalVoteValue = new BigInteger(resultSet.getString(1));
			}
			currentEncryptedTotalVoteValue = previousEncryptedTotalVoteValue.multiply(encryptedCandidateId).mod(getprimen());
			//Updating this in the databases
			PreparedStatement updateEXP = connect.prepareStatement("UPDATE `online_voting`.`parameter` SET `parameter_value` = '"+currentEncryptedTotalVoteValue+"' WHERE `parameter`.`parameter_name` = 'vote_condition';");
			int updateEXP_done = updateEXP.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
