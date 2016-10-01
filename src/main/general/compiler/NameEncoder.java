package general.compiler;

import org.giandomenico.stephen.util.NotYetImplementedException;

public class NameEncoder
{
  public static final String JVM_RESERVED_CHARS = ".;[/<>:";
  public static final String GENERAL_RESERVED_CHARS = "$";
  public static final String RESERVED_CHARS = JVM_RESERVED_CHARS + GENERAL_RESERVED_CHARS;
  
  public static String encode(String name)
  {
    // TODO: Generalize?
    if ("<init>".equals(name) || "<clinit>".equals(name))
      return name;
    
    throw new NotYetImplementedException();
  }
  
  public static String decode(String name)
  {
    throw new NotYetImplementedException();
  }
}
