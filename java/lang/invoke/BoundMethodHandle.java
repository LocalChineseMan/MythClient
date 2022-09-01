package java.lang.invoke;

import java.lang.invoke.BoundMethodHandle;
import java.lang.invoke.DelegatingMethodHandle;
import java.lang.invoke.InvokerBytecodeGenerator;
import java.lang.invoke.LambdaForm;
import java.lang.invoke.LambdaFormEditor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.Stable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import sun.invoke.util.ValueConversions;
import sun.invoke.util.Wrapper;

abstract class BoundMethodHandle extends MethodHandle {
  private static final int FIELD_COUNT_THRESHOLD = 12;
  
  private static final int FORM_EXPRESSION_THRESHOLD = 24;
  
  BoundMethodHandle(MethodType paramMethodType, LambdaForm paramLambdaForm) {
    super(paramMethodType, paramLambdaForm);
    assert speciesData() == speciesData(paramLambdaForm);
  }
  
  static BoundMethodHandle bindSingle(MethodType paramMethodType, LambdaForm paramLambdaForm, LambdaForm.BasicType paramBasicType, Object paramObject) {
    try {
      switch (null.$SwitchMap$java$lang$invoke$LambdaForm$BasicType[paramBasicType.ordinal()]) {
        case 1:
          return bindSingle(paramMethodType, paramLambdaForm, paramObject);
        case 2:
          return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ValueConversions.widenSubword(paramObject));
        case 3:
          return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Long)paramObject).longValue());
        case 4:
          return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Float)paramObject).floatValue());
        case 5:
          return SpeciesData.EMPTY.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(paramMethodType, paramLambdaForm, ((Double)paramObject).doubleValue());
      } 
      throw MethodHandleStatics.newInternalError("unexpected xtype: " + paramBasicType);
    } catch (Throwable throwable) {
      throw MethodHandleStatics.newInternalError(throwable);
    } 
  }
  
  LambdaFormEditor editor() {
    return this.form.editor();
  }
  
  static BoundMethodHandle bindSingle(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject) {
    return Species_L.make(paramMethodType, paramLambdaForm, paramObject);
  }
  
  BoundMethodHandle bindArgumentL(int paramInt, Object paramObject) {
    return editor().bindArgumentL(this, paramInt, paramObject);
  }
  
  BoundMethodHandle bindArgumentI(int paramInt1, int paramInt2) {
    return editor().bindArgumentI(this, paramInt1, paramInt2);
  }
  
  BoundMethodHandle bindArgumentJ(int paramInt, long paramLong) {
    return editor().bindArgumentJ(this, paramInt, paramLong);
  }
  
  BoundMethodHandle bindArgumentF(int paramInt, float paramFloat) {
    return editor().bindArgumentF(this, paramInt, paramFloat);
  }
  
  BoundMethodHandle bindArgumentD(int paramInt, double paramDouble) {
    return editor().bindArgumentD(this, paramInt, paramDouble);
  }
  
  BoundMethodHandle rebind() {
    if (!tooComplex())
      return this; 
    return makeReinvoker((MethodHandle)this);
  }
  
  private boolean tooComplex() {
    return (fieldCount() > 12 || this.form
      .expressionCount() > 24);
  }
  
  static BoundMethodHandle makeReinvoker(MethodHandle paramMethodHandle) {
    LambdaForm lambdaForm = DelegatingMethodHandle.makeReinvokerForm(paramMethodHandle, 7, Species_L.SPECIES_DATA, Species_L.SPECIES_DATA
        
        .getterFunction(0));
    return Species_L.make(paramMethodHandle.type(), lambdaForm, paramMethodHandle);
  }
  
  static SpeciesData speciesData(LambdaForm paramLambdaForm) {
    Object object = (paramLambdaForm.names[0]).constraint;
    if (object instanceof SpeciesData)
      return (SpeciesData)object; 
    return SpeciesData.EMPTY;
  }
  
  Object internalProperties() {
    return "\n& BMH=" + internalValues();
  }
  
  final Object internalValues() {
    Object[] arrayOfObject = new Object[speciesData().fieldCount()];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfObject[b] = arg(b); 
    return Arrays.asList(arrayOfObject);
  }
  
  final Object arg(int paramInt) {
    try {
      switch (null.$SwitchMap$java$lang$invoke$LambdaForm$BasicType[speciesData().fieldType(paramInt).ordinal()]) {
        case 1:
          return (speciesData()).getters[paramInt].invokeBasic(this);
        case 2:
          return Integer.valueOf((speciesData()).getters[paramInt].invokeBasic(this));
        case 3:
          return Long.valueOf((speciesData()).getters[paramInt].invokeBasic(this));
        case 4:
          return Float.valueOf((speciesData()).getters[paramInt].invokeBasic(this));
        case 5:
          return Double.valueOf((speciesData()).getters[paramInt].invokeBasic(this));
      } 
    } catch (Throwable throwable) {
      throw MethodHandleStatics.newInternalError(throwable);
    } 
    throw new InternalError("unexpected type: " + (speciesData()).typeChars + "." + paramInt);
  }
  
  private static final class Species_L extends BoundMethodHandle {
    final Object argL0;
    
    private Species_L(MethodType param1MethodType, LambdaForm param1LambdaForm, Object param1Object) {
      super(param1MethodType, param1LambdaForm);
      this.argL0 = param1Object;
    }
    
    BoundMethodHandle.SpeciesData speciesData() {
      return SPECIES_DATA;
    }
    
    int fieldCount() {
      return 1;
    }
    
    static final BoundMethodHandle.SpeciesData SPECIES_DATA = BoundMethodHandle.SpeciesData.getForClass("L", (Class)Species_L.class);
    
    static BoundMethodHandle make(MethodType param1MethodType, LambdaForm param1LambdaForm, Object param1Object) {
      return new Species_L(param1MethodType, param1LambdaForm, param1Object);
    }
    
    final BoundMethodHandle copyWith(MethodType param1MethodType, LambdaForm param1LambdaForm) {
      return new Species_L(param1MethodType, param1LambdaForm, this.argL0);
    }
    
    final BoundMethodHandle copyWithExtendL(MethodType param1MethodType, LambdaForm param1LambdaForm, Object param1Object) {
      try {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.L_TYPE).constructor().invokeBasic(param1MethodType, param1LambdaForm, this.argL0, param1Object);
      } catch (Throwable throwable) {
        throw MethodHandleStatics.uncaughtException(throwable);
      } 
    }
    
    final BoundMethodHandle copyWithExtendI(MethodType param1MethodType, LambdaForm param1LambdaForm, int param1Int) {
      try {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.I_TYPE).constructor().invokeBasic(param1MethodType, param1LambdaForm, this.argL0, param1Int);
      } catch (Throwable throwable) {
        throw MethodHandleStatics.uncaughtException(throwable);
      } 
    }
    
    final BoundMethodHandle copyWithExtendJ(MethodType param1MethodType, LambdaForm param1LambdaForm, long param1Long) {
      try {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.J_TYPE).constructor().invokeBasic(param1MethodType, param1LambdaForm, this.argL0, param1Long);
      } catch (Throwable throwable) {
        throw MethodHandleStatics.uncaughtException(throwable);
      } 
    }
    
    final BoundMethodHandle copyWithExtendF(MethodType param1MethodType, LambdaForm param1LambdaForm, float param1Float) {
      try {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.F_TYPE).constructor().invokeBasic(param1MethodType, param1LambdaForm, this.argL0, param1Float);
      } catch (Throwable throwable) {
        throw MethodHandleStatics.uncaughtException(throwable);
      } 
    }
    
    final BoundMethodHandle copyWithExtendD(MethodType param1MethodType, LambdaForm param1LambdaForm, double param1Double) {
      try {
        return SPECIES_DATA.extendWith(LambdaForm.BasicType.D_TYPE).constructor().invokeBasic(param1MethodType, param1LambdaForm, this.argL0, param1Double);
      } catch (Throwable throwable) {
        throw MethodHandleStatics.uncaughtException(throwable);
      } 
    }
  }
  
  static class SpeciesData {
    private final String typeChars;
    
    private final LambdaForm.BasicType[] typeCodes;
    
    private final Class<? extends BoundMethodHandle> clazz;
    
    @Stable
    private final MethodHandle[] constructor;
    
    @Stable
    private final MethodHandle[] getters;
    
    @Stable
    private final LambdaForm.NamedFunction[] nominalGetters;
    
    @Stable
    private final SpeciesData[] extensions;
    
    int fieldCount() {
      return this.typeCodes.length;
    }
    
    LambdaForm.BasicType fieldType(int param1Int) {
      return this.typeCodes[param1Int];
    }
    
    char fieldTypeChar(int param1Int) {
      return this.typeChars.charAt(param1Int);
    }
    
    Object fieldSignature() {
      return this.typeChars;
    }
    
    public Class<? extends BoundMethodHandle> fieldHolder() {
      return this.clazz;
    }
    
    public String toString() {
      return "SpeciesData<" + fieldSignature() + ">";
    }
    
    LambdaForm.NamedFunction getterFunction(int param1Int) {
      LambdaForm.NamedFunction namedFunction = this.nominalGetters[param1Int];
      assert namedFunction.memberDeclaringClassOrNull() == fieldHolder();
      assert namedFunction.returnType() == fieldType(param1Int);
      return namedFunction;
    }
    
    LambdaForm.NamedFunction[] getterFunctions() {
      return this.nominalGetters;
    }
    
    MethodHandle[] getterHandles() {
      return this.getters;
    }
    
    MethodHandle constructor() {
      return this.constructor[0];
    }
    
    static final SpeciesData EMPTY = new SpeciesData("", BoundMethodHandle.class);
    
    private SpeciesData(String param1String, Class<? extends BoundMethodHandle> param1Class) {
      this.typeChars = param1String;
      this.typeCodes = LambdaForm.BasicType.basicTypes(param1String);
      this.clazz = param1Class;
      if (!INIT_DONE) {
        this.constructor = new MethodHandle[1];
        this.getters = new MethodHandle[param1String.length()];
        this.nominalGetters = new LambdaForm.NamedFunction[param1String.length()];
      } else {
        this.constructor = BoundMethodHandle.Factory.makeCtors(param1Class, param1String, null);
        this.getters = BoundMethodHandle.Factory.makeGetters(param1Class, param1String, null);
        this.nominalGetters = BoundMethodHandle.Factory.makeNominalGetters(param1String, null, this.getters);
      } 
      this.extensions = new SpeciesData[LambdaForm.BasicType.ARG_TYPE_LIMIT];
    }
    
    private void initForBootstrap() {
      assert !INIT_DONE;
      if (constructor() == null) {
        String str = this.typeChars;
        BoundMethodHandle.Factory.makeCtors(this.clazz, str, this.constructor);
        BoundMethodHandle.Factory.makeGetters(this.clazz, str, this.getters);
        BoundMethodHandle.Factory.makeNominalGetters(str, this.nominalGetters, this.getters);
      } 
    }
    
    private SpeciesData(String param1String) {
      this.typeChars = param1String;
      this.typeCodes = LambdaForm.BasicType.basicTypes(param1String);
      this.clazz = null;
      this.constructor = null;
      this.getters = null;
      this.nominalGetters = null;
      this.extensions = null;
    }
    
    private boolean isPlaceholder() {
      return (this.clazz == null);
    }
    
    private static final HashMap<String, SpeciesData> CACHE = new HashMap<>();
    
    static {
      CACHE.put("", EMPTY);
      Class<BoundMethodHandle> clazz = BoundMethodHandle.class;
      try {
        for (Class<?> clazz1 : clazz.getDeclaredClasses()) {
          if (clazz.isAssignableFrom(clazz1)) {
            Class<? extends BoundMethodHandle> clazz2 = clazz1.asSubclass(BoundMethodHandle.class);
            SpeciesData speciesData = BoundMethodHandle.Factory.speciesDataFromConcreteBMHClass(clazz2);
            assert speciesData != null : clazz2.getName();
            assert speciesData.clazz == clazz2;
            assert speciesData == lookupCache(speciesData.typeChars);
          } 
        } 
      } catch (Throwable throwable) {
        throw MethodHandleStatics.newInternalError(throwable);
      } 
      for (SpeciesData speciesData : CACHE.values())
        speciesData.initForBootstrap(); 
    }
    
    SpeciesData extendWith(byte param1Byte) {
      return extendWith(LambdaForm.BasicType.basicType(param1Byte));
    }
    
    SpeciesData extendWith(LambdaForm.BasicType param1BasicType) {
      int i = param1BasicType.ordinal();
      SpeciesData speciesData = this.extensions[i];
      if (speciesData != null)
        return speciesData; 
      this.extensions[i] = speciesData = get(this.typeChars + param1BasicType.basicTypeChar());
      return speciesData;
    }
    
    private static SpeciesData get(String param1String) {
      SpeciesData speciesData = lookupCache(param1String);
      if (!speciesData.isPlaceholder())
        return speciesData; 
      synchronized (speciesData) {
        if (lookupCache(param1String).isPlaceholder())
          BoundMethodHandle.Factory.generateConcreteBMHClass(param1String); 
      } 
      speciesData = lookupCache(param1String);
      assert speciesData != null && !speciesData.isPlaceholder();
      return speciesData;
    }
    
    static SpeciesData getForClass(String param1String, Class<? extends BoundMethodHandle> param1Class) {
      return updateCache(param1String, new SpeciesData(param1String, param1Class));
    }
    
    private static synchronized SpeciesData lookupCache(String param1String) {
      SpeciesData speciesData = CACHE.get(param1String);
      if (speciesData != null)
        return speciesData; 
      speciesData = new SpeciesData(param1String);
      assert speciesData.isPlaceholder();
      CACHE.put(param1String, speciesData);
      return speciesData;
    }
    
    private static synchronized SpeciesData updateCache(String param1String, SpeciesData param1SpeciesData) {
      SpeciesData speciesData;
      assert (speciesData = CACHE.get(param1String)) == null || speciesData.isPlaceholder();
      assert !param1SpeciesData.isPlaceholder();
      CACHE.put(param1String, param1SpeciesData);
      return param1SpeciesData;
    }
    
    private static final boolean INIT_DONE = Boolean.TRUE.booleanValue();
  }
  
  static SpeciesData getSpeciesData(String paramString) {
    return SpeciesData.get(paramString);
  }
  
  static class Factory {
    static final String JLO_SIG = "Ljava/lang/Object;";
    
    static final String JLS_SIG = "Ljava/lang/String;";
    
    static final String JLC_SIG = "Ljava/lang/Class;";
    
    static final String MH = "java/lang/invoke/MethodHandle";
    
    static final String MH_SIG = "Ljava/lang/invoke/MethodHandle;";
    
    static final String BMH = "java/lang/invoke/BoundMethodHandle";
    
    static final String BMH_SIG = "Ljava/lang/invoke/BoundMethodHandle;";
    
    static final String SPECIES_DATA = "java/lang/invoke/BoundMethodHandle$SpeciesData";
    
    static final String SPECIES_DATA_SIG = "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    
    static final String SPECIES_PREFIX_NAME = "Species_";
    
    static final String SPECIES_PREFIX_PATH = "java/lang/invoke/BoundMethodHandle$Species_";
    
    static final String BMHSPECIES_DATA_EWI_SIG = "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    
    static final String BMHSPECIES_DATA_GFC_SIG = "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    
    static final String MYSPECIES_DATA_SIG = "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;";
    
    static final String VOID_SIG = "()V";
    
    static final String INT_SIG = "()I";
    
    static final String SIG_INCIPIT = "(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;";
    
    static final String[] E_THROWABLE = new String[] { "java/lang/Throwable" };
    
    static Class<? extends BoundMethodHandle> generateConcreteBMHClass(String param1String) {
      ClassWriter classWriter = new ClassWriter(3);
      String str1 = LambdaForm.shortenSignature(param1String);
      String str2 = "java/lang/invoke/BoundMethodHandle$Species_" + str1;
      String str3 = "Species_" + str1;
      classWriter.visit(50, 48, str2, null, "java/lang/invoke/BoundMethodHandle", null);
      classWriter.visitSource(str3, null);
      classWriter.visitField(8, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null).visitEnd();
      for (byte b1 = 0; b1 < param1String.length(); b1++) {
        char c = param1String.charAt(b1);
        String str4 = makeFieldName(param1String, b1);
        String str5 = (c == 'L') ? "Ljava/lang/Object;" : String.valueOf(c);
        classWriter.visitField(16, str4, str5, null, null).visitEnd();
      } 
      MethodVisitor methodVisitor = classWriter.visitMethod(2, "<init>", makeSignature(param1String, true), null, null);
      methodVisitor.visitCode();
      methodVisitor.visitVarInsn(25, 0);
      methodVisitor.visitVarInsn(25, 1);
      methodVisitor.visitVarInsn(25, 2);
      methodVisitor.visitMethodInsn(183, "java/lang/invoke/BoundMethodHandle", "<init>", makeSignature("", true), false);
      int i;
      byte b2;
      for (i = 0, b2 = 0; i < param1String.length(); i++, b2++) {
        char c = param1String.charAt(i);
        methodVisitor.visitVarInsn(25, 0);
        methodVisitor.visitVarInsn(typeLoadOp(c), b2 + 3);
        methodVisitor.visitFieldInsn(181, str2, makeFieldName(param1String, i), typeSig(c));
        if (c == 'J' || c == 'D')
          b2++; 
      } 
      methodVisitor.visitInsn(177);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      methodVisitor = classWriter.visitMethod(16, "speciesData", "()Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", null, null);
      methodVisitor.visitCode();
      methodVisitor.visitFieldInsn(178, str2, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
      methodVisitor.visitInsn(176);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      methodVisitor = classWriter.visitMethod(16, "fieldCount", "()I", null, null);
      methodVisitor.visitCode();
      i = param1String.length();
      if (i <= 5) {
        methodVisitor.visitInsn(3 + i);
      } else {
        methodVisitor.visitIntInsn(17, i);
      } 
      methodVisitor.visitInsn(172);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      methodVisitor = classWriter.visitMethod(8, "make", makeSignature(param1String, false), null, null);
      methodVisitor.visitCode();
      methodVisitor.visitTypeInsn(187, str2);
      methodVisitor.visitInsn(89);
      methodVisitor.visitVarInsn(25, 0);
      methodVisitor.visitVarInsn(25, 1);
      for (b2 = 0, null = 0; b2 < param1String.length(); b2++, null++) {
        char c = param1String.charAt(b2);
        methodVisitor.visitVarInsn(typeLoadOp(c), null + 2);
        if (c == 'J' || c == 'D')
          null++; 
      } 
      methodVisitor.visitMethodInsn(183, str2, "<init>", makeSignature(param1String, true), false);
      methodVisitor.visitInsn(176);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      methodVisitor = classWriter.visitMethod(16, "copyWith", makeSignature("", false), null, null);
      methodVisitor.visitCode();
      methodVisitor.visitTypeInsn(187, str2);
      methodVisitor.visitInsn(89);
      methodVisitor.visitVarInsn(25, 1);
      methodVisitor.visitVarInsn(25, 2);
      emitPushFields(param1String, str2, methodVisitor);
      methodVisitor.visitMethodInsn(183, str2, "<init>", makeSignature(param1String, true), false);
      methodVisitor.visitInsn(176);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      for (LambdaForm.BasicType basicType : LambdaForm.BasicType.ARG_TYPES) {
        int j = basicType.ordinal();
        char c = basicType.basicTypeChar();
        methodVisitor = classWriter.visitMethod(16, "copyWithExtend" + c, makeSignature(String.valueOf(c), false), null, E_THROWABLE);
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(178, str2, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
        int k = 3 + j;
        assert k <= 8;
        methodVisitor.visitInsn(k);
        methodVisitor.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "extendWith", "(B)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", false);
        methodVisitor.visitMethodInsn(182, "java/lang/invoke/BoundMethodHandle$SpeciesData", "constructor", "()Ljava/lang/invoke/MethodHandle;", false);
        methodVisitor.visitVarInsn(25, 1);
        methodVisitor.visitVarInsn(25, 2);
        emitPushFields(param1String, str2, methodVisitor);
        methodVisitor.visitVarInsn(typeLoadOp(c), 3);
        methodVisitor.visitMethodInsn(182, "java/lang/invoke/MethodHandle", "invokeBasic", makeSignature(param1String + c, false), false);
        methodVisitor.visitInsn(176);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
      } 
      methodVisitor = classWriter.visitMethod(8, "<clinit>", "()V", null, null);
      methodVisitor.visitCode();
      methodVisitor.visitLdcInsn(param1String);
      methodVisitor.visitLdcInsn(Type.getObjectType(str2));
      methodVisitor.visitMethodInsn(184, "java/lang/invoke/BoundMethodHandle$SpeciesData", "getForClass", "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/BoundMethodHandle$SpeciesData;", false);
      methodVisitor.visitFieldInsn(179, str2, "SPECIES_DATA", "Ljava/lang/invoke/BoundMethodHandle$SpeciesData;");
      methodVisitor.visitInsn(177);
      methodVisitor.visitMaxs(0, 0);
      methodVisitor.visitEnd();
      classWriter.visitEnd();
      byte[] arrayOfByte = classWriter.toByteArray();
      InvokerBytecodeGenerator.maybeDump(str2, arrayOfByte);
      Class<?> clazz = MethodHandleStatics.UNSAFE.defineClass(str2, arrayOfByte, 0, arrayOfByte.length, BoundMethodHandle.class.getClassLoader(), null).asSubclass(BoundMethodHandle.class);
      MethodHandleStatics.UNSAFE.ensureClassInitialized(clazz);
      return (Class)clazz;
    }
    
    private static int typeLoadOp(char param1Char) {
      switch (param1Char) {
        case 'L':
          return 25;
        case 'I':
          return 21;
        case 'J':
          return 22;
        case 'F':
          return 23;
        case 'D':
          return 24;
      } 
      throw MethodHandleStatics.newInternalError("unrecognized type " + param1Char);
    }
    
    private static void emitPushFields(String param1String1, String param1String2, MethodVisitor param1MethodVisitor) {
      for (byte b = 0; b < param1String1.length(); b++) {
        char c = param1String1.charAt(b);
        param1MethodVisitor.visitVarInsn(25, 0);
        param1MethodVisitor.visitFieldInsn(180, param1String2, makeFieldName(param1String1, b), typeSig(c));
      } 
    }
    
    static String typeSig(char param1Char) {
      return (param1Char == 'L') ? "Ljava/lang/Object;" : String.valueOf(param1Char);
    }
    
    private static MethodHandle makeGetter(Class<?> param1Class, String param1String, int param1Int) {
      String str = makeFieldName(param1String, param1Int);
      Class<?> clazz = Wrapper.forBasicType(param1String.charAt(param1Int)).primitiveType();
      try {
        return BoundMethodHandle.LOOKUP.findGetter(param1Class, str, clazz);
      } catch (NoSuchFieldException|IllegalAccessException noSuchFieldException) {
        throw MethodHandleStatics.newInternalError(noSuchFieldException);
      } 
    }
    
    static MethodHandle[] makeGetters(Class<?> param1Class, String param1String, MethodHandle[] param1ArrayOfMethodHandle) {
      if (param1ArrayOfMethodHandle == null)
        param1ArrayOfMethodHandle = new MethodHandle[param1String.length()]; 
      for (byte b = 0; b < param1ArrayOfMethodHandle.length; b++) {
        param1ArrayOfMethodHandle[b] = makeGetter(param1Class, param1String, b);
        assert param1ArrayOfMethodHandle[b].internalMemberName().getDeclaringClass() == param1Class;
      } 
      return param1ArrayOfMethodHandle;
    }
    
    static MethodHandle[] makeCtors(Class<? extends BoundMethodHandle> param1Class, String param1String, MethodHandle[] param1ArrayOfMethodHandle) {
      if (param1ArrayOfMethodHandle == null)
        param1ArrayOfMethodHandle = new MethodHandle[1]; 
      if (param1String.equals(""))
        return param1ArrayOfMethodHandle; 
      param1ArrayOfMethodHandle[0] = makeCbmhCtor(param1Class, param1String);
      return param1ArrayOfMethodHandle;
    }
    
    static LambdaForm.NamedFunction[] makeNominalGetters(String param1String, LambdaForm.NamedFunction[] param1ArrayOfNamedFunction, MethodHandle[] param1ArrayOfMethodHandle) {
      if (param1ArrayOfNamedFunction == null)
        param1ArrayOfNamedFunction = new LambdaForm.NamedFunction[param1String.length()]; 
      for (byte b = 0; b < param1ArrayOfNamedFunction.length; b++)
        param1ArrayOfNamedFunction[b] = new LambdaForm.NamedFunction(param1ArrayOfMethodHandle[b]); 
      return param1ArrayOfNamedFunction;
    }
    
    static BoundMethodHandle.SpeciesData speciesDataFromConcreteBMHClass(Class<? extends BoundMethodHandle> param1Class) {
      try {
        Field field = param1Class.getDeclaredField("SPECIES_DATA");
        return (BoundMethodHandle.SpeciesData)field.get(null);
      } catch (ReflectiveOperationException reflectiveOperationException) {
        throw MethodHandleStatics.newInternalError(reflectiveOperationException);
      } 
    }
    
    private static String makeFieldName(String param1String, int param1Int) {
      assert param1Int >= 0 && param1Int < param1String.length();
      return "arg" + param1String.charAt(param1Int) + param1Int;
    }
    
    private static String makeSignature(String param1String, boolean param1Boolean) {
      StringBuilder stringBuilder = new StringBuilder("(Ljava/lang/invoke/MethodType;Ljava/lang/invoke/LambdaForm;");
      for (char c : param1String.toCharArray())
        stringBuilder.append(typeSig(c)); 
      return stringBuilder.append(')').append(param1Boolean ? "V" : BMH_SIG).toString();
    }
    
    static MethodHandle makeCbmhCtor(Class<? extends BoundMethodHandle> param1Class, String param1String) {
      try {
        return BoundMethodHandle.LOOKUP.findStatic(param1Class, "make", MethodType.fromMethodDescriptorString(makeSignature(param1String, false), null));
      } catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|TypeNotPresentException noSuchMethodException) {
        throw MethodHandleStatics.newInternalError(noSuchMethodException);
      } 
    }
  }
  
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.Lookup.IMPL_LOOKUP;
  
  static final SpeciesData SPECIES_DATA = SpeciesData.EMPTY;
  
  private static final SpeciesData[] SPECIES_DATA_CACHE = new SpeciesData[5];
  
  private static SpeciesData checkCache(int paramInt, String paramString) {
    int i = paramInt - 1;
    SpeciesData speciesData = SPECIES_DATA_CACHE[i];
    if (speciesData != null)
      return speciesData; 
    SPECIES_DATA_CACHE[i] = speciesData = getSpeciesData(paramString);
    return speciesData;
  }
  
  static SpeciesData speciesData_L() {
    return checkCache(1, "L");
  }
  
  static SpeciesData speciesData_LL() {
    return checkCache(2, "LL");
  }
  
  static SpeciesData speciesData_LLL() {
    return checkCache(3, "LLL");
  }
  
  static SpeciesData speciesData_LLLL() {
    return checkCache(4, "LLLL");
  }
  
  static SpeciesData speciesData_LLLLL() {
    return checkCache(5, "LLLLL");
  }
  
  abstract SpeciesData speciesData();
  
  abstract int fieldCount();
  
  abstract BoundMethodHandle copyWith(MethodType paramMethodType, LambdaForm paramLambdaForm);
  
  abstract BoundMethodHandle copyWithExtendL(MethodType paramMethodType, LambdaForm paramLambdaForm, Object paramObject);
  
  abstract BoundMethodHandle copyWithExtendI(MethodType paramMethodType, LambdaForm paramLambdaForm, int paramInt);
  
  abstract BoundMethodHandle copyWithExtendJ(MethodType paramMethodType, LambdaForm paramLambdaForm, long paramLong);
  
  abstract BoundMethodHandle copyWithExtendF(MethodType paramMethodType, LambdaForm paramLambdaForm, float paramFloat);
  
  abstract BoundMethodHandle copyWithExtendD(MethodType paramMethodType, LambdaForm paramLambdaForm, double paramDouble);
}
