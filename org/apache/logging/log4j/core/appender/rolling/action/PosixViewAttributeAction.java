package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

@Plugin(name = "PosixViewAttribute", category = "Core", printObject = true)
public class PosixViewAttributeAction extends AbstractPathAction {
  private final Set<PosixFilePermission> filePermissions;
  
  private final String fileOwner;
  
  private final String fileGroup;
  
  private PosixViewAttributeAction(String basePath, boolean followSymbolicLinks, int maxDepth, PathCondition[] pathConditions, StrSubstitutor subst, Set<PosixFilePermission> filePermissions, String fileOwner, String fileGroup) {
    super(basePath, followSymbolicLinks, maxDepth, pathConditions, subst);
    this.filePermissions = filePermissions;
    this.fileOwner = fileOwner;
    this.fileGroup = fileGroup;
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  protected FileVisitor<Path> createFileVisitor(Path basePath, List<PathCondition> conditions) {
    return (FileVisitor<Path>)new Object(this, conditions, basePath);
  }
  
  public Set<PosixFilePermission> getFilePermissions() {
    return this.filePermissions;
  }
  
  public String getFileOwner() {
    return this.fileOwner;
  }
  
  public String getFileGroup() {
    return this.fileGroup;
  }
  
  public String toString() {
    return "PosixViewAttributeAction [filePermissions=" + this.filePermissions + ", fileOwner=" + this.fileOwner + ", fileGroup=" + this.fileGroup + ", getBasePath()=" + 
      getBasePath() + ", getMaxDepth()=" + 
      getMaxDepth() + ", getPathConditions()=" + 
      getPathConditions() + "]";
  }
  
  public static class PosixViewAttributeAction {}
}
