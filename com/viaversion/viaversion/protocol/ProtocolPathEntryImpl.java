package com.viaversion.viaversion.protocol;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;

public class ProtocolPathEntryImpl implements ProtocolPathEntry {
  private final int outputProtocolVersion;
  
  private final Protocol protocol;
  
  public ProtocolPathEntryImpl(int outputProtocolVersion, Protocol protocol) {
    this.outputProtocolVersion = outputProtocolVersion;
    this.protocol = protocol;
  }
  
  public int getOutputProtocolVersion() {
    return this.outputProtocolVersion;
  }
  
  public Protocol getProtocol() {
    return this.protocol;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (o == null || getClass() != o.getClass())
      return false; 
    ProtocolPathEntryImpl that = (ProtocolPathEntryImpl)o;
    if (this.outputProtocolVersion != that.outputProtocolVersion)
      return false; 
    return this.protocol.equals(that.protocol);
  }
  
  public int hashCode() {
    int result = this.outputProtocolVersion;
    result = 31 * result + this.protocol.hashCode();
    return result;
  }
}
