package joptsimple;

import java.util.Collection;
import java.util.List;

public interface OptionSpec<V> {
  List<V> values(OptionSet paramOptionSet);
  
  V value(OptionSet paramOptionSet);
  
  Collection<String> options();
  
  boolean isForHelp();
}
