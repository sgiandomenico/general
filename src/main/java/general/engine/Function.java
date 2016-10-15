package general.engine;

public interface Function
{
//** Methods *******************************************************************
  
  public default Class<?> getType()
  {
    return Object.class;
  }
  
//  public Class<?>[] getArugmentTypes();
  
  public Object apply(Object... args);
  
//------------------------------------------------------------------------------
}
