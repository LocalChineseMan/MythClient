package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public abstract class Entity {
  public String name;
  
  public boolean inExternalSubset;
  
  public Entity() {
    clear();
  }
  
  public Entity(String name, boolean inExternalSubset) {
    this.name = name;
    this.inExternalSubset = inExternalSubset;
  }
  
  public boolean isEntityDeclInExternalSubset() {
    return this.inExternalSubset;
  }
  
  public abstract boolean isExternal();
  
  public abstract boolean isUnparsed();
  
  public void clear() {
    this.name = null;
    this.inExternalSubset = false;
  }
  
  public void setValues(Entity entity) {
    this.name = entity.name;
    this.inExternalSubset = entity.inExternalSubset;
  }
  
  public static class ScannedEntity extends Entity {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public int fBufferSize = 8192;
    
    public static final int DEFAULT_XMLDECL_BUFFER_SIZE = 28;
    
    public static final int DEFAULT_INTERNAL_BUFFER_SIZE = 1024;
    
    public InputStream stream;
    
    public Reader reader;
    
    public XMLResourceIdentifier entityLocation;
    
    public String encoding;
    
    public boolean literal;
    
    public boolean isExternal;
    
    public String version;
    
    public char[] ch = null;
    
    public int position;
    
    public int count;
    
    public int lineNumber = 1;
    
    public int columnNumber = 1;
    
    boolean declaredEncoding = false;
    
    boolean externallySpecifiedEncoding = false;
    
    public String xmlVersion = "1.0";
    
    public int fTotalCountTillLastLoad;
    
    public int fLastCount;
    
    public int baseCharOffset;
    
    public int startPosition;
    
    public boolean mayReadChunks;
    
    public boolean xmlDeclChunkRead = false;
    
    public String getEncodingName() {
      return this.encoding;
    }
    
    public String getEntityVersion() {
      return this.version;
    }
    
    public void setEntityVersion(String version) {
      this.version = version;
    }
    
    public Reader getEntityReader() {
      return this.reader;
    }
    
    public InputStream getEntityInputStream() {
      return this.stream;
    }
    
    public ScannedEntity(String name, XMLResourceIdentifier entityLocation, InputStream stream, Reader reader, String encoding, boolean literal, boolean mayReadChunks, boolean isExternal) {
      this.name = name;
      this.entityLocation = entityLocation;
      this.stream = stream;
      this.reader = reader;
      this.encoding = encoding;
      this.literal = literal;
      this.mayReadChunks = mayReadChunks;
      this.isExternal = isExternal;
      int size = isExternal ? this.fBufferSize : 1024;
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      this.ch = ba.getCharBuffer(size);
      if (this.ch == null)
        this.ch = new char[size]; 
    }
    
    public void close() throws IOException {
      BufferAllocator ba = ThreadLocalBufferAllocator.getBufferAllocator();
      ba.returnCharBuffer(this.ch);
      this.ch = null;
      this.reader.close();
    }
    
    public boolean isEncodingExternallySpecified() {
      return this.externallySpecifiedEncoding;
    }
    
    public void setEncodingExternallySpecified(boolean value) {
      this.externallySpecifiedEncoding = value;
    }
    
    public boolean isDeclaredEncoding() {
      return this.declaredEncoding;
    }
    
    public void setDeclaredEncoding(boolean value) {
      this.declaredEncoding = value;
    }
    
    public final boolean isExternal() {
      return this.isExternal;
    }
    
    public final boolean isUnparsed() {
      return false;
    }
    
    public String toString() {
      StringBuffer str = new StringBuffer();
      str.append("name=\"" + this.name + '"');
      str.append(",ch=" + new String(this.ch));
      str.append(",position=" + this.position);
      str.append(",count=" + this.count);
      return str.toString();
    }
  }
  
  public static class Entity {}
  
  public static class Entity {}
}
