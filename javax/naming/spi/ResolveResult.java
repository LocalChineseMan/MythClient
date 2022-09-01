package javax.naming.spi;

import java.io.Serializable;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public class ResolveResult implements Serializable {
  protected Object resolvedObj;
  
  protected Name remainingName;
  
  private static final long serialVersionUID = -4552108072002407559L;
  
  protected ResolveResult() {
    this.resolvedObj = null;
    this.remainingName = null;
  }
  
  public ResolveResult(Object paramObject, String paramString) {
    this.resolvedObj = paramObject;
    try {
      this.remainingName = new CompositeName(paramString);
    } catch (InvalidNameException invalidNameException) {}
  }
  
  public ResolveResult(Object paramObject, Name paramName) {
    this.resolvedObj = paramObject;
    setRemainingName(paramName);
  }
  
  public Name getRemainingName() {
    return this.remainingName;
  }
  
  public Object getResolvedObj() {
    return this.resolvedObj;
  }
  
  public void setRemainingName(Name paramName) {
    if (paramName != null) {
      this.remainingName = (Name)paramName.clone();
    } else {
      this.remainingName = null;
    } 
  }
  
  public void appendRemainingName(Name paramName) {
    if (paramName != null)
      if (this.remainingName != null) {
        try {
          this.remainingName.addAll(paramName);
        } catch (InvalidNameException invalidNameException) {}
      } else {
        this.remainingName = (Name)paramName.clone();
      }  
  }
  
  public void appendRemainingComponent(String paramString) {
    if (paramString != null) {
      CompositeName compositeName = new CompositeName();
      try {
        compositeName.add(paramString);
      } catch (InvalidNameException invalidNameException) {}
      appendRemainingName(compositeName);
    } 
  }
  
  public void setResolvedObj(Object paramObject) {
    this.resolvedObj = paramObject;
  }
}
