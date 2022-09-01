package notthatuwu.xyz.mythrecode.api.utils.shader.list;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import notthatuwu.xyz.mythrecode.api.utils.shader.KoksFramebuffer;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderExtension;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderProgram;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderRenderType;
import notthatuwu.xyz.mythrecode.api.utils.shader.impl.StencilUtil;
import org.lwjgl.opengl.GL11;

@ShaderAnnoation(fragName = "Blur.glsl", renderType = ShaderRenderType.RENDER2D)
public class BlurShader extends ShaderProgram {
  private static Framebuffer framebuffer = new Framebuffer(1, 1, false);
  
  public void doRender() {
    doRender(22, 22, 1.0D);
  }
  
  public void doRender(int sigma, int radius, double texelSize) {
    framebuffer = KoksFramebuffer.doFrameBuffer(framebuffer);
    ShaderExtension.useShader(getShaderProgramID());
    setUniforms(1, 0, sigma, radius, texelSize);
    framebuffer.framebufferClear();
    framebuffer.bindFramebuffer(true);
    GL11.glBindTexture(3553, (mc.getFramebuffer()).framebufferTexture);
    KoksFramebuffer.renderTexture();
    framebuffer.unbindFramebuffer();
    ShaderExtension.useShader(getShaderProgramID());
    setUniforms(0, 1, sigma, radius, texelSize);
    mc.getFramebuffer().bindFramebuffer(true);
    GL11.glBindTexture(3553, framebuffer.framebufferTexture);
    KoksFramebuffer.renderTexture();
    ShaderExtension.deleteProgram();
    super.doRender();
  }
  
  public void startBlur() {
    StencilUtil.initStencilToWrite();
  }
  
  public void stopBlur() {
    stopBlur(7, 12, 1);
  }
  
  public void stopBlur(int sigma, int radius, int texelSize) {
    StencilUtil.readStencilBuffer(1);
    doRender(sigma, radius, texelSize);
    StencilUtil.uninitStencilBuffer();
    GlStateManager.enableBlend();
    mc.entityRenderer.setupOverlayRendering();
  }
  
  private void setUniforms(int xAxis, int yAxis, int sigma, int radius, double texelSize) {
    setShaderUniformI("currentTexture", new int[] { 0 });
    setShaderUniform("texelSize", new float[] { 1.0F / mc.displayWidth, 1.0F / mc.displayHeight });
    setShaderUniform("coords", new float[] { xAxis, yAxis });
    setShaderUniform("blurRadius", new float[] { radius });
    setShaderUniform("uRTPixelSizePixelSizeHalf", new float[] { 20.0F, 20.0F, 20.0F, 20.0F });
    setShaderUniform("blursigma", new float[] { sigma });
  }
}
