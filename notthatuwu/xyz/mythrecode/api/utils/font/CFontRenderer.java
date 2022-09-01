package notthatuwu.xyz.mythrecode.api.utils.font;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class CFontRenderer extends CFont {
  protected CFont.CharData[] boldChars = new CFont.CharData[256];
  
  protected CFont.CharData[] italicChars = new CFont.CharData[256];
  
  protected CFont.CharData[] boldItalicChars = new CFont.CharData[256];
  
  private final int[] colorCode = new int[32];
  
  private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
  
  protected DynamicTexture texBold;
  
  protected DynamicTexture texItalic;
  
  protected DynamicTexture texItalicBold;
  
  public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
    super(font, antiAlias, fractionalMetrics);
    setupMinecraftColorcodes();
    setupBoldItalicIDs();
  }
  
  public float drawStringWithShadow(String text, double x, double y, int color) {
    float shadowWidth = drawString(text, x + 0.5D, y + 0.5D, color, true);
    return Math.max(shadowWidth, drawString(text, x, y, color, false));
  }
  
  public float drawString(String text, float x, float y, int color) {
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    RenderUtils.color(color);
    return drawString(text, x, y, color, false);
  }
  
  public float drawCenteredString(String text, double x, double y, int color) {
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    return drawString(text, (float)(x - (getStringWidth(text) / 2)), (float)y, color);
  }
  
  public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
    return drawStringWithShadow(text, (x - (getStringWidth(text) / 2)), y, color);
  }
  
  public float drawCenteredStringWithShadow(String text, double x, double y, int color) {
    return drawStringWithShadow(text, x - (getStringWidth(text) / 2), y, color);
  }
  
  public float drawString(String text, double x, double y, int color, boolean shadow) {
    GlStateManager.enableBlend();
    x--;
    if (text == null)
      return 0.0F; 
    if (color == 553648127)
      color = 16777215; 
    if ((color & 0xFC000000) == 0)
      color |= 0xFF000000; 
    if (shadow)
      color = (new Color(0, 0, 0)).getRGB(); 
    CFont.CharData[] currentData = this.charData;
    float alpha = (color >> 24 & 0xFF) / 255.0F;
    boolean randomCase = false;
    boolean bold = false;
    boolean italic = false;
    boolean strikethrough = false;
    boolean underline = false;
    boolean render = true;
    x *= 2.0D;
    y = (y - 3.0D) * 2.0D;
    if (render) {
      GL11.glPushMatrix();
      GlStateManager.scale(0.5D, 0.5D, 0.5D);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
      int size = text.length();
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(this.tex.getGlTextureId());
      GL11.glBindTexture(3553, this.tex.getGlTextureId());
      int i = 0;
      while (i < size) {
        char character = text.charAt(i);
        if (character == '§' && i < size) {
          int colorIndex = 21;
          try {
            colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
          } catch (Exception e) {
            e.printStackTrace();
          } 
          if (colorIndex < 16) {
            bold = false;
            italic = false;
            randomCase = false;
            underline = false;
            strikethrough = false;
            GlStateManager.bindTexture(this.tex.getGlTextureId());
            currentData = this.charData;
            if (colorIndex < 0 || colorIndex > 15)
              colorIndex = 15; 
            if (shadow)
              colorIndex += 16; 
            int colorcode = this.colorCode[colorIndex];
            GlStateManager.color((colorcode >> 16 & 0xFF) / 255.0F, (colorcode >> 8 & 0xFF) / 255.0F, (colorcode & 0xFF) / 255.0F, alpha);
          } else if (colorIndex == 16) {
            randomCase = true;
          } else if (colorIndex == 17) {
            bold = true;
            if (italic) {
              GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
              currentData = this.boldItalicChars;
            } else {
              GlStateManager.bindTexture(this.texBold.getGlTextureId());
              currentData = this.boldChars;
            } 
          } else if (colorIndex == 18) {
            strikethrough = true;
          } else if (colorIndex == 19) {
            underline = true;
          } else if (colorIndex == 20) {
            italic = true;
            if (bold) {
              GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
              currentData = this.boldItalicChars;
            } else {
              GlStateManager.bindTexture(this.texItalic.getGlTextureId());
              currentData = this.italicChars;
            } 
          } else if (colorIndex == 21) {
            bold = false;
            italic = false;
            randomCase = false;
            underline = false;
            strikethrough = false;
            GlStateManager.color((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
            GlStateManager.bindTexture(this.tex.getGlTextureId());
            currentData = this.charData;
          } 
          i++;
        } else if (character < currentData.length && character >= '\000') {
          GL11.glBegin(4);
          drawChar(currentData, character, (float)x, (float)y);
          GL11.glEnd();
          if (strikethrough)
            drawLine(x, y + ((currentData[character]).height / 2), x + (currentData[character]).width - 8.0D, y + ((currentData[character]).height / 2), 1.0F); 
          if (underline)
            drawLine(x, y + (currentData[character]).height - 2.0D, x + (currentData[character]).width - 8.0D, y + (currentData[character]).height - 2.0D, 1.0F); 
          x += ((currentData[character]).width - 8 + this.charOffset);
        } 
        i++;
      } 
      GL11.glHint(3155, 4352);
      GL11.glPopMatrix();
    } 
    GlStateManager.disableBlend();
    return (float)x / 2.0F;
  }
  
  public int getStringWidth(String text) {
    if (text == null)
      return 0; 
    int width = 0;
    CFont.CharData[] currentData = this.charData;
    boolean bold = false;
    boolean italic = false;
    int size = text.length();
    int i = 0;
    while (i < size) {
      char character = text.charAt(i);
      if (character == '§' && i < size) {
        int colorIndex = "0123456789abcdefklmnor".indexOf(character);
        if (colorIndex < 16) {
          bold = false;
          italic = false;
        } else if (colorIndex == 17) {
          bold = true;
          currentData = italic ? this.boldItalicChars : this.boldChars;
        } else if (colorIndex == 20) {
          italic = true;
          currentData = bold ? this.boldItalicChars : this.italicChars;
        } else if (colorIndex == 21) {
          bold = false;
          italic = false;
          currentData = this.charData;
        } 
        i++;
      } else if (character < currentData.length && character >= '\000') {
        width += (currentData[character]).width - 8 + this.charOffset;
      } 
      i++;
    } 
    return width / 2;
  }
  
  public void setFont(Font font) {
    super.setFont(font);
    setupBoldItalicIDs();
  }
  
  public void setAntiAlias(boolean antiAlias) {
    super.setAntiAlias(antiAlias);
    setupBoldItalicIDs();
  }
  
  public void setFractionalMetrics(boolean fractionalMetrics) {
    super.setFractionalMetrics(fractionalMetrics);
    setupBoldItalicIDs();
  }
  
  private void setupBoldItalicIDs() {
    this.texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
    this.texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
  }
  
  private void drawLine(double x, double y, double x1, double y1, float width) {
    GL11.glDisable(3553);
    GL11.glLineWidth(width);
    GL11.glBegin(1);
    GL11.glVertex2d(x, y);
    GL11.glVertex2d(x1, y1);
    GL11.glEnd();
    GL11.glEnable(3553);
  }
  
  public List<String> wrapWords(String text, double width) {
    ArrayList<String> finalWords = new ArrayList<>();
    if (getStringWidth(text) > width) {
      String[] words = text.split(" ");
      String currentWord = "";
      int lastColorCode = 65535;
      String[] arrstring = words;
      int n = arrstring.length;
      int n2 = 0;
      while (n2 < n) {
        String word = arrstring[n2];
        int i = 0;
        while (i < (word.toCharArray()).length) {
          char c = word.toCharArray()[i];
          if (c == '§' && i < (word.toCharArray()).length - 1)
            lastColorCode = word.toCharArray()[i + 1]; 
          i++;
        } 
        if (getStringWidth(currentWord + word + " ") < width) {
          currentWord = currentWord + word + " ";
        } else {
          finalWords.add(currentWord);
          currentWord = (167 + lastColorCode) + word + " ";
        } 
        n2++;
      } 
      if (currentWord.length() > 0)
        if (getStringWidth(currentWord) < width) {
          finalWords.add((167 + lastColorCode) + currentWord + " ");
          currentWord = "";
        } else {
          for (String s : formatString(currentWord, width))
            finalWords.add(s); 
        }  
    } else {
      finalWords.add(text);
    } 
    return finalWords;
  }
  
  public List<String> formatString(String string, double width) {
    ArrayList<String> finalWords = new ArrayList<>();
    String currentWord = "";
    int lastColorCode = 65535;
    char[] chars = string.toCharArray();
    int i = 0;
    while (i < chars.length) {
      char c = chars[i];
      if (c == '§' && i < chars.length - 1)
        lastColorCode = chars[i + 1]; 
      if (getStringWidth(currentWord + c) < width) {
        currentWord = currentWord + c;
      } else {
        finalWords.add(currentWord);
        currentWord = String.valueOf(167 + lastColorCode) + c;
      } 
      i++;
    } 
    if (currentWord.length() > 0)
      finalWords.add(currentWord); 
    return finalWords;
  }
  
  private void setupMinecraftColorcodes() {
    int index = 0;
    while (index < 32) {
      int noClue = (index >> 3 & 0x1) * 85;
      int red = (index >> 2 & 0x1) * 170 + noClue;
      int green = (index >> 1 & 0x1) * 170 + noClue;
      int blue = (index >> 0 & 0x1) * 170 + noClue;
      if (index == 6)
        red += 85; 
      if (index >= 16) {
        red /= 4;
        green /= 4;
        blue /= 4;
      } 
      this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
      index++;
    } 
  }
}
