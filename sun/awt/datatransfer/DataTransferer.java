package sun.awt.datatransfer;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;
import sun.util.logging.PlatformLogger;

public abstract class DataTransferer {
  public static final DataFlavor plainTextStringFlavor;
  
  public static final DataFlavor javaTextEncodingFlavor;
  
  private static final Map textMIMESubtypeCharsetSupport;
  
  private static String defaultEncoding;
  
  private static class StandardEncodingsHolder {
    private static final SortedSet<String> standardEncodings = load();
    
    private static SortedSet<String> load() {
      DataTransferer.CharsetComparator charsetComparator = new DataTransferer.CharsetComparator(false);
      TreeSet<String> treeSet = new TreeSet(charsetComparator);
      treeSet.add("US-ASCII");
      treeSet.add("ISO-8859-1");
      treeSet.add("UTF-8");
      treeSet.add("UTF-16BE");
      treeSet.add("UTF-16LE");
      treeSet.add("UTF-16");
      treeSet.add(DataTransferer.getDefaultTextCharset());
      return Collections.unmodifiableSortedSet(treeSet);
    }
  }
  
  private static final Set textNatives = Collections.synchronizedSet(new HashSet());
  
  private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap<>());
  
  private static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap<>());
  
  private static final Map nativeTerminators = Collections.synchronizedMap(new HashMap<>());
  
  private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
  
  private static DataTransferer transferer;
  
  private static final PlatformLogger dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
  
  private static final String[] DEPLOYMENT_CACHE_PROPERTIES;
  
  private static final ArrayList<File> deploymentCacheDirectoryList;
  
  static {
    DataFlavor dataFlavor1 = null;
    try {
      dataFlavor1 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
    } catch (ClassNotFoundException classNotFoundException) {}
    plainTextStringFlavor = dataFlavor1;
    DataFlavor dataFlavor2 = null;
    try {
      dataFlavor2 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
    } catch (ClassNotFoundException classNotFoundException) {}
    javaTextEncodingFlavor = dataFlavor2;
    HashMap<Object, Object> hashMap = new HashMap<>(17);
    hashMap.put("sgml", Boolean.TRUE);
    hashMap.put("xml", Boolean.TRUE);
    hashMap.put("html", Boolean.TRUE);
    hashMap.put("enriched", Boolean.TRUE);
    hashMap.put("richtext", Boolean.TRUE);
    hashMap.put("uri-list", Boolean.TRUE);
    hashMap.put("directory", Boolean.TRUE);
    hashMap.put("css", Boolean.TRUE);
    hashMap.put("calendar", Boolean.TRUE);
    hashMap.put("plain", Boolean.TRUE);
    hashMap.put("rtf", Boolean.FALSE);
    hashMap.put("tab-separated-values", Boolean.FALSE);
    hashMap.put("t140", Boolean.FALSE);
    hashMap.put("rfc822-headers", Boolean.FALSE);
    hashMap.put("parityfec", Boolean.FALSE);
    textMIMESubtypeCharsetSupport = Collections.synchronizedMap(hashMap);
    DEPLOYMENT_CACHE_PROPERTIES = new String[] { "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir" };
    deploymentCacheDirectoryList = new ArrayList<>();
  }
  
  public static synchronized DataTransferer getInstance() {
    return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer();
  }
  
  public static String canonicalName(String paramString) {
    if (paramString == null)
      return null; 
    try {
      return Charset.forName(paramString).name();
    } catch (IllegalCharsetNameException illegalCharsetNameException) {
      return paramString;
    } catch (UnsupportedCharsetException unsupportedCharsetException) {
      return paramString;
    } 
  }
  
  public static String getTextCharset(DataFlavor paramDataFlavor) {
    if (!isFlavorCharsetTextType(paramDataFlavor))
      return null; 
    String str = paramDataFlavor.getParameter("charset");
    return (str != null) ? str : getDefaultTextCharset();
  }
  
  public static String getDefaultTextCharset() {
    if (defaultEncoding != null)
      return defaultEncoding; 
    return defaultEncoding = Charset.defaultCharset().name();
  }
  
  public static boolean doesSubtypeSupportCharset(DataFlavor paramDataFlavor) {
    if (dtLog.isLoggable(PlatformLogger.Level.FINE) && !"text".equals(paramDataFlavor.getPrimaryType()))
      dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed"); 
    String str = paramDataFlavor.getSubType();
    if (str == null)
      return false; 
    Object object = textMIMESubtypeCharsetSupport.get(str);
    if (object != null)
      return (object == Boolean.TRUE); 
    boolean bool = (paramDataFlavor.getParameter("charset") != null) ? true : false;
    textMIMESubtypeCharsetSupport.put(str, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean doesSubtypeSupportCharset(String paramString1, String paramString2) {
    Object object = textMIMESubtypeCharsetSupport.get(paramString1);
    if (object != null)
      return (object == Boolean.TRUE); 
    boolean bool = (paramString2 != null) ? true : false;
    textMIMESubtypeCharsetSupport.put(paramString1, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean isFlavorCharsetTextType(DataFlavor paramDataFlavor) {
    if (DataFlavor.stringFlavor.equals(paramDataFlavor))
      return true; 
    if (!"text".equals(paramDataFlavor.getPrimaryType()) || !doesSubtypeSupportCharset(paramDataFlavor))
      return false; 
    Class<?> clazz = paramDataFlavor.getRepresentationClass();
    if (paramDataFlavor.isRepresentationClassReader() || String.class.equals(clazz) || paramDataFlavor.isRepresentationClassCharBuffer() || char[].class.equals(clazz))
      return true; 
    if (!paramDataFlavor.isRepresentationClassInputStream() && !paramDataFlavor.isRepresentationClassByteBuffer() && !byte[].class.equals(clazz))
      return false; 
    String str = paramDataFlavor.getParameter("charset");
    return (str != null) ? isEncodingSupported(str) : true;
  }
  
  public static boolean isFlavorNoncharsetTextType(DataFlavor paramDataFlavor) {
    if (!"text".equals(paramDataFlavor.getPrimaryType()) || doesSubtypeSupportCharset(paramDataFlavor))
      return false; 
    return (paramDataFlavor.isRepresentationClassInputStream() || paramDataFlavor.isRepresentationClassByteBuffer() || byte[].class.equals(paramDataFlavor.getRepresentationClass()));
  }
  
  public static boolean isEncodingSupported(String paramString) {
    if (paramString == null)
      return false; 
    try {
      return Charset.isSupported(paramString);
    } catch (IllegalCharsetNameException illegalCharsetNameException) {
      return false;
    } 
  }
  
  public static boolean isRemote(Class<?> paramClass) {
    return RMI.isRemote(paramClass);
  }
  
  public static Set<String> standardEncodings() {
    return StandardEncodingsHolder.standardEncodings;
  }
  
  public static FlavorTable adaptFlavorMap(FlavorMap paramFlavorMap) {
    if (paramFlavorMap instanceof FlavorTable)
      return (FlavorTable)paramFlavorMap; 
    return (FlavorTable)new Object(paramFlavorMap);
  }
  
  public void registerTextFlavorProperties(String paramString1, String paramString2, String paramString3, String paramString4) {
    Long long_ = getFormatForNativeAsLong(paramString1);
    textNatives.add(long_);
    nativeCharsets.put(long_, (paramString2 != null && paramString2.length() != 0) ? paramString2 : getDefaultTextCharset());
    if (paramString3 != null && paramString3.length() != 0 && !paramString3.equals("\n"))
      nativeEOLNs.put(long_, paramString3); 
    if (paramString4 != null && paramString4.length() != 0) {
      Integer integer = Integer.valueOf(paramString4);
      if (integer.intValue() > 0)
        nativeTerminators.put(long_, integer); 
    } 
  }
  
  protected boolean isTextFormat(long paramLong) {
    return textNatives.contains(Long.valueOf(paramLong));
  }
  
  protected String getCharsetForTextFormat(Long paramLong) {
    return (String)nativeCharsets.get(paramLong);
  }
  
  protected boolean isURIListFormat(long paramLong) {
    return false;
  }
  
  public SortedMap<Long, DataFlavor> getFormatsForTransferable(Transferable paramTransferable, FlavorTable paramFlavorTable) {
    DataFlavor[] arrayOfDataFlavor = paramTransferable.getTransferDataFlavors();
    if (arrayOfDataFlavor == null)
      return new TreeMap<>(); 
    return getFormatsForFlavors(arrayOfDataFlavor, paramFlavorTable);
  }
  
  public SortedMap getFormatsForFlavor(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable) {
    return getFormatsForFlavors(new DataFlavor[] { paramDataFlavor }, paramFlavorTable);
  }
  
  public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable) {
    HashMap<Object, Object> hashMap1 = new HashMap<>(paramArrayOfDataFlavor.length);
    HashMap<Object, Object> hashMap2 = new HashMap<>(paramArrayOfDataFlavor.length);
    HashMap<Object, Object> hashMap3 = new HashMap<>(paramArrayOfDataFlavor.length);
    HashMap<Object, Object> hashMap4 = new HashMap<>(paramArrayOfDataFlavor.length);
    int i = 0;
    for (int j = paramArrayOfDataFlavor.length - 1; j >= 0; j--) {
      DataFlavor dataFlavor = paramArrayOfDataFlavor[j];
      if (dataFlavor != null)
        if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
          List<String> list = paramFlavorTable.getNativesForFlavor(dataFlavor);
          i += list.size();
          for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
            Long long_ = getFormatForNativeAsLong(iterator.next());
            Integer integer = Integer.valueOf(i--);
            hashMap1.put(long_, dataFlavor);
            hashMap3.put(long_, integer);
            if (("text".equals(dataFlavor.getPrimaryType()) && "plain".equals(dataFlavor.getSubType())) || dataFlavor.equals(DataFlavor.stringFlavor)) {
              hashMap2.put(long_, dataFlavor);
              hashMap4.put(long_, integer);
            } 
          } 
          i += list.size();
        }  
    } 
    hashMap1.putAll(hashMap2);
    hashMap3.putAll(hashMap4);
    IndexOrderComparator indexOrderComparator = new IndexOrderComparator(hashMap3, false);
    TreeMap<Object, Object> treeMap = new TreeMap<>(indexOrderComparator);
    treeMap.putAll(hashMap1);
    return (SortedMap)treeMap;
  }
  
  public long[] getFormatsForTransferableAsArray(Transferable paramTransferable, FlavorTable paramFlavorTable) {
    return keysToLongArray(getFormatsForTransferable(paramTransferable, paramFlavorTable));
  }
  
  public long[] getFormatsForFlavorAsArray(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable) {
    return keysToLongArray(getFormatsForFlavor(paramDataFlavor, paramFlavorTable));
  }
  
  public long[] getFormatsForFlavorsAsArray(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable) {
    return keysToLongArray(getFormatsForFlavors(paramArrayOfDataFlavor, paramFlavorTable));
  }
  
  public Map getFlavorsForFormat(long paramLong, FlavorTable paramFlavorTable) {
    return getFlavorsForFormats(new long[] { paramLong }, paramFlavorTable);
  }
  
  public Map getFlavorsForFormats(long[] paramArrayOflong, FlavorTable paramFlavorTable) {
    HashMap<Object, Object> hashMap = new HashMap<>(paramArrayOflong.length);
    HashSet<Object> hashSet = new HashSet(paramArrayOflong.length);
    HashSet<DataFlavor> hashSet1 = new HashSet(paramArrayOflong.length);
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      long l = paramArrayOflong[b];
      String str = getNativeForFormat(l);
      List<DataFlavor> list = paramFlavorTable.getFlavorsForNative(str);
      for (DataFlavor dataFlavor : list) {
        if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
          Long long_ = Long.valueOf(l);
          Object object = createMapping(long_, dataFlavor);
          hashMap.put(dataFlavor, long_);
          hashSet.add(object);
          hashSet1.add(dataFlavor);
        } 
      } 
    } 
    Iterator<DataFlavor> iterator = hashSet1.iterator();
    while (iterator.hasNext()) {
      DataFlavor dataFlavor = iterator.next();
      List<String> list = paramFlavorTable.getNativesForFlavor(dataFlavor);
      Iterator<String> iterator1 = list.iterator();
      while (iterator1.hasNext()) {
        Long long_ = getFormatForNativeAsLong(iterator1.next());
        Object object = createMapping(long_, dataFlavor);
        if (hashSet.contains(object))
          hashMap.put(dataFlavor, long_); 
      } 
    } 
    return hashMap;
  }
  
  public Set getFlavorsForFormatsAsSet(long[] paramArrayOflong, FlavorTable paramFlavorTable) {
    HashSet<DataFlavor> hashSet = new HashSet(paramArrayOflong.length);
    for (byte b = 0; b < paramArrayOflong.length; b++) {
      String str = getNativeForFormat(paramArrayOflong[b]);
      List<DataFlavor> list = paramFlavorTable.getFlavorsForNative(str);
      for (DataFlavor dataFlavor : list) {
        if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote())
          hashSet.add(dataFlavor); 
      } 
    } 
    return hashSet;
  }
  
  public DataFlavor[] getFlavorsForFormatAsArray(long paramLong, FlavorTable paramFlavorTable) {
    return getFlavorsForFormatsAsArray(new long[] { paramLong }, paramFlavorTable);
  }
  
  public DataFlavor[] getFlavorsForFormatsAsArray(long[] paramArrayOflong, FlavorTable paramFlavorTable) {
    return setToSortedDataFlavorArray(getFlavorsForFormatsAsSet(paramArrayOflong, paramFlavorTable));
  }
  
  private static Object createMapping(Object paramObject1, Object paramObject2) {
    return Arrays.asList(new Object[] { paramObject1, paramObject2 });
  }
  
  private String getBestCharsetForTextFormat(Long paramLong, Transferable paramTransferable) throws IOException {
    String str = null;
    if (paramTransferable != null && isLocaleDependentTextFormat(paramLong.longValue()) && paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor)) {
      try {
        str = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
      } catch (UnsupportedFlavorException unsupportedFlavorException) {}
    } else {
      str = getCharsetForTextFormat(paramLong);
    } 
    if (str == null)
      str = getDefaultTextCharset(); 
    return str;
  }
  
  private byte[] translateTransferableString(String paramString, long paramLong) throws IOException {
    Long long_ = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(long_, null);
    String str2 = (String)nativeEOLNs.get(long_);
    if (str2 != null) {
      int i = paramString.length();
      StringBuffer stringBuffer = new StringBuffer(i * 2);
      for (int j = 0; j < i; j++) {
        if (paramString.startsWith(str2, j)) {
          stringBuffer.append(str2);
          j += str2.length() - 1;
        } else {
          char c = paramString.charAt(j);
          if (c == '\n') {
            stringBuffer.append(str2);
          } else {
            stringBuffer.append(c);
          } 
        } 
      } 
      paramString = stringBuffer.toString();
    } 
    byte[] arrayOfByte = paramString.getBytes(str1);
    Integer integer = (Integer)nativeTerminators.get(long_);
    if (integer != null) {
      int i = integer.intValue();
      byte[] arrayOfByte1 = new byte[arrayOfByte.length + i];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte.length);
      for (int j = arrayOfByte.length; j < arrayOfByte1.length; j++)
        arrayOfByte1[j] = 0; 
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  private String translateBytesToString(byte[] paramArrayOfbyte, long paramLong, Transferable paramTransferable) throws IOException {
    int i;
    Long long_ = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(long_, paramTransferable);
    String str2 = (String)nativeEOLNs.get(long_);
    Integer integer = (Integer)nativeTerminators.get(long_);
    if (integer != null) {
      int j = integer.intValue();
      for (i = 0; i < paramArrayOfbyte.length - j + 1;) {
        for (int k = i; k < i + j; k++) {
          if (paramArrayOfbyte[k] != 0) {
            i += j;
            continue;
          } 
        } 
      } 
    } else {
      i = paramArrayOfbyte.length;
    } 
    String str3 = new String(paramArrayOfbyte, 0, i, str1);
    if (str2 != null) {
      char[] arrayOfChar1 = str3.toCharArray();
      char[] arrayOfChar2 = str2.toCharArray();
      str3 = null;
      byte b = 0;
      for (int j = 0; j < arrayOfChar1.length; ) {
        if (j + arrayOfChar2.length > arrayOfChar1.length) {
          arrayOfChar1[b++] = arrayOfChar1[j++];
          continue;
        } 
        boolean bool = true;
        byte b1;
        int k;
        for (b1 = 0, k = j; b1 < arrayOfChar2.length; b1++, k++) {
          if (arrayOfChar2[b1] != arrayOfChar1[k]) {
            bool = false;
            break;
          } 
        } 
        if (bool) {
          arrayOfChar1[b++] = '\n';
          j += arrayOfChar2.length;
          continue;
        } 
        arrayOfChar1[b++] = arrayOfChar1[j++];
      } 
      str3 = new String(arrayOfChar1, 0, b);
    } 
    return str3;
  }
  
  public byte[] translateTransferable(Transferable paramTransferable, DataFlavor paramDataFlavor, long paramLong) throws IOException {
    // Byte code:
    //   0: aload_1
    //   1: aload_2
    //   2: invokeinterface getTransferData : (Ljava/awt/datatransfer/DataFlavor;)Ljava/lang/Object;
    //   7: astore #5
    //   9: aload #5
    //   11: ifnonnull -> 16
    //   14: aconst_null
    //   15: areturn
    //   16: aload_2
    //   17: getstatic java/awt/datatransfer/DataFlavor.plainTextFlavor : Ljava/awt/datatransfer/DataFlavor;
    //   20: invokevirtual equals : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   23: ifeq -> 58
    //   26: aload #5
    //   28: instanceof java/io/InputStream
    //   31: ifne -> 58
    //   34: aload_1
    //   35: getstatic java/awt/datatransfer/DataFlavor.stringFlavor : Ljava/awt/datatransfer/DataFlavor;
    //   38: invokeinterface getTransferData : (Ljava/awt/datatransfer/DataFlavor;)Ljava/lang/Object;
    //   43: astore #5
    //   45: aload #5
    //   47: ifnonnull -> 52
    //   50: aconst_null
    //   51: areturn
    //   52: iconst_1
    //   53: istore #6
    //   55: goto -> 61
    //   58: iconst_0
    //   59: istore #6
    //   61: goto -> 79
    //   64: astore #7
    //   66: new java/io/IOException
    //   69: dup
    //   70: aload #7
    //   72: invokevirtual getMessage : ()Ljava/lang/String;
    //   75: invokespecial <init> : (Ljava/lang/String;)V
    //   78: athrow
    //   79: iload #6
    //   81: ifne -> 111
    //   84: ldc java/lang/String
    //   86: aload_2
    //   87: invokevirtual getRepresentationClass : ()Ljava/lang/Class;
    //   90: invokevirtual equals : (Ljava/lang/Object;)Z
    //   93: ifeq -> 132
    //   96: aload_2
    //   97: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   100: ifeq -> 132
    //   103: aload_0
    //   104: lload_3
    //   105: invokevirtual isTextFormat : (J)Z
    //   108: ifeq -> 132
    //   111: aload_0
    //   112: aload_2
    //   113: aload_1
    //   114: aload #5
    //   116: checkcast java/lang/String
    //   119: invokespecial removeSuspectedData : (Ljava/awt/datatransfer/DataFlavor;Ljava/awt/datatransfer/Transferable;Ljava/lang/String;)Ljava/lang/String;
    //   122: astore #7
    //   124: aload_0
    //   125: aload #7
    //   127: lload_3
    //   128: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   131: areturn
    //   132: aload_2
    //   133: invokevirtual isRepresentationClassReader : ()Z
    //   136: ifeq -> 306
    //   139: aload_2
    //   140: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   143: ifeq -> 154
    //   146: aload_0
    //   147: lload_3
    //   148: invokevirtual isTextFormat : (J)Z
    //   151: ifne -> 165
    //   154: new java/io/IOException
    //   157: dup
    //   158: ldc_w 'cannot transfer non-text data as Reader'
    //   161: invokespecial <init> : (Ljava/lang/String;)V
    //   164: athrow
    //   165: new java/lang/StringBuffer
    //   168: dup
    //   169: invokespecial <init> : ()V
    //   172: astore #7
    //   174: aload #5
    //   176: checkcast java/io/Reader
    //   179: astore #8
    //   181: aconst_null
    //   182: astore #9
    //   184: aload #8
    //   186: invokevirtual read : ()I
    //   189: dup
    //   190: istore #10
    //   192: iconst_m1
    //   193: if_icmpeq -> 208
    //   196: aload #7
    //   198: iload #10
    //   200: i2c
    //   201: invokevirtual append : (C)Ljava/lang/StringBuffer;
    //   204: pop
    //   205: goto -> 184
    //   208: aload #8
    //   210: ifnull -> 295
    //   213: aload #9
    //   215: ifnull -> 238
    //   218: aload #8
    //   220: invokevirtual close : ()V
    //   223: goto -> 295
    //   226: astore #10
    //   228: aload #9
    //   230: aload #10
    //   232: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   235: goto -> 295
    //   238: aload #8
    //   240: invokevirtual close : ()V
    //   243: goto -> 295
    //   246: astore #10
    //   248: aload #10
    //   250: astore #9
    //   252: aload #10
    //   254: athrow
    //   255: astore #11
    //   257: aload #8
    //   259: ifnull -> 292
    //   262: aload #9
    //   264: ifnull -> 287
    //   267: aload #8
    //   269: invokevirtual close : ()V
    //   272: goto -> 292
    //   275: astore #12
    //   277: aload #9
    //   279: aload #12
    //   281: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   284: goto -> 292
    //   287: aload #8
    //   289: invokevirtual close : ()V
    //   292: aload #11
    //   294: athrow
    //   295: aload_0
    //   296: aload #7
    //   298: invokevirtual toString : ()Ljava/lang/String;
    //   301: lload_3
    //   302: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   305: areturn
    //   306: aload_2
    //   307: invokevirtual isRepresentationClassCharBuffer : ()Z
    //   310: ifeq -> 385
    //   313: aload_2
    //   314: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   317: ifeq -> 328
    //   320: aload_0
    //   321: lload_3
    //   322: invokevirtual isTextFormat : (J)Z
    //   325: ifne -> 339
    //   328: new java/io/IOException
    //   331: dup
    //   332: ldc_w 'cannot transfer non-text data as CharBuffer'
    //   335: invokespecial <init> : (Ljava/lang/String;)V
    //   338: athrow
    //   339: aload #5
    //   341: checkcast java/nio/CharBuffer
    //   344: astore #7
    //   346: aload #7
    //   348: invokevirtual remaining : ()I
    //   351: istore #8
    //   353: iload #8
    //   355: newarray char
    //   357: astore #9
    //   359: aload #7
    //   361: aload #9
    //   363: iconst_0
    //   364: iload #8
    //   366: invokevirtual get : ([CII)Ljava/nio/CharBuffer;
    //   369: pop
    //   370: aload_0
    //   371: new java/lang/String
    //   374: dup
    //   375: aload #9
    //   377: invokespecial <init> : ([C)V
    //   380: lload_3
    //   381: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   384: areturn
    //   385: ldc [C
    //   387: aload_2
    //   388: invokevirtual getRepresentationClass : ()Ljava/lang/Class;
    //   391: invokevirtual equals : (Ljava/lang/Object;)Z
    //   394: ifeq -> 444
    //   397: aload_2
    //   398: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   401: ifeq -> 412
    //   404: aload_0
    //   405: lload_3
    //   406: invokevirtual isTextFormat : (J)Z
    //   409: ifne -> 423
    //   412: new java/io/IOException
    //   415: dup
    //   416: ldc_w 'cannot transfer non-text data as char array'
    //   419: invokespecial <init> : (Ljava/lang/String;)V
    //   422: athrow
    //   423: aload_0
    //   424: new java/lang/String
    //   427: dup
    //   428: aload #5
    //   430: checkcast [C
    //   433: checkcast [C
    //   436: invokespecial <init> : ([C)V
    //   439: lload_3
    //   440: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   443: areturn
    //   444: aload_2
    //   445: invokevirtual isRepresentationClassByteBuffer : ()Z
    //   448: ifeq -> 523
    //   451: aload #5
    //   453: checkcast java/nio/ByteBuffer
    //   456: astore #7
    //   458: aload #7
    //   460: invokevirtual remaining : ()I
    //   463: istore #8
    //   465: iload #8
    //   467: newarray byte
    //   469: astore #9
    //   471: aload #7
    //   473: aload #9
    //   475: iconst_0
    //   476: iload #8
    //   478: invokevirtual get : ([BII)Ljava/nio/ByteBuffer;
    //   481: pop
    //   482: aload_2
    //   483: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   486: ifeq -> 520
    //   489: aload_0
    //   490: lload_3
    //   491: invokevirtual isTextFormat : (J)Z
    //   494: ifeq -> 520
    //   497: aload_2
    //   498: invokestatic getTextCharset : (Ljava/awt/datatransfer/DataFlavor;)Ljava/lang/String;
    //   501: astore #10
    //   503: aload_0
    //   504: new java/lang/String
    //   507: dup
    //   508: aload #9
    //   510: aload #10
    //   512: invokespecial <init> : ([BLjava/lang/String;)V
    //   515: lload_3
    //   516: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   519: areturn
    //   520: aload #9
    //   522: areturn
    //   523: ldc [B
    //   525: aload_2
    //   526: invokevirtual getRepresentationClass : ()Ljava/lang/Class;
    //   529: invokevirtual equals : (Ljava/lang/Object;)Z
    //   532: ifeq -> 586
    //   535: aload #5
    //   537: checkcast [B
    //   540: checkcast [B
    //   543: astore #7
    //   545: aload_2
    //   546: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   549: ifeq -> 583
    //   552: aload_0
    //   553: lload_3
    //   554: invokevirtual isTextFormat : (J)Z
    //   557: ifeq -> 583
    //   560: aload_2
    //   561: invokestatic getTextCharset : (Ljava/awt/datatransfer/DataFlavor;)Ljava/lang/String;
    //   564: astore #8
    //   566: aload_0
    //   567: new java/lang/String
    //   570: dup
    //   571: aload #7
    //   573: aload #8
    //   575: invokespecial <init> : ([BLjava/lang/String;)V
    //   578: lload_3
    //   579: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   582: areturn
    //   583: aload #7
    //   585: areturn
    //   586: getstatic java/awt/datatransfer/DataFlavor.imageFlavor : Ljava/awt/datatransfer/DataFlavor;
    //   589: aload_2
    //   590: invokevirtual equals : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   593: ifeq -> 650
    //   596: aload_0
    //   597: lload_3
    //   598: invokevirtual isImageFormat : (J)Z
    //   601: ifne -> 615
    //   604: new java/io/IOException
    //   607: dup
    //   608: ldc_w 'Data translation failed: not an image format'
    //   611: invokespecial <init> : (Ljava/lang/String;)V
    //   614: athrow
    //   615: aload #5
    //   617: checkcast java/awt/Image
    //   620: astore #7
    //   622: aload_0
    //   623: aload #7
    //   625: lload_3
    //   626: invokevirtual imageToPlatformBytes : (Ljava/awt/Image;J)[B
    //   629: astore #8
    //   631: aload #8
    //   633: ifnonnull -> 647
    //   636: new java/io/IOException
    //   639: dup
    //   640: ldc_w 'Data translation failed: cannot convert java image to native format'
    //   643: invokespecial <init> : (Ljava/lang/String;)V
    //   646: athrow
    //   647: aload #8
    //   649: areturn
    //   650: aconst_null
    //   651: astore #7
    //   653: aload_0
    //   654: lload_3
    //   655: invokevirtual isFileFormat : (J)Z
    //   658: ifeq -> 813
    //   661: getstatic java/awt/datatransfer/DataFlavor.javaFileListFlavor : Ljava/awt/datatransfer/DataFlavor;
    //   664: aload_2
    //   665: invokevirtual equals : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   668: ifne -> 682
    //   671: new java/io/IOException
    //   674: dup
    //   675: ldc_w 'data translation failed'
    //   678: invokespecial <init> : (Ljava/lang/String;)V
    //   681: athrow
    //   682: aload #5
    //   684: checkcast java/util/List
    //   687: astore #8
    //   689: aload_1
    //   690: invokestatic getUserProtectionDomain : (Ljava/awt/datatransfer/Transferable;)Ljava/security/ProtectionDomain;
    //   693: astore #9
    //   695: aload_0
    //   696: aload #8
    //   698: aload #9
    //   700: invokespecial castToFiles : (Ljava/util/List;Ljava/security/ProtectionDomain;)Ljava/util/ArrayList;
    //   703: astore #10
    //   705: aload_0
    //   706: aload #10
    //   708: invokevirtual convertFileListToBytes : (Ljava/util/ArrayList;)Ljava/io/ByteArrayOutputStream;
    //   711: astore #11
    //   713: aconst_null
    //   714: astore #12
    //   716: aload #11
    //   718: invokevirtual toByteArray : ()[B
    //   721: astore #7
    //   723: aload #11
    //   725: ifnull -> 810
    //   728: aload #12
    //   730: ifnull -> 753
    //   733: aload #11
    //   735: invokevirtual close : ()V
    //   738: goto -> 810
    //   741: astore #13
    //   743: aload #12
    //   745: aload #13
    //   747: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   750: goto -> 810
    //   753: aload #11
    //   755: invokevirtual close : ()V
    //   758: goto -> 810
    //   761: astore #13
    //   763: aload #13
    //   765: astore #12
    //   767: aload #13
    //   769: athrow
    //   770: astore #14
    //   772: aload #11
    //   774: ifnull -> 807
    //   777: aload #12
    //   779: ifnull -> 802
    //   782: aload #11
    //   784: invokevirtual close : ()V
    //   787: goto -> 807
    //   790: astore #15
    //   792: aload #12
    //   794: aload #15
    //   796: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   799: goto -> 807
    //   802: aload #11
    //   804: invokevirtual close : ()V
    //   807: aload #14
    //   809: athrow
    //   810: goto -> 1642
    //   813: aload_0
    //   814: lload_3
    //   815: invokevirtual isURIListFormat : (J)Z
    //   818: ifeq -> 1207
    //   821: getstatic java/awt/datatransfer/DataFlavor.javaFileListFlavor : Ljava/awt/datatransfer/DataFlavor;
    //   824: aload_2
    //   825: invokevirtual equals : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   828: ifne -> 842
    //   831: new java/io/IOException
    //   834: dup
    //   835: ldc_w 'data translation failed'
    //   838: invokespecial <init> : (Ljava/lang/String;)V
    //   841: athrow
    //   842: aload_0
    //   843: lload_3
    //   844: invokevirtual getNativeForFormat : (J)Ljava/lang/String;
    //   847: astore #8
    //   849: aconst_null
    //   850: astore #9
    //   852: aload #8
    //   854: ifnull -> 888
    //   857: new java/awt/datatransfer/DataFlavor
    //   860: dup
    //   861: aload #8
    //   863: invokespecial <init> : (Ljava/lang/String;)V
    //   866: ldc 'charset'
    //   868: invokevirtual getParameter : (Ljava/lang/String;)Ljava/lang/String;
    //   871: astore #9
    //   873: goto -> 888
    //   876: astore #10
    //   878: new java/io/IOException
    //   881: dup
    //   882: aload #10
    //   884: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   887: athrow
    //   888: aload #9
    //   890: ifnonnull -> 898
    //   893: ldc_w 'UTF-8'
    //   896: astore #9
    //   898: aload #5
    //   900: checkcast java/util/List
    //   903: astore #10
    //   905: aload_1
    //   906: invokestatic getUserProtectionDomain : (Ljava/awt/datatransfer/Transferable;)Ljava/security/ProtectionDomain;
    //   909: astore #11
    //   911: aload_0
    //   912: aload #10
    //   914: aload #11
    //   916: invokespecial castToFiles : (Ljava/util/List;Ljava/security/ProtectionDomain;)Ljava/util/ArrayList;
    //   919: astore #12
    //   921: new java/util/ArrayList
    //   924: dup
    //   925: aload #12
    //   927: invokevirtual size : ()I
    //   930: invokespecial <init> : (I)V
    //   933: astore #13
    //   935: aload #12
    //   937: invokevirtual iterator : ()Ljava/util/Iterator;
    //   940: astore #14
    //   942: aload #14
    //   944: invokeinterface hasNext : ()Z
    //   949: ifeq -> 1030
    //   952: aload #14
    //   954: invokeinterface next : ()Ljava/lang/Object;
    //   959: checkcast java/lang/String
    //   962: astore #15
    //   964: new java/io/File
    //   967: dup
    //   968: aload #15
    //   970: invokespecial <init> : (Ljava/lang/String;)V
    //   973: invokevirtual toURI : ()Ljava/net/URI;
    //   976: astore #16
    //   978: aload #13
    //   980: new java/net/URI
    //   983: dup
    //   984: aload #16
    //   986: invokevirtual getScheme : ()Ljava/lang/String;
    //   989: ldc_w ''
    //   992: aload #16
    //   994: invokevirtual getPath : ()Ljava/lang/String;
    //   997: aload #16
    //   999: invokevirtual getFragment : ()Ljava/lang/String;
    //   1002: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   1005: invokevirtual toString : ()Ljava/lang/String;
    //   1008: invokevirtual add : (Ljava/lang/Object;)Z
    //   1011: pop
    //   1012: goto -> 1027
    //   1015: astore #17
    //   1017: new java/io/IOException
    //   1020: dup
    //   1021: aload #17
    //   1023: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   1026: athrow
    //   1027: goto -> 942
    //   1030: ldc_w '\\r\\n'
    //   1033: aload #9
    //   1035: invokevirtual getBytes : (Ljava/lang/String;)[B
    //   1038: astore #14
    //   1040: new java/io/ByteArrayOutputStream
    //   1043: dup
    //   1044: invokespecial <init> : ()V
    //   1047: astore #15
    //   1049: aconst_null
    //   1050: astore #16
    //   1052: iconst_0
    //   1053: istore #17
    //   1055: iload #17
    //   1057: aload #13
    //   1059: invokevirtual size : ()I
    //   1062: if_icmpge -> 1110
    //   1065: aload #13
    //   1067: iload #17
    //   1069: invokevirtual get : (I)Ljava/lang/Object;
    //   1072: checkcast java/lang/String
    //   1075: aload #9
    //   1077: invokevirtual getBytes : (Ljava/lang/String;)[B
    //   1080: astore #18
    //   1082: aload #15
    //   1084: aload #18
    //   1086: iconst_0
    //   1087: aload #18
    //   1089: arraylength
    //   1090: invokevirtual write : ([BII)V
    //   1093: aload #15
    //   1095: aload #14
    //   1097: iconst_0
    //   1098: aload #14
    //   1100: arraylength
    //   1101: invokevirtual write : ([BII)V
    //   1104: iinc #17, 1
    //   1107: goto -> 1055
    //   1110: aload #15
    //   1112: invokevirtual toByteArray : ()[B
    //   1115: astore #7
    //   1117: aload #15
    //   1119: ifnull -> 1204
    //   1122: aload #16
    //   1124: ifnull -> 1147
    //   1127: aload #15
    //   1129: invokevirtual close : ()V
    //   1132: goto -> 1204
    //   1135: astore #17
    //   1137: aload #16
    //   1139: aload #17
    //   1141: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1144: goto -> 1204
    //   1147: aload #15
    //   1149: invokevirtual close : ()V
    //   1152: goto -> 1204
    //   1155: astore #17
    //   1157: aload #17
    //   1159: astore #16
    //   1161: aload #17
    //   1163: athrow
    //   1164: astore #19
    //   1166: aload #15
    //   1168: ifnull -> 1201
    //   1171: aload #16
    //   1173: ifnull -> 1196
    //   1176: aload #15
    //   1178: invokevirtual close : ()V
    //   1181: goto -> 1201
    //   1184: astore #20
    //   1186: aload #16
    //   1188: aload #20
    //   1190: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1193: goto -> 1201
    //   1196: aload #15
    //   1198: invokevirtual close : ()V
    //   1201: aload #19
    //   1203: athrow
    //   1204: goto -> 1642
    //   1207: aload_2
    //   1208: invokevirtual isRepresentationClassInputStream : ()Z
    //   1211: ifeq -> 1590
    //   1214: aload #5
    //   1216: instanceof java/io/InputStream
    //   1219: ifne -> 1226
    //   1222: iconst_0
    //   1223: newarray byte
    //   1225: areturn
    //   1226: new java/io/ByteArrayOutputStream
    //   1229: dup
    //   1230: invokespecial <init> : ()V
    //   1233: astore #8
    //   1235: aconst_null
    //   1236: astore #9
    //   1238: aload #5
    //   1240: checkcast java/io/InputStream
    //   1243: astore #10
    //   1245: aconst_null
    //   1246: astore #11
    //   1248: iconst_0
    //   1249: istore #12
    //   1251: aload #10
    //   1253: invokevirtual available : ()I
    //   1256: istore #13
    //   1258: iload #13
    //   1260: sipush #8192
    //   1263: if_icmple -> 1271
    //   1266: iload #13
    //   1268: goto -> 1274
    //   1271: sipush #8192
    //   1274: newarray byte
    //   1276: astore #14
    //   1278: aload #10
    //   1280: aload #14
    //   1282: iconst_0
    //   1283: aload #14
    //   1285: arraylength
    //   1286: invokevirtual read : ([BII)I
    //   1289: dup
    //   1290: istore #15
    //   1292: iconst_m1
    //   1293: if_icmpne -> 1300
    //   1296: iconst_1
    //   1297: goto -> 1301
    //   1300: iconst_0
    //   1301: dup
    //   1302: istore #12
    //   1304: ifne -> 1317
    //   1307: aload #8
    //   1309: aload #14
    //   1311: iconst_0
    //   1312: iload #15
    //   1314: invokevirtual write : ([BII)V
    //   1317: iload #12
    //   1319: ifeq -> 1278
    //   1322: aload #10
    //   1324: ifnull -> 1409
    //   1327: aload #11
    //   1329: ifnull -> 1352
    //   1332: aload #10
    //   1334: invokevirtual close : ()V
    //   1337: goto -> 1409
    //   1340: astore #12
    //   1342: aload #11
    //   1344: aload #12
    //   1346: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1349: goto -> 1409
    //   1352: aload #10
    //   1354: invokevirtual close : ()V
    //   1357: goto -> 1409
    //   1360: astore #12
    //   1362: aload #12
    //   1364: astore #11
    //   1366: aload #12
    //   1368: athrow
    //   1369: astore #21
    //   1371: aload #10
    //   1373: ifnull -> 1406
    //   1376: aload #11
    //   1378: ifnull -> 1401
    //   1381: aload #10
    //   1383: invokevirtual close : ()V
    //   1386: goto -> 1406
    //   1389: astore #22
    //   1391: aload #11
    //   1393: aload #22
    //   1395: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1398: goto -> 1406
    //   1401: aload #10
    //   1403: invokevirtual close : ()V
    //   1406: aload #21
    //   1408: athrow
    //   1409: aload_2
    //   1410: invokestatic isFlavorCharsetTextType : (Ljava/awt/datatransfer/DataFlavor;)Z
    //   1413: ifeq -> 1493
    //   1416: aload_0
    //   1417: lload_3
    //   1418: invokevirtual isTextFormat : (J)Z
    //   1421: ifeq -> 1493
    //   1424: aload #8
    //   1426: invokevirtual toByteArray : ()[B
    //   1429: astore #10
    //   1431: aload_2
    //   1432: invokestatic getTextCharset : (Ljava/awt/datatransfer/DataFlavor;)Ljava/lang/String;
    //   1435: astore #11
    //   1437: aload_0
    //   1438: new java/lang/String
    //   1441: dup
    //   1442: aload #10
    //   1444: aload #11
    //   1446: invokespecial <init> : ([BLjava/lang/String;)V
    //   1449: lload_3
    //   1450: invokespecial translateTransferableString : (Ljava/lang/String;J)[B
    //   1453: astore #12
    //   1455: aload #8
    //   1457: ifnull -> 1490
    //   1460: aload #9
    //   1462: ifnull -> 1485
    //   1465: aload #8
    //   1467: invokevirtual close : ()V
    //   1470: goto -> 1490
    //   1473: astore #13
    //   1475: aload #9
    //   1477: aload #13
    //   1479: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1482: goto -> 1490
    //   1485: aload #8
    //   1487: invokevirtual close : ()V
    //   1490: aload #12
    //   1492: areturn
    //   1493: aload #8
    //   1495: invokevirtual toByteArray : ()[B
    //   1498: astore #7
    //   1500: aload #8
    //   1502: ifnull -> 1587
    //   1505: aload #9
    //   1507: ifnull -> 1530
    //   1510: aload #8
    //   1512: invokevirtual close : ()V
    //   1515: goto -> 1587
    //   1518: astore #10
    //   1520: aload #9
    //   1522: aload #10
    //   1524: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1527: goto -> 1587
    //   1530: aload #8
    //   1532: invokevirtual close : ()V
    //   1535: goto -> 1587
    //   1538: astore #10
    //   1540: aload #10
    //   1542: astore #9
    //   1544: aload #10
    //   1546: athrow
    //   1547: astore #23
    //   1549: aload #8
    //   1551: ifnull -> 1584
    //   1554: aload #9
    //   1556: ifnull -> 1579
    //   1559: aload #8
    //   1561: invokevirtual close : ()V
    //   1564: goto -> 1584
    //   1567: astore #24
    //   1569: aload #9
    //   1571: aload #24
    //   1573: invokevirtual addSuppressed : (Ljava/lang/Throwable;)V
    //   1576: goto -> 1584
    //   1579: aload #8
    //   1581: invokevirtual close : ()V
    //   1584: aload #23
    //   1586: athrow
    //   1587: goto -> 1642
    //   1590: aload_2
    //   1591: invokevirtual isRepresentationClassRemote : ()Z
    //   1594: ifeq -> 1614
    //   1597: aload #5
    //   1599: invokestatic newMarshalledObject : (Ljava/lang/Object;)Ljava/lang/Object;
    //   1602: astore #8
    //   1604: aload #8
    //   1606: invokestatic convertObjectToBytes : (Ljava/lang/Object;)[B
    //   1609: astore #7
    //   1611: goto -> 1642
    //   1614: aload_2
    //   1615: invokevirtual isRepresentationClassSerializable : ()Z
    //   1618: ifeq -> 1631
    //   1621: aload #5
    //   1623: invokestatic convertObjectToBytes : (Ljava/lang/Object;)[B
    //   1626: astore #7
    //   1628: goto -> 1642
    //   1631: new java/io/IOException
    //   1634: dup
    //   1635: ldc_w 'data translation failed'
    //   1638: invokespecial <init> : (Ljava/lang/String;)V
    //   1641: athrow
    //   1642: aload #7
    //   1644: areturn
    // Line number table:
    //   Java source line number -> byte code offset
    //   #1105	-> 0
    //   #1106	-> 9
    //   #1107	-> 14
    //   #1109	-> 16
    //   #1112	-> 34
    //   #1113	-> 45
    //   #1114	-> 50
    //   #1116	-> 52
    //   #1118	-> 58
    //   #1122	-> 61
    //   #1120	-> 64
    //   #1121	-> 66
    //   #1126	-> 79
    //   #1127	-> 87
    //   #1128	-> 97
    //   #1130	-> 111
    //   #1132	-> 124
    //   #1138	-> 132
    //   #1139	-> 139
    //   #1140	-> 154
    //   #1144	-> 165
    //   #1145	-> 174
    //   #1147	-> 184
    //   #1148	-> 196
    //   #1150	-> 208
    //   #1145	-> 246
    //   #1150	-> 255
    //   #1152	-> 295
    //   #1153	-> 298
    //   #1152	-> 302
    //   #1157	-> 306
    //   #1158	-> 313
    //   #1159	-> 328
    //   #1163	-> 339
    //   #1164	-> 346
    //   #1165	-> 353
    //   #1166	-> 359
    //   #1168	-> 370
    //   #1173	-> 385
    //   #1174	-> 397
    //   #1175	-> 412
    //   #1179	-> 423
    //   #1186	-> 444
    //   #1187	-> 451
    //   #1188	-> 458
    //   #1189	-> 465
    //   #1190	-> 471
    //   #1192	-> 482
    //   #1193	-> 497
    //   #1194	-> 503
    //   #1198	-> 520
    //   #1204	-> 523
    //   #1205	-> 535
    //   #1207	-> 545
    //   #1208	-> 560
    //   #1209	-> 566
    //   #1213	-> 583
    //   #1216	-> 586
    //   #1217	-> 596
    //   #1218	-> 604
    //   #1222	-> 615
    //   #1223	-> 622
    //   #1225	-> 631
    //   #1226	-> 636
    //   #1229	-> 647
    //   #1232	-> 650
    //   #1236	-> 653
    //   #1237	-> 661
    //   #1238	-> 671
    //   #1241	-> 682
    //   #1243	-> 689
    //   #1245	-> 695
    //   #1247	-> 705
    //   #1248	-> 716
    //   #1249	-> 723
    //   #1247	-> 761
    //   #1249	-> 770
    //   #1253	-> 810
    //   #1254	-> 821
    //   #1255	-> 831
    //   #1257	-> 842
    //   #1258	-> 849
    //   #1259	-> 852
    //   #1261	-> 857
    //   #1264	-> 873
    //   #1262	-> 876
    //   #1263	-> 878
    //   #1266	-> 888
    //   #1267	-> 893
    //   #1269	-> 898
    //   #1270	-> 905
    //   #1271	-> 911
    //   #1272	-> 921
    //   #1273	-> 935
    //   #1274	-> 964
    //   #1277	-> 978
    //   #1280	-> 1012
    //   #1278	-> 1015
    //   #1279	-> 1017
    //   #1281	-> 1027
    //   #1283	-> 1030
    //   #1285	-> 1040
    //   #1286	-> 1052
    //   #1287	-> 1065
    //   #1288	-> 1082
    //   #1289	-> 1093
    //   #1286	-> 1104
    //   #1291	-> 1110
    //   #1292	-> 1117
    //   #1285	-> 1155
    //   #1292	-> 1164
    //   #1297	-> 1204
    //   #1303	-> 1214
    //   #1304	-> 1222
    //   #1307	-> 1226
    //   #1308	-> 1238
    //   #1309	-> 1248
    //   #1310	-> 1251
    //   #1311	-> 1258
    //   #1314	-> 1278
    //   #1315	-> 1307
    //   #1317	-> 1317
    //   #1318	-> 1322
    //   #1308	-> 1360
    //   #1318	-> 1369
    //   #1320	-> 1409
    //   #1321	-> 1424
    //   #1322	-> 1431
    //   #1323	-> 1437
    //   #1328	-> 1455
    //   #1327	-> 1493
    //   #1328	-> 1500
    //   #1307	-> 1538
    //   #1328	-> 1547
    //   #1333	-> 1590
    //   #1335	-> 1597
    //   #1336	-> 1604
    //   #1339	-> 1611
    //   #1341	-> 1621
    //   #1344	-> 1631
    //   #1349	-> 1642
    // Exception table:
    //   from	to	target	type
    //   0	15	64	java/awt/datatransfer/UnsupportedFlavorException
    //   16	51	64	java/awt/datatransfer/UnsupportedFlavorException
    //   52	61	64	java/awt/datatransfer/UnsupportedFlavorException
    //   184	208	246	java/lang/Throwable
    //   184	208	255	finally
    //   218	223	226	java/lang/Throwable
    //   246	257	255	finally
    //   267	272	275	java/lang/Throwable
    //   716	723	761	java/lang/Throwable
    //   716	723	770	finally
    //   733	738	741	java/lang/Throwable
    //   761	772	770	finally
    //   782	787	790	java/lang/Throwable
    //   857	873	876	java/lang/ClassNotFoundException
    //   978	1012	1015	java/net/URISyntaxException
    //   1052	1117	1155	java/lang/Throwable
    //   1052	1117	1164	finally
    //   1127	1132	1135	java/lang/Throwable
    //   1155	1166	1164	finally
    //   1176	1181	1184	java/lang/Throwable
    //   1238	1455	1538	java/lang/Throwable
    //   1238	1455	1547	finally
    //   1248	1322	1360	java/lang/Throwable
    //   1248	1322	1369	finally
    //   1332	1337	1340	java/lang/Throwable
    //   1360	1371	1369	finally
    //   1381	1386	1389	java/lang/Throwable
    //   1465	1470	1473	java/lang/Throwable
    //   1493	1500	1538	java/lang/Throwable
    //   1493	1500	1547	finally
    //   1510	1515	1518	java/lang/Throwable
    //   1538	1549	1547	finally
    //   1559	1564	1567	java/lang/Throwable
  }
  
  private static byte[] convertObjectToBytes(Object paramObject) throws IOException {
    try(ByteArrayOutputStream null = new ByteArrayOutputStream(); ObjectOutputStream null = new ObjectOutputStream(byteArrayOutputStream)) {
      objectOutputStream.writeObject(paramObject);
      return byteArrayOutputStream.toByteArray();
    } 
  }
  
  private String removeSuspectedData(DataFlavor paramDataFlavor, Transferable paramTransferable, String paramString) throws IOException {
    if (null == System.getSecurityManager() || !paramDataFlavor.isMimeTypeEqual("text/uri-list"))
      return paramString; 
    String str = "";
    ProtectionDomain protectionDomain = getUserProtectionDomain(paramTransferable);
    try {
      str = AccessController.<String>doPrivileged((PrivilegedExceptionAction<String>)new Object(this, paramString, protectionDomain));
    } catch (PrivilegedActionException privilegedActionException) {
      throw new IOException(privilegedActionException.getMessage(), privilegedActionException);
    } 
    return str;
  }
  
  private static ProtectionDomain getUserProtectionDomain(Transferable paramTransferable) {
    return paramTransferable.getClass().getProtectionDomain();
  }
  
  private boolean isForbiddenToRead(File paramFile, ProtectionDomain paramProtectionDomain) {
    if (null == paramProtectionDomain)
      return false; 
    try {
      FilePermission filePermission = new FilePermission(paramFile.getCanonicalPath(), "read, delete");
      if (paramProtectionDomain.implies(filePermission))
        return false; 
    } catch (IOException iOException) {}
    return true;
  }
  
  private ArrayList<String> castToFiles(List paramList, ProtectionDomain paramProtectionDomain) throws IOException {
    ArrayList<String> arrayList = new ArrayList();
    try {
      AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Object(this, paramList, paramProtectionDomain, arrayList));
    } catch (PrivilegedActionException privilegedActionException) {
      throw new IOException(privilegedActionException.getMessage());
    } 
    return arrayList;
  }
  
  private File castToFile(Object paramObject) throws IOException {
    String str = null;
    if (paramObject instanceof File) {
      str = ((File)paramObject).getCanonicalPath();
    } else if (paramObject instanceof String) {
      str = (String)paramObject;
    } else {
      return null;
    } 
    return new File(str);
  }
  
  private static boolean isFileInWebstartedCache(File paramFile) {
    if (deploymentCacheDirectoryList.isEmpty())
      for (String str1 : DEPLOYMENT_CACHE_PROPERTIES) {
        String str2 = System.getProperty(str1);
        if (str2 != null)
          try {
            File file = (new File(str2)).getCanonicalFile();
            if (file != null)
              deploymentCacheDirectoryList.add(file); 
          } catch (IOException iOException) {} 
      }  
    for (File file1 : deploymentCacheDirectoryList) {
      for (File file2 = paramFile; file2 != null; file2 = file2.getParentFile()) {
        if (file2.equals(file1))
          return true; 
      } 
    } 
    return false;
  }
  
  public Object translateBytes(byte[] paramArrayOfbyte, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    Object object;
    List<File> list = null;
    if (isFileFormat(paramLong)) {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor))
        throw new IOException("data translation failed"); 
      String[] arrayOfString = dragQueryFile(paramArrayOfbyte);
      if (arrayOfString == null)
        return null; 
      File[] arrayOfFile = new File[arrayOfString.length];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfFile[b] = new File(arrayOfString[b]); 
      list = Arrays.asList(arrayOfFile);
    } else if (isURIListFormat(paramLong) && DataFlavor.javaFileListFlavor
      .equals(paramDataFlavor)) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte)) {
        URI[] arrayOfURI = dragQueryURIs(byteArrayInputStream, paramLong, paramTransferable);
        if (arrayOfURI == null)
          return null; 
        ArrayList<File> arrayList = new ArrayList();
        for (URI uRI : arrayOfURI) {
          try {
            arrayList.add(new File(uRI));
          } catch (IllegalArgumentException illegalArgumentException) {}
        } 
        list = arrayList;
      } 
    } else if (String.class.equals(paramDataFlavor.getRepresentationClass()) && 
      isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
      String str = translateBytesToString(paramArrayOfbyte, paramLong, paramTransferable);
    } else if (paramDataFlavor.isRepresentationClassReader()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte)) {
        object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (paramDataFlavor.isRepresentationClassCharBuffer()) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as CharBuffer"); 
      CharBuffer charBuffer = CharBuffer.wrap(
          translateBytesToString(paramArrayOfbyte, paramLong, paramTransferable));
      object = constructFlavoredObject(charBuffer, paramDataFlavor, CharBuffer.class);
    } else if (char[].class.equals(paramDataFlavor.getRepresentationClass())) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as char array"); 
      object = translateBytesToString(paramArrayOfbyte, paramLong, paramTransferable).toCharArray();
    } else if (paramDataFlavor.isRepresentationClassByteBuffer()) {
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
        paramArrayOfbyte = translateBytesToString(paramArrayOfbyte, paramLong, paramTransferable).getBytes(
            getTextCharset(paramDataFlavor)); 
      ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte);
      object = constructFlavoredObject(byteBuffer, paramDataFlavor, ByteBuffer.class);
    } else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
        object = translateBytesToString(paramArrayOfbyte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
      } else {
        object = paramArrayOfbyte;
      } 
    } else if (paramDataFlavor.isRepresentationClassInputStream()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte)) {
        object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (paramDataFlavor.isRepresentationClassRemote()) {
      try(ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte); 
          ObjectInputStream null = new ObjectInputStream(byteArrayInputStream)) {
        object = RMI.getMarshalledObject(objectInputStream.readObject());
      } catch (Exception exception) {
        throw new IOException(exception.getMessage());
      } 
    } else if (paramDataFlavor.isRepresentationClassSerializable()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte)) {
        object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
      if (!isImageFormat(paramLong))
        throw new IOException("data translation failed"); 
      object = platformImageBytesToImage(paramArrayOfbyte, paramLong);
    } 
    if (object == null)
      throw new IOException("data translation failed"); 
    return object;
  }
  
  public Object translateStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    Object object;
    ArrayList<File> arrayList = null;
    if (isURIListFormat(paramLong) && DataFlavor.javaFileListFlavor
      .equals(paramDataFlavor)) {
      URI[] arrayOfURI = dragQueryURIs(paramInputStream, paramLong, paramTransferable);
      if (arrayOfURI == null)
        return null; 
      ArrayList<File> arrayList1 = new ArrayList();
      for (URI uRI : arrayOfURI) {
        try {
          arrayList1.add(new File(uRI));
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
      arrayList = arrayList1;
    } else {
      if (String.class.equals(paramDataFlavor.getRepresentationClass()) && 
        isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
        return translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable); 
      if (DataFlavor.plainTextFlavor.equals(paramDataFlavor)) {
        StringReader stringReader = new StringReader(translateBytesToString(
              inputStreamToByteArray(paramInputStream), paramLong, paramTransferable));
      } else if (paramDataFlavor.isRepresentationClassInputStream()) {
        object = translateStreamToInputStream(paramInputStream, paramDataFlavor, paramLong, paramTransferable);
      } else if (paramDataFlavor.isRepresentationClassReader()) {
        if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
          throw new IOException("cannot transfer non-text data as Reader"); 
        InputStream inputStream = (InputStream)translateStreamToInputStream(paramInputStream, DataFlavor.plainTextFlavor, paramLong, paramTransferable);
        String str = getTextCharset(DataFlavor.plainTextFlavor);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, str);
        object = constructFlavoredObject(inputStreamReader, paramDataFlavor, Reader.class);
      } else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
        if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
          object = translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
        } else {
          object = inputStreamToByteArray(paramInputStream);
        } 
      } else if (paramDataFlavor.isRepresentationClassRemote()) {
        try (ObjectInputStream null = new ObjectInputStream(paramInputStream)) {
          object = RMI.getMarshalledObject(objectInputStream.readObject());
        } catch (Exception exception) {
          throw new IOException(exception.getMessage());
        } 
      } else if (paramDataFlavor.isRepresentationClassSerializable()) {
        try (ObjectInputStream null = new ObjectInputStream(paramInputStream)) {
          object = objectInputStream.readObject();
        } catch (Exception exception) {
          throw new IOException(exception.getMessage());
        } 
      } else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
        if (!isImageFormat(paramLong))
          throw new IOException("data translation failed"); 
        object = platformImageBytesToImage(inputStreamToByteArray(paramInputStream), paramLong);
      } 
    } 
    if (object == null)
      throw new IOException("data translation failed"); 
    return object;
  }
  
  private Object translateStreamToInputStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
      paramInputStream = new ReencodingInputStream(this, paramInputStream, paramLong, getTextCharset(paramDataFlavor), paramTransferable); 
    return constructFlavoredObject(paramInputStream, paramDataFlavor, InputStream.class);
  }
  
  private Object constructFlavoredObject(Object paramObject, DataFlavor paramDataFlavor, Class paramClass) throws IOException {
    Class<?> clazz = paramDataFlavor.getRepresentationClass();
    if (paramClass.equals(clazz))
      return paramObject; 
    Constructor[] arrayOfConstructor = null;
    try {
      arrayOfConstructor = AccessController.<Constructor[]>doPrivileged((PrivilegedAction<Constructor>)new Object(this, clazz));
    } catch (SecurityException securityException) {
      throw new IOException(securityException.getMessage());
    } 
    Constructor constructor = null;
    for (byte b = 0; b < arrayOfConstructor.length; b++) {
      if (Modifier.isPublic(arrayOfConstructor[b].getModifiers())) {
        Class[] arrayOfClass = arrayOfConstructor[b].getParameterTypes();
        if (arrayOfClass != null && arrayOfClass.length == 1 && paramClass
          .equals(arrayOfClass[0])) {
          constructor = arrayOfConstructor[b];
          break;
        } 
      } 
    } 
    if (constructor == null)
      throw new IOException("can't find <init>(L" + paramClass + ";)V for class: " + clazz
          .getName()); 
    try {
      return constructor.newInstance(new Object[] { paramObject });
    } catch (Exception exception) {
      throw new IOException(exception.getMessage());
    } 
  }
  
  protected URI[] dragQueryURIs(InputStream paramInputStream, long paramLong, Transferable paramTransferable) throws IOException {
    throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
  }
  
  protected Image standardImageBytesToImage(byte[] paramArrayOfbyte, String paramString) throws IOException {
    Iterator<ImageReader> iterator = ImageIO.getImageReadersByMIMEType(paramString);
    if (!iterator.hasNext())
      throw new IOException("No registered service provider can decode  an image from " + paramString); 
    IOException iOException = null;
    while (iterator.hasNext()) {
      ImageReader imageReader = iterator.next();
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfbyte)) {
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
        try {
          ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
          imageReader.setInput(imageInputStream, true, true);
          BufferedImage bufferedImage = imageReader.read(imageReader.getMinIndex(), imageReadParam);
          if (bufferedImage != null)
            return bufferedImage; 
        } finally {
          imageInputStream.close();
          imageReader.dispose();
        } 
      } catch (IOException iOException1) {
        iOException = iOException1;
      } 
    } 
    if (iOException == null)
      iOException = new IOException("Registered service providers failed to decode an image from " + paramString); 
    throw iOException;
  }
  
  protected byte[] imageToStandardBytes(Image paramImage, String paramString) throws IOException {
    IOException iOException = null;
    Iterator<ImageWriter> iterator = ImageIO.getImageWritersByMIMEType(paramString);
    if (!iterator.hasNext())
      throw new IOException("No registered service provider can encode  an image to " + paramString); 
    if (paramImage instanceof RenderedImage)
      try {
        return imageToStandardBytesImpl((RenderedImage)paramImage, paramString);
      } catch (IOException iOException1) {
        iOException = iOException1;
      }  
    int i = 0;
    int j = 0;
    if (paramImage instanceof ToolkitImage) {
      ImageRepresentation imageRepresentation = ((ToolkitImage)paramImage).getImageRep();
      imageRepresentation.reconstruct(32);
      i = imageRepresentation.getWidth();
      j = imageRepresentation.getHeight();
    } else {
      i = paramImage.getWidth(null);
      j = paramImage.getHeight(null);
    } 
    ColorModel colorModel = ColorModel.getRGBdefault();
    WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(i, j);
    BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
    Graphics graphics = bufferedImage.getGraphics();
    try {
      graphics.drawImage(paramImage, 0, 0, i, j, null);
    } finally {
      graphics.dispose();
    } 
    try {
      return imageToStandardBytesImpl(bufferedImage, paramString);
    } catch (IOException iOException1) {
      if (iOException != null)
        throw iOException; 
      throw iOException1;
    } 
  }
  
  protected byte[] imageToStandardBytesImpl(RenderedImage paramRenderedImage, String paramString) throws IOException {
    Iterator<ImageWriter> iterator = ImageIO.getImageWritersByMIMEType(paramString);
    ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    IOException iOException = null;
    while (iterator.hasNext()) {
      ImageWriter imageWriter = iterator.next();
      ImageWriterSpi imageWriterSpi = imageWriter.getOriginatingProvider();
      if (!imageWriterSpi.canEncodeImage(imageTypeSpecifier))
        continue; 
      try {
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
        try {
          imageWriter.setOutput(imageOutputStream);
          imageWriter.write(paramRenderedImage);
          imageOutputStream.flush();
        } finally {
          imageOutputStream.close();
        } 
      } catch (IOException iOException1) {
        imageWriter.dispose();
        byteArrayOutputStream.reset();
        iOException = iOException1;
        continue;
      } 
      imageWriter.dispose();
      byteArrayOutputStream.close();
      return byteArrayOutputStream.toByteArray();
    } 
    byteArrayOutputStream.close();
    if (iOException == null)
      iOException = new IOException("Registered service providers failed to encode " + paramRenderedImage + " to " + paramString); 
    throw iOException;
  }
  
  private Object concatData(Object paramObject1, Object paramObject2) {
    InputStream inputStream1 = null;
    InputStream inputStream2 = null;
    if (paramObject1 instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject1;
      if (paramObject2 instanceof byte[]) {
        byte[] arrayOfByte1 = (byte[])paramObject2;
        byte[] arrayOfByte2 = new byte[arrayOfByte.length + arrayOfByte1.length];
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, 0, arrayOfByte.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte.length, arrayOfByte1.length);
        return arrayOfByte2;
      } 
      inputStream1 = new ByteArrayInputStream(arrayOfByte);
      inputStream2 = (InputStream)paramObject2;
    } else {
      inputStream1 = (InputStream)paramObject1;
      if (paramObject2 instanceof byte[]) {
        inputStream2 = new ByteArrayInputStream((byte[])paramObject2);
      } else {
        inputStream2 = (InputStream)paramObject2;
      } 
    } 
    return new SequenceInputStream(inputStream1, inputStream2);
  }
  
  public byte[] convertData(Object paramObject, Transferable paramTransferable, long paramLong, Map paramMap, boolean paramBoolean) throws IOException {
    byte[] arrayOfByte = null;
    if (paramBoolean) {
      try {
        Stack<byte[]> stack = new Stack();
        Object object = new Object(this, paramMap, paramLong, paramTransferable, stack);
        AppContext appContext = SunToolkit.targetToAppContext(paramObject);
        getToolkitThreadBlockedHandler().lock();
        if (appContext != null)
          appContext.put("DATA_CONVERTER_KEY", object); 
        SunToolkit.executeOnEventHandlerThread(paramObject, (Runnable)object);
        while (stack.empty())
          getToolkitThreadBlockedHandler().enter(); 
        if (appContext != null)
          appContext.remove("DATA_CONVERTER_KEY"); 
        arrayOfByte = stack.pop();
      } finally {
        getToolkitThreadBlockedHandler().unlock();
      } 
    } else {
      DataFlavor dataFlavor = (DataFlavor)paramMap.get(Long.valueOf(paramLong));
      if (dataFlavor != null)
        arrayOfByte = translateTransferable(paramTransferable, dataFlavor, paramLong); 
    } 
    return arrayOfByte;
  }
  
  public void processDataConversionRequests() {
    if (EventQueue.isDispatchThread()) {
      AppContext appContext = AppContext.getAppContext();
      getToolkitThreadBlockedHandler().lock();
      try {
        Runnable runnable = (Runnable)appContext.get("DATA_CONVERTER_KEY");
        if (runnable != null) {
          runnable.run();
          appContext.remove("DATA_CONVERTER_KEY");
        } 
      } finally {
        getToolkitThreadBlockedHandler().unlock();
      } 
    } 
  }
  
  public static long[] keysToLongArray(SortedMap paramSortedMap) {
    Set set = paramSortedMap.keySet();
    long[] arrayOfLong = new long[set.size()];
    byte b = 0;
    for (Iterator iterator = set.iterator(); iterator.hasNext(); b++)
      arrayOfLong[b] = ((Long)iterator.next()).longValue(); 
    return arrayOfLong;
  }
  
  public static DataFlavor[] setToSortedDataFlavorArray(Set paramSet) {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[paramSet.size()];
    paramSet.toArray((Object[])arrayOfDataFlavor);
    DataFlavorComparator dataFlavorComparator = new DataFlavorComparator(false);
    Arrays.sort(arrayOfDataFlavor, (Comparator<? super DataFlavor>)dataFlavorComparator);
    return arrayOfDataFlavor;
  }
  
  protected static byte[] inputStreamToByteArray(InputStream paramInputStream) throws IOException {
    try (ByteArrayOutputStream null = new ByteArrayOutputStream()) {
      int i = 0;
      byte[] arrayOfByte = new byte[8192];
      while ((i = paramInputStream.read(arrayOfByte)) != -1)
        byteArrayOutputStream.write(arrayOfByte, 0, i); 
      return byteArrayOutputStream.toByteArray();
    } 
  }
  
  public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String paramString) {
    return new LinkedHashSet<>();
  }
  
  public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor paramDataFlavor) {
    return new LinkedHashSet<>();
  }
  
  public abstract String getDefaultUnicodeEncoding();
  
  public abstract boolean isLocaleDependentTextFormat(long paramLong);
  
  public abstract boolean isFileFormat(long paramLong);
  
  public abstract boolean isImageFormat(long paramLong);
  
  protected abstract Long getFormatForNativeAsLong(String paramString);
  
  protected abstract String getNativeForFormat(long paramLong);
  
  protected abstract ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList) throws IOException;
  
  protected abstract String[] dragQueryFile(byte[] paramArrayOfbyte);
  
  protected abstract Image platformImageBytesToImage(byte[] paramArrayOfbyte, long paramLong) throws IOException;
  
  protected abstract byte[] imageToPlatformBytes(Image paramImage, long paramLong) throws IOException;
  
  public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();
  
  public class DataTransferer {}
  
  public static abstract class IndexedComparator implements Comparator {
    public static final boolean SELECT_BEST = true;
    
    public static final boolean SELECT_WORST = false;
    
    protected final boolean order;
    
    public IndexedComparator() {
      this(true);
    }
    
    public IndexedComparator(boolean param1Boolean) {
      this.order = param1Boolean;
    }
    
    protected static int compareIndices(Map param1Map, Object param1Object1, Object param1Object2, Integer param1Integer) {
      Integer integer1 = (Integer)param1Map.get(param1Object1);
      Integer integer2 = (Integer)param1Map.get(param1Object2);
      if (integer1 == null)
        integer1 = param1Integer; 
      if (integer2 == null)
        integer2 = param1Integer; 
      return integer1.compareTo(integer2);
    }
    
    protected static int compareLongs(Map param1Map, Object param1Object1, Object param1Object2, Long param1Long) {
      Long long_1 = (Long)param1Map.get(param1Object1);
      Long long_2 = (Long)param1Map.get(param1Object2);
      if (long_1 == null)
        long_1 = param1Long; 
      if (long_2 == null)
        long_2 = param1Long; 
      return long_1.compareTo(long_2);
    }
  }
  
  public static class CharsetComparator extends IndexedComparator {
    private static final Map charsets;
    
    private static String defaultEncoding;
    
    private static final Integer DEFAULT_CHARSET_INDEX = Integer.valueOf(2);
    
    private static final Integer OTHER_CHARSET_INDEX = Integer.valueOf(1);
    
    private static final Integer WORST_CHARSET_INDEX = Integer.valueOf(0);
    
    private static final Integer UNSUPPORTED_CHARSET_INDEX = Integer.valueOf(-2147483648);
    
    private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
    
    static {
      HashMap<Object, Object> hashMap = new HashMap<>(8, 1.0F);
      hashMap.put(DataTransferer.canonicalName("UTF-16LE"), Integer.valueOf(4));
      hashMap.put(DataTransferer.canonicalName("UTF-16BE"), Integer.valueOf(5));
      hashMap.put(DataTransferer.canonicalName("UTF-8"), Integer.valueOf(6));
      hashMap.put(DataTransferer.canonicalName("UTF-16"), Integer.valueOf(7));
      hashMap.put(DataTransferer.canonicalName("US-ASCII"), WORST_CHARSET_INDEX);
      String str = DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
      if (hashMap.get(defaultEncoding) == null)
        hashMap.put(defaultEncoding, DEFAULT_CHARSET_INDEX); 
      hashMap.put("UNSUPPORTED", UNSUPPORTED_CHARSET_INDEX);
      charsets = Collections.unmodifiableMap(hashMap);
    }
    
    public CharsetComparator() {
      this(true);
    }
    
    public CharsetComparator(boolean param1Boolean) {
      super(param1Boolean);
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      String str1 = null;
      String str2 = null;
      if (this.order == true) {
        str1 = (String)param1Object1;
        str2 = (String)param1Object2;
      } else {
        str1 = (String)param1Object2;
        str2 = (String)param1Object1;
      } 
      return compareCharsets(str1, str2);
    }
    
    protected int compareCharsets(String param1String1, String param1String2) {
      param1String1 = getEncoding(param1String1);
      param1String2 = getEncoding(param1String2);
      int i = compareIndices(charsets, param1String1, param1String2, OTHER_CHARSET_INDEX);
      if (i == 0)
        return param1String2.compareTo(param1String1); 
      return i;
    }
    
    protected static String getEncoding(String param1String) {
      if (param1String == null)
        return null; 
      if (!DataTransferer.isEncodingSupported(param1String))
        return "UNSUPPORTED"; 
      String str = DataTransferer.canonicalName(param1String);
      return charsets.containsKey(str) ? str : param1String;
    }
  }
  
  public static class DataTransferer {}
  
  public static class IndexOrderComparator extends IndexedComparator {
    private final Map indexMap;
    
    private static final Integer FALLBACK_INDEX = Integer.valueOf(-2147483648);
    
    public IndexOrderComparator(Map param1Map) {
      super(true);
      this.indexMap = param1Map;
    }
    
    public IndexOrderComparator(Map param1Map, boolean param1Boolean) {
      super(param1Boolean);
      this.indexMap = param1Map;
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      if (!this.order)
        return -compareIndices(this.indexMap, param1Object1, param1Object2, FALLBACK_INDEX); 
      return compareIndices(this.indexMap, param1Object1, param1Object2, FALLBACK_INDEX);
    }
  }
  
  private static class DataTransferer {}
}
