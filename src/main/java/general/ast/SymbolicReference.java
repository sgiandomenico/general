package general.ast;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.engine.Symbol;
import general.type.Type;

// TODO: Rename to identifier?
public class SymbolicReference implements Expression
{
  public final Symbol symbol;
  
  public NameReference reference;
  
  public SymbolicReference(Symbol symbol)
  {
    this.symbol = symbol;
  }
  
  public SymbolicReference(String symbol)
  {
    this(Symbol.get(symbol));
  }
  
  @Override
  public Type getType()
  {
    // TODO Auto-generated method stub
    throw new NotYetImplementedException();
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
