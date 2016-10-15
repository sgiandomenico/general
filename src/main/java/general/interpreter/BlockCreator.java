package general.interpreter;

import general.ast.*;

public class BlockCreator extends ParentheticalAdaptor
{
  
  // FIXME
  @Override
  public Expression apply(Expression[] exps)
  {
    return new Block(exps);
  }
  
}
