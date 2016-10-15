package general.ast;

import org.giandomenico.stephen.util.ObjectUtil;

import general.type.Type;

public class Literal implements Expression
{
  public final Object value;
  
  public Literal(Object value)
  {
    this.value = value;
  }
  
  @Override
  public Type getType()
  {
    return value == null ? null : Type.fromClass(value.getClass());
  }
  
  @Override
  public String toString()
  {
    return ObjectUtil.deepToString(value);
  }
  
}
