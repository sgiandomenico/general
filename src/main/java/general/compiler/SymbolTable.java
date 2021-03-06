package general.compiler;

import java.util.*;

import general.ast.NameReference;

public class SymbolTable implements NameServer
{
//** Inner Classes *************************************************************

//  public static abstract class Descriptor
//  {
//    String id;
//    Type type;
//  }

//** Fields ********************************************************************
  
  final NameServer parent;
  
  final Map<String, NameReference> table = new LinkedHashMap<>();
  
//** Constructors **************************************************************
  
  public SymbolTable(NameServer parent)
  {
    this.parent = parent;
  }
  
//** Methods *******************************************************************
  
  @Override
  public NameReference lookup(String name)
  {
    NameReference ref = table.get(name);
    if (ref == null && parent != null)
      return parent.lookup(name);
    
    return ref;
  }
  
//------------------------------------------------------------------------------
  
  @Override
  public boolean add(String name, NameReference ref)
  {
    return table.putIfAbsent(name, ref) == null;
  }
  
//------------------------------------------------------------------------------
}
