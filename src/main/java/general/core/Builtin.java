package general.core;

import java.util.*;

import org.giandomenico.stephen.util.StringUtil;

import general.engine.*;
import general.interpreter.Executor;

public class Builtin
{
//** Constructors **************************************************************
  
  private Builtin()
  {
    ;
  }
  
//** Fields ********************************************************************

//  public static final String RUNTIME_VARIABLE_NAME = null;
  public static final String PARSE_COMMAND_NAME = " ";
  public static final String EXECUTE_COMMAND_NAME = "";
  
//------------------------------------------------------------------------------

//  public static final Parser BuiltinParser = new Parser();
  public static final Executor BuiltinExecute = new Executor();
//  public static final GetScope BuiltinScopeAccessor = new GetScope();
  public static final JavaClassLoader BuiltinClassLoader = new JavaClassLoader();
  
//  public static final DeclareOperator BuiltinDeclare = new DeclareOperator();
//  public static final AssignOperator BuiltinAssign = new AssignOperator();
  
  public static final Function BuiltinExit = new Function() {
    @Override
    public Object apply(Object... args)
    {
      int status = args.length > 0 ? ((Number) args[0]).intValue() : 0;
      
      System.exit(status);
      
      return null;
    }
  };
  
  public static final Function BuiltinPrint = new Function() {
    @Override
    public Object apply(Object... args)
    {
      System.out.println(StringUtil.concatenate(" ", args));
      
      return null;
    }
  };
  
  public static final Function BuiltinInspect = new Function() {
    @Override
    public Object apply(Object... args)
    {
      VariableScope scope = (VariableScope) args[0];
      String group = args.length > 1 ? (String) args[1] : "stack";
      
      if (group.equals("stack"))
      {
        Deque<VariableScope> stack = new ArrayDeque<>();
        
        while (scope != null)
        {
          stack.push(scope);
          scope = scope.parentScope;
        }
        
        String indent = "";
        for (int frame = 0; !stack.isEmpty(); frame++)
        {
          System.out.println(indent + "[Frame " + frame + "]");
          for (Map.Entry<String, Object> var : stack.pop().getLocalMap().entrySet())
            System.out.println(indent + var);
          
          indent += "  ";
        }
      }
      else
      {
        Map<String, Object> variables = group.matches("locals?")
            ? scope.getLocalMap()
            : scope.getAccessibleMap();
        
        for (Map.Entry<String, Object> var : variables.entrySet())
          System.out.println(var);
      }
      
      return null;
    }
  };
  
}
