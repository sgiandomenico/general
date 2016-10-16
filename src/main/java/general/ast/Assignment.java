package general.ast;

import general.type.Type;

public class Assignment implements Expression
{
  public final Expression location;
  public final Expression value;
  
  public Assignment(Expression location, Expression value)
  {
    this.location = location;
    this.value = value;
  }
  
//  public Assignment(String symbol, Expression value)
//  {
//    this(Symbol.get(symbol), value);
//  }
  
  @Override
  public Type getType()
  {
    return value.getType();
  }
  
  @Override
  public String toString()
  {
    return location + " = " + value;
  }
  
}
