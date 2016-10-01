package general.interpreter;

import java.util.Arrays;

import general.ast.*;

public class StatementCreator extends ParentheticalAdaptor
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { Expression.class };
//  private static final Class<?>[] resultTypes = new Class<?>[] { Clause.class };

//** Constructors **************************************************************

//  public StatementCreator()
//  {
//    super(argumentTypes, resultTypes);
//  }

//** Methods *******************************************************************

//  @Override
//  public ExecutionPriority getPriority()
//  {
//    return ExecutionPriority.DELAYED;
//  }
//  
//------------------------------------------------------------------------------
//  
//  @Override
//  public boolean areArgumentsValid(Object[] args)
//  {
//    if (args.length < 1 || !(args[0] instanceof Expression))
//      return false;
//    
//    return true;
//  }
//  
//------------------------------------------------------------------------------
//  
//  @Override
//  public Object[] eval(FlowRuntime runtime, Object[] args)
//  {
//    Expression[] body = ArrayUtil.cast(Expression.class, args);
//    
//    return new Object[] { Clause.create(body) };
//  }

//  @Override
//  public Object apply(Object... args)
//  {
//    Expression[] body = ArrayUtil.cast(Expression.class, args);
//    return Clause.create(Arrays.asList(body));
//  }
  
  @Override
  public Expression apply(Expression[] exps)
  {
    if (exps[0].getName().equals("assign"))
    {
      if (exps.length != 3)
        throw new IllegalArgumentException("Wrong number of arguments: " + (exps.length - 1));
      
      SymbolicReference ref = (SymbolicReference) exps[1];
      return new Assignment(ref.symbol, exps[2]);
    }
    else if (exps[0].getName().equals("var"))
    {
      if (exps.length != 2)
        throw new IllegalArgumentException("Wrong number of arguments: " + (exps.length - 1));
      
      SymbolicReference ref = (SymbolicReference) exps[1];
      return new Declaration(ref.symbol);
    }
    else if (exps[0].getName().equals("get"))
    {
      if (exps.length != 3)
        throw new IllegalArgumentException("Wrong number of arguments: " + (exps.length - 1));
      
      return new MemberReference(exps[0], exps[1]);
    }
    else
    {
      return Clause.create(Arrays.asList(exps));
    }
  }
  
//------------------------------------------------------------------------------
}
