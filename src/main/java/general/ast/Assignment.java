package general.ast;

import general.engine.Symbol;
import general.type.Type;

public class Assignment implements Expression
{
  @Deprecated
  public final Symbol symbol;
  public final SymbolicReference symRef;
  public final Expression value;
  
  public Assignment(SymbolicReference symRef, Expression value)
  {
    this.symRef = symRef;
    this.symbol = symRef.symbol;
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
    return symRef + " = " + value;
  }
  
}
