package sun.security.ssl;

import java.io.PrintStream;
import java.security.AccessController;
import java.util.Locale;
import sun.security.action.GetPropertyAction;

public class Debug {
  private String prefix;
  
  private static String args = AccessController.<String>doPrivileged(new GetPropertyAction("javax.net.debug", ""));
  
  static {
    args = args.toLowerCase(Locale.ENGLISH);
    if (args.equals("help"))
      Help(); 
  }
  
  public static void Help() {
    System.err.println();
    System.err.println("all            turn on all debugging");
    System.err.println("ssl            turn on ssl debugging");
    System.err.println();
    System.err.println("The following can be used with ssl:");
    System.err.println("\trecord       enable per-record tracing");
    System.err.println("\thandshake    print each handshake message");
    System.err.println("\tkeygen       print key generation data");
    System.err.println("\tsession      print session activity");
    System.err.println("\tdefaultctx   print default SSL initialization");
    System.err.println("\tsslctx       print SSLContext tracing");
    System.err.println("\tsessioncache print session cache tracing");
    System.err.println("\tkeymanager   print key manager tracing");
    System.err.println("\ttrustmanager print trust manager tracing");
    System.err.println("\tpluggability print pluggability tracing");
    System.err.println();
    System.err.println("\thandshake debugging can be widened with:");
    System.err.println("\tdata         hex dump of each handshake message");
    System.err.println("\tverbose      verbose handshake message printing");
    System.err.println();
    System.err.println("\trecord debugging can be widened with:");
    System.err.println("\tplaintext    hex dump of record plaintext");
    System.err.println("\tpacket       print raw SSL/TLS packets");
    System.err.println();
    System.exit(0);
  }
  
  public static Debug getInstance(String paramString) {
    return getInstance(paramString, paramString);
  }
  
  public static Debug getInstance(String paramString1, String paramString2) {
    if (isOn(paramString1)) {
      Debug debug = new Debug();
      debug.prefix = paramString2;
      return debug;
    } 
    return null;
  }
  
  public static boolean isOn(String paramString) {
    if (args == null)
      return false; 
    int i = 0;
    paramString = paramString.toLowerCase(Locale.ENGLISH);
    if (args.indexOf("all") != -1)
      return true; 
    if ((i = args.indexOf("ssl")) != -1 && 
      args.indexOf("sslctx", i) == -1)
      if (!paramString.equals("data") && 
        !paramString.equals("packet") && 
        !paramString.equals("plaintext"))
        return true;  
    return (args.indexOf(paramString) != -1);
  }
  
  public void println(String paramString) {
    System.err.println(this.prefix + ": " + paramString);
  }
  
  public void println() {
    System.err.println(this.prefix + ":");
  }
  
  public static void println(String paramString1, String paramString2) {
    System.err.println(paramString1 + ": " + paramString2);
  }
  
  public static void println(PrintStream paramPrintStream, String paramString, byte[] paramArrayOfbyte) {
    paramPrintStream.print(paramString + ":  { ");
    if (paramArrayOfbyte == null) {
      paramPrintStream.print("null");
    } else {
      for (byte b = 0; b < paramArrayOfbyte.length; b++) {
        if (b != 0)
          paramPrintStream.print(", "); 
        paramPrintStream.print(paramArrayOfbyte[b] & 0xFF);
      } 
    } 
    paramPrintStream.println(" }");
  }
  
  static boolean getBooleanProperty(String paramString, boolean paramBoolean) {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction(paramString));
    if (str == null)
      return paramBoolean; 
    if (str.equalsIgnoreCase("false"))
      return false; 
    if (str.equalsIgnoreCase("true"))
      return true; 
    throw new RuntimeException("Value of " + paramString + " must either be 'true' or 'false'");
  }
  
  static String toString(byte[] paramArrayOfbyte) {
    return sun.security.util.Debug.toString(paramArrayOfbyte);
  }
}
