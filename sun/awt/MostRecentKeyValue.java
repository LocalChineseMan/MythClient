package sun.awt;

final class MostRecentKeyValue {
  Object key;
  
  Object value;
  
  MostRecentKeyValue(Object paramObject1, Object paramObject2) {
    this.key = paramObject1;
    this.value = paramObject2;
  }
  
  void setPair(Object paramObject1, Object paramObject2) {
    this.key = paramObject1;
    this.value = paramObject2;
  }
}
