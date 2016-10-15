package general.engine;

import java.lang.reflect.Method;

import general.util.ReflectUtil;

public class JavaMethodVerb implements Function
{
//** Variables *****************************************************************
  
  private final Object obj;
  private final Method[] methods;
  
//** Constructors **************************************************************
  
  public JavaMethodVerb(Object obj, Method[] methods)
  {
//    super(EMPTY_TYPES, EMPTY_TYPES);
    
    this.obj = obj;
    this.methods = methods;
  }
  
//** Methods *******************************************************************

//  @Override
//  public boolean areArgumentsValid(Object[] args)
//  {
//    return getMatchingMethod(args) != null;
//  }
//  
//  @Override
//  public boolean areResultsValid(Object[] results)
//  {
//    return true;
//  }

//------------------------------------------------------------------------------

//  protected Method getMatchingMethod(Object[] args)
//  {
//    for (Method method : methods)
//    {
//      if (instanceOfs(method.getParameterTypes(), args))
//        return method;
//    }
//    
//    return null;
//  }

//------------------------------------------------------------------------------
  
  @Override
  public Object apply(Object... args)
  {
    Method method = ReflectUtil.selectMethod(methods, args);
    
    try
    {
      if (method == null)
        throw new NoSuchMethodException();
      
      return method.invoke(obj, args);
    }
    catch (ReflectiveOperationException e)
    {
      throw new RuntimeException(e);
    }
  }
  
//------------------------------------------------------------------------------
}
