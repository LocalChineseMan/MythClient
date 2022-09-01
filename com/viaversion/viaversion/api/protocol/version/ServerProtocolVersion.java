package com.viaversion.viaversion.api.protocol.version;

import com.viaversion.viaversion.libs.fastutil.ints.IntSortedSet;

public interface ServerProtocolVersion {
  int lowestSupportedVersion();
  
  int highestSupportedVersion();
  
  IntSortedSet supportedVersions();
  
  default boolean isKnown() {
    return (lowestSupportedVersion() != -1 && highestSupportedVersion() != -1);
  }
}
