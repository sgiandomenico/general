package general.core;

import java.lang.reflect.*;
import java.util.Arrays;

import general.engine.Function;
import general.util.ReflectUtil;

public class ObjectCreator implements Function
{
  public final static String NEW = "new";
  
  @Override
  public Object apply(Object... args)
  {
    Class<?> clazz = (Class<?>) args[0];
    Object[] cArgs = Arrays.copyOfRange(args, 1, args.length);
    Constructor<?> c = ReflectUtil.getConstructor(clazz, cArgs);
    
    try
    {
      if (c == null)
        throw new NoSuchMethodException();
      
      return c.newInstance(cArgs);
    }
    catch (ReflectiveOperationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
}
