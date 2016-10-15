package general.interpreter;

import java.util.Arrays;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.ast.*;
import general.core.ObjectCreator;

public class ExpressionQuoter
{
//** Fields ********************************************************************

//  protected final static Symbol NEW = Symbol.get(ObjectCreator.NEW);
  protected final static SymbolicReference NEW = new SymbolicReference(ObjectCreator.NEW);
  
//** Methods *******************************************************************
  
  public Expression quote(Expression exp)
  {
    if (exp instanceof Block)
    {
      return quote((Block) exp);
    }
    else if (exp instanceof Clause)
    {
      return quote((Clause) exp);
    }
    else if (exp instanceof Literal)
    {
      return quote((Literal) exp);
    }
    else if (exp instanceof SymbolicReference)
    {
      return quote((SymbolicReference) exp);
    }
//    else if (exp instanceof Parameter)
//    {
//      return quote((Parameter) exp);
//    }
    else if (exp instanceof Assignment)
    {
      return quote((Assignment) exp);
    }
    else if (exp instanceof Declaration)
    {
      return quote((Declaration) exp);
    }
    else if (exp instanceof New)
    {
      return quote((New) exp);
    }
    else
    {
      throw new RuntimeException("Unrecognized expression type: " + exp);
    }
  }
  
  public Expression[] quote(Expression... exps)
  {
    return Arrays.asList(exps).stream().map(this::quote).toArray(Expression[]::new);
  }
  
//------------------------------------------------------------------------------
  
  public Expression quote(Block block)
  {
    return new Clause(NEW, new Literal(Block.class), new Literal(block.body));
//    return asNew(Block.class, block.body);
  }
  
  public Expression quote(Clause sexp)
  {
    return new Clause(NEW, new Literal(Clause.class), quote(sexp.verb), new Literal(sexp.args));
//    return new Clause(NEW, new Literal(Clause.class), quote(sexp.verb), quote(sexp.args));

//    Expression[] args = new Expression[sexp.args.length + 1];
//    args[0] = sexp.verb;
//    System.arraycopy(sexp.args, 0, args, 1, sexp.args.length);
//    
//    return asNew(Clause.class, args);
  }
  
  public Expression quote(Literal c)
  {
    return new Clause(NEW, new Literal(Literal.class), new Literal(c.value));
  }
  
  public Expression quote(SymbolicReference var)
  {
    return new Clause(NEW, new Literal(SymbolicReference.class), new Literal(var.symbol.name));
  }
  
  public Expression quote(Assignment assign)
  {
    return new Clause(NEW, new Literal(Assignment.class), new Literal(assign.symbol), quote(assign.value));
  }
  
  public Expression quote(Declaration decl)
  {
    return new Clause(NEW, new Literal(Declaration.class), new Literal(decl.symbol));
  }
  
  public Expression quote(New newObj)
  {
    throw new NotYetImplementedException();
  }
  
//------------------------------------------------------------------------------
  
  @Deprecated
  public Expression asNew(Class<?> exprClass, Expression... args)
  {
    Expression[] newArgs = new Expression[args.length + 1];
    newArgs[0] = new Literal(exprClass);
    System.arraycopy(quote(args), 0, newArgs, 1, args.length);
    
    return new Clause(NEW, newArgs);
  }
  
//------------------------------------------------------------------------------
}
