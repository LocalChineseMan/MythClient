package com.sun.org.apache.xerces.internal.util;

public class SymbolTable {
  protected static final int TABLE_SIZE = 173;
  
  protected Entry[] fBuckets = null;
  
  protected int fTableSize;
  
  public SymbolTable() {
    this(173);
  }
  
  public SymbolTable(int tableSize) {
    this.fTableSize = tableSize;
    this.fBuckets = new Entry[this.fTableSize];
  }
  
  public String addSymbol(String symbol) {
    int hash = hash(symbol);
    int bucket = hash % this.fTableSize;
    int length = symbol.length();
    Entry entry;
    for (entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
      if (length == entry.characters.length && hash == entry.hashCode && 
        symbol.regionMatches(0, entry.symbol, 0, length))
        return entry.symbol; 
    } 
    entry = new Entry(symbol, this.fBuckets[bucket]);
    entry.hashCode = hash;
    this.fBuckets[bucket] = entry;
    return entry.symbol;
  }
  
  public String addSymbol(char[] buffer, int offset, int length) {
    int hash = hash(buffer, offset, length);
    int bucket = hash % this.fTableSize;
    Entry entry;
    for (entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
      if (length == entry.characters.length && hash == entry.hashCode) {
        int i = 0;
        while (true) {
          if (i < length) {
            if (buffer[offset + i] != entry.characters[i])
              break; 
            i++;
            continue;
          } 
          return entry.symbol;
        } 
      } 
    } 
    entry = new Entry(buffer, offset, length, this.fBuckets[bucket]);
    this.fBuckets[bucket] = entry;
    entry.hashCode = hash;
    return entry.symbol;
  }
  
  public int hash(String symbol) {
    int code = 0;
    int length = symbol.length();
    for (int i = 0; i < length; i++)
      code = code * 37 + symbol.charAt(i); 
    return code & Integer.MAX_VALUE;
  }
  
  public int hash(char[] buffer, int offset, int length) {
    int code = 0;
    for (int i = 0; i < length; i++)
      code = code * 37 + buffer[offset + i]; 
    return code & Integer.MAX_VALUE;
  }
  
  public boolean containsSymbol(String symbol) {
    int hash = hash(symbol);
    int bucket = hash % this.fTableSize;
    int length = symbol.length();
    for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
      if (length == entry.characters.length && hash == entry.hashCode && 
        symbol.regionMatches(0, entry.symbol, 0, length))
        return true; 
    } 
    return false;
  }
  
  public boolean containsSymbol(char[] buffer, int offset, int length) {
    int hash = hash(buffer, offset, length);
    int bucket = hash % this.fTableSize;
    for (Entry entry = this.fBuckets[bucket]; entry != null; entry = entry.next) {
      if (length == entry.characters.length && hash == entry.hashCode) {
        int i = 0;
        while (true) {
          if (i < length) {
            if (buffer[offset + i] != entry.characters[i])
              break; 
            i++;
            continue;
          } 
          return true;
        } 
      } 
    } 
    return false;
  }
  
  protected static final class Entry {
    public String symbol;
    
    int hashCode = 0;
    
    public char[] characters;
    
    public Entry next;
    
    public Entry(String symbol, Entry next) {
      this.symbol = symbol.intern();
      this.characters = new char[symbol.length()];
      symbol.getChars(0, this.characters.length, this.characters, 0);
      this.next = next;
    }
    
    public Entry(char[] ch, int offset, int length, Entry next) {
      this.characters = new char[length];
      System.arraycopy(ch, offset, this.characters, 0, length);
      this.symbol = (new String(this.characters)).intern();
      this.next = next;
    }
  }
}
