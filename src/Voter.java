
import java.util.ArrayList;
import java.util.Scanner;

import java.math.BigInteger;

//@SuppressWarnings("unused")
public class Voter {
	
	public static void main(String[] args) {
		Scanner getInput = new Scanner(System.in);
		encryptionDecryptionAlgo encryptionDecryptionAlgo = new encryptionDecryptionAlgo();
		// TODO Ask user about username and password.
		System.out.println("Enter your Username: ");
		String userName = getInput.nextLine();
		System.out.println("Enter the password: ");
		String userPassword = getInput.nextLine();
		
		// TODO Match the username and password with the databases.
		
		PublicCloud publicCloud = new PublicCloud(userName,encryptionDecryptionAlgo.GenerateSHA(userPassword));
		
		// TODO it matches then give the voter public key Kpub and the candidate id's
		//We will ask for kpub now
		BigInteger kpub = publicCloud.getKpub();
		//System.out.println(encryptionDecryptionAlgo.RSAEncrypt("5",kpub,publicCloud.getprimen()));
		//Asking user if they want to check their vote or cast their vote
		System.out.println("Coose option from the below:");
		System.out.println("[1] You want to check your vote?");
		System.out.println("[2] You want to cast your vote");
		String userChoice = getInput.nextLine();
		if(userChoice.equals("1")) {
			//User want to check his/her vote
			ArrayList<BigInteger> candidateIds = publicCloud.getCandidateIds();
			if(candidateIds == null) {
				System.exit(0);
			}
			for(BigInteger c_id:candidateIds) {
				System.out.println(publicCloud.getSingleCandiateInformation(c_id));
			}
			// TODO Let the voter chose among the Among the candidate using their id
			//Let the user choose
			System.out.print("Whom did you vote before?");
			String selectedCandidate = getInput.nextLine().trim();
			
			if(!candidateIds.contains(new BigInteger(selectedCandidate))) {
				System.err.println("You didn't select the correct candidate id.");
				System.exit(0);
			}
			String encryptedCandidate = encryptionDecryptionAlgo.RSAEncrypt(selectedCandidate,kpub,publicCloud.getprimen());
			publicCloud.wasMyVoteCounted(userName, encryptedCandidate);
		}else {
			//User want to cast vote
			//Ask for the Candidate ID
			ArrayList<BigInteger> candidateIds = publicCloud.getCandidateIds();
			if(candidateIds == null) {
				System.exit(0);
			}
			for(BigInteger c_id:candidateIds) {
				System.out.println(publicCloud.getSingleCandiateInformation(c_id));
			}
			// TODO Let the voter chose among the Among the candidate using their id
			//Let the user choose
			System.out.print("Choose your candiate. Input the candidate id: ");
			String selectedCandidate = getInput.nextLine().trim();
			
			if(!candidateIds.contains(new BigInteger(selectedCandidate))) {
				System.err.println("You didn't select the correct candidate id.");
				System.exit(0);
			}
			// TODO Encrypt the candidate ID using RSA
			//Encrypting the selected candidate
			
			String encryptedCandidate = encryptionDecryptionAlgo.RSAEncrypt(selectedCandidate,kpub,publicCloud.getprimen());
			// TODO Sent the encrypted ID to the public cloud
			publicCloud.setVotedcandidateId(new BigInteger(encryptedCandidate),encryptionDecryptionAlgo);
		}
		
		
		
		// TODO Mark voter as voted.
		// TODO close the connection.
		
		getInput.close();
		
	}

}
