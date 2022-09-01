package org.apache.logging.log4j.core.time;

import org.apache.logging.log4j.core.util.Clock;

public interface PreciseClock extends Clock {
  void init(MutableInstant paramMutableInstant);
}
