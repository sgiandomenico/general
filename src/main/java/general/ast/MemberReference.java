package general.ast;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.type.Type;

public class MemberReference implements Expression
{
  public final Expression target, member;
  
  public MemberReference(Expression target, Expression member)
  {
    this.target = target;
    this.member = member;
  }
  
  @Override
  public Type getType()
  {
    throw new NotYetImplementedException();
  }
  
}
