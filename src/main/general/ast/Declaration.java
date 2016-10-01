package general.ast;

import general.engine.Symbol;

public class Declaration implements Expression
{
  public final Symbol symbol;
//  public final Class<?> type;
  
  public Declaration(Symbol symbol)
  {
    this.symbol = symbol;
  }
  
  public Declaration(String symbol)
  {
    this(Symbol.get(symbol));
  }
  
  @Override
  public Class<?> getType()
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
