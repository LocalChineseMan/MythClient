package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.io.InvalidObjectException;
import java.lang.annotation.ElementType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeDataView;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultMXBeanMappingFactory extends MXBeanMappingFactory {
  static abstract class NonNullMXBeanMapping extends MXBeanMapping {
    NonNullMXBeanMapping(Type param1Type, OpenType<?> param1OpenType) {
      super(param1Type, param1OpenType);
    }
    
    public final Object fromOpenValue(Object param1Object) throws InvalidObjectException {
      if (param1Object == null)
        return null; 
      return fromNonNullOpenValue(param1Object);
    }
    
    public final Object toOpenValue(Object param1Object) throws OpenDataException {
      if (param1Object == null)
        return null; 
      return toNonNullOpenValue(param1Object);
    }
    
    abstract Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException;
    
    abstract Object toNonNullOpenValue(Object param1Object) throws OpenDataException;
    
    boolean isIdentity() {
      return false;
    }
  }
  
  static boolean isIdentity(MXBeanMapping paramMXBeanMapping) {
    return (paramMXBeanMapping instanceof NonNullMXBeanMapping && ((NonNullMXBeanMapping)paramMXBeanMapping)
      .isIdentity());
  }
  
  private static final class Mappings extends WeakHashMap<Type, WeakReference<MXBeanMapping>> {
    private Mappings() {}
  }
  
  private static final Mappings mappings = new Mappings();
  
  private static final List<MXBeanMapping> permanentMappings = Util.newList();
  
  private static synchronized MXBeanMapping getMapping(Type paramType) {
    WeakReference<MXBeanMapping> weakReference = mappings.get(paramType);
    return (weakReference == null) ? null : weakReference.get();
  }
  
  private static synchronized void putMapping(Type paramType, MXBeanMapping paramMXBeanMapping) {
    WeakReference<MXBeanMapping> weakReference = new WeakReference<>(paramMXBeanMapping);
    mappings.put(paramType, weakReference);
  }
  
  private static synchronized void putPermanentMapping(Type paramType, MXBeanMapping paramMXBeanMapping) {
    putMapping(paramType, paramMXBeanMapping);
    permanentMappings.add(paramMXBeanMapping);
  }
  
  static {
    OpenType[] arrayOfOpenType = { 
        SimpleType.BIGDECIMAL, SimpleType.BIGINTEGER, SimpleType.BOOLEAN, SimpleType.BYTE, SimpleType.CHARACTER, SimpleType.DATE, SimpleType.DOUBLE, SimpleType.FLOAT, SimpleType.INTEGER, SimpleType.LONG, 
        SimpleType.OBJECTNAME, SimpleType.SHORT, SimpleType.STRING, SimpleType.VOID };
    for (byte b = 0; b < arrayOfOpenType.length; b++) {
      Class<?> clazz;
      OpenType<?> openType = arrayOfOpenType[b];
      try {
        clazz = Class.forName(openType.getClassName(), false, ObjectName.class
            .getClassLoader());
      } catch (ClassNotFoundException classNotFoundException) {
        throw new Error(classNotFoundException);
      } 
      IdentityMapping identityMapping = new IdentityMapping(clazz, openType);
      putPermanentMapping(clazz, identityMapping);
      if (clazz.getName().startsWith("java.lang."))
        try {
          Field field = clazz.getField("TYPE");
          Class<void> clazz1 = (Class)field.get(null);
          IdentityMapping identityMapping1 = new IdentityMapping(clazz1, openType);
          putPermanentMapping(clazz1, identityMapping1);
          if (clazz1 != void.class) {
            Class<?> clazz2 = Array.newInstance(clazz1, 0).getClass();
            ArrayType<?> arrayType = ArrayType.getPrimitiveArrayType(clazz2);
            IdentityMapping identityMapping2 = new IdentityMapping(clazz2, arrayType);
            putPermanentMapping(clazz2, identityMapping2);
          } 
        } catch (NoSuchFieldException noSuchFieldException) {
        
        } catch (IllegalAccessException illegalAccessException) {
          assert false;
        }  
    } 
  }
  
  public synchronized MXBeanMapping mappingForType(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    if (inProgress.containsKey(paramType))
      throw new OpenDataException("Recursive data structure, including " + 
          MXBeanIntrospector.typeName(paramType)); 
    MXBeanMapping mXBeanMapping = getMapping(paramType);
    if (mXBeanMapping != null)
      return mXBeanMapping; 
    inProgress.put(paramType, paramType);
    try {
      mXBeanMapping = makeMapping(paramType, paramMXBeanMappingFactory);
    } catch (OpenDataException openDataException) {
      throw openDataException("Cannot convert type: " + MXBeanIntrospector.typeName(paramType), openDataException);
    } finally {
      inProgress.remove(paramType);
    } 
    putMapping(paramType, mXBeanMapping);
    return mXBeanMapping;
  }
  
  private MXBeanMapping makeMapping(Type paramType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    if (paramType instanceof GenericArrayType) {
      Type type = ((GenericArrayType)paramType).getGenericComponentType();
      return makeArrayOrCollectionMapping(paramType, type, paramMXBeanMappingFactory);
    } 
    if (paramType instanceof Class) {
      Class<?> clazz = (Class)paramType;
      if (clazz.isEnum())
        return makeEnumMapping(clazz, ElementType.class); 
      if (clazz.isArray()) {
        Class<?> clazz1 = clazz.getComponentType();
        return makeArrayOrCollectionMapping(clazz, clazz1, paramMXBeanMappingFactory);
      } 
      if (JMX.isMXBeanInterface(clazz))
        return makeMXBeanRefMapping(clazz); 
      return makeCompositeMapping(clazz, paramMXBeanMappingFactory);
    } 
    if (paramType instanceof ParameterizedType)
      return makeParameterizedTypeMapping((ParameterizedType)paramType, paramMXBeanMappingFactory); 
    throw new OpenDataException("Cannot map type: " + paramType);
  }
  
  private static <T extends Enum<T>> MXBeanMapping makeEnumMapping(Class<?> paramClass, Class<T> paramClass1) {
    ReflectUtil.checkPackageAccess(paramClass);
    return new EnumMapping<>(Util.<Class<Enum>>cast(paramClass));
  }
  
  private MXBeanMapping makeArrayOrCollectionMapping(Type paramType1, Type paramType2, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    Class<?> clazz2;
    String str;
    MXBeanMapping mXBeanMapping = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    OpenType<?> openType = mXBeanMapping.getOpenType();
    ArrayType<?> arrayType = ArrayType.getArrayType(openType);
    Class<?> clazz1 = mXBeanMapping.getOpenClass();
    if (clazz1.isArray()) {
      str = "[" + clazz1.getName();
    } else {
      str = "[L" + clazz1.getName() + ";";
    } 
    try {
      clazz2 = Class.forName(str);
    } catch (ClassNotFoundException classNotFoundException) {
      throw openDataException("Cannot obtain array class", classNotFoundException);
    } 
    if (paramType1 instanceof ParameterizedType)
      return new CollectionMapping(paramType1, arrayType, clazz2, mXBeanMapping); 
    if (isIdentity(mXBeanMapping))
      return new IdentityMapping(paramType1, arrayType); 
    return new ArrayMapping(paramType1, arrayType, clazz2, mXBeanMapping);
  }
  
  private static final String[] keyArray = new String[] { "key" };
  
  private static final String[] keyValueArray = new String[] { "key", "value" };
  
  private MXBeanMapping makeTabularMapping(Type paramType1, boolean paramBoolean, Type paramType2, Type paramType3, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    String str = MXBeanIntrospector.typeName(paramType1);
    MXBeanMapping mXBeanMapping1 = paramMXBeanMappingFactory.mappingForType(paramType2, paramMXBeanMappingFactory);
    MXBeanMapping mXBeanMapping2 = paramMXBeanMappingFactory.mappingForType(paramType3, paramMXBeanMappingFactory);
    OpenType<?> openType1 = mXBeanMapping1.getOpenType();
    OpenType<?> openType2 = mXBeanMapping2.getOpenType();
    CompositeType compositeType = new CompositeType(str, str, keyValueArray, keyValueArray, (OpenType<?>[])new OpenType[] { openType1, openType2 });
    TabularType tabularType = new TabularType(str, str, compositeType, keyArray);
    return new TabularMapping(paramType1, paramBoolean, tabularType, mXBeanMapping1, mXBeanMapping2);
  }
  
  private MXBeanMapping makeParameterizedTypeMapping(ParameterizedType paramParameterizedType, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    Type type = paramParameterizedType.getRawType();
    if (type instanceof Class) {
      Class<List> clazz = (Class)type;
      if (clazz == List.class || clazz == Set.class || clazz == SortedSet.class) {
        Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
        assert arrayOfType.length == 1;
        if (clazz == SortedSet.class)
          mustBeComparable(clazz, arrayOfType[0]); 
        return makeArrayOrCollectionMapping(paramParameterizedType, arrayOfType[0], paramMXBeanMappingFactory);
      } 
      boolean bool = (clazz == SortedMap.class) ? true : false;
      if (clazz == Map.class || bool) {
        Type[] arrayOfType = paramParameterizedType.getActualTypeArguments();
        assert arrayOfType.length == 2;
        if (bool)
          mustBeComparable(clazz, arrayOfType[0]); 
        return makeTabularMapping(paramParameterizedType, bool, arrayOfType[0], arrayOfType[1], paramMXBeanMappingFactory);
      } 
    } 
    throw new OpenDataException("Cannot convert type: " + paramParameterizedType);
  }
  
  private static MXBeanMapping makeMXBeanRefMapping(Type paramType) throws OpenDataException {
    return new MXBeanRefMapping(paramType);
  }
  
  private MXBeanMapping makeCompositeMapping(Class<?> paramClass, MXBeanMappingFactory paramMXBeanMappingFactory) throws OpenDataException {
    boolean bool = (paramClass.getName().equals("com.sun.management.GcInfo") && paramClass.getClassLoader() == null) ? true : false;
    ReflectUtil.checkPackageAccess(paramClass);
    List<Method> list = MBeanAnalyzer.eliminateCovariantMethods(Arrays.asList(paramClass.getMethods()));
    SortedMap<?, ?> sortedMap = Util.newSortedMap();
    for (Method method1 : list) {
      String str = propertyName(method1);
      if (str == null)
        continue; 
      if (bool && str.equals("CompositeType"))
        continue; 
      Method method2 = (Method)sortedMap.put(decapitalize(str), method1);
      if (method2 != null) {
        String str1 = "Class " + paramClass.getName() + " has method name clash: " + method2.getName() + ", " + method1.getName();
        throw new OpenDataException(str1);
      } 
    } 
    int i = sortedMap.size();
    if (i == 0)
      throw new OpenDataException("Can't map " + paramClass.getName() + " to an open data type"); 
    Method[] arrayOfMethod = new Method[i];
    String[] arrayOfString = new String[i];
    OpenType[] arrayOfOpenType = new OpenType[i];
    byte b = 0;
    for (Map.Entry<?, ?> entry : sortedMap.entrySet()) {
      arrayOfString[b] = (String)entry.getKey();
      Method method = (Method)entry.getValue();
      arrayOfMethod[b] = method;
      Type type = method.getGenericReturnType();
      arrayOfOpenType[b] = paramMXBeanMappingFactory.mappingForType(type, paramMXBeanMappingFactory).getOpenType();
      b++;
    } 
    CompositeType compositeType = new CompositeType(paramClass.getName(), paramClass.getName(), arrayOfString, arrayOfString, (OpenType<?>[])arrayOfOpenType);
    return new CompositeMapping(paramClass, compositeType, arrayOfString, arrayOfMethod, paramMXBeanMappingFactory);
  }
  
  private static final class IdentityMapping extends NonNullMXBeanMapping {
    IdentityMapping(Type param1Type, OpenType<?> param1OpenType) {
      super(param1Type, param1OpenType);
    }
    
    boolean isIdentity() {
      return true;
    }
    
    Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      return param1Object;
    }
    
    Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      return param1Object;
    }
  }
  
  private static final class EnumMapping<T extends Enum<T>> extends NonNullMXBeanMapping {
    private final Class<T> enumClass;
    
    EnumMapping(Class<T> param1Class) {
      super(param1Class, SimpleType.STRING);
      this.enumClass = param1Class;
    }
    
    final Object toNonNullOpenValue(Object param1Object) {
      return ((Enum)param1Object).name();
    }
    
    final T fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      try {
        return Enum.valueOf(this.enumClass, (String)param1Object);
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot convert to enum: " + param1Object, exception);
      } 
    }
  }
  
  private static final class ArrayMapping extends NonNullMXBeanMapping {
    private final MXBeanMapping elementMapping;
    
    ArrayMapping(Type param1Type, ArrayType<?> param1ArrayType, Class<?> param1Class, MXBeanMapping param1MXBeanMapping) {
      super(param1Type, param1ArrayType);
      this.elementMapping = param1MXBeanMapping;
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Object[] arrayOfObject1 = (Object[])param1Object;
      int i = arrayOfObject1.length;
      Object[] arrayOfObject2 = (Object[])Array.newInstance(getOpenClass().getComponentType(), i);
      for (byte b = 0; b < i; b++)
        arrayOfObject2[b] = this.elementMapping.toOpenValue(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      Type type2;
      Object[] arrayOfObject1 = (Object[])param1Object;
      Type type1 = getJavaType();
      if (type1 instanceof GenericArrayType) {
        type2 = ((GenericArrayType)type1).getGenericComponentType();
      } else if (type1 instanceof Class && ((Class)type1)
        .isArray()) {
        type2 = ((Class)type1).getComponentType();
      } else {
        throw new IllegalArgumentException("Not an array: " + type1);
      } 
      Object[] arrayOfObject2 = (Object[])Array.newInstance((Class)type2, arrayOfObject1.length);
      for (byte b = 0; b < arrayOfObject1.length; b++)
        arrayOfObject2[b] = this.elementMapping.fromOpenValue(arrayOfObject1[b]); 
      return arrayOfObject2;
    }
    
    public void checkReconstructible() throws InvalidObjectException {
      this.elementMapping.checkReconstructible();
    }
  }
  
  private static final class CollectionMapping extends NonNullMXBeanMapping {
    private final Class<? extends Collection<?>> collectionClass;
    
    private final MXBeanMapping elementMapping;
    
    CollectionMapping(Type param1Type, ArrayType<?> param1ArrayType, Class<?> param1Class, MXBeanMapping param1MXBeanMapping) {
      super(param1Type, param1ArrayType);
      Object object;
      this.elementMapping = param1MXBeanMapping;
      Type type = ((ParameterizedType)param1Type).getRawType();
      Class<List> clazz = (Class)type;
      if (clazz == List.class) {
        object = ArrayList.class;
      } else if (clazz == Set.class) {
        object = HashSet.class;
      } else if (clazz == SortedSet.class) {
        object = TreeSet.class;
      } else {
        assert false;
        object = null;
      } 
      this.collectionClass = Util.<Class<? extends Collection<?>>>cast(object);
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Collection collection = (Collection)param1Object;
      if (collection instanceof SortedSet) {
        Comparator comparator = ((SortedSet)collection).comparator();
        if (comparator != null) {
          String str = "Cannot convert SortedSet with non-null comparator: " + comparator;
          throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
        } 
      } 
      Object[] arrayOfObject = (Object[])Array.newInstance(getOpenClass().getComponentType(), collection
          .size());
      byte b = 0;
      for (Object object : collection)
        arrayOfObject[b++] = this.elementMapping.toOpenValue(object); 
      return arrayOfObject;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      Collection<Object> collection;
      Object[] arrayOfObject = (Object[])param1Object;
      try {
        collection = Util.<Collection>cast(this.collectionClass.newInstance());
      } catch (Exception exception) {
        throw DefaultMXBeanMappingFactory.invalidObjectException("Cannot create collection", exception);
      } 
      for (Object object1 : arrayOfObject) {
        Object object2 = this.elementMapping.fromOpenValue(object1);
        if (!collection.add(object2)) {
          String str = "Could not add " + object1 + " to " + this.collectionClass.getName() + " (duplicate set element?)";
          throw new InvalidObjectException(str);
        } 
      } 
      return collection;
    }
    
    public void checkReconstructible() throws InvalidObjectException {
      this.elementMapping.checkReconstructible();
    }
  }
  
  private static final class DefaultMXBeanMappingFactory {}
  
  private static final class TabularMapping extends NonNullMXBeanMapping {
    private final boolean sortedMap;
    
    private final MXBeanMapping keyMapping;
    
    private final MXBeanMapping valueMapping;
    
    TabularMapping(Type param1Type, boolean param1Boolean, TabularType param1TabularType, MXBeanMapping param1MXBeanMapping1, MXBeanMapping param1MXBeanMapping2) {
      super(param1Type, param1TabularType);
      this.sortedMap = param1Boolean;
      this.keyMapping = param1MXBeanMapping1;
      this.valueMapping = param1MXBeanMapping2;
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      Map map = Util.<Map>cast(param1Object);
      if (map instanceof SortedMap) {
        Comparator comparator = ((SortedMap)map).comparator();
        if (comparator != null) {
          String str = "Cannot convert SortedMap with non-null comparator: " + comparator;
          throw DefaultMXBeanMappingFactory.openDataException(str, new IllegalArgumentException(str));
        } 
      } 
      TabularType tabularType = (TabularType)getOpenType();
      TabularDataSupport tabularDataSupport = new TabularDataSupport(tabularType);
      CompositeType compositeType = tabularType.getRowType();
      for (Map.Entry entry : map.entrySet()) {
        Object object1 = this.keyMapping.toOpenValue(entry.getKey());
        Object object2 = this.valueMapping.toOpenValue(entry.getValue());
        CompositeDataSupport compositeDataSupport = new CompositeDataSupport(compositeType, DefaultMXBeanMappingFactory.keyValueArray, new Object[] { object1, object2 });
        tabularDataSupport.put(compositeDataSupport);
      } 
      return tabularDataSupport;
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      TabularData tabularData = (TabularData)param1Object;
      Collection collection = Util.<Collection>cast(tabularData.values());
      Map<Object, Object> map = this.sortedMap ? Util.newSortedMap() : Util.newInsertionOrderMap();
      for (CompositeData compositeData : collection) {
        Object object1 = this.keyMapping.fromOpenValue(compositeData.get("key"));
        Object object2 = this.valueMapping.fromOpenValue(compositeData.get("value"));
        if (map.put(object1, object2) != null) {
          String str = "Duplicate entry in TabularData: key=" + object1;
          throw new InvalidObjectException(str);
        } 
      } 
      return map;
    }
    
    public void checkReconstructible() throws InvalidObjectException {
      this.keyMapping.checkReconstructible();
      this.valueMapping.checkReconstructible();
    }
  }
  
  private final class CompositeMapping extends NonNullMXBeanMapping {
    private final String[] itemNames;
    
    private final Method[] getters;
    
    private final MXBeanMapping[] getterMappings;
    
    private DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder;
    
    CompositeMapping(Class<?> param1Class, CompositeType param1CompositeType, String[] param1ArrayOfString, Method[] param1ArrayOfMethod, MXBeanMappingFactory param1MXBeanMappingFactory) throws OpenDataException {
      super(param1Class, param1CompositeType);
      assert param1ArrayOfString.length == param1ArrayOfMethod.length;
      this.itemNames = param1ArrayOfString;
      this.getters = param1ArrayOfMethod;
      this.getterMappings = new MXBeanMapping[param1ArrayOfMethod.length];
      for (byte b = 0; b < param1ArrayOfMethod.length; b++) {
        Type type = param1ArrayOfMethod[b].getGenericReturnType();
        this.getterMappings[b] = param1MXBeanMappingFactory.mappingForType(type, param1MXBeanMappingFactory);
      } 
    }
    
    final Object toNonNullOpenValue(Object param1Object) throws OpenDataException {
      CompositeType compositeType = (CompositeType)getOpenType();
      if (param1Object instanceof CompositeDataView)
        return ((CompositeDataView)param1Object).toCompositeData(compositeType); 
      if (param1Object == null)
        return null; 
      Object[] arrayOfObject = new Object[this.getters.length];
      for (byte b = 0; b < this.getters.length; b++) {
        try {
          Object object = MethodUtil.invoke(this.getters[b], param1Object, (Object[])null);
          arrayOfObject[b] = this.getterMappings[b].toOpenValue(object);
        } catch (Exception exception) {
          throw DefaultMXBeanMappingFactory.openDataException("Error calling getter for " + this.itemNames[b] + ": " + exception, exception);
        } 
      } 
      return new CompositeDataSupport(compositeType, this.itemNames, arrayOfObject);
    }
    
    private synchronized void makeCompositeBuilder() throws InvalidObjectException {
      if (this.compositeBuilder != null)
        return; 
      Class<?> clazz = (Class)getJavaType();
      DefaultMXBeanMappingFactory.CompositeBuilder[][] arrayOfCompositeBuilder = { { new DefaultMXBeanMappingFactory.CompositeBuilderViaFrom(clazz, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderViaConstructor(clazz, this.itemNames) }, { new DefaultMXBeanMappingFactory.CompositeBuilderCheckGetters(clazz, this.itemNames, this.getterMappings), new DefaultMXBeanMappingFactory.CompositeBuilderViaSetters(clazz, this.itemNames), new DefaultMXBeanMappingFactory.CompositeBuilderViaProxy(clazz, this.itemNames) } };
      DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder = null;
      StringBuilder stringBuilder = new StringBuilder();
      Throwable throwable = null;
      label33: for (DefaultMXBeanMappingFactory.CompositeBuilder[] arrayOfCompositeBuilder1 : arrayOfCompositeBuilder) {
        for (byte b = 0; b < arrayOfCompositeBuilder1.length; b++) {
          DefaultMXBeanMappingFactory.CompositeBuilder compositeBuilder1 = arrayOfCompositeBuilder1[b];
          String str = compositeBuilder1.applicable(this.getters);
          if (str == null) {
            compositeBuilder = compositeBuilder1;
            break label33;
          } 
          Throwable throwable1 = compositeBuilder1.possibleCause();
          if (throwable1 != null)
            throwable = throwable1; 
          if (str.length() > 0) {
            if (stringBuilder.length() > 0)
              stringBuilder.append("; "); 
            stringBuilder.append(str);
            if (b == 0)
              break; 
          } 
        } 
      } 
      if (compositeBuilder == null) {
        String str = "Do not know how to make a " + clazz.getName() + " from a CompositeData: " + stringBuilder;
        if (throwable != null)
          str = str + ". Remaining exceptions show a POSSIBLE cause."; 
        throw DefaultMXBeanMappingFactory.invalidObjectException(str, throwable);
      } 
      this.compositeBuilder = compositeBuilder;
    }
    
    public void checkReconstructible() throws InvalidObjectException {
      makeCompositeBuilder();
    }
    
    final Object fromNonNullOpenValue(Object param1Object) throws InvalidObjectException {
      makeCompositeBuilder();
      return this.compositeBuilder.fromCompositeData((CompositeData)param1Object, this.itemNames, this.getterMappings);
    }
  }
  
  static InvalidObjectException invalidObjectException(String paramString, Throwable paramThrowable) {
    return EnvHelp.<InvalidObjectException>initCause(new InvalidObjectException(paramString), paramThrowable);
  }
  
  static InvalidObjectException invalidObjectException(Throwable paramThrowable) {
    return invalidObjectException(paramThrowable.getMessage(), paramThrowable);
  }
  
  static OpenDataException openDataException(String paramString, Throwable paramThrowable) {
    return EnvHelp.<OpenDataException>initCause(new OpenDataException(paramString), paramThrowable);
  }
  
  static OpenDataException openDataException(Throwable paramThrowable) {
    return openDataException(paramThrowable.getMessage(), paramThrowable);
  }
  
  static void mustBeComparable(Class<?> paramClass, Type paramType) throws OpenDataException {
    if (!(paramType instanceof Class) || 
      !Comparable.class.isAssignableFrom((Class)paramType)) {
      String str = "Parameter class " + paramType + " of " + paramClass.getName() + " does not implement " + Comparable.class.getName();
      throw new OpenDataException(str);
    } 
  }
  
  public static String decapitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    int i = Character.offsetByCodePoints(paramString, 0, 1);
    if (i < paramString.length() && 
      Character.isUpperCase(paramString.codePointAt(i)))
      return paramString; 
    return paramString.substring(0, i).toLowerCase() + paramString
      .substring(i);
  }
  
  static String capitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    int i = paramString.offsetByCodePoints(0, 1);
    return paramString.substring(0, i).toUpperCase() + paramString
      .substring(i);
  }
  
  public static String propertyName(Method paramMethod) {
    String str1 = null;
    String str2 = paramMethod.getName();
    if (str2.startsWith("get")) {
      str1 = str2.substring(3);
    } else if (str2.startsWith("is") && paramMethod.getReturnType() == boolean.class) {
      str1 = str2.substring(2);
    } 
    if (str1 == null || str1.length() == 0 || (paramMethod
      .getParameterTypes()).length > 0 || paramMethod
      .getReturnType() == void.class || str2
      .equals("getClass"))
      return null; 
    return str1;
  }
  
  private static final Map<Type, Type> inProgress = Util.newIdentityHashMap();
  
  private static abstract class DefaultMXBeanMappingFactory {}
  
  private static final class DefaultMXBeanMappingFactory {}
  
  private static class DefaultMXBeanMappingFactory {}
  
  private static class DefaultMXBeanMappingFactory {}
  
  private static final class DefaultMXBeanMappingFactory {}
  
  private static final class DefaultMXBeanMappingFactory {}
}
