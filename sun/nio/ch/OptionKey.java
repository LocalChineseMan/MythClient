package sun.nio.ch;

class OptionKey {
  private int level;
  
  private int name;
  
  OptionKey(int paramInt1, int paramInt2) {
    this.level = paramInt1;
    this.name = paramInt2;
  }
  
  int level() {
    return this.level;
  }
  
  int name() {
    return this.name;
  }
}
