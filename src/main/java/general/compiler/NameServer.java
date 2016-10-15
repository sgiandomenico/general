package general.compiler;

import general.ast.NameReference;

public interface NameServer
{
  public NameReference lookup(String name);
}
