package general.type;

// TODO: Handle arrays.
public interface Type
{
//** Fields ********************************************************************
  
  // TODO: Generalize to wildcard type.
  public final static Type ANY = new Type() {
    @Override
    public String getBinaryName()
    {
      return "ANY";
    }
    
    @Override
    public String getDescriptor()
    {
      return "ANY";
    }
  };
  
//** Methods *******************************************************************
  
  /**
   * AKA Class name.
   * 
   * @return
   */
  public String getBinaryName();
  
  default public String getSimpleName()
  {
    String binaryName = getBinaryName();
    return binaryName.substring(binaryName.lastIndexOf(".") + 1);
  }
  
  /**
   * AKA Internal name.
   * 
   * @return
   */
  default public String getClassFileName()
  {
    return getBinaryName().replaceAll("\\.", "/");
  }
  
  default public String getDescriptor()
  {
    return "L" + getClassFileName() + ";";
  }
  
//------------------------------------------------------------------------------
  
  public static Type fromClass(Class<?> clazz)
  {
    if (clazz.isPrimitive())
    {
      return PrimitiveType.fromClass(clazz);
    }
    else
    {
      return new ClassType(clazz);
    }
  }
  
//------------------------------------------------------------------------------
}
