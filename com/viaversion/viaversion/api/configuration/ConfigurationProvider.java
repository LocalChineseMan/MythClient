package com.viaversion.viaversion.api.configuration;

import java.util.Map;

public interface ConfigurationProvider {
  void set(String paramString, Object paramObject);
  
  void saveConfig();
  
  void reloadConfig();
  
  Map<String, Object> getValues();
}
