package java.io;

public interface ObjectInput extends DataInput, AutoCloseable {
  Object readObject() throws ClassNotFoundException, IOException;
  
  int read() throws IOException;
  
  int read(byte[] paramArrayOfbyte) throws IOException;
  
  int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  long skip(long paramLong) throws IOException;
  
  int available() throws IOException;
  
  void close() throws IOException;
}
