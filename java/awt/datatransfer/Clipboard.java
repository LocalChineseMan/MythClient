package java.awt.datatransfer;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import sun.awt.EventListenerAggregate;

public class Clipboard {
  String name;
  
  protected ClipboardOwner owner;
  
  protected Transferable contents;
  
  private EventListenerAggregate flavorListeners;
  
  private Set<DataFlavor> currentDataFlavors;
  
  public Clipboard(String paramString) {
    this.name = paramString;
  }
  
  public String getName() {
    return this.name;
  }
  
  public synchronized void setContents(Transferable paramTransferable, ClipboardOwner paramClipboardOwner) {
    ClipboardOwner clipboardOwner = this.owner;
    Transferable transferable = this.contents;
    this.owner = paramClipboardOwner;
    this.contents = paramTransferable;
    if (clipboardOwner != null && clipboardOwner != paramClipboardOwner)
      EventQueue.invokeLater((Runnable)new Object(this, clipboardOwner, transferable)); 
    fireFlavorsChanged();
  }
  
  public synchronized Transferable getContents(Object paramObject) {
    return this.contents;
  }
  
  public DataFlavor[] getAvailableDataFlavors() {
    Transferable transferable = getContents(null);
    if (transferable == null)
      return new DataFlavor[0]; 
    return transferable.getTransferDataFlavors();
  }
  
  public boolean isDataFlavorAvailable(DataFlavor paramDataFlavor) {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable = getContents(null);
    if (transferable == null)
      return false; 
    return transferable.isDataFlavorSupported(paramDataFlavor);
  }
  
  public Object getData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    if (paramDataFlavor == null)
      throw new NullPointerException("flavor"); 
    Transferable transferable = getContents(null);
    if (transferable == null)
      throw new UnsupportedFlavorException(paramDataFlavor); 
    return transferable.getTransferData(paramDataFlavor);
  }
  
  public synchronized void addFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null)
      return; 
    if (this.flavorListeners == null) {
      this.currentDataFlavors = getAvailableDataFlavorSet();
      this.flavorListeners = new EventListenerAggregate(FlavorListener.class);
    } 
    this.flavorListeners.add(paramFlavorListener);
  }
  
  public synchronized void removeFlavorListener(FlavorListener paramFlavorListener) {
    if (paramFlavorListener == null || this.flavorListeners == null)
      return; 
    this.flavorListeners.remove(paramFlavorListener);
  }
  
  public synchronized FlavorListener[] getFlavorListeners() {
    return (this.flavorListeners == null) ? new FlavorListener[0] : (FlavorListener[])this.flavorListeners
      .getListenersCopy();
  }
  
  private void fireFlavorsChanged() {
    if (this.flavorListeners == null)
      return; 
    Set<DataFlavor> set = this.currentDataFlavors;
    this.currentDataFlavors = getAvailableDataFlavorSet();
    if (set.equals(this.currentDataFlavors))
      return; 
    FlavorListener[] arrayOfFlavorListener = (FlavorListener[])this.flavorListeners.getListenersInternal();
    for (byte b = 0; b < arrayOfFlavorListener.length; b++) {
      FlavorListener flavorListener = arrayOfFlavorListener[b];
      EventQueue.invokeLater((Runnable)new Object(this, flavorListener));
    } 
  }
  
  private Set<DataFlavor> getAvailableDataFlavorSet() {
    HashSet<DataFlavor> hashSet = new HashSet();
    Transferable transferable = getContents(null);
    if (transferable != null) {
      DataFlavor[] arrayOfDataFlavor = transferable.getTransferDataFlavors();
      if (arrayOfDataFlavor != null)
        hashSet.addAll(Arrays.asList(arrayOfDataFlavor)); 
    } 
    return hashSet;
  }
}
