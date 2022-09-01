package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {
  final ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
    return isEmpty() ? ImmutableSet.<Table.Cell<R, C, V>>of() : (ImmutableSet<Table.Cell<R, C, V>>)new CellSet(this, null);
  }
  
  final ImmutableCollection<V> createValues() {
    return isEmpty() ? ImmutableList.<V>of() : (ImmutableCollection<V>)new Values(this, null);
  }
  
  static <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator) {
    Preconditions.checkNotNull(cells);
    if (rowComparator != null || columnComparator != null) {
      Object object = new Object(rowComparator, columnComparator);
      Collections.sort(cells, (Comparator<? super Table.Cell<R, C, V>>)object);
    } 
    return forCellsInternal(cells, rowComparator, columnComparator);
  }
  
  static <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> cells) {
    return forCellsInternal(cells, null, null);
  }
  
  private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator) {
    ImmutableSet.Builder<R> rowSpaceBuilder = ImmutableSet.builder();
    ImmutableSet.Builder<C> columnSpaceBuilder = ImmutableSet.builder();
    ImmutableList<Table.Cell<R, C, V>> cellList = ImmutableList.copyOf(cells);
    for (Table.Cell<R, C, V> cell : cellList) {
      rowSpaceBuilder.add(cell.getRowKey());
      columnSpaceBuilder.add(cell.getColumnKey());
    } 
    ImmutableSet<R> rowSpace = rowSpaceBuilder.build();
    if (rowComparator != null) {
      List<R> rowList = Lists.newArrayList(rowSpace);
      Collections.sort(rowList, rowComparator);
      rowSpace = ImmutableSet.copyOf(rowList);
    } 
    ImmutableSet<C> columnSpace = columnSpaceBuilder.build();
    if (columnComparator != null) {
      List<C> columnList = Lists.newArrayList(columnSpace);
      Collections.sort(columnList, columnComparator);
      columnSpace = ImmutableSet.copyOf(columnList);
    } 
    return (cellList.size() > rowSpace.size() * columnSpace.size() / 2L) ? new DenseImmutableTable<R, C, V>(cellList, rowSpace, columnSpace) : new SparseImmutableTable<R, C, V>(cellList, rowSpace, columnSpace);
  }
  
  abstract Table.Cell<R, C, V> getCell(int paramInt);
  
  abstract V getValue(int paramInt);
  
  private final class RegularImmutableTable {}
  
  private final class RegularImmutableTable {}
}
