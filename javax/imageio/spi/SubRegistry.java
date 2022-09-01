package javax.imageio.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class SubRegistry {
  ServiceRegistry registry;
  
  Class category;
  
  PartiallyOrderedSet poset = new PartiallyOrderedSet();
  
  Map<Class<?>, Object> map = new HashMap<>();
  
  public SubRegistry(ServiceRegistry paramServiceRegistry, Class paramClass) {
    this.registry = paramServiceRegistry;
    this.category = paramClass;
  }
  
  public boolean registerServiceProvider(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    boolean bool = (object != null) ? true : false;
    if (bool)
      deregisterServiceProvider(object); 
    this.map.put(paramObject.getClass(), paramObject);
    this.poset.add(paramObject);
    if (paramObject instanceof RegisterableService) {
      RegisterableService registerableService = (RegisterableService)paramObject;
      registerableService.onRegistration(this.registry, this.category);
    } 
    return !bool;
  }
  
  public boolean deregisterServiceProvider(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    if (paramObject == object) {
      this.map.remove(paramObject.getClass());
      this.poset.remove(paramObject);
      if (paramObject instanceof RegisterableService) {
        RegisterableService registerableService = (RegisterableService)paramObject;
        registerableService.onDeregistration(this.registry, this.category);
      } 
      return true;
    } 
    return false;
  }
  
  public boolean contains(Object paramObject) {
    Object object = this.map.get(paramObject.getClass());
    return (object == paramObject);
  }
  
  public boolean setOrdering(Object paramObject1, Object paramObject2) {
    return this.poset.setOrdering(paramObject1, paramObject2);
  }
  
  public boolean unsetOrdering(Object paramObject1, Object paramObject2) {
    return this.poset.unsetOrdering(paramObject1, paramObject2);
  }
  
  public Iterator getServiceProviders(boolean paramBoolean) {
    if (paramBoolean)
      return this.poset.iterator(); 
    return this.map.values().iterator();
  }
  
  public <T> T getServiceProviderByClass(Class<T> paramClass) {
    return (T)this.map.get(paramClass);
  }
  
  public void clear() {
    Iterator<Object> iterator = this.map.values().iterator();
    while (iterator.hasNext()) {
      RegisterableService registerableService = (RegisterableService)iterator.next();
      iterator.remove();
      if (registerableService instanceof RegisterableService) {
        RegisterableService registerableService1 = registerableService;
        registerableService1.onDeregistration(this.registry, this.category);
      } 
    } 
    this.poset.clear();
  }
  
  public void finalize() {
    clear();
  }
}
