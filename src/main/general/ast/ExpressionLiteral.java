package general.ast;

public class ExpressionLiteral implements Expression
{
  public final Expression expr;
  
  public ExpressionLiteral(Expression expr)
  {
    this.expr = expr;
  }
  
//** Methods *******************************************************************
  
  @Override
  public Class<?> getType()
  {
    return Expression.class;
  }
  
//------------------------------------------------------------------------------
}
