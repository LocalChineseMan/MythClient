package sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.BadPaddingException;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSession;
import sun.misc.JavaNetAccess;
import sun.misc.SharedSecrets;

public final class SSLSocketImpl extends BaseSSLSocketImpl {
  private static final int cs_START = 0;
  
  private static final int cs_HANDSHAKE = 1;
  
  private static final int cs_DATA = 2;
  
  private static final int cs_RENEGOTIATE = 3;
  
  private static final int cs_ERROR = 4;
  
  private static final int cs_SENT_CLOSE = 5;
  
  private static final int cs_CLOSED = 6;
  
  private static final int cs_APP_CLOSED = 7;
  
  private volatile int connectionState;
  
  private boolean receivedCCS;
  
  private boolean expectingFinished;
  
  private SSLException closeReason;
  
  private byte doClientAuth;
  
  private boolean roleIsServer;
  
  private boolean enableSessionCreation = true;
  
  private String host;
  
  private boolean autoClose = true;
  
  private AccessControlContext acc;
  
  private CipherSuiteList enabledCipherSuites;
  
  private String identificationProtocol = null;
  
  private AlgorithmConstraints algorithmConstraints = null;
  
  List<SNIServerName> serverNames = Collections.emptyList();
  
  Collection<SNIMatcher> sniMatchers = Collections.emptyList();
  
  private final Object handshakeLock = new Object();
  
  final ReentrantLock writeLock = new ReentrantLock();
  
  private final Object readLock = new Object();
  
  private InputRecord inrec;
  
  private Authenticator readAuthenticator;
  
  private Authenticator writeAuthenticator;
  
  private CipherBox readCipher;
  
  private CipherBox writeCipher;
  
  private boolean secureRenegotiation;
  
  private byte[] clientVerifyData;
  
  private byte[] serverVerifyData;
  
  private SSLContextImpl sslContext;
  
  private Handshaker handshaker;
  
  private SSLSessionImpl sess;
  
  private volatile SSLSessionImpl handshakeSession;
  
  private HashMap<HandshakeCompletedListener, AccessControlContext> handshakeListeners;
  
  private InputStream sockInput;
  
  private OutputStream sockOutput;
  
  private AppInputStream input;
  
  private AppOutputStream output;
  
  private ProtocolList enabledProtocols;
  
  private ProtocolVersion protocolVersion = ProtocolVersion.DEFAULT;
  
  private static final Debug debug = Debug.getInstance("ssl");
  
  private boolean isFirstAppOutputRecord = true;
  
  private ByteArrayOutputStream heldRecordBuffer = null;
  
  private boolean preferLocalCipherSuites = false;
  
  static final boolean trustNameService = Debug.getBooleanProperty("jdk.tls.trustNameService", false);
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, String paramString, int paramInt) throws IOException, UnknownHostException {
    this.host = paramString;
    this
      .serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
    init(paramSSLContextImpl, false);
    InetSocketAddress inetSocketAddress = (paramString != null) ? new InetSocketAddress(paramString, paramInt) : new InetSocketAddress(InetAddress.getByName(null), paramInt);
    connect(inetSocketAddress, 0);
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, InetAddress paramInetAddress, int paramInt) throws IOException {
    init(paramSSLContextImpl, false);
    InetSocketAddress inetSocketAddress = new InetSocketAddress(paramInetAddress, paramInt);
    connect(inetSocketAddress, 0);
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException, UnknownHostException {
    this.host = paramString;
    this
      .serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
    init(paramSSLContextImpl, false);
    bind(new InetSocketAddress(paramInetAddress, paramInt2));
    InetSocketAddress inetSocketAddress = (paramString != null) ? new InetSocketAddress(paramString, paramInt1) : new InetSocketAddress(InetAddress.getByName(null), paramInt1);
    connect(inetSocketAddress, 0);
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException {
    init(paramSSLContextImpl, false);
    bind(new InetSocketAddress(paramInetAddress2, paramInt2));
    InetSocketAddress inetSocketAddress = new InetSocketAddress(paramInetAddress1, paramInt1);
    connect(inetSocketAddress, 0);
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, boolean paramBoolean1, CipherSuiteList paramCipherSuiteList, byte paramByte, boolean paramBoolean2, ProtocolList paramProtocolList, String paramString, AlgorithmConstraints paramAlgorithmConstraints, Collection<SNIMatcher> paramCollection, boolean paramBoolean3) throws IOException {
    this.doClientAuth = paramByte;
    this.enableSessionCreation = paramBoolean2;
    this.identificationProtocol = paramString;
    this.algorithmConstraints = paramAlgorithmConstraints;
    this.sniMatchers = paramCollection;
    this.preferLocalCipherSuites = paramBoolean3;
    init(paramSSLContextImpl, paramBoolean1);
    this.enabledCipherSuites = paramCipherSuiteList;
    this.enabledProtocols = paramProtocolList;
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl) {
    init(paramSSLContextImpl, false);
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, Socket paramSocket, String paramString, int paramInt, boolean paramBoolean) throws IOException {
    super(paramSocket);
    if (!paramSocket.isConnected())
      throw new SocketException("Underlying socket is not connected"); 
    this.host = paramString;
    this
      .serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
    init(paramSSLContextImpl, false);
    this.autoClose = paramBoolean;
    doneConnect();
  }
  
  SSLSocketImpl(SSLContextImpl paramSSLContextImpl, Socket paramSocket, InputStream paramInputStream, boolean paramBoolean) throws IOException {
    super(paramSocket, paramInputStream);
    if (!paramSocket.isConnected())
      throw new SocketException("Underlying socket is not connected"); 
    init(paramSSLContextImpl, true);
    this.autoClose = paramBoolean;
    doneConnect();
  }
  
  private void init(SSLContextImpl paramSSLContextImpl, boolean paramBoolean) {
    this.sslContext = paramSSLContextImpl;
    this.sess = SSLSessionImpl.nullSession;
    this.handshakeSession = null;
    this.roleIsServer = paramBoolean;
    this.connectionState = 0;
    this.receivedCCS = false;
    this.readCipher = CipherBox.NULL;
    this.readAuthenticator = MAC.NULL;
    this.writeCipher = CipherBox.NULL;
    this.writeAuthenticator = MAC.NULL;
    this.secureRenegotiation = false;
    this.clientVerifyData = new byte[0];
    this.serverVerifyData = new byte[0];
    this
      .enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(this.roleIsServer);
    this
      .enabledProtocols = this.sslContext.getDefaultProtocolList(this.roleIsServer);
    this.inrec = null;
    this.acc = AccessController.getContext();
    this.input = new AppInputStream(this);
    this.output = new AppOutputStream(this);
  }
  
  public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    if (isLayered())
      throw new SocketException("Already connected"); 
    if (!(paramSocketAddress instanceof InetSocketAddress))
      throw new SocketException("Cannot handle non-Inet socket addresses."); 
    super.connect(paramSocketAddress, paramInt);
    doneConnect();
  }
  
  void doneConnect() throws IOException {
    this.sockInput = super.getInputStream();
    this.sockOutput = super.getOutputStream();
    initHandshaker();
  }
  
  private synchronized int getConnectionState() {
    return this.connectionState;
  }
  
  private synchronized void setConnectionState(int paramInt) {
    this.connectionState = paramInt;
  }
  
  AccessControlContext getAcc() {
    return this.acc;
  }
  
  void writeRecord(OutputRecord paramOutputRecord) throws IOException {
    writeRecord(paramOutputRecord, false);
  }
  
  void writeRecord(OutputRecord paramOutputRecord, boolean paramBoolean) throws IOException {
    while (paramOutputRecord.contentType() == 23) {
      switch (getConnectionState()) {
        case 1:
          performInitialHandshake();
          continue;
        case 2:
        case 3:
          break;
        case 4:
          fatal((byte)0, "error while writing to socket");
          continue;
        case 5:
        case 6:
        case 7:
          if (this.closeReason != null)
            throw this.closeReason; 
          throw new SocketException("Socket closed");
      } 
      throw new SSLProtocolException("State error, send app data");
    } 
    if (!paramOutputRecord.isEmpty())
      if (paramOutputRecord.isAlert((byte)0) && getSoLinger() >= 0) {
        boolean bool = Thread.interrupted();
        try {
          if (this.writeLock.tryLock(getSoLinger(), TimeUnit.SECONDS)) {
            try {
              writeRecordInternal(paramOutputRecord, paramBoolean);
            } finally {
              this.writeLock.unlock();
            } 
          } else {
            SSLException sSLException = new SSLException("SO_LINGER timeout, close_notify message cannot be sent.");
            if (isLayered() && !this.autoClose) {
              fatal((byte)-1, sSLException);
            } else if (debug != null && Debug.isOn("ssl")) {
              System.out.println(
                  Thread.currentThread().getName() + ", received Exception: " + sSLException);
            } 
            this.sess.invalidate();
          } 
        } catch (InterruptedException interruptedException) {
          bool = true;
        } 
        if (bool)
          Thread.currentThread().interrupt(); 
      } else {
        this.writeLock.lock();
        try {
          writeRecordInternal(paramOutputRecord, paramBoolean);
        } finally {
          this.writeLock.unlock();
        } 
      }  
  }
  
  private void writeRecordInternal(OutputRecord paramOutputRecord, boolean paramBoolean) throws IOException {
    paramOutputRecord.encrypt(this.writeAuthenticator, this.writeCipher);
    if (paramBoolean)
      if (getTcpNoDelay()) {
        paramBoolean = false;
      } else if (this.heldRecordBuffer == null) {
        this.heldRecordBuffer = new ByteArrayOutputStream(40);
      }  
    paramOutputRecord.write(this.sockOutput, paramBoolean, this.heldRecordBuffer);
    if (this.connectionState < 4)
      checkSequenceNumber(this.writeAuthenticator, paramOutputRecord.contentType()); 
    if (this.isFirstAppOutputRecord && paramOutputRecord
      .contentType() == 23)
      this.isFirstAppOutputRecord = false; 
  }
  
  boolean needToSplitPayload() {
    this.writeLock.lock();
    try {
      return (this.protocolVersion.v <= ProtocolVersion.TLS10.v && this.writeCipher
        .isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection);
    } finally {
      this.writeLock.unlock();
    } 
  }
  
  void readDataRecord(InputRecord paramInputRecord) throws IOException {
    if (getConnectionState() == 1)
      performInitialHandshake(); 
    readRecord(paramInputRecord, true);
  }
  
  private void readRecord(InputRecord paramInputRecord, boolean paramBoolean) throws IOException {
    synchronized (this.readLock) {
      int i;
      while ((i = getConnectionState()) != 6 && i != 4 && i != 7) {
        try {
          paramInputRecord.setAppDataValid(false);
          paramInputRecord.read(this.sockInput, this.sockOutput);
        } catch (SSLProtocolException sSLProtocolException) {
          try {
            fatal((byte)10, sSLProtocolException);
          } catch (IOException iOException) {}
          throw sSLProtocolException;
        } catch (EOFException eOFException) {
          boolean bool1 = (getConnectionState() <= 1) ? true : false;
          boolean bool2 = (requireCloseNotify || bool1) ? true : false;
          if (debug != null && Debug.isOn("ssl"))
            System.out.println(Thread.currentThread().getName() + ", received EOFException: " + (bool2 ? "error" : "ignored")); 
          if (bool2) {
            SSLProtocolException sSLProtocolException;
            if (bool1) {
              SSLHandshakeException sSLHandshakeException = new SSLHandshakeException("Remote host closed connection during handshake");
            } else {
              sSLProtocolException = new SSLProtocolException("Remote host closed connection incorrectly");
            } 
            sSLProtocolException.initCause(eOFException);
            throw sSLProtocolException;
          } 
          closeInternal(false);
          continue;
        } 
        try {
          paramInputRecord.decrypt(this.readAuthenticator, this.readCipher);
        } catch (BadPaddingException badPaddingException) {
          byte b = (paramInputRecord.contentType() == 22) ? 40 : 20;
          fatal(b, badPaddingException.getMessage(), badPaddingException);
        } 
        synchronized (this) {
          switch (paramInputRecord.contentType()) {
            case 22:
              initHandshaker();
              if (!this.handshaker.activated())
                if (this.connectionState == 3) {
                  this.handshaker.activate(this.protocolVersion);
                } else {
                  this.handshaker.activate(null);
                }  
              this.handshaker.process_record(paramInputRecord, this.expectingFinished);
              this.expectingFinished = false;
              if (this.handshaker.invalidated) {
                this.handshaker = null;
                this.receivedCCS = false;
                if (this.connectionState == 3)
                  this.connectionState = 2; 
              } else if (this.handshaker.isDone()) {
                this
                  .secureRenegotiation = this.handshaker.isSecureRenegotiation();
                this.clientVerifyData = this.handshaker.getClientVerifyData();
                this.serverVerifyData = this.handshaker.getServerVerifyData();
                this.sess = this.handshaker.getSession();
                this.handshakeSession = null;
                this.handshaker = null;
                this.connectionState = 2;
                this.receivedCCS = false;
                if (this.handshakeListeners != null) {
                  HandshakeCompletedEvent handshakeCompletedEvent = new HandshakeCompletedEvent(this, this.sess);
                  NotifyHandshakeThread notifyHandshakeThread = new NotifyHandshakeThread(this.handshakeListeners.entrySet(), handshakeCompletedEvent);
                  notifyHandshakeThread.start();
                } 
              } 
              if (paramBoolean || this.connectionState != 2)
                continue; 
              break;
            case 23:
              if (this.connectionState != 2 && this.connectionState != 3 && this.connectionState != 5)
                throw new SSLProtocolException("Data received in non-data state: " + this.connectionState); 
              if (this.expectingFinished)
                throw new SSLProtocolException("Expecting finished message, received data"); 
              if (!paramBoolean)
                throw new SSLException("Discarding app data"); 
              paramInputRecord.setAppDataValid(true);
              break;
            case 21:
              recvAlert(paramInputRecord);
              continue;
            case 20:
              if ((this.connectionState != 1 && this.connectionState != 3) || 
                
                !this.handshaker.sessionKeysCalculated() || this.receivedCCS) {
                fatal((byte)10, "illegal change cipher spec msg, conn state = " + this.connectionState + ", handshake state = " + this.handshaker.state);
              } else if (paramInputRecord.available() != 1 || paramInputRecord.read() != 1) {
                fatal((byte)10, "Malformed change cipher spec msg");
              } 
              this.receivedCCS = true;
              changeReadCiphers();
              this.expectingFinished = true;
              continue;
            default:
              if (debug != null && Debug.isOn("ssl"))
                System.out.println(Thread.currentThread().getName() + ", Received record type: " + paramInputRecord
                    
                    .contentType()); 
              continue;
          } 
          if (this.connectionState < 4)
            checkSequenceNumber(this.readAuthenticator, paramInputRecord.contentType()); 
          return;
        } 
      } 
      paramInputRecord.close();
      return;
    } 
  }
  
  private void checkSequenceNumber(Authenticator paramAuthenticator, byte paramByte) throws IOException {
    if (this.connectionState >= 4 || paramAuthenticator == MAC.NULL)
      return; 
    if (paramAuthenticator.seqNumOverflow()) {
      if (debug != null && Debug.isOn("ssl"))
        System.out.println(Thread.currentThread().getName() + ", sequence number extremely close to overflow " + "(2^64-1 packets). Closing connection."); 
      fatal((byte)40, "sequence number overflow");
    } 
    if (paramByte != 22 && paramAuthenticator.seqNumIsHuge()) {
      if (debug != null && Debug.isOn("ssl"))
        System.out.println(Thread.currentThread().getName() + ", request renegotiation " + "to avoid sequence number overflow"); 
      startHandshake();
    } 
  }
  
  AppInputStream getAppInputStream() {
    return this.input;
  }
  
  AppOutputStream getAppOutputStream() {
    return this.output;
  }
  
  private void initHandshaker() {
    switch (this.connectionState) {
      case 0:
      case 2:
        break;
      case 1:
      case 3:
        return;
      default:
        throw new IllegalStateException("Internal error");
    } 
    if (this.connectionState == 0) {
      this.connectionState = 1;
    } else {
      this.connectionState = 3;
    } 
    if (this.roleIsServer) {
      this.handshaker = new ServerHandshaker(this, this.sslContext, this.enabledProtocols, this.doClientAuth, this.protocolVersion, (this.connectionState == 1), this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData);
      this.handshaker.setSNIMatchers(this.sniMatchers);
      this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
    } else {
      this.handshaker = new ClientHandshaker(this, this.sslContext, this.enabledProtocols, this.protocolVersion, (this.connectionState == 1), this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData);
      this.handshaker.setSNIServerNames(this.serverNames);
    } 
    this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites);
    this.handshaker.setEnableSessionCreation(this.enableSessionCreation);
  }
  
  private void performInitialHandshake() throws IOException {
    synchronized (this.handshakeLock) {
      if (getConnectionState() == 1) {
        kickstartHandshake();
        if (this.inrec == null) {
          this.inrec = new InputRecord();
          this.inrec.setHandshakeHash(this.input.r.getHandshakeHash());
          this.inrec.setHelloVersion(this.input.r.getHelloVersion());
          this.inrec.enableFormatChecks();
        } 
        readRecord(this.inrec, false);
        this.inrec = null;
      } 
    } 
  }
  
  public void startHandshake() throws IOException {
    startHandshake(true);
  }
  
  private void startHandshake(boolean paramBoolean) throws IOException {
    checkWrite();
    try {
      if (getConnectionState() == 1) {
        performInitialHandshake();
      } else {
        kickstartHandshake();
      } 
    } catch (Exception exception) {
      handleException(exception, paramBoolean);
    } 
  }
  
  private synchronized void kickstartHandshake() throws IOException {
    switch (this.connectionState) {
      case 1:
        break;
      case 2:
        if (!this.secureRenegotiation && !Handshaker.allowUnsafeRenegotiation)
          throw new SSLHandshakeException("Insecure renegotiation is not allowed"); 
        if (!this.secureRenegotiation && 
          debug != null && Debug.isOn("handshake"))
          System.out.println("Warning: Using insecure renegotiation"); 
        initHandshaker();
        break;
      case 3:
        return;
      case 0:
        throw new SocketException("handshaking attempted on unconnected socket");
      default:
        throw new SocketException("connection is closed");
    } 
    if (!this.handshaker.activated()) {
      if (this.connectionState == 3) {
        this.handshaker.activate(this.protocolVersion);
      } else {
        this.handshaker.activate(null);
      } 
      if (this.handshaker instanceof ClientHandshaker) {
        this.handshaker.kickstart();
      } else if (this.connectionState != 1) {
        this.handshaker.kickstart();
        this.handshaker.handshakeHash.reset();
      } 
    } 
  }
  
  public boolean isClosed() {
    return (this.connectionState == 7);
  }
  
  boolean checkEOF() throws IOException {
    switch (getConnectionState()) {
      case 0:
        throw new SocketException("Socket is not connected");
      case 1:
      case 2:
      case 3:
      case 5:
        return false;
      case 7:
        throw new SocketException("Socket is closed");
    } 
    if (this.closeReason == null)
      return true; 
    SSLException sSLException = new SSLException("Connection has been shutdown: " + this.closeReason);
    sSLException.initCause(this.closeReason);
    throw sSLException;
  }
  
  void checkWrite() throws IOException {
    if (checkEOF() || getConnectionState() == 5)
      throw new SocketException("Connection closed by remote host"); 
  }
  
  protected void closeSocket() throws IOException {
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", called closeSocket()"); 
    super.close();
  }
  
  private void closeSocket(boolean paramBoolean) throws IOException {
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", called closeSocket(" + paramBoolean + ")"); 
    if (!isLayered() || this.autoClose) {
      super.close();
    } else if (paramBoolean) {
      waitForClose(false);
    } 
  }
  
  public void close() throws IOException {
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", called close()"); 
    closeInternal(true);
    setConnectionState(7);
  }
  
  private void closeInternal(boolean paramBoolean) throws IOException {
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", called closeInternal(" + paramBoolean + ")"); 
    int i = getConnectionState();
    boolean bool = false;
    Throwable throwable = null;
    try {
      switch (i) {
        case 0:
          closeSocket(paramBoolean);
          break;
        case 4:
          closeSocket();
          break;
        case 6:
        case 7:
          break;
        default:
          synchronized (this) {
            if ((i = getConnectionState()) == 6 || i == 4 || i == 7)
              return; 
            if (i != 5)
              try {
                warning((byte)0);
                this.connectionState = 5;
              } catch (Throwable throwable1) {
                this.connectionState = 4;
                throwable = throwable1;
                bool = true;
                closeSocket(paramBoolean);
              }  
          } 
          if (i == 5) {
            if (debug != null && Debug.isOn("ssl"))
              System.out.println(Thread.currentThread().getName() + ", close invoked again; state = " + 
                  
                  getConnectionState()); 
            if (!paramBoolean)
              return; 
            synchronized (this) {
              while (this.connectionState < 6) {
                try {
                  wait();
                } catch (InterruptedException interruptedException) {}
              } 
            } 
            if (debug != null && Debug.isOn("ssl"))
              System.out.println(Thread.currentThread().getName() + ", after primary close; state = " + 
                  
                  getConnectionState()); 
            return;
          } 
          if (!bool) {
            bool = true;
            closeSocket(paramBoolean);
          } 
          break;
      } 
    } finally {
      synchronized (this) {
        this.connectionState = (this.connectionState == 7) ? 7 : 6;
        notifyAll();
      } 
      if (bool)
        disposeCiphers(); 
      if (throwable != null) {
        if (throwable instanceof Error)
          throw (Error)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
      } 
    } 
  }
  
  void waitForClose(boolean paramBoolean) throws IOException {
    // Byte code:
    //   0: getstatic sun/security/ssl/SSLSocketImpl.debug : Lsun/security/ssl/Debug;
    //   3: ifnull -> 53
    //   6: ldc_w 'ssl'
    //   9: invokestatic isOn : (Ljava/lang/String;)Z
    //   12: ifeq -> 53
    //   15: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   18: new java/lang/StringBuilder
    //   21: dup
    //   22: invokespecial <init> : ()V
    //   25: invokestatic currentThread : ()Ljava/lang/Thread;
    //   28: invokevirtual getName : ()Ljava/lang/String;
    //   31: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: ldc_w ', waiting for close_notify or alert: state '
    //   37: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: aload_0
    //   41: invokespecial getConnectionState : ()I
    //   44: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   47: invokevirtual toString : ()Ljava/lang/String;
    //   50: invokevirtual println : (Ljava/lang/String;)V
    //   53: aload_0
    //   54: invokespecial getConnectionState : ()I
    //   57: dup
    //   58: istore_2
    //   59: bipush #6
    //   61: if_icmpeq -> 109
    //   64: iload_2
    //   65: iconst_4
    //   66: if_icmpeq -> 109
    //   69: iload_2
    //   70: bipush #7
    //   72: if_icmpeq -> 109
    //   75: aload_0
    //   76: getfield inrec : Lsun/security/ssl/InputRecord;
    //   79: ifnonnull -> 93
    //   82: aload_0
    //   83: new sun/security/ssl/InputRecord
    //   86: dup
    //   87: invokespecial <init> : ()V
    //   90: putfield inrec : Lsun/security/ssl/InputRecord;
    //   93: aload_0
    //   94: aload_0
    //   95: getfield inrec : Lsun/security/ssl/InputRecord;
    //   98: iconst_1
    //   99: invokespecial readRecord : (Lsun/security/ssl/InputRecord;Z)V
    //   102: goto -> 53
    //   105: astore_3
    //   106: goto -> 53
    //   109: aload_0
    //   110: aconst_null
    //   111: putfield inrec : Lsun/security/ssl/InputRecord;
    //   114: goto -> 174
    //   117: astore_2
    //   118: getstatic sun/security/ssl/SSLSocketImpl.debug : Lsun/security/ssl/Debug;
    //   121: ifnull -> 168
    //   124: ldc_w 'ssl'
    //   127: invokestatic isOn : (Ljava/lang/String;)Z
    //   130: ifeq -> 168
    //   133: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   136: new java/lang/StringBuilder
    //   139: dup
    //   140: invokespecial <init> : ()V
    //   143: invokestatic currentThread : ()Ljava/lang/Thread;
    //   146: invokevirtual getName : ()Ljava/lang/String;
    //   149: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: ldc_w ', Exception while waiting for close '
    //   155: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: aload_2
    //   159: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   162: invokevirtual toString : ()Ljava/lang/String;
    //   165: invokevirtual println : (Ljava/lang/String;)V
    //   168: iload_1
    //   169: ifeq -> 174
    //   172: aload_2
    //   173: athrow
    //   174: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1751	-> 0
    //   #1752	-> 15
    //   #1754	-> 41
    //   #1752	-> 50
    //   #1760	-> 53
    //   #1763	-> 75
    //   #1764	-> 82
    //   #1769	-> 93
    //   #1772	-> 102
    //   #1770	-> 105
    //   #1772	-> 106
    //   #1774	-> 109
    //   #1783	-> 114
    //   #1775	-> 117
    //   #1776	-> 118
    //   #1777	-> 133
    //   #1780	-> 168
    //   #1781	-> 172
    //   #1784	-> 174
    // Exception table:
    //   from	to	target	type
    //   53	114	117	java/io/IOException
    //   93	102	105	java/net/SocketTimeoutException
  }
  
  private void disposeCiphers() {
    synchronized (this.readLock) {
      this.readCipher.dispose();
    } 
    this.writeLock.lock();
    try {
      this.writeCipher.dispose();
    } finally {
      this.writeLock.unlock();
    } 
  }
  
  void handleException(Exception paramException) throws IOException {
    handleException(paramException, true);
  }
  
  private synchronized void handleException(Exception paramException, boolean paramBoolean) throws IOException {
    byte b;
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", handling exception: " + paramException
          .toString()); 
    if (paramException instanceof java.io.InterruptedIOException && paramBoolean)
      throw (IOException)paramException; 
    if (this.closeReason != null) {
      if (paramException instanceof IOException)
        throw (IOException)paramException; 
      throw Alerts.getSSLException((byte)80, paramException, "Unexpected exception");
    } 
    boolean bool = paramException instanceof SSLException;
    if (!bool && paramException instanceof IOException) {
      try {
        fatal((byte)10, paramException);
      } catch (IOException iOException) {}
      throw (IOException)paramException;
    } 
    if (bool) {
      if (paramException instanceof SSLHandshakeException) {
        b = 40;
      } else {
        b = 10;
      } 
    } else {
      b = 80;
    } 
    fatal(b, paramException);
  }
  
  void warning(byte paramByte) {
    sendAlert((byte)1, paramByte);
  }
  
  synchronized void fatal(byte paramByte, String paramString) throws IOException {
    fatal(paramByte, paramString, (Throwable)null);
  }
  
  synchronized void fatal(byte paramByte, Throwable paramThrowable) throws IOException {
    fatal(paramByte, (String)null, paramThrowable);
  }
  
  synchronized void fatal(byte paramByte, String paramString, Throwable paramThrowable) throws IOException {
    if (this.input != null && this.input.r != null)
      this.input.r.close(); 
    this.sess.invalidate();
    if (this.handshakeSession != null)
      this.handshakeSession.invalidate(); 
    int i = this.connectionState;
    if (this.connectionState < 4)
      this.connectionState = 4; 
    if (this.closeReason == null) {
      if (i == 1)
        this.sockInput.skip(this.sockInput.available()); 
      if (paramByte != -1)
        sendAlert((byte)2, paramByte); 
      if (paramThrowable instanceof SSLException) {
        this.closeReason = (SSLException)paramThrowable;
      } else {
        this
          .closeReason = Alerts.getSSLException(paramByte, paramThrowable, paramString);
      } 
    } 
    closeSocket();
    if (this.connectionState < 6) {
      this.connectionState = (i == 7) ? 7 : 6;
      this.readCipher.dispose();
      this.writeCipher.dispose();
    } 
    throw this.closeReason;
  }
  
  private void recvAlert(InputRecord paramInputRecord) throws IOException {
    byte b1 = (byte)paramInputRecord.read();
    byte b2 = (byte)paramInputRecord.read();
    if (b2 == -1)
      fatal((byte)47, "Short alert message"); 
    if (debug != null && (Debug.isOn("record") || 
      Debug.isOn("handshake")))
      synchronized (System.out) {
        System.out.print(Thread.currentThread().getName());
        System.out.print(", RECV " + this.protocolVersion + " ALERT:  ");
        if (b1 == 2) {
          System.out.print("fatal, ");
        } else if (b1 == 1) {
          System.out.print("warning, ");
        } else {
          System.out.print("<level " + (0xFF & b1) + ">, ");
        } 
        System.out.println(Alerts.alertDescription(b2));
      }  
    if (b1 == 1) {
      if (b2 == 0) {
        if (this.connectionState == 1) {
          fatal((byte)10, "Received close_notify during handshake");
        } else {
          closeInternal(false);
        } 
      } else if (this.handshaker != null) {
        this.handshaker.handshakeAlert(b2);
      } 
    } else {
      String str = "Received fatal alert: " + Alerts.alertDescription(b2);
      if (this.closeReason == null)
        this.closeReason = Alerts.getSSLException(b2, str); 
      fatal((byte)10, str);
    } 
  }
  
  private void sendAlert(byte paramByte1, byte paramByte2) {
    if (this.connectionState >= 5)
      return; 
    if (this.connectionState == 1 && (this.handshaker == null || 
      !this.handshaker.started()))
      return; 
    OutputRecord outputRecord = new OutputRecord((byte)21);
    outputRecord.setVersion(this.protocolVersion);
    boolean bool = (debug != null && Debug.isOn("ssl")) ? true : false;
    if (bool)
      synchronized (System.out) {
        System.out.print(Thread.currentThread().getName());
        System.out.print(", SEND " + this.protocolVersion + " ALERT:  ");
        if (paramByte1 == 2) {
          System.out.print("fatal, ");
        } else if (paramByte1 == 1) {
          System.out.print("warning, ");
        } else {
          System.out.print("<level = " + (0xFF & paramByte1) + ">, ");
        } 
        System.out.println("description = " + 
            Alerts.alertDescription(paramByte2));
      }  
    outputRecord.write(paramByte1);
    outputRecord.write(paramByte2);
    try {
      writeRecord(outputRecord);
    } catch (IOException iOException) {
      if (bool)
        System.out.println(Thread.currentThread().getName() + ", Exception sending alert: " + iOException); 
    } 
  }
  
  private void changeReadCiphers() throws SSLException {
    if (this.connectionState != 1 && this.connectionState != 3)
      throw new SSLProtocolException("State error, change cipher specs"); 
    CipherBox cipherBox = this.readCipher;
    try {
      this.readCipher = this.handshaker.newReadCipher();
      this.readAuthenticator = this.handshaker.newReadAuthenticator();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SSLException("Algorithm missing:  ", generalSecurityException);
    } 
    cipherBox.dispose();
  }
  
  void changeWriteCiphers() throws SSLException {
    if (this.connectionState != 1 && this.connectionState != 3)
      throw new SSLProtocolException("State error, change cipher specs"); 
    CipherBox cipherBox = this.writeCipher;
    try {
      this.writeCipher = this.handshaker.newWriteCipher();
      this.writeAuthenticator = this.handshaker.newWriteAuthenticator();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new SSLException("Algorithm missing:  ", generalSecurityException);
    } 
    cipherBox.dispose();
    this.isFirstAppOutputRecord = true;
  }
  
  synchronized void setVersion(ProtocolVersion paramProtocolVersion) {
    this.protocolVersion = paramProtocolVersion;
    this.output.r.setVersion(paramProtocolVersion);
  }
  
  synchronized String getHost() {
    if (this.host == null || this.host.length() == 0)
      if (!trustNameService) {
        this.host = getOriginalHostname(getInetAddress());
      } else {
        this.host = getInetAddress().getHostName();
      }  
    return this.host;
  }
  
  private static String getOriginalHostname(InetAddress paramInetAddress) {
    JavaNetAccess javaNetAccess = SharedSecrets.getJavaNetAccess();
    String str = javaNetAccess.getOriginalHostName(paramInetAddress);
    if (str == null || str.length() == 0)
      str = paramInetAddress.getHostAddress(); 
    return str;
  }
  
  public synchronized void setHost(String paramString) {
    this.host = paramString;
    this
      .serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
  }
  
  public synchronized InputStream getInputStream() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (this.connectionState == 0)
      throw new SocketException("Socket is not connected"); 
    return this.input;
  }
  
  public synchronized OutputStream getOutputStream() throws IOException {
    if (isClosed())
      throw new SocketException("Socket is closed"); 
    if (this.connectionState == 0)
      throw new SocketException("Socket is not connected"); 
    return this.output;
  }
  
  public SSLSession getSession() {
    if (getConnectionState() == 1)
      try {
        startHandshake(false);
      } catch (IOException iOException) {
        if (debug != null && Debug.isOn("handshake"))
          System.out.println(Thread.currentThread().getName() + ", IOException in getSession():  " + iOException); 
      }  
    synchronized (this) {
      return this.sess;
    } 
  }
  
  public synchronized SSLSession getHandshakeSession() {
    return this.handshakeSession;
  }
  
  synchronized void setHandshakeSession(SSLSessionImpl paramSSLSessionImpl) {
    this.handshakeSession = paramSSLSessionImpl;
  }
  
  public synchronized void setEnableSessionCreation(boolean paramBoolean) {
    this.enableSessionCreation = paramBoolean;
    if (this.handshaker != null && !this.handshaker.activated())
      this.handshaker.setEnableSessionCreation(this.enableSessionCreation); 
  }
  
  public synchronized boolean getEnableSessionCreation() {
    return this.enableSessionCreation;
  }
  
  public synchronized void setNeedClientAuth(boolean paramBoolean) {
    this.doClientAuth = paramBoolean ? 2 : 0;
    if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && 
      
      !this.handshaker.activated())
      ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth); 
  }
  
  public synchronized boolean getNeedClientAuth() {
    return (this.doClientAuth == 2);
  }
  
  public synchronized void setWantClientAuth(boolean paramBoolean) {
    this.doClientAuth = paramBoolean ? 1 : 0;
    if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && 
      
      !this.handshaker.activated())
      ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth); 
  }
  
  public synchronized boolean getWantClientAuth() {
    return (this.doClientAuth == 1);
  }
  
  public synchronized void setUseClientMode(boolean paramBoolean) {
    switch (this.connectionState) {
      case 0:
        if (this.roleIsServer != (!paramBoolean) && this.sslContext
          .isDefaultProtocolList(this.enabledProtocols))
          this.enabledProtocols = this.sslContext.getDefaultProtocolList(!paramBoolean); 
        this.roleIsServer = !paramBoolean;
        return;
      case 1:
        assert this.handshaker != null;
        if (!this.handshaker.activated()) {
          if (this.roleIsServer != (!paramBoolean) && this.sslContext
            .isDefaultProtocolList(this.enabledProtocols))
            this.enabledProtocols = this.sslContext.getDefaultProtocolList(!paramBoolean); 
          this.roleIsServer = !paramBoolean;
          this.connectionState = 0;
          initHandshaker();
          return;
        } 
        break;
    } 
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", setUseClientMode() invoked in state = " + this.connectionState); 
    throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
  }
  
  public synchronized boolean getUseClientMode() {
    return !this.roleIsServer;
  }
  
  public String[] getSupportedCipherSuites() {
    return this.sslContext.getSupportedCipherSuiteList().toStringArray();
  }
  
  public synchronized void setEnabledCipherSuites(String[] paramArrayOfString) {
    this.enabledCipherSuites = new CipherSuiteList(paramArrayOfString);
    if (this.handshaker != null && !this.handshaker.activated())
      this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites); 
  }
  
  public synchronized String[] getEnabledCipherSuites() {
    return this.enabledCipherSuites.toStringArray();
  }
  
  public String[] getSupportedProtocols() {
    return this.sslContext.getSuportedProtocolList().toStringArray();
  }
  
  public synchronized void setEnabledProtocols(String[] paramArrayOfString) {
    this.enabledProtocols = new ProtocolList(paramArrayOfString);
    if (this.handshaker != null && !this.handshaker.activated())
      this.handshaker.setEnabledProtocols(this.enabledProtocols); 
  }
  
  public synchronized String[] getEnabledProtocols() {
    return this.enabledProtocols.toStringArray();
  }
  
  public void setSoTimeout(int paramInt) throws SocketException {
    if (debug != null && Debug.isOn("ssl"))
      System.out.println(Thread.currentThread().getName() + ", setSoTimeout(" + paramInt + ") called"); 
    super.setSoTimeout(paramInt);
  }
  
  public synchronized void addHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener) {
    if (paramHandshakeCompletedListener == null)
      throw new IllegalArgumentException("listener is null"); 
    if (this.handshakeListeners == null)
      this.handshakeListeners = new HashMap<>(4); 
    this.handshakeListeners.put(paramHandshakeCompletedListener, AccessController.getContext());
  }
  
  public synchronized void removeHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener) {
    if (this.handshakeListeners == null)
      throw new IllegalArgumentException("no listeners"); 
    if (this.handshakeListeners.remove(paramHandshakeCompletedListener) == null)
      throw new IllegalArgumentException("listener not registered"); 
    if (this.handshakeListeners.isEmpty())
      this.handshakeListeners = null; 
  }
  
  public synchronized SSLParameters getSSLParameters() {
    SSLParameters sSLParameters = super.getSSLParameters();
    sSLParameters.setEndpointIdentificationAlgorithm(this.identificationProtocol);
    sSLParameters.setAlgorithmConstraints(this.algorithmConstraints);
    sSLParameters.setSNIMatchers(this.sniMatchers);
    sSLParameters.setServerNames(this.serverNames);
    sSLParameters.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
    return sSLParameters;
  }
  
  public synchronized void setSSLParameters(SSLParameters paramSSLParameters) {
    super.setSSLParameters(paramSSLParameters);
    this.identificationProtocol = paramSSLParameters.getEndpointIdentificationAlgorithm();
    this.algorithmConstraints = paramSSLParameters.getAlgorithmConstraints();
    this.preferLocalCipherSuites = paramSSLParameters.getUseCipherSuitesOrder();
    List<SNIServerName> list = paramSSLParameters.getServerNames();
    if (list != null)
      this.serverNames = list; 
    Collection<SNIMatcher> collection = paramSSLParameters.getSNIMatchers();
    if (collection != null)
      this.sniMatchers = collection; 
    if (this.handshaker != null && !this.handshaker.started()) {
      this.handshaker.setIdentificationProtocol(this.identificationProtocol);
      this.handshaker.setAlgorithmConstraints(this.algorithmConstraints);
      if (this.roleIsServer) {
        this.handshaker.setSNIMatchers(this.sniMatchers);
        this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
      } else {
        this.handshaker.setSNIServerNames(this.serverNames);
      } 
    } 
  }
  
  boolean receivedChangeCipherSpec() {
    return this.receivedCCS;
  }
  
  private static class NotifyHandshakeThread extends Thread {
    private Set<Map.Entry<HandshakeCompletedListener, AccessControlContext>> targets;
    
    private HandshakeCompletedEvent event;
    
    NotifyHandshakeThread(Set<Map.Entry<HandshakeCompletedListener, AccessControlContext>> param1Set, HandshakeCompletedEvent param1HandshakeCompletedEvent) {
      super("HandshakeCompletedNotify-Thread");
      this.targets = new HashSet<>(param1Set);
      this.event = param1HandshakeCompletedEvent;
    }
    
    public void run() {
      for (Map.Entry<HandshakeCompletedListener, AccessControlContext> entry : this.targets) {
        final HandshakeCompletedListener l = (HandshakeCompletedListener)entry.getKey();
        AccessControlContext accessControlContext = (AccessControlContext)entry.getValue();
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                l.handshakeCompleted(SSLSocketImpl.NotifyHandshakeThread.this.event);
                return null;
              }
            },  accessControlContext);
      } 
    }
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(80);
    stringBuffer.append(Integer.toHexString(hashCode()));
    stringBuffer.append("[");
    stringBuffer.append(this.sess.getCipherSuite());
    stringBuffer.append(": ");
    stringBuffer.append(super.toString());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}
