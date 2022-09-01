package com.sun.imageio.stream;

import java.io.Closeable;
import java.io.IOException;
import sun.java2d.DisposerRecord;

public class CloseableDisposerRecord implements DisposerRecord {
  private Closeable closeable;
  
  public CloseableDisposerRecord(Closeable paramCloseable) {
    this.closeable = paramCloseable;
  }
  
  public synchronized void dispose() {
    if (this.closeable != null)
      try {
        this.closeable.close();
      } catch (IOException iOException) {
      
      } finally {
        this.closeable = null;
      }  
  }
}
