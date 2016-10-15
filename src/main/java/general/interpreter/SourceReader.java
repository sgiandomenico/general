package general.interpreter;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;

import general.ast.Block;
import general.engine.*;

public class SourceReader implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { InputStream.class };
//  private static final Class<?>[] argumentTypes = new Class<?>[] { String.class };
//  private static final Class<?>[] resultTypes = EMPTY_TYPES;
  
  private static final Pattern END_OF_STREAM_ANCHOR = Pattern.compile("\\Z");
  
  protected final Parser parser;
  protected final Executor executor;
  protected final VariableScope scope;
  
//** Constructors **************************************************************

//  public SourceReader()
//  {
//    super(argumentTypes, resultTypes);
//  }
  
  public SourceReader(Parser parser, Executor executor, VariableScope scope)
  {
    this.parser = parser;
    this.executor = executor;
    this.scope = scope;
  }
  
//** Methods *******************************************************************
  
  /*
  @Deprecated
  public static Expression findExpression(BufferedReader in, FlowRuntime runtime) throws IOException
  {
    String statement = "";
    
    scan: while (true)
    {
      String line = in.readLine();
      if (line == null)
        break;
      
      statement += line;
      
      try
      {
        Expression expression = runtime.parse(statement);
        if (expression == null)
          continue scan;
        
        return expression;
      }
      catch (IncompleteStatementException e)
      {
        continue scan;
      }
    }
    
    if (!Parser.WHITESPACE_OR_EMPTY.matcher(statement).matches())
      throw new IncompleteStatementException(statement);
    
    return null;
  }
  */
  
//------------------------------------------------------------------------------

//  @Override
//  public boolean areResultsValid(Object[] results)
//  {
//    return true;
//  }

//------------------------------------------------------------------------------
  
  @Override
  public Object apply(Object... args)
  {
    try (InputStream stream = new FileInputStream((String) args[0]);
         Scanner in = new Scanner(stream))
    {
      String source = in.useDelimiter(END_OF_STREAM_ANCHOR).next();
      
      Parser.ParseResult pr = parser.parse(source);
      return executor.evaluate(new Block(pr.statements), scope);
//      throw new NotYetImplementedException();
//      return runtime.execute(source);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  /*
  @Deprecated
  public Object[] eval2(FlowRuntime runtime, Object[] args)
  {
    InputStream stream = (InputStream) args[0];
    
    try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
    {
      while (true)
      {
        String statement = "";
        Expression expression = null;
        
        scan: while (true)
        {
          String line = in.readLine();
          if (line == null)
            break;
          
          statement += line;
          
          try
          {
            expression = runtime.parse(statement);
            break;
          }
          catch (IncompleteStatementException e)
          {
            continue scan;
          }
        }
        
        if (!Parser.WHITESPACE_OR_EMPTY.matcher(statement).matches())
          throw new IncompleteStatementException(statement);
        
        expression.evaluate(runtime);
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  @Deprecated
  public Object[] eval1(FlowRuntime runtime, Object[] args)
  {
    InputStream stream = (InputStream) args[0];
    
    try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
    {
      String statement = "";
      reading: while (true)
      {
        String line = in.readLine();
        
        if (line == null)
        {
          return null;
        }
        else if (line.length() == 0)
          continue;
        
        statement += line;
        
        try
        {
          Expression expression = runtime.parse(statement);
          expression.evaluate(runtime);
          statement = "";
        }
        catch (IncompleteStatementException e)
        {
          continue reading;
        }
      }
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
  */
  
//------------------------------------------------------------------------------
}
