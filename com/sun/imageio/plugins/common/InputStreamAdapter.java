package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public class InputStreamAdapter extends InputStream {
  ImageInputStream stream;
  
  public InputStreamAdapter(ImageInputStream paramImageInputStream) {
    this.stream = paramImageInputStream;
  }
  
  public int read() throws IOException {
    return this.stream.read();
  }
  
  public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    return this.stream.read(paramArrayOfbyte, paramInt1, paramInt2);
  }
}
