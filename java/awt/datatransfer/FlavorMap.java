package java.awt.datatransfer;

import java.util.Map;

public interface FlavorMap {
  Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] paramArrayOfDataFlavor);
  
  Map<String, DataFlavor> getFlavorsForNatives(String[] paramArrayOfString);
}
