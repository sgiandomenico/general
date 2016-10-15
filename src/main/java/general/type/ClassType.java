package general.type;

public class ClassType implements ReferenceType
{
  final String binaryName;
  
  public ClassType(String binaryName)
  {
    this.binaryName = binaryName;
  }
  
  public ClassType(Class<?> clazz)
  {
    this(clazz.getName());
  }
  
  @Override
  public String getBinaryName()
  {
    return binaryName;
  }
}
