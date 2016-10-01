package general.engine;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.stream.Collectors;

import general.ast.Expression;
import general.compiler.*;
import general.interpreter.Executor;
import general.util.CollapsibleFunction;

public class CompileableFunction extends CollapsibleFunction
{
//** Fields ********************************************************************
  
  private static volatile int functionIndex = 0;
  
//** Constructors **************************************************************
  
  public CompileableFunction(Expression expr, VariableScope parentScope, Symbol... symbols)
  {
    coreFunction = new BootstrapFunction() {
      @Override
      protected Function bootstrap()
      {
        return generateFunction(expr, parentScope, symbols);
      }
    };
  }
  
//** Methods *******************************************************************
  
  // TODO: Fix synchronization?
  public Function compile()
  {
    if (coreFunction instanceof BootstrapFunction)
      return ((BootstrapFunction) coreFunction).collapse();
    else
      return coreFunction;
  }
  
//------------------------------------------------------------------------------
  
  protected static int nextIndex()
  {
    return functionIndex++;
  }
  
  public static Function generateFunction(Expression expr, VariableScope parentScope, Symbol... symbols)
  {
//    return wrapFunction(expr, parentScope, symbols);
    return compileFunction(expr, parentScope, symbols);
  }
  
  public static Function wrapFunction(Expression expr, VariableScope parentScope, Symbol... symbols)
  {
    System.err.println("Wrapping expression: " + expr);
    
    return new Function() {
      @Override
      public Object apply(Object... args)
      {
        if (symbols.length != args.length)
          throw new IllegalArgumentException();
        
        VariableScope functionScope = parentScope.extend();
        for (int i = 0; i < symbols.length; i++)
        {
          functionScope.declare(symbols[i].name);
          functionScope.assign(symbols[i].name, args[i]);
        }
        
        return new Executor().evaluate(expr, functionScope);
      }
      
      @Override
      public String toString()
      {
        return Arrays.stream(symbols).map(s -> s.toString()).collect(Collectors.joining(",", "(", ")"))
            + " => " + expr.toString();
      }
    };
  }
  
  public static Function compileFunction(Expression expr, VariableScope parentScope, Symbol... symbols)
  {
    System.err.println("Compiling expression: " + expr);
    int funcIndex = nextIndex();
    
    byte[] classData = ClosureWriter.writeSexpClass("AnonymousFunction" + funcIndex, expr, symbols);
    
//    // TODO: Remove.
//    Object[] constantPool = Javap.getConstantPool(classData);
//    Javap.printConstantPool(constantPool, System.err);
//    
//    // TODO: Remove.
//    ClassReader cr = new ClassReader(classData);
//    cr.accept(new TraceClassVisitor(new PrintWriter(System.err)), 0);
    
    Class<?> clazz = new CustomClassLoader().defineClass("AnonymousFunction" + funcIndex, classData);
    
    try
    {
      Constructor<?> constructor = clazz.getConstructor(VariableScope.class);
      return (Function) constructor.newInstance(parentScope);
    }
//    catch (NoSuchMethodException | SecurityException e)
//    {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      throw new RuntimeException(e);
//    }
    catch (ReflectiveOperationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
//------------------------------------------------------------------------------
}
