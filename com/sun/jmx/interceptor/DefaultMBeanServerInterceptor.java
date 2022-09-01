package com.sun.jmx.interceptor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.DynamicMBean2;
import com.sun.jmx.mbeanserver.Introspector;
import com.sun.jmx.mbeanserver.MBeanInstantiator;
import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.ObjectInputStream;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistration;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MBeanTrustPermission;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.loading.ClassLoaderRepository;

public class DefaultMBeanServerInterceptor implements MBeanServerInterceptor {
  private final transient MBeanInstantiator instantiator;
  
  private transient MBeanServer server = null;
  
  private final transient MBeanServerDelegate delegate;
  
  private final transient Repository repository;
  
  private final transient WeakHashMap<ListenerWrapper, WeakReference<ListenerWrapper>> listenerWrappers = new WeakHashMap<>();
  
  private final String domain;
  
  private final Set<ObjectName> beingUnregistered;
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
    return createMBean(paramString, paramObjectName, (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
    return createMBean(paramString, paramObjectName1, paramObjectName2, (Object[])null, (String[])null);
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
    try {
      return createMBean(paramString, paramObjectName, null, true, paramArrayOfObject, paramArrayOfString);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw (IllegalArgumentException)EnvHelp.initCause(new IllegalArgumentException("Unexpected exception: " + instanceNotFoundException), instanceNotFoundException);
    } 
  }
  
  public ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
    return createMBean(paramString, paramObjectName1, paramObjectName2, false, paramArrayOfObject, paramArrayOfString);
  }
  
  private ObjectInstance createMBean(String paramString, ObjectName paramObjectName1, ObjectName paramObjectName2, boolean paramBoolean, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
    Class<?> clazz;
    if (paramString == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("The class name cannot be null");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred during MBean creation");
    } 
    if (paramObjectName1 != null) {
      if (paramObjectName1.isPattern()) {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Invalid name->" + paramObjectName1.toString());
        throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred during MBean creation");
      } 
      paramObjectName1 = nonDefaultDomain(paramObjectName1);
    } 
    checkMBeanPermission(paramString, (String)null, (ObjectName)null, "instantiate");
    checkMBeanPermission(paramString, (String)null, paramObjectName1, "registerMBean");
    if (paramBoolean) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
            .getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1); 
      clazz = this.instantiator.findClassWithDefaultLoaderRepository(paramString);
    } else if (paramObjectName2 == null) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
            .getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1 + ", Loader name = null"); 
      clazz = this.instantiator.findClass(paramString, this.server
          .getClass().getClassLoader());
    } else {
      paramObjectName2 = nonDefaultDomain(paramObjectName2);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
            .getName(), "createMBean", "ClassName = " + paramString + ", ObjectName = " + paramObjectName1 + ", Loader name = " + paramObjectName2); 
      clazz = this.instantiator.findClass(paramString, paramObjectName2);
    } 
    checkMBeanTrustPermission(clazz);
    Introspector.testCreation(clazz);
    Introspector.checkCompliance(clazz);
    Object object = this.instantiator.instantiate(clazz, paramArrayOfObject, paramArrayOfString, this.server
        .getClass().getClassLoader());
    String str = getNewMBeanClassName(object);
    return registerObject(str, object, paramObjectName1);
  }
  
  public ObjectInstance registerMBean(Object paramObject, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
    Class<?> clazz = paramObject.getClass();
    Introspector.checkCompliance(clazz);
    String str = getNewMBeanClassName(paramObject);
    checkMBeanPermission(str, (String)null, paramObjectName, "registerMBean");
    checkMBeanTrustPermission(clazz);
    return registerObject(str, paramObject, paramObjectName);
  }
  
  private static String getNewMBeanClassName(Object paramObject) throws NotCompliantMBeanException {
    if (paramObject instanceof DynamicMBean) {
      String str;
      DynamicMBean dynamicMBean = (DynamicMBean)paramObject;
      try {
        str = dynamicMBean.getMBeanInfo().getClassName();
      } catch (Exception exception) {
        NotCompliantMBeanException notCompliantMBeanException = new NotCompliantMBeanException("Bad getMBeanInfo()");
        notCompliantMBeanException.initCause(exception);
        throw notCompliantMBeanException;
      } 
      if (str == null)
        throw new NotCompliantMBeanException("MBeanInfo has null class name"); 
      return str;
    } 
    return paramObject.getClass().getName();
  }
  
  public DefaultMBeanServerInterceptor(MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate, MBeanInstantiator paramMBeanInstantiator, Repository paramRepository) {
    this.beingUnregistered = new HashSet<>();
    if (paramMBeanServer == null)
      throw new IllegalArgumentException("outer MBeanServer cannot be null"); 
    if (paramMBeanServerDelegate == null)
      throw new IllegalArgumentException("MBeanServerDelegate cannot be null"); 
    if (paramMBeanInstantiator == null)
      throw new IllegalArgumentException("MBeanInstantiator cannot be null"); 
    if (paramRepository == null)
      throw new IllegalArgumentException("Repository cannot be null"); 
    this.server = paramMBeanServer;
    this.delegate = paramMBeanServerDelegate;
    this.instantiator = paramMBeanInstantiator;
    this.repository = paramRepository;
    this.domain = paramRepository.getDefaultDomain();
  }
  
  public void unregisterMBean(ObjectName paramObjectName) throws InstanceNotFoundException, MBeanRegistrationException {
    if (paramObjectName == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Object name cannot be null");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to unregister the MBean");
    } 
    paramObjectName = nonDefaultDomain(paramObjectName);
    synchronized (this.beingUnregistered) {
      while (this.beingUnregistered.contains(paramObjectName)) {
        try {
          this.beingUnregistered.wait();
        } catch (InterruptedException interruptedException) {
          throw new MBeanRegistrationException(interruptedException, interruptedException.toString());
        } 
      } 
      this.beingUnregistered.add(paramObjectName);
    } 
    try {
      exclusiveUnregisterMBean(paramObjectName);
    } finally {
      synchronized (this.beingUnregistered) {
        this.beingUnregistered.remove(paramObjectName);
        this.beingUnregistered.notifyAll();
      } 
    } 
  }
  
  private void exclusiveUnregisterMBean(ObjectName paramObjectName) throws InstanceNotFoundException, MBeanRegistrationException {
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "unregisterMBean");
    if (dynamicMBean instanceof MBeanRegistration)
      preDeregisterInvoke((MBeanRegistration)dynamicMBean); 
    Object object = getResource(dynamicMBean);
    ResourceContext resourceContext = unregisterFromRepository(object, dynamicMBean, paramObjectName);
    try {
      if (dynamicMBean instanceof MBeanRegistration)
        postDeregisterInvoke(paramObjectName, (MBeanRegistration)dynamicMBean); 
    } finally {
      resourceContext.done();
    } 
  }
  
  public ObjectInstance getObjectInstance(ObjectName paramObjectName) throws InstanceNotFoundException {
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "getObjectInstance");
    String str = getClassName(dynamicMBean);
    return new ObjectInstance(paramObjectName, str);
  }
  
  public Set<ObjectInstance> queryMBeans(ObjectName paramObjectName, QueryExp paramQueryExp) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      checkMBeanPermission((String)null, (String)null, (ObjectName)null, "queryMBeans");
      Set<ObjectInstance> set = queryMBeansImpl(paramObjectName, null);
      HashSet<ObjectInstance> hashSet = new HashSet(set.size());
      for (ObjectInstance objectInstance : set) {
        try {
          checkMBeanPermission(objectInstance.getClassName(), (String)null, objectInstance
              .getObjectName(), "queryMBeans");
          hashSet.add(objectInstance);
        } catch (SecurityException securityException) {}
      } 
      return filterListOfObjectInstances(hashSet, paramQueryExp);
    } 
    return queryMBeansImpl(paramObjectName, paramQueryExp);
  }
  
  private Set<ObjectInstance> queryMBeansImpl(ObjectName paramObjectName, QueryExp paramQueryExp) {
    Set<NamedObject> set = this.repository.query(paramObjectName, paramQueryExp);
    return objectInstancesFromFilteredNamedObjects(set, paramQueryExp);
  }
  
  public Set<ObjectName> queryNames(ObjectName paramObjectName, QueryExp paramQueryExp) {
    Set<ObjectName> set;
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      checkMBeanPermission((String)null, (String)null, (ObjectName)null, "queryNames");
      Set<ObjectInstance> set1 = queryMBeansImpl(paramObjectName, null);
      HashSet<ObjectInstance> hashSet = new HashSet(set1.size());
      for (ObjectInstance objectInstance : set1) {
        try {
          checkMBeanPermission(objectInstance.getClassName(), (String)null, objectInstance
              .getObjectName(), "queryNames");
          hashSet.add(objectInstance);
        } catch (SecurityException securityException) {}
      } 
      Set<ObjectInstance> set2 = filterListOfObjectInstances(hashSet, paramQueryExp);
      set = new HashSet(set2.size());
      for (ObjectInstance objectInstance : set2)
        set.add(objectInstance.getObjectName()); 
    } else {
      set = queryNamesImpl(paramObjectName, paramQueryExp);
    } 
    return set;
  }
  
  private Set<ObjectName> queryNamesImpl(ObjectName paramObjectName, QueryExp paramQueryExp) {
    Set<NamedObject> set = this.repository.query(paramObjectName, paramQueryExp);
    return objectNamesFromFilteredNamedObjects(set, paramQueryExp);
  }
  
  public boolean isRegistered(ObjectName paramObjectName) {
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Object name cannot be null"); 
    paramObjectName = nonDefaultDomain(paramObjectName);
    return this.repository.contains(paramObjectName);
  }
  
  public String[] getDomains() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      checkMBeanPermission((String)null, (String)null, (ObjectName)null, "getDomains");
      String[] arrayOfString = this.repository.getDomains();
      ArrayList<String> arrayList = new ArrayList(arrayOfString.length);
      for (byte b = 0; b < arrayOfString.length; b++) {
        try {
          ObjectName objectName = Util.newObjectName(arrayOfString[b] + ":x=x");
          checkMBeanPermission((String)null, (String)null, objectName, "getDomains");
          arrayList.add(arrayOfString[b]);
        } catch (SecurityException securityException) {}
      } 
      return arrayList.<String>toArray(new String[arrayList.size()]);
    } 
    return this.repository.getDomains();
  }
  
  public Integer getMBeanCount() {
    return this.repository.getCount();
  }
  
  public Object getAttribute(ObjectName paramObjectName, String paramString) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to invoke the getter on the MBean"); 
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the getter on the MBean"); 
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "getAttribute", "Attribute = " + paramString + ", ObjectName = " + paramObjectName); 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, paramString, paramObjectName, "getAttribute");
    try {
      return dynamicMBean.getAttribute(paramString);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (Throwable throwable) {
      rethrowMaybeMBeanException(throwable);
      throw new AssertionError();
    } 
  }
  
  public AttributeList getAttributes(ObjectName paramObjectName, String[] paramArrayOfString) throws InstanceNotFoundException, ReflectionException {
    String[] arrayOfString;
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the getter on the MBean"); 
    if (paramArrayOfString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attributes cannot be null"), "Exception occurred trying to invoke the getter on the MBean"); 
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "getAttributes", "ObjectName = " + paramObjectName); 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      arrayOfString = paramArrayOfString;
    } else {
      String str = getClassName(dynamicMBean);
      checkMBeanPermission(str, (String)null, paramObjectName, "getAttribute");
      ArrayList<String> arrayList = new ArrayList(paramArrayOfString.length);
      for (String str1 : paramArrayOfString) {
        try {
          checkMBeanPermission(str, str1, paramObjectName, "getAttribute");
          arrayList.add(str1);
        } catch (SecurityException securityException) {}
      } 
      arrayOfString = arrayList.<String>toArray(new String[arrayList.size()]);
    } 
    try {
      return dynamicMBean.getAttributes(arrayOfString);
    } catch (Throwable throwable) {
      rethrow(throwable);
      throw new AssertionError();
    } 
  }
  
  public void setAttribute(ObjectName paramObjectName, Attribute paramAttribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean"); 
    if (paramAttribute == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the setter on the MBean"); 
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "setAttribute", "ObjectName = " + paramObjectName + ", Attribute = " + paramAttribute
          
          .getName()); 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, paramAttribute.getName(), paramObjectName, "setAttribute");
    try {
      dynamicMBean.setAttribute(paramAttribute);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (InvalidAttributeValueException invalidAttributeValueException) {
      throw invalidAttributeValueException;
    } catch (Throwable throwable) {
      rethrowMaybeMBeanException(throwable);
      throw new AssertionError();
    } 
  }
  
  public AttributeList setAttributes(ObjectName paramObjectName, AttributeList paramAttributeList) throws InstanceNotFoundException, ReflectionException {
    AttributeList attributeList;
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean"); 
    if (paramAttributeList == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList  cannot be null"), "Exception occurred trying to invoke the setter on the MBean"); 
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null) {
      attributeList = paramAttributeList;
    } else {
      String str = getClassName(dynamicMBean);
      checkMBeanPermission(str, (String)null, paramObjectName, "setAttribute");
      attributeList = new AttributeList(paramAttributeList.size());
      for (Attribute attribute : paramAttributeList.asList()) {
        try {
          checkMBeanPermission(str, attribute.getName(), paramObjectName, "setAttribute");
          attributeList.add(attribute);
        } catch (SecurityException securityException) {}
      } 
    } 
    try {
      return dynamicMBean.setAttributes(attributeList);
    } catch (Throwable throwable) {
      rethrow(throwable);
      throw new AssertionError();
    } 
  }
  
  public Object invoke(ObjectName paramObjectName, String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws InstanceNotFoundException, MBeanException, ReflectionException {
    paramObjectName = nonDefaultDomain(paramObjectName);
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, paramString, paramObjectName, "invoke");
    try {
      return dynamicMBean.invoke(paramString, paramArrayOfObject, paramArrayOfString);
    } catch (Throwable throwable) {
      rethrowMaybeMBeanException(throwable);
      throw new AssertionError();
    } 
  }
  
  private static void rethrow(Throwable paramThrowable) throws ReflectionException {
    try {
      throw paramThrowable;
    } catch (ReflectionException reflectionException) {
      throw reflectionException;
    } catch (RuntimeOperationsException runtimeOperationsException) {
      throw runtimeOperationsException;
    } catch (RuntimeErrorException runtimeErrorException) {
      throw runtimeErrorException;
    } catch (RuntimeException runtimeException) {
      throw new RuntimeMBeanException(runtimeException, runtimeException.toString());
    } catch (Error error) {
      throw new RuntimeErrorException(error, error.toString());
    } catch (Throwable throwable) {
      throw new RuntimeException("Unexpected exception", throwable);
    } 
  }
  
  private static void rethrowMaybeMBeanException(Throwable paramThrowable) throws ReflectionException, MBeanException {
    if (paramThrowable instanceof MBeanException)
      throw (MBeanException)paramThrowable; 
    rethrow(paramThrowable);
  }
  
  private ObjectInstance registerObject(String paramString, Object paramObject, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
    if (paramObject == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Cannot add null object");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to register the MBean");
    } 
    DynamicMBean dynamicMBean = Introspector.makeDynamicMBean(paramObject);
    return registerDynamicMBean(paramString, dynamicMBean, paramObjectName);
  }
  
  private ObjectInstance registerDynamicMBean(String paramString, DynamicMBean paramDynamicMBean, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
    paramObjectName = nonDefaultDomain(paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "registerMBean", "ObjectName = " + paramObjectName); 
    ObjectName objectName = preRegister(paramDynamicMBean, this.server, paramObjectName);
    boolean bool1 = false;
    boolean bool2 = false;
    ResourceContext resourceContext = null;
    try {
      if (paramDynamicMBean instanceof DynamicMBean2)
        try {
          ((DynamicMBean2)paramDynamicMBean).preRegister2(this.server, objectName);
          bool2 = true;
        } catch (Exception exception) {
          if (exception instanceof RuntimeException)
            throw (RuntimeException)exception; 
          if (exception instanceof InstanceAlreadyExistsException)
            throw (InstanceAlreadyExistsException)exception; 
          throw new RuntimeException(exception);
        }  
      if (objectName != paramObjectName && objectName != null)
        objectName = ObjectName.getInstance(nonDefaultDomain(objectName)); 
      checkMBeanPermission(paramString, (String)null, objectName, "registerMBean");
      if (objectName == null) {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException("No object name specified");
        throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to register the MBean");
      } 
      Object object = getResource(paramDynamicMBean);
      resourceContext = registerWithRepository(object, paramDynamicMBean, objectName);
      bool2 = false;
      bool1 = true;
    } finally {
      try {
        postRegister(objectName, paramDynamicMBean, bool1, bool2);
      } finally {
        if (bool1 && resourceContext != null)
          resourceContext.done(); 
      } 
    } 
    return new ObjectInstance(objectName, paramString);
  }
  
  private static void throwMBeanRegistrationException(Throwable paramThrowable, String paramString) throws MBeanRegistrationException {
    if (paramThrowable instanceof RuntimeException)
      throw new RuntimeMBeanException((RuntimeException)paramThrowable, "RuntimeException thrown " + paramString); 
    if (paramThrowable instanceof Error)
      throw new RuntimeErrorException((Error)paramThrowable, "Error thrown " + paramString); 
    if (paramThrowable instanceof MBeanRegistrationException)
      throw (MBeanRegistrationException)paramThrowable; 
    if (paramThrowable instanceof Exception)
      throw new MBeanRegistrationException((Exception)paramThrowable, "Exception thrown " + paramString); 
    throw new RuntimeException(paramThrowable);
  }
  
  private static ObjectName preRegister(DynamicMBean paramDynamicMBean, MBeanServer paramMBeanServer, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException {
    ObjectName objectName = null;
    try {
      if (paramDynamicMBean instanceof MBeanRegistration)
        objectName = ((MBeanRegistration)paramDynamicMBean).preRegister(paramMBeanServer, paramObjectName); 
    } catch (Throwable throwable) {
      throwMBeanRegistrationException(throwable, "in preRegister method");
    } 
    if (objectName != null)
      return objectName; 
    return paramObjectName;
  }
  
  private static void postRegister(ObjectName paramObjectName, DynamicMBean paramDynamicMBean, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean2 && paramDynamicMBean instanceof DynamicMBean2)
      ((DynamicMBean2)paramDynamicMBean).registerFailed(); 
    try {
      if (paramDynamicMBean instanceof MBeanRegistration)
        ((MBeanRegistration)paramDynamicMBean).postRegister(Boolean.valueOf(paramBoolean1)); 
    } catch (RuntimeException runtimeException) {
      JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + paramObjectName + "]: " + "Exception thrown by postRegister: " + "rethrowing <" + runtimeException + ">, but keeping the MBean registered");
      throw new RuntimeMBeanException(runtimeException, "RuntimeException thrown in postRegister method: rethrowing <" + runtimeException + ">, but keeping the MBean registered");
    } catch (Error error) {
      JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + paramObjectName + "]: " + "Error thrown by postRegister: " + "rethrowing <" + error + ">, but keeping the MBean registered");
      throw new RuntimeErrorException(error, "Error thrown in postRegister method: rethrowing <" + error + ">, but keeping the MBean registered");
    } 
  }
  
  private static void preDeregisterInvoke(MBeanRegistration paramMBeanRegistration) throws MBeanRegistrationException {
    try {
      paramMBeanRegistration.preDeregister();
    } catch (Throwable throwable) {
      throwMBeanRegistrationException(throwable, "in preDeregister method");
    } 
  }
  
  private static void postDeregisterInvoke(ObjectName paramObjectName, MBeanRegistration paramMBeanRegistration) {
    try {
      paramMBeanRegistration.postDeregister();
    } catch (RuntimeException runtimeException) {
      JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + paramObjectName + "]: " + "Exception thrown by postDeregister: " + "rethrowing <" + runtimeException + ">, although the MBean is succesfully " + "unregistered");
      throw new RuntimeMBeanException(runtimeException, "RuntimeException thrown in postDeregister method: rethrowing <" + runtimeException + ">, although the MBean is sucessfully unregistered");
    } catch (Error error) {
      JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + paramObjectName + "]: " + "Error thrown by postDeregister: " + "rethrowing <" + error + ">, although the MBean is succesfully " + "unregistered");
      throw new RuntimeErrorException(error, "Error thrown in postDeregister method: rethrowing <" + error + ">, although the MBean is sucessfully unregistered");
    } 
  }
  
  private DynamicMBean getMBean(ObjectName paramObjectName) throws InstanceNotFoundException {
    if (paramObjectName == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to get an MBean"); 
    DynamicMBean dynamicMBean = this.repository.retrieve(paramObjectName);
    if (dynamicMBean == null) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
            .getName(), "getMBean", paramObjectName + " : Found no object"); 
      throw new InstanceNotFoundException(paramObjectName.toString());
    } 
    return dynamicMBean;
  }
  
  private static Object getResource(DynamicMBean paramDynamicMBean) {
    if (paramDynamicMBean instanceof DynamicMBean2)
      return ((DynamicMBean2)paramDynamicMBean).getResource(); 
    return paramDynamicMBean;
  }
  
  private ObjectName nonDefaultDomain(ObjectName paramObjectName) {
    if (paramObjectName == null || paramObjectName.getDomain().length() > 0)
      return paramObjectName; 
    String str = this.domain + paramObjectName;
    return Util.newObjectName(str);
  }
  
  public String getDefaultDomain() {
    return this.domain;
  }
  
  public void addNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "addNotificationListener", "ObjectName = " + paramObjectName); 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "addNotificationListener");
    NotificationBroadcaster notificationBroadcaster = (NotificationBroadcaster)getNotificationBroadcaster(paramObjectName, dynamicMBean, (Class)NotificationBroadcaster.class);
    if (paramNotificationListener == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Null listener"), "Null listener"); 
    NotificationListener notificationListener = getListenerWrapper(paramNotificationListener, paramObjectName, dynamicMBean, true);
    notificationBroadcaster.addNotificationListener(notificationListener, paramNotificationFilter, paramObject);
  }
  
  public void addNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException {
    DynamicMBean dynamicMBean = getMBean(paramObjectName2);
    Object object = getResource(dynamicMBean);
    if (!(object instanceof NotificationListener))
      throw new RuntimeOperationsException(new IllegalArgumentException(paramObjectName2
            .getCanonicalName()), "The MBean " + paramObjectName2
          .getCanonicalName() + "does not implement the NotificationListener interface"); 
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "addNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2); 
    this.server.addNotificationListener(paramObjectName1, (NotificationListener)object, paramNotificationFilter, paramObject);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener) throws InstanceNotFoundException, ListenerNotFoundException {
    removeNotificationListener(paramObjectName, paramNotificationListener, null, null, true);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, ListenerNotFoundException {
    removeNotificationListener(paramObjectName, paramNotificationListener, paramNotificationFilter, paramObject, false);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2) throws InstanceNotFoundException, ListenerNotFoundException {
    NotificationListener notificationListener = getListener(paramObjectName2);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "removeNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2); 
    this.server.removeNotificationListener(paramObjectName1, notificationListener);
  }
  
  public void removeNotificationListener(ObjectName paramObjectName1, ObjectName paramObjectName2, NotificationFilter paramNotificationFilter, Object paramObject) throws InstanceNotFoundException, ListenerNotFoundException {
    NotificationListener notificationListener = getListener(paramObjectName2);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "removeNotificationListener", "ObjectName = " + paramObjectName1 + ", Listener = " + paramObjectName2); 
    this.server.removeNotificationListener(paramObjectName1, notificationListener, paramNotificationFilter, paramObject);
  }
  
  private NotificationListener getListener(ObjectName paramObjectName) throws ListenerNotFoundException {
    DynamicMBean dynamicMBean;
    try {
      dynamicMBean = getMBean(paramObjectName);
    } catch (InstanceNotFoundException instanceNotFoundException) {
      throw (ListenerNotFoundException)EnvHelp.initCause(new ListenerNotFoundException(instanceNotFoundException
            .getMessage()), instanceNotFoundException);
    } 
    Object object = getResource(dynamicMBean);
    if (!(object instanceof NotificationListener)) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException(paramObjectName.getCanonicalName());
      String str = "MBean " + paramObjectName.getCanonicalName() + " does not " + "implement " + NotificationListener.class.getName();
      throw new RuntimeOperationsException(illegalArgumentException, str);
    } 
    return (NotificationListener)object;
  }
  
  private void removeNotificationListener(ObjectName paramObjectName, NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject, boolean paramBoolean) throws InstanceNotFoundException, ListenerNotFoundException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "removeNotificationListener", "ObjectName = " + paramObjectName); 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "removeNotificationListener");
    Class clazz = (Class)(paramBoolean ? NotificationBroadcaster.class : NotificationEmitter.class);
    NotificationBroadcaster notificationBroadcaster = (NotificationBroadcaster)getNotificationBroadcaster(paramObjectName, dynamicMBean, clazz);
    NotificationListener notificationListener = getListenerWrapper(paramNotificationListener, paramObjectName, dynamicMBean, false);
    if (notificationListener == null)
      throw new ListenerNotFoundException("Unknown listener"); 
    if (paramBoolean) {
      notificationBroadcaster.removeNotificationListener(notificationListener);
    } else {
      NotificationEmitter notificationEmitter = (NotificationEmitter)notificationBroadcaster;
      notificationEmitter.removeNotificationListener(notificationListener, paramNotificationFilter, paramObject);
    } 
  }
  
  private static <T extends NotificationBroadcaster> T getNotificationBroadcaster(ObjectName paramObjectName, Object paramObject, Class<T> paramClass) {
    if (paramClass.isInstance(paramObject))
      return paramClass.cast(paramObject); 
    if (paramObject instanceof DynamicMBean2)
      paramObject = ((DynamicMBean2)paramObject).getResource(); 
    if (paramClass.isInstance(paramObject))
      return paramClass.cast(paramObject); 
    IllegalArgumentException illegalArgumentException = new IllegalArgumentException(paramObjectName.getCanonicalName());
    String str = "MBean " + paramObjectName.getCanonicalName() + " does not " + "implement " + paramClass.getName();
    throw new RuntimeOperationsException(illegalArgumentException, str);
  }
  
  public MBeanInfo getMBeanInfo(ObjectName paramObjectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
    MBeanInfo mBeanInfo;
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    try {
      mBeanInfo = dynamicMBean.getMBeanInfo();
    } catch (RuntimeMBeanException runtimeMBeanException) {
      throw runtimeMBeanException;
    } catch (RuntimeErrorException runtimeErrorException) {
      throw runtimeErrorException;
    } catch (RuntimeException runtimeException) {
      throw new RuntimeMBeanException(runtimeException, "getMBeanInfo threw RuntimeException");
    } catch (Error error) {
      throw new RuntimeErrorException(error, "getMBeanInfo threw Error");
    } 
    if (mBeanInfo == null)
      throw new JMRuntimeException("MBean " + paramObjectName + "has no MBeanInfo"); 
    checkMBeanPermission(mBeanInfo.getClassName(), (String)null, paramObjectName, "getMBeanInfo");
    return mBeanInfo;
  }
  
  public boolean isInstanceOf(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException {
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "isInstanceOf");
    try {
      Object object = getResource(dynamicMBean);
      String str = (object instanceof DynamicMBean) ? getClassName((DynamicMBean)object) : object.getClass().getName();
      if (str.equals(paramString))
        return true; 
      ClassLoader classLoader = object.getClass().getClassLoader();
      Class<?> clazz1 = Class.forName(paramString, false, classLoader);
      if (clazz1.isInstance(object))
        return true; 
      Class<?> clazz2 = Class.forName(str, false, classLoader);
      return clazz1.isAssignableFrom(clazz2);
    } catch (Exception exception) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class
            .getName(), "isInstanceOf", "Exception calling isInstanceOf", exception); 
      return false;
    } 
  }
  
  public ClassLoader getClassLoaderFor(ObjectName paramObjectName) throws InstanceNotFoundException {
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "getClassLoaderFor");
    return getResource(dynamicMBean).getClass().getClassLoader();
  }
  
  public ClassLoader getClassLoader(ObjectName paramObjectName) throws InstanceNotFoundException {
    if (paramObjectName == null) {
      checkMBeanPermission((String)null, (String)null, (ObjectName)null, "getClassLoader");
      return this.server.getClass().getClassLoader();
    } 
    DynamicMBean dynamicMBean = getMBean(paramObjectName);
    checkMBeanPermission(dynamicMBean, (String)null, paramObjectName, "getClassLoader");
    Object object = getResource(dynamicMBean);
    if (!(object instanceof ClassLoader))
      throw new InstanceNotFoundException(paramObjectName.toString() + " is not a classloader"); 
    return (ClassLoader)object;
  }
  
  private void sendNotification(String paramString, ObjectName paramObjectName) {
    MBeanServerNotification mBeanServerNotification = new MBeanServerNotification(paramString, MBeanServerDelegate.DELEGATE_NAME, 0L, paramObjectName);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "sendNotification", paramString + " " + paramObjectName); 
    this.delegate.sendNotification(mBeanServerNotification);
  }
  
  private Set<ObjectName> objectNamesFromFilteredNamedObjects(Set<NamedObject> paramSet, QueryExp paramQueryExp) {
    HashSet<ObjectName> hashSet = new HashSet();
    if (paramQueryExp == null) {
      for (NamedObject namedObject : paramSet)
        hashSet.add(namedObject.getName()); 
    } else {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(this.server);
      try {
        for (NamedObject namedObject : paramSet) {
          boolean bool;
          try {
            bool = paramQueryExp.apply(namedObject.getName());
          } catch (Exception exception) {
            bool = false;
          } 
          if (bool)
            hashSet.add(namedObject.getName()); 
        } 
      } finally {
        paramQueryExp.setMBeanServer(mBeanServer);
      } 
    } 
    return hashSet;
  }
  
  private Set<ObjectInstance> objectInstancesFromFilteredNamedObjects(Set<NamedObject> paramSet, QueryExp paramQueryExp) {
    HashSet<ObjectInstance> hashSet = new HashSet();
    if (paramQueryExp == null) {
      for (NamedObject namedObject : paramSet) {
        DynamicMBean dynamicMBean = namedObject.getObject();
        String str = safeGetClassName(dynamicMBean);
        hashSet.add(new ObjectInstance(namedObject.getName(), str));
      } 
    } else {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(this.server);
      try {
        for (NamedObject namedObject : paramSet) {
          boolean bool;
          DynamicMBean dynamicMBean = namedObject.getObject();
          try {
            bool = paramQueryExp.apply(namedObject.getName());
          } catch (Exception exception) {
            bool = false;
          } 
          if (bool) {
            String str = safeGetClassName(dynamicMBean);
            hashSet.add(new ObjectInstance(namedObject.getName(), str));
          } 
        } 
      } finally {
        paramQueryExp.setMBeanServer(mBeanServer);
      } 
    } 
    return hashSet;
  }
  
  private static String safeGetClassName(DynamicMBean paramDynamicMBean) {
    try {
      return getClassName(paramDynamicMBean);
    } catch (Exception exception) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class
            .getName(), "safeGetClassName", "Exception getting MBean class name", exception); 
      return null;
    } 
  }
  
  private Set<ObjectInstance> filterListOfObjectInstances(Set<ObjectInstance> paramSet, QueryExp paramQueryExp) {
    if (paramQueryExp == null)
      return paramSet; 
    HashSet<ObjectInstance> hashSet = new HashSet();
    for (ObjectInstance objectInstance : paramSet) {
      boolean bool = false;
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      paramQueryExp.setMBeanServer(this.server);
      try {
        bool = paramQueryExp.apply(objectInstance.getObjectName());
      } catch (Exception exception) {
        bool = false;
      } finally {
        paramQueryExp.setMBeanServer(mBeanServer);
      } 
      if (bool)
        hashSet.add(objectInstance); 
    } 
    return hashSet;
  }
  
  private NotificationListener getListenerWrapper(NotificationListener paramNotificationListener, ObjectName paramObjectName, DynamicMBean paramDynamicMBean, boolean paramBoolean) {
    Object object = getResource(paramDynamicMBean);
    ListenerWrapper listenerWrapper = new ListenerWrapper(paramNotificationListener, paramObjectName, object);
    synchronized (this.listenerWrappers) {
      WeakReference<NotificationListener> weakReference = (WeakReference)this.listenerWrappers.get(listenerWrapper);
      if (weakReference != null) {
        NotificationListener notificationListener = weakReference.get();
        if (notificationListener != null)
          return notificationListener; 
      } 
      if (paramBoolean) {
        weakReference = new WeakReference<>(listenerWrapper);
        this.listenerWrappers.put(listenerWrapper, weakReference);
        return listenerWrapper;
      } 
      return null;
    } 
  }
  
  public Object instantiate(String paramString) throws ReflectionException, MBeanException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public Object instantiate(String paramString, ObjectName paramObjectName, Object[] paramArrayOfObject, String[] paramArrayOfString) throws ReflectionException, MBeanException, InstanceNotFoundException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(ObjectName paramObjectName, byte[] paramArrayOfbyte) throws InstanceNotFoundException, OperationsException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(String paramString, byte[] paramArrayOfbyte) throws OperationsException, ReflectionException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ObjectInputStream deserialize(String paramString, ObjectName paramObjectName, byte[] paramArrayOfbyte) throws InstanceNotFoundException, OperationsException, ReflectionException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ClassLoaderRepository getClassLoaderRepository() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  private static String getClassName(DynamicMBean paramDynamicMBean) {
    if (paramDynamicMBean instanceof DynamicMBean2)
      return ((DynamicMBean2)paramDynamicMBean).getClassName(); 
    return paramDynamicMBean.getMBeanInfo().getClassName();
  }
  
  private static void checkMBeanPermission(DynamicMBean paramDynamicMBean, String paramString1, ObjectName paramObjectName, String paramString2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkMBeanPermission(safeGetClassName(paramDynamicMBean), paramString1, paramObjectName, paramString2); 
  }
  
  private static void checkMBeanPermission(String paramString1, String paramString2, ObjectName paramObjectName, String paramString3) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanPermission mBeanPermission = new MBeanPermission(paramString1, paramString2, paramObjectName, paramString3);
      securityManager.checkPermission(mBeanPermission);
    } 
  }
  
  private static void checkMBeanTrustPermission(Class<?> paramClass) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      MBeanTrustPermission mBeanTrustPermission = new MBeanTrustPermission("register");
      Object object = new Object(paramClass);
      ProtectionDomain protectionDomain = AccessController.<ProtectionDomain>doPrivileged((PrivilegedAction<ProtectionDomain>)object);
      AccessControlContext accessControlContext = new AccessControlContext(new ProtectionDomain[] { protectionDomain });
      securityManager.checkPermission(mBeanTrustPermission, accessControlContext);
    } 
  }
  
  private static interface ResourceContext extends Repository.RegistrationContext {
    public static final ResourceContext NONE = new ResourceContext() {
        public void done() {}
        
        public void registering() {}
        
        public void unregistered() {}
      };
    
    void done();
  }
  
  private ResourceContext registerWithRepository(Object paramObject, DynamicMBean paramDynamicMBean, ObjectName paramObjectName) throws InstanceAlreadyExistsException, MBeanRegistrationException {
    ResourceContext resourceContext = makeResourceContextFor(paramObject, paramObjectName);
    this.repository.addMBean(paramDynamicMBean, paramObjectName, resourceContext);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "addObject", "Send create notification of object " + paramObjectName
          
          .getCanonicalName()); 
    sendNotification("JMX.mbean.registered", paramObjectName);
    return resourceContext;
  }
  
  private ResourceContext unregisterFromRepository(Object paramObject, DynamicMBean paramDynamicMBean, ObjectName paramObjectName) throws InstanceNotFoundException {
    ResourceContext resourceContext = makeResourceContextFor(paramObject, paramObjectName);
    this.repository.remove(paramObjectName, resourceContext);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class
          .getName(), "unregisterMBean", "Send delete notification of object " + paramObjectName
          
          .getCanonicalName()); 
    sendNotification("JMX.mbean.unregistered", paramObjectName);
    return resourceContext;
  }
  
  private void addClassLoader(ClassLoader paramClassLoader, ObjectName paramObjectName) {
    ModifiableClassLoaderRepository modifiableClassLoaderRepository = getInstantiatorCLR();
    if (modifiableClassLoaderRepository == null) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Dynamic addition of class loaders is not supported");
      throw new RuntimeOperationsException(illegalArgumentException, "Exception occurred trying to register the MBean as a class loader");
    } 
    modifiableClassLoaderRepository.addClassLoader(paramObjectName, paramClassLoader);
  }
  
  private void removeClassLoader(ClassLoader paramClassLoader, ObjectName paramObjectName) {
    if (paramClassLoader != this.server.getClass().getClassLoader()) {
      ModifiableClassLoaderRepository modifiableClassLoaderRepository = getInstantiatorCLR();
      if (modifiableClassLoaderRepository != null)
        modifiableClassLoaderRepository.removeClassLoader(paramObjectName); 
    } 
  }
  
  private ResourceContext createClassLoaderContext(ClassLoader paramClassLoader, ObjectName paramObjectName) {
    return (ResourceContext)new Object(this, paramClassLoader, paramObjectName);
  }
  
  private ResourceContext makeResourceContextFor(Object paramObject, ObjectName paramObjectName) {
    if (paramObject instanceof ClassLoader)
      return createClassLoaderContext((ClassLoader)paramObject, paramObjectName); 
    return ResourceContext.NONE;
  }
  
  private ModifiableClassLoaderRepository getInstantiatorCLR() {
    return AccessController.<ModifiableClassLoaderRepository>doPrivileged((PrivilegedAction<ModifiableClassLoaderRepository>)new Object(this));
  }
  
  private static class DefaultMBeanServerInterceptor {}
}
