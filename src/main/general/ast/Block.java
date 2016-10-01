package general.ast;

public class Block implements Expression
{
  public final Expression[] body;
  
  public Block(Expression... body)
  {
    this.body = body;
  }
  
  @Override
  public Class<?> getType()
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
