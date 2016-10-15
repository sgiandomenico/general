package general.engine;

import java.util.*;

public class ClassMeta
{
//** Fields ********************************************************************
  
  public final String binaryName;
  
  public final Map<String, FieldMeta> fields = new LinkedHashMap<>();
  public final Map<String, MethodMeta> methods = new LinkedHashMap<>();
  
//** Constructors **************************************************************
  
  public ClassMeta(String binaryName)
  {
    this.binaryName = binaryName;
  }
  
//** Methods *******************************************************************
  
  public boolean addField(FieldMeta field)
  {
    return fields.putIfAbsent(field.name, field) == null;
  }
  
  public boolean addMethod(MethodMeta method)
  {
    return methods.putIfAbsent(method.name, method) == null;
  }
  
//------------------------------------------------------------------------------
}
