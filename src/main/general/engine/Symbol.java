package general.engine;

public class Symbol implements Comparable<Symbol>
{
//** Variables *****************************************************************

//  private final Map<String, Symbol> existingSymbols
  
  public final String name;
  
//** Constructors **************************************************************
  
  private Symbol(String name)
  {
    this.name = name;
  }
  
  // TODO: Memoize symbols.
  public static Symbol get(String name)
  {
    return new Symbol(name);
  }
  
//** Methods *******************************************************************
  
  @Override
  public String toString()
  {
    return name;
  }
  
  @Override
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public boolean equals(Symbol other)
  {
    return other != null && this.name.equals(other.name);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof Symbol && this.equals((Symbol) obj);
  }
  
  @Override
  public int compareTo(Symbol other)
  {
    return this.name.compareTo(other.name);
  }
  
//------------------------------------------------------------------------------
}
