package sun.reflect.generics.reflectiveObjects;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

public class ParameterizedTypeImpl implements ParameterizedType {
  private final Type[] actualTypeArguments;
  
  private final Class<?> rawType;
  
  private final Type ownerType;
  
  private ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType) {
    this.actualTypeArguments = paramArrayOfType;
    this.rawType = paramClass;
    this.ownerType = (paramType != null) ? paramType : paramClass.getDeclaringClass();
    validateConstructorArguments();
  }
  
  private void validateConstructorArguments() {
    TypeVariable[] arrayOfTypeVariable = (TypeVariable[])this.rawType.getTypeParameters();
    if (arrayOfTypeVariable.length != this.actualTypeArguments.length)
      throw new MalformedParameterizedTypeException(); 
    for (byte b = 0; b < this.actualTypeArguments.length; b++);
  }
  
  public static ParameterizedTypeImpl make(Class<?> paramClass, Type[] paramArrayOfType, Type paramType) {
    return new ParameterizedTypeImpl(paramClass, paramArrayOfType, paramType);
  }
  
  public Type[] getActualTypeArguments() {
    return (Type[])this.actualTypeArguments.clone();
  }
  
  public Class<?> getRawType() {
    return this.rawType;
  }
  
  public Type getOwnerType() {
    return this.ownerType;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramObject;
      if (this == parameterizedType)
        return true; 
      Type type1 = parameterizedType.getOwnerType();
      Type type2 = parameterizedType.getRawType();
      return (
        Objects.equals(this.ownerType, type1) && 
        Objects.equals(this.rawType, type2) && 
        Arrays.equals((Object[])this.actualTypeArguments, (Object[])parameterizedType
          .getActualTypeArguments()));
    } 
    return false;
  }
  
  public int hashCode() {
    return Arrays.hashCode((Object[])this.actualTypeArguments) ^ Objects.hashCode(this.ownerType) ^ Objects.hashCode(this.rawType);
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.ownerType != null) {
      if (this.ownerType instanceof Class) {
        stringBuilder.append(((Class)this.ownerType).getName());
      } else {
        stringBuilder.append(this.ownerType.toString());
      } 
      stringBuilder.append(".");
      if (this.ownerType instanceof ParameterizedTypeImpl) {
        stringBuilder.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
      } else {
        stringBuilder.append(this.rawType.getName());
      } 
    } else {
      stringBuilder.append(this.rawType.getName());
    } 
    if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
      stringBuilder.append("<");
      boolean bool = true;
      for (Type type : this.actualTypeArguments) {
        if (!bool)
          stringBuilder.append(", "); 
        stringBuilder.append(type.getTypeName());
        bool = false;
      } 
      stringBuilder.append(">");
    } 
    return stringBuilder.toString();
  }
}
