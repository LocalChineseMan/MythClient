package notthatuwu.xyz.mythrecode.api.utils.render;

import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class BlurUtil {
  private static final Minecraft MC = Minecraft.getMinecraft();
  
  private final ResourceLocation resourceLocation = new ResourceLocation("myth/blur.json");
  
  private ShaderGroup shaderGroup;
  
  private Framebuffer framebuffer;
  
  private int lastFactor;
  
  private int lastWidth;
  
  private int lastHeight;
  
  public void init() {
    try {
      this.shaderGroup = new ShaderGroup(MC.getTextureManager(), MC.getResourceManager(), MC.getFramebuffer(), this.resourceLocation);
      this.shaderGroup.createBindFramebuffers(MC.displayWidth, MC.displayHeight);
      this.framebuffer = this.shaderGroup.mainFramebuffer;
    } catch (JsonSyntaxException|java.io.IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void setValues(int strength) {
    ((Shader)this.shaderGroup.getListShaders().get(0)).getShaderManager().getShaderUniform("Radius").set(strength);
    ((Shader)this.shaderGroup.getListShaders().get(1)).getShaderManager().getShaderUniform("Radius").set(strength);
    ((Shader)this.shaderGroup.getListShaders().get(2)).getShaderManager().getShaderUniform("Radius").set(strength);
    ((Shader)this.shaderGroup.getListShaders().get(3)).getShaderManager().getShaderUniform("Radius").set(strength);
  }
  
  public void blur(int blurStrength) {
    ScaledResolution scaledResolution = new ScaledResolution(MC);
    int scaleFactor = scaledResolution.getScaleFactor();
    int width = scaledResolution.getScaledWidth();
    int height = scaledResolution.getScaledHeight();
    if (sizeHasChanged(scaleFactor, width, height) || this.framebuffer == null || this.shaderGroup == null)
      init(); 
    this.lastFactor = scaleFactor;
    this.lastWidth = width;
    this.lastHeight = height;
    setValues(blurStrength);
    this.framebuffer.bindFramebuffer(true);
    this.shaderGroup.loadShaderGroup(MC.timer.renderPartialTicks);
    MC.getFramebuffer().bindFramebuffer(true);
    GlStateManager.enableAlpha();
  }
  
  public void blur(double x, double y, double areaWidth, double areaHeight, int blurStrength) {
    ScaledResolution scaledResolution = new ScaledResolution(MC);
    int scaleFactor = scaledResolution.getScaleFactor();
    int width = scaledResolution.getScaledWidth();
    int height = scaledResolution.getScaledHeight();
    if (sizeHasChanged(scaleFactor, width, height) || this.framebuffer == null || this.shaderGroup == null)
      init(); 
    this.lastFactor = scaleFactor;
    this.lastWidth = width;
    this.lastHeight = height;
    GL11.glEnable(3089);
    RenderUtils.scissor((int)x, (int)(y + 1.0D), (int)areaWidth, (int)(areaHeight - 1.0D));
    this.framebuffer.bindFramebuffer(true);
    this.shaderGroup.loadShaderGroup(MC.timer.renderPartialTicks);
    setValues(blurStrength);
    MC.getFramebuffer().bindFramebuffer(false);
    GL11.glDisable(3089);
  }
  
  private boolean sizeHasChanged(int scaleFactor, int width, int height) {
    return (this.lastFactor != scaleFactor || this.lastWidth != width || this.lastHeight != height);
  }
}
