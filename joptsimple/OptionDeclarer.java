package joptsimple;

import java.util.Collection;

public interface OptionDeclarer {
  OptionSpecBuilder accepts(String paramString);
  
  OptionSpecBuilder accepts(String paramString1, String paramString2);
  
  OptionSpecBuilder acceptsAll(Collection<String> paramCollection);
  
  OptionSpecBuilder acceptsAll(Collection<String> paramCollection, String paramString);
  
  NonOptionArgumentSpec<String> nonOptions();
  
  NonOptionArgumentSpec<String> nonOptions(String paramString);
  
  void posixlyCorrect(boolean paramBoolean);
  
  void allowsUnrecognizedOptions();
  
  void recognizeAlternativeLongOptions(boolean paramBoolean);
}
