import java.sql.*;
import java.util.ArrayList;
import java.math.BigInteger;

@SuppressWarnings("unused")
public class PublicCloud {

	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	private boolean userValid = false;
	private boolean userVotedBefore = false;
	private String userName = "";
	private encryptionDecryptionAlgo encryptionDecryptionAlgo;
	//TODO Handle the authentication with the voter
	public PublicCloud(String userName,String userPassword) {
		//Make database connection first
		// TODO 
		try {
			// Setup the connection with the DB
			connect = DriverManager.getConnection("jdbc:mysql://localhost/online_voting?"+ "user=root&password=");
			encryptionDecryptionAlgo = new encryptionDecryptionAlgo();

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from online_voting.users WHERE `user_name`='"+userName+"'");
			while(resultSet.next()) {
				if(resultSet.getString("user_name").equals(userName) && resultSet.getString("password").equals(userPassword)) {
					//username and password matched
					//TODO: Username password matched so we will ask give user the candidate id's and public key for encryption
					this.userValid = true;
					//Checking user voted before or not
					//this.userVotedBefore = resultSet.getBoolean("user_voted");
					this.userName = resultSet.getString("user_name");
					//Checking user voted or not
				}
			}
			if(userValid == false) {
				//User name didnt matched
				System.err.println("You aren't authorized to vote in the system");
				System.exit(0);//We will exit immediately
			}
			ResultSet userVoted = statement.executeQuery("SELECT `hashed_user` FROM `online_voting`.`voting_table` WHERE `hashed_user` = '"+encryptionDecryptionAlgo.GenerateSHA(userName)+"'");
			if(userVoted.next()) {
				userVotedBefore = true;
			}else {
				userVotedBefore = false;
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
	
	public void setVotedcandidateId(BigInteger encryptedCandidateId,encryptionDecryptionAlgo encryptionDecryptionAlgo) {
		if(!userValid) return;
		if(userVotedBefore) { System.err.println("You voted before."); return; }
		BigInteger previousEncryptedTotalVoteValue = null;
		BigInteger currentEncryptedTotalVoteValue;
		try {
			resultSet = statement.executeQuery("SELECT `total_encrypted_vote` FROM `online_voting`.`voting_table` ORDER BY `ID` DESC LIMIT 1");
			//Formating the id
			if(resultSet.next()) {
				previousEncryptedTotalVoteValue = new BigInteger(resultSet.getString(1));
			}else {
				previousEncryptedTotalVoteValue = BigInteger.ONE;
			}
			currentEncryptedTotalVoteValue = previousEncryptedTotalVoteValue.multiply(encryptedCandidateId).mod(getprimen());
			//Updating this in the databases
			//PreparedStatement updateEXP = connect.prepareStatement("UPDATE `online_voting`.`parameter` SET `parameter_value` = '"+currentEncryptedTotalVoteValue+"' WHERE `parameter`.`parameter_name` = 'vote_condition';");
			//int updateEXP_done = updateEXP.executeUpdate();
			//TODO: update the current voting scenario in voting_table
			PreparedStatement updateVotingTable = connect.prepareStatement("INSERT INTO `online_voting`.`voting_table` (`encrypted_vote`,`hashed_user`,`total_encrypted_vote`) VALUES ('"+ currentEncryptedTotalVoteValue +"','"+encryptionDecryptionAlgo.GenerateSHA(this.userName)+"','"+currentEncryptedTotalVoteValue+"')");
			int insertingVoteInVotingTable = updateVotingTable.executeUpdate();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Your vote casted Successfully!");
	}
	public boolean wasMyVoteCounted(String userName,String encryptedCandidateId) {
		boolean voteCounted = false;
		String currentVote = null;
		String currentIndividualVote = null;
		BigInteger prevVote = null;
		int currentRowID = 0;
		int PreRowId = 0;
		try {
			ResultSet userVoted = statement.executeQuery("SELECT * FROM `online_voting`.`voting_table` WHERE `hashed_user` = '"+encryptionDecryptionAlgo.GenerateSHA(userName)+"'");
			while(userVoted.next()) {
				//Get this table id
				currentRowID = userVoted.getInt("ID");
				currentVote = userVoted.getString("total_encrypted_vote");
				currentIndividualVote = userVoted.getString("encrypted_vote");
				PreRowId = currentRowID - 1;
				System.out.println(currentIndividualVote);
				System.out.println(encryptedCandidateId);
				if(currentIndividualVote.equals(encryptedCandidateId)) {
					System.out.println("Your vote was counted now checking if it was modified or not.");
				}else {
					System.err.println("Your vote was modified and altered");
					voteCounted = false;
					return voteCounted;
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		try {
			ResultSet prevUserVoted = statement.executeQuery("SELECT * FROM `online_voting`.`voting_table` WHERE `ID` = '"+PreRowId+"'");
			while(prevUserVoted.next()) {
				prevVote = new BigInteger(prevUserVoted.getString("total_encrypted_vote"));
			}
			if(prevVote == null) {
				//No previous vote was casted
				//User user was first man to cast the vote
				//We will just check user user encrypted vote with the current vote
				if(encryptedCandidateId.equals(currentVote)) {
					System.out.println("Your vote was counted successfully");
					voteCounted = true;
				}else {
					System.out.println("Seems like your vote was altered");
					voteCounted = false;
				}
			}else {
				//There are previous vote that are casted
				if(prevVote.multiply(new BigInteger(encryptedCandidateId)).mod(getprimen()).toString().equals(currentVote)) {
					System.out.println("Your vote was counted Successfully");
					voteCounted = true;
				}else {
					System.out.println("Seems like your vote was altered");
					voteCounted = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return voteCounted;
	}
}
