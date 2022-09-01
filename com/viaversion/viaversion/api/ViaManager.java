package com.viaversion.viaversion.api;

import com.viaversion.viaversion.api.command.ViaVersionCommand;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.ProtocolManager;
import java.util.Set;

public interface ViaManager {
  ProtocolManager getProtocolManager();
  
  ViaPlatform<?> getPlatform();
  
  ConnectionManager getConnectionManager();
  
  ViaProviders getProviders();
  
  ViaInjector getInjector();
  
  ViaVersionCommand getCommandHandler();
  
  ViaPlatformLoader getLoader();
  
  boolean isDebug();
  
  void setDebug(boolean paramBoolean);
  
  Set<String> getSubPlatforms();
  
  void addEnableListener(Runnable paramRunnable);
}
