package general.interpreter;

import general.ast.*;
import general.core.ObjectCreator;
import general.engine.*;

public class BlockExpressionCreator extends ParentheticalAdaptor
{
  
  @Override
  public Expression apply(Expression[] exps)
  {
    return new Clause(new SymbolicReference(ObjectCreator.NEW), new Literal(PartiallyBoundExpression.class),
        new Literal(new Block(exps)), new SymbolicReference(VariableScope.LOCAL_SCOPE));
//    return new New(PartiallyBoundExpression.class, new Literal(new Block(exps)),
//        new SymbolicReference(Symbol.get(VariableScope.LOCAL_SCOPE)));
  }
  
}
