import java.math.BigInteger;

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
	
	public String RSAEncrypt(String candidateId,BigInteger kpub,BigInteger n) {
		return SAM(new BigInteger(candidateId),kpub,n).toString();
	}
	
	public String RSAdecrypt(String message,BigInteger kpr, BigInteger n){
    	return new String(SAM(new BigInteger(message),kpr,n).toByteArray());
    }
}
