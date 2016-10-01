package general.compiler;

import java.io.*;

import general.engine.Function;

public class ClassFileWriter implements Function
{
//** Fields ********************************************************************
  
  final File rootDir;
  
//** Constructors **************************************************************
  
  public ClassFileWriter(File rootDir)
  {
    this.rootDir = rootDir;
  }
  
  public ClassFileWriter(String rootDirPath)
  {
    this(new File(rootDirPath));
  }
  
//** Methods *******************************************************************
  
  @Override
  public Object apply(Object... args)
  {
    writeClassFile((Class<?>) args[0]);
    return null;
  }
  
//------------------------------------------------------------------------------
  
  public void writeClassFile(Class<?> clazz)
  {
    ClassLoader l = clazz.getClassLoader();
    if (!(l instanceof CustomClassLoader))
      throw new RuntimeException(new NotSerializableException(clazz.getName()));
    
    CustomClassLoader loader = (CustomClassLoader) l;
    
    String qualifiedName = clazz.getName();
    File outputFile = new File(rootDir, qualifiedName.replace(".", File.separator) + ".class");
    
    byte[] classData = loader.getClassBytes(clazz);
    
    try
    {
      ensureDir(outputFile.getParentFile());
      try (OutputStream out = new FileOutputStream(outputFile))
      {
        out.write(classData);
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  void ensureDir(File dir) throws FileNotFoundException
  {
    if (dir.exists())
      return;
    
    dir.mkdirs();
    if (!dir.exists())
      throw new FileNotFoundException("Could not make directory: " + dir);
  }
  
//------------------------------------------------------------------------------
}
