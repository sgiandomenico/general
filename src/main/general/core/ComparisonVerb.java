package general.core;

import org.giandomenico.stephen.util.*;

import general.engine.Function;

public class ComparisonVerb implements Function
{
//** Variables *****************************************************************
  
  public final Comparison comparison;
  
//** Constructors **************************************************************
  
  public ComparisonVerb(Comparison comparison)
  {
    this.comparison = comparison;
  }
  
//** Methods *******************************************************************
  
  @Override
  public String toString()
  {
    return comparison.symbol;
  }
  
//------------------------------------------------------------------------------
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Boolean apply(Object... args)
  {
//    Number left = (Number) args[0], right = (Number) args[1];
//    
//    return comparison.test(left.doubleValue(), right.doubleValue());
    
    return comparison.test((Comparable) args[0], (Comparable) args[1]);
  }
  
//------------------------------------------------------------------------------
}
