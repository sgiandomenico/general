package general.ast;

import java.util.*;
import java.util.stream.Collectors;

import general.type.Type;

public interface Expression
{
//** Methods *******************************************************************
  
  // Note: In order for clauses to resolve types, they need to resolve the type of functions.
  // Actually, more generally symbols need to be able to resolve their types.
  // Note that the type of an expression may not yet exist as an actual class.
  @Deprecated
  public Type getType();
  
  // FIXME!
  @Deprecated
  public default String getName()
  {
    return "";
  }
  
//  public New getConstructor();

//------------------------------------------------------------------------------
  
  public static Expression[] quote(Expression... exps)
  {
    return Arrays.asList(exps).stream().map(exp -> new Literal(exp)).toArray(Expression[]::new);
  }
  
  public static List<Expression> quote(List<Expression> exps)
  {
    return exps.stream().map(exp -> new Literal(exp)).collect(Collectors.toList());
  }
  
//------------------------------------------------------------------------------
}
