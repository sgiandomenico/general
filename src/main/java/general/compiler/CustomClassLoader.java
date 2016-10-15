package general.compiler;

import java.util.*;

import general.util.Javap;

public class CustomClassLoader extends ClassLoader
{
//** Fields ********************************************************************
  
  public static final boolean DUMP_CLASSES = true;
  
  Map<Class<?>, byte[]> loadedClasses = new HashMap<>();
  
//** Constructors **************************************************************

//  public CustomClassLoader()
//  {
//    // TODO Auto-generated constructor stub
//  }

//** Methods *******************************************************************
  
  public Class<?> defineClass(String name, byte[] b)
  {
    if (DUMP_CLASSES)
      Javap.printClass(b, System.err);
    
    Class<?> clazz = defineClass(name, b, 0, b.length);
    loadedClasses.put(clazz, b);
    return clazz;
  }
  
  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException
  {
    // TODO Generate class on the fly?
    return super.findClass(name);
  }
  
//------------------------------------------------------------------------------
  
  public byte[] getClassBytes(Class<?> clazz)
  {
    return loadedClasses.get(clazz);
  }
  
//------------------------------------------------------------------------------
}
