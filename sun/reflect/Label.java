package sun.reflect;

import java.util.ArrayList;
import java.util.List;

class Label {
  static class PatchInfo {
    final ClassFileAssembler asm;
    
    final short instrBCI;
    
    final short patchBCI;
    
    final int stackDepth;
    
    PatchInfo(ClassFileAssembler param1ClassFileAssembler, short param1Short1, short param1Short2, int param1Int) {
      this.asm = param1ClassFileAssembler;
      this.instrBCI = param1Short1;
      this.patchBCI = param1Short2;
      this.stackDepth = param1Int;
    }
  }
  
  private List<PatchInfo> patches = new ArrayList<>();
  
  void add(ClassFileAssembler paramClassFileAssembler, short paramShort1, short paramShort2, int paramInt) {
    this.patches.add(new PatchInfo(paramClassFileAssembler, paramShort1, paramShort2, paramInt));
  }
  
  public void bind() {
    for (PatchInfo patchInfo : this.patches) {
      short s1 = patchInfo.asm.getLength();
      short s2 = (short)(s1 - patchInfo.instrBCI);
      patchInfo.asm.emitShort(patchInfo.patchBCI, s2);
      patchInfo.asm.setStack(patchInfo.stackDepth);
    } 
  }
}
