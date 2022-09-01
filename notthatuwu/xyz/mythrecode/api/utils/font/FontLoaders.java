package notthatuwu.xyz.mythrecode.api.utils.font;

import java.awt.Font;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class FontLoaders {
  public static CFontRenderer Ali15 = new CFontRenderer(getFonts("Ali.ttf", 15), true, true);
  
  public static CFontRenderer Ali18 = new CFontRenderer(getFonts("Ali.ttf", 18), true, true);
  
  public static CFontRenderer Ali25 = new CFontRenderer(getFonts("Ali.ttf", 25), true, true);
  
  public static CFontRenderer Sfui9 = new CFontRenderer(getFonts("SF-UI.ttf", 9), true, true);
  
  public static CFontRenderer Sfui12 = new CFontRenderer(getFonts("SF-UI.ttf", 12), true, true);
  
  public static CFontRenderer Sfui13 = new CFontRenderer(getFonts("SF-UI.ttf", 13), true, true);
  
  public static CFontRenderer Sfui14 = new CFontRenderer(getFonts("SF-UI.ttf", 14), true, true);
  
  public static CFontRenderer Sfui15 = new CFontRenderer(getFonts("SF-UI.ttf", 15), true, true);
  
  public static CFontRenderer Sfui16 = new CFontRenderer(getFonts("SF-UI.ttf", 16), true, true);
  
  public static CFontRenderer Sfui18 = new CFontRenderer(getFonts("SF-UI.ttf", 18), true, true);
  
  public static CFontRenderer Sfui19 = new CFontRenderer(getFonts("SF-UI.ttf", 19), true, true);
  
  public static CFontRenderer Sfui20 = new CFontRenderer(getFonts("SF-UI.ttf", 20), true, true);
  
  public static CFontRenderer Sfui22 = new CFontRenderer(getFonts("SF-UI.ttf", 22), true, true);
  
  public static CFontRenderer Sfui35 = new CFontRenderer(getFonts("SF-UI.ttf", 40), true, true);
  
  public static CFontRenderer Sfui40 = new CFontRenderer(getFonts("SF-UI.ttf", 40), true, true);
  
  public static CFontRenderer Sfui70 = new CFontRenderer(getFonts("SF-UI.ttf", 70), true, true);
  
  public static CFontRenderer brains = new CFontRenderer(getFonts("brains.ttf", 18), true, true);
  
  public static CFontRenderer chill = new CFontRenderer(getFonts("chill.ttf", 20), true, true);
  
  public static CFontRenderer realjapan = new CFontRenderer(getFonts("realjapan.ttf", 40), true, true);
  
  public static CFontRenderer NovICON18 = new CFontRenderer(getFonts("big.ttf", 18), true, true);
  
  public static CFontRenderer big = new CFontRenderer(getFonts("big.ttf", 40), true, true);
  
  public static CFontRenderer bigfont = new CFontRenderer(getFonts("bigfont.ttf", 32), true, true);
  
  public static CFontRenderer quickSand = new CFontRenderer(getFonts("quicksand.ttf", 20), true, true);
  
  private static Font getFonts(String fontname, int size) {
    Font font;
    try {
      InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("myth/font/" + fontname)).getInputStream();
      font = Font.createFont(0, is);
      font = font.deriveFont(0, size);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Error loading font");
      font = new Font("default", 0, size);
    } 
    return font;
  }
}
