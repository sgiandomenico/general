package general.type;

import java.lang.reflect.Method;
import java.util.Arrays;

public class FunctionType implements Type
{
  public final Type[] argumentTypes;
  public final Type returnType;
  
  @Deprecated
  public FunctionType(Type[] argumentTypes, Type returnType)
  {
    this.argumentTypes = Arrays.copyOf(argumentTypes, argumentTypes.length);
    this.returnType = returnType;
  }
  
  public FunctionType(Type returnType, Type... argumentTypes)
  {
    this.argumentTypes = Arrays.copyOf(argumentTypes, argumentTypes.length);
    this.returnType = returnType;
  }
  
  public FunctionType(Method method)
  {
    Class<?>[] paramTypes = method.getParameterTypes();
    this.argumentTypes = new Type[paramTypes.length];
    for (int i = 0; i < argumentTypes.length; i++)
      this.argumentTypes[i] = Type.fromClass(paramTypes[i]);
    
    this.returnType = Type.fromClass(method.getReturnType());
  }
  
  @Override
  public String getBinaryName()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public String getDescriptor()
  {
    StringBuilder sb = new StringBuilder("(");
    for (Type argType : argumentTypes)
      sb.append(argType.getDescriptor());
    sb.append(")");
    sb.append(returnType.getDescriptor());
    
    return sb.toString();
  }
}
