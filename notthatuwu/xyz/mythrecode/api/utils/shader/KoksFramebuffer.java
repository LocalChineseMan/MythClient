package notthatuwu.xyz.mythrecode.api.utils.shader;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import notthatuwu.xyz.mythrecode.api.interfaces.MCHook;
import org.lwjgl.opengl.GL11;

public final class KoksFramebuffer implements MCHook {
  private KoksFramebuffer() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  public static void renderTexture() {
    ScaledResolution resolution = new ScaledResolution(mc);
    drawQuads(0.0F, 0.0F, resolution.getScaledWidth(), resolution.getScaledHeight());
  }
  
  public static void drawQuads() {
    if (mc.gameSettings.ofFastRender)
      return; 
    ScaledResolution sr = new ScaledResolution(mc);
    float width = (float)sr.getScaledWidth_double();
    float height = (float)sr.getScaledHeight_double();
    GL11.glBegin(7);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex2f(0.0F, 0.0F);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex2f(0.0F, height);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex2f(width, height);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex2f(width, 0.0F);
    GL11.glEnd();
  }
  
  public static void bindTexture(int texture) {
    GL11.glBindTexture(3553, texture);
  }
  
  public static void drawQuads(float x, float y, float width, float height) {
    if (mc.gameSettings.ofFastRender)
      return; 
    GL11.glBegin(7);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex2f(x, y);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex2f(x, y + height);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex2f(x + width, y + height);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex2f(x + width, y);
    GL11.glEnd();
  }
  
  public static Framebuffer doFrameBuffer(Framebuffer framebuffer) {
    if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
      if (framebuffer != null)
        framebuffer.deleteFramebuffer(); 
      return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
    } 
    return framebuffer;
  }
  
  public static void renderFRFscreen(int framebuffer) {
    if (mc.gameSettings.ofFastRender)
      return; 
    ScaledResolution resolution = new ScaledResolution(mc);
    GL11.glBindTexture(3553, framebuffer);
    GL11.glBegin(7);
    GL11.glTexCoord2d(0.0D, 1.0D);
    GL11.glVertex2d(0.0D, 0.0D);
    GL11.glTexCoord2d(0.0D, 0.0D);
    GL11.glVertex2d(0.0D, resolution.getScaledHeight());
    GL11.glTexCoord2d(1.0D, 0.0D);
    GL11.glVertex2d(resolution.getScaledWidth(), resolution.getScaledHeight());
    GL11.glTexCoord2d(1.0D, 1.0D);
    GL11.glVertex2d(resolution.getScaledWidth(), 0.0D);
    GL11.glEnd();
    ShaderExtension.deleteProgram();
  }
  
  public static void renderFRFscreen(Framebuffer framebuffer) {
    if (mc.gameSettings.ofFastRender)
      return; 
    ScaledResolution resolution = new ScaledResolution(mc);
    GL11.glBindTexture(3553, framebuffer.framebufferTexture);
    GL11.glBegin(7);
    GL11.glTexCoord2d(0.0D, 1.0D);
    GL11.glVertex2d(0.0D, 0.0D);
    GL11.glTexCoord2d(0.0D, 0.0D);
    GL11.glVertex2d(0.0D, resolution.getScaledHeight());
    GL11.glTexCoord2d(1.0D, 0.0D);
    GL11.glVertex2d(resolution.getScaledWidth(), resolution.getScaledHeight());
    GL11.glTexCoord2d(1.0D, 1.0D);
    GL11.glVertex2d(resolution.getScaledWidth(), 0.0D);
    GL11.glEnd();
    ShaderExtension.deleteProgram();
  }
}
