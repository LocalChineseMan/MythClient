package java.util;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import sun.misc.SharedSecrets;

public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, Serializable {
  final Class<E> elementType;
  
  final Enum<?>[] universe;
  
  private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = (Enum<?>[])new Enum[0];
  
  EnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum) {
    this.elementType = paramClass;
    this.universe = paramArrayOfEnum;
  }
  
  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> paramClass) {
    Object[] arrayOfObject = getUniverse((Class)paramClass);
    if (arrayOfObject == null)
      throw new ClassCastException(paramClass + " not an enum"); 
    if (arrayOfObject.length <= 64)
      return new RegularEnumSet<>(paramClass, (Enum<?>[])arrayOfObject); 
    return new JumboEnumSet<>(paramClass, (Enum<?>[])arrayOfObject);
  }
  
  public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> paramClass) {
    EnumSet<E> enumSet = noneOf(paramClass);
    enumSet.addAll();
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> paramEnumSet) {
    return paramEnumSet.clone();
  }
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> paramCollection) {
    if (paramCollection instanceof EnumSet)
      return ((EnumSet)paramCollection).clone(); 
    if (paramCollection.isEmpty())
      throw new IllegalArgumentException("Collection is empty"); 
    Iterator<E> iterator = paramCollection.iterator();
    Enum enum_ = (Enum)iterator.next();
    EnumSet<Enum> enumSet = of(enum_);
    while (iterator.hasNext())
      enumSet.add(iterator.next()); 
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> paramEnumSet) {
    EnumSet<E> enumSet = copyOf(paramEnumSet);
    enumSet.complement();
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE) {
    EnumSet<Enum> enumSet = noneOf(paramE.getDeclaringClass());
    enumSet.add(paramE);
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2) {
    EnumSet<Enum> enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3) {
    EnumSet<Enum> enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4) {
    EnumSet<Enum> enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    enumSet.add(paramE4);
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4, E paramE5) {
    EnumSet<Enum> enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    enumSet.add(paramE4);
    enumSet.add(paramE5);
    return (EnumSet)enumSet;
  }
  
  @SafeVarargs
  public static <E extends Enum<E>> EnumSet<E> of(E paramE, E... paramVarArgs) {
    EnumSet<Enum> enumSet = noneOf(paramE.getDeclaringClass());
    enumSet.add(paramE);
    for (E e : paramVarArgs)
      enumSet.add(e); 
    return (EnumSet)enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> range(E paramE1, E paramE2) {
    if (paramE1.compareTo(paramE2) > 0)
      throw new IllegalArgumentException((new StringBuilder()).append(paramE1).append(" > ").append(paramE2).toString()); 
    EnumSet<Enum> enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.addRange(paramE1, paramE2);
    return (EnumSet)enumSet;
  }
  
  public EnumSet<E> clone() {
    try {
      return (EnumSet<E>)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError(cloneNotSupportedException);
    } 
  }
  
  final void typeCheck(E paramE) {
    Class<?> clazz = paramE.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      throw new ClassCastException(clazz + " != " + this.elementType); 
  }
  
  private static <E extends Enum<E>> E[] getUniverse(Class<E> paramClass) {
    return (E[])SharedSecrets.getJavaLangAccess().getEnumConstantsShared(paramClass);
  }
  
  Object writeReplace() {
    return new SerializationProxy<>(this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException {
    throw new InvalidObjectException("Proxy required");
  }
  
  abstract void addAll();
  
  abstract void addRange(E paramE1, E paramE2);
  
  abstract void complement();
  
  private static class EnumSet {}
}
