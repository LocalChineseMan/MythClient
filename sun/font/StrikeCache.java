package sun.font;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.java2d.Disposer;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.misc.Unsafe;

public final class StrikeCache {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  static ReferenceQueue refQueue = Disposer.getQueue();
  
  static ArrayList<GlyphDisposedListener> disposeListeners = new ArrayList<>(1);
  
  static int MINSTRIKES = 8;
  
  static int recentStrikeIndex = 0;
  
  static FontStrike[] recentStrikes;
  
  static boolean cacheRefTypeWeak;
  
  static int nativeAddressSize;
  
  static int glyphInfoSize;
  
  static int xAdvanceOffset;
  
  static int yAdvanceOffset;
  
  static int boundsOffset;
  
  static int widthOffset;
  
  static int heightOffset;
  
  static int rowBytesOffset;
  
  static int topLeftXOffset;
  
  static int topLeftYOffset;
  
  static int pixelDataOffset;
  
  static int cacheCellOffset;
  
  static int managedOffset;
  
  static long invisibleGlyphPtr;
  
  static {
    long[] arrayOfLong = new long[13];
    getGlyphCacheDescription(arrayOfLong);
    nativeAddressSize = (int)arrayOfLong[0];
    glyphInfoSize = (int)arrayOfLong[1];
    xAdvanceOffset = (int)arrayOfLong[2];
    yAdvanceOffset = (int)arrayOfLong[3];
    widthOffset = (int)arrayOfLong[4];
    heightOffset = (int)arrayOfLong[5];
    rowBytesOffset = (int)arrayOfLong[6];
    topLeftXOffset = (int)arrayOfLong[7];
    topLeftYOffset = (int)arrayOfLong[8];
    pixelDataOffset = (int)arrayOfLong[9];
    invisibleGlyphPtr = arrayOfLong[10];
    cacheCellOffset = (int)arrayOfLong[11];
    managedOffset = (int)arrayOfLong[12];
    if (nativeAddressSize < 4)
      throw new InternalError("Unexpected address size for font data: " + nativeAddressSize); 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str1 = System.getProperty("sun.java2d.font.reftype", "soft");
            StrikeCache.cacheRefTypeWeak = str1.equals("weak");
            String str2 = System.getProperty("sun.java2d.font.minstrikes");
            if (str2 != null)
              try {
                StrikeCache.MINSTRIKES = Integer.parseInt(str2);
                if (StrikeCache.MINSTRIKES <= 0)
                  StrikeCache.MINSTRIKES = 1; 
              } catch (NumberFormatException numberFormatException) {} 
            StrikeCache.recentStrikes = new FontStrike[StrikeCache.MINSTRIKES];
            return null;
          }
        });
  }
  
  static void refStrike(FontStrike paramFontStrike) {
    int i = recentStrikeIndex;
    recentStrikes[i] = paramFontStrike;
    i++;
    if (i == MINSTRIKES)
      i = 0; 
    recentStrikeIndex = i;
  }
  
  private static final void doDispose(FontStrikeDisposer paramFontStrikeDisposer) {
    if (paramFontStrikeDisposer.intGlyphImages != null) {
      freeCachedIntMemory(paramFontStrikeDisposer.intGlyphImages, paramFontStrikeDisposer.pScalerContext);
    } else if (paramFontStrikeDisposer.longGlyphImages != null) {
      freeCachedLongMemory(paramFontStrikeDisposer.longGlyphImages, paramFontStrikeDisposer.pScalerContext);
    } else if (paramFontStrikeDisposer.segIntGlyphImages != null) {
      for (byte b = 0; b < paramFontStrikeDisposer.segIntGlyphImages.length; b++) {
        if (paramFontStrikeDisposer.segIntGlyphImages[b] != null) {
          freeCachedIntMemory(paramFontStrikeDisposer.segIntGlyphImages[b], paramFontStrikeDisposer.pScalerContext);
          paramFontStrikeDisposer.pScalerContext = 0L;
          paramFontStrikeDisposer.segIntGlyphImages[b] = null;
        } 
      } 
      if (paramFontStrikeDisposer.pScalerContext != 0L)
        freeCachedIntMemory(new int[0], paramFontStrikeDisposer.pScalerContext); 
    } else if (paramFontStrikeDisposer.segLongGlyphImages != null) {
      for (byte b = 0; b < paramFontStrikeDisposer.segLongGlyphImages.length; b++) {
        if (paramFontStrikeDisposer.segLongGlyphImages[b] != null) {
          freeCachedLongMemory(paramFontStrikeDisposer.segLongGlyphImages[b], paramFontStrikeDisposer.pScalerContext);
          paramFontStrikeDisposer.pScalerContext = 0L;
          paramFontStrikeDisposer.segLongGlyphImages[b] = null;
        } 
      } 
      if (paramFontStrikeDisposer.pScalerContext != 0L)
        freeCachedLongMemory(new long[0], paramFontStrikeDisposer.pScalerContext); 
    } else if (paramFontStrikeDisposer.pScalerContext != 0L) {
      if (longAddresses()) {
        freeCachedLongMemory(new long[0], paramFontStrikeDisposer.pScalerContext);
      } else {
        freeCachedIntMemory(new int[0], paramFontStrikeDisposer.pScalerContext);
      } 
    } 
  }
  
  private static boolean longAddresses() {
    return (nativeAddressSize == 8);
  }
  
  static void disposeStrike(FontStrikeDisposer paramFontStrikeDisposer) {
    if (Disposer.pollingQueue) {
      doDispose(paramFontStrikeDisposer);
      return;
    } 
    RenderQueue renderQueue = null;
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (!GraphicsEnvironment.isHeadless()) {
      GraphicsConfiguration graphicsConfiguration = graphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
      if (graphicsConfiguration instanceof AccelGraphicsConfig) {
        AccelGraphicsConfig accelGraphicsConfig = (AccelGraphicsConfig)graphicsConfiguration;
        BufferedContext bufferedContext = accelGraphicsConfig.getContext();
        if (bufferedContext != null)
          renderQueue = bufferedContext.getRenderQueue(); 
      } 
    } 
    if (renderQueue != null) {
      renderQueue.lock();
      try {
        renderQueue.flushAndInvokeNow((Runnable)new Object(paramFontStrikeDisposer));
      } finally {
        renderQueue.unlock();
      } 
    } else {
      doDispose(paramFontStrikeDisposer);
    } 
  }
  
  private static void freeCachedIntMemory(int[] paramArrayOfint, long paramLong) {
    synchronized (disposeListeners) {
      if (disposeListeners.size() > 0) {
        ArrayList<Long> arrayList = null;
        for (byte b = 0; b < paramArrayOfint.length; b++) {
          if (paramArrayOfint[b] != 0 && unsafe.getByte((paramArrayOfint[b] + managedOffset)) == 0) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(Long.valueOf(paramArrayOfint[b]));
          } 
        } 
        if (arrayList != null)
          notifyDisposeListeners(arrayList); 
      } 
    } 
    freeIntMemory(paramArrayOfint, paramLong);
  }
  
  private static void freeCachedLongMemory(long[] paramArrayOflong, long paramLong) {
    synchronized (disposeListeners) {
      if (disposeListeners.size() > 0) {
        ArrayList<Long> arrayList = null;
        for (byte b = 0; b < paramArrayOflong.length; b++) {
          if (paramArrayOflong[b] != 0L && unsafe
            .getByte(paramArrayOflong[b] + managedOffset) == 0) {
            if (arrayList == null)
              arrayList = new ArrayList(); 
            arrayList.add(Long.valueOf(paramArrayOflong[b]));
          } 
        } 
        if (arrayList != null)
          notifyDisposeListeners(arrayList); 
      } 
    } 
    freeLongMemory(paramArrayOflong, paramLong);
  }
  
  public static void addGlyphDisposedListener(GlyphDisposedListener paramGlyphDisposedListener) {
    synchronized (disposeListeners) {
      disposeListeners.add(paramGlyphDisposedListener);
    } 
  }
  
  private static void notifyDisposeListeners(ArrayList<Long> paramArrayList) {
    for (GlyphDisposedListener glyphDisposedListener : disposeListeners)
      glyphDisposedListener.glyphDisposed(paramArrayList); 
  }
  
  public static Reference getStrikeRef(FontStrike paramFontStrike) {
    return getStrikeRef(paramFontStrike, cacheRefTypeWeak);
  }
  
  public static Reference getStrikeRef(FontStrike paramFontStrike, boolean paramBoolean) {
    if (paramFontStrike.disposer == null) {
      if (paramBoolean)
        return new WeakReference<>(paramFontStrike); 
      return new SoftReference<>(paramFontStrike);
    } 
    if (paramBoolean)
      return new WeakDisposerRef(paramFontStrike); 
    return new SoftDisposerRef(paramFontStrike);
  }
  
  static native void getGlyphCacheDescription(long[] paramArrayOflong);
  
  static native void freeIntPointer(int paramInt);
  
  static native void freeLongPointer(long paramLong);
  
  private static native void freeIntMemory(int[] paramArrayOfint, long paramLong);
  
  private static native void freeLongMemory(long[] paramArrayOflong, long paramLong);
  
  static interface DisposableStrike {
    FontStrikeDisposer getDisposer();
  }
  
  static class SoftDisposerRef extends SoftReference implements DisposableStrike {
    private FontStrikeDisposer disposer;
    
    public FontStrikeDisposer getDisposer() {
      return this.disposer;
    }
    
    SoftDisposerRef(FontStrike param1FontStrike) {
      super((T)param1FontStrike, StrikeCache.refQueue);
      this.disposer = param1FontStrike.disposer;
      Disposer.addReference(this, this.disposer);
    }
  }
  
  static class StrikeCache {}
}
