package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.InnocuousThread;

class T2KFontScaler extends FontScaler {
  private int[] bwGlyphs;
  
  private static final int TRUETYPE_FONT = 1;
  
  private static final int TYPE1_FONT = 2;
  
  private long layoutTablePtr;
  
  private void initBWGlyphs() {
    if (this.font.get() != null && "Courier New".equals(((Font2D)this.font.get()).getFontName(null))) {
      this.bwGlyphs = new int[2];
      CharToGlyphMapper charToGlyphMapper = ((Font2D)this.font.get()).getMapper();
      this.bwGlyphs[0] = charToGlyphMapper.charToGlyph('W');
      this.bwGlyphs[1] = charToGlyphMapper.charToGlyph('w');
    } 
  }
  
  static {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            FontManagerNativeLibrary.load();
            System.loadLibrary("t2k");
            return null;
          }
        });
    initIDs(T2KFontScaler.class);
  }
  
  private void invalidateScaler() throws FontScalerException {
    this.nativeScaler = 0L;
    this.font = null;
    throw new FontScalerException();
  }
  
  public T2KFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2) {
    this.layoutTablePtr = 0L;
    byte b = 1;
    if (paramFont2D instanceof Type1Font)
      b = 2; 
    this.font = new WeakReference<>(paramFont2D);
    initBWGlyphs();
    this.nativeScaler = initNativeScaler(paramFont2D, b, paramInt1, paramBoolean, paramInt2, this.bwGlyphs);
  }
  
  synchronized StrikeMetrics getFontMetrics(long paramLong) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getFontMetricsNative(this.font.get(), paramLong, this.nativeScaler); 
    return getNullScaler().getFontMetrics(0L);
  }
  
  synchronized float getGlyphAdvance(long paramLong, int paramInt) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphAdvanceNative(this.font.get(), paramLong, this.nativeScaler, paramInt); 
    return getNullScaler().getGlyphAdvance(0L, paramInt);
  }
  
  synchronized void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) throws FontScalerException {
    if (this.nativeScaler != 0L) {
      getGlyphMetricsNative(this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat);
    } else {
      getNullScaler().getGlyphMetrics(0L, paramInt, paramFloat);
    } 
  }
  
  synchronized long getGlyphImage(long paramLong, int paramInt) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphImageNative(this.font.get(), paramLong, this.nativeScaler, paramInt); 
    return getNullScaler().getGlyphImage(0L, paramInt);
  }
  
  synchronized Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphOutlineBoundsNative(this.font.get(), paramLong, this.nativeScaler, paramInt); 
    return getNullScaler().getGlyphOutlineBounds(0L, paramInt);
  }
  
  synchronized GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphOutlineNative(this.font.get(), paramLong, this.nativeScaler, paramInt, paramFloat1, paramFloat2); 
    return getNullScaler().getGlyphOutline(0L, paramInt, paramFloat1, paramFloat2);
  }
  
  synchronized GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfint, int paramInt, float paramFloat1, float paramFloat2) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphVectorOutlineNative(this.font.get(), paramLong, this.nativeScaler, paramArrayOfint, paramInt, paramFloat1, paramFloat2); 
    return getNullScaler().getGlyphVectorOutline(0L, paramArrayOfint, paramInt, paramFloat1, paramFloat2);
  }
  
  synchronized int getNumGlyphs() throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getNumGlyphsNative(this.nativeScaler); 
    return getNullScaler().getNumGlyphs();
  }
  
  synchronized int getMissingGlyphCode() throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getMissingGlyphCodeNative(this.nativeScaler); 
    return getNullScaler().getMissingGlyphCode();
  }
  
  synchronized int getGlyphCode(char paramChar) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphCodeNative(this.nativeScaler, paramChar); 
    return getNullScaler().getGlyphCode(paramChar);
  }
  
  synchronized long getLayoutTableCache() throws FontScalerException {
    if (this.nativeScaler == 0L)
      return getNullScaler().getLayoutTableCache(); 
    if (this.layoutTablePtr == 0L)
      this.layoutTablePtr = getLayoutTableCacheNative(this.nativeScaler); 
    return this.layoutTablePtr;
  }
  
  private synchronized void disposeScaler() {
    disposeNativeScaler(this.nativeScaler, this.layoutTablePtr);
    this.nativeScaler = 0L;
    this.layoutTablePtr = 0L;
  }
  
  public synchronized void dispose() {
    if (this.nativeScaler != 0L || this.layoutTablePtr != 0L) {
      T2KFontScaler t2KFontScaler = this;
      Object object = new Object(this, t2KFontScaler);
      (new InnocuousThread((Runnable)object)).start();
    } 
  }
  
  synchronized Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) throws FontScalerException {
    if (this.nativeScaler != 0L)
      return getGlyphPointNative(this.font.get(), paramLong, this.nativeScaler, paramInt1, paramInt2); 
    return getNullScaler().getGlyphPoint(paramLong, paramInt1, paramInt2);
  }
  
  synchronized long getUnitsPerEm() {
    if (this.nativeScaler != 0L)
      return getUnitsPerEMNative(this.nativeScaler); 
    return getNullScaler().getUnitsPerEm();
  }
  
  synchronized long createScalerContext(double[] paramArrayOfdouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean) {
    if (this.nativeScaler != 0L)
      return createScalerContextNative(this.nativeScaler, paramArrayOfdouble, paramInt1, paramInt2, paramFloat1, paramFloat2, paramBoolean); 
    return NullFontScaler.getNullScalerContext();
  }
  
  void invalidateScalerContext(long paramLong) {}
  
  private static native void initIDs(Class paramClass);
  
  private native long initNativeScaler(Font2D paramFont2D, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int[] paramArrayOfint);
  
  private native StrikeMetrics getFontMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2);
  
  private native float getGlyphAdvanceNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native void getGlyphMetricsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, Point2D.Float paramFloat);
  
  private native long getGlyphImageNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native Rectangle2D.Float getGlyphOutlineBoundsNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt);
  
  private native GeneralPath getGlyphOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt, float paramFloat1, float paramFloat2);
  
  private native GeneralPath getGlyphVectorOutlineNative(Font2D paramFont2D, long paramLong1, long paramLong2, int[] paramArrayOfint, int paramInt, float paramFloat1, float paramFloat2);
  
  private native int getGlyphCodeNative(long paramLong, char paramChar);
  
  private native long getLayoutTableCacheNative(long paramLong);
  
  private native void disposeNativeScaler(long paramLong1, long paramLong2);
  
  private native int getNumGlyphsNative(long paramLong);
  
  private native int getMissingGlyphCodeNative(long paramLong);
  
  private native long getUnitsPerEMNative(long paramLong);
  
  private native long createScalerContextNative(long paramLong, double[] paramArrayOfdouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean);
  
  private native Point2D.Float getGlyphPointNative(Font2D paramFont2D, long paramLong1, long paramLong2, int paramInt1, int paramInt2);
}
