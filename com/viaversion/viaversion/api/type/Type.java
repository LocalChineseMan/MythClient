package com.viaversion.viaversion.api.type;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.EulerAngle;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.types.ArrayType;
import com.viaversion.viaversion.api.type.types.BooleanType;
import com.viaversion.viaversion.api.type.types.ByteArrayType;
import com.viaversion.viaversion.api.type.types.ByteType;
import com.viaversion.viaversion.api.type.types.ComponentType;
import com.viaversion.viaversion.api.type.types.DoubleType;
import com.viaversion.viaversion.api.type.types.FloatType;
import com.viaversion.viaversion.api.type.types.IntType;
import com.viaversion.viaversion.api.type.types.LongArrayType;
import com.viaversion.viaversion.api.type.types.LongType;
import com.viaversion.viaversion.api.type.types.RemainingBytesType;
import com.viaversion.viaversion.api.type.types.ShortByteArrayType;
import com.viaversion.viaversion.api.type.types.ShortType;
import com.viaversion.viaversion.api.type.types.StringType;
import com.viaversion.viaversion.api.type.types.UUIDIntArrayType;
import com.viaversion.viaversion.api.type.types.UUIDType;
import com.viaversion.viaversion.api.type.types.UnsignedByteType;
import com.viaversion.viaversion.api.type.types.UnsignedShortType;
import com.viaversion.viaversion.api.type.types.VarIntArrayType;
import com.viaversion.viaversion.api.type.types.VarIntType;
import com.viaversion.viaversion.api.type.types.VarLongType;
import com.viaversion.viaversion.api.type.types.VoidType;
import com.viaversion.viaversion.api.type.types.minecraft.BlockChangeRecordType;
import com.viaversion.viaversion.api.type.types.minecraft.EulerAngleType;
import com.viaversion.viaversion.api.type.types.minecraft.FlatItemArrayType;
import com.viaversion.viaversion.api.type.types.minecraft.FlatItemType;
import com.viaversion.viaversion.api.type.types.minecraft.FlatVarIntItemArrayType;
import com.viaversion.viaversion.api.type.types.minecraft.FlatVarIntItemType;
import com.viaversion.viaversion.api.type.types.minecraft.ItemArrayType;
import com.viaversion.viaversion.api.type.types.minecraft.ItemType;
import com.viaversion.viaversion.api.type.types.minecraft.NBTType;
import com.viaversion.viaversion.api.type.types.minecraft.OptPosition1_14Type;
import com.viaversion.viaversion.api.type.types.minecraft.OptPositionType;
import com.viaversion.viaversion.api.type.types.minecraft.OptUUIDType;
import com.viaversion.viaversion.api.type.types.minecraft.OptionalComponentType;
import com.viaversion.viaversion.api.type.types.minecraft.OptionalVarIntType;
import com.viaversion.viaversion.api.type.types.minecraft.Position1_14Type;
import com.viaversion.viaversion.api.type.types.minecraft.PositionType;
import com.viaversion.viaversion.api.type.types.minecraft.VarLongBlockChangeRecordType;
import com.viaversion.viaversion.api.type.types.minecraft.VectorType;
import com.viaversion.viaversion.api.type.types.minecraft.VillagerDataType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import java.util.UUID;

public abstract class Type<T> implements ByteBufReader<T>, ByteBufWriter<T> {
  public static final ByteType BYTE = new ByteType();
  
  public static final UnsignedByteType UNSIGNED_BYTE = new UnsignedByteType();
  
  public static final Type<byte[]> BYTE_ARRAY_PRIMITIVE = (Type<byte[]>)new ByteArrayType();
  
  public static final Type<byte[]> SHORT_BYTE_ARRAY = (Type<byte[]>)new ShortByteArrayType();
  
  public static final Type<byte[]> REMAINING_BYTES = (Type<byte[]>)new RemainingBytesType();
  
  public static final ShortType SHORT = new ShortType();
  
  public static final UnsignedShortType UNSIGNED_SHORT = new UnsignedShortType();
  
  public static final IntType INT = new IntType();
  
  public static final FloatType FLOAT = new FloatType();
  
  public static final DoubleType DOUBLE = new DoubleType();
  
  public static final LongType LONG = new LongType();
  
  public static final Type<long[]> LONG_ARRAY_PRIMITIVE = (Type<long[]>)new LongArrayType();
  
  public static final BooleanType BOOLEAN = new BooleanType();
  
  public static final Type<JsonElement> COMPONENT = (Type<JsonElement>)new ComponentType();
  
  public static final Type<JsonElement> OPTIONAL_COMPONENT = (Type<JsonElement>)new OptionalComponentType();
  
  public static final Type<String> STRING = (Type<String>)new StringType();
  
  public static final Type<String[]> STRING_ARRAY = (Type<String[]>)new ArrayType(STRING);
  
  public static final Type<UUID> UUID = (Type<UUID>)new UUIDType();
  
  public static final Type<UUID> OPTIONAL_UUID = (Type<UUID>)new OptUUIDType();
  
  public static final Type<UUID> UUID_INT_ARRAY = (Type<UUID>)new UUIDIntArrayType();
  
  public static final Type<UUID[]> UUID_ARRAY = (Type<UUID[]>)new ArrayType(UUID);
  
  public static final VarIntType VAR_INT = new VarIntType();
  
  public static final OptionalVarIntType OPTIONAL_VAR_INT = new OptionalVarIntType();
  
  public static final Type<int[]> VAR_INT_ARRAY_PRIMITIVE = (Type<int[]>)new VarIntArrayType();
  
  public static final VarLongType VAR_LONG = new VarLongType();
  
  @Deprecated
  public static final Type<Byte[]> BYTE_ARRAY = (Type<Byte[]>)new ArrayType((Type)BYTE);
  
  @Deprecated
  public static final Type<Short[]> UNSIGNED_BYTE_ARRAY = (Type<Short[]>)new ArrayType((Type)UNSIGNED_BYTE);
  
  @Deprecated
  public static final Type<Boolean[]> BOOLEAN_ARRAY = (Type<Boolean[]>)new ArrayType((Type)BOOLEAN);
  
  @Deprecated
  public static final Type<Integer[]> INT_ARRAY = (Type<Integer[]>)new ArrayType((Type)INT);
  
  @Deprecated
  public static final Type<Short[]> SHORT_ARRAY = (Type<Short[]>)new ArrayType((Type)SHORT);
  
  @Deprecated
  public static final Type<Integer[]> UNSIGNED_SHORT_ARRAY = (Type<Integer[]>)new ArrayType((Type)UNSIGNED_SHORT);
  
  @Deprecated
  public static final Type<Double[]> DOUBLE_ARRAY = (Type<Double[]>)new ArrayType((Type)DOUBLE);
  
  @Deprecated
  public static final Type<Long[]> LONG_ARRAY = (Type<Long[]>)new ArrayType((Type)LONG);
  
  @Deprecated
  public static final Type<Float[]> FLOAT_ARRAY = (Type<Float[]>)new ArrayType((Type)FLOAT);
  
  @Deprecated
  public static final Type<Integer[]> VAR_INT_ARRAY = (Type<Integer[]>)new ArrayType((Type)VAR_INT);
  
  @Deprecated
  public static final Type<Long[]> VAR_LONG_ARRAY = (Type<Long[]>)new ArrayType((Type)VAR_LONG);
  
  public static final VoidType NOTHING = new VoidType();
  
  public static final Type<Position> POSITION = (Type<Position>)new PositionType();
  
  public static final Type<Position> POSITION1_14 = (Type<Position>)new Position1_14Type();
  
  public static final Type<EulerAngle> ROTATION = (Type<EulerAngle>)new EulerAngleType();
  
  public static final Type<Vector> VECTOR = (Type<Vector>)new VectorType();
  
  public static final Type<CompoundTag> NBT = (Type<CompoundTag>)new NBTType();
  
  public static final Type<CompoundTag[]> NBT_ARRAY = (Type<CompoundTag[]>)new ArrayType(NBT);
  
  public static final Type<Position> OPTIONAL_POSITION = (Type<Position>)new OptPositionType();
  
  public static final Type<Position> OPTIONAL_POSITION_1_14 = (Type<Position>)new OptPosition1_14Type();
  
  public static final Type<BlockChangeRecord> BLOCK_CHANGE_RECORD = (Type<BlockChangeRecord>)new BlockChangeRecordType();
  
  public static final Type<BlockChangeRecord[]> BLOCK_CHANGE_RECORD_ARRAY = (Type<BlockChangeRecord[]>)new ArrayType(BLOCK_CHANGE_RECORD);
  
  public static final Type<BlockChangeRecord> VAR_LONG_BLOCK_CHANGE_RECORD = (Type<BlockChangeRecord>)new VarLongBlockChangeRecordType();
  
  public static final Type<BlockChangeRecord[]> VAR_LONG_BLOCK_CHANGE_RECORD_ARRAY = (Type<BlockChangeRecord[]>)new ArrayType(VAR_LONG_BLOCK_CHANGE_RECORD);
  
  public static final Type<VillagerData> VILLAGER_DATA = (Type<VillagerData>)new VillagerDataType();
  
  public static final Type<Item> ITEM = (Type<Item>)new ItemType();
  
  public static final Type<Item[]> ITEM_ARRAY = (Type<Item[]>)new ItemArrayType();
  
  public static final Type<Item> FLAT_ITEM = (Type<Item>)new FlatItemType();
  
  public static final Type<Item> FLAT_VAR_INT_ITEM = (Type<Item>)new FlatVarIntItemType();
  
  public static final Type<Item[]> FLAT_ITEM_ARRAY = (Type<Item[]>)new FlatItemArrayType();
  
  public static final Type<Item[]> FLAT_VAR_INT_ITEM_ARRAY = (Type<Item[]>)new FlatVarIntItemArrayType();
  
  public static final Type<Item[]> FLAT_ITEM_ARRAY_VAR_INT = (Type<Item[]>)new ArrayType(FLAT_ITEM);
  
  public static final Type<Item[]> FLAT_VAR_INT_ITEM_ARRAY_VAR_INT = (Type<Item[]>)new ArrayType(FLAT_VAR_INT_ITEM);
  
  private final Class<? super T> outputClass;
  
  private final String typeName;
  
  protected Type(Class<? super T> outputClass) {
    this(outputClass.getSimpleName(), outputClass);
  }
  
  protected Type(String typeName, Class<? super T> outputClass) {
    this.outputClass = outputClass;
    this.typeName = typeName;
  }
  
  public Class<? super T> getOutputClass() {
    return this.outputClass;
  }
  
  public String getTypeName() {
    return this.typeName;
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)getClass();
  }
  
  public String toString() {
    return "Type|" + this.typeName;
  }
}
