package general.interpreter;

import org.giandomenico.stephen.util.ArrayUtil;

import general.ast.Expression;
import general.engine.Function;

public abstract class ParentheticalAdaptor implements Function
{
  
  @Override
  public Object apply(Object... args)
  {
    Expression[] body = ArrayUtil.cast(Expression.class, args);
    return apply(body);
  }
  
  public abstract Expression apply(Expression[] exps);
  
}
