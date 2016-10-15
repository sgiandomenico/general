package general.ast;

import general.type.Type;

public abstract class VariableReference implements NameReference
{
  private final String id;
  public Type type;
  
  public VariableReference(String id)
  {
    this.id = id;
  }
  
  @Override
  public String getID()
  {
    return id;
  }
  
  @Override
  public Type getType()
  {
    return type;
  }
}
