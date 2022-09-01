package sun.security.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandomSpi;

public final class SecureRandom extends SecureRandomSpi implements Serializable {
  private static final long serialVersionUID = 3581829991155417889L;
  
  private static final int DIGEST_SIZE = 20;
  
  private transient MessageDigest digest;
  
  private byte[] state;
  
  private byte[] remainder;
  
  private int remCount;
  
  public SecureRandom() {
    init(null);
  }
  
  private SecureRandom(byte[] paramArrayOfbyte) {
    init(paramArrayOfbyte);
  }
  
  private void init(byte[] paramArrayOfbyte) {
    try {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    } catch (NoSuchProviderException|NoSuchAlgorithmException noSuchProviderException) {
      try {
        this.digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
      } 
    } 
    if (paramArrayOfbyte != null)
      engineSetSeed(paramArrayOfbyte); 
  }
  
  public byte[] engineGenerateSeed(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    SeedGenerator.generateSeed(arrayOfByte);
    return arrayOfByte;
  }
  
  public synchronized void engineSetSeed(byte[] paramArrayOfbyte) {
    if (this.state != null) {
      this.digest.update(this.state);
      for (byte b = 0; b < this.state.length; b++)
        this.state[b] = 0; 
    } 
    this.state = this.digest.digest(paramArrayOfbyte);
  }
  
  private static void updateState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = 1;
    int j = 0;
    for (byte b = 0; b < paramArrayOfbyte1.length; b++) {
      int k = paramArrayOfbyte1[b] + paramArrayOfbyte2[b] + i;
      byte b1 = (byte)k;
      j |= (paramArrayOfbyte1[b] != b1) ? 1 : 0;
      paramArrayOfbyte1[b] = b1;
      i = k >> 8;
    } 
    if (j == 0)
      paramArrayOfbyte1[0] = (byte)(paramArrayOfbyte1[0] + 1); 
  }
  
  private static class SeederHolder {
    private static final SecureRandom seeder = new SecureRandom(SeedGenerator.getSystemEntropy());
    
    static {
      byte[] arrayOfByte = new byte[20];
      SeedGenerator.generateSeed(arrayOfByte);
      seeder.engineSetSeed(arrayOfByte);
    }
  }
  
  public synchronized void engineNextBytes(byte[] paramArrayOfbyte) {
    int i = 0;
    byte[] arrayOfByte = this.remainder;
    if (this.state == null) {
      byte[] arrayOfByte1 = new byte[20];
      SeederHolder.seeder.engineNextBytes(arrayOfByte1);
      this.state = this.digest.digest(arrayOfByte1);
    } 
    int j = this.remCount;
    if (j > 0) {
      int k = (paramArrayOfbyte.length - i < 20 - j) ? (paramArrayOfbyte.length - i) : (20 - j);
      for (byte b = 0; b < k; b++) {
        paramArrayOfbyte[b] = arrayOfByte[j];
        arrayOfByte[j++] = 0;
      } 
      this.remCount += k;
      i += k;
    } 
    while (i < paramArrayOfbyte.length) {
      this.digest.update(this.state);
      arrayOfByte = this.digest.digest();
      updateState(this.state, arrayOfByte);
      byte b1 = (paramArrayOfbyte.length - i > 20) ? 20 : (paramArrayOfbyte.length - i);
      for (byte b2 = 0; b2 < b1; b2++) {
        paramArrayOfbyte[i++] = arrayOfByte[b2];
        arrayOfByte[b2] = 0;
      } 
      this.remCount += b1;
    } 
    this.remainder = arrayOfByte;
    this.remCount %= 20;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.digest = MessageDigest.getInstance("SHA", "SUN");
    } catch (NoSuchProviderException|NoSuchAlgorithmException noSuchProviderException) {
      try {
        this.digest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
      } 
    } 
  }
}
