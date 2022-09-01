package java.awt.font;

import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public final class TextAttribute extends AttributedCharacterIterator.Attribute {
  private static final Map<String, TextAttribute> instanceMap = new HashMap<>(29);
  
  static final long serialVersionUID = 7744112784117861702L;
  
  protected TextAttribute(String paramString) {
    super(paramString);
    if (getClass() == TextAttribute.class)
      instanceMap.put(paramString, this); 
  }
  
  protected Object readResolve() throws InvalidObjectException {
    if (getClass() != TextAttribute.class)
      throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
    TextAttribute textAttribute = instanceMap.get(getName());
    if (textAttribute != null)
      return textAttribute; 
    throw new InvalidObjectException("unknown attribute name");
  }
  
  public static final TextAttribute FAMILY = new TextAttribute("family");
  
  public static final TextAttribute WEIGHT = new TextAttribute("weight");
  
  public static final Float WEIGHT_EXTRA_LIGHT = Float.valueOf(0.5F);
  
  public static final Float WEIGHT_LIGHT = Float.valueOf(0.75F);
  
  public static final Float WEIGHT_DEMILIGHT = Float.valueOf(0.875F);
  
  public static final Float WEIGHT_REGULAR = Float.valueOf(1.0F);
  
  public static final Float WEIGHT_SEMIBOLD = Float.valueOf(1.25F);
  
  public static final Float WEIGHT_MEDIUM = Float.valueOf(1.5F);
  
  public static final Float WEIGHT_DEMIBOLD = Float.valueOf(1.75F);
  
  public static final Float WEIGHT_BOLD = Float.valueOf(2.0F);
  
  public static final Float WEIGHT_HEAVY = Float.valueOf(2.25F);
  
  public static final Float WEIGHT_EXTRABOLD = Float.valueOf(2.5F);
  
  public static final Float WEIGHT_ULTRABOLD = Float.valueOf(2.75F);
  
  public static final TextAttribute WIDTH = new TextAttribute("width");
  
  public static final Float WIDTH_CONDENSED = Float.valueOf(0.75F);
  
  public static final Float WIDTH_SEMI_CONDENSED = Float.valueOf(0.875F);
  
  public static final Float WIDTH_REGULAR = Float.valueOf(1.0F);
  
  public static final Float WIDTH_SEMI_EXTENDED = Float.valueOf(1.25F);
  
  public static final Float WIDTH_EXTENDED = Float.valueOf(1.5F);
  
  public static final TextAttribute POSTURE = new TextAttribute("posture");
  
  public static final Float POSTURE_REGULAR = Float.valueOf(0.0F);
  
  public static final Float POSTURE_OBLIQUE = Float.valueOf(0.2F);
  
  public static final TextAttribute SIZE = new TextAttribute("size");
  
  public static final TextAttribute TRANSFORM = new TextAttribute("transform");
  
  public static final TextAttribute SUPERSCRIPT = new TextAttribute("superscript");
  
  public static final Integer SUPERSCRIPT_SUPER = Integer.valueOf(1);
  
  public static final Integer SUPERSCRIPT_SUB = Integer.valueOf(-1);
  
  public static final TextAttribute FONT = new TextAttribute("font");
  
  public static final TextAttribute CHAR_REPLACEMENT = new TextAttribute("char_replacement");
  
  public static final TextAttribute FOREGROUND = new TextAttribute("foreground");
  
  public static final TextAttribute BACKGROUND = new TextAttribute("background");
  
  public static final TextAttribute UNDERLINE = new TextAttribute("underline");
  
  public static final Integer UNDERLINE_ON = Integer.valueOf(0);
  
  public static final TextAttribute STRIKETHROUGH = new TextAttribute("strikethrough");
  
  public static final Boolean STRIKETHROUGH_ON = Boolean.TRUE;
  
  public static final TextAttribute RUN_DIRECTION = new TextAttribute("run_direction");
  
  public static final Boolean RUN_DIRECTION_LTR = Boolean.FALSE;
  
  public static final Boolean RUN_DIRECTION_RTL = Boolean.TRUE;
  
  public static final TextAttribute BIDI_EMBEDDING = new TextAttribute("bidi_embedding");
  
  public static final TextAttribute JUSTIFICATION = new TextAttribute("justification");
  
  public static final Float JUSTIFICATION_FULL = Float.valueOf(1.0F);
  
  public static final Float JUSTIFICATION_NONE = Float.valueOf(0.0F);
  
  public static final TextAttribute INPUT_METHOD_HIGHLIGHT = new TextAttribute("input method highlight");
  
  public static final TextAttribute INPUT_METHOD_UNDERLINE = new TextAttribute("input method underline");
  
  public static final Integer UNDERLINE_LOW_ONE_PIXEL = Integer.valueOf(1);
  
  public static final Integer UNDERLINE_LOW_TWO_PIXEL = Integer.valueOf(2);
  
  public static final Integer UNDERLINE_LOW_DOTTED = Integer.valueOf(3);
  
  public static final Integer UNDERLINE_LOW_GRAY = Integer.valueOf(4);
  
  public static final Integer UNDERLINE_LOW_DASHED = Integer.valueOf(5);
  
  public static final TextAttribute SWAP_COLORS = new TextAttribute("swap_colors");
  
  public static final Boolean SWAP_COLORS_ON = Boolean.TRUE;
  
  public static final TextAttribute NUMERIC_SHAPING = new TextAttribute("numeric_shaping");
  
  public static final TextAttribute KERNING = new TextAttribute("kerning");
  
  public static final Integer KERNING_ON = Integer.valueOf(1);
  
  public static final TextAttribute LIGATURES = new TextAttribute("ligatures");
  
  public static final Integer LIGATURES_ON = Integer.valueOf(1);
  
  public static final TextAttribute TRACKING = new TextAttribute("tracking");
  
  public static final Float TRACKING_TIGHT = Float.valueOf(-0.04F);
  
  public static final Float TRACKING_LOOSE = Float.valueOf(0.04F);
}
