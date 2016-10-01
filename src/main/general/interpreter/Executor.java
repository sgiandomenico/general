package general.interpreter;

import java.lang.reflect.*;
import java.util.Arrays;

import general.ast.*;
import general.engine.*;

public class Executor implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { String[].class };
//  private static final Class<?>[] resultTypes = EMPTY_TYPES;

//** Constructors **************************************************************

//  public Executor()
//  {
//    super(argumentTypes, resultTypes);
//  }

//** Methods *******************************************************************
  
  @Override
  public String toString()
  {
    return "BuiltinExecutor";
  }
  
//------------------------------------------------------------------------------
  
  /*
  // TODO: Generalize.
  @Override
  public boolean areArgumentsValid(Object[] args)
  {
    for (Object arg : args)
      if (!(arg instanceof String))
        return false;
    
    return true;
  }
  
  // TODO: Generalize.
  @Override
  public boolean areResultsValid(Object[] results)
  {
    return true;
  }
  
  @Override
  public ValuePackage eval(FlowRuntime runtime, ValuePackage args)
  {
    String[] rawTokens = ArrayUtil.cast(String.class, args.values);
    
    System.err.println("Executing: " + StringUtil.concatenate(", ", rawTokens));
    
    Expression parseExp = Clause.create(runtime.getParser(), new Literal(rawTokens));
    ParseResult parseResult = (ParseResult) parseExp.evaluate(runtime).values[0];
    System.err.println("Parsed: " + ObjectUtil.deepToString(parseResult.statements));
    
  //    Expression parseResult = (Expression) parseExp.evaluate(runtime).values[0];
  //    System.err.println("Parsed: " + parseResult.getName());
  //    Expression statement = (Expression) parseResult.evaluate(runtime).values[0];
    
    return new Block(runtime.getCurrentScope(), parseResult.statements).evaluate(runtime);
  }
  */
  
  @Override
  public Object apply(Object... args)
  {
    PartiallyBoundExpression block = (PartiallyBoundExpression) args[1];
    Function func = block.bind();
    return func.apply();
//    return evaluate((Expression) args[1], (VariableScope) args[0]);
  }
  
//------------------------------------------------------------------------------
  
  public Object evaluate(Expression expr, VariableScope scope)
  {
    return new ExpressionExecutor(scope).evaluate(expr);
  }
  
//** Inner Classes *************************************************************
  
  protected static class ExpressionExecutor
  {
    //** Fields ****************************************************************
    
    public final VariableScope scope;
    
    //** Constructors **********************************************************
    
    public ExpressionExecutor(VariableScope scope)
    {
      this.scope = scope;
    }
    
    //** Methods ***************************************************************
    
    public Object evaluate(Expression exp)
    {
      if (exp instanceof Block)
      {
        return evaluate((Block) exp);
      }
      if (exp instanceof Clause)
      {
        return evaluate((Clause) exp);
      }
      else if (exp instanceof Literal)
      {
        return evaluate((Literal) exp);
      }
      else if (exp instanceof SymbolicReference)
      {
        return evaluate((SymbolicReference) exp);
      }
//      else if (exp instanceof Parameter)
//      {
//        return evaluate((Parameter) exp);
//      }
      else if (exp instanceof Assignment)
      {
        return evaluate((Assignment) exp);
      }
      else if (exp instanceof Declaration)
      {
        return evaluate((Declaration) exp);
      }
      else if (exp instanceof New)
      {
        return evaluate((New) exp);
      }
      else
      {
        throw new RuntimeException("Unrecognized expression type: " + exp);
      }
    }
    
    //--------------------------------------------------------------------------
    
    public Object evaluate(Block block)
    {
      Object result = null;
      for (Expression expr : block.body)
        result = evaluate(expr);
      
      return result;
    }
    
    public Object evaluate(Clause clause)
    {
      Function func = (Function) evaluate(clause.verb);
      Object[] args = Arrays.asList(clause.args).stream().map(exp -> evaluate(exp)).toArray();
      return func.apply(args);
    }
    
    public Object evaluate(Literal c)
    {
      return c.value;
    }
    
    public Object evaluate(SymbolicReference ref)
    {
      return scope.resolve(ref.symbol.name);
    }
    
//    public Object evaluate(Parameter param)
//    {
//      return null;
//    }
    
    public Object evaluate(Declaration decl)
    {
      scope.declare(decl.symbol.name);
      return null;
    }
    
    public Object evaluate(Assignment assign)
    {
      // FIXME: Remove.
      scope.declare(assign.symbol.name);
      
      Object value = evaluate(assign.value);
      scope.assign(assign.symbol.name, value);
      return value;
    }
    
    public Object evaluate(New construct)
    {
      Object[] args = Arrays.asList(construct.constructorArgs).stream().map(exp -> evaluate(exp)).toArray();
      
      Constructor<?> constructor = getConstructor(construct.type, args);
      try
      {
        return constructor.newInstance(args);
      }
      catch (ReflectiveOperationException e)
      {
        throw new RuntimeException(e);
      }
    }
    
    //--------------------------------------------------------------------------
    
    protected static boolean instanceOfs(Class<?>[] classes, Object[] objs)
    {
      if (objs.length != classes.length)
        return false;
      
      for (int i = 0; i < objs.length; i++)
      {
        if (objs[i] != null && !classes[i].isInstance(objs[i]))
          return false;
      }
      
      return true;
    }
    
    protected static Constructor<?> getConstructor(Class<?> type, Object[] args)
    {
      for (Constructor<?> c : type.getConstructors())
      {
        if (instanceOfs(c.getParameterTypes(), args))
          return c;
      }
      
      return null;
    }
  }
  
//------------------------------------------------------------------------------
}
