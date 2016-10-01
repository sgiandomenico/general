package general.ast;

import general.engine.Symbol;

public class SymbolicReference implements Expression
{
  public final Symbol symbol;
  
  public SymbolicReference(Symbol symbol)
  {
    this.symbol = symbol;
  }
  
  public SymbolicReference(String symbol)
  {
    this(Symbol.get(symbol));
  }
  
  @Override
  public Class<?> getType()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  // FIXME?
  @Override
  public String getName()
  {
    return symbol.name;
  }
  
  @Override
  public String toString()
  {
    return symbol.name;
  }
  
}
