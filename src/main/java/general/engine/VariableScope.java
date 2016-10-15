package general.engine;

import java.util.*;

import org.giandomenico.stephen.util.LayeredMap;

import general.interpreter.*;

public class VariableScope
{
//** Variables *****************************************************************
  
  public static final String LOCAL_SCOPE = "here";
  
//------------------------------------------------------------------------------
  
  public final VariableScope parentScope;
  
  private final Map<String, Object> local = new TreeMap<>(), accessible;
  
//** Constructors **************************************************************
  
  public VariableScope(VariableScope parentScope)
  {
    this.parentScope = parentScope;
    
    if (parentScope == null)
      accessible = local;
    else
      accessible = new LayeredMap<>(parentScope.accessible, local);
  }
  
//** Methods *******************************************************************

//  @Override
//  public String toString()
//  {
//    return toString("");
//  }
//  
//  protected String toString(String indent)
//  {
//    StringBuilder sb = new StringBuilder(indent + "VariableScope[\n");
//    
//    for (Variable<?> var : local.values())
//    {
//      sb.append(indent + var + "\n");
//    }
//    
//    if (parentScope != null)
//      sb.append(parentScope.toString(indent + "  "));
//    
//    sb.append("\n" + indent + "]");
//    
//    return sb.toString();
//    
////    StringBuilder sb = new StringBuilder(indent + "VariableScope[\n");
////    
////    indent = "";
////    if (parentScope != null)
////      sb.append(parentScope.toString(null));
////    
////    for (Variable<?> var : local.values())
////    {
////      sb.append(indent + var + "\n");
////    }
////    
////    sb.append("\n" + indent + "]");
////    
////    return sb.toString();
//  }

//------------------------------------------------------------------------------
  
  public VariableScope extend()
  {
    return new VariableScope(this);
  }
  
//------------------------------------------------------------------------------
  
  public Object resolve(String name)
  {
    // FIXME
    if (LOCAL_SCOPE.equals(name))
      return this;
    
    if (!accessible.containsKey(name))
      throw new NoSuchVariableException(name);
    
    return accessible.get(name);
  }
  
  public Map<String, Object> getLocalMap()
  {
    return Collections.unmodifiableMap(local);
  }
  
//  public Set<Object> getLocalVariables()
//  {
//    return Collections.unmodifiableSet(new TreeSet<>(local.values()));
//  }
  
  public Map<String, Object> getAccessibleMap()
  {
    return Collections.unmodifiableMap(accessible);
  }
  
//  public Set<Object> getAccessibleVariables()
//  {
//    return Collections.unmodifiableSet(new TreeSet<>(accessible.values()));
//  }

//------------------------------------------------------------------------------
  
  // FIXME
  public void declare(String name)
  {
//    if (local.containsKey(name))
//      throw new VariableConflictException(name);
    
    local.put(name, null);
  }
  
  // FIXME
  public void assign(String name, Object value)
  {
//    if (!accessible.containsKey(name))
//      throw new NoSuchVariableException(name);

//    if (local.containsKey(name))
    if (local.containsKey(name) || !accessible.containsKey(name))
      local.put(name, value);
    else
      parentScope.assign(name, value);
  }
  
//  public void setVariable(Variable<?> variable)
//  {
//    local.put(variable.name, variable);
//  }
//  
//  public void addVariable(Variable<?> variable)
//  {
//    if (local.containsKey(variable.name))
//      throw new VariableConflictException(variable.name);
//      
//    setVariable(variable);
//  }
//  
//  public void addVariables(Variable<?>... variables)
//  {
//    for (Variable<?> var : variables)
//      addVariable(var);
//  }

//------------------------------------------------------------------------------

}
