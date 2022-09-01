package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageTranscoderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;

public final class ImageIO {
  private static final IIORegistry theRegistry = IIORegistry.getDefaultInstance();
  
  private static Method readerFormatNamesMethod;
  
  private static Method readerFileSuffixesMethod;
  
  private static Method readerMIMETypesMethod;
  
  private static Method writerFormatNamesMethod;
  
  private static Method writerFileSuffixesMethod;
  
  private static Method writerMIMETypesMethod;
  
  public static void scanForPlugins() {
    theRegistry.registerApplicationClasspathSpis();
  }
  
  static class CacheInfo {
    boolean useCache = true;
    
    File cacheDirectory = null;
    
    Boolean hasPermission = null;
    
    public boolean getUseCache() {
      return this.useCache;
    }
    
    public void setUseCache(boolean param1Boolean) {
      this.useCache = param1Boolean;
    }
    
    public File getCacheDirectory() {
      return this.cacheDirectory;
    }
    
    public void setCacheDirectory(File param1File) {
      this.cacheDirectory = param1File;
    }
    
    public Boolean getHasPermission() {
      return this.hasPermission;
    }
    
    public void setHasPermission(Boolean param1Boolean) {
      this.hasPermission = param1Boolean;
    }
  }
  
  private static synchronized CacheInfo getCacheInfo() {
    AppContext appContext = AppContext.getAppContext();
    CacheInfo cacheInfo = (CacheInfo)appContext.get(CacheInfo.class);
    if (cacheInfo == null) {
      cacheInfo = new CacheInfo();
      appContext.put(CacheInfo.class, cacheInfo);
    } 
    return cacheInfo;
  }
  
  private static String getTempDir() {
    GetPropertyAction getPropertyAction = new GetPropertyAction("java.io.tmpdir");
    return AccessController.<String>doPrivileged(getPropertyAction);
  }
  
  private static boolean hasCachePermission() {
    Boolean bool = getCacheInfo().getHasPermission();
    if (bool != null)
      return bool.booleanValue(); 
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        String str1;
        File file = getCacheDirectory();
        if (file != null) {
          str1 = file.getPath();
        } else {
          str1 = getTempDir();
          if (str1 == null || str1.isEmpty()) {
            getCacheInfo().setHasPermission(Boolean.FALSE);
            return false;
          } 
        } 
        String str2 = str1;
        if (!str2.endsWith(File.separator))
          str2 = str2 + File.separator; 
        str2 = str2 + "*";
        securityManager.checkPermission(new FilePermission(str2, "read, write, delete"));
      } 
    } catch (SecurityException securityException) {
      getCacheInfo().setHasPermission(Boolean.FALSE);
      return false;
    } 
    getCacheInfo().setHasPermission(Boolean.TRUE);
    return true;
  }
  
  public static void setUseCache(boolean paramBoolean) {
    getCacheInfo().setUseCache(paramBoolean);
  }
  
  public static boolean getUseCache() {
    return getCacheInfo().getUseCache();
  }
  
  public static void setCacheDirectory(File paramFile) {
    if (paramFile != null && !paramFile.isDirectory())
      throw new IllegalArgumentException("Not a directory!"); 
    getCacheInfo().setCacheDirectory(paramFile);
    getCacheInfo().setHasPermission(null);
  }
  
  public static File getCacheDirectory() {
    return getCacheInfo().getCacheDirectory();
  }
  
  public static ImageInputStream createImageInputStream(Object paramObject) throws IOException {
    Iterator<ImageInputStreamSpi> iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("input == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageInputStreamSpi.class, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
    boolean bool = (getUseCache() && hasCachePermission()) ? true : false;
    while (iterator.hasNext()) {
      ImageInputStreamSpi imageInputStreamSpi = iterator.next();
      if (imageInputStreamSpi.getInputClass().isInstance(paramObject))
        try {
          return imageInputStreamSpi.createInputStreamInstance(paramObject, bool, 
              
              getCacheDirectory());
        } catch (IOException iOException) {
          throw new IIOException("Can't create cache file!", iOException);
        }  
    } 
    return null;
  }
  
  public static ImageOutputStream createImageOutputStream(Object paramObject) throws IOException {
    Iterator<ImageOutputStreamSpi> iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("output == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageOutputStreamSpi.class, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
    boolean bool = (getUseCache() && hasCachePermission()) ? true : false;
    while (iterator.hasNext()) {
      ImageOutputStreamSpi imageOutputStreamSpi = iterator.next();
      if (imageOutputStreamSpi.getOutputClass().isInstance(paramObject))
        try {
          return imageOutputStreamSpi.createOutputStreamInstance(paramObject, bool, 
              
              getCacheDirectory());
        } catch (IOException iOException) {
          throw new IIOException("Can't create cache file!", iOException);
        }  
    } 
    return null;
  }
  
  private static <S extends ImageReaderWriterSpi> String[] getReaderWriterInfo(Class<S> paramClass, SpiInfo paramSpiInfo) {
    Iterator<S> iterator;
    try {
      iterator = theRegistry.getServiceProviders(paramClass, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return new String[0];
    } 
    HashSet<? super String> hashSet = new HashSet();
    while (iterator.hasNext()) {
      ImageReaderWriterSpi imageReaderWriterSpi = (ImageReaderWriterSpi)iterator.next();
      Collections.addAll(hashSet, paramSpiInfo.info(imageReaderWriterSpi));
    } 
    return hashSet.<String>toArray(new String[hashSet.size()]);
  }
  
  public static String[] getReaderFormatNames() {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FORMAT_NAMES);
  }
  
  public static String[] getReaderMIMETypes() {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.MIME_TYPES);
  }
  
  public static String[] getReaderFileSuffixes() {
    return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FILE_SUFFIXES);
  }
  
  private enum ImageIO {
  
  }
  
  static class ImageReaderIterator implements Iterator<ImageReader> {
    public Iterator iter;
    
    public ImageReaderIterator(Iterator param1Iterator) {
      this.iter = param1Iterator;
    }
    
    public boolean hasNext() {
      return this.iter.hasNext();
    }
    
    public ImageReader next() {
      ImageReaderSpi imageReaderSpi = null;
      try {
        imageReaderSpi = this.iter.next();
        return imageReaderSpi.createReaderInstance();
      } catch (IOException iOException) {
        ImageIO.theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
        return null;
      } 
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  static class CanDecodeInputFilter implements ServiceRegistry.Filter {
    Object input;
    
    public CanDecodeInputFilter(Object param1Object) {
      this.input = param1Object;
    }
    
    public boolean filter(Object param1Object) {
      try {
        ImageReaderSpi imageReaderSpi = (ImageReaderSpi)param1Object;
        ImageInputStream imageInputStream = null;
        if (this.input instanceof ImageInputStream)
          imageInputStream = (ImageInputStream)this.input; 
        boolean bool = false;
        if (imageInputStream != null)
          imageInputStream.mark(); 
        bool = imageReaderSpi.canDecodeInput(this.input);
        if (imageInputStream != null)
          imageInputStream.reset(); 
        return bool;
      } catch (IOException iOException) {
        return false;
      } 
    }
  }
  
  static class ImageIO {}
  
  static class ContainsFilter implements ServiceRegistry.Filter {
    Method method;
    
    String name;
    
    public ContainsFilter(Method param1Method, String param1String) {
      this.method = param1Method;
      this.name = param1String;
    }
    
    public boolean filter(Object param1Object) {
      try {
        return ImageIO.contains((String[])this.method.invoke(param1Object, new Object[0]), this.name);
      } catch (Exception exception) {
        return false;
      } 
    }
  }
  
  public static Iterator<ImageReader> getImageReaders(Object paramObject) {
    Iterator<ImageReaderSpi> iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("input == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new CanDecodeInputFilter(paramObject), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  static {
    try {
      readerFormatNamesMethod = ImageReaderSpi.class.getMethod("getFormatNames", new Class[0]);
      readerFileSuffixesMethod = ImageReaderSpi.class.getMethod("getFileSuffixes", new Class[0]);
      readerMIMETypesMethod = ImageReaderSpi.class.getMethod("getMIMETypes", new Class[0]);
      writerFormatNamesMethod = ImageWriterSpi.class.getMethod("getFormatNames", new Class[0]);
      writerFileSuffixesMethod = ImageWriterSpi.class.getMethod("getFileSuffixes", new Class[0]);
      writerMIMETypesMethod = ImageWriterSpi.class.getMethod("getMIMETypes", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      noSuchMethodException.printStackTrace();
    } 
  }
  
  public static Iterator<ImageReader> getImageReadersByFormatName(String paramString) {
    Iterator<ImageReaderSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFormatNamesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static Iterator<ImageReader> getImageReadersBySuffix(String paramString) {
    Iterator<ImageReaderSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("fileSuffix == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFileSuffixesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static Iterator<ImageReader> getImageReadersByMIMEType(String paramString) {
    Iterator<ImageReaderSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("MIMEType == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerMIMETypesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static String[] getWriterFormatNames() {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FORMAT_NAMES);
  }
  
  public static String[] getWriterMIMETypes() {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.MIME_TYPES);
  }
  
  public static String[] getWriterFileSuffixes() {
    return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FILE_SUFFIXES);
  }
  
  private static boolean contains(String[] paramArrayOfString, String paramString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equalsIgnoreCase(paramArrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  public static Iterator<ImageWriter> getImageWritersByFormatName(String paramString) {
    Iterator<ImageWriterSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFormatNamesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersBySuffix(String paramString) {
    Iterator<ImageWriterSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("fileSuffix == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFileSuffixesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersByMIMEType(String paramString) {
    Iterator<ImageWriterSpi> iterator;
    if (paramString == null)
      throw new IllegalArgumentException("MIMEType == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerMIMETypesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static ImageWriter getImageWriter(ImageReader paramImageReader) {
    if (paramImageReader == null)
      throw new IllegalArgumentException("reader == null!"); 
    ImageReaderSpi imageReaderSpi = paramImageReader.getOriginatingProvider();
    if (imageReaderSpi == null) {
      Iterator<ImageReaderSpi> iterator;
      try {
        iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, false);
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      } 
      while (iterator.hasNext()) {
        ImageReaderSpi imageReaderSpi1 = iterator.next();
        if (imageReaderSpi1.isOwnReader(paramImageReader)) {
          imageReaderSpi = imageReaderSpi1;
          break;
        } 
      } 
      if (imageReaderSpi == null)
        return null; 
    } 
    String[] arrayOfString = imageReaderSpi.getImageWriterSpiNames();
    if (arrayOfString == null)
      return null; 
    Class<?> clazz = null;
    try {
      clazz = Class.forName(arrayOfString[0], true, 
          ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
    ImageWriterSpi imageWriterSpi = (ImageWriterSpi)theRegistry.getServiceProviderByClass(clazz);
    if (imageWriterSpi == null)
      return null; 
    try {
      return imageWriterSpi.createWriterInstance();
    } catch (IOException iOException) {
      theRegistry.deregisterServiceProvider(imageWriterSpi, ImageWriterSpi.class);
      return null;
    } 
  }
  
  public static ImageReader getImageReader(ImageWriter paramImageWriter) {
    if (paramImageWriter == null)
      throw new IllegalArgumentException("writer == null!"); 
    ImageWriterSpi imageWriterSpi = paramImageWriter.getOriginatingProvider();
    if (imageWriterSpi == null) {
      Iterator<ImageWriterSpi> iterator;
      try {
        iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, false);
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      } 
      while (iterator.hasNext()) {
        ImageWriterSpi imageWriterSpi1 = iterator.next();
        if (imageWriterSpi1.isOwnWriter(paramImageWriter)) {
          imageWriterSpi = imageWriterSpi1;
          break;
        } 
      } 
      if (imageWriterSpi == null)
        return null; 
    } 
    String[] arrayOfString = imageWriterSpi.getImageReaderSpiNames();
    if (arrayOfString == null)
      return null; 
    Class<?> clazz = null;
    try {
      clazz = Class.forName(arrayOfString[0], true, 
          ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
    ImageReaderSpi imageReaderSpi = (ImageReaderSpi)theRegistry.getServiceProviderByClass(clazz);
    if (imageReaderSpi == null)
      return null; 
    try {
      return imageReaderSpi.createReaderInstance();
    } catch (IOException iOException) {
      theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
      return null;
    } 
  }
  
  public static Iterator<ImageWriter> getImageWriters(ImageTypeSpecifier paramImageTypeSpecifier, String paramString) {
    Iterator<ImageWriterSpi> iterator;
    if (paramImageTypeSpecifier == null)
      throw new IllegalArgumentException("type == null!"); 
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new CanEncodeImageAndFormatFilter(paramImageTypeSpecifier, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageTranscoder> getImageTranscoders(ImageReader paramImageReader, ImageWriter paramImageWriter) {
    Iterator<ImageTranscoderSpi> iterator;
    if (paramImageReader == null)
      throw new IllegalArgumentException("reader == null!"); 
    if (paramImageWriter == null)
      throw new IllegalArgumentException("writer == null!"); 
    ImageReaderSpi imageReaderSpi = paramImageReader.getOriginatingProvider();
    ImageWriterSpi imageWriterSpi = paramImageWriter.getOriginatingProvider();
    TranscoderFilter transcoderFilter = new TranscoderFilter(imageReaderSpi, imageWriterSpi);
    try {
      iterator = theRegistry.getServiceProviders(ImageTranscoderSpi.class, transcoderFilter, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageTranscoderIterator(iterator);
  }
  
  public static BufferedImage read(File paramFile) throws IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("input == null!"); 
    if (!paramFile.canRead())
      throw new IIOException("Can't read input file!"); 
    ImageInputStream imageInputStream = createImageInputStream(paramFile);
    if (imageInputStream == null)
      throw new IIOException("Can't create an ImageInputStream!"); 
    BufferedImage bufferedImage = read(imageInputStream);
    if (bufferedImage == null)
      imageInputStream.close(); 
    return bufferedImage;
  }
  
  public static BufferedImage read(InputStream paramInputStream) throws IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("input == null!"); 
    ImageInputStream imageInputStream = createImageInputStream(paramInputStream);
    BufferedImage bufferedImage = read(imageInputStream);
    if (bufferedImage == null)
      imageInputStream.close(); 
    return bufferedImage;
  }
  
  public static BufferedImage read(URL paramURL) throws IOException {
    BufferedImage bufferedImage;
    if (paramURL == null)
      throw new IllegalArgumentException("input == null!"); 
    InputStream inputStream = null;
    try {
      inputStream = paramURL.openStream();
    } catch (IOException iOException) {
      throw new IIOException("Can't get input stream from URL!", iOException);
    } 
    ImageInputStream imageInputStream = createImageInputStream(inputStream);
    try {
      bufferedImage = read(imageInputStream);
      if (bufferedImage == null)
        imageInputStream.close(); 
    } finally {
      inputStream.close();
    } 
    return bufferedImage;
  }
  
  public static BufferedImage read(ImageInputStream paramImageInputStream) throws IOException {
    BufferedImage bufferedImage;
    if (paramImageInputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    Iterator<ImageReader> iterator = getImageReaders(paramImageInputStream);
    if (!iterator.hasNext())
      return null; 
    ImageReader imageReader = iterator.next();
    ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
    imageReader.setInput(paramImageInputStream, true, true);
    try {
      bufferedImage = imageReader.read(0, imageReadParam);
    } finally {
      imageReader.dispose();
      paramImageInputStream.close();
    } 
    return bufferedImage;
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, ImageOutputStream paramImageOutputStream) throws IOException {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("im == null!"); 
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    if (paramImageOutputStream == null)
      throw new IllegalArgumentException("output == null!"); 
    return doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), paramImageOutputStream);
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, File paramFile) throws IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("output == null!"); 
    ImageOutputStream imageOutputStream = null;
    ImageWriter imageWriter = getWriter(paramRenderedImage, paramString);
    if (imageWriter == null)
      return false; 
    try {
      paramFile.delete();
      imageOutputStream = createImageOutputStream(paramFile);
    } catch (IOException iOException) {
      throw new IIOException("Can't create output stream!", iOException);
    } 
    try {
      return doWrite(paramRenderedImage, imageWriter, imageOutputStream);
    } finally {
      imageOutputStream.close();
    } 
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, OutputStream paramOutputStream) throws IOException {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("output == null!"); 
    ImageOutputStream imageOutputStream = null;
    try {
      imageOutputStream = createImageOutputStream(paramOutputStream);
    } catch (IOException iOException) {
      throw new IIOException("Can't create output stream!", iOException);
    } 
    try {
      return doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), imageOutputStream);
    } finally {
      imageOutputStream.close();
    } 
  }
  
  private static ImageWriter getWriter(RenderedImage paramRenderedImage, String paramString) {
    ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromRenderedImage(paramRenderedImage);
    Iterator<ImageWriter> iterator = getImageWriters(imageTypeSpecifier, paramString);
    if (iterator.hasNext())
      return iterator.next(); 
    return null;
  }
  
  private static boolean doWrite(RenderedImage paramRenderedImage, ImageWriter paramImageWriter, ImageOutputStream paramImageOutputStream) throws IOException {
    if (paramImageWriter == null)
      return false; 
    paramImageWriter.setOutput(paramImageOutputStream);
    try {
      paramImageWriter.write(paramRenderedImage);
    } finally {
      paramImageWriter.dispose();
      paramImageOutputStream.flush();
    } 
    return true;
  }
  
  static class ImageIO {}
  
  static class ImageIO {}
  
  static class ImageIO {}
}
