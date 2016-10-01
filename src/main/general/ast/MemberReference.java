package general.ast;

import org.giandomenico.stephen.util.NotYetImplementedException;

public class MemberReference implements Expression
{
  public final Expression target, member;
  
  public MemberReference(Expression target, Expression member)
  {
    this.target = target;
    this.member = member;
  }
  
  @Override
  public Class<?> getType()
  {
    throw new NotYetImplementedException();
  }
  
}
