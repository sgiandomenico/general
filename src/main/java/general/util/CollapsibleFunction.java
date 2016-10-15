package general.util;

import general.engine.Function;

public class CollapsibleFunction implements Function
{
//** Inner Classes *************************************************************
  
  protected abstract class BootstrapFunction implements Function
  {
    /**
     * Generates the collapsed function.
     * 
     * @return The collapsed function.
     */
    // TODO: Rename?
    protected abstract Function bootstrap();
    
    /**
     * Collapses <code>coreFunction</code>.
     * 
     * @return The new collpased function.
     */
    public synchronized Function collapse()
    {
      if (coreFunction != this)
        return coreFunction;
      
      return (coreFunction = bootstrap());
    }
    
    @Override
    public Object apply(Object... args)
    {
      return collapse().apply(args);
    }
  }
  
//** Fields ********************************************************************
  
  /**
   * Collapsing function. This must be assigned prior to invocation of the
   * {@link #apply()} method (ideally, by all constructors).
   */
  protected Function coreFunction;
  
//** Constructors **************************************************************

//  public CollapsableFunction(Function bootstrapFunction)
//  {
//    coreFunction = bootstrapFunction;
//  }

//** Methods *******************************************************************
  
  @Override
  public Object apply(Object... args)
  {
    return coreFunction.apply(args);
  }
  
//------------------------------------------------------------------------------
}
