package sun.security.util;

import java.io.IOException;
import java.util.ArrayList;

class DerIndefLenConverter {
  private static final int TAG_MASK = 31;
  
  private static final int FORM_MASK = 32;
  
  private static final int CLASS_MASK = 192;
  
  private static final int LEN_LONG = 128;
  
  private static final int LEN_MASK = 127;
  
  private static final int SKIP_EOC_BYTES = 2;
  
  private byte[] data;
  
  private byte[] newData;
  
  private int newDataPos;
  
  private int dataPos;
  
  private int dataSize;
  
  private int index;
  
  private int unresolved = 0;
  
  private ArrayList<Object> ndefsList = new ArrayList();
  
  private int numOfTotalLenBytes = 0;
  
  private boolean isEOC(int paramInt) {
    return ((paramInt & 0x1F) == 0 && (paramInt & 0x20) == 0 && (paramInt & 0xC0) == 0);
  }
  
  static boolean isLongForm(int paramInt) {
    return ((paramInt & 0x80) == 128);
  }
  
  static boolean isIndefinite(int paramInt) {
    return (isLongForm(paramInt) && (paramInt & 0x7F) == 0);
  }
  
  private void parseTag() throws IOException {
    if (this.dataPos == this.dataSize)
      return; 
    if (isEOC(this.data[this.dataPos]) && this.data[this.dataPos + 1] == 0) {
      int i = 0;
      Object object = null;
      int j;
      for (j = this.ndefsList.size() - 1; j >= 0; j--) {
        object = this.ndefsList.get(j);
        if (object instanceof Integer)
          break; 
        i += ((byte[])object).length - 3;
      } 
      if (j < 0)
        throw new IOException("EOC does not have matching indefinite-length tag"); 
      int k = this.dataPos - ((Integer)object).intValue() + i;
      byte[] arrayOfByte = getLengthBytes(k);
      this.ndefsList.set(j, arrayOfByte);
      this.unresolved--;
      this.numOfTotalLenBytes += arrayOfByte.length - 3;
    } 
    this.dataPos++;
  }
  
  private void writeTag() {
    if (this.dataPos == this.dataSize)
      return; 
    byte b = this.data[this.dataPos++];
    if (isEOC(b) && this.data[this.dataPos] == 0) {
      this.dataPos++;
      writeTag();
    } else {
      this.newData[this.newDataPos++] = (byte)b;
    } 
  }
  
  private int parseLength() throws IOException {
    int i = 0;
    if (this.dataPos == this.dataSize)
      return i; 
    int j = this.data[this.dataPos++] & 0xFF;
    if (isIndefinite(j)) {
      this.ndefsList.add(new Integer(this.dataPos));
      this.unresolved++;
      return i;
    } 
    if (isLongForm(j)) {
      j &= 0x7F;
      if (j > 4)
        throw new IOException("Too much data"); 
      if (this.dataSize - this.dataPos < j + 1)
        throw new IOException("Too little data"); 
      for (byte b = 0; b < j; b++)
        i = (i << 8) + (this.data[this.dataPos++] & 0xFF); 
      if (i < 0)
        throw new IOException("Invalid length bytes"); 
    } else {
      i = j & 0x7F;
    } 
    return i;
  }
  
  private void writeLengthAndValue() throws IOException {
    if (this.dataPos == this.dataSize)
      return; 
    int i = 0;
    int j = this.data[this.dataPos++] & 0xFF;
    if (isIndefinite(j)) {
      byte[] arrayOfByte = (byte[])this.ndefsList.get(this.index++);
      System.arraycopy(arrayOfByte, 0, this.newData, this.newDataPos, arrayOfByte.length);
      this.newDataPos += arrayOfByte.length;
      return;
    } 
    if (isLongForm(j)) {
      j &= 0x7F;
      for (byte b = 0; b < j; b++)
        i = (i << 8) + (this.data[this.dataPos++] & 0xFF); 
      if (i < 0)
        throw new IOException("Invalid length bytes"); 
    } else {
      i = j & 0x7F;
    } 
    writeLength(i);
    writeValue(i);
  }
  
  private void writeLength(int paramInt) {
    if (paramInt < 128) {
      this.newData[this.newDataPos++] = (byte)paramInt;
    } else if (paramInt < 256) {
      this.newData[this.newDataPos++] = -127;
      this.newData[this.newDataPos++] = (byte)paramInt;
    } else if (paramInt < 65536) {
      this.newData[this.newDataPos++] = -126;
      this.newData[this.newDataPos++] = (byte)(paramInt >> 8);
      this.newData[this.newDataPos++] = (byte)paramInt;
    } else if (paramInt < 16777216) {
      this.newData[this.newDataPos++] = -125;
      this.newData[this.newDataPos++] = (byte)(paramInt >> 16);
      this.newData[this.newDataPos++] = (byte)(paramInt >> 8);
      this.newData[this.newDataPos++] = (byte)paramInt;
    } else {
      this.newData[this.newDataPos++] = -124;
      this.newData[this.newDataPos++] = (byte)(paramInt >> 24);
      this.newData[this.newDataPos++] = (byte)(paramInt >> 16);
      this.newData[this.newDataPos++] = (byte)(paramInt >> 8);
      this.newData[this.newDataPos++] = (byte)paramInt;
    } 
  }
  
  private byte[] getLengthBytes(int paramInt) {
    byte[] arrayOfByte;
    byte b = 0;
    if (paramInt < 128) {
      arrayOfByte = new byte[1];
      arrayOfByte[b++] = (byte)paramInt;
    } else if (paramInt < 256) {
      arrayOfByte = new byte[2];
      arrayOfByte[b++] = -127;
      arrayOfByte[b++] = (byte)paramInt;
    } else if (paramInt < 65536) {
      arrayOfByte = new byte[3];
      arrayOfByte[b++] = -126;
      arrayOfByte[b++] = (byte)(paramInt >> 8);
      arrayOfByte[b++] = (byte)paramInt;
    } else if (paramInt < 16777216) {
      arrayOfByte = new byte[4];
      arrayOfByte[b++] = -125;
      arrayOfByte[b++] = (byte)(paramInt >> 16);
      arrayOfByte[b++] = (byte)(paramInt >> 8);
      arrayOfByte[b++] = (byte)paramInt;
    } else {
      arrayOfByte = new byte[5];
      arrayOfByte[b++] = -124;
      arrayOfByte[b++] = (byte)(paramInt >> 24);
      arrayOfByte[b++] = (byte)(paramInt >> 16);
      arrayOfByte[b++] = (byte)(paramInt >> 8);
      arrayOfByte[b++] = (byte)paramInt;
    } 
    return arrayOfByte;
  }
  
  private int getNumOfLenBytes(int paramInt) {
    byte b = 0;
    if (paramInt < 128) {
      b = 1;
    } else if (paramInt < 256) {
      b = 2;
    } else if (paramInt < 65536) {
      b = 3;
    } else if (paramInt < 16777216) {
      b = 4;
    } else {
      b = 5;
    } 
    return b;
  }
  
  private void parseValue(int paramInt) {
    this.dataPos += paramInt;
  }
  
  private void writeValue(int paramInt) {
    for (byte b = 0; b < paramInt; b++)
      this.newData[this.newDataPos++] = this.data[this.dataPos++]; 
  }
  
  byte[] convert(byte[] paramArrayOfbyte) throws IOException {
    this.data = paramArrayOfbyte;
    this.dataPos = 0;
    this.index = 0;
    this.dataSize = this.data.length;
    int i = 0;
    int j = 0;
    while (this.dataPos < this.dataSize) {
      parseTag();
      i = parseLength();
      parseValue(i);
      if (this.unresolved == 0) {
        j = this.dataSize - this.dataPos;
        this.dataSize = this.dataPos;
        break;
      } 
    } 
    if (this.unresolved != 0)
      throw new IOException("not all indef len BER resolved"); 
    this.newData = new byte[this.dataSize + this.numOfTotalLenBytes + j];
    this.dataPos = 0;
    this.newDataPos = 0;
    this.index = 0;
    while (this.dataPos < this.dataSize) {
      writeTag();
      writeLengthAndValue();
    } 
    System.arraycopy(paramArrayOfbyte, this.dataSize, this.newData, this.dataSize + this.numOfTotalLenBytes, j);
    return this.newData;
  }
}
