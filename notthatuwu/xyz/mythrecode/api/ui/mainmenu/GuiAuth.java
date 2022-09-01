package notthatuwu.xyz.mythrecode.api.ui.mainmenu;

import java.io.IOException;
import java.security.MessageDigest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;

public class GuiAuth extends GuiScreen {
  private int fade;
  
  private int fade2;
  
  private int statusMove;
  
  private String status;
  
  private boolean authPass;
  
  private boolean working;
  
  private EmptyInputBox uid;
  
  public TimeHelper timeHelper = new TimeHelper();
  
  public void initGui() {
    this.fade = 0;
    this.fade2 = 0;
    this.statusMove = 0;
    this.authPass = true;
    this.status = EnumChatFormatting.YELLOW + "Waiting for authentication...";
    this.uid = new EmptyInputBox(1, this.mc.fontRendererObj, 20, 150, 50, 20);
    this.uid.setMaxStringLength(7);
    super.initGui();
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
    try {
      (new Thread(() -> {
            this.working = true;
            this.authPass = true;
          })).start();
    } catch (Exception e) {
      this.status = EnumChatFormatting.RED + "Error while logging in, Logs: " + e.getMessage() + " " + e.getCause();
    } 
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    this.uid.mouseClicked(mouseX, mouseY, mouseButton);
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.uid.isFocused())
      this.uid.textboxKeyTyped(typedChar, keyCode); 
  }
  
  public void updateScreen() {
    Client.INSTANCE.startClient();
    super.updateScreen();
  }
  
  public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
  }
  
  private static String getHwidus() {
    try {
      String toEncrypt = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(toEncrypt.getBytes());
      StringBuffer hexString = new StringBuffer();
      byte[] byteData;
      for (byte aByteData : byteData = md.digest()) {
        String hex = Integer.toHexString(0xFF & aByteData);
        if (hex.length() == 1)
          hexString.append('0'); 
        hexString.append(hex);
      } 
      return hexString.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "Error";
    } 
  }
}
