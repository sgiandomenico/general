package general.ast;

public class LocalReference extends VariableReference
{
  public int index;
  
  public LocalReference(String id)
  {
    super(id);
  }
  
  public int getIndex()
  {
    return index;
  }
}
