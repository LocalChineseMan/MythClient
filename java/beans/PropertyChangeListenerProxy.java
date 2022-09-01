package java.beans;

import java.util.EventListenerProxy;

public class PropertyChangeListenerProxy extends EventListenerProxy<PropertyChangeListener> implements PropertyChangeListener {
  private final String propertyName;
  
  public PropertyChangeListenerProxy(String paramString, PropertyChangeListener paramPropertyChangeListener) {
    super(paramPropertyChangeListener);
    this.propertyName = paramString;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    getListener().propertyChange(paramPropertyChangeEvent);
  }
  
  public String getPropertyName() {
    return this.propertyName;
  }
}
