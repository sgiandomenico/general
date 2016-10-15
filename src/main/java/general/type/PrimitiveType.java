package general.type;

public enum PrimitiveType implements Type
{
  BOOLEAN("boolean", "Z", Boolean.TYPE),
  BYTE("byte", "B", Byte.TYPE),
  SHORT("short", "S", Short.TYPE),
  INTEGER("int", "I", Integer.TYPE),
  LONG("long", "J", Long.TYPE),
  FLOAT("float", "F", Float.TYPE),
  DOUBLE("double", "D", Double.TYPE),
  CHARACTER("char", "C", Character.TYPE),
  VOID("void", "V", Void.TYPE);

//** Fields ********************************************************************
  
  final String binaryName;
  final String descriptor;
  final Class<?> javaClass;
  
//** Constructors **************************************************************
  
  private PrimitiveType(String binaryName, String descriptor, Class<?> javaClass)
  {
    this.binaryName = binaryName;
    this.descriptor = descriptor;
    this.javaClass = javaClass;
  }
  
//------------------------------------------------------------------------------
  
  public static PrimitiveType fromClass(Class<?> clazz)
  {
    for (PrimitiveType type : values())
    {
      if (type.javaClass == clazz)
        return type;
    }
    
    return null;
  }
  
//** Methods *******************************************************************
  
  @Override
  public String getBinaryName()
  {
    return binaryName;
  }
  
  @Override
  public String getDescriptor()
  {
    return descriptor;
  }
  
//------------------------------------------------------------------------------
}
