package sun.security.ssl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLProtocolException;

final class ServerNameExtension extends HelloExtension {
  static final int NAME_HEADER_LENGTH = 3;
  
  private Map<Integer, SNIServerName> sniMap;
  
  private int listLength;
  
  ServerNameExtension() throws IOException {
    super(ExtensionType.EXT_SERVER_NAME);
    this.listLength = 0;
    this.sniMap = Collections.emptyMap();
  }
  
  ServerNameExtension(List<SNIServerName> paramList) throws IOException {
    super(ExtensionType.EXT_SERVER_NAME);
    this.listLength = 0;
    this.sniMap = new LinkedHashMap<>();
    for (SNIServerName sNIServerName : paramList) {
      if (this.sniMap.put(Integer.valueOf(sNIServerName.getType()), sNIServerName) != null)
        throw new RuntimeException("Duplicated server name of type " + sNIServerName
            .getType()); 
      this.listLength += (sNIServerName.getEncoded()).length + 3;
    } 
    if (this.listLength == 0)
      throw new RuntimeException("The ServerNameList cannot be empty"); 
  }
  
  ServerNameExtension(HandshakeInStream paramHandshakeInStream, int paramInt) throws IOException {
    super(ExtensionType.EXT_SERVER_NAME);
    int i = paramInt;
    if (paramInt >= 2) {
      this.listLength = paramHandshakeInStream.getInt16();
      if (this.listLength == 0 || this.listLength + 2 != paramInt)
        throw new SSLProtocolException("Invalid " + this.type + " extension"); 
      i -= 2;
      this.sniMap = new LinkedHashMap<>();
      while (i > 0) {
        UnknownServerName unknownServerName;
        int j = paramHandshakeInStream.getInt8();
        byte[] arrayOfByte = paramHandshakeInStream.getBytes16();
        switch (j) {
          case 0:
            if (arrayOfByte.length == 0)
              throw new SSLProtocolException("Empty HostName in server name indication"); 
            try {
              SNIHostName sNIHostName = new SNIHostName(arrayOfByte);
            } catch (IllegalArgumentException illegalArgumentException) {
              SSLProtocolException sSLProtocolException = new SSLProtocolException("Illegal server name, type=host_name(" + j + "), name=" + new String(arrayOfByte, StandardCharsets.UTF_8) + ", value=" + Debug.toString(arrayOfByte));
              sSLProtocolException.initCause(illegalArgumentException);
              throw sSLProtocolException;
            } 
            break;
          default:
            try {
              unknownServerName = new UnknownServerName(j, arrayOfByte);
            } catch (IllegalArgumentException illegalArgumentException) {
              SSLProtocolException sSLProtocolException = new SSLProtocolException("Illegal server name, type=(" + j + "), value=" + Debug.toString(arrayOfByte));
              sSLProtocolException.initCause(illegalArgumentException);
              throw sSLProtocolException;
            } 
            break;
        } 
        if (this.sniMap.put(Integer.valueOf(unknownServerName.getType()), unknownServerName) != null)
          throw new SSLProtocolException("Duplicated server name of type " + unknownServerName
              
              .getType()); 
        i -= arrayOfByte.length + 3;
      } 
    } else if (paramInt == 0) {
      this.listLength = 0;
      this.sniMap = Collections.emptyMap();
    } 
    if (i != 0)
      throw new SSLProtocolException("Invalid server_name extension"); 
  }
  
  List<SNIServerName> getServerNames() {
    if (this.sniMap != null && !this.sniMap.isEmpty())
      return Collections.unmodifiableList(new ArrayList<>(this.sniMap
            .values())); 
    return Collections.emptyList();
  }
  
  boolean isMatched(Collection<SNIMatcher> paramCollection) {
    if (this.sniMap != null && !this.sniMap.isEmpty())
      for (SNIMatcher sNIMatcher : paramCollection) {
        SNIServerName sNIServerName = this.sniMap.get(Integer.valueOf(sNIMatcher.getType()));
        if (sNIServerName != null && !sNIMatcher.matches(sNIServerName))
          return false; 
      }  
    return true;
  }
  
  boolean isIdentical(List<SNIServerName> paramList) {
    if (paramList.size() == this.sniMap.size()) {
      for (SNIServerName sNIServerName1 : paramList) {
        SNIServerName sNIServerName2 = this.sniMap.get(Integer.valueOf(sNIServerName1.getType()));
        if (sNIServerName2 == null || !sNIServerName1.equals(sNIServerName2))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  int length() {
    return (this.listLength == 0) ? 4 : (6 + this.listLength);
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putInt16(this.type.id);
    if (this.listLength == 0) {
      paramHandshakeOutStream.putInt16(this.listLength);
    } else {
      paramHandshakeOutStream.putInt16(this.listLength + 2);
      paramHandshakeOutStream.putInt16(this.listLength);
      for (SNIServerName sNIServerName : this.sniMap.values()) {
        paramHandshakeOutStream.putInt8(sNIServerName.getType());
        paramHandshakeOutStream.putBytes16(sNIServerName.getEncoded());
      } 
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (SNIServerName sNIServerName : this.sniMap.values())
      stringBuffer.append("[" + sNIServerName + "]"); 
    return "Extension " + this.type + ", server_name: " + stringBuffer;
  }
  
  private static class ServerNameExtension {}
}
