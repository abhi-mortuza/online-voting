
import java.util.Scanner;
import java.math.BigInteger;

public class Voter {
	public static void main(String[] args) {
		Scanner getInput = new Scanner(System.in);
		// TODO Ask user about username and password.
		System.out.println("Enter your Username: ");
		String userName = getInput.nextLine();
		System.out.println("Enter the password: ");
		String userPassword = getInput.nextLine();
		GenerateSHA sha256 = new GenerateSHA(userPassword);
		System.out.println(sha256.getSHAHash());
		
		// TODO Match the username and password with the databases.
		
		PublicCloud publicCloud = new PublicCloud(userName,sha256.getSHAHash());
		// TODO it matches then give the voter public key Kpub and the candidate id's
		// TODO Let the voter chose among the Among the candidate using their id
		// TODO Encrypt the candidate ID
		// TODO Sent the encrypted ID to the public cloud
		// TODO Mark voter as voted.
		// TODO close the connection.
		
		
		getInput.close();

	}

}
