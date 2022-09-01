package sun.security.provider;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import sun.misc.IOUtils;
import sun.security.pkcs.EncryptedPrivateKeyInfo;

abstract class JavaKeyStore extends KeyStoreSpi {
  private static final int MAGIC = -17957139;
  
  private static final int VERSION_1 = 1;
  
  private static final int VERSION_2 = 2;
  
  private final Hashtable<String, Object> entries;
  
  public static final class JKS extends JavaKeyStore {
    String convertAlias(String param1String) {
      return param1String.toLowerCase(Locale.ENGLISH);
    }
  }
  
  public static final class JavaKeyStore {}
  
  private static class JavaKeyStore {}
  
  private static class TrustedCertEntry {
    Date date;
    
    Certificate cert;
    
    private TrustedCertEntry() {}
  }
  
  JavaKeyStore() {
    this.entries = new Hashtable<>();
  }
  
  abstract String convertAlias(String paramString);
  
  public Key engineGetKey(String paramString, char[] paramArrayOfchar) throws NoSuchAlgorithmException, UnrecoverableKeyException {
    EncryptedPrivateKeyInfo encryptedPrivateKeyInfo;
    Object object = this.entries.get(convertAlias(paramString));
    if (object == null || !(object instanceof KeyEntry))
      return null; 
    if (paramArrayOfchar == null)
      throw new UnrecoverableKeyException("Password must not be null"); 
    KeyProtector keyProtector = new KeyProtector(paramArrayOfchar);
    byte[] arrayOfByte = ((KeyEntry)object).protectedPrivKey;
    try {
      encryptedPrivateKeyInfo = new EncryptedPrivateKeyInfo(arrayOfByte);
    } catch (IOException iOException) {
      throw new UnrecoverableKeyException("Private key not stored as PKCS #8 EncryptedPrivateKeyInfo");
    } 
    return keyProtector.recover(encryptedPrivateKeyInfo);
  }
  
  public Certificate[] engineGetCertificateChain(String paramString) {
    Object object = this.entries.get(convertAlias(paramString));
    if (object != null && object instanceof KeyEntry) {
      if (((KeyEntry)object).chain == null)
        return null; 
      return (Certificate[])((KeyEntry)object).chain.clone();
    } 
    return null;
  }
  
  public Certificate engineGetCertificate(String paramString) {
    Object object = this.entries.get(convertAlias(paramString));
    if (object != null) {
      if (object instanceof TrustedCertEntry)
        return ((TrustedCertEntry)object).cert; 
      if (((KeyEntry)object).chain == null)
        return null; 
      return ((KeyEntry)object).chain[0];
    } 
    return null;
  }
  
  public Date engineGetCreationDate(String paramString) {
    Object object = this.entries.get(convertAlias(paramString));
    if (object != null) {
      if (object instanceof TrustedCertEntry)
        return new Date(((TrustedCertEntry)object).date.getTime()); 
      return new Date(((KeyEntry)object).date.getTime());
    } 
    return null;
  }
  
  public void engineSetKeyEntry(String paramString, Key paramKey, char[] paramArrayOfchar, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    KeyProtector keyProtector = null;
    if (!(paramKey instanceof java.security.PrivateKey))
      throw new KeyStoreException("Cannot store non-PrivateKeys"); 
    try {
      synchronized (this.entries) {
        KeyEntry keyEntry = new KeyEntry(null);
        keyEntry.date = new Date();
        keyProtector = new KeyProtector(paramArrayOfchar);
        keyEntry.protectedPrivKey = keyProtector.protect(paramKey);
        if (paramArrayOfCertificate != null && paramArrayOfCertificate.length != 0) {
          keyEntry.chain = (Certificate[])paramArrayOfCertificate.clone();
        } else {
          keyEntry.chain = null;
        } 
        this.entries.put(convertAlias(paramString), keyEntry);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new KeyStoreException("Key protection algorithm not found");
    } finally {
      keyProtector = null;
    } 
  }
  
  public void engineSetKeyEntry(String paramString, byte[] paramArrayOfbyte, Certificate[] paramArrayOfCertificate) throws KeyStoreException {
    synchronized (this.entries) {
      try {
        new EncryptedPrivateKeyInfo(paramArrayOfbyte);
      } catch (IOException iOException) {
        throw new KeyStoreException("key is not encoded as EncryptedPrivateKeyInfo");
      } 
      KeyEntry keyEntry = new KeyEntry(null);
      keyEntry.date = new Date();
      keyEntry.protectedPrivKey = (byte[])paramArrayOfbyte.clone();
      if (paramArrayOfCertificate != null && paramArrayOfCertificate.length != 0) {
        keyEntry.chain = (Certificate[])paramArrayOfCertificate.clone();
      } else {
        keyEntry.chain = null;
      } 
      this.entries.put(convertAlias(paramString), keyEntry);
    } 
  }
  
  public void engineSetCertificateEntry(String paramString, Certificate paramCertificate) throws KeyStoreException {
    synchronized (this.entries) {
      Object object = this.entries.get(convertAlias(paramString));
      if (object != null && object instanceof KeyEntry)
        throw new KeyStoreException("Cannot overwrite own certificate"); 
      TrustedCertEntry trustedCertEntry = new TrustedCertEntry();
      trustedCertEntry.cert = paramCertificate;
      trustedCertEntry.date = new Date();
      this.entries.put(convertAlias(paramString), trustedCertEntry);
    } 
  }
  
  public void engineDeleteEntry(String paramString) throws KeyStoreException {
    synchronized (this.entries) {
      this.entries.remove(convertAlias(paramString));
    } 
  }
  
  public Enumeration<String> engineAliases() {
    return this.entries.keys();
  }
  
  public boolean engineContainsAlias(String paramString) {
    return this.entries.containsKey(convertAlias(paramString));
  }
  
  public int engineSize() {
    return this.entries.size();
  }
  
  public boolean engineIsKeyEntry(String paramString) {
    Object object = this.entries.get(convertAlias(paramString));
    if (object != null && object instanceof KeyEntry)
      return true; 
    return false;
  }
  
  public boolean engineIsCertificateEntry(String paramString) {
    Object object = this.entries.get(convertAlias(paramString));
    if (object != null && object instanceof TrustedCertEntry)
      return true; 
    return false;
  }
  
  public String engineGetCertificateAlias(Certificate paramCertificate) {
    for (Enumeration<String> enumeration = this.entries.keys(); enumeration.hasMoreElements(); ) {
      Certificate certificate;
      String str = enumeration.nextElement();
      Object object = this.entries.get(str);
      if (object instanceof TrustedCertEntry) {
        certificate = ((TrustedCertEntry)object).cert;
      } else if (((KeyEntry)object).chain != null) {
        certificate = ((KeyEntry)object).chain[0];
      } else {
        continue;
      } 
      if (certificate.equals(paramCertificate))
        return str; 
    } 
    return null;
  }
  
  public void engineStore(OutputStream paramOutputStream, char[] paramArrayOfchar) throws IOException, NoSuchAlgorithmException, CertificateException {
    synchronized (this.entries) {
      if (paramArrayOfchar == null)
        throw new IllegalArgumentException("password can't be null"); 
      MessageDigest messageDigest = getPreKeyedHash(paramArrayOfchar);
      DataOutputStream dataOutputStream = new DataOutputStream(new DigestOutputStream(paramOutputStream, messageDigest));
      dataOutputStream.writeInt(-17957139);
      dataOutputStream.writeInt(2);
      dataOutputStream.writeInt(this.entries.size());
      for (Enumeration<String> enumeration = this.entries.keys(); enumeration.hasMoreElements(); ) {
        String str = enumeration.nextElement();
        Object object = this.entries.get(str);
        if (object instanceof KeyEntry) {
          int i;
          dataOutputStream.writeInt(1);
          dataOutputStream.writeUTF(str);
          dataOutputStream.writeLong(((KeyEntry)object).date.getTime());
          dataOutputStream.writeInt(((KeyEntry)object).protectedPrivKey.length);
          dataOutputStream.write(((KeyEntry)object).protectedPrivKey);
          if (((KeyEntry)object).chain == null) {
            i = 0;
          } else {
            i = ((KeyEntry)object).chain.length;
          } 
          dataOutputStream.writeInt(i);
          for (byte b = 0; b < i; b++) {
            byte[] arrayOfByte2 = ((KeyEntry)object).chain[b].getEncoded();
            dataOutputStream.writeUTF(((KeyEntry)object).chain[b].getType());
            dataOutputStream.writeInt(arrayOfByte2.length);
            dataOutputStream.write(arrayOfByte2);
          } 
          continue;
        } 
        dataOutputStream.writeInt(2);
        dataOutputStream.writeUTF(str);
        dataOutputStream.writeLong(((TrustedCertEntry)object).date.getTime());
        byte[] arrayOfByte1 = ((TrustedCertEntry)object).cert.getEncoded();
        dataOutputStream.writeUTF(((TrustedCertEntry)object).cert.getType());
        dataOutputStream.writeInt(arrayOfByte1.length);
        dataOutputStream.write(arrayOfByte1);
      } 
      byte[] arrayOfByte = messageDigest.digest();
      dataOutputStream.write(arrayOfByte);
      dataOutputStream.flush();
    } 
  }
  
  public void engineLoad(InputStream paramInputStream, char[] paramArrayOfchar) throws IOException, NoSuchAlgorithmException, CertificateException {
    synchronized (this.entries) {
      DataInputStream dataInputStream;
      MessageDigest messageDigest = null;
      CertificateFactory certificateFactory = null;
      Hashtable<Object, Object> hashtable = null;
      ByteArrayInputStream byteArrayInputStream = null;
      byte[] arrayOfByte = null;
      if (paramInputStream == null)
        return; 
      if (paramArrayOfchar != null) {
        messageDigest = getPreKeyedHash(paramArrayOfchar);
        dataInputStream = new DataInputStream(new DigestInputStream(paramInputStream, messageDigest));
      } else {
        dataInputStream = new DataInputStream(paramInputStream);
      } 
      int i = dataInputStream.readInt();
      int j = dataInputStream.readInt();
      if (i != -17957139 || (j != 1 && j != 2))
        throw new IOException("Invalid keystore format"); 
      if (j == 1) {
        certificateFactory = CertificateFactory.getInstance("X509");
      } else {
        hashtable = new Hashtable<>(3);
      } 
      this.entries.clear();
      int k = dataInputStream.readInt();
      for (byte b = 0; b < k; b++) {
        int m = dataInputStream.readInt();
        if (m == 1) {
          KeyEntry keyEntry = new KeyEntry(null);
          String str = dataInputStream.readUTF();
          keyEntry.date = new Date(dataInputStream.readLong());
          keyEntry
            .protectedPrivKey = IOUtils.readFully(dataInputStream, dataInputStream.readInt(), true);
          int n = dataInputStream.readInt();
          if (n > 0) {
            ArrayList<Certificate> arrayList = new ArrayList((n > 10) ? 10 : n);
            for (byte b1 = 0; b1 < n; b1++) {
              if (j == 2) {
                String str1 = dataInputStream.readUTF();
                if (hashtable.containsKey(str1)) {
                  certificateFactory = (CertificateFactory)hashtable.get(str1);
                } else {
                  certificateFactory = CertificateFactory.getInstance(str1);
                  hashtable.put(str1, certificateFactory);
                } 
              } 
              arrayOfByte = IOUtils.readFully(dataInputStream, dataInputStream.readInt(), true);
              byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
              arrayList.add(certificateFactory.generateCertificate(byteArrayInputStream));
              byteArrayInputStream.close();
            } 
            keyEntry.chain = arrayList.<Certificate>toArray(new Certificate[n]);
          } 
          this.entries.put(str, keyEntry);
        } else if (m == 2) {
          TrustedCertEntry trustedCertEntry = new TrustedCertEntry();
          String str = dataInputStream.readUTF();
          trustedCertEntry.date = new Date(dataInputStream.readLong());
          if (j == 2) {
            String str1 = dataInputStream.readUTF();
            if (hashtable.containsKey(str1)) {
              certificateFactory = (CertificateFactory)hashtable.get(str1);
            } else {
              certificateFactory = CertificateFactory.getInstance(str1);
              hashtable.put(str1, certificateFactory);
            } 
          } 
          arrayOfByte = IOUtils.readFully(dataInputStream, dataInputStream.readInt(), true);
          byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
          trustedCertEntry.cert = certificateFactory.generateCertificate(byteArrayInputStream);
          byteArrayInputStream.close();
          this.entries.put(str, trustedCertEntry);
        } else {
          throw new IOException("Unrecognized keystore entry");
        } 
      } 
      if (paramArrayOfchar != null) {
        byte[] arrayOfByte1 = messageDigest.digest();
        byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
        dataInputStream.readFully(arrayOfByte2);
        for (byte b1 = 0; b1 < arrayOfByte1.length; b1++) {
          if (arrayOfByte1[b1] != arrayOfByte2[b1]) {
            UnrecoverableKeyException unrecoverableKeyException = new UnrecoverableKeyException("Password verification failed");
            throw (IOException)(new IOException("Keystore was tampered with, or password was incorrect"))
              
              .initCause(unrecoverableKeyException);
          } 
        } 
      } 
    } 
  }
  
  private MessageDigest getPreKeyedHash(char[] paramArrayOfchar) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    MessageDigest messageDigest = MessageDigest.getInstance("SHA");
    byte[] arrayOfByte = new byte[paramArrayOfchar.length * 2];
    byte b1, b2;
    for (b1 = 0, b2 = 0; b1 < paramArrayOfchar.length; b1++) {
      arrayOfByte[b2++] = (byte)(paramArrayOfchar[b1] >> 8);
      arrayOfByte[b2++] = (byte)paramArrayOfchar[b1];
    } 
    messageDigest.update(arrayOfByte);
    for (b1 = 0; b1 < arrayOfByte.length; b1++)
      arrayOfByte[b1] = 0; 
    messageDigest.update("Mighty Aphrodite".getBytes("UTF8"));
    return messageDigest;
  }
}
