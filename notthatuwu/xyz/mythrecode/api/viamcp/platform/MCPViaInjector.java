package notthatuwu.xyz.mythrecode.api.viamcp.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class MCPViaInjector implements ViaInjector {
  public void inject() {}
  
  public void uninject() {}
  
  public int getServerProtocolVersion() {
    return 47;
  }
  
  public String getEncoderName() {
    return "via-encoder";
  }
  
  public String getDecoderName() {
    return "via-decoder";
  }
  
  public JsonObject getDump() {
    JsonObject obj = new JsonObject();
    return obj;
  }
}
