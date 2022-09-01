package sun.font;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class FileFontStrike extends PhysicalStrike {
  static final int INVISIBLE_GLYPHS = 65534;
  
  private FileFont fileFont;
  
  private static final int UNINITIALISED = 0;
  
  private static final int INTARRAY = 1;
  
  private static final int LONGARRAY = 2;
  
  private static final int SEGINTARRAY = 3;
  
  private static final int SEGLONGARRAY = 4;
  
  private volatile int glyphCacheFormat;
  
  private static final int SEGSHIFT = 5;
  
  private static final int SEGSIZE = 32;
  
  private boolean segmentedCache;
  
  private int[][] segIntGlyphImages;
  
  private long[][] segLongGlyphImages;
  
  private float[] horizontalAdvances;
  
  private float[][] segHorizontalAdvances;
  
  ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
  
  SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
  
  AffineTransform invertDevTx;
  
  boolean useNatives;
  
  NativeStrike[] nativeStrikes;
  
  private int intPtSize;
  
  private static boolean isXPorLater = false;
  
  private static final int SLOTZEROMAX = 16777215;
  
  private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;
  
  static {
    if (FontUtilities.isWindows && !FontUtilities.useT2K && 
      !GraphicsEnvironment.isHeadless())
      isXPorLater = initNative(); 
  }
  
  FileFontStrike(FileFont paramFileFont, FontStrikeDesc paramFontStrikeDesc) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: invokespecial <init> : (Lsun/font/PhysicalFont;Lsun/font/FontStrikeDesc;)V
    //   6: aload_0
    //   7: iconst_0
    //   8: putfield glyphCacheFormat : I
    //   11: aload_0
    //   12: aload_1
    //   13: putfield fileFont : Lsun/font/FileFont;
    //   16: aload_2
    //   17: getfield style : I
    //   20: aload_1
    //   21: getfield style : I
    //   24: if_icmpeq -> 87
    //   27: aload_2
    //   28: getfield style : I
    //   31: iconst_2
    //   32: iand
    //   33: iconst_2
    //   34: if_icmpne -> 57
    //   37: aload_1
    //   38: getfield style : I
    //   41: iconst_2
    //   42: iand
    //   43: ifne -> 57
    //   46: aload_0
    //   47: iconst_1
    //   48: putfield algoStyle : Z
    //   51: aload_0
    //   52: ldc 0.7
    //   54: putfield italic : F
    //   57: aload_2
    //   58: getfield style : I
    //   61: iconst_1
    //   62: iand
    //   63: iconst_1
    //   64: if_icmpne -> 87
    //   67: aload_1
    //   68: getfield style : I
    //   71: iconst_1
    //   72: iand
    //   73: ifne -> 87
    //   76: aload_0
    //   77: iconst_1
    //   78: putfield algoStyle : Z
    //   81: aload_0
    //   82: ldc 1.33
    //   84: putfield boldness : F
    //   87: iconst_4
    //   88: newarray double
    //   90: astore_3
    //   91: aload_2
    //   92: getfield glyphTx : Ljava/awt/geom/AffineTransform;
    //   95: astore #4
    //   97: aload #4
    //   99: aload_3
    //   100: invokevirtual getMatrix : ([D)V
    //   103: aload_2
    //   104: getfield devTx : Ljava/awt/geom/AffineTransform;
    //   107: invokevirtual isIdentity : ()Z
    //   110: ifne -> 140
    //   113: aload_2
    //   114: getfield devTx : Ljava/awt/geom/AffineTransform;
    //   117: invokevirtual getType : ()I
    //   120: iconst_1
    //   121: if_icmpeq -> 140
    //   124: aload_0
    //   125: aload_2
    //   126: getfield devTx : Ljava/awt/geom/AffineTransform;
    //   129: invokevirtual createInverse : ()Ljava/awt/geom/AffineTransform;
    //   132: putfield invertDevTx : Ljava/awt/geom/AffineTransform;
    //   135: goto -> 140
    //   138: astore #5
    //   140: aload_2
    //   141: getfield aaHint : I
    //   144: iconst_1
    //   145: if_icmpeq -> 164
    //   148: aload_1
    //   149: getfield familyName : Ljava/lang/String;
    //   152: ldc 'Amble'
    //   154: invokevirtual startsWith : (Ljava/lang/String;)Z
    //   157: ifeq -> 164
    //   160: iconst_1
    //   161: goto -> 165
    //   164: iconst_0
    //   165: istore #5
    //   167: aload_3
    //   168: iconst_0
    //   169: daload
    //   170: invokestatic isNaN : (D)Z
    //   173: ifne -> 210
    //   176: aload_3
    //   177: iconst_1
    //   178: daload
    //   179: invokestatic isNaN : (D)Z
    //   182: ifne -> 210
    //   185: aload_3
    //   186: iconst_2
    //   187: daload
    //   188: invokestatic isNaN : (D)Z
    //   191: ifne -> 210
    //   194: aload_3
    //   195: iconst_3
    //   196: daload
    //   197: invokestatic isNaN : (D)Z
    //   200: ifne -> 210
    //   203: aload_1
    //   204: invokevirtual getScaler : ()Lsun/font/FontScaler;
    //   207: ifnonnull -> 220
    //   210: aload_0
    //   211: invokestatic getNullScalerContext : ()J
    //   214: putfield pScalerContext : J
    //   217: goto -> 250
    //   220: aload_0
    //   221: aload_1
    //   222: invokevirtual getScaler : ()Lsun/font/FontScaler;
    //   225: aload_3
    //   226: aload_2
    //   227: getfield aaHint : I
    //   230: aload_2
    //   231: getfield fmHint : I
    //   234: aload_0
    //   235: getfield boldness : F
    //   238: aload_0
    //   239: getfield italic : F
    //   242: iload #5
    //   244: invokevirtual createScalerContext : ([DIIFFZ)J
    //   247: putfield pScalerContext : J
    //   250: aload_0
    //   251: aload_1
    //   252: invokevirtual getMapper : ()Lsun/font/CharToGlyphMapper;
    //   255: putfield mapper : Lsun/font/CharToGlyphMapper;
    //   258: aload_0
    //   259: getfield mapper : Lsun/font/CharToGlyphMapper;
    //   262: invokevirtual getNumGlyphs : ()I
    //   265: istore #6
    //   267: aload_3
    //   268: iconst_3
    //   269: daload
    //   270: d2f
    //   271: fstore #7
    //   273: aload_0
    //   274: fload #7
    //   276: f2i
    //   277: dup_x1
    //   278: putfield intPtSize : I
    //   281: istore #8
    //   283: aload #4
    //   285: invokevirtual getType : ()I
    //   288: bipush #124
    //   290: iand
    //   291: ifne -> 298
    //   294: iconst_1
    //   295: goto -> 299
    //   298: iconst_0
    //   299: istore #9
    //   301: aload_0
    //   302: iload #6
    //   304: sipush #256
    //   307: if_icmpgt -> 345
    //   310: iload #6
    //   312: bipush #64
    //   314: if_icmple -> 349
    //   317: iload #9
    //   319: ifeq -> 345
    //   322: fload #7
    //   324: iload #8
    //   326: i2f
    //   327: fcmpl
    //   328: ifne -> 345
    //   331: iload #8
    //   333: bipush #6
    //   335: if_icmplt -> 345
    //   338: iload #8
    //   340: bipush #36
    //   342: if_icmple -> 349
    //   345: iconst_1
    //   346: goto -> 350
    //   349: iconst_0
    //   350: putfield segmentedCache : Z
    //   353: aload_0
    //   354: getfield pScalerContext : J
    //   357: lconst_0
    //   358: lcmp
    //   359: ifne -> 394
    //   362: aload_0
    //   363: new sun/font/FontStrikeDisposer
    //   366: dup
    //   367: aload_1
    //   368: aload_2
    //   369: invokespecial <init> : (Lsun/font/Font2D;Lsun/font/FontStrikeDesc;)V
    //   372: putfield disposer : Lsun/font/FontStrikeDisposer;
    //   375: aload_0
    //   376: invokespecial initGlyphCache : ()V
    //   379: aload_0
    //   380: invokestatic getNullScalerContext : ()J
    //   383: putfield pScalerContext : J
    //   386: invokestatic getInstance : ()Lsun/font/SunFontManager;
    //   389: aload_1
    //   390: invokevirtual deRegisterBadFont : (Lsun/font/Font2D;)V
    //   393: return
    //   394: getstatic sun/font/FontUtilities.isWindows : Z
    //   397: ifeq -> 509
    //   400: getstatic sun/font/FileFontStrike.isXPorLater : Z
    //   403: ifeq -> 509
    //   406: getstatic sun/font/FontUtilities.useT2K : Z
    //   409: ifne -> 509
    //   412: invokestatic isHeadless : ()Z
    //   415: ifne -> 509
    //   418: aload_1
    //   419: getfield useJavaRasterizer : Z
    //   422: ifne -> 509
    //   425: aload_2
    //   426: getfield aaHint : I
    //   429: iconst_4
    //   430: if_icmpeq -> 441
    //   433: aload_2
    //   434: getfield aaHint : I
    //   437: iconst_5
    //   438: if_icmpne -> 509
    //   441: aload_3
    //   442: iconst_1
    //   443: daload
    //   444: dconst_0
    //   445: dcmpl
    //   446: ifne -> 509
    //   449: aload_3
    //   450: iconst_2
    //   451: daload
    //   452: dconst_0
    //   453: dcmpl
    //   454: ifne -> 509
    //   457: aload_3
    //   458: iconst_0
    //   459: daload
    //   460: aload_3
    //   461: iconst_3
    //   462: daload
    //   463: dcmpl
    //   464: ifne -> 509
    //   467: aload_3
    //   468: iconst_0
    //   469: daload
    //   470: ldc2_w 3.0
    //   473: dcmpl
    //   474: iflt -> 509
    //   477: aload_3
    //   478: iconst_0
    //   479: daload
    //   480: ldc2_w 100.0
    //   483: dcmpg
    //   484: ifgt -> 509
    //   487: aload_1
    //   488: checkcast sun/font/TrueTypeFont
    //   491: aload_0
    //   492: getfield intPtSize : I
    //   495: invokevirtual useEmbeddedBitmapsForSize : (I)Z
    //   498: ifne -> 509
    //   501: aload_0
    //   502: iconst_1
    //   503: putfield useNatives : Z
    //   506: goto -> 636
    //   509: aload_1
    //   510: invokevirtual checkUseNatives : ()Z
    //   513: ifeq -> 636
    //   516: aload_2
    //   517: getfield aaHint : I
    //   520: ifne -> 636
    //   523: aload_0
    //   524: getfield algoStyle : Z
    //   527: ifne -> 636
    //   530: aload_3
    //   531: iconst_1
    //   532: daload
    //   533: dconst_0
    //   534: dcmpl
    //   535: ifne -> 636
    //   538: aload_3
    //   539: iconst_2
    //   540: daload
    //   541: dconst_0
    //   542: dcmpl
    //   543: ifne -> 636
    //   546: aload_3
    //   547: iconst_0
    //   548: daload
    //   549: ldc2_w 6.0
    //   552: dcmpl
    //   553: iflt -> 636
    //   556: aload_3
    //   557: iconst_0
    //   558: daload
    //   559: ldc2_w 36.0
    //   562: dcmpg
    //   563: ifgt -> 636
    //   566: aload_3
    //   567: iconst_0
    //   568: daload
    //   569: aload_3
    //   570: iconst_3
    //   571: daload
    //   572: dcmpl
    //   573: ifne -> 636
    //   576: aload_0
    //   577: iconst_1
    //   578: putfield useNatives : Z
    //   581: aload_1
    //   582: getfield nativeFonts : [Lsun/font/NativeFont;
    //   585: arraylength
    //   586: istore #10
    //   588: aload_0
    //   589: iload #10
    //   591: anewarray sun/font/NativeStrike
    //   594: putfield nativeStrikes : [Lsun/font/NativeStrike;
    //   597: iconst_0
    //   598: istore #11
    //   600: iload #11
    //   602: iload #10
    //   604: if_icmpge -> 636
    //   607: aload_0
    //   608: getfield nativeStrikes : [Lsun/font/NativeStrike;
    //   611: iload #11
    //   613: new sun/font/NativeStrike
    //   616: dup
    //   617: aload_1
    //   618: getfield nativeFonts : [Lsun/font/NativeFont;
    //   621: iload #11
    //   623: aaload
    //   624: aload_2
    //   625: iconst_0
    //   626: invokespecial <init> : (Lsun/font/NativeFont;Lsun/font/FontStrikeDesc;Z)V
    //   629: aastore
    //   630: iinc #11, 1
    //   633: goto -> 600
    //   636: invokestatic isLogging : ()Z
    //   639: ifeq -> 746
    //   642: getstatic sun/font/FontUtilities.isWindows : Z
    //   645: ifeq -> 746
    //   648: invokestatic getLogger : ()Lsun/util/logging/PlatformLogger;
    //   651: new java/lang/StringBuilder
    //   654: dup
    //   655: invokespecial <init> : ()V
    //   658: ldc_w 'Strike for '
    //   661: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   664: aload_1
    //   665: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   668: ldc_w ' at size = '
    //   671: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   674: aload_0
    //   675: getfield intPtSize : I
    //   678: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   681: ldc_w ' use natives = '
    //   684: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   687: aload_0
    //   688: getfield useNatives : Z
    //   691: invokevirtual append : (Z)Ljava/lang/StringBuilder;
    //   694: ldc_w ' useJavaRasteriser = '
    //   697: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   700: aload_1
    //   701: getfield useJavaRasterizer : Z
    //   704: invokevirtual append : (Z)Ljava/lang/StringBuilder;
    //   707: ldc_w ' AAHint = '
    //   710: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   713: aload_2
    //   714: getfield aaHint : I
    //   717: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   720: ldc_w ' Has Embedded bitmaps = '
    //   723: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   726: aload_1
    //   727: checkcast sun/font/TrueTypeFont
    //   730: aload_0
    //   731: getfield intPtSize : I
    //   734: invokevirtual useEmbeddedBitmapsForSize : (I)Z
    //   737: invokevirtual append : (Z)Ljava/lang/StringBuilder;
    //   740: invokevirtual toString : ()Ljava/lang/String;
    //   743: invokevirtual info : (Ljava/lang/String;)V
    //   746: aload_0
    //   747: new sun/font/FontStrikeDisposer
    //   750: dup
    //   751: aload_1
    //   752: aload_2
    //   753: aload_0
    //   754: getfield pScalerContext : J
    //   757: invokespecial <init> : (Lsun/font/Font2D;Lsun/font/FontStrikeDesc;J)V
    //   760: putfield disposer : Lsun/font/FontStrikeDisposer;
    //   763: ldc2_w 48.0
    //   766: dstore #10
    //   768: aload_0
    //   769: aload #4
    //   771: invokevirtual getScaleX : ()D
    //   774: invokestatic abs : (D)D
    //   777: dload #10
    //   779: dcmpg
    //   780: ifgt -> 829
    //   783: aload #4
    //   785: invokevirtual getScaleY : ()D
    //   788: invokestatic abs : (D)D
    //   791: dload #10
    //   793: dcmpg
    //   794: ifgt -> 829
    //   797: aload #4
    //   799: invokevirtual getShearX : ()D
    //   802: invokestatic abs : (D)D
    //   805: dload #10
    //   807: dcmpg
    //   808: ifgt -> 829
    //   811: aload #4
    //   813: invokevirtual getShearY : ()D
    //   816: invokestatic abs : (D)D
    //   819: dload #10
    //   821: dcmpg
    //   822: ifgt -> 829
    //   825: iconst_1
    //   826: goto -> 830
    //   829: iconst_0
    //   830: putfield getImageWithAdvance : Z
    //   833: aload_0
    //   834: getfield getImageWithAdvance : Z
    //   837: ifne -> 905
    //   840: aload_0
    //   841: getfield segmentedCache : Z
    //   844: ifne -> 884
    //   847: aload_0
    //   848: iload #6
    //   850: newarray float
    //   852: putfield horizontalAdvances : [F
    //   855: iconst_0
    //   856: istore #12
    //   858: iload #12
    //   860: iload #6
    //   862: if_icmpge -> 881
    //   865: aload_0
    //   866: getfield horizontalAdvances : [F
    //   869: iload #12
    //   871: ldc_w 3.4028235E38
    //   874: fastore
    //   875: iinc #12, 1
    //   878: goto -> 858
    //   881: goto -> 905
    //   884: iload #6
    //   886: bipush #32
    //   888: iadd
    //   889: iconst_1
    //   890: isub
    //   891: bipush #32
    //   893: idiv
    //   894: istore #12
    //   896: aload_0
    //   897: iload #12
    //   899: anewarray [F
    //   902: putfield segHorizontalAdvances : [[F
    //   905: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #124	-> 0
    //   #61	-> 6
    //   #125	-> 11
    //   #127	-> 16
    //   #132	-> 27
    //   #134	-> 46
    //   #135	-> 51
    //   #137	-> 57
    //   #139	-> 76
    //   #140	-> 81
    //   #143	-> 87
    //   #144	-> 91
    //   #145	-> 97
    //   #146	-> 103
    //   #147	-> 117
    //   #149	-> 124
    //   #151	-> 135
    //   #150	-> 138
    //   #168	-> 140
    //   #169	-> 154
    //   #177	-> 167
    //   #178	-> 188
    //   #179	-> 204
    //   #180	-> 210
    //   #182	-> 220
    //   #187	-> 250
    //   #188	-> 258
    //   #198	-> 267
    //   #199	-> 273
    //   #200	-> 283
    //   #201	-> 301
    //   #213	-> 353
    //   #217	-> 362
    //   #218	-> 375
    //   #219	-> 379
    //   #220	-> 386
    //   #221	-> 393
    //   #230	-> 394
    //   #232	-> 412
    //   #239	-> 495
    //   #240	-> 501
    //   #242	-> 509
    //   #245	-> 530
    //   #248	-> 576
    //   #249	-> 581
    //   #250	-> 588
    //   #254	-> 597
    //   #255	-> 607
    //   #254	-> 630
    //   #260	-> 636
    //   #261	-> 648
    //   #268	-> 734
    //   #262	-> 743
    //   #270	-> 746
    //   #278	-> 763
    //   #279	-> 768
    //   #280	-> 771
    //   #281	-> 785
    //   #282	-> 799
    //   #283	-> 813
    //   #294	-> 833
    //   #295	-> 840
    //   #296	-> 847
    //   #298	-> 855
    //   #299	-> 865
    //   #298	-> 875
    //   #302	-> 884
    //   #303	-> 896
    //   #306	-> 905
    // Exception table:
    //   from	to	target	type
    //   124	135	138	java/awt/geom/NoninvertibleTransformException
  }
  
  public int getNumGlyphs() {
    return this.fileFont.getNumGlyphs();
  }
  
  long getGlyphImageFromNative(int paramInt) {
    if (FontUtilities.isWindows)
      return getGlyphImageFromWindows(paramInt); 
    return getGlyphImageFromX11(paramInt);
  }
  
  long getGlyphImageFromWindows(int paramInt) {
    String str = this.fileFont.getFamilyName(null);
    int i = this.desc.style & 0x1 | this.desc.style & 0x2 | this.fileFont.getStyle();
    int j = this.intPtSize;
    long l = _getGlyphImageFromWindows(str, i, j, paramInt, (this.desc.fmHint == 2));
    if (l != 0L) {
      float f = getGlyphAdvance(paramInt, false);
      StrikeCache.unsafe.putFloat(l + StrikeCache.xAdvanceOffset, f);
      return l;
    } 
    return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
  }
  
  long getGlyphImageFromX11(int paramInt) {
    char c = this.fileFont.glyphToCharMap[paramInt];
    for (byte b = 0; b < this.nativeStrikes.length; b++) {
      CharToGlyphMapper charToGlyphMapper = this.fileFont.nativeFonts[b].getMapper();
      int i = charToGlyphMapper.charToGlyph(c) & 0xFFFF;
      if (i != charToGlyphMapper.getMissingGlyphCode()) {
        long l = this.nativeStrikes[b].getGlyphImagePtrNoCache(i);
        if (l != 0L)
          return l; 
      } 
    } 
    return this.fileFont.getGlyphImage(this.pScalerContext, paramInt);
  }
  
  long getGlyphImagePtr(int paramInt) {
    if (paramInt >= 65534)
      return StrikeCache.invisibleGlyphPtr; 
    long l = 0L;
    if ((l = getCachedGlyphPtr(paramInt)) != 0L)
      return l; 
    if (this.useNatives) {
      l = getGlyphImageFromNative(paramInt);
      if (l == 0L && FontUtilities.isLogging())
        FontUtilities.getLogger()
          .info("Strike for " + this.fileFont + " at size = " + this.intPtSize + " couldn't get native glyph for code = " + paramInt); 
    } 
    if (l == 0L)
      l = this.fileFont.getGlyphImage(this.pScalerContext, paramInt); 
    return setCachedGlyphPtr(paramInt, l);
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfint, long[] paramArrayOflong, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      int i = paramArrayOfint[b];
      if (i >= 65534) {
        paramArrayOflong[b] = StrikeCache.invisibleGlyphPtr;
      } else {
        paramArrayOflong[b] = getCachedGlyphPtr(i);
        if (getCachedGlyphPtr(i) == 0L) {
          long l = 0L;
          if (this.useNatives)
            l = getGlyphImageFromNative(i); 
          if (l == 0L)
            l = this.fileFont.getGlyphImage(this.pScalerContext, i); 
          paramArrayOflong[b] = setCachedGlyphPtr(i, l);
        } 
      } 
    } 
  }
  
  int getSlot0GlyphImagePtrs(int[] paramArrayOfint, long[] paramArrayOflong, int paramInt) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt; b2++) {
      int i = paramArrayOfint[b2];
      if (i >= 16777215)
        return b1; 
      b1++;
      if (i >= 65534) {
        paramArrayOflong[b2] = StrikeCache.invisibleGlyphPtr;
      } else {
        paramArrayOflong[b2] = getCachedGlyphPtr(i);
        if (getCachedGlyphPtr(i) == 0L) {
          long l = 0L;
          if (this.useNatives)
            l = getGlyphImageFromNative(i); 
          if (l == 0L)
            l = this.fileFont.getGlyphImage(this.pScalerContext, i); 
          paramArrayOflong[b2] = setCachedGlyphPtr(i, l);
        } 
      } 
    } 
    return b1;
  }
  
  long getCachedGlyphPtr(int paramInt) {
    int i;
    switch (this.glyphCacheFormat) {
      case 1:
        return this.intGlyphImages[paramInt] & 0xFFFFFFFFL;
      case 3:
        i = paramInt >> 5;
        if (this.segIntGlyphImages[i] != null) {
          int j = paramInt % 32;
          return this.segIntGlyphImages[i][j] & 0xFFFFFFFFL;
        } 
        return 0L;
      case 2:
        return this.longGlyphImages[paramInt];
      case 4:
        i = paramInt >> 5;
        if (this.segLongGlyphImages[i] != null) {
          int j = paramInt % 32;
          return this.segLongGlyphImages[i][j];
        } 
        return 0L;
    } 
    return 0L;
  }
  
  private synchronized long setCachedGlyphPtr(int paramInt, long paramLong) {
    int i, j;
    switch (this.glyphCacheFormat) {
      case 1:
        if (this.intGlyphImages[paramInt] == 0) {
          this.intGlyphImages[paramInt] = (int)paramLong;
          return paramLong;
        } 
        StrikeCache.freeIntPointer((int)paramLong);
        return this.intGlyphImages[paramInt] & 0xFFFFFFFFL;
      case 3:
        i = paramInt >> 5;
        j = paramInt % 32;
        if (this.segIntGlyphImages[i] == null)
          this.segIntGlyphImages[i] = new int[32]; 
        if (this.segIntGlyphImages[i][j] == 0) {
          this.segIntGlyphImages[i][j] = (int)paramLong;
          return paramLong;
        } 
        StrikeCache.freeIntPointer((int)paramLong);
        return this.segIntGlyphImages[i][j] & 0xFFFFFFFFL;
      case 2:
        if (this.longGlyphImages[paramInt] == 0L) {
          this.longGlyphImages[paramInt] = paramLong;
          return paramLong;
        } 
        StrikeCache.freeLongPointer(paramLong);
        return this.longGlyphImages[paramInt];
      case 4:
        i = paramInt >> 5;
        j = paramInt % 32;
        if (this.segLongGlyphImages[i] == null)
          this.segLongGlyphImages[i] = new long[32]; 
        if (this.segLongGlyphImages[i][j] == 0L) {
          this.segLongGlyphImages[i][j] = paramLong;
          return paramLong;
        } 
        StrikeCache.freeLongPointer(paramLong);
        return this.segLongGlyphImages[i][j];
    } 
    initGlyphCache();
    return setCachedGlyphPtr(paramInt, paramLong);
  }
  
  private synchronized void initGlyphCache() {
    int i = this.mapper.getNumGlyphs();
    byte b = 0;
    if (this.segmentedCache) {
      int j = (i + 32 - 1) / 32;
      if (longAddresses) {
        b = 4;
        this.segLongGlyphImages = new long[j][];
        this.disposer.segLongGlyphImages = this.segLongGlyphImages;
      } else {
        b = 3;
        this.segIntGlyphImages = new int[j][];
        this.disposer.segIntGlyphImages = this.segIntGlyphImages;
      } 
    } else if (longAddresses) {
      b = 2;
      this.longGlyphImages = new long[i];
      this.disposer.longGlyphImages = this.longGlyphImages;
    } else {
      b = 1;
      this.intGlyphImages = new int[i];
      this.disposer.intGlyphImages = this.intGlyphImages;
    } 
    this.glyphCacheFormat = b;
  }
  
  float getGlyphAdvance(int paramInt) {
    return getGlyphAdvance(paramInt, true);
  }
  
  private float getGlyphAdvance(int paramInt, boolean paramBoolean) {
    float f;
    if (paramInt >= 65534)
      return 0.0F; 
    if (this.horizontalAdvances != null) {
      f = this.horizontalAdvances[paramInt];
      if (f != Float.MAX_VALUE) {
        if (!paramBoolean && this.invertDevTx != null) {
          Point2D.Float float_ = new Point2D.Float(f, 0.0F);
          this.desc.devTx.deltaTransform(float_, float_);
          return float_.x;
        } 
        return f;
      } 
    } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
      int i = paramInt >> 5;
      float[] arrayOfFloat = this.segHorizontalAdvances[i];
      if (arrayOfFloat != null) {
        f = arrayOfFloat[paramInt % 32];
        if (f != Float.MAX_VALUE) {
          if (!paramBoolean && this.invertDevTx != null) {
            Point2D.Float float_ = new Point2D.Float(f, 0.0F);
            this.desc.devTx.deltaTransform(float_, float_);
            return float_.x;
          } 
          return f;
        } 
      } 
    } 
    if (!paramBoolean && this.invertDevTx != null) {
      Point2D.Float float_ = new Point2D.Float();
      this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, float_);
      return float_.x;
    } 
    if (this.invertDevTx != null || !paramBoolean) {
      f = (getGlyphMetrics(paramInt, paramBoolean)).x;
    } else {
      long l;
      if (this.getImageWithAdvance) {
        l = getGlyphImagePtr(paramInt);
      } else {
        l = getCachedGlyphPtr(paramInt);
      } 
      if (l != 0L) {
        f = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      } else {
        f = this.fileFont.getGlyphAdvance(this.pScalerContext, paramInt);
      } 
    } 
    if (this.horizontalAdvances != null) {
      this.horizontalAdvances[paramInt] = f;
    } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
      int i = paramInt >> 5;
      int j = paramInt % 32;
      if (this.segHorizontalAdvances[i] == null) {
        this.segHorizontalAdvances[i] = new float[32];
        for (byte b = 0; b < 32; b++)
          this.segHorizontalAdvances[i][b] = Float.MAX_VALUE; 
      } 
      this.segHorizontalAdvances[i][j] = f;
    } 
    return f;
  }
  
  float getCodePointAdvance(int paramInt) {
    return getGlyphAdvance(this.mapper.charToGlyph(paramInt));
  }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle) {
    long l = getGlyphImagePtr(paramInt);
    if (l == 0L) {
      paramRectangle.x = (int)Math.floor(paramFloat.x);
      paramRectangle.y = (int)Math.floor(paramFloat.y);
      paramRectangle.width = paramRectangle.height = 0;
      return;
    } 
    float f1 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftXOffset);
    float f2 = StrikeCache.unsafe.getFloat(l + StrikeCache.topLeftYOffset);
    paramRectangle.x = (int)Math.floor((paramFloat.x + f1));
    paramRectangle.y = (int)Math.floor((paramFloat.y + f2));
    paramRectangle
      .width = StrikeCache.unsafe.getShort(l + StrikeCache.widthOffset) & 0xFFFF;
    paramRectangle
      .height = StrikeCache.unsafe.getShort(l + StrikeCache.heightOffset) & 0xFFFF;
    if ((this.desc.aaHint == 4 || this.desc.aaHint == 5) && f1 <= -2.0F) {
      int i = getGlyphImageMinX(l, paramRectangle.x);
      if (i > paramRectangle.x) {
        paramRectangle.x++;
        paramRectangle.width--;
      } 
    } 
  }
  
  private int getGlyphImageMinX(long paramLong, int paramInt) {
    char c1 = StrikeCache.unsafe.getChar(paramLong + StrikeCache.widthOffset);
    char c2 = StrikeCache.unsafe.getChar(paramLong + StrikeCache.heightOffset);
    char c3 = StrikeCache.unsafe.getChar(paramLong + StrikeCache.rowBytesOffset);
    if (c3 == c1)
      return paramInt; 
    long l = StrikeCache.unsafe.getAddress(paramLong + StrikeCache.pixelDataOffset);
    if (l == 0L)
      return paramInt; 
    for (byte b = 0; b < c2; b++) {
      for (byte b1 = 0; b1 < 3; b1++) {
        if (StrikeCache.unsafe.getByte(l + (b * c3) + b1) != 0)
          return paramInt; 
      } 
    } 
    return paramInt + 1;
  }
  
  StrikeMetrics getFontMetrics() {
    if (this.strikeMetrics == null) {
      this
        .strikeMetrics = this.fileFont.getFontMetrics(this.pScalerContext);
      if (this.invertDevTx != null)
        this.strikeMetrics.convertToUserSpace(this.invertDevTx); 
    } 
    return this.strikeMetrics;
  }
  
  Point2D.Float getGlyphMetrics(int paramInt) {
    return getGlyphMetrics(paramInt, true);
  }
  
  private Point2D.Float getGlyphMetrics(int paramInt, boolean paramBoolean) {
    long l;
    Point2D.Float float_ = new Point2D.Float();
    if (paramInt >= 65534)
      return float_; 
    if (this.getImageWithAdvance && paramBoolean) {
      l = getGlyphImagePtr(paramInt);
    } else {
      l = getCachedGlyphPtr(paramInt);
    } 
    if (l != 0L) {
      float_ = new Point2D.Float();
      float_
        .x = StrikeCache.unsafe.getFloat(l + StrikeCache.xAdvanceOffset);
      float_
        .y = StrikeCache.unsafe.getFloat(l + StrikeCache.yAdvanceOffset);
      if (this.invertDevTx != null)
        this.invertDevTx.deltaTransform(float_, float_); 
    } else {
      Integer integer = Integer.valueOf(paramInt);
      Point2D.Float float_1 = null;
      ConcurrentHashMap<Object, Object> concurrentHashMap = null;
      if (this.glyphMetricsMapRef != null)
        concurrentHashMap = (ConcurrentHashMap)this.glyphMetricsMapRef.get(); 
      if (concurrentHashMap != null) {
        float_1 = (Point2D.Float)concurrentHashMap.get(integer);
        if (float_1 != null) {
          float_.x = float_1.x;
          float_.y = float_1.y;
          return float_;
        } 
      } 
      if (float_1 == null) {
        this.fileFont.getGlyphMetrics(this.pScalerContext, paramInt, float_);
        if (this.invertDevTx != null)
          this.invertDevTx.deltaTransform(float_, float_); 
        float_1 = new Point2D.Float(float_.x, float_.y);
        if (concurrentHashMap == null) {
          concurrentHashMap = new ConcurrentHashMap<>();
          this.glyphMetricsMapRef = new SoftReference(concurrentHashMap);
        } 
        concurrentHashMap.put(integer, float_1);
      } 
    } 
    return float_;
  }
  
  Point2D.Float getCharMetrics(char paramChar) {
    return getGlyphMetrics(this.mapper.charToGlyph(paramChar));
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt) {
    if (this.boundsMap == null)
      this.boundsMap = new ConcurrentHashMap<>(); 
    Integer integer = Integer.valueOf(paramInt);
    Rectangle2D.Float float_ = this.boundsMap.get(integer);
    if (float_ == null) {
      float_ = this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt);
      this.boundsMap.put(integer, float_);
    } 
    return float_;
  }
  
  public Rectangle2D getOutlineBounds(int paramInt) {
    return this.fileFont.getGlyphOutlineBounds(this.pScalerContext, paramInt);
  }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
    GeneralPath generalPath = null;
    ConcurrentHashMap<Object, Object> concurrentHashMap = null;
    if (this.outlineMapRef != null) {
      concurrentHashMap = (ConcurrentHashMap)this.outlineMapRef.get();
      if (concurrentHashMap != null)
        generalPath = (GeneralPath)concurrentHashMap.get(Integer.valueOf(paramInt)); 
    } 
    if (generalPath == null) {
      generalPath = this.fileFont.getGlyphOutline(this.pScalerContext, paramInt, 0.0F, 0.0F);
      if (concurrentHashMap == null) {
        concurrentHashMap = new ConcurrentHashMap<>();
        this.outlineMapRef = new WeakReference(concurrentHashMap);
      } 
      concurrentHashMap.put(Integer.valueOf(paramInt), generalPath);
    } 
    generalPath = (GeneralPath)generalPath.clone();
    if (paramFloat1 != 0.0F || paramFloat2 != 0.0F)
      generalPath.transform(AffineTransform.getTranslateInstance(paramFloat1, paramFloat2)); 
    return generalPath;
  }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfint, float paramFloat1, float paramFloat2) {
    return this.fileFont.getGlyphVectorOutline(this.pScalerContext, paramArrayOfint, paramArrayOfint.length, paramFloat1, paramFloat2);
  }
  
  protected void adjustPoint(Point2D.Float paramFloat) {
    if (this.invertDevTx != null)
      this.invertDevTx.deltaTransform(paramFloat, paramFloat); 
  }
  
  private static native boolean initNative();
  
  private native long _getGlyphImageFromWindows(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
}
