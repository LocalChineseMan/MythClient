package sun.awt;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.KeyboardFocusManager;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.ScrollPaneAdjustable;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.util.ResourceBundle;
import javax.accessibility.AccessibleContext;
import sun.misc.Unsafe;

public final class AWTAccessor {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static ComponentAccessor componentAccessor;
  
  private static ContainerAccessor containerAccessor;
  
  private static WindowAccessor windowAccessor;
  
  private static AWTEventAccessor awtEventAccessor;
  
  private static InputEventAccessor inputEventAccessor;
  
  private static FrameAccessor frameAccessor;
  
  private static KeyboardFocusManagerAccessor kfmAccessor;
  
  private static MenuComponentAccessor menuComponentAccessor;
  
  private static EventQueueAccessor eventQueueAccessor;
  
  private static PopupMenuAccessor popupMenuAccessor;
  
  private static FileDialogAccessor fileDialogAccessor;
  
  private static ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor;
  
  private static CheckboxMenuItemAccessor checkboxMenuItemAccessor;
  
  private static CursorAccessor cursorAccessor;
  
  private static MenuBarAccessor menuBarAccessor;
  
  private static MenuItemAccessor menuItemAccessor;
  
  private static MenuAccessor menuAccessor;
  
  private static KeyEventAccessor keyEventAccessor;
  
  private static ClientPropertyKeyAccessor clientPropertyKeyAccessor;
  
  private static SystemTrayAccessor systemTrayAccessor;
  
  private static TrayIconAccessor trayIconAccessor;
  
  private static DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor;
  
  private static SequencedEventAccessor sequencedEventAccessor;
  
  private static ToolkitAccessor toolkitAccessor;
  
  private static InvocationEventAccessor invocationEventAccessor;
  
  private static SystemColorAccessor systemColorAccessor;
  
  private static AccessibleContextAccessor accessibleContextAccessor;
  
  public static void setComponentAccessor(ComponentAccessor paramComponentAccessor) {
    componentAccessor = paramComponentAccessor;
  }
  
  public static ComponentAccessor getComponentAccessor() {
    if (componentAccessor == null)
      unsafe.ensureClassInitialized(Component.class); 
    return componentAccessor;
  }
  
  public static void setContainerAccessor(ContainerAccessor paramContainerAccessor) {
    containerAccessor = paramContainerAccessor;
  }
  
  public static ContainerAccessor getContainerAccessor() {
    if (containerAccessor == null)
      unsafe.ensureClassInitialized(Container.class); 
    return containerAccessor;
  }
  
  public static void setWindowAccessor(WindowAccessor paramWindowAccessor) {
    windowAccessor = paramWindowAccessor;
  }
  
  public static WindowAccessor getWindowAccessor() {
    if (windowAccessor == null)
      unsafe.ensureClassInitialized(Window.class); 
    return windowAccessor;
  }
  
  public static void setAWTEventAccessor(AWTEventAccessor paramAWTEventAccessor) {
    awtEventAccessor = paramAWTEventAccessor;
  }
  
  public static AWTEventAccessor getAWTEventAccessor() {
    if (awtEventAccessor == null)
      unsafe.ensureClassInitialized(AWTEvent.class); 
    return awtEventAccessor;
  }
  
  public static void setInputEventAccessor(InputEventAccessor paramInputEventAccessor) {
    inputEventAccessor = paramInputEventAccessor;
  }
  
  public static InputEventAccessor getInputEventAccessor() {
    if (inputEventAccessor == null)
      unsafe.ensureClassInitialized(InputEvent.class); 
    return inputEventAccessor;
  }
  
  public static void setFrameAccessor(FrameAccessor paramFrameAccessor) {
    frameAccessor = paramFrameAccessor;
  }
  
  public static FrameAccessor getFrameAccessor() {
    if (frameAccessor == null)
      unsafe.ensureClassInitialized(Frame.class); 
    return frameAccessor;
  }
  
  public static void setKeyboardFocusManagerAccessor(KeyboardFocusManagerAccessor paramKeyboardFocusManagerAccessor) {
    kfmAccessor = paramKeyboardFocusManagerAccessor;
  }
  
  public static KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor() {
    if (kfmAccessor == null)
      unsafe.ensureClassInitialized(KeyboardFocusManager.class); 
    return kfmAccessor;
  }
  
  public static void setMenuComponentAccessor(MenuComponentAccessor paramMenuComponentAccessor) {
    menuComponentAccessor = paramMenuComponentAccessor;
  }
  
  public static MenuComponentAccessor getMenuComponentAccessor() {
    if (menuComponentAccessor == null)
      unsafe.ensureClassInitialized(MenuComponent.class); 
    return menuComponentAccessor;
  }
  
  public static void setEventQueueAccessor(EventQueueAccessor paramEventQueueAccessor) {
    eventQueueAccessor = paramEventQueueAccessor;
  }
  
  public static EventQueueAccessor getEventQueueAccessor() {
    if (eventQueueAccessor == null)
      unsafe.ensureClassInitialized(EventQueue.class); 
    return eventQueueAccessor;
  }
  
  public static void setPopupMenuAccessor(PopupMenuAccessor paramPopupMenuAccessor) {
    popupMenuAccessor = paramPopupMenuAccessor;
  }
  
  public static PopupMenuAccessor getPopupMenuAccessor() {
    if (popupMenuAccessor == null)
      unsafe.ensureClassInitialized(PopupMenu.class); 
    return popupMenuAccessor;
  }
  
  public static void setFileDialogAccessor(FileDialogAccessor paramFileDialogAccessor) {
    fileDialogAccessor = paramFileDialogAccessor;
  }
  
  public static FileDialogAccessor getFileDialogAccessor() {
    if (fileDialogAccessor == null)
      unsafe.ensureClassInitialized(FileDialog.class); 
    return fileDialogAccessor;
  }
  
  public static void setScrollPaneAdjustableAccessor(ScrollPaneAdjustableAccessor paramScrollPaneAdjustableAccessor) {
    scrollPaneAdjustableAccessor = paramScrollPaneAdjustableAccessor;
  }
  
  public static ScrollPaneAdjustableAccessor getScrollPaneAdjustableAccessor() {
    if (scrollPaneAdjustableAccessor == null)
      unsafe.ensureClassInitialized(ScrollPaneAdjustable.class); 
    return scrollPaneAdjustableAccessor;
  }
  
  public static void setCheckboxMenuItemAccessor(CheckboxMenuItemAccessor paramCheckboxMenuItemAccessor) {
    checkboxMenuItemAccessor = paramCheckboxMenuItemAccessor;
  }
  
  public static CheckboxMenuItemAccessor getCheckboxMenuItemAccessor() {
    if (checkboxMenuItemAccessor == null)
      unsafe.ensureClassInitialized(CheckboxMenuItemAccessor.class); 
    return checkboxMenuItemAccessor;
  }
  
  public static void setCursorAccessor(CursorAccessor paramCursorAccessor) {
    cursorAccessor = paramCursorAccessor;
  }
  
  public static CursorAccessor getCursorAccessor() {
    if (cursorAccessor == null)
      unsafe.ensureClassInitialized(CursorAccessor.class); 
    return cursorAccessor;
  }
  
  public static void setMenuBarAccessor(MenuBarAccessor paramMenuBarAccessor) {
    menuBarAccessor = paramMenuBarAccessor;
  }
  
  public static MenuBarAccessor getMenuBarAccessor() {
    if (menuBarAccessor == null)
      unsafe.ensureClassInitialized(MenuBarAccessor.class); 
    return menuBarAccessor;
  }
  
  public static void setMenuItemAccessor(MenuItemAccessor paramMenuItemAccessor) {
    menuItemAccessor = paramMenuItemAccessor;
  }
  
  public static MenuItemAccessor getMenuItemAccessor() {
    if (menuItemAccessor == null)
      unsafe.ensureClassInitialized(MenuItemAccessor.class); 
    return menuItemAccessor;
  }
  
  public static void setMenuAccessor(MenuAccessor paramMenuAccessor) {
    menuAccessor = paramMenuAccessor;
  }
  
  public static MenuAccessor getMenuAccessor() {
    if (menuAccessor == null)
      unsafe.ensureClassInitialized(MenuAccessor.class); 
    return menuAccessor;
  }
  
  public static void setKeyEventAccessor(KeyEventAccessor paramKeyEventAccessor) {
    keyEventAccessor = paramKeyEventAccessor;
  }
  
  public static KeyEventAccessor getKeyEventAccessor() {
    if (keyEventAccessor == null)
      unsafe.ensureClassInitialized(KeyEventAccessor.class); 
    return keyEventAccessor;
  }
  
  public static void setClientPropertyKeyAccessor(ClientPropertyKeyAccessor paramClientPropertyKeyAccessor) {
    clientPropertyKeyAccessor = paramClientPropertyKeyAccessor;
  }
  
  public static ClientPropertyKeyAccessor getClientPropertyKeyAccessor() {
    if (clientPropertyKeyAccessor == null)
      unsafe.ensureClassInitialized(ClientPropertyKeyAccessor.class); 
    return clientPropertyKeyAccessor;
  }
  
  public static void setSystemTrayAccessor(SystemTrayAccessor paramSystemTrayAccessor) {
    systemTrayAccessor = paramSystemTrayAccessor;
  }
  
  public static SystemTrayAccessor getSystemTrayAccessor() {
    if (systemTrayAccessor == null)
      unsafe.ensureClassInitialized(SystemTrayAccessor.class); 
    return systemTrayAccessor;
  }
  
  public static void setTrayIconAccessor(TrayIconAccessor paramTrayIconAccessor) {
    trayIconAccessor = paramTrayIconAccessor;
  }
  
  public static TrayIconAccessor getTrayIconAccessor() {
    if (trayIconAccessor == null)
      unsafe.ensureClassInitialized(TrayIconAccessor.class); 
    return trayIconAccessor;
  }
  
  public static void setDefaultKeyboardFocusManagerAccessor(DefaultKeyboardFocusManagerAccessor paramDefaultKeyboardFocusManagerAccessor) {
    defaultKeyboardFocusManagerAccessor = paramDefaultKeyboardFocusManagerAccessor;
  }
  
  public static DefaultKeyboardFocusManagerAccessor getDefaultKeyboardFocusManagerAccessor() {
    if (defaultKeyboardFocusManagerAccessor == null)
      unsafe.ensureClassInitialized(DefaultKeyboardFocusManagerAccessor.class); 
    return defaultKeyboardFocusManagerAccessor;
  }
  
  public static void setSequencedEventAccessor(SequencedEventAccessor paramSequencedEventAccessor) {
    sequencedEventAccessor = paramSequencedEventAccessor;
  }
  
  public static SequencedEventAccessor getSequencedEventAccessor() {
    return sequencedEventAccessor;
  }
  
  public static void setToolkitAccessor(ToolkitAccessor paramToolkitAccessor) {
    toolkitAccessor = paramToolkitAccessor;
  }
  
  public static ToolkitAccessor getToolkitAccessor() {
    if (toolkitAccessor == null)
      unsafe.ensureClassInitialized(Toolkit.class); 
    return toolkitAccessor;
  }
  
  public static void setInvocationEventAccessor(InvocationEventAccessor paramInvocationEventAccessor) {
    invocationEventAccessor = paramInvocationEventAccessor;
  }
  
  public static InvocationEventAccessor getInvocationEventAccessor() {
    return invocationEventAccessor;
  }
  
  public static SystemColorAccessor getSystemColorAccessor() {
    if (systemColorAccessor == null)
      unsafe.ensureClassInitialized(SystemColor.class); 
    return systemColorAccessor;
  }
  
  public static void setSystemColorAccessor(SystemColorAccessor paramSystemColorAccessor) {
    systemColorAccessor = paramSystemColorAccessor;
  }
  
  public static AccessibleContextAccessor getAccessibleContextAccessor() {
    if (accessibleContextAccessor == null)
      unsafe.ensureClassInitialized(AccessibleContext.class); 
    return accessibleContextAccessor;
  }
  
  public static void setAccessibleContextAccessor(AccessibleContextAccessor paramAccessibleContextAccessor) {
    accessibleContextAccessor = paramAccessibleContextAccessor;
  }
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface ToolkitAccessor {
    void setPlatformResources(ResourceBundle param1ResourceBundle);
  }
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface EventQueueAccessor {
    Thread getDispatchThread(EventQueue param1EventQueue);
    
    boolean isDispatchThreadImpl(EventQueue param1EventQueue);
    
    void removeSourceEvents(EventQueue param1EventQueue, Object param1Object, boolean param1Boolean);
    
    boolean noEvents(EventQueue param1EventQueue);
    
    void wakeup(EventQueue param1EventQueue, boolean param1Boolean);
    
    void invokeAndWait(Object param1Object, Runnable param1Runnable) throws InterruptedException, InvocationTargetException;
    
    void setFwDispatcher(EventQueue param1EventQueue, FwDispatcher param1FwDispatcher);
    
    long getMostRecentEventTime(EventQueue param1EventQueue);
  }
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface InputEventAccessor {
    int[] getButtonDownMasks();
  }
  
  public static interface AWTEventAccessor {
    void setPosted(AWTEvent param1AWTEvent);
    
    void setSystemGenerated(AWTEvent param1AWTEvent);
    
    boolean isSystemGenerated(AWTEvent param1AWTEvent);
    
    AccessControlContext getAccessControlContext(AWTEvent param1AWTEvent);
    
    byte[] getBData(AWTEvent param1AWTEvent);
    
    void setBData(AWTEvent param1AWTEvent, byte[] param1ArrayOfbyte);
  }
  
  public static interface AWTAccessor {}
  
  public static interface AWTAccessor {}
  
  public static interface ComponentAccessor {
    void setBackgroundEraseDisabled(Component param1Component, boolean param1Boolean);
    
    boolean getBackgroundEraseDisabled(Component param1Component);
    
    Rectangle getBounds(Component param1Component);
    
    void setMixingCutoutShape(Component param1Component, Shape param1Shape);
    
    void setGraphicsConfiguration(Component param1Component, GraphicsConfiguration param1GraphicsConfiguration);
    
    boolean requestFocus(Component param1Component, CausedFocusEvent.Cause param1Cause);
    
    boolean canBeFocusOwner(Component param1Component);
    
    boolean isVisible(Component param1Component);
    
    void setRequestFocusController(RequestFocusController param1RequestFocusController);
    
    AppContext getAppContext(Component param1Component);
    
    void setAppContext(Component param1Component, AppContext param1AppContext);
    
    Container getParent(Component param1Component);
    
    void setParent(Component param1Component, Container param1Container);
    
    void setSize(Component param1Component, int param1Int1, int param1Int2);
    
    Point getLocation(Component param1Component);
    
    void setLocation(Component param1Component, int param1Int1, int param1Int2);
    
    boolean isEnabled(Component param1Component);
    
    boolean isDisplayable(Component param1Component);
    
    Cursor getCursor(Component param1Component);
    
    ComponentPeer getPeer(Component param1Component);
    
    void setPeer(Component param1Component, ComponentPeer param1ComponentPeer);
    
    boolean isLightweight(Component param1Component);
    
    boolean getIgnoreRepaint(Component param1Component);
    
    int getWidth(Component param1Component);
    
    int getHeight(Component param1Component);
    
    int getX(Component param1Component);
    
    int getY(Component param1Component);
    
    Color getForeground(Component param1Component);
    
    Color getBackground(Component param1Component);
    
    void setBackground(Component param1Component, Color param1Color);
    
    Font getFont(Component param1Component);
    
    void processEvent(Component param1Component, AWTEvent param1AWTEvent);
    
    AccessControlContext getAccessControlContext(Component param1Component);
    
    void revalidateSynchronously(Component param1Component);
  }
}
