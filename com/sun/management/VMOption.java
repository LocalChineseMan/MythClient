package com.sun.management;

import javax.management.openmbean.CompositeData;
import jdk.Exported;
import sun.management.VMOptionCompositeData;

@Exported
public class VMOption {
  private String name;
  
  private String value;
  
  private boolean writeable;
  
  private Origin origin;
  
  @Exported
  public enum Origin {
    DEFAULT, VM_CREATION, ENVIRON_VAR, CONFIG_FILE, MANAGEMENT, ERGONOMIC, OTHER;
  }
  
  public VMOption(String paramString1, String paramString2, boolean paramBoolean, Origin paramOrigin) {
    this.name = paramString1;
    this.value = paramString2;
    this.writeable = paramBoolean;
    this.origin = paramOrigin;
  }
  
  private VMOption(CompositeData paramCompositeData) {
    VMOptionCompositeData.validateCompositeData(paramCompositeData);
    this.name = VMOptionCompositeData.getName(paramCompositeData);
    this.value = VMOptionCompositeData.getValue(paramCompositeData);
    this.writeable = VMOptionCompositeData.isWriteable(paramCompositeData);
    this.origin = VMOptionCompositeData.getOrigin(paramCompositeData);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public Origin getOrigin() {
    return this.origin;
  }
  
  public boolean isWriteable() {
    return this.writeable;
  }
  
  public String toString() {
    return "VM option: " + getName() + " value: " + this.value + " " + " origin: " + this.origin + " " + (this.writeable ? "(read-write)" : "(read-only)");
  }
  
  public static VMOption from(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      return null; 
    if (paramCompositeData instanceof VMOptionCompositeData)
      return ((VMOptionCompositeData)paramCompositeData).getVMOption(); 
    return new VMOption(paramCompositeData);
  }
}
