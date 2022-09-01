package sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

abstract class UnicodeDecoder extends CharsetDecoder {
  protected static final char BYTE_ORDER_MARK = '﻿';
  
  protected static final char REVERSED_MARK = '￾';
  
  protected static final int NONE = 0;
  
  protected static final int BIG = 1;
  
  protected static final int LITTLE = 2;
  
  private final int expectedByteOrder;
  
  private int currentByteOrder;
  
  private int defaultByteOrder = 1;
  
  public UnicodeDecoder(Charset paramCharset, int paramInt) {
    super(paramCharset, 0.5F, 1.0F);
    this.expectedByteOrder = this.currentByteOrder = paramInt;
  }
  
  public UnicodeDecoder(Charset paramCharset, int paramInt1, int paramInt2) {
    this(paramCharset, paramInt1);
    this.defaultByteOrder = paramInt2;
  }
  
  private char decode(int paramInt1, int paramInt2) {
    if (this.currentByteOrder == 1)
      return (char)(paramInt1 << 8 | paramInt2); 
    return (char)(paramInt2 << 8 | paramInt1);
  }
  
  protected CoderResult decodeLoop(ByteBuffer paramByteBuffer, CharBuffer paramCharBuffer) {
    int i = paramByteBuffer.position();
    try {
      while (paramByteBuffer.remaining() > 1) {
        int j = paramByteBuffer.get() & 0xFF;
        int k = paramByteBuffer.get() & 0xFF;
        if (this.currentByteOrder == 0) {
          char c1 = (char)(j << 8 | k);
          if (c1 == '﻿') {
            this.currentByteOrder = 1;
            i += 2;
            continue;
          } 
          if (c1 == '￾') {
            this.currentByteOrder = 2;
            i += 2;
            continue;
          } 
          this.currentByteOrder = this.defaultByteOrder;
        } 
        char c = decode(j, k);
        if (c == '￾')
          return CoderResult.malformedForLength(2); 
        if (Character.isSurrogate(c)) {
          if (Character.isHighSurrogate(c)) {
            if (paramByteBuffer.remaining() < 2)
              return CoderResult.UNDERFLOW; 
            char c1 = decode(paramByteBuffer.get() & 0xFF, paramByteBuffer.get() & 0xFF);
            if (!Character.isLowSurrogate(c1))
              return CoderResult.malformedForLength(4); 
            if (paramCharBuffer.remaining() < 2)
              return CoderResult.OVERFLOW; 
            i += 4;
            paramCharBuffer.put(c);
            paramCharBuffer.put(c1);
            continue;
          } 
          return CoderResult.malformedForLength(2);
        } 
        if (!paramCharBuffer.hasRemaining())
          return CoderResult.OVERFLOW; 
        i += 2;
        paramCharBuffer.put(c);
      } 
      return CoderResult.UNDERFLOW;
    } finally {
      paramByteBuffer.position(i);
    } 
  }
  
  protected void implReset() {
    this.currentByteOrder = this.expectedByteOrder;
  }
}
