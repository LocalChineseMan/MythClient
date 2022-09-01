package sun.reflect;

interface ByteVector {
  int getLength();
  
  byte get(int paramInt);
  
  void put(int paramInt, byte paramByte);
  
  void add(byte paramByte);
  
  void trim();
  
  byte[] getData();
}
