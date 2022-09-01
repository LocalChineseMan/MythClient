package sun.security.x509;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KeyIdentifier {
  private byte[] octetString;
  
  public KeyIdentifier(byte[] paramArrayOfbyte) {
    this.octetString = (byte[])paramArrayOfbyte.clone();
  }
  
  public KeyIdentifier(DerValue paramDerValue) throws IOException {
    this.octetString = paramDerValue.getOctetString();
  }
  
  public KeyIdentifier(PublicKey paramPublicKey) throws IOException {
    DerValue derValue = new DerValue(paramPublicKey.getEncoded());
    if (derValue.tag != 48)
      throw new IOException("PublicKey value is not a valid X.509 public key"); 
    AlgorithmId algorithmId = AlgorithmId.parse(derValue.data.getDerValue());
    byte[] arrayOfByte = derValue.data.getUnalignedBitString().toByteArray();
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new IOException("SHA1 not supported");
    } 
    messageDigest.update(arrayOfByte);
    this.octetString = messageDigest.digest();
  }
  
  public byte[] getIdentifier() {
    return (byte[])this.octetString.clone();
  }
  
  public String toString() {
    String str = "KeyIdentifier [\n";
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    str = str + hexDumpEncoder.encodeBuffer(this.octetString);
    str = str + "]\n";
    return str;
  }
  
  void encode(DerOutputStream paramDerOutputStream) throws IOException {
    paramDerOutputStream.putOctetString(this.octetString);
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.octetString.length; b++)
      i += this.octetString[b] * b; 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KeyIdentifier))
      return false; 
    return Arrays.equals(this.octetString, ((KeyIdentifier)paramObject)
        .getIdentifier());
  }
}
