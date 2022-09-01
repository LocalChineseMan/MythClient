package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.util.StringTokenizer;

public class ListDatatypeValidator implements DatatypeValidator {
  DatatypeValidator fItemValidator;
  
  public ListDatatypeValidator(DatatypeValidator itemDV) {
    this.fItemValidator = itemDV;
  }
  
  public void validate(String content, ValidationContext context) throws InvalidDatatypeValueException {
    StringTokenizer parsedList = new StringTokenizer(content, " ");
    int numberOfTokens = parsedList.countTokens();
    if (numberOfTokens == 0)
      throw new InvalidDatatypeValueException("EmptyList", null); 
    while (parsedList.hasMoreTokens())
      this.fItemValidator.validate(parsedList.nextToken(), context); 
  }
}
