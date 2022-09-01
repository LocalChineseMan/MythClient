package javax.imageio;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageTypeSpecifier {
  protected ColorModel colorModel;
  
  protected SampleModel sampleModel;
  
  private static ImageTypeSpecifier[] BISpecifier;
  
  private static ColorSpace sRGB = ColorSpace.getInstance(1000);
  
  static {
    BISpecifier = new ImageTypeSpecifier[14];
  }
  
  private ImageTypeSpecifier() {}
  
  public ImageTypeSpecifier(ColorModel paramColorModel, SampleModel paramSampleModel) {
    if (paramColorModel == null)
      throw new IllegalArgumentException("colorModel == null!"); 
    if (paramSampleModel == null)
      throw new IllegalArgumentException("sampleModel == null!"); 
    if (!paramColorModel.isCompatibleSampleModel(paramSampleModel))
      throw new IllegalArgumentException("sampleModel is incompatible with colorModel!"); 
    this.colorModel = paramColorModel;
    this.sampleModel = paramSampleModel;
  }
  
  public ImageTypeSpecifier(RenderedImage paramRenderedImage) {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("image == null!"); 
    this.colorModel = paramRenderedImage.getColorModel();
    this.sampleModel = paramRenderedImage.getSampleModel();
  }
  
  static class Packed extends ImageTypeSpecifier {
    ColorSpace colorSpace;
    
    int redMask;
    
    int greenMask;
    
    int blueMask;
    
    int alphaMask;
    
    int transferType;
    
    boolean isAlphaPremultiplied;
    
    public Packed(ColorSpace param1ColorSpace, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, boolean param1Boolean) {
      if (param1ColorSpace == null)
        throw new IllegalArgumentException("colorSpace == null!"); 
      if (param1ColorSpace.getType() != 5)
        throw new IllegalArgumentException("colorSpace is not of type TYPE_RGB!"); 
      if (param1Int5 != 0 && param1Int5 != 1 && param1Int5 != 3)
        throw new IllegalArgumentException("Bad value for transferType!"); 
      if (param1Int1 == 0 && param1Int2 == 0 && param1Int3 == 0 && param1Int4 == 0)
        throw new IllegalArgumentException("No mask has at least 1 bit set!"); 
      this.colorSpace = param1ColorSpace;
      this.redMask = param1Int1;
      this.greenMask = param1Int2;
      this.blueMask = param1Int3;
      this.alphaMask = param1Int4;
      this.transferType = param1Int5;
      this.isAlphaPremultiplied = param1Boolean;
      byte b = 32;
      this.colorModel = new DirectColorModel(param1ColorSpace, b, param1Int1, param1Int2, param1Int3, param1Int4, param1Boolean, param1Int5);
      this.sampleModel = this.colorModel.createCompatibleSampleModel(1, 1);
    }
  }
  
  public static ImageTypeSpecifier createPacked(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean) {
    return new Packed(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean);
  }
  
  static ColorModel createComponentCM(ColorSpace paramColorSpace, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool = paramBoolean1 ? true : true;
    int[] arrayOfInt = new int[paramInt1];
    int i = DataBuffer.getDataTypeSize(paramInt2);
    for (byte b = 0; b < paramInt1; b++)
      arrayOfInt[b] = i; 
    return new ComponentColorModel(paramColorSpace, arrayOfInt, paramBoolean1, paramBoolean2, bool, paramInt2);
  }
  
  static class Interleaved extends ImageTypeSpecifier {
    ColorSpace colorSpace;
    
    int[] bandOffsets;
    
    int dataType;
    
    boolean hasAlpha;
    
    boolean isAlphaPremultiplied;
    
    public Interleaved(ColorSpace param1ColorSpace, int[] param1ArrayOfint, int param1Int, boolean param1Boolean1, boolean param1Boolean2) {
      if (param1ColorSpace == null)
        throw new IllegalArgumentException("colorSpace == null!"); 
      if (param1ArrayOfint == null)
        throw new IllegalArgumentException("bandOffsets == null!"); 
      int i = param1ColorSpace.getNumComponents() + (param1Boolean1 ? 1 : 0);
      if (param1ArrayOfint.length != i)
        throw new IllegalArgumentException("bandOffsets.length is wrong!"); 
      if (param1Int != 0 && param1Int != 2 && param1Int != 1 && param1Int != 3 && param1Int != 4 && param1Int != 5)
        throw new IllegalArgumentException("Bad value for dataType!"); 
      this.colorSpace = param1ColorSpace;
      this.bandOffsets = (int[])param1ArrayOfint.clone();
      this.dataType = param1Int;
      this.hasAlpha = param1Boolean1;
      this.isAlphaPremultiplied = param1Boolean2;
      this
        .colorModel = ImageTypeSpecifier.createComponentCM(param1ColorSpace, param1ArrayOfint.length, param1Int, param1Boolean1, param1Boolean2);
      int j = param1ArrayOfint[0];
      int k = j;
      int m;
      for (m = 0; m < param1ArrayOfint.length; m++) {
        int n = param1ArrayOfint[m];
        j = Math.min(n, j);
        k = Math.max(n, k);
      } 
      m = k - j + 1;
      byte b = 1;
      boolean bool = true;
      this.sampleModel = new PixelInterleavedSampleModel(param1Int, b, bool, m, b * m, param1ArrayOfint);
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null || !(param1Object instanceof Interleaved))
        return false; 
      Interleaved interleaved = (Interleaved)param1Object;
      if (!this.colorSpace.equals(interleaved.colorSpace) || this.dataType != interleaved.dataType || this.hasAlpha != interleaved.hasAlpha || this.isAlphaPremultiplied != interleaved.isAlphaPremultiplied || this.bandOffsets.length != interleaved.bandOffsets.length)
        return false; 
      for (byte b = 0; b < this.bandOffsets.length; b++) {
        if (this.bandOffsets[b] != interleaved.bandOffsets[b])
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      return super.hashCode() + 4 * this.bandOffsets.length + 25 * this.dataType + (this.hasAlpha ? 17 : 18);
    }
  }
  
  public static ImageTypeSpecifier createInterleaved(ColorSpace paramColorSpace, int[] paramArrayOfint, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    return new Interleaved(paramColorSpace, paramArrayOfint, paramInt, paramBoolean1, paramBoolean2);
  }
  
  public static ImageTypeSpecifier createBanded(ColorSpace paramColorSpace, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    return new Banded(paramColorSpace, paramArrayOfint1, paramArrayOfint2, paramInt, paramBoolean1, paramBoolean2);
  }
  
  public static ImageTypeSpecifier createGrayscale(int paramInt1, int paramInt2, boolean paramBoolean) {
    return new Grayscale(paramInt1, paramInt2, paramBoolean, false, false);
  }
  
  public static ImageTypeSpecifier createGrayscale(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    return new Grayscale(paramInt1, paramInt2, paramBoolean1, true, paramBoolean2);
  }
  
  static class ImageTypeSpecifier {}
  
  static class ImageTypeSpecifier {}
  
  static class Indexed extends ImageTypeSpecifier {
    byte[] redLUT;
    
    byte[] greenLUT;
    
    byte[] blueLUT;
    
    byte[] alphaLUT = null;
    
    int bits;
    
    int dataType;
    
    public Indexed(byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, byte[] param1ArrayOfbyte3, byte[] param1ArrayOfbyte4, int param1Int1, int param1Int2) {
      if (param1ArrayOfbyte1 == null || param1ArrayOfbyte2 == null || param1ArrayOfbyte3 == null)
        throw new IllegalArgumentException("LUT is null!"); 
      if (param1Int1 != 1 && param1Int1 != 2 && param1Int1 != 4 && param1Int1 != 8 && param1Int1 != 16)
        throw new IllegalArgumentException("Bad value for bits!"); 
      if (param1Int2 != 0 && param1Int2 != 2 && param1Int2 != 1 && param1Int2 != 3)
        throw new IllegalArgumentException("Bad value for dataType!"); 
      if ((param1Int1 > 8 && param1Int2 == 0) || (param1Int1 > 16 && param1Int2 != 3))
        throw new IllegalArgumentException("Too many bits for dataType!"); 
      int i = 1 << param1Int1;
      if (param1ArrayOfbyte1.length != i || param1ArrayOfbyte2.length != i || param1ArrayOfbyte3.length != i || (param1ArrayOfbyte4 != null && param1ArrayOfbyte4.length != i))
        throw new IllegalArgumentException("LUT has improper length!"); 
      this.redLUT = (byte[])param1ArrayOfbyte1.clone();
      this.greenLUT = (byte[])param1ArrayOfbyte2.clone();
      this.blueLUT = (byte[])param1ArrayOfbyte3.clone();
      if (param1ArrayOfbyte4 != null)
        this.alphaLUT = (byte[])param1ArrayOfbyte4.clone(); 
      this.bits = param1Int1;
      this.dataType = param1Int2;
      if (param1ArrayOfbyte4 == null) {
        this.colorModel = new IndexColorModel(param1Int1, param1ArrayOfbyte1.length, param1ArrayOfbyte1, param1ArrayOfbyte2, param1ArrayOfbyte3);
      } else {
        this.colorModel = new IndexColorModel(param1Int1, param1ArrayOfbyte1.length, param1ArrayOfbyte1, param1ArrayOfbyte2, param1ArrayOfbyte3, param1ArrayOfbyte4);
      } 
      if ((param1Int1 == 8 && param1Int2 == 0) || (param1Int1 == 16 && (param1Int2 == 2 || param1Int2 == 1))) {
        int[] arrayOfInt = { 0 };
        this.sampleModel = new PixelInterleavedSampleModel(param1Int2, 1, 1, 1, 1, arrayOfInt);
      } else {
        this.sampleModel = new MultiPixelPackedSampleModel(param1Int2, 1, 1, param1Int1);
      } 
    }
  }
  
  public static ImageTypeSpecifier createIndexed(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4, int paramInt1, int paramInt2) {
    return new Indexed(paramArrayOfbyte1, paramArrayOfbyte2, paramArrayOfbyte3, paramArrayOfbyte4, paramInt1, paramInt2);
  }
  
  public static ImageTypeSpecifier createFromBufferedImageType(int paramInt) {
    if (paramInt >= 1 && paramInt <= 13)
      return getSpecifier(paramInt); 
    if (paramInt == 0)
      throw new IllegalArgumentException("Cannot create from TYPE_CUSTOM!"); 
    throw new IllegalArgumentException("Invalid BufferedImage type!");
  }
  
  public static ImageTypeSpecifier createFromRenderedImage(RenderedImage paramRenderedImage) {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("image == null!"); 
    if (paramRenderedImage instanceof BufferedImage) {
      int i = ((BufferedImage)paramRenderedImage).getType();
      if (i != 0)
        return getSpecifier(i); 
    } 
    return new ImageTypeSpecifier(paramRenderedImage);
  }
  
  public int getBufferedImageType() {
    BufferedImage bufferedImage = createBufferedImage(1, 1);
    return bufferedImage.getType();
  }
  
  public int getNumComponents() {
    return this.colorModel.getNumComponents();
  }
  
  public int getNumBands() {
    return this.sampleModel.getNumBands();
  }
  
  public int getBitsPerBand(int paramInt) {
    if ((((paramInt < 0) ? 1 : 0) | ((paramInt >= getNumBands()) ? 1 : 0)) != 0)
      throw new IllegalArgumentException("band out of range!"); 
    return this.sampleModel.getSampleSize(paramInt);
  }
  
  public SampleModel getSampleModel() {
    return this.sampleModel;
  }
  
  public SampleModel getSampleModel(int paramInt1, int paramInt2) {
    if (paramInt1 * paramInt2 > 2147483647L)
      throw new IllegalArgumentException("width*height > Integer.MAX_VALUE!"); 
    return this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
  }
  
  public ColorModel getColorModel() {
    return this.colorModel;
  }
  
  public BufferedImage createBufferedImage(int paramInt1, int paramInt2) {
    try {
      SampleModel sampleModel = getSampleModel(paramInt1, paramInt2);
      WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, new Point(0, 0));
      return new BufferedImage(this.colorModel, writableRaster, this.colorModel
          .isAlphaPremultiplied(), new Hashtable<>());
    } catch (NegativeArraySizeException negativeArraySizeException) {
      throw new IllegalArgumentException("Array size > Integer.MAX_VALUE!");
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof ImageTypeSpecifier))
      return false; 
    ImageTypeSpecifier imageTypeSpecifier = (ImageTypeSpecifier)paramObject;
    return (this.colorModel.equals(imageTypeSpecifier.colorModel) && this.sampleModel
      .equals(imageTypeSpecifier.sampleModel));
  }
  
  public int hashCode() {
    return 9 * this.colorModel.hashCode() + 14 * this.sampleModel.hashCode();
  }
  
  private static ImageTypeSpecifier getSpecifier(int paramInt) {
    if (BISpecifier[paramInt] == null)
      BISpecifier[paramInt] = createSpecifier(paramInt); 
    return BISpecifier[paramInt];
  }
  
  private static ImageTypeSpecifier createSpecifier(int paramInt) {
    BufferedImage bufferedImage;
    IndexColorModel indexColorModel;
    int i;
    byte[] arrayOfByte1;
    byte[] arrayOfByte2;
    byte[] arrayOfByte3;
    byte[] arrayOfByte4;
    switch (paramInt) {
      case 1:
        return createPacked(sRGB, 16711680, 65280, 255, 0, 3, false);
      case 2:
        return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, false);
      case 3:
        return createPacked(sRGB, 16711680, 65280, 255, -16777216, 3, true);
      case 4:
        return createPacked(sRGB, 255, 65280, 16711680, 0, 3, false);
      case 5:
        return createInterleaved(sRGB, new int[] { 2, 1, 0 }, 0, false, false);
      case 6:
        return createInterleaved(sRGB, new int[] { 3, 2, 1, 0 }, 0, true, false);
      case 7:
        return createInterleaved(sRGB, new int[] { 3, 2, 1, 0 }, 0, true, true);
      case 8:
        return createPacked(sRGB, 63488, 2016, 31, 0, 1, false);
      case 9:
        return createPacked(sRGB, 31744, 992, 31, 0, 1, false);
      case 10:
        return createGrayscale(8, 0, false);
      case 11:
        return createGrayscale(16, 1, false);
      case 12:
        return createGrayscale(1, 0, false);
      case 13:
        bufferedImage = new BufferedImage(1, 1, 13);
        indexColorModel = (IndexColorModel)bufferedImage.getColorModel();
        i = indexColorModel.getMapSize();
        arrayOfByte1 = new byte[i];
        arrayOfByte2 = new byte[i];
        arrayOfByte3 = new byte[i];
        arrayOfByte4 = new byte[i];
        indexColorModel.getReds(arrayOfByte1);
        indexColorModel.getGreens(arrayOfByte2);
        indexColorModel.getBlues(arrayOfByte3);
        indexColorModel.getAlphas(arrayOfByte4);
        return createIndexed(arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4, 8, 0);
    } 
    throw new IllegalArgumentException("Invalid BufferedImage type!");
  }
}
