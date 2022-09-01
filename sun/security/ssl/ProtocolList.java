package sun.security.ssl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

final class ProtocolList {
  private final ArrayList<ProtocolVersion> protocols;
  
  private String[] protocolNames;
  
  final ProtocolVersion min;
  
  final ProtocolVersion max;
  
  final ProtocolVersion helloVersion;
  
  ProtocolList(String[] paramArrayOfString) {
    this(convert(paramArrayOfString));
  }
  
  ProtocolList(ArrayList<ProtocolVersion> paramArrayList) {
    this.protocols = paramArrayList;
    if (this.protocols.size() == 1 && this.protocols
      .contains(ProtocolVersion.SSL20Hello))
      throw new IllegalArgumentException("SSLv2Hello cannot be enabled unless at least one other supported version is also enabled."); 
    if (this.protocols.size() != 0) {
      Collections.sort(this.protocols);
      this.min = this.protocols.get(0);
      this.max = this.protocols.get(this.protocols.size() - 1);
      this.helloVersion = this.protocols.get(0);
    } else {
      this.min = ProtocolVersion.NONE;
      this.max = ProtocolVersion.NONE;
      this.helloVersion = ProtocolVersion.NONE;
    } 
  }
  
  private static ArrayList<ProtocolVersion> convert(String[] paramArrayOfString) {
    if (paramArrayOfString == null)
      throw new IllegalArgumentException("Protocols may not be null"); 
    ArrayList<ProtocolVersion> arrayList = new ArrayList(paramArrayOfString.length);
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      ProtocolVersion protocolVersion = ProtocolVersion.valueOf(paramArrayOfString[b]);
      if (!arrayList.contains(protocolVersion))
        arrayList.add(protocolVersion); 
    } 
    return arrayList;
  }
  
  boolean contains(ProtocolVersion paramProtocolVersion) {
    if (paramProtocolVersion == ProtocolVersion.SSL20Hello)
      return false; 
    return this.protocols.contains(paramProtocolVersion);
  }
  
  Collection<ProtocolVersion> collection() {
    return this.protocols;
  }
  
  ProtocolVersion selectProtocolVersion(ProtocolVersion paramProtocolVersion) {
    ProtocolVersion protocolVersion = null;
    for (ProtocolVersion protocolVersion1 : this.protocols) {
      if (protocolVersion1.v > paramProtocolVersion.v)
        break; 
      protocolVersion = protocolVersion1;
    } 
    return protocolVersion;
  }
  
  synchronized String[] toStringArray() {
    if (this.protocolNames == null) {
      this.protocolNames = new String[this.protocols.size()];
      byte b = 0;
      for (ProtocolVersion protocolVersion : this.protocols)
        this.protocolNames[b++] = protocolVersion.name; 
    } 
    return (String[])this.protocolNames.clone();
  }
  
  public String toString() {
    return this.protocols.toString();
  }
}
