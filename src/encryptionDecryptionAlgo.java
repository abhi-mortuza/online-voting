import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class encryptionDecryptionAlgo {
	public BigInteger SAM(BigInteger number,BigInteger exponent,BigInteger modulus) {
		BigInteger base = (number.signum() < 0 || number.compareTo(modulus) >= 0 ? number.mod(modulus) : number);
		BigInteger result = base;
		char[] binArray = exponent.toString(2).substring(1).toCharArray();//Convert it in binary as a string for easy calculation
		//char[] itreableBin = Arrays.copyOfRange(binArray, 1, binArray.length);
		for(char exsingle: binArray) {
			result = (result.multiply(result)).mod(modulus);
			if(exsingle == '1') {
				result = (result.multiply(base)).mod(modulus);
			}
		}
		return result;
	}
	
	public String GenerateSHA(String password) {
		String hashValue = "";
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
	        hashValue = sb.toString();
	    } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
	    
	    return hashValue;
	}
	
	public String RSAEncrypt(String candidateId,BigInteger kpub,BigInteger n) {
		return SAM(new BigInteger(candidateId),kpub,n).toString();
	}
	
	public BigInteger RSAdecrypt(String message,BigInteger kpr, BigInteger n){
    	return SAM(new BigInteger(message),kpr,n);
    }
}