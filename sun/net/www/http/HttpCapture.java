package sun.net.www.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.util.logging.PlatformLogger;

public class HttpCapture {
  private File file = null;
  
  private boolean incoming = true;
  
  private BufferedWriter out = null;
  
  private static boolean initialized = false;
  
  private static volatile ArrayList<Pattern> patterns = null;
  
  private static volatile ArrayList<String> capFiles = null;
  
  private static synchronized void init() {
    initialized = true;
    String str = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            return NetProperties.get("sun.net.http.captureRules");
          }
        });
    if (str != null && !str.isEmpty()) {
      BufferedReader bufferedReader;
      try {
        bufferedReader = new BufferedReader(new FileReader(str));
      } catch (FileNotFoundException fileNotFoundException) {
        return;
      } 
      try {
        String str1 = bufferedReader.readLine();
        while (str1 != null) {
          str1 = str1.trim();
          if (!str1.startsWith("#")) {
            String[] arrayOfString = str1.split(",");
            if (arrayOfString.length == 2) {
              if (patterns == null) {
                patterns = new ArrayList<>();
                capFiles = new ArrayList<>();
              } 
              patterns.add(Pattern.compile(arrayOfString[0].trim()));
              capFiles.add(arrayOfString[1].trim());
            } 
          } 
          str1 = bufferedReader.readLine();
        } 
      } catch (IOException iOException) {
        try {
          bufferedReader.close();
        } catch (IOException iOException1) {}
      } finally {
        try {
          bufferedReader.close();
        } catch (IOException iOException) {}
      } 
    } 
  }
  
  private static synchronized boolean isInitialized() {
    return initialized;
  }
  
  private HttpCapture(File paramFile, URL paramURL) {
    this.file = paramFile;
    try {
      this.out = new BufferedWriter(new FileWriter(this.file, true));
      this.out.write("URL: " + paramURL + "\n");
    } catch (IOException iOException) {
      PlatformLogger.getLogger(HttpCapture.class.getName()).severe((String)null, iOException);
    } 
  }
  
  public synchronized void sent(int paramInt) throws IOException {
    if (this.incoming) {
      this.out.write("\n------>\n");
      this.incoming = false;
      this.out.flush();
    } 
    this.out.write(paramInt);
  }
  
  public synchronized void received(int paramInt) throws IOException {
    if (!this.incoming) {
      this.out.write("\n<------\n");
      this.incoming = true;
      this.out.flush();
    } 
    this.out.write(paramInt);
  }
  
  public synchronized void flush() throws IOException {
    this.out.flush();
  }
  
  public static HttpCapture getCapture(URL paramURL) {
    if (!isInitialized())
      init(); 
    if (patterns == null || patterns.isEmpty())
      return null; 
    String str = paramURL.toString();
    for (byte b = 0; b < patterns.size(); b++) {
      Pattern pattern = patterns.get(b);
      if (pattern.matcher(str).find()) {
        File file;
        String str1 = capFiles.get(b);
        if (str1.indexOf("%d") >= 0) {
          Random random = new Random();
          do {
            String str2 = str1.replace("%d", Integer.toString(random.nextInt()));
            file = new File(str2);
          } while (file.exists());
        } else {
          file = new File(str1);
        } 
        return new HttpCapture(file, paramURL);
      } 
    } 
    return null;
  }
}
