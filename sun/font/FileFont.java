package sun.font;

import java.awt.FontFormatException;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.ref.Reference;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public abstract class FileFont extends PhysicalFont {
  protected boolean useJavaRasterizer = true;
  
  protected int fileSize;
  
  protected FontScaler scaler;
  
  protected boolean checkedNatives;
  
  protected boolean useNatives;
  
  protected NativeFont[] nativeFonts;
  
  protected char[] glyphToCharMap;
  
  FileFont(String paramString, Object paramObject) throws FontFormatException {
    super(paramString, paramObject);
  }
  
  FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc) {
    if (!this.checkedNatives)
      checkUseNatives(); 
    return new FileFontStrike(this, paramFontStrikeDesc);
  }
  
  protected boolean checkUseNatives() {
    this.checkedNatives = true;
    return this.useNatives;
  }
  
  protected abstract void close();
  
  abstract ByteBuffer readBlock(int paramInt1, int paramInt2);
  
  public boolean canDoStyle(int paramInt) {
    return true;
  }
  
  void setFileToRemove(File paramFile, CreatedFontTracker paramCreatedFontTracker) {
    Disposer.addObjectRecord(this, (DisposerRecord)new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker));
  }
  
  static void setFileToRemove(Object paramObject, File paramFile, CreatedFontTracker paramCreatedFontTracker) {
    Disposer.addObjectRecord(paramObject, (DisposerRecord)new CreatedFontFileDisposerRecord(paramFile, paramCreatedFontTracker));
  }
  
  synchronized void deregisterFontAndClearStrikeCache() {
    SunFontManager sunFontManager = SunFontManager.getInstance();
    sunFontManager.deRegisterBadFont((Font2D)this);
    for (Reference<FileFontStrike> reference : (Iterable<Reference<FileFontStrike>>)this.strikeCache.values()) {
      if (reference != null) {
        FileFontStrike fileFontStrike = reference.get();
        if (fileFontStrike != null && fileFontStrike.pScalerContext != 0L)
          this.scaler.invalidateScalerContext(fileFontStrike.pScalerContext); 
      } 
    } 
    if (this.scaler != null)
      this.scaler.dispose(); 
    this.scaler = FontScaler.getNullScaler();
  }
  
  StrikeMetrics getFontMetrics(long paramLong) {
    try {
      return getScaler().getFontMetrics(paramLong);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getFontMetrics(paramLong);
    } 
  }
  
  float getGlyphAdvance(long paramLong, int paramInt) {
    try {
      return getScaler().getGlyphAdvance(paramLong, paramInt);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphAdvance(paramLong, paramInt);
    } 
  }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) {
    try {
      getScaler().getGlyphMetrics(paramLong, paramInt, paramFloat);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      getGlyphMetrics(paramLong, paramInt, paramFloat);
    } 
  }
  
  long getGlyphImage(long paramLong, int paramInt) {
    try {
      return getScaler().getGlyphImage(paramLong, paramInt);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphImage(paramLong, paramInt);
    } 
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) {
    try {
      return getScaler().getGlyphOutlineBounds(paramLong, paramInt);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphOutlineBounds(paramLong, paramInt);
    } 
  }
  
  GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) {
    try {
      return getScaler().getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphOutline(paramLong, paramInt, paramFloat1, paramFloat2);
    } 
  }
  
  GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfint, int paramInt, float paramFloat1, float paramFloat2) {
    try {
      return getScaler().getGlyphVectorOutline(paramLong, paramArrayOfint, paramInt, paramFloat1, paramFloat2);
    } catch (FontScalerException fontScalerException) {
      this.scaler = FontScaler.getNullScaler();
      return getGlyphVectorOutline(paramLong, paramArrayOfint, paramInt, paramFloat1, paramFloat2);
    } 
  }
  
  protected abstract FontScaler getScaler();
  
  protected long getUnitsPerEm() {
    return getScaler().getUnitsPerEm();
  }
  
  private static class CreatedFontFileDisposerRecord implements DisposerRecord {
    File fontFile = null;
    
    CreatedFontTracker tracker;
    
    private CreatedFontFileDisposerRecord(File param1File, CreatedFontTracker param1CreatedFontTracker) {
      this.fontFile = param1File;
      this.tracker = param1CreatedFontTracker;
    }
    
    public void dispose() {
      AccessController.doPrivileged((PrivilegedAction<?>)new Object(this));
    }
  }
  
  protected String getPublicFileName() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null)
      return this.platName; 
    boolean bool = true;
    try {
      securityManager.checkPropertyAccess("java.io.tmpdir");
    } catch (SecurityException securityException) {
      bool = false;
    } 
    if (bool)
      return this.platName; 
    File file = new File(this.platName);
    Boolean bool1 = Boolean.FALSE;
    try {
      bool1 = AccessController.<Boolean>doPrivileged((PrivilegedExceptionAction<Boolean>)new Object(this, file));
    } catch (PrivilegedActionException privilegedActionException) {
      bool1 = Boolean.TRUE;
    } 
    return bool1.booleanValue() ? "temp file" : this.platName;
  }
}
