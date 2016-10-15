package general.ast;

import general.type.Type;

public class ClassReference implements NameReference
{
  private final Type type;
  
  public ClassReference(Type t)
  {
    this.type = t;
  }
  
  @Override
  public String getID()
  {
    return type.getBinaryName();
  }
  
  @Override
  public Type getType()
  {
    return type;
  }
}
