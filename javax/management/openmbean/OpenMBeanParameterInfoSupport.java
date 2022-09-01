package javax.management.openmbean;

import java.util.Set;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanParameterInfo;

public class OpenMBeanParameterInfoSupport extends MBeanParameterInfo implements OpenMBeanParameterInfo {
  static final long serialVersionUID = -7235016873758443122L;
  
  private OpenType<?> openType;
  
  private Object defaultValue = null;
  
  private Set<?> legalValues = null;
  
  private Comparable<?> minValue = null;
  
  private Comparable<?> maxValue = null;
  
  private transient Integer myHashCode = null;
  
  private transient String myToString = null;
  
  public OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType) {
    this(paramString1, paramString2, paramOpenType, (Descriptor)null);
  }
  
  public OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<?> paramOpenType, Descriptor paramDescriptor) {
    super(paramString1, (paramOpenType == null) ? null : paramOpenType
        .getClassName(), paramString2, 
        
        ImmutableDescriptor.union(new Descriptor[] { paramDescriptor, (paramOpenType == null) ? null : paramOpenType.getDescriptor() }));
    this.openType = paramOpenType;
    paramDescriptor = getDescriptor();
    this.defaultValue = OpenMBeanAttributeInfoSupport.valueFrom(paramDescriptor, "defaultValue", paramOpenType);
    this.legalValues = OpenMBeanAttributeInfoSupport.valuesFrom(paramDescriptor, "legalValues", paramOpenType);
    this.minValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(paramDescriptor, "minValue", paramOpenType);
    this.maxValue = OpenMBeanAttributeInfoSupport.comparableValueFrom(paramDescriptor, "maxValue", paramOpenType);
    try {
      OpenMBeanAttributeInfoSupport.check(this);
    } catch (OpenDataException openDataException) {
      throw new IllegalArgumentException(openDataException.getMessage(), openDataException);
    } 
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT) throws OpenDataException {
    this(paramString1, paramString2, paramOpenType, paramT, (T[])null);
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT) throws OpenDataException {
    this(paramString1, paramString2, paramOpenType, paramT, paramArrayOfT, (Comparable<T>)null, (Comparable<T>)null);
  }
  
  public <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, Comparable<T> paramComparable1, Comparable<T> paramComparable2) throws OpenDataException {
    this(paramString1, paramString2, paramOpenType, paramT, (T[])null, paramComparable1, paramComparable2);
  }
  
  private <T> OpenMBeanParameterInfoSupport(String paramString1, String paramString2, OpenType<T> paramOpenType, T paramT, T[] paramArrayOfT, Comparable<T> paramComparable1, Comparable<T> paramComparable2) throws OpenDataException {
    super(paramString1, (paramOpenType == null) ? null : paramOpenType
        .getClassName(), paramString2, 
        
        OpenMBeanAttributeInfoSupport.makeDescriptor(paramOpenType, paramT, paramArrayOfT, paramComparable1, paramComparable2));
    this.openType = paramOpenType;
    Descriptor descriptor = getDescriptor();
    this.defaultValue = paramT;
    this.minValue = paramComparable1;
    this.maxValue = paramComparable2;
    this.legalValues = (Set)descriptor.getFieldValue("legalValues");
    OpenMBeanAttributeInfoSupport.check(this);
  }
  
  private Object readResolve() {
    if ((getDescriptor().getFieldNames()).length == 0) {
      OpenType openType = OpenMBeanAttributeInfoSupport.<OpenType>cast(this.openType);
      Set set = OpenMBeanAttributeInfoSupport.<Set>cast(this.legalValues);
      Comparable comparable1 = OpenMBeanAttributeInfoSupport.<Comparable>cast(this.minValue);
      Comparable comparable2 = OpenMBeanAttributeInfoSupport.<Comparable>cast(this.maxValue);
      return new OpenMBeanParameterInfoSupport(this.name, this.description, this.openType, 
          
          OpenMBeanAttributeInfoSupport.makeDescriptor(openType, this.defaultValue, set, comparable1, comparable2));
    } 
    return this;
  }
  
  public OpenType<?> getOpenType() {
    return this.openType;
  }
  
  public Object getDefaultValue() {
    return this.defaultValue;
  }
  
  public Set<?> getLegalValues() {
    return this.legalValues;
  }
  
  public Comparable<?> getMinValue() {
    return this.minValue;
  }
  
  public Comparable<?> getMaxValue() {
    return this.maxValue;
  }
  
  public boolean hasDefaultValue() {
    return (this.defaultValue != null);
  }
  
  public boolean hasLegalValues() {
    return (this.legalValues != null);
  }
  
  public boolean hasMinValue() {
    return (this.minValue != null);
  }
  
  public boolean hasMaxValue() {
    return (this.maxValue != null);
  }
  
  public boolean isValue(Object paramObject) {
    return OpenMBeanAttributeInfoSupport.isValue(this, paramObject);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof OpenMBeanParameterInfo))
      return false; 
    OpenMBeanParameterInfo openMBeanParameterInfo = (OpenMBeanParameterInfo)paramObject;
    return OpenMBeanAttributeInfoSupport.equal(this, openMBeanParameterInfo);
  }
  
  public int hashCode() {
    if (this.myHashCode == null)
      this.myHashCode = Integer.valueOf(OpenMBeanAttributeInfoSupport.hashCode(this)); 
    return this.myHashCode.intValue();
  }
  
  public String toString() {
    if (this.myToString == null)
      this.myToString = OpenMBeanAttributeInfoSupport.toString(this); 
    return this.myToString;
  }
}
