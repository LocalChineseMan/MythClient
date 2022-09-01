package java.awt.datatransfer;

import java.io.IOException;

public interface Transferable {
  DataFlavor[] getTransferDataFlavors();
  
  boolean isDataFlavorSupported(DataFlavor paramDataFlavor);
  
  Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException;
}
