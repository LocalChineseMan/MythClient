package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.Hashtable;

public abstract class DTDDVFactory {
  private static final String DEFAULT_FACTORY_CLASS = "com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl";
  
  public static final DTDDVFactory getInstance() throws DVFactoryException {
    return getInstance("com.sun.org.apache.xerces.internal.impl.dv.dtd.DTDDVFactoryImpl");
  }
  
  public static final DTDDVFactory getInstance(String factoryClass) throws DVFactoryException {
    try {
      return (DTDDVFactory)ObjectFactory.newInstance(factoryClass, true);
    } catch (ClassCastException e) {
      throw new DVFactoryException("DTD factory class " + factoryClass + " does not extend from DTDDVFactory.");
    } 
  }
  
  public abstract DatatypeValidator getBuiltInDV(String paramString);
  
  public abstract Hashtable getBuiltInTypes();
}
