package general.core;

import general.engine.*;

// TODO: Delay block creation? Create blocks immediately?
public class ConditionalFlow implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { Boolean.class, Expression.class, Expression.class };
//  private static final Class<?>[] resultTypes = EMPTY_TYPES;

//** Constructors **************************************************************

//  public ConditionalFlow()
//  {
//    super(argumentTypes, resultTypes);
//  }

//** Methods *******************************************************************

//  @Override
//  public ExecutionPriority getPriority()
//  {
//    return ExecutionPriority.DELAYED;
//  }

//------------------------------------------------------------------------------

//  @Override
//  public boolean areArgumentsValid(Object[] args)
//  {
//    if (!super.areArgumentsValid(args))
//      return false;
//    
//    for (Object arg : args)
//    {
//      if (arg == null)
//        return false;
//    }
//    
//    return true;
//  }
//  
//  @Override
//  public boolean areResultsValid(Object[] results)
//  {
//    return true;
//  }

//------------------------------------------------------------------------------

//  @Override
//  public ValuePackage eval(FlowRuntime runtime, ValuePackage args)
//  {
//    Boolean testResult = (Boolean) args.values[0];
//    Expression body = (Expression) (testResult ? args.values[1] : args.values[2]);
//    
//    return body.evaluate(runtime);
//  }
  
  @Override
  public Object apply(Object... args)
  {
//    Boolean testResult = (Boolean) args[0];
//    PartiallyBoundExpression body = (PartiallyBoundExpression) (testResult ? args[1] : args[2]);
//    
//    return body.bind().apply();
    
    Boolean testResult = (Boolean) args[0];
    Object body = testResult ? args[1] : args[2];
    
    Function f = body instanceof PartiallyBoundExpression ? ((PartiallyBoundExpression) body).bind() : (Function) body;
    
    return f.apply();
  }
  
//------------------------------------------------------------------------------
}
