package java.awt;

public interface MenuContainer {
  Font getFont();
  
  void remove(MenuComponent paramMenuComponent);
  
  @Deprecated
  boolean postEvent(Event paramEvent);
}
