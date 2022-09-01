package java.io;

public interface ObjectOutput extends DataOutput, AutoCloseable {
  void writeObject(Object paramObject) throws IOException;
  
  void write(int paramInt) throws IOException;
  
  void write(byte[] paramArrayOfbyte) throws IOException;
  
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  void flush() throws IOException;
  
  void close() throws IOException;
}
