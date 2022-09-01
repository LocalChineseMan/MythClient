package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SimpleLoggerFactory implements ILoggerFactory {
  ConcurrentMap<String, Logger> loggerMap;
  
  public SimpleLoggerFactory() {
    this.loggerMap = new ConcurrentHashMap<String, Logger>();
    SimpleLogger.lazyInit();
  }
  
  public Logger getLogger(String name) {
    Logger simpleLogger = this.loggerMap.get(name);
    if (simpleLogger != null)
      return simpleLogger; 
    SimpleLogger simpleLogger1 = new SimpleLogger(name);
    Logger oldInstance = (Logger)this.loggerMap.putIfAbsent(name, simpleLogger1);
    return (oldInstance == null) ? (Logger)simpleLogger1 : oldInstance;
  }
  
  void reset() {
    this.loggerMap.clear();
  }
}
