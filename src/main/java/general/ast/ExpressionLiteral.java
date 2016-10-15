package general.ast;

import general.type.Type;

public class ExpressionLiteral implements Expression
{
  public final Expression expr;
  
  public ExpressionLiteral(Expression expr)
  {
    this.expr = expr;
  }
  
//** Methods *******************************************************************
  
  @Override
  public Type getType()
  {
    return Type.fromClass(Expression.class);
  }
  
//------------------------------------------------------------------------------
}
