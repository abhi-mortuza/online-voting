import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

@SuppressWarnings("unused")
public class PrivateCloud {
	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	private boolean userValid = false;
	
	public PrivateCloud() {
		try {
			connect = DriverManager.getConnection("jdbc:mysql://localhost/online_voting?"+ "user=root&password=");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<BigInteger> getCandidateIds(){
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
	
	private String getCandidateInformationFromID(String candidateId) {
		String returnData = "";
		try {
			resultSet = statement.executeQuery("SELECT * FROM online_voting.candidates WHERE `candidate_id`="+Integer.parseInt(candidateId));
			while(resultSet.next()) {
				returnData += "[ "+ candidateId +" ] "+resultSet.getString("c_first_name")+" "+ resultSet.getString("c_last_name");
			}
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		return returnData;
	}
	
	private void voteAlteredOrNot() {
		BigInteger indiviudalVote;
		BigInteger toalVote;
		BigInteger preTotalVote = BigInteger.ONE;
		try {
			resultSet = statement.executeQuery("SELECT * FROM `online_voting`.`voting_table` ORDER BY `ID` ASC");
			while(resultSet.next()) {
				if(!preTotalVote.multiply(new BigInteger(resultSet.getString("encrypted_vote"))).toString().equals(resultSet.getString("total_encrypted_vote"))) {
					System.err.println("Vote was altered");
					System.exit(0);
				}
			}
		}catch(Exception e) {
			
		}
	}
	
	private void DisplayResults() {
		voteAlteredOrNot();
		System.out.println("==================================================");
		System.out.println("============ Displaying final Vote ===============");
		System.out.println("==================================================");
		BigInteger primeN = this.getVotingParameter("prime_n");
		BigInteger primeQ = this.getVotingParameter("prime_q");
		BigInteger primeP = this.getVotingParameter("prime_p");
		BigInteger phi = this.getVotingParameter("phi");
		BigInteger kpub = this.getVotingParameter("kpub");
		BigInteger privateKey = null;
		String encryptedVote = null;
		
		ArrayList<BigInteger> candidateIds = getCandidateIds();
		HashMap<BigInteger,Integer> decIndiVoteResult = new HashMap<>();
		
		//Getting the private key
		File pkeyFile = new File("private.key");
		
		
		try {
			Scanner sc = new Scanner(pkeyFile);
			privateKey = new BigInteger(sc.nextLine());
			sc.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			resultSet = statement.executeQuery("SELECT `total_encrypted_vote` FROM `online_voting`.`voting_table` ORDER BY `ID` DESC LIMIT 1");
			//Formating the id
			if(resultSet.next()) {
				encryptedVote = resultSet.getString(1);
			}else {
				encryptedVote = "1";
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		//Decrypting the encrypted vote first
		encryptionDecryptionAlgo enDeAlgo = new encryptionDecryptionAlgo();
		BigInteger decryptedVote = enDeAlgo.RSAdecrypt(encryptedVote , privateKey, primeN);
		//while(decryptedVote != BigInteger.ONE) {
			for(BigInteger candiateId: candidateIds) {
				boolean continueCalculating = true;
				int currentVoteForThisCandidate = 0;
				while(continueCalculating) {
					if(decryptedVote.mod(candiateId) == BigInteger.ZERO) {
						continueCalculating = true;
						//This candidate gets a vote
						currentVoteForThisCandidate++;
						decryptedVote = decryptedVote.divide(candiateId);
					}else {
						continueCalculating = false;
					}
				}
				decIndiVoteResult.put(candiateId, currentVoteForThisCandidate);
			}
		//}
		
		//Displaying the result
		for(BigInteger candiateId: decIndiVoteResult.keySet()) {
			System.out.println(this.getCandidateInformationFromID(candiateId.toString()) +" => " +decIndiVoteResult.get(candiateId));
		}
	}
	
	private BigInteger getVotingParameter(String parameterName) {
		BigInteger parameterValue = null;
		try {
			resultSet = statement.executeQuery("select `parameter_value` from online_voting.parameter WHERE `parameter_name`='"+parameterName+"'");
			while(resultSet.next()) {
				parameterValue = new BigInteger(resultSet.getString(1));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return parameterValue;
	}
	
	public static void main(String args[]) {
		PrivateCloud privateCloud = new PrivateCloud();
		//Getting the encryption parameter
		privateCloud.DisplayResults();
	}
	
	
	
}
