package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public interface TypeTree extends Tree {
  void accept(TypeTreeVisitor<?> paramTypeTreeVisitor);
}
