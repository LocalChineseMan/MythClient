package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class UTF8Reader extends Reader {
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  
  private static final boolean DEBUG_READ = false;
  
  protected InputStream fInputStream;
  
  protected byte[] fBuffer;
  
  protected int fOffset;
  
  private int fSurrogate = -1;
  
  private MessageFormatter fFormatter = null;
  
  private Locale fLocale = null;
  
  public UTF8Reader(InputStream inputStream) {
    this(inputStream, 2048, new XMLMessageFormatter(), Locale.getDefault());
  }
  
  public UTF8Reader(InputStream inputStream, MessageFormatter messageFormatter, Locale locale) {
    this(inputStream, 2048, messageFormatter, locale);
  }
  
  public UTF8Reader(InputStream inputStream, int size, MessageFormatter messageFormatter, Locale locale) {
    this.fInputStream = inputStream;
    BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
    this.fBuffer = ba.getByteBuffer(size);
    if (this.fBuffer == null)
      this.fBuffer = new byte[size]; 
    this.fFormatter = messageFormatter;
    this.fLocale = locale;
  }
  
  public int read() throws IOException {
    int c = this.fSurrogate;
    if (this.fSurrogate == -1) {
      int index = 0;
      int b0 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
      if (b0 == -1)
        return -1; 
      if (b0 < 128) {
        c = (char)b0;
      } else if ((b0 & 0xE0) == 192 && (b0 & 0x1E) != 0) {
        int b1 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b1 == -1)
          expectedByte(2, 2); 
        if ((b1 & 0xC0) != 128)
          invalidByte(2, 2, b1); 
        c = b0 << 6 & 0x7C0 | b1 & 0x3F;
      } else if ((b0 & 0xF0) == 224) {
        int b1 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b1 == -1)
          expectedByte(2, 3); 
        if ((b1 & 0xC0) != 128 || (b0 == 237 && b1 >= 160) || ((b0 & 0xF) == 0 && (b1 & 0x20) == 0))
          invalidByte(2, 3, b1); 
        int b2 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b2 == -1)
          expectedByte(3, 3); 
        if ((b2 & 0xC0) != 128)
          invalidByte(3, 3, b2); 
        c = b0 << 12 & 0xF000 | b1 << 6 & 0xFC0 | b2 & 0x3F;
      } else if ((b0 & 0xF8) == 240) {
        int b1 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b1 == -1)
          expectedByte(2, 4); 
        if ((b1 & 0xC0) != 128 || ((b1 & 0x30) == 0 && (b0 & 0x7) == 0))
          invalidByte(2, 3, b1); 
        int b2 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b2 == -1)
          expectedByte(3, 4); 
        if ((b2 & 0xC0) != 128)
          invalidByte(3, 3, b2); 
        int b3 = (index == this.fOffset) ? this.fInputStream.read() : (this.fBuffer[index++] & 0xFF);
        if (b3 == -1)
          expectedByte(4, 4); 
        if ((b3 & 0xC0) != 128)
          invalidByte(4, 4, b3); 
        int uuuuu = b0 << 2 & 0x1C | b1 >> 4 & 0x3;
        if (uuuuu > 16)
          invalidSurrogate(uuuuu); 
        int wwww = uuuuu - 1;
        int hs = 0xD800 | wwww << 6 & 0x3C0 | b1 << 2 & 0x3C | b2 >> 4 & 0x3;
        int ls = 0xDC00 | b2 << 6 & 0x3C0 | b3 & 0x3F;
        c = hs;
        this.fSurrogate = ls;
      } else {
        invalidByte(1, 1, b0);
      } 
    } else {
      this.fSurrogate = -1;
    } 
    return c;
  }
  
  public int read(char[] ch, int offset, int length) throws IOException {
    int out = offset;
    if (this.fSurrogate != -1) {
      ch[offset + 1] = (char)this.fSurrogate;
      this.fSurrogate = -1;
      length--;
      out++;
    } 
    int count = 0;
    if (this.fOffset == 0) {
      if (length > this.fBuffer.length)
        length = this.fBuffer.length; 
      count = this.fInputStream.read(this.fBuffer, 0, length);
      if (count == -1)
        return -1; 
      count += out - offset;
    } else {
      count = this.fOffset;
      this.fOffset = 0;
    } 
    int total = count;
    byte byte0 = 0;
    int in;
    for (in = 0; in < total; ) {
      byte byte1 = this.fBuffer[in];
      if (byte1 >= 0) {
        ch[out++] = (char)byte1;
        in++;
      } 
    } 
    for (; in < total; in++) {
      byte byte1 = this.fBuffer[in];
      if (byte1 >= 0) {
        ch[out++] = (char)byte1;
      } else {
        int b0 = byte1 & 0xFF;
        if ((b0 & 0xE0) == 192 && (b0 & 0x1E) != 0) {
          int b1 = -1;
          if (++in < total) {
            b1 = this.fBuffer[in] & 0xFF;
          } else {
            b1 = this.fInputStream.read();
            if (b1 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fOffset = 1;
                return out - offset;
              } 
              expectedByte(2, 2);
            } 
            count++;
          } 
          if ((b1 & 0xC0) != 128) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fOffset = 2;
              return out - offset;
            } 
            invalidByte(2, 2, b1);
          } 
          int c = b0 << 6 & 0x7C0 | b1 & 0x3F;
          ch[out++] = (char)c;
          count--;
        } else if ((b0 & 0xF0) == 224) {
          int b1 = -1;
          if (++in < total) {
            b1 = this.fBuffer[in] & 0xFF;
          } else {
            b1 = this.fInputStream.read();
            if (b1 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fOffset = 1;
                return out - offset;
              } 
              expectedByte(2, 3);
            } 
            count++;
          } 
          if ((b1 & 0xC0) != 128 || (b0 == 237 && b1 >= 160) || ((b0 & 0xF) == 0 && (b1 & 0x20) == 0)) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fOffset = 2;
              return out - offset;
            } 
            invalidByte(2, 3, b1);
          } 
          int b2 = -1;
          if (++in < total) {
            b2 = this.fBuffer[in] & 0xFF;
          } else {
            b2 = this.fInputStream.read();
            if (b2 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fBuffer[1] = (byte)b1;
                this.fOffset = 2;
                return out - offset;
              } 
              expectedByte(3, 3);
            } 
            count++;
          } 
          if ((b2 & 0xC0) != 128) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fBuffer[2] = (byte)b2;
              this.fOffset = 3;
              return out - offset;
            } 
            invalidByte(3, 3, b2);
          } 
          int c = b0 << 12 & 0xF000 | b1 << 6 & 0xFC0 | b2 & 0x3F;
          ch[out++] = (char)c;
          count -= 2;
        } else if ((b0 & 0xF8) == 240) {
          int b1 = -1;
          if (++in < total) {
            b1 = this.fBuffer[in] & 0xFF;
          } else {
            b1 = this.fInputStream.read();
            if (b1 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fOffset = 1;
                return out - offset;
              } 
              expectedByte(2, 4);
            } 
            count++;
          } 
          if ((b1 & 0xC0) != 128 || ((b1 & 0x30) == 0 && (b0 & 0x7) == 0)) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fOffset = 2;
              return out - offset;
            } 
            invalidByte(2, 4, b1);
          } 
          int b2 = -1;
          if (++in < total) {
            b2 = this.fBuffer[in] & 0xFF;
          } else {
            b2 = this.fInputStream.read();
            if (b2 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fBuffer[1] = (byte)b1;
                this.fOffset = 2;
                return out - offset;
              } 
              expectedByte(3, 4);
            } 
            count++;
          } 
          if ((b2 & 0xC0) != 128) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fBuffer[2] = (byte)b2;
              this.fOffset = 3;
              return out - offset;
            } 
            invalidByte(3, 4, b2);
          } 
          int b3 = -1;
          if (++in < total) {
            b3 = this.fBuffer[in] & 0xFF;
          } else {
            b3 = this.fInputStream.read();
            if (b3 == -1) {
              if (out > offset) {
                this.fBuffer[0] = (byte)b0;
                this.fBuffer[1] = (byte)b1;
                this.fBuffer[2] = (byte)b2;
                this.fOffset = 3;
                return out - offset;
              } 
              expectedByte(4, 4);
            } 
            count++;
          } 
          if ((b3 & 0xC0) != 128) {
            if (out > offset) {
              this.fBuffer[0] = (byte)b0;
              this.fBuffer[1] = (byte)b1;
              this.fBuffer[2] = (byte)b2;
              this.fBuffer[3] = (byte)b3;
              this.fOffset = 4;
              return out - offset;
            } 
            invalidByte(4, 4, b2);
          } 
          int uuuuu = b0 << 2 & 0x1C | b1 >> 4 & 0x3;
          if (uuuuu > 16)
            invalidSurrogate(uuuuu); 
          int wwww = uuuuu - 1;
          int zzzz = b1 & 0xF;
          int yyyyyy = b2 & 0x3F;
          int xxxxxx = b3 & 0x3F;
          int hs = 0xD800 | wwww << 6 & 0x3C0 | zzzz << 2 | yyyyyy >> 4;
          int ls = 0xDC00 | yyyyyy << 6 & 0x3C0 | xxxxxx;
          ch[out++] = (char)hs;
          ch[out++] = (char)ls;
          count -= 2;
        } else {
          if (out > offset) {
            this.fBuffer[0] = (byte)b0;
            this.fOffset = 1;
            return out - offset;
          } 
          invalidByte(1, 1, b0);
        } 
      } 
    } 
    return count;
  }
  
  public long skip(long n) throws IOException {
    long remaining = n;
    char[] ch = new char[this.fBuffer.length];
    while (true) {
      int length = (ch.length < remaining) ? ch.length : (int)remaining;
      int count = read(ch, 0, length);
      if (count > 0) {
        remaining -= count;
        if (remaining <= 0L)
          break; 
        continue;
      } 
      break;
    } 
    long skipped = n - remaining;
    return skipped;
  }
  
  public boolean ready() throws IOException {
    return false;
  }
  
  public boolean markSupported() {
    return false;
  }
  
  public void mark(int readAheadLimit) throws IOException {
    throw new IOException(this.fFormatter.formatMessage(this.fLocale, "OperationNotSupported", new Object[] { "mark()", "UTF-8" }));
  }
  
  public void reset() throws IOException {
    this.fOffset = 0;
    this.fSurrogate = -1;
  }
  
  public void close() throws IOException {
    BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
    ba.returnByteBuffer(this.fBuffer);
    this.fBuffer = null;
    this.fInputStream.close();
  }
  
  private void expectedByte(int position, int count) throws MalformedByteSequenceException {
    throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[] { Integer.toString(position), Integer.toString(count) });
  }
  
  private void invalidByte(int position, int count, int c) throws MalformedByteSequenceException {
    throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[] { Integer.toString(position), Integer.toString(count) });
  }
  
  private void invalidSurrogate(int uuuuu) throws MalformedByteSequenceException {
    throw new MalformedByteSequenceException(this.fFormatter, this.fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[] { Integer.toHexString(uuuuu) });
  }
}
