package general.util;

import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectUtil
{
//** Constructors **************************************************************
  
  private ReflectUtil()
  {
    ;
  }
  
//** Methods *******************************************************************
  
  public static Class<?> asBoxedClass(Class<?> c)
  {
    if (!c.isPrimitive())
      return c;
    else if (c == Boolean.TYPE)
      return Boolean.class;
    else if (c == Byte.TYPE)
      return Byte.class;
    else if (c == Character.TYPE)
      return Character.class;
    else if (c == Short.TYPE)
      return Short.class;
    else if (c == Integer.TYPE)
      return Integer.class;
    else if (c == Long.TYPE)
      return Long.class;
    else if (c == Float.TYPE)
      return Float.class;
    else if (c == Double.TYPE)
      return Double.class;
    else
      throw new RuntimeException(new ClassNotFoundException());
  }
  
//------------------------------------------------------------------------------
  
  public static boolean instanceOfs(Class<?>[] classes, Object[] objs)
  {
    if (objs.length != classes.length)
      return false;
    
    for (int i = 0; i < objs.length; i++)
    {
      if (objs[i] != null && !asBoxedClass(classes[i]).isInstance(objs[i]))
        return false;
    }
    
    return true;
  }
  
  public static Constructor<?> getConstructor(Class<?> type, Object[] args)
  {
    for (Constructor<?> c : type.getConstructors())
    {
      if (instanceOfs(c.getParameterTypes(), args))
        return c;
    }
    
    return null;
  }
  
  public static Method[] getMethods(Class<?> type, String name)
  {
    return Arrays.stream(type.getMethods()).filter(m -> name.equals(m.getName())).toArray(Method[]::new);
  }
  
  public static Method getMethod(Class<?> type, String name, Object[] args)
  {
    return selectMethod(getMethods(type, name), args);
  }
  
  public static Method selectMethod(Method[] methods, Object[] args)
  {
    for (Method m : methods)
    {
      if (instanceOfs(m.getParameterTypes(), args))
        return m;
    }
    
    return null;
  }
  
//------------------------------------------------------------------------------
  
  public static Object callMethod(Class<?> type, String name, Object obj, Object[] args)
      throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
  {
    Method m = getMethod(type, name, args);
    if (m == null)
      throw new NoSuchMethodException(String.format("%s.%s(%s)", type, name, Arrays.toString(args)));
    
    return m.invoke(obj, args);
  }
  
  public static Object callConstructor(Class<?> type, Object[] args)
      throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException
  {
    Constructor<?> c = getConstructor(type, args);
    if (c == null)
      throw new NoSuchMethodException(String.format("%s(%s)", type, Arrays.toString(args)));
    
    return c.newInstance(args);
  }
  
//------------------------------------------------------------------------------
}
