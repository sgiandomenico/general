package general.ast;

import general.type.*;

public class MethodReference implements NameReference
{
  private final String id;
  public Type definingClass;
  public FunctionType type;
  
  public boolean isStatic;
  
  public MethodReference(String id)
  {
    this.id = id;
  }
  
  @Override
  public String getID()
  {
    return id;
  }
  
  @Override
  public FunctionType getType()
  {
    return type;
  }
}
