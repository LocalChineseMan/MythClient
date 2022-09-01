package javax.management;

public class StandardEmitterMBean extends StandardMBean implements NotificationEmitter {
  private static final MBeanNotificationInfo[] NO_NOTIFICATION_INFO = new MBeanNotificationInfo[0];
  
  private final NotificationEmitter emitter;
  
  private final MBeanNotificationInfo[] notificationInfo;
  
  public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, NotificationEmitter paramNotificationEmitter) {
    this(paramT, paramClass, false, paramNotificationEmitter);
  }
  
  public <T> StandardEmitterMBean(T paramT, Class<T> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter) {
    super(paramT, paramClass, paramBoolean);
    if (paramNotificationEmitter == null)
      throw new IllegalArgumentException("Null emitter"); 
    this.emitter = paramNotificationEmitter;
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramNotificationEmitter.getNotificationInfo();
    if (arrayOfMBeanNotificationInfo == null || arrayOfMBeanNotificationInfo.length == 0) {
      this.notificationInfo = NO_NOTIFICATION_INFO;
    } else {
      this.notificationInfo = (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
    } 
  }
  
  protected StandardEmitterMBean(Class<?> paramClass, NotificationEmitter paramNotificationEmitter) {
    this(paramClass, false, paramNotificationEmitter);
  }
  
  protected StandardEmitterMBean(Class<?> paramClass, boolean paramBoolean, NotificationEmitter paramNotificationEmitter) {
    super(paramClass, paramBoolean);
    if (paramNotificationEmitter == null)
      throw new IllegalArgumentException("Null emitter"); 
    this.emitter = paramNotificationEmitter;
    MBeanNotificationInfo[] arrayOfMBeanNotificationInfo = paramNotificationEmitter.getNotificationInfo();
    if (arrayOfMBeanNotificationInfo == null || arrayOfMBeanNotificationInfo.length == 0) {
      this.notificationInfo = NO_NOTIFICATION_INFO;
    } else {
      this.notificationInfo = (MBeanNotificationInfo[])arrayOfMBeanNotificationInfo.clone();
    } 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    this.emitter.removeNotificationListener(paramNotificationListener);
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws ListenerNotFoundException {
    this.emitter.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    this.emitter.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (this.notificationInfo == null)
      return NO_NOTIFICATION_INFO; 
    if (this.notificationInfo.length == 0)
      return this.notificationInfo; 
    return (MBeanNotificationInfo[])this.notificationInfo.clone();
  }
  
  public void sendNotification(Notification paramNotification) {
    if (this.emitter instanceof NotificationBroadcasterSupport) {
      ((NotificationBroadcasterSupport)this.emitter).sendNotification(paramNotification);
    } else {
      String str = "Cannot sendNotification when emitter is not an instance of NotificationBroadcasterSupport: " + this.emitter.getClass().getName();
      throw new ClassCastException(str);
    } 
  }
  
  MBeanNotificationInfo[] getNotifications(MBeanInfo paramMBeanInfo) {
    return getNotificationInfo();
  }
}
