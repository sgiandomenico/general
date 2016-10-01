package general.engine;

import java.util.Arrays;

public class FunctionCreator implements Function
{
  
  @Override
  public Object apply(Object... args)
  {
    Symbol[] argSymbols = Arrays.stream(args, 0, args.length - 1).toArray(Symbol[]::new);
    PartiallyBoundExpression block = (PartiallyBoundExpression) args[args.length - 1];
    
    return block.bind(argSymbols);
  }
  
}
