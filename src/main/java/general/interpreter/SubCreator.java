package general.interpreter;

import java.util.Arrays;

import general.ast.*;

public class SubCreator extends ParentheticalAdaptor
{
  
  @Override
  public Expression apply(Expression[] exps)
  {
    return Clause.create(Arrays.asList(exps));
  }
  
}
