package sun.awt;

import java.awt.Component;

public interface RequestFocusController {
  boolean acceptRequestFocus(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, CausedFocusEvent.Cause paramCause);
}
