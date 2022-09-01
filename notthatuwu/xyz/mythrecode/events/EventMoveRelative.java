package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventMoveRelative extends Event {
  private float strafe;
  
  private float forward;
  
  private float friction;
  
  private float yaw;
  
  public void setStrafe(float strafe) {
    this.strafe = strafe;
  }
  
  public void setForward(float forward) {
    this.forward = forward;
  }
  
  public void setFriction(float friction) {
    this.friction = friction;
  }
  
  public void setYaw(float yaw) {
    this.yaw = yaw;
  }
  
  public float getStrafe() {
    return this.strafe;
  }
  
  public float getForward() {
    return this.forward;
  }
  
  public float getFriction() {
    return this.friction;
  }
  
  public float getYaw() {
    return this.yaw;
  }
  
  public EventMoveRelative(float strafe, float forward, float friction, float yaw) {
    this.strafe = strafe;
    this.forward = forward;
    this.friction = friction;
    this.yaw = yaw;
  }
}
