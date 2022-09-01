package com.viaversion.viaversion.api.protocol.version;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.Provider;

public interface VersionProvider extends Provider {
  int getClosestServerProtocol(UserConnection paramUserConnection) throws Exception;
}
