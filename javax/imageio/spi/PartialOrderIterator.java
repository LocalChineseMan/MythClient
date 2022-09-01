package javax.imageio.spi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

class PartialOrderIterator implements Iterator {
  LinkedList zeroList = new LinkedList();
  
  Map inDegrees = new HashMap<>();
  
  public PartialOrderIterator(Iterator<DigraphNode> paramIterator) {
    while (paramIterator.hasNext()) {
      DigraphNode digraphNode = paramIterator.next();
      int i = digraphNode.getInDegree();
      this.inDegrees.put(digraphNode, new Integer(i));
      if (i == 0)
        this.zeroList.add(digraphNode); 
    } 
  }
  
  public boolean hasNext() {
    return !this.zeroList.isEmpty();
  }
  
  public Object next() {
    DigraphNode digraphNode = this.zeroList.removeFirst();
    Iterator<DigraphNode> iterator = digraphNode.getOutNodes();
    while (iterator.hasNext()) {
      DigraphNode digraphNode1 = iterator.next();
      int i = ((Integer)this.inDegrees.get(digraphNode1)).intValue() - 1;
      this.inDegrees.put(digraphNode1, new Integer(i));
      if (i == 0)
        this.zeroList.add(digraphNode1); 
    } 
    return digraphNode.getData();
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
