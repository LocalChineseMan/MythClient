package com.viaversion.viaversion.libs.kyori.adventure.util;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

public final class Services {
  private static final boolean SERVICE_LOAD_FAILURES_ARE_FATAL = Boolean.parseBoolean(System.getProperty(String.join(".", new CharSequence[] { "net", "kyori", "adventure", "serviceLoadFailuresAreFatal" }), String.valueOf(true)));
  
  @NotNull
  public static <P> Optional<P> service(@NotNull Class<P> type) {
    ServiceLoader<P> loader = Services0.loader(type);
    Iterator<P> it = loader.iterator();
    while (it.hasNext()) {
      P instance;
      try {
        instance = it.next();
      } catch (Throwable t) {
        if (SERVICE_LOAD_FAILURES_ARE_FATAL)
          throw new IllegalStateException("Encountered an exception loading service " + type, t); 
        continue;
      } 
      if (it.hasNext())
        throw new IllegalStateException("Expected to find one service " + type + ", found multiple"); 
      return Optional.of(instance);
    } 
    return Optional.empty();
  }
}
