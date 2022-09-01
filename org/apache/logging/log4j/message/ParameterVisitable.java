package org.apache.logging.log4j.message;

import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public interface ParameterVisitable {
  <S> void forEachParameter(ParameterConsumer<S> paramParameterConsumer, S paramS);
}
