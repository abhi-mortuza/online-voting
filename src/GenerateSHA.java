import java.security.*;

public class GenerateSHA {
	private String hashValue;
	public GenerateSHA(String password) {
	    try {
	        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
	        String salt = "asdowesadddf";
	        String passWithSalt = password + salt;
	        byte[] passBytes = passWithSalt.getBytes();
	        byte[] passHash = sha256.digest(passBytes);             
	        StringBuilder sb = new StringBuilder();
	        for(int i=0; i< passHash.length ;i++) {
	            sb.append(Integer.toString((passHash[i] & 0xff) + 0x100, 16).substring(1));         
	        }
	        this.hashValue = sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }     
	}
	public String getSHAHash() {
		return hashValue;
	}
}
