package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;

@Beta
@GwtCompatible(emulated = true)
public abstract class TreeTraverser<T> {
  public abstract Iterable<T> children(T paramT);
  
  public final FluentIterable<T> preOrderTraversal(T root) {
    Preconditions.checkNotNull(root);
    return (FluentIterable<T>)new Object(this, root);
  }
  
  UnmodifiableIterator<T> preOrderIterator(T root) {
    return (UnmodifiableIterator<T>)new PreOrderIterator(this, root);
  }
  
  public final FluentIterable<T> postOrderTraversal(T root) {
    Preconditions.checkNotNull(root);
    return (FluentIterable<T>)new Object(this, root);
  }
  
  UnmodifiableIterator<T> postOrderIterator(T root) {
    return (UnmodifiableIterator<T>)new PostOrderIterator(this, root);
  }
  
  public final FluentIterable<T> breadthFirstTraversal(T root) {
    Preconditions.checkNotNull(root);
    return (FluentIterable<T>)new Object(this, root);
  }
  
  private final class TreeTraverser {}
  
  private final class TreeTraverser {}
  
  private static final class TreeTraverser {}
  
  private final class TreeTraverser {}
}
