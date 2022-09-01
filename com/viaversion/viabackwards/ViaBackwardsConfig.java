package com.viaversion.viabackwards;

import com.viaversion.viabackwards.api.ViaBackwardsConfig;
import com.viaversion.viaversion.util.Config;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaBackwardsConfig extends Config implements ViaBackwardsConfig {
  private boolean addCustomEnchantsToLore;
  
  private boolean addTeamColorToPrefix;
  
  private boolean fix1_13FacePlayer;
  
  private boolean alwaysShowOriginalMobName;
  
  private boolean handlePingsAsInvAcknowledgements;
  
  public ViaBackwardsConfig(File configFile) {
    super(configFile);
  }
  
  public void reloadConfig() {
    super.reloadConfig();
    loadFields();
  }
  
  private void loadFields() {
    this.addCustomEnchantsToLore = getBoolean("add-custom-enchants-into-lore", true);
    this.addTeamColorToPrefix = getBoolean("add-teamcolor-to-prefix", true);
    this.fix1_13FacePlayer = getBoolean("fix-1_13-face-player", false);
    this.alwaysShowOriginalMobName = getBoolean("always-show-original-mob-name", true);
    this.handlePingsAsInvAcknowledgements = getBoolean("handle-pings-as-inv-acknowledgements", false);
  }
  
  public boolean addCustomEnchantsToLore() {
    return this.addCustomEnchantsToLore;
  }
  
  public boolean addTeamColorTo1_13Prefix() {
    return this.addTeamColorToPrefix;
  }
  
  public boolean isFix1_13FacePlayer() {
    return this.fix1_13FacePlayer;
  }
  
  public boolean alwaysShowOriginalMobName() {
    return this.alwaysShowOriginalMobName;
  }
  
  public boolean handlePingsAsInvAcknowledgements() {
    return (this.handlePingsAsInvAcknowledgements || Boolean.getBoolean("com.viaversion.handlePingsAsInvAcknowledgements"));
  }
  
  public URL getDefaultConfigURL() {
    return getClass().getClassLoader().getResource("assets/viabackwards/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> map) {}
  
  public List<String> getUnsupportedOptions() {
    return Collections.emptyList();
  }
}
