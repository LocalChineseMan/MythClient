package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class NMTOKENDatatypeValidator implements DatatypeValidator {
  public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
    if (!XMLChar.isValidNmtoken(content))
      throw new InvalidDatatypeValueException("NMTOKENInvalid", new Object[] { content }); 
  }
}
