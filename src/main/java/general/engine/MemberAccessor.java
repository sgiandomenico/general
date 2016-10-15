package general.engine;

import java.lang.reflect.Method;

import general.interpreter.NoSuchVariableException;
import general.util.ReflectUtil;

public class MemberAccessor implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { Object.class, Symbol.class };
//  private static final Class<?>[] resultTypes = new Class<?>[] { Object.class };

//** Constructors **************************************************************

//  public MemberAccessor()
//  {
//    super(argumentTypes, resultTypes);
//  }

//** Methods *******************************************************************

//  @Override
//  public Object[] eval(FlowRuntime runtime, Object[] args)
//  {
//    Object obj = args[0];
//    Symbol member = (Symbol) args[1];
//    
//    return new Object[] { resolveMember(obj, member.name) };
//  }
//  
//  public Object resolveMember(Object obj, String memberName)
//  {
//    if (obj instanceof VariableScope)
//    {
//      return ((VariableScope) obj).getVariable(memberName).getValue();
//    }
//    
//    Class<?> clazz = obj.getClass();
//    
//    try
//    {
//      return clazz.getField(memberName).get(obj);
//    }
//    catch (ReflectiveOperationException e)
//    {
//      // Try something else.
//    }
//    
//    Method[] methods = clazz.getMethods();
//    List<Method> matchingMethods = new LinkedList<>();
//    
//    for (Method method : methods)
//    {
//      if (ObjectUtil.deepEquals(memberName, method.getName()))
//        matchingMethods.add(method);
//    }
//    
//    if (!matchingMethods.isEmpty())
//    {
//      return new JavaMethodVerb(obj, CollectionUtil.toArray(matchingMethods, Method.class));
//    }
//    
//    throw new NoSuchVariableException(memberName);
//  }
  
  @Override
  public Object apply(Object... args)
  {
    Object obj = args[0];
    String memberName = ((Symbol) args[1]).name;
    
//    if (obj instanceof VariableScope)
//    {
//      return ((VariableScope) obj).resolve(memberName);
//    }
    
    Class<?> clazz = obj.getClass();
    
    try
    {
      return clazz.getField(memberName).get(obj);
    }
    catch (ReflectiveOperationException e)
    {
      // Try something else.
    }
    
    Method[] methods = ReflectUtil.getMethods(clazz, memberName);
    if (methods.length > 0)
      return new JavaMethodVerb(obj, methods);
    
    throw new NoSuchVariableException(memberName);
  }
  
//------------------------------------------------------------------------------
}
