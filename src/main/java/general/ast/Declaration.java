package general.ast;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.engine.Symbol;
import general.type.Type;

public class Declaration implements Expression
{
  public final Symbol symbol;
//  public final Class<?> type;
  public final SymbolicReference typeExpr;
  
  public Declaration(Symbol symbol, SymbolicReference typeExpr)
  {
    this.symbol = symbol;
    this.typeExpr = typeExpr;
  }
  
  public Declaration(String symbol, SymbolicReference typeExpr)
  {
    this(Symbol.get(symbol), typeExpr);
  }
  
  @Override
  public Type getType()
  {
    throw new NotYetImplementedException();
  }
  
}
