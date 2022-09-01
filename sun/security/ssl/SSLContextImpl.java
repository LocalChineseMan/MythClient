package sun.security.ssl;

import java.io.FileInputStream;
import java.security.AccessController;
import java.security.CryptoPrimitive;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import sun.security.action.GetPropertyAction;

public abstract class SSLContextImpl extends SSLContextSpi {
  private static final Debug debug = Debug.getInstance("ssl");
  
  private final EphemeralKeyManager ephemeralKeyManager = new EphemeralKeyManager();
  
  private final SSLSessionContextImpl clientCache = new SSLSessionContextImpl();
  
  private final SSLSessionContextImpl serverCache = new SSLSessionContextImpl();
  
  private boolean isInitialized;
  
  private X509ExtendedKeyManager keyManager;
  
  private X509TrustManager trustManager;
  
  private SecureRandom secureRandom;
  
  private ProtocolList defaultServerProtocolList;
  
  private ProtocolList defaultClientProtocolList;
  
  private ProtocolList supportedProtocolList;
  
  private CipherSuiteList defaultServerCipherSuiteList;
  
  private CipherSuiteList defaultClientCipherSuiteList;
  
  private CipherSuiteList supportedCipherSuiteList;
  
  protected void engineInit(KeyManager[] paramArrayOfKeyManager, TrustManager[] paramArrayOfTrustManager, SecureRandom paramSecureRandom) throws KeyManagementException {
    this.isInitialized = false;
    this.keyManager = chooseKeyManager(paramArrayOfKeyManager);
    if (paramArrayOfTrustManager == null)
      try {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);
        paramArrayOfTrustManager = trustManagerFactory.getTrustManagers();
      } catch (Exception exception) {} 
    this.trustManager = chooseTrustManager(paramArrayOfTrustManager);
    if (paramSecureRandom == null) {
      this.secureRandom = JsseJce.getSecureRandom();
    } else {
      if (SunJSSE.isFIPS() && paramSecureRandom
        .getProvider() != SunJSSE.cryptoProvider)
        throw new KeyManagementException("FIPS mode: SecureRandom must be from provider " + SunJSSE.cryptoProvider
            
            .getName()); 
      this.secureRandom = paramSecureRandom;
    } 
    if (debug != null && Debug.isOn("sslctx"))
      System.out.println("trigger seeding of SecureRandom"); 
    this.secureRandom.nextInt();
    if (debug != null && Debug.isOn("sslctx"))
      System.out.println("done seeding SecureRandom"); 
    this.isInitialized = true;
  }
  
  private X509TrustManager chooseTrustManager(TrustManager[] paramArrayOfTrustManager) throws KeyManagementException {
    for (byte b = 0; paramArrayOfTrustManager != null && b < paramArrayOfTrustManager.length; b++) {
      if (paramArrayOfTrustManager[b] instanceof X509TrustManager) {
        if (SunJSSE.isFIPS() && !(paramArrayOfTrustManager[b] instanceof X509TrustManagerImpl))
          throw new KeyManagementException("FIPS mode: only SunJSSE TrustManagers may be used"); 
        if (paramArrayOfTrustManager[b] instanceof javax.net.ssl.X509ExtendedTrustManager)
          return (X509TrustManager)paramArrayOfTrustManager[b]; 
        return new AbstractTrustManagerWrapper((X509TrustManager)paramArrayOfTrustManager[b]);
      } 
    } 
    return DummyX509TrustManager.INSTANCE;
  }
  
  private X509ExtendedKeyManager chooseKeyManager(KeyManager[] paramArrayOfKeyManager) throws KeyManagementException {
    for (byte b = 0; paramArrayOfKeyManager != null && b < paramArrayOfKeyManager.length; ) {
      KeyManager keyManager = paramArrayOfKeyManager[b];
      if (!(keyManager instanceof X509KeyManager)) {
        b++;
        continue;
      } 
      if (SunJSSE.isFIPS()) {
        if (keyManager instanceof X509KeyManagerImpl || keyManager instanceof SunX509KeyManagerImpl)
          return (X509ExtendedKeyManager)keyManager; 
        throw new KeyManagementException("FIPS mode: only SunJSSE KeyManagers may be used");
      } 
      if (keyManager instanceof X509ExtendedKeyManager)
        return (X509ExtendedKeyManager)keyManager; 
      if (debug != null && Debug.isOn("sslctx"))
        System.out.println("X509KeyManager passed to SSLContext.init():  need an X509ExtendedKeyManager for SSLEngine use"); 
      return new AbstractKeyManagerWrapper((X509KeyManager)keyManager);
    } 
    return DummyX509KeyManager.INSTANCE;
  }
  
  protected SSLSocketFactory engineGetSocketFactory() {
    if (!this.isInitialized)
      throw new IllegalStateException("SSLContextImpl is not initialized"); 
    return new SSLSocketFactoryImpl(this);
  }
  
  protected SSLServerSocketFactory engineGetServerSocketFactory() {
    if (!this.isInitialized)
      throw new IllegalStateException("SSLContext is not initialized"); 
    return new SSLServerSocketFactoryImpl(this);
  }
  
  protected SSLEngine engineCreateSSLEngine() {
    if (!this.isInitialized)
      throw new IllegalStateException("SSLContextImpl is not initialized"); 
    return new SSLEngineImpl(this);
  }
  
  protected SSLEngine engineCreateSSLEngine(String paramString, int paramInt) {
    if (!this.isInitialized)
      throw new IllegalStateException("SSLContextImpl is not initialized"); 
    return new SSLEngineImpl(this, paramString, paramInt);
  }
  
  protected SSLSessionContext engineGetClientSessionContext() {
    return this.clientCache;
  }
  
  protected SSLSessionContext engineGetServerSessionContext() {
    return this.serverCache;
  }
  
  SecureRandom getSecureRandom() {
    return this.secureRandom;
  }
  
  X509ExtendedKeyManager getX509KeyManager() {
    return this.keyManager;
  }
  
  X509TrustManager getX509TrustManager() {
    return this.trustManager;
  }
  
  EphemeralKeyManager getEphemeralKeyManager() {
    return this.ephemeralKeyManager;
  }
  
  ProtocolList getSuportedProtocolList() {
    if (this.supportedProtocolList == null)
      this
        .supportedProtocolList = new ProtocolList(getSupportedSSLParams().getProtocols()); 
    return this.supportedProtocolList;
  }
  
  ProtocolList getDefaultProtocolList(boolean paramBoolean) {
    if (paramBoolean) {
      if (this.defaultServerProtocolList == null)
        this
          .defaultServerProtocolList = new ProtocolList(getDefaultServerSSLParams().getProtocols()); 
      return this.defaultServerProtocolList;
    } 
    if (this.defaultClientProtocolList == null)
      this
        .defaultClientProtocolList = new ProtocolList(getDefaultClientSSLParams().getProtocols()); 
    return this.defaultClientProtocolList;
  }
  
  CipherSuiteList getSupportedCipherSuiteList() {
    synchronized (this) {
      clearAvailableCache();
      if (this.supportedCipherSuiteList == null)
        this.supportedCipherSuiteList = getApplicableCipherSuiteList(
            getSuportedProtocolList(), false); 
      return this.supportedCipherSuiteList;
    } 
  }
  
  CipherSuiteList getDefaultCipherSuiteList(boolean paramBoolean) {
    synchronized (this) {
      clearAvailableCache();
      if (paramBoolean) {
        if (this.defaultServerCipherSuiteList == null)
          this.defaultServerCipherSuiteList = getApplicableCipherSuiteList(
              getDefaultProtocolList(true), true); 
        return this.defaultServerCipherSuiteList;
      } 
      if (this.defaultClientCipherSuiteList == null)
        this.defaultClientCipherSuiteList = getApplicableCipherSuiteList(
            getDefaultProtocolList(false), true); 
      return this.defaultClientCipherSuiteList;
    } 
  }
  
  boolean isDefaultProtocolList(ProtocolList paramProtocolList) {
    return (paramProtocolList == this.defaultServerProtocolList || paramProtocolList == this.defaultClientProtocolList);
  }
  
  private CipherSuiteList getApplicableCipherSuiteList(ProtocolList paramProtocolList, boolean paramBoolean) {
    char c = '\001';
    if (paramBoolean)
      c = 'Ĭ'; 
    Collection<CipherSuite> collection = CipherSuite.allowedCipherSuites();
    TreeSet<CipherSuite> treeSet = new TreeSet();
    if (!paramProtocolList.collection().isEmpty() && paramProtocolList.min.v != ProtocolVersion.NONE.v)
      for (CipherSuite cipherSuite : collection) {
        if (!cipherSuite.allowed || cipherSuite.priority < c)
          continue; 
        if (cipherSuite.isAvailable() && cipherSuite.obsoleted > paramProtocolList.min.v && cipherSuite.supported <= paramProtocolList.max.v) {
          if (SSLAlgorithmConstraints.DEFAULT.permits(
              EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), cipherSuite.name, null))
            treeSet.add(cipherSuite); 
          continue;
        } 
        if (debug != null && 
          Debug.isOn("sslctx") && Debug.isOn("verbose")) {
          if (cipherSuite.obsoleted <= paramProtocolList.min.v) {
            System.out.println("Ignoring obsoleted cipher suite: " + cipherSuite);
            continue;
          } 
          if (cipherSuite.supported > paramProtocolList.max.v) {
            System.out.println("Ignoring unsupported cipher suite: " + cipherSuite);
            continue;
          } 
          System.out.println("Ignoring unavailable cipher suite: " + cipherSuite);
        } 
      }  
    return new CipherSuiteList(treeSet);
  }
  
  private void clearAvailableCache() {
    this.supportedCipherSuiteList = null;
    this.defaultServerCipherSuiteList = null;
    this.defaultClientCipherSuiteList = null;
    CipherSuite.BulkCipher.clearAvailableCache();
    JsseJce.clearEcAvailable();
  }
  
  abstract SSLParameters getDefaultServerSSLParams();
  
  abstract SSLParameters getDefaultClientSSLParams();
  
  abstract SSLParameters getSupportedSSLParams();
  
  private static abstract class AbstractSSLContext extends SSLContextImpl {
    private static final SSLParameters defaultServerSSLParams;
    
    static {
      ProtocolVersion[] arrayOfProtocolVersion;
    }
    
    private AbstractSSLContext() {}
    
    private static final SSLParameters supportedSSLParams = new SSLParameters();
    
    static {
      if (SunJSSE.isFIPS()) {
        supportedSSLParams.setProtocols(new String[] { ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name });
        arrayOfProtocolVersion = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
      } else {
        supportedSSLParams.setProtocols(new String[] { ProtocolVersion.SSL20Hello.name, ProtocolVersion.SSL30.name, ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name });
        arrayOfProtocolVersion = new ProtocolVersion[] { ProtocolVersion.SSL20Hello, ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
      } 
      defaultServerSSLParams = new SSLParameters();
      defaultServerSSLParams.setProtocols(
          getAvailableProtocols(arrayOfProtocolVersion).<String>toArray(new String[0]));
    }
    
    SSLParameters getDefaultServerSSLParams() {
      return defaultServerSSLParams;
    }
    
    SSLParameters getSupportedSSLParams() {
      return supportedSSLParams;
    }
    
    static List<String> getAvailableProtocols(ProtocolVersion[] param1ArrayOfProtocolVersion) {
      List<?> list = Collections.emptyList();
      if (param1ArrayOfProtocolVersion != null && param1ArrayOfProtocolVersion.length != 0) {
        list = new ArrayList(param1ArrayOfProtocolVersion.length);
        for (ProtocolVersion protocolVersion : param1ArrayOfProtocolVersion) {
          if (ProtocolVersion.availableProtocols.contains(protocolVersion))
            list.add(protocolVersion.name); 
        } 
      } 
      return (List)list;
    }
  }
  
  public static final class SSLContextImpl {}
  
  public static final class SSLContextImpl {}
  
  public static final class SSLContextImpl {}
  
  private static class CustomizedSSLContext extends AbstractSSLContext {
    private static final String PROPERTY_NAME = "jdk.tls.client.protocols";
    
    private static final SSLParameters defaultClientSSLParams;
    
    private static IllegalArgumentException reservedException = null;
    
    static {
      ProtocolVersion[] arrayOfProtocolVersion;
    }
    
    static {
      String str = AccessController.<String>doPrivileged(new GetPropertyAction("jdk.tls.client.protocols"));
      if (str == null || str.length() == 0) {
        if (SunJSSE.isFIPS()) {
          arrayOfProtocolVersion = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
        } else {
          arrayOfProtocolVersion = new ProtocolVersion[] { ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
        } 
      } else {
        if (str.length() > 1 && str.charAt(0) == '"' && str
          .charAt(str.length() - 1) == '"')
          str = str.substring(1, str.length() - 1); 
        String[] arrayOfString = null;
        if (str != null && str.length() != 0) {
          arrayOfString = str.split(",");
        } else {
          reservedException = new IllegalArgumentException("No protocol specified in jdk.tls.client.protocols system property");
          arrayOfString = new String[0];
        } 
        arrayOfProtocolVersion = new ProtocolVersion[arrayOfString.length];
        for (byte b = 0; b < arrayOfString.length; b++) {
          arrayOfString[b] = arrayOfString[b].trim();
          try {
            arrayOfProtocolVersion[b] = ProtocolVersion.valueOf(arrayOfString[b]);
          } catch (IllegalArgumentException illegalArgumentException) {
            reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + arrayOfString[b] + " is not a standard SSL/TLS protocol name", illegalArgumentException);
            break;
          } 
        } 
        if (reservedException == null && SunJSSE.isFIPS())
          for (ProtocolVersion protocolVersion : arrayOfProtocolVersion) {
            if (ProtocolVersion.SSL20Hello.v == protocolVersion.v || ProtocolVersion.SSL30.v == protocolVersion.v)
              reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + protocolVersion + " is not FIPS compliant"); 
          }  
      } 
      defaultClientSSLParams = new SSLParameters();
      if (reservedException == null)
        defaultClientSSLParams.setProtocols(
            getAvailableProtocols(arrayOfProtocolVersion).<String>toArray(new String[0])); 
    }
    
    protected CustomizedSSLContext() {
      if (reservedException != null)
        throw reservedException; 
    }
    
    SSLParameters getDefaultClientSSLParams() {
      return defaultClientSSLParams;
    }
  }
  
  public static final class SSLContextImpl {}
  
  public static final class DefaultSSLContext extends CustomizedSSLContext {
    private static final String NONE = "NONE";
    
    private static final String P11KEYSTORE = "PKCS11";
    
    private static volatile SSLContextImpl defaultImpl;
    
    private static TrustManager[] defaultTrustManagers;
    
    private static KeyManager[] defaultKeyManagers;
    
    public DefaultSSLContext() throws Exception {
      try {
        super.engineInit(getDefaultKeyManager(), 
            getDefaultTrustManager(), null);
      } catch (Exception exception) {
        if (SSLContextImpl.debug != null && Debug.isOn("defaultctx"))
          System.out.println("default context init failed: " + exception); 
        throw exception;
      } 
      if (defaultImpl == null)
        defaultImpl = (SSLContextImpl)this; 
    }
    
    protected void engineInit(KeyManager[] param1ArrayOfKeyManager, TrustManager[] param1ArrayOfTrustManager, SecureRandom param1SecureRandom) throws KeyManagementException {
      throw new KeyManagementException("Default SSLContext is initialized automatically");
    }
    
    static synchronized SSLContextImpl getDefaultImpl() throws Exception {
      if (defaultImpl == null)
        new DefaultSSLContext(); 
      return defaultImpl;
    }
    
    private static synchronized TrustManager[] getDefaultTrustManager() throws Exception {
      if (defaultTrustManagers != null)
        return defaultTrustManagers; 
      KeyStore keyStore = TrustManagerFactoryImpl.getCacertsKeyStore("defaultctx");
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      defaultTrustManagers = trustManagerFactory.getTrustManagers();
      return defaultTrustManagers;
    }
    
    private static synchronized KeyManager[] getDefaultKeyManager() throws Exception {
      if (defaultKeyManagers != null)
        return defaultKeyManagers; 
      final HashMap<Object, Object> props = new HashMap<>();
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() throws Exception {
              props.put("keyStore", System.getProperty("javax.net.ssl.keyStore", ""));
              props.put("keyStoreType", System.getProperty("javax.net.ssl.keyStoreType", 
                    
                    KeyStore.getDefaultType()));
              props.put("keyStoreProvider", System.getProperty("javax.net.ssl.keyStoreProvider", ""));
              props.put("keyStorePasswd", System.getProperty("javax.net.ssl.keyStorePassword", ""));
              return null;
            }
          });
      String str1 = (String)hashMap.get("keyStore");
      String str2 = (String)hashMap.get("keyStoreType");
      String str3 = (String)hashMap.get("keyStoreProvider");
      if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
        System.out.println("keyStore is : " + str1);
        System.out.println("keyStore type is : " + str2);
        System.out.println("keyStore provider is : " + str3);
      } 
      if ("PKCS11".equals(str2) && 
        !"NONE".equals(str1))
        throw new IllegalArgumentException("if keyStoreType is PKCS11, then keyStore must be NONE"); 
      FileInputStream fileInputStream = null;
      KeyStore keyStore = null;
      char[] arrayOfChar = null;
      try {
        if (str1.length() != 0 && 
          !"NONE".equals(str1))
          fileInputStream = AccessController.<FileInputStream>doPrivileged((PrivilegedExceptionAction<FileInputStream>)new Object(str1)); 
        String str = (String)hashMap.get("keyStorePasswd");
        if (str.length() != 0)
          arrayOfChar = str.toCharArray(); 
        if (str2.length() != 0) {
          if (SSLContextImpl.debug != null && Debug.isOn("defaultctx"))
            System.out.println("init keystore"); 
          if (str3.length() == 0) {
            keyStore = KeyStore.getInstance(str2);
          } else {
            keyStore = KeyStore.getInstance(str2, str3);
          } 
          keyStore.load(fileInputStream, arrayOfChar);
        } 
      } finally {
        if (fileInputStream != null) {
          fileInputStream.close();
          fileInputStream = null;
        } 
      } 
      if (SSLContextImpl.debug != null && Debug.isOn("defaultctx"))
        System.out.println("init keymanager of type " + 
            KeyManagerFactory.getDefaultAlgorithm()); 
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      if ("PKCS11".equals(str2)) {
        keyManagerFactory.init(keyStore, null);
      } else {
        keyManagerFactory.init(keyStore, arrayOfChar);
      } 
      defaultKeyManagers = keyManagerFactory.getKeyManagers();
      return defaultKeyManagers;
    }
  }
}
