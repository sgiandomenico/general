package general.interpreter;

@SuppressWarnings("serial")
public class NoSuchVariableException extends RuntimeException
{
  public NoSuchVariableException(String variableName)
  {
    super("No variable accessible from the current context with name: " + variableName);
  }
}
