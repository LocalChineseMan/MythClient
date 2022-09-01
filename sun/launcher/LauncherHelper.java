package sun.launcher;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.misc.VM;

public enum LauncherHelper {
  INSTANCE;
  
  private static boolean isCharsetSupported;
  
  private static String encoding;
  
  private static final String encprop = "sun.jnu.encoding";
  
  private static final int LM_JAR = 2;
  
  private static final int LM_CLASS = 1;
  
  private static final int LM_UNKNOWN = 0;
  
  private static Class<?> appClass;
  
  private static final ClassLoader scloader;
  
  private static PrintStream ostream;
  
  private static final String defaultBundleName = "sun.launcher.resources.launcher";
  
  static final boolean trace;
  
  private static final String diagprop = "sun.java.launcher.diag";
  
  private static final String LOCALE_SETTINGS = "Locale settings:";
  
  private static final String PROP_SETTINGS = "Property settings:";
  
  private static final String VM_SETTINGS = "VM settings:";
  
  private static final String INDENT = "    ";
  
  private static StringBuilder outBuf;
  
  private static final String MAIN_CLASS = "Main-Class";
  
  static {
    outBuf = new StringBuilder();
    trace = (VM.getSavedProperty("sun.java.launcher.diag") != null);
    scloader = ClassLoader.getSystemClassLoader();
    encoding = null;
    isCharsetSupported = false;
  }
  
  static void showSettings(boolean paramBoolean1, String paramString, long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean2) {
    initOutput(paramBoolean1);
    String[] arrayOfString = paramString.split(":");
    String str = (arrayOfString.length > 1 && arrayOfString[1] != null) ? arrayOfString[1].trim() : "all";
    switch (str) {
      case "vm":
        printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
        return;
      case "properties":
        printProperties();
        return;
      case "locale":
        printLocale();
        return;
    } 
    printVmSettings(paramLong1, paramLong2, paramLong3, paramBoolean2);
    printProperties();
    printLocale();
  }
  
  private static void printVmSettings(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean) {
    ostream.println("VM settings:");
    if (paramLong3 != 0L)
      ostream.println("    Stack Size: " + SizePrefix.scaleValue(paramLong3)); 
    if (paramLong1 != 0L)
      ostream.println("    Min. Heap Size: " + SizePrefix.scaleValue(paramLong1)); 
    if (paramLong2 != 0L) {
      ostream.println("    Max. Heap Size: " + SizePrefix.scaleValue(paramLong2));
    } else {
      ostream.println("    Max. Heap Size (Estimated): " + SizePrefix.scaleValue(Runtime.getRuntime().maxMemory()));
    } 
    ostream.println("    Ergonomics Machine Class: " + (paramBoolean ? "server" : "client"));
    ostream.println("    Using VM: " + System.getProperty("java.vm.name"));
    ostream.println();
  }
  
  private static void printProperties() {
    Properties properties = System.getProperties();
    ostream.println("Property settings:");
    ArrayList<String> arrayList = new ArrayList();
    arrayList.addAll(properties.stringPropertyNames());
    Collections.sort(arrayList);
    for (String str : arrayList)
      printPropertyValue(str, properties.getProperty(str)); 
    ostream.println();
  }
  
  private static boolean isPath(String paramString) {
    return (paramString.endsWith(".dirs") || paramString.endsWith(".path"));
  }
  
  private static void printPropertyValue(String paramString1, String paramString2) {
    ostream.print("    " + paramString1 + " = ");
    if (paramString1.equals("line.separator")) {
      for (byte b : paramString2.getBytes()) {
        switch (b) {
          case 13:
            ostream.print("\\r ");
            break;
          case 10:
            ostream.print("\\n ");
            break;
          default:
            ostream.printf("0x%02X", new Object[] { Integer.valueOf(b & 0xFF) });
            break;
        } 
      } 
      ostream.println();
      return;
    } 
    if (!isPath(paramString1)) {
      ostream.println(paramString2);
      return;
    } 
    String[] arrayOfString = paramString2.split(System.getProperty("path.separator"));
    boolean bool = true;
    for (String str : arrayOfString) {
      if (bool) {
        ostream.println(str);
        bool = false;
      } else {
        ostream.println("        " + str);
      } 
    } 
  }
  
  private static void printLocale() {
    Locale locale = Locale.getDefault();
    ostream.println("Locale settings:");
    ostream.println("    default locale = " + locale.getDisplayLanguage());
    ostream.println("    default display locale = " + Locale.getDefault(Locale.Category.DISPLAY).getDisplayName());
    ostream.println("    default format locale = " + Locale.getDefault(Locale.Category.FORMAT).getDisplayName());
    printLocales();
    ostream.println();
  }
  
  private static void printLocales() {
    Locale[] arrayOfLocale = Locale.getAvailableLocales();
    byte b = (arrayOfLocale == null) ? 0 : arrayOfLocale.length;
    if (b < 1)
      return; 
    TreeSet<String> treeSet = new TreeSet();
    for (Locale locale : arrayOfLocale)
      treeSet.add(locale.toString()); 
    ostream.print("    available locales = ");
    Iterator<String> iterator = treeSet.iterator();
    int i = b - 1;
    for (int j = 0; iterator.hasNext(); j++) {
      String str = iterator.next();
      ostream.print(str);
      if (j != i)
        ostream.print(", "); 
      if ((j + 1) % 8 == 0) {
        ostream.println();
        ostream.print("        ");
      } 
    } 
  }
  
  private static String getLocalizedMessage(String paramString, Object... paramVarArgs) {
    String str = ResourceBundleHolder.access$000().getString(paramString);
    return (paramVarArgs != null) ? MessageFormat.format(str, paramVarArgs) : str;
  }
  
  static void initHelpMessage(String paramString) {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.header", new Object[] { (paramString == null) ? "java" : paramString }));
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] { Integer.valueOf(32) }));
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.datamodel", new Object[] { Integer.valueOf(64) }));
  }
  
  static void appendVmSelectMessage(String paramString1, String paramString2) {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.vmselect", new Object[] { paramString1, paramString2 }));
  }
  
  static void appendVmSynonymMessage(String paramString1, String paramString2) {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.hotspot", new Object[] { paramString1, paramString2 }));
  }
  
  static void appendVmErgoMessage(boolean paramBoolean, String paramString) {
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.ergo.message1", new Object[] { paramString }));
    outBuf = paramBoolean ? outBuf.append(",\n" + getLocalizedMessage("java.launcher.ergo.message2", new Object[0]) + "\n\n") : outBuf.append(".\n\n");
  }
  
  static void printHelpMessage(boolean paramBoolean) {
    initOutput(paramBoolean);
    outBuf = outBuf.append(getLocalizedMessage("java.launcher.opt.footer", new Object[] { File.pathSeparator }));
    ostream.println(outBuf.toString());
  }
  
  static void printXUsageMessage(boolean paramBoolean) {
    initOutput(paramBoolean);
    ostream.println(getLocalizedMessage("java.launcher.X.usage", new Object[] { File.pathSeparator }));
    if (System.getProperty("os.name").contains("OS X"))
      ostream.println(getLocalizedMessage("java.launcher.X.macosx.usage", new Object[] { File.pathSeparator })); 
  }
  
  static void initOutput(boolean paramBoolean) {
    ostream = paramBoolean ? System.err : System.out;
  }
  
  static String getMainClassFromJar(String paramString) {
    String str = null;
    try (JarFile null = new JarFile(paramString)) {
      Manifest manifest = jarFile.getManifest();
      if (manifest == null)
        abort(null, "java.launcher.jar.error2", new Object[] { paramString }); 
      Attributes attributes = manifest.getMainAttributes();
      if (attributes == null)
        abort(null, "java.launcher.jar.error3", new Object[] { paramString }); 
      str = attributes.getValue("Main-Class");
      if (str == null)
        abort(null, "java.launcher.jar.error3", new Object[] { paramString }); 
      if (attributes.containsKey(new Attributes.Name("JavaFX-Application-Class")))
        return FXHelper.class.getName(); 
      return str.trim();
    } catch (IOException iOException) {
      abort(iOException, "java.launcher.jar.error1", new Object[] { paramString });
      return null;
    } 
  }
  
  static void abort(Throwable paramThrowable, String paramString, Object... paramVarArgs) {
    if (paramString != null)
      ostream.println(getLocalizedMessage(paramString, paramVarArgs)); 
    if (trace)
      if (paramThrowable != null) {
        paramThrowable.printStackTrace();
      } else {
        Thread.dumpStack();
      }  
    System.exit(1);
  }
  
  public static Class<?> checkAndLoadMain(boolean paramBoolean, int paramInt, String paramString) {
    initOutput(paramBoolean);
    String str = null;
    switch (paramInt) {
      case 1:
        str = paramString;
        break;
      case 2:
        str = getMainClassFromJar(paramString);
        break;
      default:
        throw new InternalError("" + paramInt + ": Unknown launch mode");
    } 
    str = str.replace('/', '.');
    Class<?> clazz = null;
    try {
      clazz = scloader.loadClass(str);
    } catch (NoClassDefFoundError|ClassNotFoundException noClassDefFoundError) {
      if (System.getProperty("os.name", "").contains("OS X") && Normalizer.isNormalized(str, Normalizer.Form.NFD)) {
        try {
          clazz = scloader.loadClass(Normalizer.normalize(str, Normalizer.Form.NFC));
        } catch (NoClassDefFoundError|ClassNotFoundException noClassDefFoundError1) {
          abort(noClassDefFoundError, "java.launcher.cls.error1", new Object[] { str });
        } 
      } else {
        abort(noClassDefFoundError, "java.launcher.cls.error1", new Object[] { str });
      } 
    } 
    appClass = clazz;
    if (clazz.equals(FXHelper.class) || FXHelper.doesExtendFXApplication(clazz)) {
      FXHelper.setFXLaunchParameters(paramString, paramInt);
      return FXHelper.class;
    } 
    validateMainClass(clazz);
    return clazz;
  }
  
  public static Class<?> getApplicationClass() {
    return appClass;
  }
  
  static void validateMainClass(Class<?> paramClass) {
    Method method;
    try {
      method = paramClass.getMethod("main", new Class[] { String[].class });
    } catch (NoSuchMethodException noSuchMethodException) {
      abort(null, "java.launcher.cls.error4", new Object[] { paramClass.getName(), "javafx.application.Application" });
      return;
    } 
    int i = method.getModifiers();
    if (!Modifier.isStatic(i))
      abort(null, "java.launcher.cls.error2", new Object[] { "static", method.getDeclaringClass().getName() }); 
    if (method.getReturnType() != void.class)
      abort(null, "java.launcher.cls.error3", new Object[] { method.getDeclaringClass().getName() }); 
  }
  
  static String makePlatformString(boolean paramBoolean, byte[] paramArrayOfbyte) {
    initOutput(paramBoolean);
    if (encoding == null) {
      encoding = System.getProperty("sun.jnu.encoding");
      isCharsetSupported = Charset.isSupported(encoding);
    } 
    try {
      return isCharsetSupported ? new String(paramArrayOfbyte, encoding) : new String(paramArrayOfbyte);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      abort(unsupportedEncodingException, null, new Object[0]);
      return null;
    } 
  }
  
  static String[] expandArgs(String[] paramArrayOfString) {
    ArrayList<StdArg> arrayList = new ArrayList();
    for (String str : paramArrayOfString)
      arrayList.add(new StdArg(str)); 
    return expandArgs(arrayList);
  }
  
  static String[] expandArgs(List<StdArg> paramList) {
    ArrayList<String> arrayList = new ArrayList();
    if (trace)
      System.err.println("Incoming arguments:"); 
    for (StdArg stdArg : paramList) {
      if (trace)
        System.err.println(stdArg); 
      if (stdArg.needsExpansion) {
        File file1 = new File(stdArg.arg);
        File file2 = file1.getParentFile();
        String str = file1.getName();
        if (file2 == null)
          file2 = new File("."); 
        try (DirectoryStream<Path> null = Files.newDirectoryStream(file2.toPath(), str)) {
          byte b = 0;
          for (Path path : directoryStream) {
            arrayList.add(path.normalize().toString());
            b++;
          } 
          if (b == 0)
            arrayList.add(stdArg.arg); 
        } catch (Exception exception) {
          arrayList.add(stdArg.arg);
          if (trace) {
            System.err.println("Warning: passing argument as-is " + stdArg);
            System.err.print(exception);
          } 
        } 
        continue;
      } 
      arrayList.add(stdArg.arg);
    } 
    String[] arrayOfString = new String[arrayList.size()];
    arrayList.toArray(arrayOfString);
    if (trace) {
      System.err.println("Expanded arguments:");
      for (String str : arrayOfString)
        System.err.println(str); 
    } 
    return arrayOfString;
  }
  
  static final class FXHelper {
    private static final String JAVAFX_APPLICATION_MARKER = "JavaFX-Application-Class";
    
    private static final String JAVAFX_APPLICATION_CLASS_NAME = "javafx.application.Application";
    
    private static final String JAVAFX_LAUNCHER_CLASS_NAME = "com.sun.javafx.application.LauncherImpl";
    
    private static final String JAVAFX_LAUNCH_MODE_CLASS = "LM_CLASS";
    
    private static final String JAVAFX_LAUNCH_MODE_JAR = "LM_JAR";
    
    private static String fxLaunchName = null;
    
    private static String fxLaunchMode = null;
    
    private static Class<?> fxLauncherClass = null;
    
    private static Method fxLauncherMethod = null;
    
    private static void setFXLaunchParameters(String param1String, int param1Int) {
      try {
        fxLauncherClass = LauncherHelper.scloader.loadClass("com.sun.javafx.application.LauncherImpl");
        fxLauncherMethod = fxLauncherClass.getMethod("launchApplication", new Class[] { String.class, String.class, String[].class });
        int i = fxLauncherMethod.getModifiers();
        if (!Modifier.isStatic(i))
          LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]); 
        if (fxLauncherMethod.getReturnType() != void.class)
          LauncherHelper.abort(null, "java.launcher.javafx.error1", new Object[0]); 
      } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {
        LauncherHelper.abort(classNotFoundException, "java.launcher.cls.error5", new Object[] { classNotFoundException });
      } 
      fxLaunchName = param1String;
      switch (param1Int) {
        case 1:
          fxLaunchMode = "LM_CLASS";
          return;
        case 2:
          fxLaunchMode = "LM_JAR";
          return;
      } 
      throw new InternalError(param1Int + ": Unknown launch mode");
    }
    
    private static boolean doesExtendFXApplication(Class<?> param1Class) {
      for (Class<?> clazz = param1Class.getSuperclass(); clazz != null; 
        clazz = clazz.getSuperclass()) {
        if (clazz.getName().equals("javafx.application.Application"))
          return true; 
      } 
      return false;
    }
    
    public static void main(String... param1VarArgs) throws Exception {
      if (fxLauncherMethod == null || fxLaunchMode == null || fxLaunchName == null)
        throw new RuntimeException("Invalid JavaFX launch parameters"); 
      fxLauncherMethod.invoke(null, new Object[] { fxLaunchName, fxLaunchMode, param1VarArgs });
    }
  }
  
  private static class LauncherHelper {}
  
  private enum LauncherHelper {
  
  }
  
  private static class LauncherHelper {}
}
