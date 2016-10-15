package general.type;

// TODO: Handle arrays.
public interface Type
{
  /**
   * AKA Class name.
   * @return
   */
  public String getBinaryName();
  
  default public String getSimpleName()
  {
    return getBinaryName().substring(getBinaryName().lastIndexOf(".") + 1);
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
}
