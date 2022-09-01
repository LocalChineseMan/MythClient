package javax.management.openmbean;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.ObjectName;

public final class SimpleType<T> extends OpenType<T> {
  static final long serialVersionUID = 2215577471957694503L;
  
  public static final SimpleType<Void> VOID = new SimpleType((Class)Void.class);
  
  public static final SimpleType<Boolean> BOOLEAN = new SimpleType((Class)Boolean.class);
  
  public static final SimpleType<Character> CHARACTER = new SimpleType((Class)Character.class);
  
  public static final SimpleType<Byte> BYTE = new SimpleType((Class)Byte.class);
  
  public static final SimpleType<Short> SHORT = new SimpleType((Class)Short.class);
  
  public static final SimpleType<Integer> INTEGER = new SimpleType((Class)Integer.class);
  
  public static final SimpleType<Long> LONG = new SimpleType((Class)Long.class);
  
  public static final SimpleType<Float> FLOAT = new SimpleType((Class)Float.class);
  
  public static final SimpleType<Double> DOUBLE = new SimpleType((Class)Double.class);
  
  public static final SimpleType<String> STRING = new SimpleType((Class)String.class);
  
  public static final SimpleType<BigDecimal> BIGDECIMAL = new SimpleType((Class)BigDecimal.class);
  
  public static final SimpleType<BigInteger> BIGINTEGER = new SimpleType((Class)BigInteger.class);
  
  public static final SimpleType<Date> DATE = new SimpleType((Class)Date.class);
  
  public static final SimpleType<ObjectName> OBJECTNAME = new SimpleType((Class)ObjectName.class);
  
  private static final SimpleType<?>[] typeArray = new SimpleType[] { 
      VOID, BOOLEAN, CHARACTER, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING, 
      BIGDECIMAL, BIGINTEGER, DATE, OBJECTNAME };
  
  private transient Integer myHashCode = null;
  
  private transient String myToString = null;
  
  private SimpleType(Class<T> paramClass) {
    super(paramClass.getName(), paramClass.getName(), paramClass.getName(), false);
  }
  
  public boolean isValue(Object paramObject) {
    if (paramObject == null)
      return false; 
    return getClassName().equals(paramObject.getClass().getName());
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SimpleType))
      return false; 
    SimpleType simpleType = (SimpleType)paramObject;
    return getClassName().equals(simpleType.getClassName());
  }
  
  public int hashCode() {
    if (this.myHashCode == null)
      this.myHashCode = Integer.valueOf(getClassName().hashCode()); 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = getClass().getName() + "(name=" + getTypeName() + ")"; 
    return this.myToString;
  }
  
  private static final Map<SimpleType<?>, SimpleType<?>> canonicalTypes = new HashMap<>();
  
  static {
    for (byte b = 0; b < typeArray.length; b++) {
      SimpleType<?> simpleType = typeArray[b];
      canonicalTypes.put(simpleType, simpleType);
    } 
  }
  
  public Object readResolve() throws ObjectStreamException {
    SimpleType simpleType = canonicalTypes.get(this);
    if (simpleType == null)
      throw new InvalidObjectException("Invalid SimpleType: " + this); 
    return simpleType;
  }
}
