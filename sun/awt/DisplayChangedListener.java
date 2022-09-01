package sun.awt;

import java.util.EventListener;

public interface DisplayChangedListener extends EventListener {
  void displayChanged();
  
  void paletteChanged();
}
