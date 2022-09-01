package org.apache.logging.log4j.core.net;

import java.util.Map;

public interface Advertiser {
  Object advertise(Map<String, String> paramMap);
  
  void unadvertise(Object paramObject);
}
