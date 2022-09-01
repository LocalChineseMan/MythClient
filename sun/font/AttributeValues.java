package sun.font;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.font.GraphicAttribute;
import java.awt.font.NumericShaper;
import java.awt.font.TextAttribute;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.im.InputMethodHighlight;
import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public final class AttributeValues implements Cloneable {
  private int defined;
  
  private int nondefault;
  
  private String family = "Default";
  
  private float weight = 1.0F;
  
  private float width = 1.0F;
  
  private float posture;
  
  private float size = 12.0F;
  
  private float tracking;
  
  private NumericShaper numericShaping;
  
  private AffineTransform transform;
  
  private GraphicAttribute charReplacement;
  
  private Paint foreground;
  
  private Paint background;
  
  private float justification = 1.0F;
  
  private Object imHighlight;
  
  private Font font;
  
  private byte imUnderline = -1;
  
  private byte superscript;
  
  private byte underline = -1;
  
  private byte runDirection = -2;
  
  private byte bidiEmbedding;
  
  private byte kerning;
  
  private byte ligatures;
  
  private boolean strikethrough;
  
  private boolean swapColors;
  
  private AffineTransform baselineTransform;
  
  private AffineTransform charTransform;
  
  private static final AttributeValues DEFAULT = new AttributeValues();
  
  public String getFamily() {
    return this.family;
  }
  
  public void setFamily(String paramString) {
    this.family = paramString;
    update(EAttribute.EFAMILY);
  }
  
  public float getWeight() {
    return this.weight;
  }
  
  public void setWeight(float paramFloat) {
    this.weight = paramFloat;
    update(EAttribute.EWEIGHT);
  }
  
  public float getWidth() {
    return this.width;
  }
  
  public void setWidth(float paramFloat) {
    this.width = paramFloat;
    update(EAttribute.EWIDTH);
  }
  
  public float getPosture() {
    return this.posture;
  }
  
  public void setPosture(float paramFloat) {
    this.posture = paramFloat;
    update(EAttribute.EPOSTURE);
  }
  
  public float getSize() {
    return this.size;
  }
  
  public void setSize(float paramFloat) {
    this.size = paramFloat;
    update(EAttribute.ESIZE);
  }
  
  public AffineTransform getTransform() {
    return this.transform;
  }
  
  public void setTransform(AffineTransform paramAffineTransform) {
    this.transform = (paramAffineTransform == null || paramAffineTransform.isIdentity()) ? DEFAULT.transform : new AffineTransform(paramAffineTransform);
    updateDerivedTransforms();
    update(EAttribute.ETRANSFORM);
  }
  
  public void setTransform(TransformAttribute paramTransformAttribute) {
    this
      
      .transform = (paramTransformAttribute == null || paramTransformAttribute.isIdentity()) ? DEFAULT.transform : paramTransformAttribute.getTransform();
    updateDerivedTransforms();
    update(EAttribute.ETRANSFORM);
  }
  
  public int getSuperscript() {
    return this.superscript;
  }
  
  public void setSuperscript(int paramInt) {
    this.superscript = (byte)paramInt;
    update(EAttribute.ESUPERSCRIPT);
  }
  
  public Font getFont() {
    return this.font;
  }
  
  public void setFont(Font paramFont) {
    this.font = paramFont;
    update(EAttribute.EFONT);
  }
  
  public GraphicAttribute getCharReplacement() {
    return this.charReplacement;
  }
  
  public void setCharReplacement(GraphicAttribute paramGraphicAttribute) {
    this.charReplacement = paramGraphicAttribute;
    update(EAttribute.ECHAR_REPLACEMENT);
  }
  
  public Paint getForeground() {
    return this.foreground;
  }
  
  public void setForeground(Paint paramPaint) {
    this.foreground = paramPaint;
    update(EAttribute.EFOREGROUND);
  }
  
  public Paint getBackground() {
    return this.background;
  }
  
  public void setBackground(Paint paramPaint) {
    this.background = paramPaint;
    update(EAttribute.EBACKGROUND);
  }
  
  public int getUnderline() {
    return this.underline;
  }
  
  public void setUnderline(int paramInt) {
    this.underline = (byte)paramInt;
    update(EAttribute.EUNDERLINE);
  }
  
  public boolean getStrikethrough() {
    return this.strikethrough;
  }
  
  public void setStrikethrough(boolean paramBoolean) {
    this.strikethrough = paramBoolean;
    update(EAttribute.ESTRIKETHROUGH);
  }
  
  public int getRunDirection() {
    return this.runDirection;
  }
  
  public void setRunDirection(int paramInt) {
    this.runDirection = (byte)paramInt;
    update(EAttribute.ERUN_DIRECTION);
  }
  
  public int getBidiEmbedding() {
    return this.bidiEmbedding;
  }
  
  public void setBidiEmbedding(int paramInt) {
    this.bidiEmbedding = (byte)paramInt;
    update(EAttribute.EBIDI_EMBEDDING);
  }
  
  public float getJustification() {
    return this.justification;
  }
  
  public void setJustification(float paramFloat) {
    this.justification = paramFloat;
    update(EAttribute.EJUSTIFICATION);
  }
  
  public Object getInputMethodHighlight() {
    return this.imHighlight;
  }
  
  public void setInputMethodHighlight(Annotation paramAnnotation) {
    this.imHighlight = paramAnnotation;
    update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
  }
  
  public void setInputMethodHighlight(InputMethodHighlight paramInputMethodHighlight) {
    this.imHighlight = paramInputMethodHighlight;
    update(EAttribute.EINPUT_METHOD_HIGHLIGHT);
  }
  
  public int getInputMethodUnderline() {
    return this.imUnderline;
  }
  
  public void setInputMethodUnderline(int paramInt) {
    this.imUnderline = (byte)paramInt;
    update(EAttribute.EINPUT_METHOD_UNDERLINE);
  }
  
  public boolean getSwapColors() {
    return this.swapColors;
  }
  
  public void setSwapColors(boolean paramBoolean) {
    this.swapColors = paramBoolean;
    update(EAttribute.ESWAP_COLORS);
  }
  
  public NumericShaper getNumericShaping() {
    return this.numericShaping;
  }
  
  public void setNumericShaping(NumericShaper paramNumericShaper) {
    this.numericShaping = paramNumericShaper;
    update(EAttribute.ENUMERIC_SHAPING);
  }
  
  public int getKerning() {
    return this.kerning;
  }
  
  public void setKerning(int paramInt) {
    this.kerning = (byte)paramInt;
    update(EAttribute.EKERNING);
  }
  
  public float getTracking() {
    return this.tracking;
  }
  
  public void setTracking(float paramFloat) {
    this.tracking = (byte)(int)paramFloat;
    update(EAttribute.ETRACKING);
  }
  
  public int getLigatures() {
    return this.ligatures;
  }
  
  public void setLigatures(int paramInt) {
    this.ligatures = (byte)paramInt;
    update(EAttribute.ELIGATURES);
  }
  
  public AffineTransform getBaselineTransform() {
    return this.baselineTransform;
  }
  
  public AffineTransform getCharTransform() {
    return this.charTransform;
  }
  
  public static int getMask(EAttribute paramEAttribute) {
    return paramEAttribute.mask;
  }
  
  public static int getMask(EAttribute... paramVarArgs) {
    int i = 0;
    for (EAttribute eAttribute : paramVarArgs)
      i |= eAttribute.mask; 
    return i;
  }
  
  public static final int MASK_ALL = getMask(EAttribute.class.getEnumConstants());
  
  private static final String DEFINED_KEY = "sun.font.attributevalues.defined_key";
  
  public void unsetDefault() {
    this.defined &= this.nondefault;
  }
  
  public void defineAll(int paramInt) {
    this.defined |= paramInt;
    if ((this.defined & EAttribute.EBASELINE_TRANSFORM.mask) != 0)
      throw new InternalError("can't define derived attribute"); 
  }
  
  public boolean allDefined(int paramInt) {
    return ((this.defined & paramInt) == paramInt);
  }
  
  public boolean anyDefined(int paramInt) {
    return ((this.defined & paramInt) != 0);
  }
  
  public boolean anyNonDefault(int paramInt) {
    return ((this.nondefault & paramInt) != 0);
  }
  
  public boolean isDefined(EAttribute paramEAttribute) {
    return ((this.defined & paramEAttribute.mask) != 0);
  }
  
  public boolean isNonDefault(EAttribute paramEAttribute) {
    return ((this.nondefault & paramEAttribute.mask) != 0);
  }
  
  public void setDefault(EAttribute paramEAttribute) {
    if (paramEAttribute.att == null)
      throw new InternalError("can't set default derived attribute: " + paramEAttribute); 
    i_set(paramEAttribute, DEFAULT);
    this.defined |= paramEAttribute.mask;
    this.nondefault &= paramEAttribute.mask ^ 0xFFFFFFFF;
  }
  
  public void unset(EAttribute paramEAttribute) {
    if (paramEAttribute.att == null)
      throw new InternalError("can't unset derived attribute: " + paramEAttribute); 
    i_set(paramEAttribute, DEFAULT);
    this.defined &= paramEAttribute.mask ^ 0xFFFFFFFF;
    this.nondefault &= paramEAttribute.mask ^ 0xFFFFFFFF;
  }
  
  public void set(EAttribute paramEAttribute, AttributeValues paramAttributeValues) {
    if (paramEAttribute.att == null)
      throw new InternalError("can't set derived attribute: " + paramEAttribute); 
    if (paramAttributeValues == null || paramAttributeValues == DEFAULT) {
      setDefault(paramEAttribute);
    } else if ((paramAttributeValues.defined & paramEAttribute.mask) != 0) {
      i_set(paramEAttribute, paramAttributeValues);
      update(paramEAttribute);
    } 
  }
  
  public void set(EAttribute paramEAttribute, Object paramObject) {
    if (paramEAttribute.att == null)
      throw new InternalError("can't set derived attribute: " + paramEAttribute); 
    if (paramObject != null)
      try {
        i_set(paramEAttribute, paramObject);
        update(paramEAttribute);
        return;
      } catch (Exception exception) {} 
    setDefault(paramEAttribute);
  }
  
  public Object get(EAttribute paramEAttribute) {
    if (paramEAttribute.att == null)
      throw new InternalError("can't get derived attribute: " + paramEAttribute); 
    if ((this.nondefault & paramEAttribute.mask) != 0)
      return i_get(paramEAttribute); 
    return null;
  }
  
  public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    return merge(paramMap, MASK_ALL);
  }
  
  public AttributeValues merge(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt) {
    if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap)
      .getValues() != null) {
      merge(((AttributeMap)paramMap).getValues(), paramInt);
    } else if (paramMap != null && !paramMap.isEmpty()) {
      for (Map.Entry<? extends AttributedCharacterIterator.Attribute, ?> entry : paramMap.entrySet()) {
        try {
          EAttribute eAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)entry.getKey());
          if (eAttribute != null && (paramInt & eAttribute.mask) != 0)
            set(eAttribute, entry.getValue()); 
        } catch (ClassCastException classCastException) {}
      } 
    } 
    return this;
  }
  
  public AttributeValues merge(AttributeValues paramAttributeValues) {
    return merge(paramAttributeValues, MASK_ALL);
  }
  
  public AttributeValues merge(AttributeValues paramAttributeValues, int paramInt) {
    int i = paramInt & paramAttributeValues.defined;
    for (EAttribute eAttribute : EAttribute.atts) {
      if (i == 0)
        break; 
      if ((i & eAttribute.mask) != 0) {
        i &= eAttribute.mask ^ 0xFFFFFFFF;
        i_set(eAttribute, paramAttributeValues);
        update(eAttribute);
      } 
    } 
    return this;
  }
  
  public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap) {
    return fromMap(paramMap, MASK_ALL);
  }
  
  public static AttributeValues fromMap(Map<? extends AttributedCharacterIterator.Attribute, ?> paramMap, int paramInt) {
    return (new AttributeValues()).merge(paramMap, paramInt);
  }
  
  public Map<TextAttribute, Object> toMap(Map<TextAttribute, Object> paramMap) {
    if (paramMap == null)
      paramMap = new HashMap<>(); 
    int i;
    byte b;
    for (i = this.defined, b = 0; i != 0; b++) {
      EAttribute eAttribute = EAttribute.atts[b];
      if ((i & eAttribute.mask) != 0) {
        i &= eAttribute.mask ^ 0xFFFFFFFF;
        paramMap.put(eAttribute.att, get(eAttribute));
      } 
    } 
    return paramMap;
  }
  
  public static boolean is16Hashtable(Hashtable<Object, Object> paramHashtable) {
    return paramHashtable.containsKey("sun.font.attributevalues.defined_key");
  }
  
  public static AttributeValues fromSerializableHashtable(Hashtable<Object, Object> paramHashtable) {
    AttributeValues attributeValues = new AttributeValues();
    if (paramHashtable != null && !paramHashtable.isEmpty())
      for (Map.Entry<Object, Object> entry : paramHashtable.entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        if (object1.equals("sun.font.attributevalues.defined_key")) {
          attributeValues.defineAll(((Integer)object2).intValue());
          continue;
        } 
        try {
          EAttribute eAttribute = EAttribute.forAttribute((AttributedCharacterIterator.Attribute)object1);
          if (eAttribute != null)
            attributeValues.set(eAttribute, object2); 
        } catch (ClassCastException classCastException) {}
      }  
    return attributeValues;
  }
  
  public Hashtable<Object, Object> toSerializableHashtable() {
    Hashtable<Object, Object> hashtable = new Hashtable<>();
    int i = this.defined;
    int j;
    byte b;
    for (j = this.defined, b = 0; j != 0; b++) {
      EAttribute eAttribute = EAttribute.atts[b];
      if ((j & eAttribute.mask) != 0) {
        j &= eAttribute.mask ^ 0xFFFFFFFF;
        Object object = get(eAttribute);
        if (object != null)
          if (object instanceof java.io.Serializable) {
            hashtable.put(eAttribute.att, object);
          } else {
            i &= eAttribute.mask ^ 0xFFFFFFFF;
          }  
      } 
    } 
    hashtable.put("sun.font.attributevalues.defined_key", Integer.valueOf(i));
    return hashtable;
  }
  
  public int hashCode() {
    return this.defined << 8 ^ this.nondefault;
  }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((AttributeValues)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public boolean equals(AttributeValues paramAttributeValues) {
    if (paramAttributeValues == null)
      return false; 
    if (paramAttributeValues == this)
      return true; 
    return (this.defined == paramAttributeValues.defined && this.nondefault == paramAttributeValues.nondefault && this.underline == paramAttributeValues.underline && this.strikethrough == paramAttributeValues.strikethrough && this.superscript == paramAttributeValues.superscript && this.width == paramAttributeValues.width && this.kerning == paramAttributeValues.kerning && this.tracking == paramAttributeValues.tracking && this.ligatures == paramAttributeValues.ligatures && this.runDirection == paramAttributeValues.runDirection && this.bidiEmbedding == paramAttributeValues.bidiEmbedding && this.swapColors == paramAttributeValues.swapColors && 
      
      equals(this.transform, paramAttributeValues.transform) && 
      equals(this.foreground, paramAttributeValues.foreground) && 
      equals(this.background, paramAttributeValues.background) && 
      equals(this.numericShaping, paramAttributeValues.numericShaping) && 
      equals(Float.valueOf(this.justification), Float.valueOf(paramAttributeValues.justification)) && 
      equals(this.charReplacement, paramAttributeValues.charReplacement) && this.size == paramAttributeValues.size && this.weight == paramAttributeValues.weight && this.posture == paramAttributeValues.posture && 
      
      equals(this.family, paramAttributeValues.family) && 
      equals(this.font, paramAttributeValues.font) && this.imUnderline == paramAttributeValues.imUnderline && 
      
      equals(this.imHighlight, paramAttributeValues.imHighlight));
  }
  
  public AttributeValues clone() {
    try {
      AttributeValues attributeValues = (AttributeValues)super.clone();
      if (this.transform != null) {
        attributeValues.transform = new AffineTransform(this.transform);
        attributeValues.updateDerivedTransforms();
      } 
      return attributeValues;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('{');
    int i;
    byte b;
    for (i = this.defined, b = 0; i != 0; b++) {
      EAttribute eAttribute = EAttribute.atts[b];
      if ((i & eAttribute.mask) != 0) {
        i &= eAttribute.mask ^ 0xFFFFFFFF;
        if (stringBuilder.length() > 1)
          stringBuilder.append(", "); 
        stringBuilder.append(eAttribute);
        stringBuilder.append('=');
        switch (null.$SwitchMap$sun$font$EAttribute[eAttribute.ordinal()]) {
          case 1:
            stringBuilder.append('"');
            stringBuilder.append(this.family);
            stringBuilder.append('"');
            break;
          case 2:
            stringBuilder.append(this.weight);
            break;
          case 3:
            stringBuilder.append(this.width);
            break;
          case 4:
            stringBuilder.append(this.posture);
            break;
          case 5:
            stringBuilder.append(this.size);
            break;
          case 6:
            stringBuilder.append(this.transform);
            break;
          case 7:
            stringBuilder.append(this.superscript);
            break;
          case 8:
            stringBuilder.append(this.font);
            break;
          case 9:
            stringBuilder.append(this.charReplacement);
            break;
          case 10:
            stringBuilder.append(this.foreground);
            break;
          case 11:
            stringBuilder.append(this.background);
            break;
          case 12:
            stringBuilder.append(this.underline);
            break;
          case 13:
            stringBuilder.append(this.strikethrough);
            break;
          case 14:
            stringBuilder.append(this.runDirection);
            break;
          case 15:
            stringBuilder.append(this.bidiEmbedding);
            break;
          case 16:
            stringBuilder.append(this.justification);
            break;
          case 17:
            stringBuilder.append(this.imHighlight);
            break;
          case 18:
            stringBuilder.append(this.imUnderline);
            break;
          case 19:
            stringBuilder.append(this.swapColors);
            break;
          case 20:
            stringBuilder.append(this.numericShaping);
            break;
          case 21:
            stringBuilder.append(this.kerning);
            break;
          case 22:
            stringBuilder.append(this.ligatures);
            break;
          case 23:
            stringBuilder.append(this.tracking);
            break;
          default:
            throw new InternalError();
        } 
        if ((this.nondefault & eAttribute.mask) == 0)
          stringBuilder.append('*'); 
      } 
    } 
    stringBuilder.append("[btx=" + this.baselineTransform + ", ctx=" + this.charTransform + "]");
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2) {
    return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2);
  }
  
  private void update(EAttribute paramEAttribute) {
    this.defined |= paramEAttribute.mask;
    if (i_validate(paramEAttribute)) {
      if (i_equals(paramEAttribute, DEFAULT)) {
        this.nondefault &= paramEAttribute.mask ^ 0xFFFFFFFF;
      } else {
        this.nondefault |= paramEAttribute.mask;
      } 
    } else {
      setDefault(paramEAttribute);
    } 
  }
  
  private void i_set(EAttribute paramEAttribute, AttributeValues paramAttributeValues) {
    switch (null.$SwitchMap$sun$font$EAttribute[paramEAttribute.ordinal()]) {
      case 1:
        this.family = paramAttributeValues.family;
        return;
      case 2:
        this.weight = paramAttributeValues.weight;
        return;
      case 3:
        this.width = paramAttributeValues.width;
        return;
      case 4:
        this.posture = paramAttributeValues.posture;
        return;
      case 5:
        this.size = paramAttributeValues.size;
        return;
      case 6:
        this.transform = paramAttributeValues.transform;
        updateDerivedTransforms();
        return;
      case 7:
        this.superscript = paramAttributeValues.superscript;
        return;
      case 8:
        this.font = paramAttributeValues.font;
        return;
      case 9:
        this.charReplacement = paramAttributeValues.charReplacement;
        return;
      case 10:
        this.foreground = paramAttributeValues.foreground;
        return;
      case 11:
        this.background = paramAttributeValues.background;
        return;
      case 12:
        this.underline = paramAttributeValues.underline;
        return;
      case 13:
        this.strikethrough = paramAttributeValues.strikethrough;
        return;
      case 14:
        this.runDirection = paramAttributeValues.runDirection;
        return;
      case 15:
        this.bidiEmbedding = paramAttributeValues.bidiEmbedding;
        return;
      case 16:
        this.justification = paramAttributeValues.justification;
        return;
      case 17:
        this.imHighlight = paramAttributeValues.imHighlight;
        return;
      case 18:
        this.imUnderline = paramAttributeValues.imUnderline;
        return;
      case 19:
        this.swapColors = paramAttributeValues.swapColors;
        return;
      case 20:
        this.numericShaping = paramAttributeValues.numericShaping;
        return;
      case 21:
        this.kerning = paramAttributeValues.kerning;
        return;
      case 22:
        this.ligatures = paramAttributeValues.ligatures;
        return;
      case 23:
        this.tracking = paramAttributeValues.tracking;
        return;
    } 
    throw new InternalError();
  }
  
  private boolean i_equals(EAttribute paramEAttribute, AttributeValues paramAttributeValues) {
    switch (null.$SwitchMap$sun$font$EAttribute[paramEAttribute.ordinal()]) {
      case 1:
        return equals(this.family, paramAttributeValues.family);
      case 2:
        return (this.weight == paramAttributeValues.weight);
      case 3:
        return (this.width == paramAttributeValues.width);
      case 4:
        return (this.posture == paramAttributeValues.posture);
      case 5:
        return (this.size == paramAttributeValues.size);
      case 6:
        return equals(this.transform, paramAttributeValues.transform);
      case 7:
        return (this.superscript == paramAttributeValues.superscript);
      case 8:
        return equals(this.font, paramAttributeValues.font);
      case 9:
        return equals(this.charReplacement, paramAttributeValues.charReplacement);
      case 10:
        return equals(this.foreground, paramAttributeValues.foreground);
      case 11:
        return equals(this.background, paramAttributeValues.background);
      case 12:
        return (this.underline == paramAttributeValues.underline);
      case 13:
        return (this.strikethrough == paramAttributeValues.strikethrough);
      case 14:
        return (this.runDirection == paramAttributeValues.runDirection);
      case 15:
        return (this.bidiEmbedding == paramAttributeValues.bidiEmbedding);
      case 16:
        return (this.justification == paramAttributeValues.justification);
      case 17:
        return equals(this.imHighlight, paramAttributeValues.imHighlight);
      case 18:
        return (this.imUnderline == paramAttributeValues.imUnderline);
      case 19:
        return (this.swapColors == paramAttributeValues.swapColors);
      case 20:
        return equals(this.numericShaping, paramAttributeValues.numericShaping);
      case 21:
        return (this.kerning == paramAttributeValues.kerning);
      case 22:
        return (this.ligatures == paramAttributeValues.ligatures);
      case 23:
        return (this.tracking == paramAttributeValues.tracking);
    } 
    throw new InternalError();
  }
  
  private void i_set(EAttribute paramEAttribute, Object paramObject) {
    switch (null.$SwitchMap$sun$font$EAttribute[paramEAttribute.ordinal()]) {
      case 1:
        this.family = ((String)paramObject).trim();
        return;
      case 2:
        this.weight = ((Number)paramObject).floatValue();
        return;
      case 3:
        this.width = ((Number)paramObject).floatValue();
        return;
      case 4:
        this.posture = ((Number)paramObject).floatValue();
        return;
      case 5:
        this.size = ((Number)paramObject).floatValue();
        return;
      case 6:
        if (paramObject instanceof TransformAttribute) {
          TransformAttribute transformAttribute = (TransformAttribute)paramObject;
          if (transformAttribute.isIdentity()) {
            this.transform = null;
          } else {
            this.transform = transformAttribute.getTransform();
          } 
        } else {
          this.transform = new AffineTransform((AffineTransform)paramObject);
        } 
        updateDerivedTransforms();
        return;
      case 7:
        this.superscript = (byte)((Integer)paramObject).intValue();
        return;
      case 8:
        this.font = (Font)paramObject;
        return;
      case 9:
        this.charReplacement = (GraphicAttribute)paramObject;
        return;
      case 10:
        this.foreground = (Paint)paramObject;
        return;
      case 11:
        this.background = (Paint)paramObject;
        return;
      case 12:
        this.underline = (byte)((Integer)paramObject).intValue();
        return;
      case 13:
        this.strikethrough = ((Boolean)paramObject).booleanValue();
        return;
      case 14:
        if (paramObject instanceof Boolean) {
          this.runDirection = (byte)(TextAttribute.RUN_DIRECTION_LTR.equals(paramObject) ? 0 : 1);
        } else {
          this.runDirection = (byte)((Integer)paramObject).intValue();
        } 
        return;
      case 15:
        this.bidiEmbedding = (byte)((Integer)paramObject).intValue();
        return;
      case 16:
        this.justification = ((Number)paramObject).floatValue();
        return;
      case 17:
        if (paramObject instanceof Annotation) {
          Annotation annotation = (Annotation)paramObject;
          this.imHighlight = annotation.getValue();
        } else {
          this.imHighlight = paramObject;
        } 
        return;
      case 18:
        this.imUnderline = (byte)((Integer)paramObject).intValue();
        return;
      case 19:
        this.swapColors = ((Boolean)paramObject).booleanValue();
        return;
      case 20:
        this.numericShaping = (NumericShaper)paramObject;
        return;
      case 21:
        this.kerning = (byte)((Integer)paramObject).intValue();
        return;
      case 22:
        this.ligatures = (byte)((Integer)paramObject).intValue();
        return;
      case 23:
        this.tracking = ((Number)paramObject).floatValue();
        return;
    } 
    throw new InternalError();
  }
  
  private Object i_get(EAttribute paramEAttribute) {
    switch (null.$SwitchMap$sun$font$EAttribute[paramEAttribute.ordinal()]) {
      case 1:
        return this.family;
      case 2:
        return Float.valueOf(this.weight);
      case 3:
        return Float.valueOf(this.width);
      case 4:
        return Float.valueOf(this.posture);
      case 5:
        return Float.valueOf(this.size);
      case 6:
        return (this.transform == null) ? TransformAttribute.IDENTITY : new TransformAttribute(this.transform);
      case 7:
        return Integer.valueOf(this.superscript);
      case 8:
        return this.font;
      case 9:
        return this.charReplacement;
      case 10:
        return this.foreground;
      case 11:
        return this.background;
      case 12:
        return Integer.valueOf(this.underline);
      case 13:
        return Boolean.valueOf(this.strikethrough);
      case 14:
        switch (this.runDirection) {
          case 0:
            return TextAttribute.RUN_DIRECTION_LTR;
          case 1:
            return TextAttribute.RUN_DIRECTION_RTL;
        } 
        return null;
      case 15:
        return Integer.valueOf(this.bidiEmbedding);
      case 16:
        return Float.valueOf(this.justification);
      case 17:
        return this.imHighlight;
      case 18:
        return Integer.valueOf(this.imUnderline);
      case 19:
        return Boolean.valueOf(this.swapColors);
      case 20:
        return this.numericShaping;
      case 21:
        return Integer.valueOf(this.kerning);
      case 22:
        return Integer.valueOf(this.ligatures);
      case 23:
        return Float.valueOf(this.tracking);
    } 
    throw new InternalError();
  }
  
  private boolean i_validate(EAttribute paramEAttribute) {
    switch (null.$SwitchMap$sun$font$EAttribute[paramEAttribute.ordinal()]) {
      case 1:
        if (this.family == null || this.family.length() == 0)
          this.family = DEFAULT.family; 
        return true;
      case 2:
        return (this.weight > 0.0F && this.weight < 10.0F);
      case 3:
        return (this.width >= 0.5F && this.width < 10.0F);
      case 4:
        return (this.posture >= -1.0F && this.posture <= 1.0F);
      case 5:
        return (this.size >= 0.0F);
      case 6:
        if (this.transform != null && this.transform.isIdentity())
          this.transform = DEFAULT.transform; 
        return true;
      case 7:
        return (this.superscript >= -7 && this.superscript <= 7);
      case 8:
        return true;
      case 9:
        return true;
      case 10:
        return true;
      case 11:
        return true;
      case 12:
        return (this.underline >= -1 && this.underline < 6);
      case 13:
        return true;
      case 14:
        return (this.runDirection >= -2 && this.runDirection <= 1);
      case 15:
        return (this.bidiEmbedding >= -61 && this.bidiEmbedding < 62);
      case 16:
        this.justification = Math.max(0.0F, Math.min(this.justification, 1.0F));
        return true;
      case 17:
        return true;
      case 18:
        return (this.imUnderline >= -1 && this.imUnderline < 6);
      case 19:
        return true;
      case 20:
        return true;
      case 21:
        return (this.kerning >= 0 && this.kerning <= 1);
      case 22:
        return (this.ligatures >= 0 && this.ligatures <= 1);
      case 23:
        return (this.tracking >= -1.0F && this.tracking <= 10.0F);
    } 
    throw new InternalError("unknown attribute: " + paramEAttribute);
  }
  
  public static float getJustification(Map<?, ?> paramMap) {
    if (paramMap != null) {
      if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap)
        .getValues() != null)
        return (((AttributeMap)paramMap).getValues()).justification; 
      Object object = paramMap.get(TextAttribute.JUSTIFICATION);
      if (object != null && object instanceof Number)
        return Math.max(0.0F, Math.min(1.0F, ((Number)object).floatValue())); 
    } 
    return DEFAULT.justification;
  }
  
  public static NumericShaper getNumericShaping(Map<?, ?> paramMap) {
    if (paramMap != null) {
      if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap)
        .getValues() != null)
        return (((AttributeMap)paramMap).getValues()).numericShaping; 
      Object object = paramMap.get(TextAttribute.NUMERIC_SHAPING);
      if (object != null && object instanceof NumericShaper)
        return (NumericShaper)object; 
    } 
    return DEFAULT.numericShaping;
  }
  
  public AttributeValues applyIMHighlight() {
    if (this.imHighlight != null) {
      InputMethodHighlight inputMethodHighlight = null;
      if (this.imHighlight instanceof InputMethodHighlight) {
        inputMethodHighlight = (InputMethodHighlight)this.imHighlight;
      } else {
        inputMethodHighlight = (InputMethodHighlight)((Annotation)this.imHighlight).getValue();
      } 
      Map<TextAttribute, ?> map = inputMethodHighlight.getStyle();
      if (map == null) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        map = toolkit.mapInputMethodHighlight(inputMethodHighlight);
      } 
      if (map != null)
        return clone().merge((Map)map); 
    } 
    return this;
  }
  
  public static AffineTransform getBaselineTransform(Map<?, ?> paramMap) {
    if (paramMap != null) {
      AttributeValues attributeValues = null;
      if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap)
        .getValues() != null) {
        attributeValues = ((AttributeMap)paramMap).getValues();
      } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
        attributeValues = fromMap((Map)paramMap);
      } 
      if (attributeValues != null)
        return attributeValues.baselineTransform; 
    } 
    return null;
  }
  
  public static AffineTransform getCharTransform(Map<?, ?> paramMap) {
    if (paramMap != null) {
      AttributeValues attributeValues = null;
      if (paramMap instanceof AttributeMap && ((AttributeMap)paramMap)
        .getValues() != null) {
        attributeValues = ((AttributeMap)paramMap).getValues();
      } else if (paramMap.get(TextAttribute.TRANSFORM) != null) {
        attributeValues = fromMap((Map)paramMap);
      } 
      if (attributeValues != null)
        return attributeValues.charTransform; 
    } 
    return null;
  }
  
  public void updateDerivedTransforms() {
    if (this.transform == null) {
      this.baselineTransform = null;
      this.charTransform = null;
    } else {
      this.charTransform = new AffineTransform(this.transform);
      this.baselineTransform = extractXRotation(this.charTransform, true);
      if (this.charTransform.isIdentity())
        this.charTransform = null; 
      if (this.baselineTransform.isIdentity())
        this.baselineTransform = null; 
    } 
    if (this.baselineTransform == null) {
      this.nondefault &= EAttribute.EBASELINE_TRANSFORM.mask ^ 0xFFFFFFFF;
    } else {
      this.nondefault |= EAttribute.EBASELINE_TRANSFORM.mask;
    } 
  }
  
  public static AffineTransform extractXRotation(AffineTransform paramAffineTransform, boolean paramBoolean) {
    return extractRotation(new Point2D.Double(1.0D, 0.0D), paramAffineTransform, paramBoolean);
  }
  
  public static AffineTransform extractYRotation(AffineTransform paramAffineTransform, boolean paramBoolean) {
    return extractRotation(new Point2D.Double(0.0D, 1.0D), paramAffineTransform, paramBoolean);
  }
  
  private static AffineTransform extractRotation(Point2D.Double paramDouble, AffineTransform paramAffineTransform, boolean paramBoolean) {
    paramAffineTransform.deltaTransform(paramDouble, paramDouble);
    AffineTransform affineTransform = AffineTransform.getRotateInstance(paramDouble.x, paramDouble.y);
    try {
      AffineTransform affineTransform1 = affineTransform.createInverse();
      double d1 = paramAffineTransform.getTranslateX();
      double d2 = paramAffineTransform.getTranslateY();
      paramAffineTransform.preConcatenate(affineTransform1);
      if (paramBoolean && (
        d1 != 0.0D || d2 != 0.0D)) {
        paramAffineTransform.setTransform(paramAffineTransform.getScaleX(), paramAffineTransform.getShearY(), paramAffineTransform
            .getShearX(), paramAffineTransform.getScaleY(), 0.0D, 0.0D);
        affineTransform.setTransform(affineTransform.getScaleX(), affineTransform.getShearY(), affineTransform
            .getShearX(), affineTransform.getScaleY(), d1, d2);
      } 
    } catch (NoninvertibleTransformException noninvertibleTransformException) {
      return null;
    } 
    return affineTransform;
  }
}
