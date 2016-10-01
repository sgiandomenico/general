package general.interpreter;

import java.util.*;

import general.ast.*;

public class BinaryCreator extends ParentheticalAdaptor
{
  
  @Override
  public Expression apply(Expression[] exps)
  {
    Queue<Expression> remaining = new LinkedList<>(Arrays.asList(exps));
    
    Expression result = remaining.remove();
    
    while (!remaining.isEmpty())
    {
      Expression op = remaining.remove();
      Expression right = remaining.remove();
      result = new Clause(op, result, right);
    }
    
    return result;
  }
  
}
