package java.text;

public class FieldPosition {
  int field = 0;
  
  int endIndex = 0;
  
  int beginIndex = 0;
  
  private Format.Field attribute;
  
  public FieldPosition(int paramInt) {
    this.field = paramInt;
  }
  
  public FieldPosition(Format.Field paramField) {
    this(paramField, -1);
  }
  
  public FieldPosition(Format.Field paramField, int paramInt) {
    this.attribute = paramField;
    this.field = paramInt;
  }
  
  public Format.Field getFieldAttribute() {
    return this.attribute;
  }
  
  public int getField() {
    return this.field;
  }
  
  public int getBeginIndex() {
    return this.beginIndex;
  }
  
  public int getEndIndex() {
    return this.endIndex;
  }
  
  public void setBeginIndex(int paramInt) {
    this.beginIndex = paramInt;
  }
  
  public void setEndIndex(int paramInt) {
    this.endIndex = paramInt;
  }
  
  Format.FieldDelegate getFieldDelegate() {
    return new Delegate(this, null);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof FieldPosition))
      return false; 
    FieldPosition fieldPosition = (FieldPosition)paramObject;
    if (this.attribute == null) {
      if (fieldPosition.attribute != null)
        return false; 
    } else if (!this.attribute.equals(fieldPosition.attribute)) {
      return false;
    } 
    return (this.beginIndex == fieldPosition.beginIndex && this.endIndex == fieldPosition.endIndex && this.field == fieldPosition.field);
  }
  
  public int hashCode() {
    return this.field << 24 | this.beginIndex << 16 | this.endIndex;
  }
  
  public String toString() {
    return getClass().getName() + "[field=" + this.field + ",attribute=" + this.attribute + ",beginIndex=" + this.beginIndex + ",endIndex=" + this.endIndex + ']';
  }
  
  private boolean matchesField(Format.Field paramField) {
    if (this.attribute != null)
      return this.attribute.equals(paramField); 
    return false;
  }
  
  private boolean matchesField(Format.Field paramField, int paramInt) {
    if (this.attribute != null)
      return this.attribute.equals(paramField); 
    return (paramInt == this.field);
  }
  
  private class FieldPosition {}
}
