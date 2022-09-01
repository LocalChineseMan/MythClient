package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventKey extends Event {
  private int key;
  
  public void setKey(int key) {
    this.key = key;
  }
  
  public int getKey() {
    return this.key;
  }
  
  public EventKey(int key) {
    this.key = key;
  }
}
