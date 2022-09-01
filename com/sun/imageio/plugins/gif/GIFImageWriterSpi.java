package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class GIFImageWriterSpi extends ImageWriterSpi {
  private static final String vendorName = "Oracle Corporation";
  
  private static final String version = "1.0";
  
  private static final String[] names = new String[] { "gif", "GIF" };
  
  private static final String[] suffixes = new String[] { "gif" };
  
  private static final String[] MIMETypes = new String[] { "image/gif" };
  
  private static final String writerClassName = "com.sun.imageio.plugins.gif.GIFImageWriter";
  
  private static final String[] readerSpiNames = new String[] { "com.sun.imageio.plugins.gif.GIFImageReaderSpi" };
  
  public GIFImageWriterSpi() {
    super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.gif.GIFImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", null, null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", null, null);
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    if (paramImageTypeSpecifier == null)
      throw new IllegalArgumentException("type == null!"); 
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    boolean bool = (sampleModel.getNumBands() == 1 && sampleModel.getSampleSize(0) <= 8 && sampleModel.getWidth() <= 65535 && sampleModel.getHeight() <= 65535 && (colorModel == null || colorModel.getComponentSize()[0] <= 8)) ? true : false;
    if (bool)
      return true; 
    return PaletteBuilder.canCreatePalette(paramImageTypeSpecifier);
  }
  
  public String getDescription(Locale paramLocale) {
    return "Standard GIF image writer";
  }
  
  public ImageWriter createWriterInstance(Object paramObject) {
    return new GIFImageWriter(this);
  }
}
