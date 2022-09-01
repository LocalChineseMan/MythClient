package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
class StandardTable<R, C, V> extends AbstractTable<R, C, V> implements Serializable {
  @GwtTransient
  final Map<R, Map<C, V>> backingMap;
  
  @GwtTransient
  final Supplier<? extends Map<C, V>> factory;
  
  private transient Set<C> columnKeySet;
  
  private transient Map<R, Map<C, V>> rowMap;
  
  private transient ColumnMap columnMap;
  
  private static final long serialVersionUID = 0L;
  
  StandardTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
    this.backingMap = backingMap;
    this.factory = factory;
  }
  
  public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
    return (rowKey != null && columnKey != null && super.contains(rowKey, columnKey));
  }
  
  public boolean containsColumn(@Nullable Object columnKey) {
    if (columnKey == null)
      return false; 
    for (Map<C, V> map : this.backingMap.values()) {
      if (Maps.safeContainsKey(map, columnKey))
        return true; 
    } 
    return false;
  }
  
  public boolean containsRow(@Nullable Object rowKey) {
    return (rowKey != null && Maps.safeContainsKey(this.backingMap, rowKey));
  }
  
  public boolean containsValue(@Nullable Object value) {
    return (value != null && super.containsValue(value));
  }
  
  public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
    return (rowKey == null || columnKey == null) ? null : super.get(rowKey, columnKey);
  }
  
  public boolean isEmpty() {
    return this.backingMap.isEmpty();
  }
  
  public int size() {
    int size = 0;
    for (Map<C, V> map : this.backingMap.values())
      size += map.size(); 
    return size;
  }
  
  public void clear() {
    this.backingMap.clear();
  }
  
  private Map<C, V> getOrCreate(R rowKey) {
    Map<C, V> map = this.backingMap.get(rowKey);
    if (map == null) {
      map = (Map<C, V>)this.factory.get();
      this.backingMap.put(rowKey, map);
    } 
    return map;
  }
  
  public V put(R rowKey, C columnKey, V value) {
    Preconditions.checkNotNull(rowKey);
    Preconditions.checkNotNull(columnKey);
    Preconditions.checkNotNull(value);
    return getOrCreate(rowKey).put(columnKey, value);
  }
  
  public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
    if (rowKey == null || columnKey == null)
      return null; 
    Map<C, V> map = Maps.<Map<C, V>>safeGet(this.backingMap, rowKey);
    if (map == null)
      return null; 
    V value = map.remove(columnKey);
    if (map.isEmpty())
      this.backingMap.remove(rowKey); 
    return value;
  }
  
  private Map<R, V> removeColumn(Object column) {
    Map<R, V> output = new LinkedHashMap<R, V>();
    Iterator<Map.Entry<R, Map<C, V>>> iterator = this.backingMap.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<R, Map<C, V>> entry = iterator.next();
      V value = (V)((Map)entry.getValue()).remove(column);
      if (value != null) {
        output.put(entry.getKey(), value);
        if (((Map)entry.getValue()).isEmpty())
          iterator.remove(); 
      } 
    } 
    return output;
  }
  
  private boolean containsMapping(Object rowKey, Object columnKey, Object value) {
    return (value != null && value.equals(get(rowKey, columnKey)));
  }
  
  private boolean removeMapping(Object rowKey, Object columnKey, Object value) {
    if (containsMapping(rowKey, columnKey, value)) {
      remove(rowKey, columnKey);
      return true;
    } 
    return false;
  }
  
  public Set<Table.Cell<R, C, V>> cellSet() {
    return super.cellSet();
  }
  
  Iterator<Table.Cell<R, C, V>> cellIterator() {
    return new CellIterator();
  }
  
  private class StandardTable {}
  
  class StandardTable {}
  
  private class StandardTable {}
  
  private class StandardTable {}
  
  private class StandardTable {}
  
  class StandardTable {}
  
  private class CellIterator implements Iterator<Table.Cell<R, C, V>> {
    final Iterator<Map.Entry<R, Map<C, V>>> rowIterator = StandardTable.this.backingMap.entrySet().iterator();
    
    Map.Entry<R, Map<C, V>> rowEntry;
    
    Iterator<Map.Entry<C, V>> columnIterator = Iterators.emptyModifiableIterator();
    
    public boolean hasNext() {
      return (this.rowIterator.hasNext() || this.columnIterator.hasNext());
    }
    
    public Table.Cell<R, C, V> next() {
      if (!this.columnIterator.hasNext()) {
        this.rowEntry = this.rowIterator.next();
        this.columnIterator = ((Map<C, V>)this.rowEntry.getValue()).entrySet().iterator();
      } 
      Map.Entry<C, V> columnEntry = this.columnIterator.next();
      return Tables.immutableCell(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
    }
    
    public void remove() {
      this.columnIterator.remove();
      if (((Map)this.rowEntry.getValue()).isEmpty())
        this.rowIterator.remove(); 
    }
    
    private CellIterator() {}
  }
  
  public Map<C, V> row(R rowKey) {
    return (Map<C, V>)new Row(this, rowKey);
  }
  
  public Map<R, V> column(C columnKey) {
    return (Map<R, V>)new Column(this, columnKey);
  }
  
  public Set<R> rowKeySet() {
    return rowMap().keySet();
  }
  
  public Set<C> columnKeySet() {
    Set<C> result = this.columnKeySet;
    return (result == null) ? (this.columnKeySet = (Set<C>)new ColumnKeySet(this, null)) : result;
  }
  
  Iterator<C> createColumnKeyIterator() {
    return (Iterator<C>)new ColumnKeyIterator(this, null);
  }
  
  public Collection<V> values() {
    return super.values();
  }
  
  public Map<R, Map<C, V>> rowMap() {
    Map<R, Map<C, V>> result = this.rowMap;
    return (result == null) ? (this.rowMap = createRowMap()) : result;
  }
  
  Map<R, Map<C, V>> createRowMap() {
    return (Map<R, Map<C, V>>)new RowMap(this);
  }
  
  public Map<C, Map<R, V>> columnMap() {
    ColumnMap result = this.columnMap;
    return (result == null) ? (Map<C, Map<R, V>>)(this.columnMap = new ColumnMap(this, null)) : (Map<C, Map<R, V>>)result;
  }
  
  private abstract class StandardTable {}
}
