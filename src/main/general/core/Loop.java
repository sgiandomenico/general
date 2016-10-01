package general.core;

import general.engine.*;

public class Loop implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { Expression.class, Expression.class };
//  private static final Class<?>[] resultTypes = EMPTY_TYPES;

//** Constructors **************************************************************

//  public Loop()
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
//    Expression testExpression = (Expression) args.values[0];
//    Expression body = (Expression) args.values[1];
//    
//    ValuePackage results = ValuePackage.EMPTY_PACKAGE;
//    
//    while ((Boolean) testExpression.evaluate(runtime).values[0])
//      results = body.evaluate(runtime);
//    
//    return results;
//  }
  
  @Override
  public Object apply(Object... args)
  {
    PartiallyBoundExpression testExpression = (PartiallyBoundExpression) args[0];
    PartiallyBoundExpression body = (PartiallyBoundExpression) args[1];
    
    Function testFunc = testExpression.bind();
    Function bodyFunc = body.bind();
    
    Object result = null;
    
    while ((Boolean) testFunc.apply())
      result = bodyFunc.apply();
    
    return result;
  }
  
//------------------------------------------------------------------------------
}
