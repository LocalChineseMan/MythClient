package notthatuwu.xyz.mythrecode.api.utils.animation;

public class Animation {
  private long animationStart;
  
  private double duration;
  
  private double animationFromValue;
  
  private double animationToValue;
  
  private Easing easing = Easings.NONE;
  
  private double lastValue;
  
  public double getValue() {
    return this.lastValue;
  }
  
  public void setValue(double value) {
    this.animationFromValue = value;
    this.animationToValue = value;
  }
  
  public void animate(double value, double duration, Easing easing) {
    this.animationFromValue = this.lastValue;
    this.animationToValue = value;
    this.animationStart = System.currentTimeMillis();
    this.duration = duration;
    this.easing = easing;
  }
  
  public boolean updateAnimation() {
    double value, part = (System.currentTimeMillis() - this.animationStart) / this.duration;
    if (isAlive()) {
      part = this.easing.ease(part);
      value = this.animationFromValue + (this.animationToValue - this.animationFromValue) * part;
    } else {
      this.animationStart = 0L;
      value = this.animationToValue;
    } 
    this.lastValue = value;
    return isAlive();
  }
  
  public boolean isDone() {
    double part = (System.currentTimeMillis() - this.animationStart) / this.duration;
    return (part >= 1.0D);
  }
  
  public boolean isAlive() {
    double part = (System.currentTimeMillis() - this.animationStart) / this.duration;
    return (part < 1.0D);
  }
}
