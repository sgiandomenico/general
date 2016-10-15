package general.interpreter;

@SuppressWarnings("serial")
public class IncompleteStatementException extends SyntaxErrorException
{
  public IncompleteStatementException(String statement)
  {
    super(statement, "Incomplete statement: ");
  }
}
