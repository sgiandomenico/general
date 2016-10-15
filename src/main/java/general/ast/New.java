package general.ast;

import general.type.Type;

//@Deprecated
public class New implements Expression
{
  public final Class<?> type;
  public final Expression[] constructorArgs;
  
  public New(Class<?> type, Expression... constructorArgs)
  {
    this.type = type;
    this.constructorArgs = constructorArgs;
  }
  
  @Override
  public Type getType()
  {
    return Type.fromClass(type);
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("new ");
    sb.append(type.getName());
    sb.append("( ");
    for (Expression exp : constructorArgs)
    {
      sb.append(exp);
      sb.append(" ");
    }
    sb.append(")");
    
    return sb.toString();
  }
}
