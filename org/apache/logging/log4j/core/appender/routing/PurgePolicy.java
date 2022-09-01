package org.apache.logging.log4j.core.appender.routing;

import org.apache.logging.log4j.core.LogEvent;

public interface PurgePolicy {
  void purge();
  
  void update(String paramString, LogEvent paramLogEvent);
  
  void initialize(RoutingAppender paramRoutingAppender);
}
