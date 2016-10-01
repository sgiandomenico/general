package general.ast;

import general.engine.Symbol;

public class Assignment implements Expression
{
  public final Symbol symbol;
  public final Expression value;
  
  public Assignment(Symbol symbol, Expression value)
  {
    this.symbol = symbol;
    this.value = value;
  }
  
  public Assignment(String symbol, Expression value)
  {
    this(Symbol.get(symbol), value);
  }
  
  @Override
  public Class<?> getType()
  {
    return value.getType();
  }
  
}
