package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import java.util.Hashtable;

public class DTDGrammarBucket {
  protected Hashtable fGrammars = new Hashtable<>();
  
  protected DTDGrammar fActiveGrammar;
  
  protected boolean fIsStandalone;
  
  public void putGrammar(DTDGrammar grammar) {
    XMLDTDDescription desc = (XMLDTDDescription)grammar.getGrammarDescription();
    this.fGrammars.put(desc, grammar);
  }
  
  public DTDGrammar getGrammar(XMLGrammarDescription desc) {
    return (DTDGrammar)this.fGrammars.get(desc);
  }
  
  public void clear() {
    this.fGrammars.clear();
    this.fActiveGrammar = null;
    this.fIsStandalone = false;
  }
  
  void setStandalone(boolean standalone) {
    this.fIsStandalone = standalone;
  }
  
  boolean getStandalone() {
    return this.fIsStandalone;
  }
  
  void setActiveGrammar(DTDGrammar grammar) {
    this.fActiveGrammar = grammar;
  }
  
  DTDGrammar getActiveGrammar() {
    return this.fActiveGrammar;
  }
}
