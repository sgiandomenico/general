package general.ast;

import java.util.List;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.type.Type;

public class Clause implements Expression
{
//** Fields ********************************************************************
  
  // TODO: Special handling?
  public final static Clause EMPTY_STATEMENT = new Clause(null);
  
  public final Expression verb;
  public final Expression[] args;
  
//** Constructors **************************************************************
  
  public Clause(Expression verb, Expression... args)
  {
    this.verb = verb;
    this.args = args;
  }
  
  public static Clause create(List<Expression> exps)
  {
    return new Clause(exps.get(0), exps.subList(1, exps.size()).toArray(new Expression[0]));
  }
  
//** Methods *******************************************************************
  
  @Override
  public Type getType()
  {
    throw new NotYetImplementedException();
//    return verb.getType();
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(verb);
    sb.append("( ");
    for (Expression exp : args)
    {
      sb.append(exp);
      sb.append(" ");
    }
    sb.append(")");
    
    return sb.toString();
  }
  
//------------------------------------------------------------------------------
}
