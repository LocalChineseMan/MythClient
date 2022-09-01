package notthatuwu.xyz.mythrecode.api.viamcp.platform;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MCPViaConfig extends AbstractViaConfig {
  private static final List<String> UNSUPPORTED = Arrays.asList(new String[] { 
        "anti-xray-patch", "bungee-ping-interval", "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking", "velocity-ping-interval", "velocity-ping-save", "velocity-servers", "blockconnection-method", 
        "change-1_9-hitbox", "change-1_14-hitbox" });
  
  public MCPViaConfig(File configFile) {
    super(configFile);
    reloadConfig();
  }
  
  public URL getDefaultConfigURL() {
    return getClass().getClassLoader().getResource("assets/viaversion/config.yml");
  }
  
  protected void handleConfig(Map<String, Object> config) {}
  
  public List<String> getUnsupportedOptions() {
    return UNSUPPORTED;
  }
  
  public boolean isAntiXRay() {
    return false;
  }
  
  public boolean isNMSPlayerTicking() {
    return false;
  }
  
  public boolean is1_12QuickMoveActionFix() {
    return false;
  }
  
  public String getBlockConnectionMethod() {
    return "packet";
  }
  
  public boolean is1_9HitboxFix() {
    return false;
  }
  
  public boolean is1_14HitboxFix() {
    return false;
  }
}
