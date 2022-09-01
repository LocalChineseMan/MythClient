package java.io;

public class StringWriter extends Writer {
  private StringBuffer buf;
  
  public StringWriter() {
    this.buf = new StringBuffer();
    this.lock = this.buf;
  }
  
  public StringWriter(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative buffer size"); 
    this.buf = new StringBuffer(paramInt);
    this.lock = this.buf;
  }
  
  public void write(int paramInt) {
    this.buf.append((char)paramInt);
  }
  
  public void write(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > paramArrayOfchar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfchar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    this.buf.append(paramArrayOfchar, paramInt1, paramInt2);
  }
  
  public void write(String paramString) {
    this.buf.append(paramString);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) {
    this.buf.append(paramString.substring(paramInt1, paramInt1 + paramInt2));
  }
  
  public StringWriter append(CharSequence paramCharSequence) {
    if (paramCharSequence == null) {
      write("null");
    } else {
      write(paramCharSequence.toString());
    } 
    return this;
  }
  
  public StringWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    CharSequence charSequence = (paramCharSequence == null) ? "null" : paramCharSequence;
    write(charSequence.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public StringWriter append(char paramChar) {
    write(paramChar);
    return this;
  }
  
  public String toString() {
    return this.buf.toString();
  }
  
  public StringBuffer getBuffer() {
    return this.buf;
  }
  
  public void flush() {}
  
  public void close() throws IOException {}
}
