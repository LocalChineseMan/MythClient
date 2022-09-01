package com.viaversion.viaversion.protocols.base;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;

public class BaseVersionProvider implements VersionProvider {
  public int getClosestServerProtocol(UserConnection connection) throws Exception {
    return Via.getAPI().getServerVersion().lowestSupportedVersion();
  }
}
