package notthatuwu.xyz.mythrecode.api.viamcp.loader;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;
import notthatuwu.xyz.mythrecode.api.viamcp.ViaMCP;

public class MCPViaLoader implements ViaPlatformLoader {
  public void load() {
    Via.getManager().getProviders().use(MovementTransmitterProvider.class, (Provider)new BungeeMovementTransmitter());
    Via.getManager().getProviders().use(VersionProvider.class, (Provider)new BaseVersionProvider() {
          public int getClosestServerProtocol(UserConnection connection) throws Exception {
            if (connection.isClientSide())
              return ViaMCP.getInstance().getVersion(); 
            return super.getClosestServerProtocol(connection);
          }
        });
  }
  
  public void unload() {}
}
