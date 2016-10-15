package general.ast;

import general.type.Type;

public class Block implements Expression
{
  public final Expression[] body;
  
  public Block(Expression... body)
  {
    this.body = body;
  }
  
  @Override
  public Type getType()
  {
    return body[body.length - 1].getType();
  }
  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    for (Expression exp : body)
    {
      sb.append(exp);
      sb.append("; ");
    }
    sb.append("}");
    
    return sb.toString();
  }
  
}
