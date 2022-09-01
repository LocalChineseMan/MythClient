package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XMLChar;

public class IDDatatypeValidator implements DatatypeValidator {
  public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
    if (context.useNamespaces()) {
      if (!XMLChar.isValidNCName(content))
        throw new InvalidDatatypeValueException("IDInvalidWithNamespaces", new Object[] { content }); 
    } else if (!XMLChar.isValidName(content)) {
      throw new InvalidDatatypeValueException("IDInvalid", new Object[] { content });
    } 
    if (context.isIdDeclared(content))
      throw new InvalidDatatypeValueException("IDNotUnique", new Object[] { content }); 
    context.addId(content);
  }
}
