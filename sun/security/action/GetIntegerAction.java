package sun.security.action;

import java.security.PrivilegedAction;

public class GetIntegerAction implements PrivilegedAction<Integer> {
  private String theProp;
  
  private int defaultVal;
  
  private boolean defaultSet = false;
  
  public GetIntegerAction(String paramString) {
    this.theProp = paramString;
  }
  
  public GetIntegerAction(String paramString, int paramInt) {
    this.theProp = paramString;
    this.defaultVal = paramInt;
    this.defaultSet = true;
  }
  
  public Integer run() {
    Integer integer = Integer.getInteger(this.theProp);
    if (integer == null && this.defaultSet)
      return new Integer(this.defaultVal); 
    return integer;
  }
}
