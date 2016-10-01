package general.ast;

import org.giandomenico.stephen.util.ObjectUtil;

public class Literal implements Expression
{
  public final Object value;
  
  public Literal(Object value)
  {
    this.value = value;
  }
  
  @Override
  public Class<?> getType()
  {
    return value == null ? null : value.getClass();
  }
  
  @Override
  public String toString()
  {
    return ObjectUtil.deepToString(value);
  }
  
}
