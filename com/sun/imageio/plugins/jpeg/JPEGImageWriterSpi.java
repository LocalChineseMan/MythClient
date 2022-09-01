package com.sun.imageio.plugins.jpeg;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class JPEGImageWriterSpi extends ImageWriterSpi {
  private static String[] readerSpiNames = new String[] { "com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi" };
  
  public JPEGImageWriterSpi() {
    super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
  }
  
  public String getDescription(Locale paramLocale) {
    return "Standard JPEG Image Writer";
  }
  
  public boolean isFormatLossless() {
    return false;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    int[] arrayOfInt = sampleModel.getSampleSize();
    int i = arrayOfInt[0];
    for (byte b = 1; b < arrayOfInt.length; b++) {
      if (arrayOfInt[b] > i)
        i = arrayOfInt[b]; 
    } 
    if (i < 1 || i > 8)
      return false; 
    return true;
  }
  
  public ImageWriter createWriterInstance(Object paramObject) throws IIOException {
    return new JPEGImageWriter(this);
  }
}
