package com.sun.jndi.dns;

import javax.naming.CommunicationException;
import javax.naming.NamingException;

class Header {
  static final int HEADER_SIZE = 12;
  
  static final short QR_BIT = -32768;
  
  static final short OPCODE_MASK = 30720;
  
  static final int OPCODE_SHIFT = 11;
  
  static final short AA_BIT = 1024;
  
  static final short TC_BIT = 512;
  
  static final short RD_BIT = 256;
  
  static final short RA_BIT = 128;
  
  static final short RCODE_MASK = 15;
  
  int xid;
  
  boolean query;
  
  int opcode;
  
  boolean authoritative;
  
  boolean truncated;
  
  boolean recursionDesired;
  
  boolean recursionAvail;
  
  int rcode;
  
  int numQuestions;
  
  int numAnswers;
  
  int numAuthorities;
  
  int numAdditionals;
  
  Header(byte[] paramArrayOfbyte, int paramInt) throws NamingException {
    decode(paramArrayOfbyte, paramInt);
  }
  
  private void decode(byte[] paramArrayOfbyte, int paramInt) throws NamingException {
    try {
      boolean bool = false;
      if (paramInt < 12)
        throw new CommunicationException("DNS error: corrupted message header"); 
      this.xid = getShort(paramArrayOfbyte, bool);
      bool += true;
      short s = (short)getShort(paramArrayOfbyte, bool);
      bool += true;
      this.query = ((s & Short.MIN_VALUE) == 0);
      this.opcode = (s & 0x7800) >>> 11;
      this.authoritative = ((s & 0x400) != 0);
      this.truncated = ((s & 0x200) != 0);
      this.recursionDesired = ((s & 0x100) != 0);
      this.recursionAvail = ((s & 0x80) != 0);
      this.rcode = s & 0xF;
      this.numQuestions = getShort(paramArrayOfbyte, bool);
      bool += true;
      this.numAnswers = getShort(paramArrayOfbyte, bool);
      bool += true;
      this.numAuthorities = getShort(paramArrayOfbyte, bool);
      bool += true;
      this.numAdditionals = getShort(paramArrayOfbyte, bool);
      bool += true;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new CommunicationException("DNS error: corrupted message header");
    } 
  }
  
  private static int getShort(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF;
  }
}
