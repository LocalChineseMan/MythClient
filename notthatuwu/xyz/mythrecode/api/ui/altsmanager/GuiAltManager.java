package notthatuwu.xyz.mythrecode.api.ui.altsmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.microsoft.model.response.MinecraftProfile;
import java.awt.Color;
import java.io.IOException;
import java.net.Proxy;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.files.AccountsFile;
import notthatuwu.xyz.mythrecode.api.utils.RandomUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GuiAltManager extends GuiScreen {
  private AltLoginThread loginThread;
  
  private int offset;
  
  public Alt selectedAlt;
  
  private String status;
  
  public static loginType type = loginType.MOJANG;
  
  public boolean close = false;
  
  public boolean closed;
  
  private GuiTextField username;
  
  private GuiTextField combined;
  
  private GuiTextField password;
  
  public TimeHelper timeHelper = new TimeHelper();
  
  public static Minecraft mc = Minecraft.getMinecraft();
  
  public GuiAltManager() {
    this.selectedAlt = null;
    this.status = "";
  }
  
  public void actionPerformed(GuiButton button) throws IOException {
    String username;
    switch (button.id) {
      case 0:
        if (this.loginThread == null) {
          mc.displayGuiScreen(null);
          break;
        } 
        if (!this.loginThread.getStatus().equals("Logging in...") && !this.loginThread.getStatus().equals("Do not hit back! Logging in...")) {
          mc.displayGuiScreen(null);
          break;
        } 
        this.loginThread.setStatus("Do not hit back! Logging in...");
        break;
      case 2:
        if (this.loginThread != null)
          this.loginThread = null; 
        AltManager.getAlts().remove(this.selectedAlt);
        this.status = "cRemoved.";
        this.selectedAlt = null;
        break;
      case 14:
        username = "MythUser" + Math.round(RandomUtil.randomInRange(211.0D, 457654.0D));
        mc.session = new Session(username, "", "", "mojang");
        this.status = "§aLogged in as " + username;
        break;
    } 
  }
  
  public void drawScreen(int par1, int par2, float par3) {
    drawDefaultBackground();
    ScaledResolution sr = new ScaledResolution(mc);
    if (Mouse.hasWheel()) {
      int wheel = Mouse.getDWheel();
      if (wheel < 0) {
        this.offset += 26;
        if (this.offset < 0)
          this.offset = 0; 
      } else if (wheel > 0) {
        this.offset -= 26;
        if (this.offset < 0)
          this.offset = 0; 
      } 
    } 
    mc.getTextureManager().bindTexture(new ResourceLocation("myth/image/" + GuiMainMenu.shaderName + ".png"));
    Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, sr.getScaledWidth(), sr.getScaledHeight(), sr.getScaledWidth(), sr.getScaledHeight(), sr.getScaledWidth(), sr.getScaledHeight());
    GL11.glBegin(7);
    GL11.glVertex2f(-1.0F, -1.0F);
    GL11.glVertex2f(-1.0F, 1.0F);
    GL11.glVertex2f(1.0F, 1.0F);
    GL11.glVertex2f(1.0F, -1.0F);
    GL11.glEnd();
    GL20.glUseProgram(0);
    GL11.glEnable(3553);
    GL11.glEnable(3008);
    this.username.drawTextBox();
    this.password.drawTextBox();
    this.combined.drawTextBox();
    FontLoaders.Sfui20.drawString("Current Alt: " + EnumChatFormatting.GRAY + mc.session.getUsername(), 10.0F, 10.0F, -1);
    FontLoaders.Sfui20.drawCenteredString((this.loginThread == null) ? this.status : this.loginThread.getStatus(), (this.width / 2), 20.0D, -1);
    GL11.glPushMatrix();
    prepareScissorBox(0.0F, 33.0F, this.width, (this.height - 50));
    GL11.glEnable(3089);
    int y = 38;
    ScaledResolution sr3 = new ScaledResolution(Minecraft.getMinecraft());
    boolean isAdolf = isHovered2(par1, par2, sr3.getScaledWidth() / 2.0D + 370.0D, sr3.getScaledHeight() / 2.0D - 30.0D - 200.0D + 100.0D, sr3.getScaledWidth() / 2.0D + 50.0D + 420.0D, sr3.getScaledHeight() / 2.0D - 200.0D + 100.0D);
    Gui.drawRect(sr3.getScaledWidth() / 2.0D + 370.0D, sr3.getScaledHeight() / 2.0D - 200.0D + 100.0D, sr3.getScaledWidth() / 2.0D + 50.0D + 420.0D, sr3.getScaledHeight() / 2.0D - 30.0D - 200.0D + 100.0D, isAdolf ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
    FontLoaders.Sfui22.drawCenteredString("Login", (sr3.getScaledWidth() / 2 + 370 + 50), (sr3.getScaledHeight() / 2 - 200 + 100 - 19), -1);
    if (isAdolf && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
      this.timeHelper.reset();
      if (this.combined.getText().isEmpty()) {
        this.loginThread = new AltLoginThread(this.username.getText(), this.password.getText(), true);
      } else if (!this.combined.getText().isEmpty() && this.combined.getText().contains(":")) {
        String u = this.combined.getText().split(":")[0];
        String p = this.combined.getText().split(":")[1];
        this.loginThread = new AltLoginThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""), true);
      } else {
        this.loginThread = new AltLoginThread(this.username.getText(), this.password.getText(), true);
      } 
      this.loginThread.start();
    } 
    ScaledResolution sr2 = new ScaledResolution(Minecraft.getMinecraft());
    boolean isAdolf1 = isHovered2(par1, par2, sr2.getScaledWidth() / 2.0D + 370.0D - 120.0D, sr2.getScaledHeight() / 2.0D - 30.0D - 200.0D + 100.0D + 40.0D, sr2
        .getScaledWidth() / 2.0D + 50.0D + 420.0D - 120.0D + 120.0D, sr2.getScaledHeight() / 2.0D - 200.0D + 100.0D + 40.0D);
    Gui.drawRect((sr2.getScaledWidth() / 2 + 370 - 120), (sr2.getScaledHeight() / 2 - 200 + 100 + 40), (sr2.getScaledWidth() / 2 + 50 + 420 - 120 + 120), (sr2.getScaledHeight() / 2 - 30 - 200 + 100 + 40), isAdolf1 ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
    FontLoaders.Sfui22.drawCenteredString("Random Cracked", (sr2.getScaledWidth() / 2 + 370 - 120 + 110), (sr2.getScaledHeight() / 2 - 200 + 100 + 21), -1);
    if (isAdolf1 && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
      String username = "MythUser" + Math.round(RandomUtil.randomInRange(211.0D, 457654.0D));
      mc.session = new Session(username, "", "", "mojang");
      this.status = "Logged in as " + username;
      this.timeHelper.reset();
    } 
    boolean isAdolf2 = isHovered2(par1, par2, (sr2.getScaledWidth() / 2 + 370 - 120), (sr2.getScaledHeight() / 2 - 30 - 200 + 100), (sr2.getScaledWidth() / 2 + 50 + 420 - 120), (sr2.getScaledHeight() / 2 - 200 + 100));
    Gui.drawRect((sr2.getScaledWidth() / 2 + 370 - 120), (sr2.getScaledHeight() / 2 - 200 + 100), (sr2.getScaledWidth() / 2 + 50 + 420 - 120), (sr2.getScaledHeight() / 2 - 30 - 200 + 100), isAdolf2 ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
    FontLoaders.Sfui22.drawCenteredString("Add Alt", (sr2.getScaledWidth() / 2 + 370 + 50 - 120), (sr2.getScaledHeight() / 2 - 200 + 100 - 19), -1);
    if (isAdolf2 && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
      AddAltThread login;
      this.timeHelper.reset();
      switch (type) {
        case MOJANG:
          if (this.combined.getText().isEmpty()) {
            login = new AddAltThread(this.username.getText(), this.password.getText());
          } else if (!this.combined.getText().isEmpty() && this.combined.getText().contains(":")) {
            String u = this.combined.getText().split(":")[0];
            String p = this.combined.getText().split(":")[1];
            login = new AddAltThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
          } else {
            login = new AddAltThread(this.username.getText(), this.password.getText());
          } 
          login.start();
          this.status = "Added Alt!";
          break;
        case MICROSOFT:
          if (this.combined.getText().isEmpty()) {
            login = new AddAltThread(this.username.getText(), this.password.getText());
          } else if (!this.combined.getText().isEmpty() && this.combined.getText().contains(":")) {
            String u = this.combined.getText().split(":")[0];
            String p = this.combined.getText().split(":")[1];
            login = new AddAltThread(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
          } else {
            login = new AddAltThread(this.username.getText(), this.password.getText());
          } 
          login.start();
          this.status = "Added Microsoft Alt!";
          break;
      } 
    } 
    boolean isAdolf3 = isHovered2(par1, par2, (sr2.getScaledWidth() / 2 + 370 - 120), (sr2.getScaledHeight() / 2 - 30 - 200 + 100 + 80), (sr2.getScaledWidth() / 2 + 50 + 420 - 120 + 120), (sr2.getScaledHeight() / 2 - 200 + 100 + 80));
    Gui.drawRect((sr2.getScaledWidth() / 2 + 370 - 120), (sr2.getScaledHeight() / 2 - 200 + 100 + 80), (sr2.getScaledWidth() / 2 + 50 + 420 - 120 + 120), (sr2.getScaledHeight() / 2 - 30 - 200 + 100 + 80), isAdolf3 ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
    FontLoaders.Sfui22.drawCenteredString("Switch to " + getType() + " Auth", (sr2.getScaledWidth() / 2 + 370 - 120 + 110), (sr2.getScaledHeight() / 2 - 200 + 100 + 61), -1);
    if (isAdolf3 && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
      this.timeHelper.reset();
      if (type == loginType.MOJANG) {
        type = loginType.MICROSOFT;
      } else {
        type = loginType.MOJANG;
      } 
    } 
    if (!this.username.isFocused() && this.username.getText().isEmpty())
      FontLoaders.Ali18.drawCenteredString("E-Mail", (sr2.getScaledWidth() / 2 + 370 - 75), (sr2.getScaledHeight() / 2 - 154), Color.GRAY.getRGB()); 
    if (!this.password.isFocused() && this.password.getText().isEmpty())
      FontLoaders.Ali18.drawString("Password", (sr2.getScaledWidth() / 2 + 370 + 30), (sr2.getScaledHeight() / 2 - 154), Color.GRAY.getRGB()); 
    if (!this.combined.isFocused() && this.combined.getText().isEmpty())
      FontLoaders.Ali18.drawString("E-Mail : Password", (sr2.getScaledWidth() / 2 + 370 - 120 - 75 + 140), (sr2.getScaledHeight() / 2 - 154 - 30), Color.GRAY.getRGB()); 
    for (Alt alt : AltManager.getAlts()) {
      int posY = 0;
      if (isMouseOverAlt(par1, par2, y - this.offset)) {
        posY = 3;
      } else {
        posY = 0;
      } 
      if (isAltInArea(y)) {
        String name, pass;
        if (alt.getMask().equals("")) {
          name = alt.getUsername();
        } else {
          name = alt.getMask();
        } 
        String firstthree = "";
        if (alt.getPassword().equals("")) {
          pass = "";
        } else {
          firstthree = alt.getPassword().substring(0, 3);
          pass = alt.getPassword().replaceAll(".", "*");
        } 
        RenderUtils.drawRoundedRect2(42.0D, (y - this.offset - 4), (this.width - 500 + posY), (y - this.offset + 20), 3.0D, (new Color(0, 0, 0, 120)).getRGB());
        FontLoaders.Sfui16.drawCenteredString(name, (this.width / 2 - 400 + name.length() - 10), (y - this.offset), -1);
        FontLoaders.Sfui16.drawCenteredString(firstthree + pass, (this.width / 2 - 400 - 8 + firstthree.length() + pass.length() - 10), (y - this.offset + 10), -1);
        if (alt.type == "MICROSOFT")
          RenderUtils.drawImage(442, y - this.offset, 16, 16, new ResourceLocation("myth/Icons/microsoft.png"), Color.WHITE); 
        y += 26;
      } 
    } 
    GL11.glDisable(3089);
    GL11.glPopMatrix();
    super.drawScreen(par1, par2, par3);
    if (Keyboard.isKeyDown(200)) {
      this.offset -= 26;
      if (this.offset < 0)
        this.offset = 0; 
    } else if (Keyboard.isKeyDown(208)) {
      this.offset += 26;
      if (this.offset < 0)
        this.offset = 0; 
    } 
  }
  
  public void initGui() {
    ScaledResolution sr = new ScaledResolution(mc);
    this.username = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 + 370 - 120, sr.getScaledHeight() / 2 - 160, 100, 20);
    this.password = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 + 370, sr.getScaledHeight() / 2 - 160, 100, 20);
    this.combined = new GuiTextField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 + 370 - 120, sr.getScaledHeight() / 2 - 160 - 30, 220, 20);
  }
  
  private boolean isAltInArea(int y) {
    return (y - this.offset <= this.height - 50);
  }
  
  private boolean isMouseOverAlt(int x, int y, int y1) {
    return (x >= 52 && y >= y1 - 4 && x <= this.width - 52 - 450 && y <= y1 + 20 && y >= 33 && x <= this.width && y <= this.height - 50);
  }
  
  public String getType() {
    return (type == loginType.MOJANG) ? "Microsoft" : "Mojang";
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    this.username.textboxKeyTyped(typedChar, keyCode);
    this.combined.textboxKeyTyped(typedChar, keyCode);
    this.password.textboxKeyTyped(typedChar, keyCode);
    if (typedChar == '\t' && (this.username.isFocused() || this.combined.isFocused() || this.password.isFocused())) {
      this.username.setFocused(!this.username.isFocused());
      this.password.setFocused(!this.password.isFocused());
      this.combined.setFocused(!this.combined.isFocused());
    } 
    if (typedChar == '\r')
      actionPerformed(this.buttonList.get(0)); 
    if (keyCode == 1)
      mc.displayGuiScreen((GuiScreen)new GuiMainMenu()); 
  }
  
  protected void mouseClicked(int par1, int par2, int par3) throws IOException {
    this.username.mouseClicked(par1, par2, par3);
    this.password.mouseClicked(par1, par2, par3);
    this.combined.mouseClicked(par1, par2, par3);
    if (this.offset < 0)
      this.offset = 0; 
    int y = 38 - this.offset;
    for (Alt alt : AltManager.getAlts()) {
      if (isMouseOverAlt(par1, par2, y)) {
        if (alt == this.selectedAlt) {
          actionPerformed(this.buttonList.get(1));
          return;
        } 
        if (alt.getType().equalsIgnoreCase("MICROSOFT")) {
          MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
          try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(this.username.getText(), this.password.getText());
            MinecraftProfile profile = result.getProfile();
            mc.session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
            if (result.getProfile() != null)
              this.loginThread = new AltLoginThread(this.username.getText(), this.password.getText(), true); 
          } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
          } 
        } else {
          if (this.combined.getText().isEmpty()) {
            this.loginThread = new AltLoginThread(alt.getUsername(), alt.getPassword(), true);
          } else {
            this.loginThread = new AltLoginThread(alt.getUsername(), alt.getPassword(), true);
          } 
          this.loginThread.start();
        } 
      } 
      y += 26;
    } 
    try {
      super.mouseClicked(par1, par2, par3);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public void prepareScissorBox(float x, float y, float x2, float y2) {
    int factor = (new ScaledResolution(mc)).getScaleFactor();
    GL11.glScissor((int)(x * factor), (int)(((new ScaledResolution(mc)).getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
  }
  
  public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
  }
  
  public boolean isHovered2(int mouseX, int mouseY, double x, double y, double x2, double y2) {
    return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
  }
  
  public class AddAltThread extends Thread {
    private final String password;
    
    private final String username;
    
    public AddAltThread(String username, String password) {
      this.username = username;
      this.password = password;
    }
    
    private final void checkAndAddAlt(String username, String password) {
      YggdrasilAuthenticationService service;
      YggdrasilUserAuthentication auth;
      MicrosoftAuthenticator authenticator;
      switch (GuiAltManager.type) {
        case MOJANG:
          service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
          auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
          auth.setUsername(username);
          auth.setPassword(password);
          try {
            auth.logIn();
            AltManager.getAlts().add(new Alt(username, password, GuiAltManager.type.name().toUpperCase(Locale.ROOT)));
            GuiAltManager.this.status = "Added Alt!";
            Client.INSTANCE.fileFactory.saveFile(AccountsFile.class);
          } catch (AuthenticationException e) {
            e.printStackTrace();
          } 
          break;
        case MICROSOFT:
          authenticator = new MicrosoftAuthenticator();
          try {
            MicrosoftAuthResult result = authenticator.loginWithCredentials(username, password);
            MinecraftProfile profile = result.getProfile();
            GuiAltManager.mc.session = new Session(profile.getName(), profile.getId(), result.getAccessToken(), "microsoft");
            if (result.getProfile() != null) {
              AltManager.getAlts().add(new Alt(username, password, GuiAltManager.type.name().toUpperCase(Locale.ROOT)));
              GuiAltManager.this.status = "Added Microsoft Alt!";
              Client.INSTANCE.fileFactory.saveFile(AccountsFile.class);
            } 
          } catch (MicrosoftAuthenticationException e) {
            e.printStackTrace();
          } 
          break;
      } 
    }
    
    public void run() {
      if (this.password.equals("")) {
        AltManager.getAlts().add(new Alt(this.username, "", GuiAltManager.type.name()));
        return;
      } 
      checkAndAddAlt(this.username, this.password);
    }
  }
}
