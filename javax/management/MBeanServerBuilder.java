package javax.management;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

public class MBeanServerBuilder {
  public MBeanServerDelegate newMBeanServerDelegate() {
    return JmxMBeanServer.newMBeanServerDelegate();
  }
  
  public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate) {
    return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, false);
  }
}
