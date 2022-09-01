package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public final class Tables {
  public static <R, C, V> Table.Cell<R, C, V> immutableCell(@Nullable R rowKey, @Nullable C columnKey, @Nullable V value) {
    return new ImmutableCell<R, C, V>(rowKey, columnKey, value);
  }
  
  static final class ImmutableCell<R, C, V> extends AbstractCell<R, C, V> implements Serializable {
    private final R rowKey;
    
    private final C columnKey;
    
    private final V value;
    
    private static final long serialVersionUID = 0L;
    
    ImmutableCell(@Nullable R rowKey, @Nullable C columnKey, @Nullable V value) {
      this.rowKey = rowKey;
      this.columnKey = columnKey;
      this.value = value;
    }
    
    public R getRowKey() {
      return this.rowKey;
    }
    
    public C getColumnKey() {
      return this.columnKey;
    }
    
    public V getValue() {
      return this.value;
    }
  }
  
  static abstract class AbstractCell<R, C, V> implements Table.Cell<R, C, V> {
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (obj instanceof Table.Cell) {
        Table.Cell<?, ?, ?> other = (Table.Cell<?, ?, ?>)obj;
        return (Objects.equal(getRowKey(), other.getRowKey()) && Objects.equal(getColumnKey(), other.getColumnKey()) && Objects.equal(getValue(), other.getValue()));
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { getRowKey(), getColumnKey(), getValue() });
    }
    
    public String toString() {
      return "(" + getRowKey() + "," + getColumnKey() + ")=" + getValue();
    }
  }
  
  public static <R, C, V> Table<C, R, V> transpose(Table<R, C, V> table) {
    return (table instanceof TransposeTable) ? ((TransposeTable)table).original : (Table<C, R, V>)new TransposeTable(table);
  }
  
  @Beta
  public static <R, C, V> Table<R, C, V> newCustomTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
    Preconditions.checkArgument(backingMap.isEmpty());
    Preconditions.checkNotNull(factory);
    return new StandardTable<R, C, V>(backingMap, factory);
  }
  
  @Beta
  public static <R, C, V1, V2> Table<R, C, V2> transformValues(Table<R, C, V1> fromTable, Function<? super V1, V2> function) {
    return (Table<R, C, V2>)new TransformedTable(fromTable, function);
  }
  
  public static <R, C, V> Table<R, C, V> unmodifiableTable(Table<? extends R, ? extends C, ? extends V> table) {
    return (Table<R, C, V>)new UnmodifiableTable(table);
  }
  
  @Beta
  public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(RowSortedTable<R, ? extends C, ? extends V> table) {
    return (RowSortedTable<R, C, V>)new UnmodifiableRowSortedMap(table);
  }
  
  private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper() {
    return (Function)UNMODIFIABLE_WRAPPER;
  }
  
  private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER = new Function<Map<Object, Object>, Map<Object, Object>>() {
      public Map<Object, Object> apply(Map<Object, Object> input) {
        return Collections.unmodifiableMap(input);
      }
    };
  
  static boolean equalsImpl(Table<?, ?, ?> table, @Nullable Object obj) {
    if (obj == table)
      return true; 
    if (obj instanceof Table) {
      Table<?, ?, ?> that = (Table<?, ?, ?>)obj;
      return table.cellSet().equals(that.cellSet());
    } 
    return false;
  }
  
  static final class Tables {}
  
  private static class Tables {}
  
  private static class Tables {}
  
  private static class Tables {}
}
