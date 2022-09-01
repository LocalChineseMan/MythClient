package com.sun.imageio.plugins.wbmp;

import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriterSpi extends ImageWriterSpi {
  private static String[] readerSpiNames = new String[] { "com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi" };
  
  private static String[] formatNames = new String[] { "wbmp", "WBMP" };
  
  private static String[] entensions = new String[] { "wbmp" };
  
  private static String[] mimeType = new String[] { "image/vnd.wap.wbmp" };
  
  private boolean registered = false;
  
  public WBMPImageWriterSpi() {
    super("Oracle Corporation", "1.0", formatNames, entensions, mimeType, "com.sun.imageio.plugins.wbmp.WBMPImageWriter", new Class[] { ImageOutputStream.class }, readerSpiNames, true, null, null, null, null, true, null, null, null, null);
  }
  
  public String getDescription(Locale paramLocale) {
    return "Standard WBMP Image Writer";
  }
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {
    if (this.registered)
      return; 
    this.registered = true;
  }
  
  public boolean canEncodeImage(ImageTypeSpecifier paramImageTypeSpecifier) {
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    if (!(sampleModel instanceof java.awt.image.MultiPixelPackedSampleModel))
      return false; 
    if (sampleModel.getSampleSize(0) != 1)
      return false; 
    return true;
  }
  
  public ImageWriter createWriterInstance(Object paramObject) throws IIOException {
    return new WBMPImageWriter(this);
  }
}
