package javax.net.ssl;

import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

public final class SNIHostName extends SNIServerName {
  private final String hostname;
  
  public SNIHostName(String paramString) {
    super(0, (
        paramString = IDN.toASCII(
          Objects.requireNonNull(paramString, "Server name value of host_name cannot be null"), 2))
        
        .getBytes(StandardCharsets.US_ASCII));
    this.hostname = paramString;
    checkHostName();
  }
  
  public SNIHostName(byte[] paramArrayOfbyte) {
    super(0, paramArrayOfbyte);
    try {
      CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
      this.hostname = IDN.toASCII(charsetDecoder
          .decode(ByteBuffer.wrap(paramArrayOfbyte)).toString());
    } catch (RuntimeException|java.nio.charset.CharacterCodingException runtimeException) {
      throw new IllegalArgumentException("The encoded server name value is invalid", runtimeException);
    } 
    checkHostName();
  }
  
  public String getAsciiName() {
    return this.hostname;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SNIHostName)
      return this.hostname.equalsIgnoreCase(((SNIHostName)paramObject).hostname); 
    return false;
  }
  
  public int hashCode() {
    int i = 17;
    i = 31 * i + this.hostname.toUpperCase(Locale.ENGLISH).hashCode();
    return i;
  }
  
  public String toString() {
    return "type=host_name (0), value=" + this.hostname;
  }
  
  public static SNIMatcher createSNIMatcher(String paramString) {
    if (paramString == null)
      throw new NullPointerException("The regular expression cannot be null"); 
    return new SNIHostNameMatcher(paramString);
  }
  
  private void checkHostName() {
    if (this.hostname.isEmpty())
      throw new IllegalArgumentException("Server name value of host_name cannot be empty"); 
    if (this.hostname.endsWith("."))
      throw new IllegalArgumentException("Server name value of host_name cannot have the trailing dot"); 
  }
  
  private static final class SNIHostName {}
}
