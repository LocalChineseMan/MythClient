package sun.security.provider;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import sun.security.util.Debug;

abstract class SeedGenerator {
  private static SeedGenerator instance;
  
  private static final Debug debug = Debug.getInstance("provider");
  
  static {
    String str = SunEntries.getSeedSource();
    if (str.equals("file:/dev/random") || str
      .equals("file:/dev/urandom")) {
      try {
        instance = new NativeSeedGenerator(str);
        if (debug != null)
          debug.println("Using operating system seed generator" + str); 
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("Failed to use operating system seed generator: " + iOException
              .toString()); 
      } 
    } else if (str.length() != 0) {
      try {
        instance = new URLSeedGenerator(str);
        if (debug != null)
          debug.println("Using URL seed generator reading from " + str); 
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("Failed to create seed generator with " + str + ": " + iOException
              .toString()); 
      } 
    } 
    if (instance == null) {
      if (debug != null)
        debug.println("Using default threaded seed generator"); 
      instance = new ThreadedSeedGenerator();
    } 
  }
  
  public static void generateSeed(byte[] paramArrayOfbyte) {
    instance.getSeedBytes(paramArrayOfbyte);
  }
  
  static byte[] getSystemEntropy() {
    final MessageDigest md;
    try {
      messageDigest = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
    } 
    byte b = (byte)(int)System.currentTimeMillis();
    messageDigest.update(b);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              Properties properties = System.getProperties();
              Enumeration<?> enumeration = properties.propertyNames();
              while (enumeration.hasMoreElements()) {
                String str = (String)enumeration.nextElement();
                md.update(str.getBytes());
                md.update(properties.getProperty(str).getBytes());
              } 
              SeedGenerator.addNetworkAdapterInfo(md);
              File file = new File(properties.getProperty("java.io.tmpdir"));
              byte b = 0;
              try (DirectoryStream<Path> null = Files.newDirectoryStream(file.toPath())) {
                Random random = new Random();
                for (Path path : directoryStream) {
                  if (b < 'Ȁ' || random.nextBoolean())
                    md.update(path.getFileName()
                        .toString().getBytes()); 
                  if (b++ > 'Ѐ')
                    break; 
                } 
              } 
            } catch (Exception exception) {
              md.update((byte)exception.hashCode());
            } 
            Runtime runtime = Runtime.getRuntime();
            byte[] arrayOfByte = SeedGenerator.longToByteArray(runtime.totalMemory());
            md.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = SeedGenerator.longToByteArray(runtime.freeMemory());
            md.update(arrayOfByte, 0, arrayOfByte.length);
            return null;
          }
        });
    return messageDigest.digest();
  }
  
  static class SeedGenerator {}
  
  private static class SeedGenerator {}
  
  private static void addNetworkAdapterInfo(MessageDigest paramMessageDigest) {
    try {
      Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
      while (enumeration.hasMoreElements()) {
        NetworkInterface networkInterface = enumeration.nextElement();
        paramMessageDigest.update(networkInterface.toString().getBytes());
        if (!networkInterface.isVirtual()) {
          byte[] arrayOfByte = networkInterface.getHardwareAddress();
          if (arrayOfByte != null) {
            paramMessageDigest.update(arrayOfByte);
            break;
          } 
        } 
      } 
    } catch (Exception exception) {}
  }
  
  private static byte[] longToByteArray(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = (byte)(int)paramLong;
      paramLong >>= 8L;
    } 
    return arrayOfByte;
  }
  
  abstract void getSeedBytes(byte[] paramArrayOfbyte);
}
