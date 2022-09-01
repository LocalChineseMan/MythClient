package com.viaversion.viaversion.api;

import com.google.common.base.Preconditions;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.ViaPlatform;

public final class Via {
  private static ViaManager manager;
  
  public static ViaAPI getAPI() {
    return manager().getPlatform().getApi();
  }
  
  public static ViaManager getManager() {
    return manager();
  }
  
  public static ViaVersionConfig getConfig() {
    return manager().getPlatform().getConf();
  }
  
  public static ViaPlatform getPlatform() {
    return manager().getPlatform();
  }
  
  public static void init(ViaManager viaManager) {
    Preconditions.checkArgument((manager == null), "ViaManager is already set");
    manager = viaManager;
  }
  
  private static ViaManager manager() {
    Preconditions.checkArgument((manager != null), "ViaVersion has not loaded the platform yet");
    return manager;
  }
}
