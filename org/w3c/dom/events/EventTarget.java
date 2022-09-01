package org.w3c.dom.events;

public interface EventTarget {
  void addEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);
  
  void removeEventListener(String paramString, EventListener paramEventListener, boolean paramBoolean);
  
  boolean dispatchEvent(Event paramEvent) throws EventException;
}
