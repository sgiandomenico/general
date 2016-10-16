package general.compiler;

import general.ast.NameReference;

public interface NameServer
{
  public NameReference lookup(String name);
  
  public boolean add(String name, NameReference ref);
  
  default public boolean add(NameReference ref)
  {
    return add(ref.getID(), ref);
  }
}
