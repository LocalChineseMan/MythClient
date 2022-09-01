package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventMove extends Event {
  private double x;
  
  private double y;
  
  private double z;
  
  private float yaw;
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public void setZ(double z) {
    this.z = z;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
  
  public double getZ() {
    return this.z;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public EventMove(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
