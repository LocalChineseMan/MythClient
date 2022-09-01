package java.security.cert;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerValue;

public class PolicyQualifierInfo {
  private byte[] mEncoded;
  
  private String mId;
  
  private byte[] mData;
  
  private String pqiString;
  
  public PolicyQualifierInfo(byte[] paramArrayOfbyte) throws IOException {
    this.mEncoded = (byte[])paramArrayOfbyte.clone();
    DerValue derValue = new DerValue(this.mEncoded);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for PolicyQualifierInfo"); 
    this.mId = derValue.data.getDerValue().getOID().toString();
    byte[] arrayOfByte = derValue.data.toByteArray();
    if (arrayOfByte == null) {
      this.mData = null;
    } else {
      this.mData = new byte[arrayOfByte.length];
      System.arraycopy(arrayOfByte, 0, this.mData, 0, arrayOfByte.length);
    } 
  }
  
  public final String getPolicyQualifierId() {
    return this.mId;
  }
  
  public final byte[] getEncoded() {
    return (byte[])this.mEncoded.clone();
  }
  
  public final byte[] getPolicyQualifier() {
    return (this.mData == null) ? null : (byte[])this.mData.clone();
  }
  
  public String toString() {
    if (this.pqiString != null)
      return this.pqiString; 
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("PolicyQualifierInfo: [\n");
    stringBuffer.append("  qualifierID: " + this.mId + "\n");
    stringBuffer.append("  qualifier: " + ((this.mData == null) ? "null" : hexDumpEncoder
        .encodeBuffer(this.mData)) + "\n");
    stringBuffer.append("]");
    this.pqiString = stringBuffer.toString();
    return this.pqiString;
  }
}
