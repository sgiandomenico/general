package general.type;

import java.util.Arrays;

public class FunctionType implements Type
{
  final Type[] argumentTypes;
  final Type returnType;
  
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
