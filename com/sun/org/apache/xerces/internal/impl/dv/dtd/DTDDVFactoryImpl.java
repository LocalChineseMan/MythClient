package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DTDDVFactory;
import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import java.util.Hashtable;

public class DTDDVFactoryImpl extends DTDDVFactory {
  static Hashtable fBuiltInTypes = new Hashtable<>();
  
  static {
    createBuiltInTypes();
  }
  
  public DatatypeValidator getBuiltInDV(String name) {
    return (DatatypeValidator)fBuiltInTypes.get(name);
  }
  
  public Hashtable getBuiltInTypes() {
    return (Hashtable)fBuiltInTypes.clone();
  }
  
  static void createBuiltInTypes() {
    fBuiltInTypes.put("string", new StringDatatypeValidator());
    fBuiltInTypes.put("ID", new IDDatatypeValidator());
    DatatypeValidator dvTemp = new IDREFDatatypeValidator();
    fBuiltInTypes.put("IDREF", dvTemp);
    fBuiltInTypes.put("IDREFS", new ListDatatypeValidator(dvTemp));
    dvTemp = new ENTITYDatatypeValidator();
    fBuiltInTypes.put("ENTITY", new ENTITYDatatypeValidator());
    fBuiltInTypes.put("ENTITIES", new ListDatatypeValidator(dvTemp));
    fBuiltInTypes.put("NOTATION", new NOTATIONDatatypeValidator());
    dvTemp = new NMTOKENDatatypeValidator();
    fBuiltInTypes.put("NMTOKEN", dvTemp);
    fBuiltInTypes.put("NMTOKENS", new ListDatatypeValidator(dvTemp));
  }
}
