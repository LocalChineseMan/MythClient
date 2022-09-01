package sun.security.ssl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.net.ssl.SSLException;

final class CipherSuiteList {
  private final Collection<CipherSuite> cipherSuites;
  
  private String[] suiteNames;
  
  private volatile Boolean containsEC;
  
  CipherSuiteList(Collection<CipherSuite> paramCollection) {
    this.cipherSuites = paramCollection;
  }
  
  CipherSuiteList(CipherSuite paramCipherSuite) {
    this.cipherSuites = new ArrayList<>(1);
    this.cipherSuites.add(paramCipherSuite);
  }
  
  CipherSuiteList(String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      throw new IllegalArgumentException("CipherSuites may not be null"); 
    this.cipherSuites = new ArrayList<>(paramArrayOfString.length);
    boolean bool = false;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      CipherSuite cipherSuite = CipherSuite.valueOf(str);
      if (!cipherSuite.isAvailable()) {
        if (!bool) {
          clearAvailableCache();
          bool = true;
        } 
        if (!cipherSuite.isAvailable())
          throw new IllegalArgumentException("Cannot support " + str + " with currently installed providers"); 
      } 
      this.cipherSuites.add(cipherSuite);
    } 
  }
  
  CipherSuiteList(HandshakeInStream paramHandshakeInStream) throws IOException {
    byte[] arrayOfByte = paramHandshakeInStream.getBytes16();
    if ((arrayOfByte.length & 0x1) != 0)
      throw new SSLException("Invalid ClientHello message"); 
    this.cipherSuites = new ArrayList<>(arrayOfByte.length >> 1);
    for (byte b = 0; b < arrayOfByte.length; b += 2)
      this.cipherSuites.add(CipherSuite.valueOf(arrayOfByte[b], arrayOfByte[b + 1])); 
  }
  
  boolean contains(CipherSuite paramCipherSuite) {
    return this.cipherSuites.contains(paramCipherSuite);
  }
  
  boolean containsEC() {
    if (this.containsEC == null) {
      for (CipherSuite cipherSuite : this.cipherSuites) {
        switch (cipherSuite.keyExchange) {
          case K_ECDH_ECDSA:
          case K_ECDH_RSA:
          case K_ECDHE_ECDSA:
          case K_ECDHE_RSA:
          case K_ECDH_ANON:
            this.containsEC = Boolean.valueOf(true);
            return true;
        } 
      } 
      this.containsEC = Boolean.valueOf(false);
    } 
    return this.containsEC.booleanValue();
  }
  
  Iterator<CipherSuite> iterator() {
    return this.cipherSuites.iterator();
  }
  
  Collection<CipherSuite> collection() {
    return this.cipherSuites;
  }
  
  int size() {
    return this.cipherSuites.size();
  }
  
  synchronized String[] toStringArray() {
    if (this.suiteNames == null) {
      this.suiteNames = new String[this.cipherSuites.size()];
      byte b = 0;
      for (CipherSuite cipherSuite : this.cipherSuites)
        this.suiteNames[b++] = cipherSuite.name; 
    } 
    return (String[])this.suiteNames.clone();
  }
  
  public String toString() {
    return this.cipherSuites.toString();
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    byte[] arrayOfByte = new byte[this.cipherSuites.size() * 2];
    byte b = 0;
    for (CipherSuite cipherSuite : this.cipherSuites) {
      arrayOfByte[b] = (byte)(cipherSuite.id >> 8);
      arrayOfByte[b + 1] = (byte)cipherSuite.id;
      b += 2;
    } 
    paramHandshakeOutStream.putBytes16(arrayOfByte);
  }
  
  static synchronized void clearAvailableCache() {
    CipherSuite.BulkCipher.clearAvailableCache();
    JsseJce.clearEcAvailable();
  }
}
