
import java.util.ArrayList;
import java.util.Scanner;

import java.math.BigInteger;

//@SuppressWarnings("unused")
public class Voter {
	public static void main(String[] args) {
		Scanner getInput = new Scanner(System.in);
		// TODO Ask user about username and password.
		System.out.println("Enter your Username: ");
		String userName = getInput.nextLine();
		System.out.println("Enter the password: ");
		String userPassword = getInput.nextLine();
		GenerateSHA sha256 = new GenerateSHA(userPassword);
		
		// TODO Match the username and password with the databases.
		
		PublicCloud publicCloud = new PublicCloud(userName,sha256.getSHAHash());
		// TODO it matches then give the voter public key Kpub and the candidate id's
		//We will ask for kpub now
		BigInteger kpub = publicCloud.getKpub();
		//Ask for the Candidate ID
		ArrayList<BigInteger> candiateIds = publicCloud.getCandidateIds();
		for(BigInteger c_id:candiateIds) {
			System.out.println(publicCloud.getSingleCandiateInformation(c_id));
		}
		// TODO Let the voter chose among the Among the candidate using their id
		//Let the user choose
		System.out.println("Choose your candiate. Input the user id: ");
		String selectedCandidate = getInput.nextLine();
		// TODO Encrypt the candidate ID using RSA
		//Encrypting the selected candidate
		encryptionDecryptionAlgo encryptionDecryptionAlgo = new encryptionDecryptionAlgo();
		String encryptedCandidate = encryptionDecryptionAlgo.RSAEncrypt(selectedCandidate,kpub,publicCloud.getprimen());
		System.out.println(encryptedCandidate);
		// TODO Sent the encrypted ID to the public cloud
		publicCloud.setVotedcandidateId(new BigInteger(encryptedCandidate));
		// TODO Mark voter as voted.
		// TODO close the connection.
		
		
		getInput.close();
		
	}

}
