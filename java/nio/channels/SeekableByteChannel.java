package java.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface SeekableByteChannel extends ByteChannel {
  int read(ByteBuffer paramByteBuffer) throws IOException;
  
  int write(ByteBuffer paramByteBuffer) throws IOException;
  
  long position() throws IOException;
  
  SeekableByteChannel position(long paramLong) throws IOException;
  
  long size() throws IOException;
  
  SeekableByteChannel truncate(long paramLong) throws IOException;
}
