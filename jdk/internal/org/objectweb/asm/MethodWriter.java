package jdk.internal.org.objectweb.asm;

class MethodWriter extends MethodVisitor {
  static final int ACC_CONSTRUCTOR = 524288;
  
  static final int SAME_FRAME = 0;
  
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  
  static final int RESERVED = 128;
  
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  
  static final int CHOP_FRAME = 248;
  
  static final int SAME_FRAME_EXTENDED = 251;
  
  static final int APPEND_FRAME = 252;
  
  static final int FULL_FRAME = 255;
  
  private static final int FRAMES = 0;
  
  private static final int MAXS = 1;
  
  private static final int NOTHING = 2;
  
  final ClassWriter cw;
  
  private int access;
  
  private final int name;
  
  private final int desc;
  
  private final String descriptor;
  
  String signature;
  
  int classReaderOffset;
  
  int classReaderLength;
  
  int exceptionCount;
  
  int[] exceptions;
  
  private ByteVector annd;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private AnnotationWriter tanns;
  
  private AnnotationWriter itanns;
  
  private AnnotationWriter[] panns;
  
  private AnnotationWriter[] ipanns;
  
  private int synthetics;
  
  private Attribute attrs;
  
  private ByteVector code = new ByteVector();
  
  private int maxStack;
  
  private int maxLocals;
  
  private int currentLocals;
  
  private int frameCount;
  
  private ByteVector stackMap;
  
  private int previousFrameOffset;
  
  private int[] previousFrame;
  
  private int[] frame;
  
  private int handlerCount;
  
  private Handler firstHandler;
  
  private Handler lastHandler;
  
  private int methodParametersCount;
  
  private ByteVector methodParameters;
  
  private int localVarCount;
  
  private ByteVector localVar;
  
  private int localVarTypeCount;
  
  private ByteVector localVarType;
  
  private int lineNumberCount;
  
  private ByteVector lineNumber;
  
  private int lastCodeOffset;
  
  private AnnotationWriter ctanns;
  
  private AnnotationWriter ictanns;
  
  private Attribute cattrs;
  
  private boolean resize;
  
  private int subroutines;
  
  private final int compute;
  
  private Label labels;
  
  private Label previousBlock;
  
  private Label currentBlock;
  
  private int stackSize;
  
  private int maxStackSize;
  
  MethodWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2) {
    super(327680);
    if (paramClassWriter.firstMethod == null) {
      paramClassWriter.firstMethod = this;
    } else {
      paramClassWriter.lastMethod.mv = this;
    } 
    paramClassWriter.lastMethod = this;
    this.cw = paramClassWriter;
    this.access = paramInt;
    if ("<init>".equals(paramString1))
      this.access |= 0x80000; 
    this.name = paramClassWriter.newUTF8(paramString1);
    this.desc = paramClassWriter.newUTF8(paramString2);
    this.descriptor = paramString2;
    this.signature = paramString3;
    if (paramArrayOfString != null && paramArrayOfString.length > 0) {
      this.exceptionCount = paramArrayOfString.length;
      this.exceptions = new int[this.exceptionCount];
      for (byte b = 0; b < this.exceptionCount; b++)
        this.exceptions[b] = paramClassWriter.newClass(paramArrayOfString[b]); 
    } 
    this.compute = paramBoolean2 ? 0 : (paramBoolean1 ? 1 : 2);
    if (paramBoolean1 || paramBoolean2) {
      int i = Type.getArgumentsAndReturnSizes(this.descriptor) >> 2;
      if ((paramInt & 0x8) != 0)
        i--; 
      this.maxLocals = i;
      this.currentLocals = i;
      this.labels = new Label();
      this.labels.status |= 0x8;
      visitLabel(this.labels);
    } 
  }
  
  public void visitParameter(String paramString, int paramInt) {
    if (this.methodParameters == null)
      this.methodParameters = new ByteVector(); 
    this.methodParametersCount++;
    this.methodParameters.putShort((paramString == null) ? 0 : this.cw.newUTF8(paramString))
      .putShort(paramInt);
  }
  
  public AnnotationVisitor visitAnnotationDefault() {
    this.annd = new ByteVector();
    return new AnnotationWriter(this.cw, false, this.annd, null, 0);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      annotationWriter.next = this.anns;
      this.anns = annotationWriter;
    } else {
      annotationWriter.next = this.ianns;
      this.ianns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, byteVector);
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.tanns;
      this.tanns = annotationWriter;
    } else {
      annotationWriter.next = this.itanns;
      this.itanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    if ("Ljava/lang/Synthetic;".equals(paramString)) {
      this.synthetics = Math.max(this.synthetics, paramInt + 1);
      return new AnnotationWriter(this.cw, false, byteVector, null, 0);
    } 
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      if (this.panns == null)
        this.panns = new AnnotationWriter[(Type.getArgumentTypes(this.descriptor)).length]; 
      annotationWriter.next = this.panns[paramInt];
      this.panns[paramInt] = annotationWriter;
    } else {
      if (this.ipanns == null)
        this.ipanns = new AnnotationWriter[(Type.getArgumentTypes(this.descriptor)).length]; 
      annotationWriter.next = this.ipanns[paramInt];
      this.ipanns[paramInt] = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    if (paramAttribute.isCodeAttribute()) {
      paramAttribute.next = this.cattrs;
      this.cattrs = paramAttribute;
    } else {
      paramAttribute.next = this.attrs;
      this.attrs = paramAttribute;
    } 
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    if (this.compute == 0)
      return; 
    if (paramInt1 == -1) {
      if (this.previousFrame == null)
        visitImplicitFirstFrame(); 
      this.currentLocals = paramInt2;
      int i = startFrame(this.code.length, paramInt2, paramInt3);
      byte b;
      for (b = 0; b < paramInt2; b++) {
        if (paramArrayOfObject1[b] instanceof String) {
          this.frame[i++] = 0x1700000 | this.cw
            .addType((String)paramArrayOfObject1[b]);
        } else if (paramArrayOfObject1[b] instanceof Integer) {
          this.frame[i++] = ((Integer)paramArrayOfObject1[b]).intValue();
        } else {
          this.frame[i++] = 0x1800000 | this.cw
            .addUninitializedType("", ((Label)paramArrayOfObject1[b]).position);
        } 
      } 
      for (b = 0; b < paramInt3; b++) {
        if (paramArrayOfObject2[b] instanceof String) {
          this.frame[i++] = 0x1700000 | this.cw
            .addType((String)paramArrayOfObject2[b]);
        } else if (paramArrayOfObject2[b] instanceof Integer) {
          this.frame[i++] = ((Integer)paramArrayOfObject2[b]).intValue();
        } else {
          this.frame[i++] = 0x1800000 | this.cw
            .addUninitializedType("", ((Label)paramArrayOfObject2[b]).position);
        } 
      } 
      endFrame();
    } else {
      int i;
      byte b;
      if (this.stackMap == null) {
        this.stackMap = new ByteVector();
        i = this.code.length;
      } else {
        i = this.code.length - this.previousFrameOffset - 1;
        if (i < 0) {
          if (paramInt1 == 3)
            return; 
          throw new IllegalStateException();
        } 
      } 
      switch (paramInt1) {
        case 0:
          this.currentLocals = paramInt2;
          this.stackMap.putByte(255).putShort(i).putShort(paramInt2);
          for (b = 0; b < paramInt2; b++)
            writeFrameType(paramArrayOfObject1[b]); 
          this.stackMap.putShort(paramInt3);
          for (b = 0; b < paramInt3; b++)
            writeFrameType(paramArrayOfObject2[b]); 
          break;
        case 1:
          this.currentLocals += paramInt2;
          this.stackMap.putByte(251 + paramInt2).putShort(i);
          for (b = 0; b < paramInt2; b++)
            writeFrameType(paramArrayOfObject1[b]); 
          break;
        case 2:
          this.currentLocals -= paramInt2;
          this.stackMap.putByte(251 - paramInt2).putShort(i);
          break;
        case 3:
          if (i < 64) {
            this.stackMap.putByte(i);
            break;
          } 
          this.stackMap.putByte(251).putShort(i);
          break;
        case 4:
          if (i < 64) {
            this.stackMap.putByte(64 + i);
          } else {
            this.stackMap.putByte(247)
              .putShort(i);
          } 
          writeFrameType(paramArrayOfObject2[0]);
          break;
      } 
      this.previousFrameOffset = this.code.length;
      this.frameCount++;
    } 
    this.maxStack = Math.max(this.maxStack, paramInt3);
    this.maxLocals = Math.max(this.maxLocals, this.currentLocals);
  }
  
  public void visitInsn(int paramInt) {
    this.lastCodeOffset = this.code.length;
    this.code.putByte(paramInt);
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
      } else {
        int i = this.stackSize + Frame.SIZE[paramInt];
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      } 
      if ((paramInt >= 172 && paramInt <= 177) || paramInt == 191)
        noSuccessor(); 
    } 
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    this.lastCodeOffset = this.code.length;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      } else if (paramInt1 != 188) {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    if (paramInt1 == 17) {
      this.code.put12(paramInt1, paramInt2);
    } else {
      this.code.put11(paramInt1, paramInt2);
    } 
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    this.lastCodeOffset = this.code.length;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      } else if (paramInt1 == 169) {
        this.currentBlock.status |= 0x100;
        this.currentBlock.inputStackTop = this.stackSize;
        noSuccessor();
      } else {
        int i = this.stackSize + Frame.SIZE[paramInt1];
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    if (this.compute != 2) {
      int i;
      if (paramInt1 == 22 || paramInt1 == 24 || paramInt1 == 55 || paramInt1 == 57) {
        i = paramInt2 + 2;
      } else {
        i = paramInt2 + 1;
      } 
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
    if (paramInt2 < 4 && paramInt1 != 169) {
      int i;
      if (paramInt1 < 54) {
        i = 26 + (paramInt1 - 21 << 2) + paramInt2;
      } else {
        i = 59 + (paramInt1 - 54 << 2) + paramInt2;
      } 
      this.code.putByte(i);
    } else if (paramInt2 >= 256) {
      this.code.putByte(196).put12(paramInt1, paramInt2);
    } else {
      this.code.put11(paramInt1, paramInt2);
    } 
    if (paramInt1 >= 54 && this.compute == 0 && this.handlerCount > 0)
      visitLabel(new Label()); 
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newClassItem(paramString);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, this.code.length, this.cw, item);
      } else if (paramInt == 187) {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    this.code.put12(paramInt, item.index);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newFieldItem(paramString1, paramString2, paramString3);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, item);
      } else {
        int i;
        char c = paramString3.charAt(0);
        switch (paramInt) {
          case 178:
            i = this.stackSize + ((c == 'D' || c == 'J') ? 2 : 1);
            break;
          case 179:
            i = this.stackSize + ((c == 'D' || c == 'J') ? -2 : -1);
            break;
          case 180:
            i = this.stackSize + ((c == 'D' || c == 'J') ? 1 : 0);
            break;
          default:
            i = this.stackSize + ((c == 'D' || c == 'J') ? -3 : -2);
            break;
        } 
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    this.code.put12(paramInt, item.index);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newMethodItem(paramString1, paramString2, paramString3, paramBoolean);
    int i = item.intVal;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, item);
      } else {
        int j;
        if (i == 0) {
          i = Type.getArgumentsAndReturnSizes(paramString3);
          item.intVal = i;
        } 
        if (paramInt == 184) {
          j = this.stackSize - (i >> 2) + (i & 0x3) + 1;
        } else {
          j = this.stackSize - (i >> 2) + (i & 0x3);
        } 
        if (j > this.maxStackSize)
          this.maxStackSize = j; 
        this.stackSize = j;
      }  
    if (paramInt == 185) {
      if (i == 0) {
        i = Type.getArgumentsAndReturnSizes(paramString3);
        item.intVal = i;
      } 
      this.code.put12(185, item.index).put11(i >> 2, 0);
    } else {
      this.code.put12(paramInt, item.index);
    } 
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newInvokeDynamicItem(paramString1, paramString2, paramHandle, paramVarArgs);
    int i = item.intVal;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(186, 0, this.cw, item);
      } else {
        if (i == 0) {
          i = Type.getArgumentsAndReturnSizes(paramString2);
          item.intVal = i;
        } 
        int j = this.stackSize - (i >> 2) + (i & 0x3) + 1;
        if (j > this.maxStackSize)
          this.maxStackSize = j; 
        this.stackSize = j;
      }  
    this.code.put12(186, item.index);
    this.code.putShort(0);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    this.lastCodeOffset = this.code.length;
    Label label = null;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
        (paramLabel.getFirst()).status |= 0x10;
        addSuccessor(0, paramLabel);
        if (paramInt != 167)
          label = new Label(); 
      } else if (paramInt == 168) {
        if ((paramLabel.status & 0x200) == 0) {
          paramLabel.status |= 0x200;
          this.subroutines++;
        } 
        this.currentBlock.status |= 0x80;
        addSuccessor(this.stackSize + 1, paramLabel);
        label = new Label();
      } else {
        this.stackSize += Frame.SIZE[paramInt];
        addSuccessor(this.stackSize, paramLabel);
      }  
    if ((paramLabel.status & 0x2) != 0 && paramLabel.position - this.code.length < -32768) {
      if (paramInt == 167) {
        this.code.putByte(200);
      } else if (paramInt == 168) {
        this.code.putByte(201);
      } else {
        if (label != null)
          label.status |= 0x10; 
        this.code.putByte((paramInt <= 166) ? ((paramInt + 1 ^ 0x1) - 1) : (paramInt ^ 0x1));
        this.code.putShort(8);
        this.code.putByte(200);
      } 
      paramLabel.put(this, this.code, this.code.length - 1, true);
    } else {
      this.code.putByte(paramInt);
      paramLabel.put(this, this.code, this.code.length - 1, false);
    } 
    if (this.currentBlock != null) {
      if (label != null)
        visitLabel(label); 
      if (paramInt == 167)
        noSuccessor(); 
    } 
  }
  
  public void visitLabel(Label paramLabel) {
    this.resize |= paramLabel.resolve(this, this.code.length, this.code.data);
    if ((paramLabel.status & 0x1) != 0)
      return; 
    if (this.compute == 0) {
      if (this.currentBlock != null) {
        if (paramLabel.position == this.currentBlock.position) {
          this.currentBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.currentBlock.frame;
          return;
        } 
        addSuccessor(0, paramLabel);
      } 
      this.currentBlock = paramLabel;
      if (paramLabel.frame == null) {
        paramLabel.frame = new Frame();
        paramLabel.frame.owner = paramLabel;
      } 
      if (this.previousBlock != null) {
        if (paramLabel.position == this.previousBlock.position) {
          this.previousBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.previousBlock.frame;
          this.currentBlock = this.previousBlock;
          return;
        } 
        this.previousBlock.successor = paramLabel;
      } 
      this.previousBlock = paramLabel;
    } else if (this.compute == 1) {
      if (this.currentBlock != null) {
        this.currentBlock.outputStackMax = this.maxStackSize;
        addSuccessor(this.stackSize, paramLabel);
      } 
      this.currentBlock = paramLabel;
      this.stackSize = 0;
      this.maxStackSize = 0;
      if (this.previousBlock != null)
        this.previousBlock.successor = paramLabel; 
      this.previousBlock = paramLabel;
    } 
  }
  
  public void visitLdcInsn(Object paramObject) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newConstItem(paramObject);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(18, 0, this.cw, item);
      } else {
        int j;
        if (item.type == 5 || item.type == 6) {
          j = this.stackSize + 2;
        } else {
          j = this.stackSize + 1;
        } 
        if (j > this.maxStackSize)
          this.maxStackSize = j; 
        this.stackSize = j;
      }  
    int i = item.index;
    if (item.type == 5 || item.type == 6) {
      this.code.put12(20, i);
    } else if (i >= 256) {
      this.code.put12(19, i);
    } else {
      this.code.put11(18, i);
    } 
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    this.lastCodeOffset = this.code.length;
    if (this.currentBlock != null && 
      this.compute == 0)
      this.currentBlock.frame.execute(132, paramInt1, null, null); 
    if (this.compute != 2) {
      int i = paramInt1 + 1;
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
    if (paramInt1 > 255 || paramInt2 > 127 || paramInt2 < -128) {
      this.code.putByte(196).put12(132, paramInt1)
        .putShort(paramInt2);
    } else {
      this.code.putByte(132).put11(paramInt1, paramInt2);
    } 
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) {
    this.lastCodeOffset = this.code.length;
    int i = this.code.length;
    this.code.putByte(170);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramInt1).putInt(paramInt2);
    for (byte b = 0; b < paramVarArgs.length; b++)
      paramVarArgs[b].put(this, this.code, i, true); 
    visitSwitchInsn(paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfint, Label[] paramArrayOfLabel) {
    this.lastCodeOffset = this.code.length;
    int i = this.code.length;
    this.code.putByte(171);
    this.code.putByteArray(null, 0, (4 - this.code.length % 4) % 4);
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramArrayOfLabel.length);
    for (byte b = 0; b < paramArrayOfLabel.length; b++) {
      this.code.putInt(paramArrayOfint[b]);
      paramArrayOfLabel[b].put(this, this.code, i, true);
    } 
    visitSwitchInsn(paramLabel, paramArrayOfLabel);
  }
  
  private void visitSwitchInsn(Label paramLabel, Label[] paramArrayOfLabel) {
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(171, 0, null, null);
        addSuccessor(0, paramLabel);
        (paramLabel.getFirst()).status |= 0x10;
        for (byte b = 0; b < paramArrayOfLabel.length; b++) {
          addSuccessor(0, paramArrayOfLabel[b]);
          (paramArrayOfLabel[b].getFirst()).status |= 0x10;
        } 
      } else {
        this.stackSize--;
        addSuccessor(this.stackSize, paramLabel);
        for (byte b = 0; b < paramArrayOfLabel.length; b++)
          addSuccessor(this.stackSize, paramArrayOfLabel[b]); 
      } 
      noSuccessor();
    } 
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    this.lastCodeOffset = this.code.length;
    Item item = this.cw.newClassItem(paramString);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(197, paramInt, this.cw, item);
      } else {
        this.stackSize += 1 - paramInt;
      }  
    this.code.put12(197, item.index).putByte(paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    paramInt = paramInt & 0xFF0000FF | this.lastCodeOffset << 8;
    AnnotationWriter.putTarget(paramInt, paramTypePath, byteVector);
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.ctanns;
      this.ctanns = annotationWriter;
    } else {
      annotationWriter.next = this.ictanns;
      this.ictanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {
    this.handlerCount++;
    Handler handler = new Handler();
    handler.start = paramLabel1;
    handler.end = paramLabel2;
    handler.handler = paramLabel3;
    handler.desc = paramString;
    handler.type = (paramString != null) ? this.cw.newClass(paramString) : 0;
    if (this.lastHandler == null) {
      this.firstHandler = handler;
    } else {
      this.lastHandler.next = handler;
    } 
    this.lastHandler = handler;
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, byteVector);
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.ctanns;
      this.ctanns = annotationWriter;
    } else {
      annotationWriter.next = this.ictanns;
      this.ictanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {
    if (paramString3 != null) {
      if (this.localVarType == null)
        this.localVarType = new ByteVector(); 
      this.localVarTypeCount++;
      this.localVarType.putShort(paramLabel1.position)
        .putShort(paramLabel2.position - paramLabel1.position)
        .putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString3))
        .putShort(paramInt);
    } 
    if (this.localVar == null)
      this.localVar = new ByteVector(); 
    this.localVarCount++;
    this.localVar.putShort(paramLabel1.position)
      .putShort(paramLabel2.position - paramLabel1.position)
      .putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString2))
      .putShort(paramInt);
    if (this.compute != 2) {
      char c = paramString2.charAt(0);
      int i = paramInt + ((c == 'J' || c == 'D') ? 2 : 1);
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfint, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    byteVector.putByte(paramInt >>> 24).putShort(paramArrayOfLabel1.length);
    int i;
    for (i = 0; i < paramArrayOfLabel1.length; i++)
      byteVector.putShort((paramArrayOfLabel1[i]).position)
        .putShort((paramArrayOfLabel2[i]).position - (paramArrayOfLabel1[i]).position)
        .putShort(paramArrayOfint[i]); 
    if (paramTypePath == null) {
      byteVector.putByte(0);
    } else {
      i = paramTypePath.b[paramTypePath.offset] * 2 + 1;
      byteVector.putByteArray(paramTypePath.b, paramTypePath.offset, i);
    } 
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.ctanns;
      this.ctanns = annotationWriter;
    } else {
      annotationWriter.next = this.ictanns;
      this.ictanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel) {
    if (this.lineNumber == null)
      this.lineNumber = new ByteVector(); 
    this.lineNumberCount++;
    this.lineNumber.putShort(paramLabel.position);
    this.lineNumber.putShort(paramInt);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    if (this.resize)
      resizeInstructions(); 
    if (this.compute == 0) {
      Handler handler = this.firstHandler;
      while (handler != null) {
        Label label3 = handler.start.getFirst();
        Label label4 = handler.handler.getFirst();
        Label label5 = handler.end.getFirst();
        String str = (handler.desc == null) ? "java/lang/Throwable" : handler.desc;
        int j = 0x1700000 | this.cw.addType(str);
        label4.status |= 0x10;
        while (label3 != label5) {
          Edge edge = new Edge();
          edge.info = j;
          edge.successor = label4;
          edge.next = label3.successors;
          label3.successors = edge;
          label3 = label3.successor;
        } 
        handler = handler.next;
      } 
      Frame frame = this.labels.frame;
      Type[] arrayOfType = Type.getArgumentTypes(this.descriptor);
      frame.initInputFrame(this.cw, this.access, arrayOfType, this.maxLocals);
      visitFrame(frame);
      int i = 0;
      Label label1 = this.labels;
      while (label1 != null) {
        Label label = label1;
        label1 = label1.next;
        label.next = null;
        frame = label.frame;
        if ((label.status & 0x10) != 0)
          label.status |= 0x20; 
        label.status |= 0x40;
        int j = frame.inputStack.length + label.outputStackMax;
        if (j > i)
          i = j; 
        Edge edge = label.successors;
        while (edge != null) {
          Label label3 = edge.successor.getFirst();
          boolean bool = frame.merge(this.cw, label3.frame, edge.info);
          if (bool && label3.next == null) {
            label3.next = label1;
            label1 = label3;
          } 
          edge = edge.next;
        } 
      } 
      Label label2 = this.labels;
      while (label2 != null) {
        frame = label2.frame;
        if ((label2.status & 0x20) != 0)
          visitFrame(frame); 
        if ((label2.status & 0x40) == 0) {
          Label label = label2.successor;
          int j = label2.position;
          int k = ((label == null) ? this.code.length : label.position) - 1;
          if (k >= j) {
            i = Math.max(i, 1);
            int m;
            for (m = j; m < k; m++)
              this.code.data[m] = 0; 
            this.code.data[k] = -65;
            m = startFrame(j, 0, 1);
            this.frame[m] = 0x1700000 | this.cw
              .addType("java/lang/Throwable");
            endFrame();
            this.firstHandler = Handler.remove(this.firstHandler, label2, label);
          } 
        } 
        label2 = label2.successor;
      } 
      handler = this.firstHandler;
      this.handlerCount = 0;
      while (handler != null) {
        this.handlerCount++;
        handler = handler.next;
      } 
      this.maxStack = i;
    } else if (this.compute == 1) {
      Handler handler = this.firstHandler;
      while (handler != null) {
        Label label1 = handler.start;
        Label label2 = handler.handler;
        Label label3 = handler.end;
        while (label1 != label3) {
          Edge edge = new Edge();
          edge.info = Integer.MAX_VALUE;
          edge.successor = label2;
          if ((label1.status & 0x80) == 0) {
            edge.next = label1.successors;
            label1.successors = edge;
          } else {
            edge.next = label1.successors.next.next;
            label1.successors.next.next = edge;
          } 
          label1 = label1.successor;
        } 
        handler = handler.next;
      } 
      if (this.subroutines > 0) {
        byte b = 0;
        this.labels.visitSubroutine(null, 1L, this.subroutines);
        Label label1 = this.labels;
        while (label1 != null) {
          if ((label1.status & 0x80) != 0) {
            Label label2 = label1.successors.next.successor;
            if ((label2.status & 0x400) == 0) {
              b++;
              label2.visitSubroutine(null, b / 32L << 32L | 1L << b % 32, this.subroutines);
            } 
          } 
          label1 = label1.successor;
        } 
        label1 = this.labels;
        while (label1 != null) {
          if ((label1.status & 0x80) != 0) {
            Label label2 = this.labels;
            while (label2 != null) {
              label2.status &= 0xFFFFF7FF;
              label2 = label2.successor;
            } 
            Label label3 = label1.successors.next.successor;
            label3.visitSubroutine(label1, 0L, this.subroutines);
          } 
          label1 = label1.successor;
        } 
      } 
      int i = 0;
      Label label = this.labels;
      while (label != null) {
        Label label1 = label;
        label = label.next;
        int j = label1.inputStackTop;
        int k = j + label1.outputStackMax;
        if (k > i)
          i = k; 
        Edge edge = label1.successors;
        if ((label1.status & 0x80) != 0)
          edge = edge.next; 
        while (edge != null) {
          label1 = edge.successor;
          if ((label1.status & 0x8) == 0) {
            label1.inputStackTop = (edge.info == Integer.MAX_VALUE) ? 1 : (j + edge.info);
            label1.status |= 0x8;
            label1.next = label;
            label = label1;
          } 
          edge = edge.next;
        } 
      } 
      this.maxStack = Math.max(paramInt1, i);
    } else {
      this.maxStack = paramInt1;
      this.maxLocals = paramInt2;
    } 
  }
  
  public void visitEnd() {}
  
  private void addSuccessor(int paramInt, Label paramLabel) {
    Edge edge = new Edge();
    edge.info = paramInt;
    edge.successor = paramLabel;
    edge.next = this.currentBlock.successors;
    this.currentBlock.successors = edge;
  }
  
  private void noSuccessor() {
    if (this.compute == 0) {
      Label label = new Label();
      label.frame = new Frame();
      label.frame.owner = label;
      label.resolve(this, this.code.length, this.code.data);
      this.previousBlock.successor = label;
      this.previousBlock = label;
    } else {
      this.currentBlock.outputStackMax = this.maxStackSize;
    } 
    this.currentBlock = null;
  }
  
  private void visitFrame(Frame paramFrame) {
    byte b2 = 0;
    int i = 0;
    byte b3 = 0;
    int[] arrayOfInt1 = paramFrame.inputLocals;
    int[] arrayOfInt2 = paramFrame.inputStack;
    byte b1;
    for (b1 = 0; b1 < arrayOfInt1.length; b1++) {
      int k = arrayOfInt1[b1];
      if (k == 16777216) {
        b2++;
      } else {
        i += b2 + 1;
        b2 = 0;
      } 
      if (k == 16777220 || k == 16777219)
        b1++; 
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      int k = arrayOfInt2[b1];
      b3++;
      if (k == 16777220 || k == 16777219)
        b1++; 
    } 
    int j = startFrame(paramFrame.owner.position, i, b3);
    for (b1 = 0; i > 0; b1++, i--) {
      int k = arrayOfInt1[b1];
      this.frame[j++] = k;
      if (k == 16777220 || k == 16777219)
        b1++; 
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      int k = arrayOfInt2[b1];
      this.frame[j++] = k;
      if (k == 16777220 || k == 16777219)
        b1++; 
    } 
    endFrame();
  }
  
  private void visitImplicitFirstFrame() {
    int i = startFrame(0, this.descriptor.length() + 1, 0);
    if ((this.access & 0x8) == 0)
      if ((this.access & 0x80000) == 0) {
        this.frame[i++] = 0x1700000 | this.cw.addType(this.cw.thisName);
      } else {
        this.frame[i++] = 6;
      }  
    byte b = 1;
    while (true) {
      byte b1 = b;
      switch (this.descriptor.charAt(b++)) {
        case 'B':
        case 'C':
        case 'I':
        case 'S':
        case 'Z':
          this.frame[i++] = 1;
          continue;
        case 'F':
          this.frame[i++] = 2;
          continue;
        case 'J':
          this.frame[i++] = 4;
          continue;
        case 'D':
          this.frame[i++] = 3;
          continue;
        case '[':
          while (this.descriptor.charAt(b) == '[')
            b++; 
          if (this.descriptor.charAt(b) == 'L') {
            b++;
            while (this.descriptor.charAt(b) != ';')
              b++; 
          } 
          this.frame[i++] = 0x1700000 | this.cw
            .addType(this.descriptor.substring(b1, ++b));
          continue;
        case 'L':
          while (this.descriptor.charAt(b) != ';')
            b++; 
          this.frame[i++] = 0x1700000 | this.cw
            .addType(this.descriptor.substring(b1 + 1, b++));
          continue;
      } 
      break;
    } 
    this.frame[1] = i - 3;
    endFrame();
  }
  
  private int startFrame(int paramInt1, int paramInt2, int paramInt3) {
    int i = 3 + paramInt2 + paramInt3;
    if (this.frame == null || this.frame.length < i)
      this.frame = new int[i]; 
    this.frame[0] = paramInt1;
    this.frame[1] = paramInt2;
    this.frame[2] = paramInt3;
    return 3;
  }
  
  private void endFrame() {
    if (this.previousFrame != null) {
      if (this.stackMap == null)
        this.stackMap = new ByteVector(); 
      writeFrame();
      this.frameCount++;
    } 
    this.previousFrame = this.frame;
    this.frame = null;
  }
  
  private void writeFrame() {
    int n, i = this.frame[1];
    int j = this.frame[2];
    if ((this.cw.version & 0xFFFF) < 50) {
      this.stackMap.putShort(this.frame[0]).putShort(i);
      writeFrameTypes(3, 3 + i);
      this.stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
      return;
    } 
    int k = this.previousFrame[1];
    char c = 'ÿ';
    int m = 0;
    if (this.frameCount == 0) {
      n = this.frame[0];
    } else {
      n = this.frame[0] - this.previousFrame[0] - 1;
    } 
    if (j == 0) {
      m = i - k;
      switch (m) {
        case -3:
        case -2:
        case -1:
          c = 'ø';
          k = i;
          break;
        case 0:
          c = (n < 64) ? Character.MIN_VALUE : 'û';
          break;
        case 1:
        case 2:
        case 3:
          c = 'ü';
          break;
      } 
    } else if (i == k && j == 1) {
      c = (n < 63) ? '@' : '÷';
    } 
    if (c != 'ÿ') {
      byte b1 = 3;
      for (byte b2 = 0; b2 < k; b2++) {
        if (this.frame[b1] != this.previousFrame[b1]) {
          c = 'ÿ';
          break;
        } 
        b1++;
      } 
    } 
    switch (c) {
      case '\000':
        this.stackMap.putByte(n);
        return;
      case '@':
        this.stackMap.putByte(64 + n);
        writeFrameTypes(3 + i, 4 + i);
        return;
      case '÷':
        this.stackMap.putByte(247).putShort(n);
        writeFrameTypes(3 + i, 4 + i);
        return;
      case 'û':
        this.stackMap.putByte(251).putShort(n);
        return;
      case 'ø':
        this.stackMap.putByte(251 + m).putShort(n);
        return;
      case 'ü':
        this.stackMap.putByte(251 + m).putShort(n);
        writeFrameTypes(3 + k, 3 + i);
        return;
    } 
    this.stackMap.putByte(255).putShort(n).putShort(i);
    writeFrameTypes(3, 3 + i);
    this.stackMap.putShort(j);
    writeFrameTypes(3 + i, 3 + i + j);
  }
  
  private void writeFrameTypes(int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      int j = this.frame[i];
      int k = j & 0xF0000000;
      if (k == 0) {
        int m = j & 0xFFFFF;
        switch (j & 0xFF00000) {
          case 24117248:
            this.stackMap.putByte(7).putShort(this.cw
                .newClass((this.cw.typeTable[m]).strVal1));
            break;
          case 25165824:
            this.stackMap.putByte(8).putShort((this.cw.typeTable[m]).intVal);
            break;
          default:
            this.stackMap.putByte(m);
            break;
        } 
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        k >>= 28;
        while (k-- > 0)
          stringBuilder.append('['); 
        if ((j & 0xFF00000) == 24117248) {
          stringBuilder.append('L');
          stringBuilder.append((this.cw.typeTable[j & 0xFFFFF]).strVal1);
          stringBuilder.append(';');
        } else {
          switch (j & 0xF) {
            case 1:
              stringBuilder.append('I');
              break;
            case 2:
              stringBuilder.append('F');
              break;
            case 3:
              stringBuilder.append('D');
              break;
            case 9:
              stringBuilder.append('Z');
              break;
            case 10:
              stringBuilder.append('B');
              break;
            case 11:
              stringBuilder.append('C');
              break;
            case 12:
              stringBuilder.append('S');
              break;
            default:
              stringBuilder.append('J');
              break;
          } 
        } 
        this.stackMap.putByte(7).putShort(this.cw.newClass(stringBuilder.toString()));
      } 
    } 
  }
  
  private void writeFrameType(Object paramObject) {
    if (paramObject instanceof String) {
      this.stackMap.putByte(7).putShort(this.cw.newClass((String)paramObject));
    } else if (paramObject instanceof Integer) {
      this.stackMap.putByte(((Integer)paramObject).intValue());
    } else {
      this.stackMap.putByte(8).putShort(((Label)paramObject).position);
    } 
  }
  
  final int getSize() {
    if (this.classReaderOffset != 0)
      return 6 + this.classReaderLength; 
    int i = 8;
    if (this.code.length > 0) {
      if (this.code.length > 65536)
        throw new RuntimeException("Method code too large!"); 
      this.cw.newUTF8("Code");
      i += 18 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null) {
        this.cw.newUTF8("LocalVariableTable");
        i += 8 + this.localVar.length;
      } 
      if (this.localVarType != null) {
        this.cw.newUTF8("LocalVariableTypeTable");
        i += 8 + this.localVarType.length;
      } 
      if (this.lineNumber != null) {
        this.cw.newUTF8("LineNumberTable");
        i += 8 + this.lineNumber.length;
      } 
      if (this.stackMap != null) {
        boolean bool = ((this.cw.version & 0xFFFF) >= 50) ? true : false;
        this.cw.newUTF8(bool ? "StackMapTable" : "StackMap");
        i += 8 + this.stackMap.length;
      } 
      if (this.ctanns != null) {
        this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
        i += 8 + this.ctanns.getSize();
      } 
      if (this.ictanns != null) {
        this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
        i += 8 + this.ictanns.getSize();
      } 
      if (this.cattrs != null)
        i += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals); 
    } 
    if (this.exceptionCount > 0) {
      this.cw.newUTF8("Exceptions");
      i += 8 + 2 * this.exceptionCount;
    } 
    if ((this.access & 0x1000) != 0 && ((
      this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
      this.cw.newUTF8("Synthetic");
      i += 6;
    } 
    if ((this.access & 0x20000) != 0) {
      this.cw.newUTF8("Deprecated");
      i += 6;
    } 
    if (this.signature != null) {
      this.cw.newUTF8("Signature");
      this.cw.newUTF8(this.signature);
      i += 8;
    } 
    if (this.methodParameters != null) {
      this.cw.newUTF8("MethodParameters");
      i += 7 + this.methodParameters.length;
    } 
    if (this.annd != null) {
      this.cw.newUTF8("AnnotationDefault");
      i += 6 + this.annd.length;
    } 
    if (this.anns != null) {
      this.cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + this.anns.getSize();
    } 
    if (this.ianns != null) {
      this.cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + this.ianns.getSize();
    } 
    if (this.tanns != null) {
      this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
      i += 8 + this.tanns.getSize();
    } 
    if (this.itanns != null) {
      this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
      i += 8 + this.itanns.getSize();
    } 
    if (this.panns != null) {
      this.cw.newUTF8("RuntimeVisibleParameterAnnotations");
      i += 7 + 2 * (this.panns.length - this.synthetics);
      for (int j = this.panns.length - 1; j >= this.synthetics; j--)
        i += (this.panns[j] == null) ? 0 : this.panns[j].getSize(); 
    } 
    if (this.ipanns != null) {
      this.cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      i += 7 + 2 * (this.ipanns.length - this.synthetics);
      for (int j = this.ipanns.length - 1; j >= this.synthetics; j--)
        i += (this.ipanns[j] == null) ? 0 : this.ipanns[j].getSize(); 
    } 
    if (this.attrs != null)
      i += this.attrs.getSize(this.cw, null, 0, -1, -1); 
    return i;
  }
  
  final void put(ByteVector paramByteVector) {
    int i = 0xE0000 | (this.access & 0x40000) / 64;
    paramByteVector.putShort(this.access & (i ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.desc);
    if (this.classReaderOffset != 0) {
      paramByteVector.putByteArray(this.cw.cr.b, this.classReaderOffset, this.classReaderLength);
      return;
    } 
    int j = 0;
    if (this.code.length > 0)
      j++; 
    if (this.exceptionCount > 0)
      j++; 
    if ((this.access & 0x1000) != 0 && ((
      this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0))
      j++; 
    if ((this.access & 0x20000) != 0)
      j++; 
    if (this.signature != null)
      j++; 
    if (this.methodParameters != null)
      j++; 
    if (this.annd != null)
      j++; 
    if (this.anns != null)
      j++; 
    if (this.ianns != null)
      j++; 
    if (this.tanns != null)
      j++; 
    if (this.itanns != null)
      j++; 
    if (this.panns != null)
      j++; 
    if (this.ipanns != null)
      j++; 
    if (this.attrs != null)
      j += this.attrs.getCount(); 
    paramByteVector.putShort(j);
    if (this.code.length > 0) {
      int k = 12 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null)
        k += 8 + this.localVar.length; 
      if (this.localVarType != null)
        k += 8 + this.localVarType.length; 
      if (this.lineNumber != null)
        k += 8 + this.lineNumber.length; 
      if (this.stackMap != null)
        k += 8 + this.stackMap.length; 
      if (this.ctanns != null)
        k += 8 + this.ctanns.getSize(); 
      if (this.ictanns != null)
        k += 8 + this.ictanns.getSize(); 
      if (this.cattrs != null)
        k += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals); 
      paramByteVector.putShort(this.cw.newUTF8("Code")).putInt(k);
      paramByteVector.putShort(this.maxStack).putShort(this.maxLocals);
      paramByteVector.putInt(this.code.length).putByteArray(this.code.data, 0, this.code.length);
      paramByteVector.putShort(this.handlerCount);
      if (this.handlerCount > 0) {
        Handler handler = this.firstHandler;
        while (handler != null) {
          paramByteVector.putShort(handler.start.position).putShort(handler.end.position)
            .putShort(handler.handler.position).putShort(handler.type);
          handler = handler.next;
        } 
      } 
      j = 0;
      if (this.localVar != null)
        j++; 
      if (this.localVarType != null)
        j++; 
      if (this.lineNumber != null)
        j++; 
      if (this.stackMap != null)
        j++; 
      if (this.ctanns != null)
        j++; 
      if (this.ictanns != null)
        j++; 
      if (this.cattrs != null)
        j += this.cattrs.getCount(); 
      paramByteVector.putShort(j);
      if (this.localVar != null) {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTable"));
        paramByteVector.putInt(this.localVar.length + 2).putShort(this.localVarCount);
        paramByteVector.putByteArray(this.localVar.data, 0, this.localVar.length);
      } 
      if (this.localVarType != null) {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTypeTable"));
        paramByteVector.putInt(this.localVarType.length + 2).putShort(this.localVarTypeCount);
        paramByteVector.putByteArray(this.localVarType.data, 0, this.localVarType.length);
      } 
      if (this.lineNumber != null) {
        paramByteVector.putShort(this.cw.newUTF8("LineNumberTable"));
        paramByteVector.putInt(this.lineNumber.length + 2).putShort(this.lineNumberCount);
        paramByteVector.putByteArray(this.lineNumber.data, 0, this.lineNumber.length);
      } 
      if (this.stackMap != null) {
        boolean bool = ((this.cw.version & 0xFFFF) >= 50) ? true : false;
        paramByteVector.putShort(this.cw.newUTF8(bool ? "StackMapTable" : "StackMap"));
        paramByteVector.putInt(this.stackMap.length + 2).putShort(this.frameCount);
        paramByteVector.putByteArray(this.stackMap.data, 0, this.stackMap.length);
      } 
      if (this.ctanns != null) {
        paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
        this.ctanns.put(paramByteVector);
      } 
      if (this.ictanns != null) {
        paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
        this.ictanns.put(paramByteVector);
      } 
      if (this.cattrs != null)
        this.cattrs.put(this.cw, this.code.data, this.code.length, this.maxLocals, this.maxStack, paramByteVector); 
    } 
    if (this.exceptionCount > 0) {
      paramByteVector.putShort(this.cw.newUTF8("Exceptions")).putInt(2 * this.exceptionCount + 2);
      paramByteVector.putShort(this.exceptionCount);
      for (byte b = 0; b < this.exceptionCount; b++)
        paramByteVector.putShort(this.exceptions[b]); 
    } 
    if ((this.access & 0x1000) != 0 && ((
      this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0))
      paramByteVector.putShort(this.cw.newUTF8("Synthetic")).putInt(0); 
    if ((this.access & 0x20000) != 0)
      paramByteVector.putShort(this.cw.newUTF8("Deprecated")).putInt(0); 
    if (this.signature != null)
      paramByteVector.putShort(this.cw.newUTF8("Signature")).putInt(2)
        .putShort(this.cw.newUTF8(this.signature)); 
    if (this.methodParameters != null) {
      paramByteVector.putShort(this.cw.newUTF8("MethodParameters"));
      paramByteVector.putInt(this.methodParameters.length + 1).putByte(this.methodParametersCount);
      paramByteVector.putByteArray(this.methodParameters.data, 0, this.methodParameters.length);
    } 
    if (this.annd != null) {
      paramByteVector.putShort(this.cw.newUTF8("AnnotationDefault"));
      paramByteVector.putInt(this.annd.length);
      paramByteVector.putByteArray(this.annd.data, 0, this.annd.length);
    } 
    if (this.anns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(paramByteVector);
    } 
    if (this.ianns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(paramByteVector);
    } 
    if (this.tanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
      this.tanns.put(paramByteVector);
    } 
    if (this.itanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.itanns.put(paramByteVector);
    } 
    if (this.panns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      AnnotationWriter.put(this.panns, this.synthetics, paramByteVector);
    } 
    if (this.ipanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      AnnotationWriter.put(this.ipanns, this.synthetics, paramByteVector);
    } 
    if (this.attrs != null)
      this.attrs.put(this.cw, null, 0, -1, -1, paramByteVector); 
  }
  
  private void resizeInstructions() {
    byte[] arrayOfByte = this.code.data;
    int[] arrayOfInt1 = new int[0];
    int[] arrayOfInt2 = new int[0];
    boolean[] arrayOfBoolean = new boolean[this.code.length];
    byte b = 3;
    do {
      if (b == 3)
        b = 2; 
      int k = 0;
      while (k < arrayOfByte.length) {
        int m, n, i1 = arrayOfByte[k] & 0xFF;
        int i2 = 0;
        switch (ClassWriter.TYPE[i1]) {
          case 0:
          case 4:
            k++;
            break;
          case 9:
            if (i1 > 201) {
              i1 = (i1 < 218) ? (i1 - 49) : (i1 - 20);
              m = k + readUnsignedShort(arrayOfByte, k + 1);
            } else {
              m = k + readShort(arrayOfByte, k + 1);
            } 
            n = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            if (n < -32768 || n > 32767)
              if (!arrayOfBoolean[k]) {
                if (i1 == 167 || i1 == 168) {
                  i2 = 2;
                } else {
                  i2 = 5;
                } 
                arrayOfBoolean[k] = true;
              }  
            k += 3;
            break;
          case 10:
            k += 5;
            break;
          case 14:
            if (b == 1) {
              n = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
              i2 = -(n & 0x3);
            } else if (!arrayOfBoolean[k]) {
              i2 = k & 0x3;
              arrayOfBoolean[k] = true;
            } 
            k = k + 4 - (k & 0x3);
            k += 4 * (readInt(arrayOfByte, k + 8) - readInt(arrayOfByte, k + 4) + 1) + 12;
            break;
          case 15:
            if (b == 1) {
              n = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
              i2 = -(n & 0x3);
            } else if (!arrayOfBoolean[k]) {
              i2 = k & 0x3;
              arrayOfBoolean[k] = true;
            } 
            k = k + 4 - (k & 0x3);
            k += 8 * readInt(arrayOfByte, k + 4) + 8;
            break;
          case 17:
            i1 = arrayOfByte[k + 1] & 0xFF;
            if (i1 == 132) {
              k += 6;
              break;
            } 
            k += 4;
            break;
          case 1:
          case 3:
          case 11:
            k += 2;
            break;
          case 2:
          case 5:
          case 6:
          case 12:
          case 13:
            k += 3;
            break;
          case 7:
          case 8:
            k += 5;
            break;
          default:
            k += 4;
            break;
        } 
        if (i2 != 0) {
          int[] arrayOfInt3 = new int[arrayOfInt1.length + 1];
          int[] arrayOfInt4 = new int[arrayOfInt2.length + 1];
          System.arraycopy(arrayOfInt1, 0, arrayOfInt3, 0, arrayOfInt1.length);
          System.arraycopy(arrayOfInt2, 0, arrayOfInt4, 0, arrayOfInt2.length);
          arrayOfInt3[arrayOfInt1.length] = k;
          arrayOfInt4[arrayOfInt2.length] = i2;
          arrayOfInt1 = arrayOfInt3;
          arrayOfInt2 = arrayOfInt4;
          if (i2 > 0)
            b = 3; 
        } 
      } 
      if (b >= 3)
        continue; 
      b--;
    } while (b != 0);
    ByteVector byteVector = new ByteVector(this.code.length);
    int i = 0;
    while (i < this.code.length) {
      int k, m, n, i1, i2 = arrayOfByte[i] & 0xFF;
      switch (ClassWriter.TYPE[i2]) {
        case 0:
        case 4:
          byteVector.putByte(i2);
          i++;
          continue;
        case 9:
          if (i2 > 201) {
            i2 = (i2 < 218) ? (i2 - 49) : (i2 - 20);
            m = i + readUnsignedShort(arrayOfByte, i + 1);
          } else {
            m = i + readShort(arrayOfByte, i + 1);
          } 
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, i, m);
          if (arrayOfBoolean[i]) {
            if (i2 == 167) {
              byteVector.putByte(200);
            } else if (i2 == 168) {
              byteVector.putByte(201);
            } else {
              byteVector.putByte((i2 <= 166) ? ((i2 + 1 ^ 0x1) - 1) : (i2 ^ 0x1));
              byteVector.putShort(8);
              byteVector.putByte(200);
              i1 -= 3;
            } 
            byteVector.putInt(i1);
          } else {
            byteVector.putByte(i2);
            byteVector.putShort(i1);
          } 
          i += 3;
          continue;
        case 10:
          m = i + readInt(arrayOfByte, i + 1);
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, i, m);
          byteVector.putByte(i2);
          byteVector.putInt(i1);
          i += 5;
          continue;
        case 14:
          k = i;
          i = i + 4 - (k & 0x3);
          byteVector.putByte(170);
          byteVector.putByteArray(null, 0, (4 - byteVector.length % 4) % 4);
          m = k + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
          byteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          byteVector.putInt(n);
          n = readInt(arrayOfByte, i) - n + 1;
          i += 4;
          byteVector.putInt(readInt(arrayOfByte, i - 4));
          for (; n > 0; n--) {
            m = k + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            byteVector.putInt(i1);
          } 
          continue;
        case 15:
          k = i;
          i = i + 4 - (k & 0x3);
          byteVector.putByte(171);
          byteVector.putByteArray(null, 0, (4 - byteVector.length % 4) % 4);
          m = k + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
          byteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          byteVector.putInt(n);
          for (; n > 0; n--) {
            byteVector.putInt(readInt(arrayOfByte, i));
            i += 4;
            m = k + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            byteVector.putInt(i1);
          } 
          continue;
        case 17:
          i2 = arrayOfByte[i + 1] & 0xFF;
          if (i2 == 132) {
            byteVector.putByteArray(arrayOfByte, i, 6);
            i += 6;
            continue;
          } 
          byteVector.putByteArray(arrayOfByte, i, 4);
          i += 4;
          continue;
        case 1:
        case 3:
        case 11:
          byteVector.putByteArray(arrayOfByte, i, 2);
          i += 2;
          continue;
        case 2:
        case 5:
        case 6:
        case 12:
        case 13:
          byteVector.putByteArray(arrayOfByte, i, 3);
          i += 3;
          continue;
        case 7:
        case 8:
          byteVector.putByteArray(arrayOfByte, i, 5);
          i += 5;
          continue;
      } 
      byteVector.putByteArray(arrayOfByte, i, 4);
      i += 4;
    } 
    if (this.compute == 0) {
      Label label = this.labels;
      while (label != null) {
        i = label.position - 3;
        if (i >= 0 && arrayOfBoolean[i])
          label.status |= 0x10; 
        getNewOffset(arrayOfInt1, arrayOfInt2, label);
        label = label.successor;
      } 
      for (byte b1 = 0; b1 < this.cw.typeTable.length; b1++) {
        Item item = this.cw.typeTable[b1];
        if (item != null && item.type == 31)
          item.intVal = getNewOffset(arrayOfInt1, arrayOfInt2, 0, item.intVal); 
      } 
    } else if (this.frameCount > 0) {
      this.cw.invalidFrames = true;
    } 
    Handler handler = this.firstHandler;
    while (handler != null) {
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.start);
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.end);
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.handler);
      handler = handler.next;
    } 
    int j;
    for (j = 0; j < 2; j++) {
      ByteVector byteVector1 = (j == 0) ? this.localVar : this.localVarType;
      if (byteVector1 != null) {
        arrayOfByte = byteVector1.data;
        i = 0;
        while (i < byteVector1.length) {
          int k = readUnsignedShort(arrayOfByte, i);
          int m = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
          writeShort(arrayOfByte, i, m);
          k += readUnsignedShort(arrayOfByte, i + 2);
          m = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k) - m;
          writeShort(arrayOfByte, i + 2, m);
          i += 10;
        } 
      } 
    } 
    if (this.lineNumber != null) {
      arrayOfByte = this.lineNumber.data;
      i = 0;
      while (i < this.lineNumber.length) {
        writeShort(arrayOfByte, i, 
            
            getNewOffset(arrayOfInt1, arrayOfInt2, 0, 
              readUnsignedShort(arrayOfByte, i)));
        i += 4;
      } 
    } 
    Attribute attribute = this.cattrs;
    while (attribute != null) {
      Label[] arrayOfLabel = attribute.getLabels();
      if (arrayOfLabel != null)
        for (j = arrayOfLabel.length - 1; j >= 0; j--)
          getNewOffset(arrayOfInt1, arrayOfInt2, arrayOfLabel[j]);  
      attribute = attribute.next;
    } 
    this.code = byteVector;
  }
  
  static int readUnsignedShort(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF;
  }
  
  static short readShort(byte[] paramArrayOfbyte, int paramInt) {
    return (short)((paramArrayOfbyte[paramInt] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF);
  }
  
  static int readInt(byte[] paramArrayOfbyte, int paramInt) {
    return (paramArrayOfbyte[paramInt] & 0xFF) << 24 | (paramArrayOfbyte[paramInt + 1] & 0xFF) << 16 | (paramArrayOfbyte[paramInt + 2] & 0xFF) << 8 | paramArrayOfbyte[paramInt + 3] & 0xFF;
  }
  
  static void writeShort(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    paramArrayOfbyte[paramInt1] = (byte)(paramInt2 >>> 8);
    paramArrayOfbyte[paramInt1 + 1] = (byte)paramInt2;
  }
  
  static int getNewOffset(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    for (byte b = 0; b < paramArrayOfint1.length; b++) {
      if (paramInt1 < paramArrayOfint1[b] && paramArrayOfint1[b] <= paramInt2) {
        i += paramArrayOfint2[b];
      } else if (paramInt2 < paramArrayOfint1[b] && paramArrayOfint1[b] <= paramInt1) {
        i -= paramArrayOfint2[b];
      } 
    } 
    return i;
  }
  
  static void getNewOffset(int[] paramArrayOfint1, int[] paramArrayOfint2, Label paramLabel) {
    if ((paramLabel.status & 0x4) == 0) {
      paramLabel.position = getNewOffset(paramArrayOfint1, paramArrayOfint2, 0, paramLabel.position);
      paramLabel.status |= 0x4;
    } 
  }
}
