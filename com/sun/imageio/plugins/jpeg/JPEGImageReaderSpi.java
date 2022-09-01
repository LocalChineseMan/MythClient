package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class JPEGImageReaderSpi extends ImageReaderSpi {
  private static String[] writerSpiNames = new String[] { "com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi" };
  
  public JPEGImageReaderSpi() {
    super("Oracle Corporation", "0.5", JPEG.names, JPEG.suffixes, JPEG.MIMETypes, "com.sun.imageio.plugins.jpeg.JPEGImageReader", new Class[] { ImageInputStream.class }, writerSpiNames, true, "javax_imageio_jpeg_stream_1.0", "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat", null, null, true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
  }
  
  public String getDescription(Locale paramLocale) {
    return "Standard JPEG Image Reader";
  }
  
  public boolean canDecodeInput(Object paramObject) throws IOException {
    if (!(paramObject instanceof ImageInputStream))
      return false; 
    ImageInputStream imageInputStream = (ImageInputStream)paramObject;
    imageInputStream.mark();
    int i = imageInputStream.read();
    int j = imageInputStream.read();
    imageInputStream.reset();
    if (i == 255 && j == 216)
      return true; 
    return false;
  }
  
  public ImageReader createReaderInstance(Object paramObject) throws IIOException {
    return new JPEGImageReader(this);
  }
}
