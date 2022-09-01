package java.lang.management;

import java.lang.management.MemoryType;

public enum MemoryType {
  HEAP("Heap memory"),
  NON_HEAP("Non-heap memory");
  
  private final String description;
  
  private static final long serialVersionUID = 6992337162326171013L;
  
  MemoryType(String paramString1) {
    this.description = paramString1;
  }
  
  public String toString() {
    return this.description;
  }
}
