package com.mojang.patchy;

import com.google.common.base.Predicate;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class BlockingDC implements DirContext {
  private final Predicate<String> blockList;
  
  private final DirContext parent;
  
  public BlockingDC(Predicate<String> blockList, DirContext parent) {
    this.blockList = blockList;
    this.parent = parent;
  }
  
  public Attributes getAttributes(String name) throws NamingException {
    if (this.blockList.apply(name))
      return new BasicAttributes(); 
    return this.parent.getAttributes(name);
  }
  
  public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
    if (this.blockList.apply(name))
      return new BasicAttributes(); 
    return this.parent.getAttributes(name, attrIds);
  }
  
  public Attributes getAttributes(Name name) throws NamingException {
    return this.parent.getAttributes(name);
  }
  
  public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
    return this.parent.getAttributes(name, attrIds);
  }
  
  public void modifyAttributes(Name name, int mod_op, Attributes attrs) throws NamingException {
    this.parent.modifyAttributes(name, mod_op, attrs);
  }
  
  public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
    this.parent.modifyAttributes(name, mod_op, attrs);
  }
  
  public void modifyAttributes(Name name, ModificationItem[] mods) throws NamingException {
    this.parent.modifyAttributes(name, mods);
  }
  
  public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
    this.parent.modifyAttributes(name, mods);
  }
  
  public void bind(Name name, Object obj, Attributes attrs) throws NamingException {
    this.parent.bind(name, obj, attrs);
  }
  
  public void bind(String name, Object obj, Attributes attrs) throws NamingException {
    this.parent.bind(name, obj, attrs);
  }
  
  public void rebind(Name name, Object obj, Attributes attrs) throws NamingException {
    this.parent.rebind(name, obj, attrs);
  }
  
  public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
    this.parent.rebind(name, obj, attrs);
  }
  
  public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
    return this.parent.createSubcontext(name, attrs);
  }
  
  public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
    return this.parent.createSubcontext(name, attrs);
  }
  
  public DirContext getSchema(Name name) throws NamingException {
    return this.parent.getSchema(name);
  }
  
  public DirContext getSchema(String name) throws NamingException {
    return this.parent.getSchema(name);
  }
  
  public DirContext getSchemaClassDefinition(Name name) throws NamingException {
    return this.parent.getSchemaClassDefinition(name);
  }
  
  public DirContext getSchemaClassDefinition(String name) throws NamingException {
    return this.parent.getSchemaClassDefinition(name);
  }
  
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
    return this.parent.search(name, matchingAttributes, attributesToReturn);
  }
  
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
    return this.parent.search(name, matchingAttributes, attributesToReturn);
  }
  
  public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
    return this.parent.search(name, matchingAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
    return this.parent.search(name, matchingAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
    return this.parent.search(name, filter, cons);
  }
  
  public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
    return this.parent.search(name, filter, cons);
  }
  
  public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
    return this.parent.search(name, filterExpr, filterArgs, cons);
  }
  
  public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
    return this.parent.search(name, filterExpr, filterArgs, cons);
  }
  
  public Object lookup(Name name) throws NamingException {
    return this.parent.lookup(name);
  }
  
  public Object lookup(String name) throws NamingException {
    return this.parent.lookup(name);
  }
  
  public void bind(Name name, Object obj) throws NamingException {
    this.parent.bind(name, obj);
  }
  
  public void bind(String name, Object obj) throws NamingException {
    this.parent.bind(name, obj);
  }
  
  public void rebind(Name name, Object obj) throws NamingException {
    this.parent.rebind(name, obj);
  }
  
  public void rebind(String name, Object obj) throws NamingException {
    this.parent.rebind(name, obj);
  }
  
  public void unbind(Name name) throws NamingException {
    this.parent.unbind(name);
  }
  
  public void unbind(String name) throws NamingException {
    this.parent.unbind(name);
  }
  
  public void rename(Name oldName, Name newName) throws NamingException {
    this.parent.rename(oldName, newName);
  }
  
  public void rename(String oldName, String newName) throws NamingException {
    this.parent.rename(oldName, newName);
  }
  
  public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
    return this.parent.list(name);
  }
  
  public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
    return this.parent.list(name);
  }
  
  public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
    return this.parent.listBindings(name);
  }
  
  public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
    return this.parent.listBindings(name);
  }
  
  public void destroySubcontext(Name name) throws NamingException {
    this.parent.destroySubcontext(name);
  }
  
  public void destroySubcontext(String name) throws NamingException {
    this.parent.destroySubcontext(name);
  }
  
  public Context createSubcontext(Name name) throws NamingException {
    return this.parent.createSubcontext(name);
  }
  
  public Context createSubcontext(String name) throws NamingException {
    return this.parent.createSubcontext(name);
  }
  
  public Object lookupLink(Name name) throws NamingException {
    return this.parent.lookupLink(name);
  }
  
  public Object lookupLink(String name) throws NamingException {
    return this.parent.lookupLink(name);
  }
  
  public NameParser getNameParser(Name name) throws NamingException {
    return this.parent.getNameParser(name);
  }
  
  public NameParser getNameParser(String name) throws NamingException {
    return this.parent.getNameParser(name);
  }
  
  public Name composeName(Name name, Name prefix) throws NamingException {
    return this.parent.composeName(name, prefix);
  }
  
  public String composeName(String name, String prefix) throws NamingException {
    return this.parent.composeName(name, prefix);
  }
  
  public Object addToEnvironment(String propName, Object propVal) throws NamingException {
    return this.parent.addToEnvironment(propName, propVal);
  }
  
  public Object removeFromEnvironment(String propName) throws NamingException {
    return this.parent.removeFromEnvironment(propName);
  }
  
  public Hashtable<?, ?> getEnvironment() throws NamingException {
    return this.parent.getEnvironment();
  }
  
  public void close() throws NamingException {
    this.parent.close();
  }
  
  public String getNameInNamespace() throws NamingException {
    return this.parent.getNameInNamespace();
  }
}
