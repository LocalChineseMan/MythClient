package org.slf4j.impl;

import java.io.PrintStream;

class OutputChoice {
  final OutputChoiceType outputChoiceType;
  
  final PrintStream targetPrintStream;
  
  enum OutputChoiceType {
    SYS_OUT, CACHED_SYS_OUT, SYS_ERR, CACHED_SYS_ERR, FILE;
  }
  
  OutputChoice(OutputChoiceType outputChoiceType) {
    if (outputChoiceType == OutputChoiceType.FILE)
      throw new IllegalArgumentException(); 
    this.outputChoiceType = outputChoiceType;
    if (outputChoiceType == OutputChoiceType.CACHED_SYS_OUT) {
      this.targetPrintStream = System.out;
    } else if (outputChoiceType == OutputChoiceType.CACHED_SYS_ERR) {
      this.targetPrintStream = System.err;
    } else {
      this.targetPrintStream = null;
    } 
  }
  
  OutputChoice(PrintStream printStream) {
    this.outputChoiceType = OutputChoiceType.FILE;
    this.targetPrintStream = printStream;
  }
  
  PrintStream getTargetPrintStream() {
    switch (null.$SwitchMap$org$slf4j$impl$OutputChoice$OutputChoiceType[this.outputChoiceType.ordinal()]) {
      case 1:
        return System.out;
      case 2:
        return System.err;
      case 3:
      case 4:
      case 5:
        return this.targetPrintStream;
    } 
    throw new IllegalArgumentException();
  }
}
