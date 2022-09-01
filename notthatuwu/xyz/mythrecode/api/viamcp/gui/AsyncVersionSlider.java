package notthatuwu.xyz.mythrecode.api.viamcp.gui;

import java.util.Arrays;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import notthatuwu.xyz.mythrecode.api.viamcp.ViaMCP;
import notthatuwu.xyz.mythrecode.api.viamcp.protocols.ProtocolCollection;

public class AsyncVersionSlider extends GuiButton {
  private float dragValue = ((ProtocolCollection.values()).length - Arrays.<ProtocolCollection>asList(ProtocolCollection.values()).indexOf(ProtocolCollection.getProtocolCollectionById(47))) / (ProtocolCollection.values()).length;
  
  private final ProtocolCollection[] values;
  
  private float sliderValue;
  
  public boolean dragging;
  
  public AsyncVersionSlider(int buttonId, int x, int y, int widthIn, int heightIn) {
    super(buttonId, x, y, Math.max(widthIn, 110), heightIn, "");
    this.values = ProtocolCollection.values();
    Collections.reverse(Arrays.asList((Object[])this.values));
    this.sliderValue = this.dragValue;
    this.displayString = this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getName();
  }
  
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    super.drawButton(mc, mouseX, mouseY);
  }
  
  protected int getHoverState(boolean mouseOver) {
    return 0;
  }
  
  protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      if (this.dragging) {
        this.sliderValue = (mouseX - this.xPosition + 4) / (this.width - 8);
        this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
        this.dragValue = this.sliderValue;
        this.displayString = this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getName();
        ViaMCP.getInstance().setVersion(this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getVersion());
      } 
      mc.getTextureManager().bindTexture(buttonTextures);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
      drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
    } 
  }
  
  public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
    if (super.mousePressed(mc, mouseX, mouseY)) {
      this.sliderValue = (mouseX - this.xPosition + 4) / (this.width - 8);
      this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
      this.dragValue = this.sliderValue;
      this.displayString = this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getName();
      ViaMCP.getInstance().setVersion(this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getVersion());
      this.dragging = true;
      return true;
    } 
    return false;
  }
  
  public void mouseReleased(int mouseX, int mouseY) {
    this.dragging = false;
  }
  
  public void setVersion(int protocol) {
    this.dragValue = ((ProtocolCollection.values()).length - Arrays.<ProtocolCollection>asList(ProtocolCollection.values()).indexOf(ProtocolCollection.getProtocolCollectionById(protocol))) / (ProtocolCollection.values()).length;
    this.sliderValue = this.dragValue;
    this.displayString = this.values[(int)(this.sliderValue * (this.values.length - 1))].getVersion().getName();
  }
}
