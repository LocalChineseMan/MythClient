package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDREFDatatypeValidator implements DatatypeValidator {
  public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
    if (context.useNamespaces()) {
      if (!XMLChar.isValidNCName(content))
        throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[] { content }); 
    } else if (!XMLChar.isValidName(content)) {
      throw new InvalidDatatypeValueException("IDREFInvalid", new Object[] { content });
    } 
    context.addIdRef(content);
  }
}
