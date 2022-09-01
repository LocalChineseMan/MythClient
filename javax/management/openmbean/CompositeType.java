package javax.management.openmbean;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class CompositeType extends OpenType<CompositeData> {
  static final long serialVersionUID = -5366242454346948798L;
  
  private TreeMap<String, String> nameToDescription;
  
  private TreeMap<String, OpenType<?>> nameToType;
  
  private transient Integer myHashCode = null;
  
  private transient String myToString = null;
  
  private transient Set<String> myNamesSet = null;
  
  public CompositeType(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, OpenType<?>[] paramArrayOfOpenType) throws OpenDataException {
    super(CompositeData.class.getName(), paramString1, paramString2, false);
    checkForNullElement((Object[])paramArrayOfString1, "itemNames");
    checkForNullElement((Object[])paramArrayOfString2, "itemDescriptions");
    checkForNullElement((Object[])paramArrayOfOpenType, "itemTypes");
    checkForEmptyString(paramArrayOfString1, "itemNames");
    checkForEmptyString(paramArrayOfString2, "itemDescriptions");
    if (paramArrayOfString1.length != paramArrayOfString2.length || paramArrayOfString1.length != paramArrayOfOpenType.length)
      throw new IllegalArgumentException("Array arguments itemNames[], itemDescriptions[] and itemTypes[] should be of same length (got " + paramArrayOfString1.length + ", " + paramArrayOfString2.length + " and " + paramArrayOfOpenType.length + ")."); 
    this.nameToDescription = new TreeMap<>();
    this.nameToType = new TreeMap<>();
    for (byte b = 0; b < paramArrayOfString1.length; b++) {
      String str = paramArrayOfString1[b].trim();
      if (this.nameToDescription.containsKey(str))
        throw new OpenDataException("Argument's element itemNames[" + b + "]=\"" + paramArrayOfString1[b] + "\" duplicates a previous item names."); 
      this.nameToDescription.put(str, paramArrayOfString2[b].trim());
      this.nameToType.put(str, paramArrayOfOpenType[b]);
    } 
  }
  
  private static void checkForNullElement(Object[] paramArrayOfObject, String paramString) {
    if (paramArrayOfObject == null || paramArrayOfObject.length == 0)
      throw new IllegalArgumentException("Argument " + paramString + "[] cannot be null or empty."); 
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] == null)
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + b + "] cannot be null."); 
    } 
  }
  
  private static void checkForEmptyString(String[] paramArrayOfString, String paramString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b].trim().equals(""))
        throw new IllegalArgumentException("Argument's element " + paramString + "[" + b + "] cannot be an empty string."); 
    } 
  }
  
  public boolean containsKey(String paramString) {
    if (paramString == null)
      return false; 
    return this.nameToDescription.containsKey(paramString);
  }
  
  public String getDescription(String paramString) {
    if (paramString == null)
      return null; 
    return this.nameToDescription.get(paramString);
  }
  
  public OpenType<?> getType(String paramString) {
    if (paramString == null)
      return null; 
    return this.nameToType.get(paramString);
  }
  
  public Set<String> keySet() {
    if (this.myNamesSet == null)
      this.myNamesSet = Collections.unmodifiableSet(this.nameToDescription.keySet()); 
    return this.myNamesSet;
  }
  
  public boolean isValue(Object paramObject) {
    if (!(paramObject instanceof CompositeData))
      return false; 
    CompositeData compositeData = (CompositeData)paramObject;
    CompositeType compositeType = compositeData.getCompositeType();
    return isAssignableFrom(compositeType);
  }
  
  boolean isAssignableFrom(OpenType<?> paramOpenType) {
    if (!(paramOpenType instanceof CompositeType))
      return false; 
    CompositeType compositeType = (CompositeType)paramOpenType;
    if (!compositeType.getTypeName().equals(getTypeName()))
      return false; 
    for (String str : keySet()) {
      OpenType<?> openType1 = compositeType.getType(str);
      OpenType<?> openType2 = getType(str);
      if (openType1 == null || 
        !openType2.isAssignableFrom(openType1))
        return false; 
    } 
    return true;
  }
  
  public boolean equals(Object paramObject) {
    CompositeType compositeType;
    if (paramObject == null)
      return false; 
    try {
      compositeType = (CompositeType)paramObject;
    } catch (ClassCastException classCastException) {
      return false;
    } 
    if (!getTypeName().equals(compositeType.getTypeName()))
      return false; 
    if (!this.nameToType.equals(compositeType.nameToType))
      return false; 
    return true;
  }
  
  public int hashCode() {
    if (this.myHashCode == null) {
      int i = 0;
      i += getTypeName().hashCode();
      for (String str : this.nameToDescription.keySet()) {
        i += str.hashCode();
        i += ((OpenType)this.nameToType.get(str)).hashCode();
      } 
      this.myHashCode = Integer.valueOf(i);
    } 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(getClass().getName());
      stringBuilder.append("(name=");
      stringBuilder.append(getTypeName());
      stringBuilder.append(",items=(");
      byte b = 0;
      Iterator<String> iterator = this.nameToType.keySet().iterator();
      while (iterator.hasNext()) {
        String str = iterator.next();
        if (b)
          stringBuilder.append(","); 
        stringBuilder.append("(itemName=");
        stringBuilder.append(str);
        stringBuilder.append(",itemType=");
        stringBuilder.append(((OpenType)this.nameToType.get(str)).toString() + ")");
        b++;
      } 
      stringBuilder.append("))");
      this.myToString = stringBuilder.toString();
    } 
    return this.myToString;
  }
}
