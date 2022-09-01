package sun.nio.ch;

class AllocatedNativeObject extends NativeObject {
  AllocatedNativeObject(int paramInt, boolean paramBoolean) {
    super(paramInt, paramBoolean);
  }
  
  synchronized void free() {
    if (this.allocationAddress != 0L) {
      unsafe.freeMemory(this.allocationAddress);
      this.allocationAddress = 0L;
    } 
  }
}
