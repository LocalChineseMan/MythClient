package org.apache.logging.log4j.core.util;

public interface WatchEventService {
  void subscribe(WatchManager paramWatchManager);
  
  void unsubscribe(WatchManager paramWatchManager);
}
