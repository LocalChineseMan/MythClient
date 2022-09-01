package sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.StringTokenizer;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;
import sun.security.action.GetPropertyAction;

public abstract class GraphicsPrimitive {
  private String methodSignature;
  
  private int uniqueID;
  
  private static int unusedPrimID = 1;
  
  private SurfaceType sourceType;
  
  private CompositeType compositeType;
  
  private SurfaceType destType;
  
  private long pNativePrim;
  
  static HashMap traceMap;
  
  public static int traceflags;
  
  public static String tracefile;
  
  public static PrintStream traceout;
  
  public static final int TRACELOG = 1;
  
  public static final int TRACETIMESTAMP = 2;
  
  public static final int TRACECOUNTS = 4;
  
  private String cachedname;
  
  public static final synchronized int makePrimTypeID() {
    if (unusedPrimID > 255)
      throw new InternalError("primitive id overflow"); 
    return unusedPrimID++;
  }
  
  public static final synchronized int makeUniqueID(int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    return paramInt << 24 | paramSurfaceType2.getUniqueID() << 16 | paramCompositeType.getUniqueID() << 8 | paramSurfaceType1.getUniqueID();
  }
  
  protected GraphicsPrimitive(String paramString, int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    this.methodSignature = paramString;
    this.sourceType = paramSurfaceType1;
    this.compositeType = paramCompositeType;
    this.destType = paramSurfaceType2;
    if (paramSurfaceType1 == null || paramCompositeType == null || paramSurfaceType2 == null) {
      this.uniqueID = paramInt << 24;
    } else {
      this.uniqueID = makeUniqueID(paramInt, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    } 
  }
  
  protected GraphicsPrimitive(long paramLong, String paramString, int paramInt, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    this.pNativePrim = paramLong;
    this.methodSignature = paramString;
    this.sourceType = paramSurfaceType1;
    this.compositeType = paramCompositeType;
    this.destType = paramSurfaceType2;
    if (paramSurfaceType1 == null || paramCompositeType == null || paramSurfaceType2 == null) {
      this.uniqueID = paramInt << 24;
    } else {
      this.uniqueID = makeUniqueID(paramInt, paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    } 
  }
  
  public final int getUniqueID() {
    return this.uniqueID;
  }
  
  public final String getSignature() {
    return this.methodSignature;
  }
  
  public final int getPrimTypeID() {
    return this.uniqueID >>> 24;
  }
  
  public final long getNativePrim() {
    return this.pNativePrim;
  }
  
  public final SurfaceType getSourceType() {
    return this.sourceType;
  }
  
  public final CompositeType getCompositeType() {
    return this.compositeType;
  }
  
  public final SurfaceType getDestType() {
    return this.destType;
  }
  
  public final boolean satisfies(String paramString, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    if (paramString != this.methodSignature)
      return false; 
    while (true) {
      if (paramSurfaceType1 == null)
        return false; 
      if (paramSurfaceType1.equals(this.sourceType))
        break; 
      paramSurfaceType1 = paramSurfaceType1.getSuperType();
    } 
    while (true) {
      if (paramCompositeType == null)
        return false; 
      if (paramCompositeType.equals(this.compositeType))
        break; 
      paramCompositeType = paramCompositeType.getSuperType();
    } 
    while (true) {
      if (paramSurfaceType2 == null)
        return false; 
      if (paramSurfaceType2.equals(this.destType))
        break; 
      paramSurfaceType2 = paramSurfaceType2.getSuperType();
    } 
    return true;
  }
  
  final boolean satisfiesSameAs(GraphicsPrimitive paramGraphicsPrimitive) {
    return (this.methodSignature == paramGraphicsPrimitive.methodSignature && this.sourceType
      .equals(paramGraphicsPrimitive.sourceType) && this.compositeType
      .equals(paramGraphicsPrimitive.compositeType) && this.destType
      .equals(paramGraphicsPrimitive.destType));
  }
  
  static {
    GetPropertyAction getPropertyAction = new GetPropertyAction("sun.java2d.trace");
    String str = AccessController.<String>doPrivileged(getPropertyAction);
    if (str != null) {
      boolean bool = false;
      int i = 0;
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        if (str1.equalsIgnoreCase("count")) {
          i |= 0x4;
          continue;
        } 
        if (str1.equalsIgnoreCase("log")) {
          i |= 0x1;
          continue;
        } 
        if (str1.equalsIgnoreCase("timestamp")) {
          i |= 0x2;
          continue;
        } 
        if (str1.equalsIgnoreCase("verbose")) {
          bool = true;
          continue;
        } 
        if (str1.regionMatches(true, 0, "out:", 0, 4)) {
          tracefile = str1.substring(4);
          continue;
        } 
        if (!str1.equalsIgnoreCase("help"))
          System.err.println("unrecognized token: " + str1); 
        System.err.println("usage: -Dsun.java2d.trace=[log[,timestamp]],[count],[out:<filename>],[help],[verbose]");
      } 
      if (bool) {
        System.err.print("GraphicsPrimitive logging ");
        if ((i & 0x1) != 0) {
          System.err.println("enabled");
          System.err.print("GraphicsPrimitive timetamps ");
          if ((i & 0x2) != 0) {
            System.err.println("enabled");
          } else {
            System.err.println("disabled");
          } 
        } else {
          System.err.println("[and timestamps] disabled");
        } 
        System.err.print("GraphicsPrimitive invocation counts ");
        if ((i & 0x4) != 0) {
          System.err.println("enabled");
        } else {
          System.err.println("disabled");
        } 
        System.err.print("GraphicsPrimitive trace output to ");
        if (tracefile == null) {
          System.err.println("System.err");
        } else {
          System.err.println("file '" + tracefile + "'");
        } 
      } 
      traceflags = i;
    } 
  }
  
  public static boolean tracingEnabled() {
    return (traceflags != 0);
  }
  
  private static PrintStream getTraceOutputFile() {
    if (traceout == null)
      if (tracefile != null) {
        FileOutputStream fileOutputStream = AccessController.<FileOutputStream>doPrivileged((PrivilegedAction<FileOutputStream>)new Object());
        if (fileOutputStream != null) {
          traceout = new PrintStream(fileOutputStream);
        } else {
          traceout = System.err;
        } 
      } else {
        traceout = System.err;
      }  
    return traceout;
  }
  
  public static synchronized void tracePrimitive(Object paramObject) {
    if ((traceflags & 0x4) != 0) {
      if (traceMap == null) {
        traceMap = new HashMap<>();
        TraceReporter.setShutdownHook();
      } 
      Object object = traceMap.get(paramObject);
      if (object == null) {
        object = new int[1];
        traceMap.put(paramObject, object);
      } 
      ((int[])object)[0] = ((int[])object)[0] + 1;
    } 
    if ((traceflags & 0x1) != 0) {
      PrintStream printStream = getTraceOutputFile();
      if ((traceflags & 0x2) != 0)
        printStream.print(System.currentTimeMillis() + ": "); 
      printStream.println(paramObject);
    } 
  }
  
  protected void setupGeneralBinaryOp(GeneralBinaryOp paramGeneralBinaryOp) {
    Blit blit2, blit3;
    int i = paramGeneralBinaryOp.getPrimTypeID();
    String str = paramGeneralBinaryOp.getSignature();
    SurfaceType surfaceType1 = paramGeneralBinaryOp.getSourceType();
    CompositeType compositeType = paramGeneralBinaryOp.getCompositeType();
    SurfaceType surfaceType2 = paramGeneralBinaryOp.getDestType();
    Blit blit1 = createConverter(surfaceType1, SurfaceType.IntArgb);
    GraphicsPrimitive graphicsPrimitive = GraphicsPrimitiveMgr.locatePrim(i, SurfaceType.IntArgb, compositeType, surfaceType2);
    if (graphicsPrimitive != null) {
      blit2 = null;
      blit3 = null;
    } else {
      graphicsPrimitive = getGeneralOp(i, compositeType);
      if (graphicsPrimitive == null)
        throw new InternalError("Cannot construct general op for " + str + " " + compositeType); 
      blit2 = createConverter(surfaceType2, SurfaceType.IntArgb);
      blit3 = createConverter(SurfaceType.IntArgb, surfaceType2);
    } 
    paramGeneralBinaryOp.setPrimitives(blit1, blit2, graphicsPrimitive, blit3);
  }
  
  protected void setupGeneralUnaryOp(GeneralUnaryOp paramGeneralUnaryOp) {
    int i = paramGeneralUnaryOp.getPrimTypeID();
    String str = paramGeneralUnaryOp.getSignature();
    CompositeType compositeType = paramGeneralUnaryOp.getCompositeType();
    SurfaceType surfaceType = paramGeneralUnaryOp.getDestType();
    Blit blit1 = createConverter(surfaceType, SurfaceType.IntArgb);
    GraphicsPrimitive graphicsPrimitive = getGeneralOp(i, compositeType);
    Blit blit2 = createConverter(SurfaceType.IntArgb, surfaceType);
    if (blit1 == null || graphicsPrimitive == null || blit2 == null)
      throw new InternalError("Cannot construct binary op for " + compositeType + " " + surfaceType); 
    paramGeneralUnaryOp.setPrimitives(blit1, graphicsPrimitive, blit2);
  }
  
  protected static Blit createConverter(SurfaceType paramSurfaceType1, SurfaceType paramSurfaceType2) {
    if (paramSurfaceType1.equals(paramSurfaceType2))
      return null; 
    Blit blit = Blit.getFromCache(paramSurfaceType1, CompositeType.SrcNoEa, paramSurfaceType2);
    if (blit == null)
      throw new InternalError("Cannot construct converter for " + paramSurfaceType1 + "=>" + paramSurfaceType2); 
    return blit;
  }
  
  protected static SurfaceData convertFrom(Blit paramBlit, SurfaceData paramSurfaceData1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, SurfaceData paramSurfaceData2) {
    return convertFrom(paramBlit, paramSurfaceData1, paramInt1, paramInt2, paramInt3, paramInt4, paramSurfaceData2, 2);
  }
  
  protected static SurfaceData convertFrom(Blit paramBlit, SurfaceData paramSurfaceData1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, SurfaceData paramSurfaceData2, int paramInt5) {
    if (paramSurfaceData2 != null) {
      Rectangle rectangle = paramSurfaceData2.getBounds();
      if (paramInt3 > rectangle.width || paramInt4 > rectangle.height)
        paramSurfaceData2 = null; 
    } 
    if (paramSurfaceData2 == null) {
      BufferedImage bufferedImage = new BufferedImage(paramInt3, paramInt4, paramInt5);
      paramSurfaceData2 = BufImgSurfaceData.createData(bufferedImage);
    } 
    paramBlit.Blit(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, null, paramInt1, paramInt2, 0, 0, paramInt3, paramInt4);
    return paramSurfaceData2;
  }
  
  protected static void convertTo(Blit paramBlit, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramBlit != null)
      paramBlit.Blit(paramSurfaceData1, paramSurfaceData2, AlphaComposite.Src, paramRegion, 0, 0, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  protected static GraphicsPrimitive getGeneralOp(int paramInt, CompositeType paramCompositeType) {
    return GraphicsPrimitiveMgr.locatePrim(paramInt, SurfaceType.IntArgb, paramCompositeType, SurfaceType.IntArgb);
  }
  
  public static String simplename(Field[] paramArrayOfField, Object paramObject) {
    for (byte b = 0; b < paramArrayOfField.length; b++) {
      Field field = paramArrayOfField[b];
      try {
        if (paramObject == field.get(null))
          return field.getName(); 
      } catch (Exception exception) {}
    } 
    return "\"" + paramObject.toString() + "\"";
  }
  
  public static String simplename(SurfaceType paramSurfaceType) {
    return simplename(SurfaceType.class.getDeclaredFields(), paramSurfaceType);
  }
  
  public static String simplename(CompositeType paramCompositeType) {
    return simplename(CompositeType.class.getDeclaredFields(), paramCompositeType);
  }
  
  public String toString() {
    if (this.cachedname == null) {
      String str = this.methodSignature;
      int i = str.indexOf('(');
      if (i >= 0)
        str = str.substring(0, i); 
      this
        
        .cachedname = getClass().getName() + "::" + str + "(" + simplename(this.sourceType) + ", " + simplename(this.compositeType) + ", " + simplename(this.destType) + ")";
    } 
    return this.cachedname;
  }
  
  public abstract GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2);
  
  public abstract GraphicsPrimitive traceWrap();
  
  public static class GraphicsPrimitive {}
  
  protected static interface GraphicsPrimitive {}
  
  protected static interface GeneralBinaryOp {
    void setPrimitives(Blit param1Blit1, Blit param1Blit2, GraphicsPrimitive param1GraphicsPrimitive, Blit param1Blit3);
    
    SurfaceType getSourceType();
    
    CompositeType getCompositeType();
    
    SurfaceType getDestType();
    
    String getSignature();
    
    int getPrimTypeID();
  }
}
