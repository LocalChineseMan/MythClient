package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public class GeneratedConstructorAccessor12 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    // Byte code:
    //   0: new sun/font/T2KFontScaler
    //   3: dup
    //   4: aload_1
    //   5: arraylength
    //   6: sipush #4
    //   9: if_icmpeq -> 20
    //   12: new java/lang/IllegalArgumentException
    //   15: dup
    //   16: invokespecial <init> : ()V
    //   19: athrow
    //   20: aload_1
    //   21: sipush #0
    //   24: aaload
    //   25: checkcast sun/font/Font2D
    //   28: aload_1
    //   29: sipush #1
    //   32: aaload
    //   33: astore_2
    //   34: aload_2
    //   35: instanceof java/lang/Byte
    //   38: ifeq -> 51
    //   41: aload_2
    //   42: checkcast java/lang/Byte
    //   45: invokevirtual byteValue : ()B
    //   48: goto -> 110
    //   51: aload_2
    //   52: instanceof java/lang/Character
    //   55: ifeq -> 68
    //   58: aload_2
    //   59: checkcast java/lang/Character
    //   62: invokevirtual charValue : ()C
    //   65: goto -> 110
    //   68: aload_2
    //   69: instanceof java/lang/Short
    //   72: ifeq -> 85
    //   75: aload_2
    //   76: checkcast java/lang/Short
    //   79: invokevirtual shortValue : ()S
    //   82: goto -> 110
    //   85: aload_2
    //   86: instanceof java/lang/Integer
    //   89: ifeq -> 102
    //   92: aload_2
    //   93: checkcast java/lang/Integer
    //   96: invokevirtual intValue : ()I
    //   99: goto -> 110
    //   102: new java/lang/IllegalArgumentException
    //   105: dup
    //   106: invokespecial <init> : ()V
    //   109: athrow
    //   110: aload_1
    //   111: sipush #2
    //   114: aaload
    //   115: astore_2
    //   116: aload_2
    //   117: instanceof java/lang/Boolean
    //   120: ifeq -> 133
    //   123: aload_2
    //   124: checkcast java/lang/Boolean
    //   127: invokevirtual booleanValue : ()Z
    //   130: goto -> 141
    //   133: new java/lang/IllegalArgumentException
    //   136: dup
    //   137: invokespecial <init> : ()V
    //   140: athrow
    //   141: aload_1
    //   142: sipush #3
    //   145: aaload
    //   146: astore_2
    //   147: aload_2
    //   148: instanceof java/lang/Byte
    //   151: ifeq -> 164
    //   154: aload_2
    //   155: checkcast java/lang/Byte
    //   158: invokevirtual byteValue : ()B
    //   161: goto -> 223
    //   164: aload_2
    //   165: instanceof java/lang/Character
    //   168: ifeq -> 181
    //   171: aload_2
    //   172: checkcast java/lang/Character
    //   175: invokevirtual charValue : ()C
    //   178: goto -> 223
    //   181: aload_2
    //   182: instanceof java/lang/Short
    //   185: ifeq -> 198
    //   188: aload_2
    //   189: checkcast java/lang/Short
    //   192: invokevirtual shortValue : ()S
    //   195: goto -> 223
    //   198: aload_2
    //   199: instanceof java/lang/Integer
    //   202: ifeq -> 215
    //   205: aload_2
    //   206: checkcast java/lang/Integer
    //   209: invokevirtual intValue : ()I
    //   212: goto -> 223
    //   215: new java/lang/IllegalArgumentException
    //   218: dup
    //   219: invokespecial <init> : ()V
    //   222: athrow
    //   223: invokespecial <init> : (Lsun/font/Font2D;IZI)V
    //   226: areturn
    //   227: invokespecial toString : ()Ljava/lang/String;
    //   230: new java/lang/IllegalArgumentException
    //   233: dup_x1
    //   234: swap
    //   235: invokespecial <init> : (Ljava/lang/String;)V
    //   238: athrow
    //   239: new java/lang/reflect/InvocationTargetException
    //   242: dup_x1
    //   243: swap
    //   244: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   247: athrow
    // Exception table:
    //   from	to	target	type
    //   0	223	227	java/lang/ClassCastException
    //   0	223	227	java/lang/NullPointerException
    //   223	226	239	java/lang/Throwable
  }
}
