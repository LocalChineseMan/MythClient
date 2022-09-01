package java.awt.datatransfer;

import java.util.List;

public interface FlavorTable extends FlavorMap {
  List<String> getNativesForFlavor(DataFlavor paramDataFlavor);
  
  List<DataFlavor> getFlavorsForNative(String paramString);
}
