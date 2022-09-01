package notthatuwu.xyz.mythrecode.api.utils.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.client.renderer.GlStateManager;
import notthatuwu.xyz.mythrecode.api.interfaces.MCHook;
import org.lwjgl.opengl.GL20;

public final class ShaderExtension implements MCHook {
  private ShaderExtension() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  private static final Logger LOGGER = Logger.getLogger(ShaderExtension.class.getName());
  
  public static String readShader(String fileName) {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      InputStreamReader inputStreamReader = new InputStreamReader(Objects.<InputStream>requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream(String.format("assets/minecraft/shaders2/%s", new Object[] { fileName }))));
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String line;
      while ((line = bufferedReader.readLine()) != null)
        stringBuilder.append(line).append('\n'); 
      bufferedReader.close();
      inputStreamReader.close();
    } catch (IOException e) {
      LOGGER.log(Level.ALL, e.getMessage());
    } 
    return stringBuilder.toString();
  }
  
  public static void bindZero() {
    GlStateManager.bindTexture(0);
  }
  
  public static void useShader(int programID) {
    GL20.glUseProgram(programID);
  }
  
  public static void deleteProgram() {
    GL20.glUseProgram(0);
  }
}
