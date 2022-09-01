package shadersmod.client;

import java.io.InputStream;

public interface IShaderPack {
  String getName();
  
  InputStream getResourceAsStream(String paramString);
  
  boolean hasDirectory(String paramString);
  
  void close();
}
