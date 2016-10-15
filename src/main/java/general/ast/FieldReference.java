package general.ast;

import general.type.Type;

public class FieldReference extends VariableReference
{
  public Type definingClass;
  
  public boolean isStatic;
  
  public FieldReference(String id)
  {
    super(id);
  }
  
  public Type getDefiningClass()
  {
    return definingClass;
  }
}
