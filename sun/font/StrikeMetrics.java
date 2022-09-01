package sun.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public final class StrikeMetrics {
  public float ascentX;
  
  public float ascentY;
  
  public float descentX;
  
  public float descentY;
  
  public float baselineX;
  
  public float baselineY;
  
  public float leadingX;
  
  public float leadingY;
  
  public float maxAdvanceX;
  
  public float maxAdvanceY;
  
  StrikeMetrics() {
    this.ascentX = this.ascentY = 2.14748365E9F;
    this.descentX = this.descentY = this.leadingX = this.leadingY = -2.14748365E9F;
    this.baselineX = this.maxAdvanceX = this.maxAdvanceY = -2.14748365E9F;
  }
  
  StrikeMetrics(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10) {
    this.ascentX = paramFloat1;
    this.ascentY = paramFloat2;
    this.descentX = paramFloat3;
    this.descentY = paramFloat4;
    this.baselineX = paramFloat5;
    this.baselineY = paramFloat6;
    this.leadingX = paramFloat7;
    this.leadingY = paramFloat8;
    this.maxAdvanceX = paramFloat9;
    this.maxAdvanceY = paramFloat10;
  }
  
  public float getAscent() {
    return -this.ascentY;
  }
  
  public float getDescent() {
    return this.descentY;
  }
  
  public float getLeading() {
    return this.leadingY;
  }
  
  public float getMaxAdvance() {
    return this.maxAdvanceX;
  }
  
  void merge(StrikeMetrics paramStrikeMetrics) {
    if (paramStrikeMetrics == null)
      return; 
    if (paramStrikeMetrics.ascentX < this.ascentX)
      this.ascentX = paramStrikeMetrics.ascentX; 
    if (paramStrikeMetrics.ascentY < this.ascentY)
      this.ascentY = paramStrikeMetrics.ascentY; 
    if (paramStrikeMetrics.descentX > this.descentX)
      this.descentX = paramStrikeMetrics.descentX; 
    if (paramStrikeMetrics.descentY > this.descentY)
      this.descentY = paramStrikeMetrics.descentY; 
    if (paramStrikeMetrics.baselineX > this.baselineX)
      this.baselineX = paramStrikeMetrics.baselineX; 
    if (paramStrikeMetrics.baselineY > this.baselineY)
      this.baselineY = paramStrikeMetrics.baselineY; 
    if (paramStrikeMetrics.leadingX > this.leadingX)
      this.leadingX = paramStrikeMetrics.leadingX; 
    if (paramStrikeMetrics.leadingY > this.leadingY)
      this.leadingY = paramStrikeMetrics.leadingY; 
    if (paramStrikeMetrics.maxAdvanceX > this.maxAdvanceX)
      this.maxAdvanceX = paramStrikeMetrics.maxAdvanceX; 
    if (paramStrikeMetrics.maxAdvanceY > this.maxAdvanceY)
      this.maxAdvanceY = paramStrikeMetrics.maxAdvanceY; 
  }
  
  void convertToUserSpace(AffineTransform paramAffineTransform) {
    Point2D.Float float_ = new Point2D.Float();
    float_.x = this.ascentX;
    float_.y = this.ascentY;
    paramAffineTransform.deltaTransform(float_, float_);
    this.ascentX = float_.x;
    this.ascentY = float_.y;
    float_.x = this.descentX;
    float_.y = this.descentY;
    paramAffineTransform.deltaTransform(float_, float_);
    this.descentX = float_.x;
    this.descentY = float_.y;
    float_.x = this.baselineX;
    float_.y = this.baselineY;
    paramAffineTransform.deltaTransform(float_, float_);
    this.baselineX = float_.x;
    this.baselineY = float_.y;
    float_.x = this.leadingX;
    float_.y = this.leadingY;
    paramAffineTransform.deltaTransform(float_, float_);
    this.leadingX = float_.x;
    this.leadingY = float_.y;
    float_.x = this.maxAdvanceX;
    float_.y = this.maxAdvanceY;
    paramAffineTransform.deltaTransform(float_, float_);
    this.maxAdvanceX = float_.x;
    this.maxAdvanceY = float_.y;
  }
  
  public String toString() {
    return "ascent:x=" + this.ascentX + " y=" + this.ascentY + " descent:x=" + this.descentX + " y=" + this.descentY + " baseline:x=" + this.baselineX + " y=" + this.baselineY + " leading:x=" + this.leadingX + " y=" + this.leadingY + " maxAdvance:x=" + this.maxAdvanceX + " y=" + this.maxAdvanceY;
  }
}
