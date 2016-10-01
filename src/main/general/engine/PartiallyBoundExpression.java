package general.engine;

import general.ast.Expression;

public class PartiallyBoundExpression
{
  public final Expression expr;
  public final VariableScope parentScope;
  
  public PartiallyBoundExpression(Expression expr, VariableScope parentScope)
  {
    this.expr = expr;
    this.parentScope = parentScope;
  }
  
  public Function bind(Symbol... symbols)
  {
    return new CompileableFunction(expr, parentScope, symbols);
//    return new Function() {
//      @Override
//      public Object apply(Object... args)
//      {
//        if (symbols.length != args.length)
//          throw new IllegalArgumentException();
//        
//        VariableScope functionScope = parentScope.extend();
//        for (int i = 0; i < symbols.length; i++)
//        {
//          functionScope.declare(symbols[i].name);
//          functionScope.assign(symbols[i].name, args[i]);
//        }
//        
//        return new Executor().evaluate(expr, functionScope);
//      }
//      
//      @Override
//      public String toString()
//      {
//        return Arrays.stream(symbols).map(s -> s.toString()).collect(Collectors.joining(",", "(", ")"))
//            + " => " + PartiallyBoundExpression.this.toString();
//      }
//    };
  }
  
  @Override
  public String toString()
  {
    return expr.toString();
  }
}
