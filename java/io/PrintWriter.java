package java.io;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.security.AccessController;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import sun.security.action.GetPropertyAction;

public class PrintWriter extends Writer {
  protected Writer out;
  
  private final boolean autoFlush;
  
  private boolean trouble = false;
  
  private Formatter formatter;
  
  private PrintStream psOut = null;
  
  private final String lineSeparator;
  
  private static Charset toCharset(String paramString) throws UnsupportedEncodingException {
    Objects.requireNonNull(paramString, "charsetName");
    try {
      return Charset.forName(paramString);
    } catch (IllegalCharsetNameException|java.nio.charset.UnsupportedCharsetException illegalCharsetNameException) {
      throw new UnsupportedEncodingException(paramString);
    } 
  }
  
  public PrintWriter(Writer paramWriter) {
    this(paramWriter, false);
  }
  
  public PrintWriter(Writer paramWriter, boolean paramBoolean) {
    super(paramWriter);
    this.out = paramWriter;
    this.autoFlush = paramBoolean;
    this.lineSeparator = AccessController.<String>doPrivileged(new GetPropertyAction("line.separator"));
  }
  
  public PrintWriter(OutputStream paramOutputStream) {
    this(paramOutputStream, false);
  }
  
  public PrintWriter(OutputStream paramOutputStream, boolean paramBoolean) {
    this(new BufferedWriter(new OutputStreamWriter(paramOutputStream)), paramBoolean);
    if (paramOutputStream instanceof PrintStream)
      this.psOut = (PrintStream)paramOutputStream; 
  }
  
  public PrintWriter(String paramString) throws FileNotFoundException {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramString))), false);
  }
  
  private PrintWriter(Charset paramCharset, File paramFile) throws FileNotFoundException {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile), paramCharset)), false);
  }
  
  public PrintWriter(String paramString1, String paramString2) throws FileNotFoundException, UnsupportedEncodingException {
    this(toCharset(paramString2), new File(paramString1));
  }
  
  public PrintWriter(File paramFile) throws FileNotFoundException {
    this(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(paramFile))), false);
  }
  
  public PrintWriter(File paramFile, String paramString) throws FileNotFoundException, UnsupportedEncodingException {
    this(toCharset(paramString), paramFile);
  }
  
  private void ensureOpen() throws IOException {
    if (this.out == null)
      throw new IOException("Stream closed"); 
  }
  
  public void flush() {
    try {
      synchronized (this.lock) {
        ensureOpen();
        this.out.flush();
      } 
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void close() {
    try {
      synchronized (this.lock) {
        if (this.out == null)
          return; 
        this.out.close();
        this.out = null;
      } 
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public boolean checkError() {
    if (this.out != null)
      flush(); 
    if (this.out instanceof PrintWriter) {
      PrintWriter printWriter = (PrintWriter)this.out;
      return printWriter.checkError();
    } 
    if (this.psOut != null)
      return this.psOut.checkError(); 
    return this.trouble;
  }
  
  protected void setError() {
    this.trouble = true;
  }
  
  protected void clearError() {
    this.trouble = false;
  }
  
  public void write(int paramInt) {
    try {
      synchronized (this.lock) {
        ensureOpen();
        this.out.write(paramInt);
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void write(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    try {
      synchronized (this.lock) {
        ensureOpen();
        this.out.write(paramArrayOfchar, paramInt1, paramInt2);
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void write(char[] paramArrayOfchar) {
    write(paramArrayOfchar, 0, paramArrayOfchar.length);
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) {
    try {
      synchronized (this.lock) {
        ensureOpen();
        this.out.write(paramString, paramInt1, paramInt2);
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void write(String paramString) {
    write(paramString, 0, paramString.length());
  }
  
  private void newLine() {
    try {
      synchronized (this.lock) {
        ensureOpen();
        this.out.write(this.lineSeparator);
        if (this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
  }
  
  public void print(boolean paramBoolean) {
    write(paramBoolean ? "true" : "false");
  }
  
  public void print(char paramChar) {
    write(paramChar);
  }
  
  public void print(int paramInt) {
    write(String.valueOf(paramInt));
  }
  
  public void print(long paramLong) {
    write(String.valueOf(paramLong));
  }
  
  public void print(float paramFloat) {
    write(String.valueOf(paramFloat));
  }
  
  public void print(double paramDouble) {
    write(String.valueOf(paramDouble));
  }
  
  public void print(char[] paramArrayOfchar) {
    write(paramArrayOfchar);
  }
  
  public void print(String paramString) {
    if (paramString == null)
      paramString = "null"; 
    write(paramString);
  }
  
  public void print(Object paramObject) {
    write(String.valueOf(paramObject));
  }
  
  public void println() {
    newLine();
  }
  
  public void println(boolean paramBoolean) {
    synchronized (this.lock) {
      print(paramBoolean);
      println();
    } 
  }
  
  public void println(char paramChar) {
    synchronized (this.lock) {
      print(paramChar);
      println();
    } 
  }
  
  public void println(int paramInt) {
    synchronized (this.lock) {
      print(paramInt);
      println();
    } 
  }
  
  public void println(long paramLong) {
    synchronized (this.lock) {
      print(paramLong);
      println();
    } 
  }
  
  public void println(float paramFloat) {
    synchronized (this.lock) {
      print(paramFloat);
      println();
    } 
  }
  
  public void println(double paramDouble) {
    synchronized (this.lock) {
      print(paramDouble);
      println();
    } 
  }
  
  public void println(char[] paramArrayOfchar) {
    synchronized (this.lock) {
      print(paramArrayOfchar);
      println();
    } 
  }
  
  public void println(String paramString) {
    synchronized (this.lock) {
      print(paramString);
      println();
    } 
  }
  
  public void println(Object paramObject) {
    String str = String.valueOf(paramObject);
    synchronized (this.lock) {
      print(str);
      println();
    } 
  }
  
  public PrintWriter printf(String paramString, Object... paramVarArgs) {
    return format(paramString, paramVarArgs);
  }
  
  public PrintWriter printf(Locale paramLocale, String paramString, Object... paramVarArgs) {
    return format(paramLocale, paramString, paramVarArgs);
  }
  
  public PrintWriter format(String paramString, Object... paramVarArgs) {
    try {
      synchronized (this.lock) {
        ensureOpen();
        if (this.formatter == null || this.formatter
          .locale() != Locale.getDefault())
          this.formatter = new Formatter(this); 
        this.formatter.format(Locale.getDefault(), paramString, paramVarArgs);
        if (this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
    return this;
  }
  
  public PrintWriter format(Locale paramLocale, String paramString, Object... paramVarArgs) {
    try {
      synchronized (this.lock) {
        ensureOpen();
        if (this.formatter == null || this.formatter.locale() != paramLocale)
          this.formatter = new Formatter(this, paramLocale); 
        this.formatter.format(paramLocale, paramString, paramVarArgs);
        if (this.autoFlush)
          this.out.flush(); 
      } 
    } catch (InterruptedIOException interruptedIOException) {
      Thread.currentThread().interrupt();
    } catch (IOException iOException) {
      this.trouble = true;
    } 
    return this;
  }
  
  public PrintWriter append(CharSequence paramCharSequence) {
    if (paramCharSequence == null) {
      write("null");
    } else {
      write(paramCharSequence.toString());
    } 
    return this;
  }
  
  public PrintWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    CharSequence charSequence = (paramCharSequence == null) ? "null" : paramCharSequence;
    write(charSequence.subSequence(paramInt1, paramInt2).toString());
    return this;
  }
  
  public PrintWriter append(char paramChar) {
    write(paramChar);
    return this;
  }
}
