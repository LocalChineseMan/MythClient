package com.ibm.icu.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public abstract class Trie2 implements Iterable<Trie2.Range> {
  public static Trie2 createFromSerialized(InputStream is) throws IOException {
    ValueWidth width;
    Trie2_32 trie2_32;
    DataInputStream dis = new DataInputStream(is);
    boolean needByteSwap = false;
    UTrie2Header header = new UTrie2Header();
    header.signature = dis.readInt();
    switch (header.signature) {
      case 1416784178:
        needByteSwap = false;
        break;
      case 845771348:
        needByteSwap = true;
        header.signature = Integer.reverseBytes(header.signature);
        break;
      default:
        throw new IllegalArgumentException("Stream does not contain a serialized UTrie2");
    } 
    header.options = swapShort(needByteSwap, dis.readUnsignedShort());
    header.indexLength = swapShort(needByteSwap, dis.readUnsignedShort());
    header.shiftedDataLength = swapShort(needByteSwap, dis.readUnsignedShort());
    header.index2NullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
    header.dataNullOffset = swapShort(needByteSwap, dis.readUnsignedShort());
    header.shiftedHighStart = swapShort(needByteSwap, dis.readUnsignedShort());
    if ((header.options & 0xF) > 1)
      throw new IllegalArgumentException("UTrie2 serialized format error."); 
    if ((header.options & 0xF) == 0) {
      width = ValueWidth.BITS_16;
      Trie2 This = new Trie2_16();
    } else {
      width = ValueWidth.BITS_32;
      trie2_32 = new Trie2_32();
    } 
    ((Trie2)trie2_32).header = header;
    ((Trie2)trie2_32).indexLength = header.indexLength;
    ((Trie2)trie2_32).dataLength = header.shiftedDataLength << 2;
    ((Trie2)trie2_32).index2NullOffset = header.index2NullOffset;
    ((Trie2)trie2_32).dataNullOffset = header.dataNullOffset;
    ((Trie2)trie2_32).highStart = header.shiftedHighStart << 11;
    ((Trie2)trie2_32).highValueIndex = ((Trie2)trie2_32).dataLength - 4;
    if (width == ValueWidth.BITS_16)
      ((Trie2)trie2_32).highValueIndex += ((Trie2)trie2_32).indexLength; 
    int indexArraySize = ((Trie2)trie2_32).indexLength;
    if (width == ValueWidth.BITS_16)
      indexArraySize += ((Trie2)trie2_32).dataLength; 
    ((Trie2)trie2_32).index = new char[indexArraySize];
    int i;
    for (i = 0; i < ((Trie2)trie2_32).indexLength; i++)
      ((Trie2)trie2_32).index[i] = swapChar(needByteSwap, dis.readChar()); 
    if (width == ValueWidth.BITS_16) {
      ((Trie2)trie2_32).data16 = ((Trie2)trie2_32).indexLength;
      for (i = 0; i < ((Trie2)trie2_32).dataLength; i++)
        ((Trie2)trie2_32).index[((Trie2)trie2_32).data16 + i] = swapChar(needByteSwap, dis.readChar()); 
    } else {
      ((Trie2)trie2_32).data32 = new int[((Trie2)trie2_32).dataLength];
      for (i = 0; i < ((Trie2)trie2_32).dataLength; i++)
        ((Trie2)trie2_32).data32[i] = swapInt(needByteSwap, dis.readInt()); 
    } 
    switch (width) {
      case BITS_16:
        ((Trie2)trie2_32).data32 = null;
        ((Trie2)trie2_32).initialValue = ((Trie2)trie2_32).index[((Trie2)trie2_32).dataNullOffset];
        ((Trie2)trie2_32).errorValue = ((Trie2)trie2_32).index[((Trie2)trie2_32).data16 + 128];
        return (Trie2)trie2_32;
      case BITS_32:
        ((Trie2)trie2_32).data16 = 0;
        ((Trie2)trie2_32).initialValue = ((Trie2)trie2_32).data32[((Trie2)trie2_32).dataNullOffset];
        ((Trie2)trie2_32).errorValue = ((Trie2)trie2_32).data32[128];
        return (Trie2)trie2_32;
    } 
    throw new IllegalArgumentException("UTrie2 serialized format error.");
  }
  
  private static int swapShort(boolean needSwap, int value) {
    return needSwap ? (Short.reverseBytes((short)value) & 0xFFFF) : value;
  }
  
  private static char swapChar(boolean needSwap, char value) {
    return needSwap ? (char)Short.reverseBytes((short)value) : value;
  }
  
  private static int swapInt(boolean needSwap, int value) {
    return needSwap ? Integer.reverseBytes(value) : value;
  }
  
  public static int getVersion(InputStream is, boolean littleEndianOk) throws IOException {
    if (!is.markSupported())
      throw new IllegalArgumentException("Input stream must support mark()."); 
    is.mark(4);
    byte[] sig = new byte[4];
    int read = is.read(sig);
    is.reset();
    if (read != sig.length)
      return 0; 
    if (sig[0] == 84 && sig[1] == 114 && sig[2] == 105 && sig[3] == 101)
      return 1; 
    if (sig[0] == 84 && sig[1] == 114 && sig[2] == 105 && sig[3] == 50)
      return 2; 
    if (littleEndianOk) {
      if (sig[0] == 101 && sig[1] == 105 && sig[2] == 114 && sig[3] == 84)
        return 1; 
      if (sig[0] == 50 && sig[1] == 105 && sig[2] == 114 && sig[3] == 84)
        return 2; 
    } 
    return 0;
  }
  
  public final boolean equals(Object other) {
    if (!(other instanceof Trie2))
      return false; 
    Trie2 OtherTrie = (Trie2)other;
    Iterator<Range> otherIter = OtherTrie.iterator();
    for (Range rangeFromThis : this) {
      if (!otherIter.hasNext())
        return false; 
      Range rangeFromOther = otherIter.next();
      if (!rangeFromThis.equals(rangeFromOther))
        return false; 
    } 
    if (otherIter.hasNext())
      return false; 
    if (this.errorValue != OtherTrie.errorValue || this.initialValue != OtherTrie.initialValue)
      return false; 
    return true;
  }
  
  public int hashCode() {
    if (this.fHash == 0) {
      int hash = initHash();
      for (Range r : this)
        hash = hashInt(hash, r.hashCode()); 
      if (hash == 0)
        hash = 1; 
      this.fHash = hash;
    } 
    return this.fHash;
  }
  
  public Iterator<Range> iterator() {
    return iterator(defaultValueMapper);
  }
  
  private static ValueMapper defaultValueMapper = new ValueMapper() {
      public int map(int in) {
        return in;
      }
    };
  
  UTrie2Header header;
  
  char[] index;
  
  int data16;
  
  int[] data32;
  
  int indexLength;
  
  int dataLength;
  
  int index2NullOffset;
  
  int initialValue;
  
  int errorValue;
  
  int highStart;
  
  int highValueIndex;
  
  int dataNullOffset;
  
  int fHash;
  
  static final int UTRIE2_OPTIONS_VALUE_BITS_MASK = 15;
  
  static final int UTRIE2_SHIFT_1 = 11;
  
  static final int UTRIE2_SHIFT_2 = 5;
  
  static final int UTRIE2_SHIFT_1_2 = 6;
  
  static final int UTRIE2_OMITTED_BMP_INDEX_1_LENGTH = 32;
  
  static final int UTRIE2_CP_PER_INDEX_1_ENTRY = 2048;
  
  static final int UTRIE2_INDEX_2_BLOCK_LENGTH = 64;
  
  static final int UTRIE2_INDEX_2_MASK = 63;
  
  static final int UTRIE2_DATA_BLOCK_LENGTH = 32;
  
  static final int UTRIE2_DATA_MASK = 31;
  
  static final int UTRIE2_INDEX_SHIFT = 2;
  
  static final int UTRIE2_DATA_GRANULARITY = 4;
  
  static final int UTRIE2_INDEX_2_OFFSET = 0;
  
  static final int UTRIE2_LSCP_INDEX_2_OFFSET = 2048;
  
  static final int UTRIE2_LSCP_INDEX_2_LENGTH = 32;
  
  static final int UTRIE2_INDEX_2_BMP_LENGTH = 2080;
  
  static final int UTRIE2_UTF8_2B_INDEX_2_OFFSET = 2080;
  
  static final int UTRIE2_UTF8_2B_INDEX_2_LENGTH = 32;
  
  static final int UTRIE2_INDEX_1_OFFSET = 2112;
  
  static final int UTRIE2_MAX_INDEX_1_LENGTH = 512;
  
  static final int UTRIE2_BAD_UTF8_DATA_OFFSET = 128;
  
  static final int UTRIE2_DATA_START_OFFSET = 192;
  
  static final int UNEWTRIE2_INDEX_GAP_OFFSET = 2080;
  
  static final int UNEWTRIE2_INDEX_GAP_LENGTH = 576;
  
  static final int UNEWTRIE2_MAX_INDEX_2_LENGTH = 35488;
  
  static final int UNEWTRIE2_INDEX_1_LENGTH = 544;
  
  static final int UNEWTRIE2_MAX_DATA_LENGTH = 1115264;
  
  public Iterator<Range> iterator(ValueMapper mapper) {
    return (Iterator<Range>)new Trie2Iterator(this, mapper);
  }
  
  public Iterator<Range> iteratorForLeadSurrogate(char lead, ValueMapper mapper) {
    return (Iterator<Range>)new Trie2Iterator(this, lead, mapper);
  }
  
  public Iterator<Range> iteratorForLeadSurrogate(char lead) {
    return (Iterator<Range>)new Trie2Iterator(this, lead, defaultValueMapper);
  }
  
  protected int serializeHeader(DataOutputStream dos) throws IOException {
    int bytesWritten = 0;
    dos.writeInt(this.header.signature);
    dos.writeShort(this.header.options);
    dos.writeShort(this.header.indexLength);
    dos.writeShort(this.header.shiftedDataLength);
    dos.writeShort(this.header.index2NullOffset);
    dos.writeShort(this.header.dataNullOffset);
    dos.writeShort(this.header.shiftedHighStart);
    bytesWritten += 16;
    for (int i = 0; i < this.header.indexLength; i++)
      dos.writeChar(this.index[i]); 
    bytesWritten += this.header.indexLength;
    return bytesWritten;
  }
  
  public CharSequenceIterator charSequenceIterator(CharSequence text, int index) {
    return new CharSequenceIterator(this, text, index);
  }
  
  enum ValueWidth {
    BITS_16, BITS_32;
  }
  
  static class UTrie2Header {
    int signature;
    
    int options;
    
    int indexLength;
    
    int shiftedDataLength;
    
    int index2NullOffset;
    
    int dataNullOffset;
    
    int shiftedHighStart;
  }
  
  int rangeEnd(int start, int limitp, int val) {
    int limit = Math.min(this.highStart, limitp);
    int c;
    for (c = start + 1; c < limit && 
      get(c) == val; c++);
    if (c >= this.highStart)
      c = limitp; 
    return c - 1;
  }
  
  private static int initHash() {
    return -2128831035;
  }
  
  private static int hashByte(int h, int b) {
    h *= 16777619;
    h ^= b;
    return h;
  }
  
  private static int hashUChar32(int h, int c) {
    h = hashByte(h, c & 0xFF);
    h = hashByte(h, c >> 8 & 0xFF);
    h = hashByte(h, c >> 16);
    return h;
  }
  
  private static int hashInt(int h, int i) {
    h = hashByte(h, i & 0xFF);
    h = hashByte(h, i >> 8 & 0xFF);
    h = hashByte(h, i >> 16 & 0xFF);
    h = hashByte(h, i >> 24 & 0xFF);
    return h;
  }
  
  public abstract int get(int paramInt);
  
  public abstract int getFromU16SingleLead(char paramChar);
  
  class Trie2 {}
  
  public class Trie2 {}
  
  public static class Trie2 {}
  
  public static interface ValueMapper {
    int map(int param1Int);
  }
  
  public static class Trie2 {}
}
