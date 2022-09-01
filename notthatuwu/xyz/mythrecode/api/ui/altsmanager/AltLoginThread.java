package notthatuwu.xyz.mythrecode.api.ui.altsmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import java.net.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import notthatuwu.xyz.mythrecode.Client;

public final class AltLoginThread extends Thread {
  private final String password;
  
  private String status;
  
  private final String username;
  
  private Minecraft mc = Minecraft.getMinecraft();
  
  boolean Mojang;
  
  public AltLoginThread(String username, String password, boolean mojang) {
    super("Alt Login Thread");
    this.username = username;
    this.password = password;
    this.Mojang = mojang;
    this.status = EnumChatFormatting.GRAY + "Waiting...";
  }
  
  private Session createSession(String username, String password) {
    if (this.Mojang) {
      Client.INSTANCE.serviceSwitcher.switchToService(AlteningServiceType.MOJANG);
    } else {
      Client.INSTANCE.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);
    } 
    YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
    YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
    auth.setUsername(username);
    auth.setPassword(password);
    try {
      auth.logIn();
      return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
    } catch (AuthenticationException localAuthenticationException) {
      localAuthenticationException.printStackTrace();
      return null;
    } 
  }
  
  public String getStatus() {
    return this.status;
  }
  
  public void run() {
    if (this.password.equals("")) {
      this.mc.session = new Session(this.username, "", "", "mojang");
      this.status = EnumChatFormatting.GREEN + "Logged in as " + this.username + " (Cracked)";
      return;
    } 
    this.status = "...";
    Session auth = createSession(this.username, this.password);
    if (auth == null) {
      this.status = EnumChatFormatting.RED + "Failed to login!";
    } else {
      this.status = EnumChatFormatting.GREEN + "Logged in as " + GuiAltManager.mc.session.getUsername();
      this.mc.session = auth;
    } 
  }
  
  public void setStatus(String status) {
    this.status = status;
  }
}
