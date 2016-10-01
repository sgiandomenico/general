package general.interpreter;

import org.giandomenico.stephen.util.InputFormatException;

@SuppressWarnings("serial")
public class SyntaxErrorException extends InputFormatException
{
  public SyntaxErrorException(String statement, String message)
  {
    super(message + statement);
  }
  
  public SyntaxErrorException(String statement, int position)
  {
    this(statement, "Syntax error at position " + position + ": ");
  }
}
