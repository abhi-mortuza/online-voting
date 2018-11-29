import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

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
	
	private void DisplayResults() {
		System.out.println("Displaying the final result");
		BigInteger primeN = this.getVotingParameter("prime_n");
		BigInteger primeQ = this.getVotingParameter("prime_q");
		BigInteger primeP = this.getVotingParameter("prime_p");
		BigInteger phi = this.getVotingParameter("phi");
		BigInteger kpub = this.getVotingParameter("kpub");
		String encryptedVote = this.getVotingParameter("vote_condition").toString();
		ArrayList<BigInteger> candidateIds = getCandidateIds();
		HashMap<BigInteger,Integer> decIndiVoteResult = new HashMap<>();
		
		
		//Decrypting the encrypted vote first
		encryptionDecryptionAlgo enDeAlgo = new encryptionDecryptionAlgo();
		BigInteger decryptedVote = enDeAlgo.RSAdecrypt(encryptedVote , new BigInteger("27"), primeN);
		
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
			System.out.println(candiateId +" => " +decIndiVoteResult.get(candiateId));
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
