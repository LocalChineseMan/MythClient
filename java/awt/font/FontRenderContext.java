package java.awt.font;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FontRenderContext {
  private transient AffineTransform tx;
  
  private transient Object aaHintValue;
  
  private transient Object fmHintValue;
  
  private transient boolean defaulting;
  
  protected FontRenderContext() {
    this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
    this.defaulting = true;
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramAffineTransform != null && !paramAffineTransform.isIdentity())
      this.tx = new AffineTransform(paramAffineTransform); 
    if (paramBoolean1) {
      this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    } else {
      this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    } 
    if (paramBoolean2) {
      this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
    } else {
      this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    } 
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2) {
    if (paramAffineTransform != null && !paramAffineTransform.isIdentity())
      this.tx = new AffineTransform(paramAffineTransform); 
    try {
      if (RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(paramObject1)) {
        this.aaHintValue = paramObject1;
      } else {
        throw new IllegalArgumentException("AA hint:" + paramObject1);
      } 
    } catch (Exception exception) {
      throw new IllegalArgumentException("AA hint:" + paramObject1);
    } 
    try {
      if (RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(paramObject2)) {
        this.fmHintValue = paramObject2;
      } else {
        throw new IllegalArgumentException("FM hint:" + paramObject2);
      } 
    } catch (Exception exception) {
      throw new IllegalArgumentException("FM hint:" + paramObject2);
    } 
  }
  
  public boolean isTransformed() {
    if (!this.defaulting)
      return (this.tx != null); 
    return !getTransform().isIdentity();
  }
  
  public int getTransformType() {
    if (!this.defaulting) {
      if (this.tx == null)
        return 0; 
      return this.tx.getType();
    } 
    return getTransform().getType();
  }
  
  public AffineTransform getTransform() {
    return (this.tx == null) ? new AffineTransform() : new AffineTransform(this.tx);
  }
  
  public boolean isAntiAliased() {
    return (this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
  }
  
  public boolean usesFractionalMetrics() {
    return (this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_OFF && this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
  }
  
  public Object getAntiAliasingHint() {
    if (this.defaulting) {
      if (isAntiAliased())
        return RenderingHints.VALUE_TEXT_ANTIALIAS_ON; 
      return RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    } 
    return this.aaHintValue;
  }
  
  public Object getFractionalMetricsHint() {
    if (this.defaulting) {
      if (usesFractionalMetrics())
        return RenderingHints.VALUE_FRACTIONALMETRICS_ON; 
      return RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    } 
    return this.fmHintValue;
  }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((FontRenderContext)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public boolean equals(FontRenderContext paramFontRenderContext) {
    if (this == paramFontRenderContext)
      return true; 
    if (paramFontRenderContext == null)
      return false; 
    if (!paramFontRenderContext.defaulting && !this.defaulting) {
      if (paramFontRenderContext.aaHintValue == this.aaHintValue && paramFontRenderContext.fmHintValue == this.fmHintValue)
        return (this.tx == null) ? ((paramFontRenderContext.tx == null)) : this.tx.equals(paramFontRenderContext.tx); 
      return false;
    } 
    return (paramFontRenderContext
      .getAntiAliasingHint() == getAntiAliasingHint() && paramFontRenderContext
      .getFractionalMetricsHint() == getFractionalMetricsHint() && paramFontRenderContext
      .getTransform().equals(getTransform()));
  }
  
  public int hashCode() {
    int i = (this.tx == null) ? 0 : this.tx.hashCode();
    if (this.defaulting) {
      i += getAntiAliasingHint().hashCode();
      i += getFractionalMetricsHint().hashCode();
    } else {
      i += this.aaHintValue.hashCode();
      i += this.fmHintValue.hashCode();
    } 
    return i;
  }
}
