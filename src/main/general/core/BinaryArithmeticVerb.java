package general.core;

import general.engine.Function;

public abstract class BinaryArithmeticVerb implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { Number.class, Number.class };
//  private static final Class<?>[] resultTypes = new Class<?>[] { Number.class };

//------------------------------------------------------------------------------
  
  public static final BinaryArithmeticVerb Add = new BinaryArithmeticVerb("+") {
    @Override
    public Number estimate(Number left, Number right)
    {
      return left.doubleValue() + right.doubleValue();
    }
  };
  
  public static final BinaryArithmeticVerb Subtract = new BinaryArithmeticVerb("-") {
    @Override
    public Number estimate(Number left, Number right)
    {
      return left.doubleValue() - right.doubleValue();
    }
  };
  
  public static final BinaryArithmeticVerb Multiply = new BinaryArithmeticVerb("*") {
    @Override
    public Number estimate(Number left, Number right)
    {
      return left.doubleValue() * right.doubleValue();
    }
  };
  
  public static final BinaryArithmeticVerb Divide = new BinaryArithmeticVerb("/") {
    @Override
    public Number estimate(Number left, Number right)
    {
      return left.doubleValue() / right.doubleValue();
    }
  };
  
  public static final BinaryArithmeticVerb Remainder = new BinaryArithmeticVerb("%") {
    @Override
    public Number estimate(Number left, Number right)
    {
      return left.doubleValue() % right.doubleValue();
    }
  };
  
//------------------------------------------------------------------------------
  
  public final String name;
  
//** Constructors **************************************************************

//  public BinaryArithmeticVerb()
//  {
//    this(null);
//  }
  
  public BinaryArithmeticVerb(String name)
  {
//    super(argumentTypes, resultTypes);
    
    this.name = name;
  }
  
//** Methods *******************************************************************
  
  @Override
  public String toString()
  {
    return name == null ? super.toString() : name;
  }
  
//------------------------------------------------------------------------------
  
  @Override
  public Object apply(Object... args)
  {
    return evaluate((Number) args[0], (Number) args[1]);
  }
  
  // FIXME!
  public Number evaluate(Number left, Number right)
  {
    Class<? extends Number> resultClass = Double.class;//coalesceType(left, right);
    
    return resultClass.cast(estimate(left, right));
  }
  
  public abstract Number estimate(Number left, Number right);
  
//------------------------------------------------------------------------------
}
