package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

public interface PluginVisitor<A extends Annotation> {
  PluginVisitor<A> setAnnotation(Annotation paramAnnotation);
  
  PluginVisitor<A> setAliases(String... paramVarArgs);
  
  PluginVisitor<A> setConversionType(Class<?> paramClass);
  
  PluginVisitor<A> setStrSubstitutor(StrSubstitutor paramStrSubstitutor);
  
  PluginVisitor<A> setMember(Member paramMember);
  
  Object visit(Configuration paramConfiguration, Node paramNode, LogEvent paramLogEvent, StringBuilder paramStringBuilder);
}
