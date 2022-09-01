package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class PNGImageReader extends ImageReader {
  static final int IHDR_TYPE = 1229472850;
  
  static final int PLTE_TYPE = 1347179589;
  
  static final int IDAT_TYPE = 1229209940;
  
  static final int IEND_TYPE = 1229278788;
  
  static final int bKGD_TYPE = 1649100612;
  
  static final int cHRM_TYPE = 1665684045;
  
  static final int gAMA_TYPE = 1732332865;
  
  static final int hIST_TYPE = 1749635924;
  
  static final int iCCP_TYPE = 1766015824;
  
  static final int iTXt_TYPE = 1767135348;
  
  static final int pHYs_TYPE = 1883789683;
  
  static final int sBIT_TYPE = 1933723988;
  
  static final int sPLT_TYPE = 1934642260;
  
  static final int sRGB_TYPE = 1934772034;
  
  static final int tEXt_TYPE = 1950701684;
  
  static final int tIME_TYPE = 1950960965;
  
  static final int tRNS_TYPE = 1951551059;
  
  static final int zTXt_TYPE = 2052348020;
  
  static final int PNG_COLOR_GRAY = 0;
  
  static final int PNG_COLOR_RGB = 2;
  
  static final int PNG_COLOR_PALETTE = 3;
  
  static final int PNG_COLOR_GRAY_ALPHA = 4;
  
  static final int PNG_COLOR_RGB_ALPHA = 6;
  
  static final int[] inputBandsForColorType = new int[] { 1, -1, 3, 1, 2, -1, 4 };
  
  static final int PNG_FILTER_NONE = 0;
  
  static final int PNG_FILTER_SUB = 1;
  
  static final int PNG_FILTER_UP = 2;
  
  static final int PNG_FILTER_AVERAGE = 3;
  
  static final int PNG_FILTER_PAETH = 4;
  
  static final int[] adam7XOffset = new int[] { 0, 4, 0, 2, 0, 1, 0 };
  
  static final int[] adam7YOffset = new int[] { 0, 0, 4, 0, 2, 0, 1 };
  
  static final int[] adam7XSubsampling = new int[] { 8, 8, 4, 4, 2, 2, 1, 1 };
  
  static final int[] adam7YSubsampling = new int[] { 8, 8, 8, 4, 4, 2, 2, 1 };
  
  private static final boolean debug = true;
  
  ImageInputStream stream = null;
  
  boolean gotHeader = false;
  
  boolean gotMetadata = false;
  
  ImageReadParam lastParam = null;
  
  long imageStartPosition = -1L;
  
  Rectangle sourceRegion = null;
  
  int sourceXSubsampling = -1;
  
  int sourceYSubsampling = -1;
  
  int sourceMinProgressivePass = 0;
  
  int sourceMaxProgressivePass = 6;
  
  int[] sourceBands = null;
  
  int[] destinationBands = null;
  
  Point destinationOffset = new Point(0, 0);
  
  PNGMetadata metadata = new PNGMetadata();
  
  DataInputStream pixelStream = null;
  
  BufferedImage theImage = null;
  
  int pixelsDone = 0;
  
  int totalPixels;
  
  public PNGImageReader(ImageReaderSpi paramImageReaderSpi) {
    super(paramImageReaderSpi);
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    this.stream = (ImageInputStream)paramObject;
    resetStreamSettings();
  }
  
  private String readNullTerminatedString(String paramString, int paramInt) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte b = 0;
    int i;
    while (paramInt > b++ && (i = this.stream.read()) != 0) {
      if (i == -1)
        throw new EOFException(); 
      byteArrayOutputStream.write(i);
    } 
    return new String(byteArrayOutputStream.toByteArray(), paramString);
  }
  
  private void readHeader() throws IIOException {
    if (this.gotHeader)
      return; 
    if (this.stream == null)
      throw new IllegalStateException("Input source not set!"); 
    try {
      byte[] arrayOfByte = new byte[8];
      this.stream.readFully(arrayOfByte);
      if (arrayOfByte[0] != -119 || arrayOfByte[1] != 80 || arrayOfByte[2] != 78 || arrayOfByte[3] != 71 || arrayOfByte[4] != 13 || arrayOfByte[5] != 10 || arrayOfByte[6] != 26 || arrayOfByte[7] != 10)
        throw new IIOException("Bad PNG signature!"); 
      int i = this.stream.readInt();
      if (i != 13)
        throw new IIOException("Bad length for IHDR chunk!"); 
      int j = this.stream.readInt();
      if (j != 1229472850)
        throw new IIOException("Bad type for IHDR chunk!"); 
      this.metadata = new PNGMetadata();
      int k = this.stream.readInt();
      int m = this.stream.readInt();
      this.stream.readFully(arrayOfByte, 0, 5);
      int n = arrayOfByte[0] & 0xFF;
      int i1 = arrayOfByte[1] & 0xFF;
      int i2 = arrayOfByte[2] & 0xFF;
      int i3 = arrayOfByte[3] & 0xFF;
      int i4 = arrayOfByte[4] & 0xFF;
      this.stream.skipBytes(4);
      this.stream.flushBefore(this.stream.getStreamPosition());
      if (k == 0)
        throw new IIOException("Image width == 0!"); 
      if (m == 0)
        throw new IIOException("Image height == 0!"); 
      if (n != 1 && n != 2 && n != 4 && n != 8 && n != 16)
        throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!"); 
      if (i1 != 0 && i1 != 2 && i1 != 3 && i1 != 4 && i1 != 6)
        throw new IIOException("Color type must be 0, 2, 3, 4, or 6!"); 
      if (i1 == 3 && n == 16)
        throw new IIOException("Bad color type/bit depth combination!"); 
      if ((i1 == 2 || i1 == 6 || i1 == 4) && n != 8 && n != 16)
        throw new IIOException("Bad color type/bit depth combination!"); 
      if (i2 != 0)
        throw new IIOException("Unknown compression method (not 0)!"); 
      if (i3 != 0)
        throw new IIOException("Unknown filter method (not 0)!"); 
      if (i4 != 0 && i4 != 1)
        throw new IIOException("Unknown interlace method (not 0 or 1)!"); 
      this.metadata.IHDR_present = true;
      this.metadata.IHDR_width = k;
      this.metadata.IHDR_height = m;
      this.metadata.IHDR_bitDepth = n;
      this.metadata.IHDR_colorType = i1;
      this.metadata.IHDR_compressionMethod = i2;
      this.metadata.IHDR_filterMethod = i3;
      this.metadata.IHDR_interlaceMethod = i4;
      this.gotHeader = true;
    } catch (IOException iOException) {
      throw new IIOException("I/O error reading PNG header!", iOException);
    } 
  }
  
  private void parse_PLTE_chunk(int paramInt) throws IOException {
    byte b1;
    if (this.metadata.PLTE_present) {
      processWarningOccurred("A PNG image may not contain more than one PLTE chunk.\nThe chunk wil be ignored.");
      return;
    } 
    if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
      processWarningOccurred("A PNG gray or gray alpha image cannot have a PLTE chunk.\nThe chunk wil be ignored.");
      return;
    } 
    byte[] arrayOfByte = new byte[paramInt];
    this.stream.readFully(arrayOfByte);
    int i = paramInt / 3;
    if (this.metadata.IHDR_colorType == 3) {
      b1 = 1 << this.metadata.IHDR_bitDepth;
      if (i > b1) {
        processWarningOccurred("PLTE chunk contains too many entries for bit depth, ignoring extras.");
        i = b1;
      } 
      i = Math.min(i, b1);
    } 
    if (i > 16) {
      b1 = 256;
    } else if (i > 4) {
      b1 = 16;
    } else if (i > 2) {
      b1 = 4;
    } else {
      b1 = 2;
    } 
    this.metadata.PLTE_present = true;
    this.metadata.PLTE_red = new byte[b1];
    this.metadata.PLTE_green = new byte[b1];
    this.metadata.PLTE_blue = new byte[b1];
    byte b2 = 0;
    for (byte b3 = 0; b3 < i; b3++) {
      this.metadata.PLTE_red[b3] = arrayOfByte[b2++];
      this.metadata.PLTE_green[b3] = arrayOfByte[b2++];
      this.metadata.PLTE_blue[b3] = arrayOfByte[b2++];
    } 
  }
  
  private void parse_bKGD_chunk() throws IOException {
    if (this.metadata.IHDR_colorType == 3) {
      this.metadata.bKGD_colorType = 3;
      this.metadata.bKGD_index = this.stream.readUnsignedByte();
    } else if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
      this.metadata.bKGD_colorType = 0;
      this.metadata.bKGD_gray = this.stream.readUnsignedShort();
    } else {
      this.metadata.bKGD_colorType = 2;
      this.metadata.bKGD_red = this.stream.readUnsignedShort();
      this.metadata.bKGD_green = this.stream.readUnsignedShort();
      this.metadata.bKGD_blue = this.stream.readUnsignedShort();
    } 
    this.metadata.bKGD_present = true;
  }
  
  private void parse_cHRM_chunk() throws IOException {
    this.metadata.cHRM_whitePointX = this.stream.readInt();
    this.metadata.cHRM_whitePointY = this.stream.readInt();
    this.metadata.cHRM_redX = this.stream.readInt();
    this.metadata.cHRM_redY = this.stream.readInt();
    this.metadata.cHRM_greenX = this.stream.readInt();
    this.metadata.cHRM_greenY = this.stream.readInt();
    this.metadata.cHRM_blueX = this.stream.readInt();
    this.metadata.cHRM_blueY = this.stream.readInt();
    this.metadata.cHRM_present = true;
  }
  
  private void parse_gAMA_chunk() throws IOException {
    int i = this.stream.readInt();
    this.metadata.gAMA_gamma = i;
    this.metadata.gAMA_present = true;
  }
  
  private void parse_hIST_chunk(int paramInt) throws IOException, IIOException {
    if (!this.metadata.PLTE_present)
      throw new IIOException("hIST chunk without prior PLTE chunk!"); 
    this.metadata.hIST_histogram = new char[paramInt / 2];
    this.stream.readFully(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
    this.metadata.hIST_present = true;
  }
  
  private void parse_iCCP_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.iCCP_profileName = str;
    this.metadata.iCCP_compressionMethod = this.stream.readUnsignedByte();
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    this.stream.readFully(arrayOfByte);
    this.metadata.iCCP_compressedProfile = arrayOfByte;
    this.metadata.iCCP_present = true;
  }
  
  private void parse_iTXt_chunk(int paramInt) throws IOException {
    String str4;
    long l1 = this.stream.getStreamPosition();
    String str1 = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.iTXt_keyword.add(str1);
    int i = this.stream.readUnsignedByte();
    this.metadata.iTXt_compressionFlag.add(Boolean.valueOf((i == 1)));
    int j = this.stream.readUnsignedByte();
    this.metadata.iTXt_compressionMethod.add(Integer.valueOf(j));
    String str2 = readNullTerminatedString("UTF8", 80);
    this.metadata.iTXt_languageTag.add(str2);
    long l2 = this.stream.getStreamPosition();
    int k = (int)(l1 + paramInt - l2);
    String str3 = readNullTerminatedString("UTF8", k);
    this.metadata.iTXt_translatedKeyword.add(str3);
    l2 = this.stream.getStreamPosition();
    byte[] arrayOfByte = new byte[(int)(l1 + paramInt - l2)];
    this.stream.readFully(arrayOfByte);
    if (i == 1) {
      str4 = new String(inflate(arrayOfByte), "UTF8");
    } else {
      str4 = new String(arrayOfByte, "UTF8");
    } 
    this.metadata.iTXt_text.add(str4);
  }
  
  private void parse_pHYs_chunk() throws IOException {
    this.metadata.pHYs_pixelsPerUnitXAxis = this.stream.readInt();
    this.metadata.pHYs_pixelsPerUnitYAxis = this.stream.readInt();
    this.metadata.pHYs_unitSpecifier = this.stream.readUnsignedByte();
    this.metadata.pHYs_present = true;
  }
  
  private void parse_sBIT_chunk() throws IOException {
    int i = this.metadata.IHDR_colorType;
    if (i == 0 || i == 4) {
      this.metadata.sBIT_grayBits = this.stream.readUnsignedByte();
    } else if (i == 2 || i == 3 || i == 6) {
      this.metadata.sBIT_redBits = this.stream.readUnsignedByte();
      this.metadata.sBIT_greenBits = this.stream.readUnsignedByte();
      this.metadata.sBIT_blueBits = this.stream.readUnsignedByte();
    } 
    if (i == 4 || i == 6)
      this.metadata.sBIT_alphaBits = this.stream.readUnsignedByte(); 
    this.metadata.sBIT_colorType = i;
    this.metadata.sBIT_present = true;
  }
  
  private void parse_sPLT_chunk(int paramInt) throws IOException, IIOException {
    this.metadata.sPLT_paletteName = readNullTerminatedString("ISO-8859-1", 80);
    paramInt -= this.metadata.sPLT_paletteName.length() + 1;
    int i = this.stream.readUnsignedByte();
    this.metadata.sPLT_sampleDepth = i;
    int j = paramInt / (4 * i / 8 + 2);
    this.metadata.sPLT_red = new int[j];
    this.metadata.sPLT_green = new int[j];
    this.metadata.sPLT_blue = new int[j];
    this.metadata.sPLT_alpha = new int[j];
    this.metadata.sPLT_frequency = new int[j];
    if (i == 8) {
      for (byte b = 0; b < j; b++) {
        this.metadata.sPLT_red[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_green[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_blue[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_alpha[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_frequency[b] = this.stream.readUnsignedShort();
      } 
    } else if (i == 16) {
      for (byte b = 0; b < j; b++) {
        this.metadata.sPLT_red[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_green[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_blue[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_alpha[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_frequency[b] = this.stream.readUnsignedShort();
      } 
    } else {
      throw new IIOException("sPLT sample depth not 8 or 16!");
    } 
    this.metadata.sPLT_present = true;
  }
  
  private void parse_sRGB_chunk() throws IOException {
    this.metadata.sRGB_renderingIntent = this.stream.readUnsignedByte();
    this.metadata.sRGB_present = true;
  }
  
  private void parse_tEXt_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.tEXt_keyword.add(str);
    byte[] arrayOfByte = new byte[paramInt - str.length() - 1];
    this.stream.readFully(arrayOfByte);
    this.metadata.tEXt_text.add(new String(arrayOfByte, "ISO-8859-1"));
  }
  
  private void parse_tIME_chunk() throws IOException {
    this.metadata.tIME_year = this.stream.readUnsignedShort();
    this.metadata.tIME_month = this.stream.readUnsignedByte();
    this.metadata.tIME_day = this.stream.readUnsignedByte();
    this.metadata.tIME_hour = this.stream.readUnsignedByte();
    this.metadata.tIME_minute = this.stream.readUnsignedByte();
    this.metadata.tIME_second = this.stream.readUnsignedByte();
    this.metadata.tIME_present = true;
  }
  
  private void parse_tRNS_chunk(int paramInt) throws IOException {
    int i = this.metadata.IHDR_colorType;
    if (i == 3) {
      if (!this.metadata.PLTE_present) {
        processWarningOccurred("tRNS chunk without prior PLTE chunk, ignoring it.");
        return;
      } 
      int j = this.metadata.PLTE_red.length;
      int k = paramInt;
      if (k > j) {
        processWarningOccurred("tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
        k = j;
      } 
      this.metadata.tRNS_alpha = new byte[k];
      this.metadata.tRNS_colorType = 3;
      this.stream.read(this.metadata.tRNS_alpha, 0, k);
      this.stream.skipBytes(paramInt - k);
    } else if (i == 0) {
      if (paramInt != 2) {
        processWarningOccurred("tRNS chunk for gray image must have length 2, ignoring chunk.");
        this.stream.skipBytes(paramInt);
        return;
      } 
      this.metadata.tRNS_gray = this.stream.readUnsignedShort();
      this.metadata.tRNS_colorType = 0;
    } else if (i == 2) {
      if (paramInt != 6) {
        processWarningOccurred("tRNS chunk for RGB image must have length 6, ignoring chunk.");
        this.stream.skipBytes(paramInt);
        return;
      } 
      this.metadata.tRNS_red = this.stream.readUnsignedShort();
      this.metadata.tRNS_green = this.stream.readUnsignedShort();
      this.metadata.tRNS_blue = this.stream.readUnsignedShort();
      this.metadata.tRNS_colorType = 2;
    } else {
      processWarningOccurred("Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
      return;
    } 
    this.metadata.tRNS_present = true;
  }
  
  private static byte[] inflate(byte[] paramArrayOfbyte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
    InflaterInputStream inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      int i;
      while ((i = inflaterInputStream.read()) != -1)
        byteArrayOutputStream.write(i); 
    } finally {
      inflaterInputStream.close();
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  private void parse_zTXt_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.zTXt_keyword.add(str);
    int i = this.stream.readUnsignedByte();
    this.metadata.zTXt_compressionMethod.add(new Integer(i));
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    this.stream.readFully(arrayOfByte);
    this.metadata.zTXt_text.add(new String(inflate(arrayOfByte), "ISO-8859-1"));
  }
  
  private void readMetadata() throws IIOException {
    // Byte code:
    //   0: aload_0
    //   1: getfield gotMetadata : Z
    //   4: ifeq -> 8
    //   7: return
    //   8: aload_0
    //   9: invokespecial readHeader : ()V
    //   12: aload_0
    //   13: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   16: getfield IHDR_colorType : I
    //   19: istore_1
    //   20: aload_0
    //   21: getfield ignoreMetadata : Z
    //   24: ifeq -> 124
    //   27: iload_1
    //   28: iconst_3
    //   29: if_icmpeq -> 124
    //   32: aload_0
    //   33: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   36: invokeinterface readInt : ()I
    //   41: istore_2
    //   42: aload_0
    //   43: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   46: invokeinterface readInt : ()I
    //   51: istore_3
    //   52: iload_3
    //   53: ldc 1229209940
    //   55: if_icmpne -> 86
    //   58: aload_0
    //   59: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   62: bipush #-8
    //   64: invokeinterface skipBytes : (I)I
    //   69: pop
    //   70: aload_0
    //   71: aload_0
    //   72: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   75: invokeinterface getStreamPosition : ()J
    //   80: putfield imageStartPosition : J
    //   83: goto -> 102
    //   86: aload_0
    //   87: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   90: iload_2
    //   91: iconst_4
    //   92: iadd
    //   93: invokeinterface skipBytes : (I)I
    //   98: pop
    //   99: goto -> 32
    //   102: goto -> 118
    //   105: astore_2
    //   106: new javax/imageio/IIOException
    //   109: dup
    //   110: ldc_w 'Error skipping PNG metadata'
    //   113: aload_2
    //   114: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
    //   117: athrow
    //   118: aload_0
    //   119: iconst_1
    //   120: putfield gotMetadata : Z
    //   123: return
    //   124: aload_0
    //   125: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   128: invokeinterface readInt : ()I
    //   133: istore_2
    //   134: aload_0
    //   135: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   138: invokeinterface readInt : ()I
    //   143: istore_3
    //   144: iload_2
    //   145: ifge -> 176
    //   148: new javax/imageio/IIOException
    //   151: dup
    //   152: new java/lang/StringBuilder
    //   155: dup
    //   156: invokespecial <init> : ()V
    //   159: ldc_w 'Invalid chunk lenght '
    //   162: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: iload_2
    //   166: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   169: invokevirtual toString : ()Ljava/lang/String;
    //   172: invokespecial <init> : (Ljava/lang/String;)V
    //   175: athrow
    //   176: aload_0
    //   177: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   180: invokeinterface mark : ()V
    //   185: aload_0
    //   186: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   189: aload_0
    //   190: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   193: invokeinterface getStreamPosition : ()J
    //   198: iload_2
    //   199: i2l
    //   200: ladd
    //   201: invokeinterface seek : (J)V
    //   206: aload_0
    //   207: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   210: invokeinterface readInt : ()I
    //   215: istore #4
    //   217: aload_0
    //   218: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   221: invokeinterface reset : ()V
    //   226: goto -> 259
    //   229: astore #5
    //   231: new javax/imageio/IIOException
    //   234: dup
    //   235: new java/lang/StringBuilder
    //   238: dup
    //   239: invokespecial <init> : ()V
    //   242: ldc_w 'Invalid chunk length '
    //   245: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: iload_2
    //   249: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   252: invokevirtual toString : ()Ljava/lang/String;
    //   255: invokespecial <init> : (Ljava/lang/String;)V
    //   258: athrow
    //   259: iload_3
    //   260: lookupswitch default -> 541, 1229209940 -> 400, 1347179589 -> 428, 1649100612 -> 436, 1665684045 -> 443, 1732332865 -> 450, 1749635924 -> 457, 1766015824 -> 465, 1767135348 -> 473, 1883789683 -> 481, 1933723988 -> 488, 1934642260 -> 495, 1934772034 -> 503, 1950701684 -> 510, 1950960965 -> 518, 1951551059 -> 525, 2052348020 -> 533
    //   400: aload_0
    //   401: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   404: bipush #-8
    //   406: invokeinterface skipBytes : (I)I
    //   411: pop
    //   412: aload_0
    //   413: aload_0
    //   414: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   417: invokeinterface getStreamPosition : ()J
    //   422: putfield imageStartPosition : J
    //   425: goto -> 730
    //   428: aload_0
    //   429: iload_2
    //   430: invokespecial parse_PLTE_chunk : (I)V
    //   433: goto -> 667
    //   436: aload_0
    //   437: invokespecial parse_bKGD_chunk : ()V
    //   440: goto -> 667
    //   443: aload_0
    //   444: invokespecial parse_cHRM_chunk : ()V
    //   447: goto -> 667
    //   450: aload_0
    //   451: invokespecial parse_gAMA_chunk : ()V
    //   454: goto -> 667
    //   457: aload_0
    //   458: iload_2
    //   459: invokespecial parse_hIST_chunk : (I)V
    //   462: goto -> 667
    //   465: aload_0
    //   466: iload_2
    //   467: invokespecial parse_iCCP_chunk : (I)V
    //   470: goto -> 667
    //   473: aload_0
    //   474: iload_2
    //   475: invokespecial parse_iTXt_chunk : (I)V
    //   478: goto -> 667
    //   481: aload_0
    //   482: invokespecial parse_pHYs_chunk : ()V
    //   485: goto -> 667
    //   488: aload_0
    //   489: invokespecial parse_sBIT_chunk : ()V
    //   492: goto -> 667
    //   495: aload_0
    //   496: iload_2
    //   497: invokespecial parse_sPLT_chunk : (I)V
    //   500: goto -> 667
    //   503: aload_0
    //   504: invokespecial parse_sRGB_chunk : ()V
    //   507: goto -> 667
    //   510: aload_0
    //   511: iload_2
    //   512: invokespecial parse_tEXt_chunk : (I)V
    //   515: goto -> 667
    //   518: aload_0
    //   519: invokespecial parse_tIME_chunk : ()V
    //   522: goto -> 667
    //   525: aload_0
    //   526: iload_2
    //   527: invokespecial parse_tRNS_chunk : (I)V
    //   530: goto -> 667
    //   533: aload_0
    //   534: iload_2
    //   535: invokespecial parse_zTXt_chunk : (I)V
    //   538: goto -> 667
    //   541: iload_2
    //   542: newarray byte
    //   544: astore #5
    //   546: aload_0
    //   547: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   550: aload #5
    //   552: invokeinterface readFully : ([B)V
    //   557: new java/lang/StringBuilder
    //   560: dup
    //   561: iconst_4
    //   562: invokespecial <init> : (I)V
    //   565: astore #6
    //   567: aload #6
    //   569: iload_3
    //   570: bipush #24
    //   572: iushr
    //   573: i2c
    //   574: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   577: pop
    //   578: aload #6
    //   580: iload_3
    //   581: bipush #16
    //   583: ishr
    //   584: sipush #255
    //   587: iand
    //   588: i2c
    //   589: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   592: pop
    //   593: aload #6
    //   595: iload_3
    //   596: bipush #8
    //   598: ishr
    //   599: sipush #255
    //   602: iand
    //   603: i2c
    //   604: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   607: pop
    //   608: aload #6
    //   610: iload_3
    //   611: sipush #255
    //   614: iand
    //   615: i2c
    //   616: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   619: pop
    //   620: iload_3
    //   621: bipush #28
    //   623: iushr
    //   624: istore #7
    //   626: iload #7
    //   628: ifne -> 638
    //   631: aload_0
    //   632: ldc_w 'Encountered unknown chunk with critical bit set!'
    //   635: invokevirtual processWarningOccurred : (Ljava/lang/String;)V
    //   638: aload_0
    //   639: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   642: getfield unknownChunkType : Ljava/util/ArrayList;
    //   645: aload #6
    //   647: invokevirtual toString : ()Ljava/lang/String;
    //   650: invokevirtual add : (Ljava/lang/Object;)Z
    //   653: pop
    //   654: aload_0
    //   655: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   658: getfield unknownChunkData : Ljava/util/ArrayList;
    //   661: aload #5
    //   663: invokevirtual add : (Ljava/lang/Object;)Z
    //   666: pop
    //   667: iload #4
    //   669: aload_0
    //   670: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   673: invokeinterface readInt : ()I
    //   678: if_icmpeq -> 709
    //   681: new javax/imageio/IIOException
    //   684: dup
    //   685: new java/lang/StringBuilder
    //   688: dup
    //   689: invokespecial <init> : ()V
    //   692: ldc_w 'Failed to read a chunk of type '
    //   695: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   698: iload_3
    //   699: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   702: invokevirtual toString : ()Ljava/lang/String;
    //   705: invokespecial <init> : (Ljava/lang/String;)V
    //   708: athrow
    //   709: aload_0
    //   710: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   713: aload_0
    //   714: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   717: invokeinterface getStreamPosition : ()J
    //   722: invokeinterface flushBefore : (J)V
    //   727: goto -> 124
    //   730: goto -> 746
    //   733: astore_2
    //   734: new javax/imageio/IIOException
    //   737: dup
    //   738: ldc_w 'Error reading PNG metadata'
    //   741: aload_2
    //   742: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
    //   745: athrow
    //   746: aload_0
    //   747: iconst_1
    //   748: putfield gotMetadata : Z
    //   751: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #650	-> 0
    //   #651	-> 7
    //   #654	-> 8
    //   #662	-> 12
    //   #663	-> 20
    //   #666	-> 32
    //   #667	-> 42
    //   #669	-> 52
    //   #671	-> 58
    //   #672	-> 70
    //   #673	-> 83
    //   #676	-> 86
    //   #678	-> 99
    //   #681	-> 102
    //   #679	-> 105
    //   #680	-> 106
    //   #683	-> 118
    //   #684	-> 123
    //   #689	-> 124
    //   #690	-> 134
    //   #694	-> 144
    //   #695	-> 148
    //   #699	-> 176
    //   #700	-> 185
    //   #701	-> 206
    //   #702	-> 217
    //   #705	-> 226
    //   #703	-> 229
    //   #704	-> 231
    //   #707	-> 259
    //   #710	-> 400
    //   #711	-> 412
    //   #712	-> 425
    //   #714	-> 428
    //   #715	-> 433
    //   #717	-> 436
    //   #718	-> 440
    //   #720	-> 443
    //   #721	-> 447
    //   #723	-> 450
    //   #724	-> 454
    //   #726	-> 457
    //   #727	-> 462
    //   #729	-> 465
    //   #730	-> 470
    //   #732	-> 473
    //   #733	-> 478
    //   #735	-> 481
    //   #736	-> 485
    //   #738	-> 488
    //   #739	-> 492
    //   #741	-> 495
    //   #742	-> 500
    //   #744	-> 503
    //   #745	-> 507
    //   #747	-> 510
    //   #748	-> 515
    //   #750	-> 518
    //   #751	-> 522
    //   #753	-> 525
    //   #754	-> 530
    //   #756	-> 533
    //   #757	-> 538
    //   #760	-> 541
    //   #761	-> 546
    //   #763	-> 557
    //   #764	-> 567
    //   #765	-> 578
    //   #766	-> 593
    //   #767	-> 608
    //   #769	-> 620
    //   #770	-> 626
    //   #771	-> 631
    //   #775	-> 638
    //   #776	-> 654
    //   #781	-> 667
    //   #782	-> 681
    //   #785	-> 709
    //   #786	-> 727
    //   #789	-> 730
    //   #787	-> 733
    //   #788	-> 734
    //   #791	-> 746
    //   #792	-> 751
    // Exception table:
    //   from	to	target	type
    //   32	102	105	java/io/IOException
    //   124	730	733	java/io/IOException
    //   176	226	229	java/io/IOException
  }
  
  private static void decodeSubFilter(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3) {
    for (int i = paramInt3; i < paramInt2; i++) {
      int j = paramArrayOfbyte[i + paramInt1] & 0xFF;
      j += paramArrayOfbyte[i + paramInt1 - paramInt3] & 0xFF;
      paramArrayOfbyte[i + paramInt1] = (byte)j;
    } 
  }
  
  private static void decodeUpFilter(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3) {
    for (byte b = 0; b < paramInt3; b++) {
      int i = paramArrayOfbyte1[b + paramInt1] & 0xFF;
      int j = paramArrayOfbyte2[b + paramInt2] & 0xFF;
      paramArrayOfbyte1[b + paramInt1] = (byte)(i + j);
    } 
  }
  
  private static void decodeAverageFilter(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    for (i = 0; i < paramInt4; i++) {
      int j = paramArrayOfbyte1[i + paramInt1] & 0xFF;
      int k = paramArrayOfbyte2[i + paramInt2] & 0xFF;
      paramArrayOfbyte1[i + paramInt1] = (byte)(j + k / 2);
    } 
    for (i = paramInt4; i < paramInt3; i++) {
      int j = paramArrayOfbyte1[i + paramInt1] & 0xFF;
      int k = paramArrayOfbyte1[i + paramInt1 - paramInt4] & 0xFF;
      int m = paramArrayOfbyte2[i + paramInt2] & 0xFF;
      paramArrayOfbyte1[i + paramInt1] = (byte)(j + (k + m) / 2);
    } 
  }
  
  private static int paethPredictor(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = Math.abs(i - paramInt1);
    int k = Math.abs(i - paramInt2);
    int m = Math.abs(i - paramInt3);
    if (j <= k && j <= m)
      return paramInt1; 
    if (k <= m)
      return paramInt2; 
    return paramInt3;
  }
  
  private static void decodePaethFilter(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    for (i = 0; i < paramInt4; i++) {
      int j = paramArrayOfbyte1[i + paramInt1] & 0xFF;
      int k = paramArrayOfbyte2[i + paramInt2] & 0xFF;
      paramArrayOfbyte1[i + paramInt1] = (byte)(j + k);
    } 
    for (i = paramInt4; i < paramInt3; i++) {
      int j = paramArrayOfbyte1[i + paramInt1] & 0xFF;
      int k = paramArrayOfbyte1[i + paramInt1 - paramInt4] & 0xFF;
      int m = paramArrayOfbyte2[i + paramInt2] & 0xFF;
      int n = paramArrayOfbyte2[i + paramInt2 - paramInt4] & 0xFF;
      paramArrayOfbyte1[i + paramInt1] = (byte)(j + paethPredictor(k, m, n));
    } 
  }
  
  private static final int[][] bandOffsets = new int[][] { null, { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
  
  private WritableRaster createRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    WritableRaster writableRaster = null;
    Point point = new Point(0, 0);
    if (paramInt5 < 8 && paramInt3 == 1) {
      DataBufferByte dataBufferByte = new DataBufferByte(paramInt2 * paramInt4);
      writableRaster = Raster.createPackedRaster(dataBufferByte, paramInt1, paramInt2, paramInt5, point);
    } else if (paramInt5 <= 8) {
      DataBufferByte dataBufferByte = new DataBufferByte(paramInt2 * paramInt4);
      writableRaster = Raster.createInterleavedRaster(dataBufferByte, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], point);
    } else {
      DataBufferUShort dataBufferUShort = new DataBufferUShort(paramInt2 * paramInt4);
      writableRaster = Raster.createInterleavedRaster(dataBufferUShort, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], point);
    } 
    return writableRaster;
  }
  
  private void skipPass(int paramInt1, int paramInt2) throws IOException, IIOException {
    if (paramInt1 == 0 || paramInt2 == 0)
      return; 
    int i = inputBandsForColorType[this.metadata.IHDR_colorType];
    int j = (i * paramInt1 * this.metadata.IHDR_bitDepth + 7) / 8;
    for (byte b = 0; b < paramInt2; b++) {
      this.pixelStream.skipBytes(1 + j);
      if (abortRequested())
        return; 
    } 
  }
  
  private void updateImageProgress(int paramInt) {
    this.pixelsDone += paramInt;
    processImageProgress(100.0F * this.pixelsDone / this.totalPixels);
  }
  
  private void decodePass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) throws IOException {
    if (paramInt6 == 0 || paramInt7 == 0)
      return; 
    WritableRaster writableRaster1 = this.theImage.getWritableTile(0, 0);
    int i = writableRaster1.getMinX();
    int j = i + writableRaster1.getWidth() - 1;
    int k = writableRaster1.getMinY();
    int m = k + writableRaster1.getHeight() - 1;
    int[] arrayOfInt1 = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, i, k, j, m, this.sourceXSubsampling, this.sourceYSubsampling, paramInt2, paramInt3, paramInt6, paramInt7, paramInt4, paramInt5);
    int n = arrayOfInt1[0];
    int i1 = arrayOfInt1[1];
    int i2 = arrayOfInt1[2];
    int i3 = arrayOfInt1[4];
    int i4 = arrayOfInt1[5];
    int i5 = this.metadata.IHDR_bitDepth;
    int i6 = inputBandsForColorType[this.metadata.IHDR_colorType];
    int i7 = (i5 == 16) ? 2 : 1;
    i7 *= i6;
    int i8 = (i6 * paramInt6 * i5 + 7) / 8;
    int i9 = (i5 == 16) ? (i8 / 2) : i8;
    if (i2 == 0) {
      for (byte b = 0; b < paramInt7; b++) {
        updateImageProgress(paramInt6);
        this.pixelStream.skipBytes(1 + i8);
      } 
      return;
    } 
    int i10 = (n - this.destinationOffset.x) * this.sourceXSubsampling + this.sourceRegion.x;
    int i11 = (i10 - paramInt2) / paramInt4;
    int i12 = i3 * this.sourceXSubsampling / paramInt4;
    byte[] arrayOfByte1 = null;
    short[] arrayOfShort = null;
    byte[] arrayOfByte2 = new byte[i8];
    byte[] arrayOfByte3 = new byte[i8];
    WritableRaster writableRaster2 = createRaster(paramInt6, 1, i6, i9, i5);
    int[] arrayOfInt2 = writableRaster2.getPixel(0, 0, (int[])null);
    DataBuffer dataBuffer = writableRaster2.getDataBuffer();
    int i13 = dataBuffer.getDataType();
    if (i13 == 0) {
      arrayOfByte1 = ((DataBufferByte)dataBuffer).getData();
    } else {
      arrayOfShort = ((DataBufferUShort)dataBuffer).getData();
    } 
    processPassStarted(this.theImage, paramInt1, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, n, i1, i3, i4, this.destinationBands);
    if (this.sourceBands != null)
      writableRaster2 = writableRaster2.createWritableChild(0, 0, writableRaster2
          .getWidth(), 1, 0, 0, this.sourceBands); 
    if (this.destinationBands != null)
      writableRaster1 = writableRaster1.createWritableChild(0, 0, writableRaster1
          .getWidth(), writableRaster1
          .getHeight(), 0, 0, this.destinationBands); 
    boolean bool1 = false;
    int[] arrayOfInt3 = writableRaster1.getSampleModel().getSampleSize();
    int i14 = arrayOfInt3.length;
    for (byte b1 = 0; b1 < i14; b1++) {
      if (arrayOfInt3[b1] != i5) {
        bool1 = true;
        break;
      } 
    } 
    int[][] arrayOfInt = (int[][])null;
    if (bool1) {
      int i15 = (1 << i5) - 1;
      int i16 = i15 / 2;
      arrayOfInt = new int[i14][];
      for (byte b = 0; b < i14; b++) {
        int i17 = (1 << arrayOfInt3[b]) - 1;
        arrayOfInt[b] = new int[i15 + 1];
        for (byte b3 = 0; b3 <= i15; b3++)
          arrayOfInt[b][b3] = (b3 * i17 + i16) / i15; 
      } 
    } 
    boolean bool2 = (i12 == 1 && i3 == 1 && !bool1 && writableRaster1 instanceof sun.awt.image.ByteInterleavedRaster) ? true : false;
    if (bool2)
      writableRaster2 = writableRaster2.createWritableChild(i11, 0, i2, 1, 0, 0, (int[])null); 
    for (byte b2 = 0; b2 < paramInt7; b2++) {
      updateImageProgress(paramInt6);
      int i15 = this.pixelStream.read();
      try {
        byte[] arrayOfByte = arrayOfByte3;
        arrayOfByte3 = arrayOfByte2;
        arrayOfByte2 = arrayOfByte;
        this.pixelStream.readFully(arrayOfByte2, 0, i8);
      } catch (ZipException zipException) {
        throw zipException;
      } 
      switch (i15) {
        case 0:
          break;
        case 1:
          decodeSubFilter(arrayOfByte2, 0, i8, i7);
          break;
        case 2:
          decodeUpFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8);
          break;
        case 3:
          decodeAverageFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8, i7);
          break;
        case 4:
          decodePaethFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8, i7);
          break;
        default:
          throw new IIOException("Unknown row filter type (= " + i15 + ")!");
      } 
      if (i5 < 16) {
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, i8);
      } else {
        byte b3 = 0;
        for (byte b4 = 0; b4 < i9; b4++) {
          arrayOfShort[b4] = (short)(arrayOfByte2[b3] << 8 | arrayOfByte2[b3 + 1] & 0xFF);
          b3 += 2;
        } 
      } 
      int i16 = b2 * paramInt5 + paramInt3;
      if (i16 >= this.sourceRegion.y && i16 < this.sourceRegion.y + this.sourceRegion.height && (i16 - this.sourceRegion.y) % this.sourceYSubsampling == 0) {
        int i17 = this.destinationOffset.y + (i16 - this.sourceRegion.y) / this.sourceYSubsampling;
        if (i17 >= k) {
          if (i17 > m)
            break; 
          if (bool2) {
            writableRaster1.setRect(n, i17, writableRaster2);
          } else {
            int i18 = i11;
            int i19 = n;
            for (; i19 < n + i2; 
              i19 += i3) {
              writableRaster2.getPixel(i18, 0, arrayOfInt2);
              if (bool1)
                for (byte b = 0; b < i14; b++)
                  arrayOfInt2[b] = arrayOfInt[b][arrayOfInt2[b]];  
              writableRaster1.setPixel(i19, i17, arrayOfInt2);
              i18 += i12;
            } 
          } 
          processImageUpdate(this.theImage, n, i17, i2, 1, i3, i4, this.destinationBands);
          if (abortRequested())
            return; 
        } 
      } 
    } 
    processPassComplete(this.theImage);
  }
  
  private void decodeImage() throws IOException, IIOException {
    int i = this.metadata.IHDR_width;
    int j = this.metadata.IHDR_height;
    this.pixelsDone = 0;
    this.totalPixels = i * j;
    clearAbortRequest();
    if (this.metadata.IHDR_interlaceMethod == 0) {
      decodePass(0, 0, 0, 1, 1, i, j);
    } else {
      for (byte b = 0; b <= this.sourceMaxProgressivePass; b++) {
        int k = adam7XOffset[b];
        int m = adam7YOffset[b];
        int n = adam7XSubsampling[b];
        int i1 = adam7YSubsampling[b];
        int i2 = adam7XSubsampling[b + 1] - 1;
        int i3 = adam7YSubsampling[b + 1] - 1;
        if (b >= this.sourceMinProgressivePass) {
          decodePass(b, k, m, n, i1, (i + i2) / n, (j + i3) / i1);
        } else {
          skipPass((i + i2) / n, (j + i3) / i1);
        } 
        if (abortRequested())
          return; 
      } 
    } 
  }
  
  private void readImage(ImageReadParam paramImageReadParam) throws IIOException {
    readMetadata();
    int i = this.metadata.IHDR_width;
    int j = this.metadata.IHDR_height;
    this.sourceXSubsampling = 1;
    this.sourceYSubsampling = 1;
    this.sourceMinProgressivePass = 0;
    this.sourceMaxProgressivePass = 6;
    this.sourceBands = null;
    this.destinationBands = null;
    this.destinationOffset = new Point(0, 0);
    if (paramImageReadParam != null) {
      this.sourceXSubsampling = paramImageReadParam.getSourceXSubsampling();
      this.sourceYSubsampling = paramImageReadParam.getSourceYSubsampling();
      this
        .sourceMinProgressivePass = Math.max(paramImageReadParam.getSourceMinProgressivePass(), 0);
      this
        .sourceMaxProgressivePass = Math.min(paramImageReadParam.getSourceMaxProgressivePass(), 6);
      this.sourceBands = paramImageReadParam.getSourceBands();
      this.destinationBands = paramImageReadParam.getDestinationBands();
      this.destinationOffset = paramImageReadParam.getDestinationOffset();
    } 
    Inflater inflater = null;
    try {
      this.stream.seek(this.imageStartPosition);
      PNGImageDataEnumeration pNGImageDataEnumeration = new PNGImageDataEnumeration(this.stream);
      SequenceInputStream sequenceInputStream = new SequenceInputStream(pNGImageDataEnumeration);
      inflater = new Inflater();
      InflaterInputStream inflaterInputStream = new InflaterInputStream(sequenceInputStream, inflater);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inflaterInputStream);
      this.pixelStream = new DataInputStream(bufferedInputStream);
      this.theImage = getDestination(paramImageReadParam, 
          getImageTypes(0), i, j);
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      this.sourceRegion = new Rectangle(0, 0, 0, 0);
      computeRegions(paramImageReadParam, i, j, this.theImage, this.sourceRegion, rectangle);
      this.destinationOffset.setLocation(rectangle.getLocation());
      int k = this.metadata.IHDR_colorType;
      checkReadParamBandSettings(paramImageReadParam, inputBandsForColorType[k], this.theImage
          
          .getSampleModel().getNumBands());
      processImageStarted(0);
      decodeImage();
      if (abortRequested()) {
        processReadAborted();
      } else {
        processImageComplete();
      } 
    } catch (IOException iOException) {
      throw new IIOException("Error reading PNG image data", iOException);
    } finally {
      if (inflater != null)
        inflater.end(); 
    } 
  }
  
  public int getNumImages(boolean paramBoolean) throws IIOException {
    if (this.stream == null)
      throw new IllegalStateException("No input source set!"); 
    if (this.seekForwardOnly && paramBoolean)
      throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!"); 
    return 1;
  }
  
  public int getWidth(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    return this.metadata.IHDR_width;
  }
  
  public int getHeight(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    return this.metadata.IHDR_height;
  }
  
  public Iterator<ImageTypeSpecifier> getImageTypes(int paramInt) throws IIOException {
    ColorSpace colorSpace1, colorSpace2;
    int[] arrayOfInt;
    boolean bool;
    int k;
    byte[] arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4;
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    ArrayList<ImageTypeSpecifier> arrayList = new ArrayList(1);
    int i = this.metadata.IHDR_bitDepth;
    int j = this.metadata.IHDR_colorType;
    if (i <= 8) {
      bool = false;
    } else {
      bool = true;
    } 
    switch (j) {
      case 0:
        arrayList.add(ImageTypeSpecifier.createGrayscale(i, bool, false));
        break;
      case 2:
        if (i == 8) {
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(5));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(1));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(4));
        } 
        colorSpace1 = ColorSpace.getInstance(1000);
        arrayOfInt = new int[3];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayOfInt[2] = 2;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace1, arrayOfInt, bool, false, false));
        break;
      case 3:
        readMetadata();
        k = 1 << i;
        arrayOfByte1 = this.metadata.PLTE_red;
        arrayOfByte2 = this.metadata.PLTE_green;
        arrayOfByte3 = this.metadata.PLTE_blue;
        if (this.metadata.PLTE_red.length < k) {
          arrayOfByte1 = Arrays.copyOf(this.metadata.PLTE_red, k);
          Arrays.fill(arrayOfByte1, this.metadata.PLTE_red.length, k, this.metadata.PLTE_red[this.metadata.PLTE_red.length - 1]);
          arrayOfByte2 = Arrays.copyOf(this.metadata.PLTE_green, k);
          Arrays.fill(arrayOfByte2, this.metadata.PLTE_green.length, k, this.metadata.PLTE_green[this.metadata.PLTE_green.length - 1]);
          arrayOfByte3 = Arrays.copyOf(this.metadata.PLTE_blue, k);
          Arrays.fill(arrayOfByte3, this.metadata.PLTE_blue.length, k, this.metadata.PLTE_blue[this.metadata.PLTE_blue.length - 1]);
        } 
        arrayOfByte4 = null;
        if (this.metadata.tRNS_present && this.metadata.tRNS_alpha != null)
          if (this.metadata.tRNS_alpha.length == arrayOfByte1.length) {
            arrayOfByte4 = this.metadata.tRNS_alpha;
          } else {
            arrayOfByte4 = Arrays.copyOf(this.metadata.tRNS_alpha, arrayOfByte1.length);
            Arrays.fill(arrayOfByte4, this.metadata.tRNS_alpha.length, arrayOfByte1.length, (byte)-1);
          }  
        arrayList.add(ImageTypeSpecifier.createIndexed(arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4, i, 0));
        break;
      case 4:
        colorSpace2 = ColorSpace.getInstance(1003);
        arrayOfInt = new int[2];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace2, arrayOfInt, bool, true, false));
        break;
      case 6:
        if (i == 8) {
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(6));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(2));
        } 
        colorSpace1 = ColorSpace.getInstance(1000);
        arrayOfInt = new int[4];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayOfInt[2] = 2;
        arrayOfInt[3] = 3;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace1, arrayOfInt, bool, true, false));
        break;
    } 
    return arrayList.iterator();
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt) throws IOException {
    Iterator<ImageTypeSpecifier> iterator = getImageTypes(paramInt);
    ImageTypeSpecifier imageTypeSpecifier = null;
    while (true) {
      imageTypeSpecifier = iterator.next();
      if (!iterator.hasNext())
        return imageTypeSpecifier; 
    } 
  }
  
  public ImageReadParam getDefaultReadParam() {
    return new ImageReadParam();
  }
  
  public IIOMetadata getStreamMetadata() throws IIOException {
    return null;
  }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readMetadata();
    return this.metadata;
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readImage(paramImageReadParam);
    return this.theImage;
  }
  
  public void reset() {
    super.reset();
    resetStreamSettings();
  }
  
  private void resetStreamSettings() {
    this.gotHeader = false;
    this.gotMetadata = false;
    this.metadata = null;
    this.pixelStream = null;
  }
}
