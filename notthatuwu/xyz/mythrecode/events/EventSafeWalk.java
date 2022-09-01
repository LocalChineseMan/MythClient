package notthatuwu.xyz.mythrecode.events;

import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventSafeWalk extends Event {
  private boolean safe;
  
  public void setSafe(boolean safe) {
    this.safe = safe;
  }
  
  public boolean isSafe() {
    return this.safe;
  }
  
  public EventSafeWalk(boolean safe) {
    this.safe = safe;
  }
}
