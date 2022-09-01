package javax.imageio.spi;

import javax.imageio.ImageTranscoder;

public abstract class ImageTranscoderSpi extends IIOServiceProvider {
  protected ImageTranscoderSpi() {}
  
  public ImageTranscoderSpi(String paramString1, String paramString2) {
    super(paramString1, paramString2);
  }
  
  public abstract String getReaderServiceProviderName();
  
  public abstract String getWriterServiceProviderName();
  
  public abstract ImageTranscoder createTranscoderInstance();
}
