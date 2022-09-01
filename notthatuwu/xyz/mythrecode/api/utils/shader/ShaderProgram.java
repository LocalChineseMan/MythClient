package notthatuwu.xyz.mythrecode.api.utils.shader;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import notthatuwu.xyz.mythrecode.api.interfaces.MCHook;
import org.lwjgl.opengl.GL20;

public class ShaderProgram implements MCHook {
  private static final Logger LOGGER = Logger.getLogger(ShaderProgram.class.getName());
  
  private final int shaderProgramID;
  
  public int getShaderProgramID() {
    return this.shaderProgramID;
  }
  
  private final String fragmentName = ((ShaderAnnoation)getClass().<ShaderAnnoation>getAnnotation(ShaderAnnoation.class)).fragName();
  
  public String getFragmentName() {
    return this.fragmentName;
  }
  
  private final ShaderRenderType shaderRenderType = ((ShaderAnnoation)getClass().<ShaderAnnoation>getAnnotation(ShaderAnnoation.class)).renderType();
  
  public ShaderRenderType getShaderRenderType() {
    return this.shaderRenderType;
  }
  
  public void doRender() {
    switch (this.shaderRenderType) {
      case RENDER2D:
        GlStateManager.enableBlend();
        mc.entityRenderer.setupOverlayRendering();
        break;
      case RENDER3D:
        GlStateManager.enableBlend();
        mc.entityRenderer.setupCameraTransform(ShaderPartialTicks.partialTicks, 0);
        break;
    } 
  }
  
  public ShaderProgram(String fragName) {
    int shaderProgramID = GL20.glCreateProgram();
    String vertexSource = ShaderExtension.readShader("vertex/vertex.glsl");
    int vertexShaderID = GL20.glCreateShader(35633);
    GL20.glShaderSource(vertexShaderID, vertexSource);
    GL20.glCompileShader(vertexShaderID);
    if (GL20.glGetShaderi(vertexShaderID, 35713) == 0) {
      LOGGER.log(Level.ALL, GL20.glGetShaderInfoLog(vertexShaderID, 4096));
      throw new IllegalStateException("Unable to decompile vertex shader: 35633");
    } 
    String fragmentSource = ShaderExtension.readShader("fragment/" + fragName);
    int fragmentShaderID = GL20.glCreateShader(35632);
    GL20.glShaderSource(fragmentShaderID, fragmentSource);
    GL20.glCompileShader(fragmentShaderID);
    if (GL20.glGetShaderi(fragmentShaderID, 35713) == 0) {
      System.out.println(GL20.glGetShaderInfoLog(fragmentShaderID, 4096));
      throw new IllegalStateException("Unable to decompile shader: " + fragName + '謱');
    } 
    GL20.glAttachShader(shaderProgramID, vertexShaderID);
    GL20.glAttachShader(shaderProgramID, fragmentShaderID);
    GL20.glLinkProgram(shaderProgramID);
    this.shaderProgramID = shaderProgramID;
  }
  
  public ShaderProgram() {
    int shaderProgramID = GL20.glCreateProgram();
    String vertexSource = ShaderExtension.readShader("vertex/vertex.glsl");
    int vertexShaderID = GL20.glCreateShader(35633);
    GL20.glShaderSource(vertexShaderID, vertexSource);
    GL20.glCompileShader(vertexShaderID);
    if (GL20.glGetShaderi(vertexShaderID, 35713) == 0) {
      LOGGER.log(Level.ALL, GL20.glGetShaderInfoLog(vertexShaderID, 4096));
      throw new IllegalStateException("Unable to decompile vertex shader: 35633");
    } 
    String fragmentSource = ShaderExtension.readShader("fragment/" + this.fragmentName);
    int fragmentShaderID = GL20.glCreateShader(35632);
    GL20.glShaderSource(fragmentShaderID, fragmentSource);
    GL20.glCompileShader(fragmentShaderID);
    if (GL20.glGetShaderi(fragmentShaderID, 35713) == 0) {
      System.out.println(GL20.glGetShaderInfoLog(fragmentShaderID, 4096));
      throw new IllegalStateException("Unable to decompile shader: " + this.fragmentName + '謱');
    } 
    GL20.glAttachShader(shaderProgramID, vertexShaderID);
    GL20.glAttachShader(shaderProgramID, fragmentShaderID);
    GL20.glLinkProgram(shaderProgramID);
    this.shaderProgramID = shaderProgramID;
  }
  
  public float texelHeight(double... downscale) {
    double downScale = (downscale[0] == 0.0D) ? 1.0D : downscale[0];
    ScaledResolution scaledResolution = new ScaledResolution(mc);
    return (float)(downScale / scaledResolution.getScaledHeight());
  }
  
  public float texelWidth(double... downscale) {
    double downScale = (downscale[0] == 0.0D) ? 1.0D : downscale[0];
    ScaledResolution scaledResolution = new ScaledResolution(mc);
    return (float)(downScale / scaledResolution.getScaledWidth());
  }
  
  public int getUniform(String name) {
    return GL20.glGetUniformLocation(this.shaderProgramID, name);
  }
  
  public void setShaderUniformI(String name, int... args) {
    int loc = GL20.glGetUniformLocation(getShaderProgramID(), name);
    if (args.length > 1) {
      GL20.glUniform2i(loc, args[0], args[1]);
    } else {
      GL20.glUniform1i(loc, args[0]);
    } 
  }
  
  public void setShaderUniform(String name, float... args) {
    int loc = GL20.glGetUniformLocation(this.shaderProgramID, name);
    if (args.length > 1) {
      if (args.length > 2) {
        if (args.length > 3) {
          GL20.glUniform4f(loc, args[0], args[1], args[2], args[3]);
        } else {
          GL20.glUniform3f(loc, args[0], args[1], args[2]);
        } 
      } else {
        GL20.glUniform2f(loc, args[0], args[1]);
      } 
    } else {
      GL20.glUniform1f(loc, args[0]);
    } 
  }
}
