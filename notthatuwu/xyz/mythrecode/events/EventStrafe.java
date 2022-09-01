package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventStrafe extends Event {
  private float strafe;
  
  private float forward;
  
  private float friction;
  
  private float yaw;
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public void setFriction(float friction) {
    this.friction = friction;
  }
  
  public void setForward(float forward) {
    this.forward = forward;
  }
  
  public void setStrafe(float strafe) {
    this.strafe = strafe;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public float getFriction() {
    return this.friction;
  }
  
  public float getForward() {
    return this.forward;
  }
  
  public float getStrafe() {
    return this.strafe;
  }
  
  public EventStrafe(float yaw, float strafe, float forward, float friction) {
    this.yaw = yaw;
    this.strafe = strafe;
    this.forward = forward;
    this.friction = friction;
  }
}
