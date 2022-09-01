package sun.font;

import java.awt.FontFormatException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import sun.java2d.Disposer;

public class Type1Font extends FileFont {
  WeakReference bufferRef = new WeakReference(null);
  
  private String psName = null;
  
  private static HashMap styleAbbreviationsMapping = new HashMap<>();
  
  private static HashSet styleNameTokes = new HashSet();
  
  private static final int PSEOFTOKEN = 0;
  
  private static final int PSNAMETOKEN = 1;
  
  private static final int PSSTRINGTOKEN = 2;
  
  static {
    String[] arrayOfString1 = { 
        "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Meduium", "Nord", "Poster", "Regular", 
        "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", 
        "Oblique", "Upright", "Sloped", "Semi", "Ultra", "Extra", "Alternate", "Alternate", "Deutsche Fraktur", "Expert", 
        "Inline", "Ornaments", "Outline", "Roman", "Rounded", "Script", "Shaded", "Swash", "Titling", "Typewriter" };
    String[] arrayOfString2 = { 
        "Blk", "Bd", "Bk", "Dm", "Hv", "Lt", "Md", "Nd", "Po", "Rg", 
        "Su", "Th", "Cm", "Cn", "Ct", "Ex", "Nr", "Ic", "It", "Ks", 
        "Obl", "Up", "Sl", "Sm", "Ult", "X", "A", "Alt", "Dfr", "Exp", 
        "In", "Or", "Ou", "Rm", "Rd", "Scr", "Sh", "Sw", "Ti", "Typ" };
    String[] arrayOfString3 = { 
        "Black", "Bold", "Book", "Demi", "Heavy", "Light", "Medium", "Nord", "Poster", "Regular", 
        "Super", "Thin", "Compressed", "Condensed", "Compact", "Extended", "Narrow", "Inclined", "Italic", "Kursiv", 
        "Oblique", "Upright", "Sloped", "Slanted", "Semi", "Ultra", "Extra" };
    byte b;
    for (b = 0; b < arrayOfString1.length; b++)
      styleAbbreviationsMapping.put(arrayOfString2[b], arrayOfString1[b]); 
    for (b = 0; b < arrayOfString3.length; b++)
      styleNameTokes.add(arrayOfString3[b]); 
  }
  
  public Type1Font(String paramString, Object paramObject) throws FontFormatException {
    this(paramString, paramObject, false);
  }
  
  public Type1Font(String paramString, Object paramObject, boolean paramBoolean) throws FontFormatException {
    super(paramString, paramObject);
    this.fontRank = 4;
    this.checkedNatives = true;
    try {
      verify();
    } catch (Throwable throwable) {
      if (paramBoolean) {
        T1DisposerRecord t1DisposerRecord = new T1DisposerRecord(paramString);
        Disposer.addObjectRecord(this.bufferRef, t1DisposerRecord);
        this.bufferRef = null;
      } 
      if (throwable instanceof FontFormatException)
        throw (FontFormatException)throwable; 
      throw new FontFormatException("Unexpected runtime exception.");
    } 
  }
  
  private synchronized ByteBuffer getBuffer() throws FontFormatException {
    MappedByteBuffer mappedByteBuffer = this.bufferRef.get();
    if (mappedByteBuffer == null)
      try {
        RandomAccessFile randomAccessFile = AccessController.<RandomAccessFile>doPrivileged((PrivilegedAction<RandomAccessFile>)new Object(this));
        FileChannel fileChannel = randomAccessFile.getChannel();
        this.fileSize = (int)fileChannel.size();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, this.fileSize);
        mappedByteBuffer.position(0);
        this.bufferRef = new WeakReference<>(mappedByteBuffer);
        fileChannel.close();
      } catch (NullPointerException nullPointerException) {
        throw new FontFormatException(nullPointerException.toString());
      } catch (ClosedChannelException closedChannelException) {
        Thread.interrupted();
        return getBuffer();
      } catch (IOException iOException) {
        throw new FontFormatException(iOException.toString());
      }  
    return mappedByteBuffer;
  }
  
  protected void close() {}
  
  void readFile(ByteBuffer paramByteBuffer) {
    RandomAccessFile randomAccessFile = null;
    try {
      randomAccessFile = AccessController.<RandomAccessFile>doPrivileged((PrivilegedAction<RandomAccessFile>)new Object(this));
      FileChannel fileChannel = randomAccessFile.getChannel();
      while (paramByteBuffer.remaining() > 0 && fileChannel.read(paramByteBuffer) != -1);
    } catch (NullPointerException nullPointerException) {
    
    } catch (ClosedChannelException closedChannelException) {
      try {
        if (randomAccessFile != null) {
          randomAccessFile.close();
          randomAccessFile = null;
        } 
      } catch (IOException iOException) {}
      Thread.interrupted();
      readFile(paramByteBuffer);
    } catch (IOException iOException) {
    
    } finally {
      if (randomAccessFile != null)
        try {
          randomAccessFile.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  public synchronized ByteBuffer readBlock(int paramInt1, int paramInt2) {
    ByteBuffer byteBuffer = null;
    try {
      byteBuffer = getBuffer();
      if (paramInt1 > this.fileSize)
        paramInt1 = this.fileSize; 
      byteBuffer.position(paramInt1);
      return byteBuffer.slice();
    } catch (FontFormatException fontFormatException) {
      return null;
    } 
  }
  
  private void verify() throws FontFormatException {
    ByteBuffer byteBuffer = getBuffer();
    if (byteBuffer.capacity() < 6)
      throw new FontFormatException("short file"); 
    int i = byteBuffer.get(0) & 0xFF;
    if ((byteBuffer.get(0) & 0xFF) == 128) {
      verifyPFB(byteBuffer);
      byteBuffer.position(6);
    } else {
      verifyPFA(byteBuffer);
      byteBuffer.position(0);
    } 
    initNames(byteBuffer);
    if (this.familyName == null || this.fullName == null)
      throw new FontFormatException("Font name not found"); 
    setStyle();
  }
  
  public int getFileSize() {
    if (this.fileSize == 0)
      try {
        getBuffer();
      } catch (FontFormatException fontFormatException) {} 
    return this.fileSize;
  }
  
  private void verifyPFA(ByteBuffer paramByteBuffer) throws FontFormatException {
    if (paramByteBuffer.getShort() != 9505)
      throw new FontFormatException("bad pfa font"); 
  }
  
  private void verifyPFB(ByteBuffer paramByteBuffer) throws FontFormatException {
    int i = 0;
    try {
      int j;
      while (true) {
        j = paramByteBuffer.getShort(i) & 0xFFFF;
        if (j == 32769 || j == 32770) {
          paramByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
          int k = paramByteBuffer.getInt(i + 2);
          paramByteBuffer.order(ByteOrder.BIG_ENDIAN);
          if (k <= 0)
            throw new FontFormatException("bad segment length"); 
          i += k + 6;
          continue;
        } 
        break;
      } 
      if (j == 32771)
        return; 
      throw new FontFormatException("bad pfb file");
    } catch (BufferUnderflowException bufferUnderflowException) {
      throw new FontFormatException(bufferUnderflowException.toString());
    } catch (Exception exception) {
      throw new FontFormatException(exception.toString());
    } 
  }
  
  private void initNames(ByteBuffer paramByteBuffer) throws FontFormatException {
    boolean bool = false;
    String str = null;
    try {
      while ((this.fullName == null || this.familyName == null || this.psName == null || str == null) && !bool) {
        int i = nextTokenType(paramByteBuffer);
        if (i == 1) {
          int j = paramByteBuffer.position();
          if (paramByteBuffer.get(j) == 70) {
            String str1 = getSimpleToken(paramByteBuffer);
            if ("FullName".equals(str1)) {
              if (nextTokenType(paramByteBuffer) == 2)
                this.fullName = getString(paramByteBuffer); 
              continue;
            } 
            if ("FamilyName".equals(str1)) {
              if (nextTokenType(paramByteBuffer) == 2)
                this.familyName = getString(paramByteBuffer); 
              continue;
            } 
            if ("FontName".equals(str1)) {
              if (nextTokenType(paramByteBuffer) == 1)
                this.psName = getSimpleToken(paramByteBuffer); 
              continue;
            } 
            if ("FontType".equals(str1)) {
              String str2 = getSimpleToken(paramByteBuffer);
              if ("def".equals(getSimpleToken(paramByteBuffer)))
                str = str2; 
            } 
            continue;
          } 
          while (paramByteBuffer.get() > 32);
          continue;
        } 
        if (i == 0)
          bool = true; 
      } 
    } catch (Exception exception) {
      throw new FontFormatException(exception.toString());
    } 
    if (!"1".equals(str))
      throw new FontFormatException("Unsupported font type"); 
    if (this.psName == null) {
      paramByteBuffer.position(0);
      if (paramByteBuffer.getShort() != 9505)
        paramByteBuffer.position(8); 
      String str1 = getSimpleToken(paramByteBuffer);
      if (!str1.startsWith("FontType1-") && !str1.startsWith("PS-AdobeFont-"))
        throw new FontFormatException("Unsupported font format [" + str1 + "]"); 
      this.psName = getSimpleToken(paramByteBuffer);
    } 
    if (bool)
      if (this.fullName != null) {
        this.familyName = fullName2FamilyName(this.fullName);
      } else if (this.familyName != null) {
        this.fullName = this.familyName;
      } else {
        this.fullName = psName2FullName(this.psName);
        this.familyName = psName2FamilyName(this.psName);
      }  
  }
  
  private String fullName2FamilyName(String paramString) {
    int i = paramString.length();
    while (i > 0) {
      int j = i - 1;
      while (j > 0 && paramString.charAt(j) != ' ')
        j--; 
      if (!isStyleToken(paramString.substring(j + 1, i)))
        return paramString.substring(0, i); 
      i = j;
    } 
    return paramString;
  }
  
  private String expandAbbreviation(String paramString) {
    if (styleAbbreviationsMapping.containsKey(paramString))
      return (String)styleAbbreviationsMapping.get(paramString); 
    return paramString;
  }
  
  private boolean isStyleToken(String paramString) {
    return styleNameTokes.contains(paramString);
  }
  
  private String psName2FullName(String paramString) {
    String str;
    int i = paramString.indexOf("-");
    if (i >= 0) {
      str = expandName(paramString.substring(0, i), false);
      str = str + " " + expandName(paramString.substring(i + 1), true);
    } else {
      str = expandName(paramString, false);
    } 
    return str;
  }
  
  private String psName2FamilyName(String paramString) {
    String str = paramString;
    if (str.indexOf("-") > 0)
      str = str.substring(0, str.indexOf("-")); 
    return expandName(str, false);
  }
  
  private int nextCapitalLetter(String paramString, int paramInt) {
    for (; paramInt >= 0 && paramInt < paramString.length(); paramInt++) {
      if (paramString.charAt(paramInt) >= 'A' && paramString.charAt(paramInt) <= 'Z')
        return paramInt; 
    } 
    return -1;
  }
  
  private String expandName(String paramString, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer(paramString.length() + 10);
    int i = 0;
    while (i < paramString.length()) {
      int j = nextCapitalLetter(paramString, i + 1);
      if (j < 0)
        j = paramString.length(); 
      if (i != 0)
        stringBuffer.append(" "); 
      if (paramBoolean) {
        stringBuffer.append(expandAbbreviation(paramString.substring(i, j)));
      } else {
        stringBuffer.append(paramString.substring(i, j));
      } 
      i = j;
    } 
    return stringBuffer.toString();
  }
  
  private byte skip(ByteBuffer paramByteBuffer) {
    byte b = paramByteBuffer.get();
    label15: while (b == 37) {
      while (true) {
        b = paramByteBuffer.get();
        if (b != 13) {
          if (b == 10)
            continue label15; 
          continue;
        } 
        continue label15;
      } 
    } 
    while (b <= 32)
      b = paramByteBuffer.get(); 
    return b;
  }
  
  private int nextTokenType(ByteBuffer paramByteBuffer) {
    try {
      byte b = skip(paramByteBuffer);
      while (true) {
        if (b == 47)
          return 1; 
        if (b == 40)
          return 2; 
        if (b == 13 || b == 10) {
          b = skip(paramByteBuffer);
          continue;
        } 
        b = paramByteBuffer.get();
      } 
    } catch (BufferUnderflowException bufferUnderflowException) {
      return 0;
    } 
  }
  
  private String getSimpleToken(ByteBuffer paramByteBuffer) {
    while (paramByteBuffer.get() <= 32);
    int i = paramByteBuffer.position() - 1;
    while (paramByteBuffer.get() > 32);
    int j = paramByteBuffer.position();
    byte[] arrayOfByte = new byte[j - i - 1];
    paramByteBuffer.position(i);
    paramByteBuffer.get(arrayOfByte);
    try {
      return new String(arrayOfByte, "US-ASCII");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return new String(arrayOfByte);
    } 
  }
  
  private String getString(ByteBuffer paramByteBuffer) {
    int i = paramByteBuffer.position();
    while (paramByteBuffer.get() != 41);
    int j = paramByteBuffer.position();
    byte[] arrayOfByte = new byte[j - i - 1];
    paramByteBuffer.position(i);
    paramByteBuffer.get(arrayOfByte);
    try {
      return new String(arrayOfByte, "US-ASCII");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return new String(arrayOfByte);
    } 
  }
  
  public String getPostscriptName() {
    return this.psName;
  }
  
  protected synchronized FontScaler getScaler() {
    if (this.scaler == null)
      this.scaler = FontScaler.getScaler((Font2D)this, 0, false, this.fileSize); 
    return this.scaler;
  }
  
  CharToGlyphMapper getMapper() {
    if (this.mapper == null)
      this.mapper = new Type1GlyphMapper(this); 
    return this.mapper;
  }
  
  public int getNumGlyphs() {
    try {
      return getScaler().getNumGlyphs();
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getNumGlyphs();
    } 
  }
  
  public int getMissingGlyphCode() {
    try {
      return getScaler().getMissingGlyphCode();
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getMissingGlyphCode();
    } 
  }
  
  public int getGlyphCode(char paramChar) {
    try {
      return getScaler().getGlyphCode(paramChar);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphCode(paramChar);
    } 
  }
  
  public String toString() {
    return "** Type1 Font: Family=" + this.familyName + " Name=" + this.fullName + " style=" + this.style + " fileName=" + 
      getPublicFileName();
  }
  
  private static class Type1Font {}
}
