package org.apache.logging.log4j.core;

public class Version {
  public static void main(String[] args) {
    System.out.println(getProductString());
  }
  
  public static String getProductString() {
    Package pkg = Version.class.getPackage();
    if (pkg == null)
      return "Apache Log4j"; 
    return String.format("%s %s", new Object[] { pkg.getSpecificationTitle(), pkg.getSpecificationVersion() });
  }
}
