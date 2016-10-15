package general.interpreter;

@SuppressWarnings("serial")
public class VariableConflictException extends RuntimeException
{
  public VariableConflictException(String variableName)
  {
    super("Variable already defined with name: " + variableName);
  }
}
