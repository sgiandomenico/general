package general.engine;

public class JavaClassLoader implements Function
{
  
  @Override
  public Class<?> getType()
  {
    return Class.class;
  }
  
  @Override
  public Object apply(Object... args)
  {
    String typeStr = (String) args[0];
    Class<?> clazz;

    try
    {
      clazz = Class.forName(typeStr);
    }
    catch (ClassNotFoundException e)
    {
      throw new RuntimeException(e);
    }

//    String name = args.length > 1 ? ((Symbol) args[1]).name : typeStr;

//    runtime.declareVariable(name, Class.class, clazz);

    return clazz;
  }
  
}
