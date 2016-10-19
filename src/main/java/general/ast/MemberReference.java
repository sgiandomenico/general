package general.ast;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.type.Type;

public class MemberReference implements Expression
{
  public final Expression subject, member;
  
  public MemberReference(Expression subject, Expression member)
  {
    this.subject = subject;
    this.member = member;
  }
  
  @Override
  public Type getType()
  {
    throw new NotYetImplementedException();
  }
  
}
