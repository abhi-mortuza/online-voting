import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

@SuppressWarnings("unused")
public class InitializeVotingSystem {
	//Generating the voting parameter
	private BigInteger p;
	private BigInteger q;
	private BigInteger n;
	private BigInteger phi;
	private BigInteger e;
	private BigInteger d;
	private Random r;
	private int bitlength = 2048;
	private encryptionDecryptionAlgo encryptionDecryptionAlgo;
	
	private Connection connect;
	private Statement statement;
	private ResultSet resultSet;
	private Scanner scan;
	private boolean userValid = false;
	
	
	public InitializeVotingSystem() {
		try {
			connect = DriverManager.getConnection("jdbc:mysql://localhost/online_voting?"+ "user=root&password=");

			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			scan = new Scanner(System.in);
			encryptionDecryptionAlgo = new encryptionDecryptionAlgo();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setAdmin() {
		//We will set up the admin here
		System.out.println("=======================================");
		System.out.println("============ Admin SetUp ==============");
		System.out.println("=======================================");
		addUser(true);
	}
	public void setVoter() {
		//We will set up the admin here
		System.out.println("=======================================");
		System.out.println("============ Voter SetUp ==============");
		System.out.println("=======================================");
		addUser(false);
	}
	public void setCandiate() {
		System.out.println("=======================================");
		System.out.println("========== Candidate SetUp ============");
		System.out.println("=======================================");
		String FirstName,LastName,email,CandiateId,userInput;
		do {
			System.out.print("Enter First name: ");
			FirstName = scan.nextLine();
			System.out.print("Enter Last name: ");
			LastName = scan.nextLine();
			System.out.print("Enter candiate email: ");
			email = scan.nextLine();
			System.out.print("Enter candidate ID: ");
			CandiateId = scan.nextLine();
			
			try {
				PreparedStatement insertUserinTable = connect.prepareStatement("INSERT INTO `online_voting`.`candidates` (`c_first_name`,`c_last_name`,`c_email`,`candidate_id`) VALUES ('"+ FirstName +"','"+LastName+"','"+email+"','"+CandiateId+"')");
				int insertUser = insertUserinTable.executeUpdate();
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.print("Add more Candidate?(y/n): ");
			userInput = scan.nextLine();
			
		}while(userInput.toLowerCase().equals("y"));
	}
	
	private void addUser(boolean isAdmin) {
		String userInput = "";
		String FirstName;
		String LastName;
		String userName;
		String Password;
		int adminValue;
		if(isAdmin) {
			adminValue = 1;
		}else {
			adminValue = 0;
		}
		do {
			System.out.print("Enter First name: ");
			FirstName = scan.nextLine();
			System.out.print("Enter Last name: ");
			LastName = scan.nextLine();
			System.out.print("Enter user name: ");
			userName = scan.nextLine();
			System.out.print("Enter Password name: ");
			Password = encryptionDecryptionAlgo.GenerateSHA(scan.nextLine());
			
			try {
				PreparedStatement insertUserinTable = connect.prepareStatement("INSERT INTO `online_voting`.`users` (`first_name`,`last_name`,`password`,`user_name`,`is_admin`) VALUES ('"+ FirstName +"','"+LastName+"','"+Password+"','"+userName+"','"+adminValue+"')");
				int insertUser = insertUserinTable.executeUpdate();
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.print("Add more?(y/n): ");
			userInput = scan.nextLine();
			
		}while(userInput.toLowerCase().equals("y"));
	}
	
	private void setParameter() {
		System.out.println("==================================================");
		System.out.println("========== Voting setup Initializtion ============");
		System.out.println("==================================================");
		
		r = new Random();
        p = new BigInteger(bitlength,40, r);
        q = new BigInteger(bitlength,40, r);
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger(bitlength/2,40, r);
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0){
            e.add(BigInteger.ONE);
        }
        d = e.modInverse(phi);
        
        System.out.println("P -> "+p);
        System.out.println("a -> "+q);
        System.out.println("n -> "+n);
        System.out.println("phi -> "+phi);
        System.out.println("e -> "+e);
        System.out.println("d -> ********************************************************");
        
        try {
        	connect.prepareStatement("UPDATE `online_voting`.`parameter` SET parameter_value='"+e+"' WHERE `parameter_name`='kpub'").executeUpdate();
        	connect.prepareStatement("UPDATE `online_voting`.`parameter` SET parameter_value='"+n+"' WHERE `parameter_name`='prime_n'").executeUpdate();
			//Storing the private key in the files
			try {
				FileWriter fstream = new FileWriter("private.key");
				FileWriter pFile = new FileWriter("p.key");
				FileWriter qFile = new FileWriter("q.key");
				FileWriter phiFile = new FileWriter("phi.key");
		        BufferedWriter out = new BufferedWriter(fstream);
		        BufferedWriter pOut = new BufferedWriter(pFile);
		        BufferedWriter qOut = new BufferedWriter(qFile);
		        BufferedWriter phiOut = new BufferedWriter(phiFile);
		        
		        out.write(d.toString());
		        pOut.write(p.toString());
		        qOut.write(q.toString());
		        phiOut.write(phi.toString());
		        //Close the output stream
		    	out.close();
		    	pOut.close();
		    	qOut.close();
		    	phiOut.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	public static void main(String args[]) {
		//TODO: Set the admin first
		InitializeVotingSystem initVoteSys = new InitializeVotingSystem();
		initVoteSys.setParameter();
		initVoteSys.setAdmin();
		initVoteSys.setCandiate();
		initVoteSys.setVoter();
		System.out.println("==================================================");
		System.out.println("========== Voting setup Finished =================");
		System.out.println("==================================================");
	}
}
