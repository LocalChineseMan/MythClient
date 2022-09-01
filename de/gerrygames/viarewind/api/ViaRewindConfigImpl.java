package de.gerrygames.viarewind.api;

import com.viaversion.viaversion.util.Config;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ViaRewindConfigImpl extends Config implements ViaRewindConfig {
  public ViaRewindConfigImpl(File configFile) {
    super(configFile);
    reloadConfig();
  }
  
  public ViaRewindConfig.CooldownIndicator getCooldownIndicator() {
    return ViaRewindConfig.CooldownIndicator.valueOf(getString("cooldown-indicator", "TITLE").toUpperCase());
  }
  
  public boolean isReplaceAdventureMode() {
    return getBoolean("replace-adventure", false);
  }
  
  public boolean isReplaceParticles() {
    return getBoolean("replace-particles", false);
  }
  
  public URL getDefaultConfigURL() {
    return getClass().getClassLoader().getResource("assets/viarewind/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> map) {}
  
  public List<String> getUnsupportedOptions() {
    return Collections.emptyList();
  }
}
