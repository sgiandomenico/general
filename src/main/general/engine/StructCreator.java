package general.engine;

import java.util.*;

import general.compiler.*;

public class StructCreator implements Function
{
  
  public static class StructMeta
  {
    public String name;
    public final Map<String, MemberMeta> members = new LinkedHashMap<>();
    
    public void addMember(String name, Class<?> type)
    {
      members.put(name, new MemberMeta(name, type));
    }
  }
  
  public static class MemberMeta
  {
    public final String name;
    public final Class<?> type;
    public Function initializer;
    
    public MemberMeta(String name, Class<?> type)
    {
      this.name = name;
      this.type = type;
    }
  }
  
  @Override
  public Object apply(Object... args)
  {
    Symbol name = (Symbol) args[0];
    PartiallyBoundExpression builder = (PartiallyBoundExpression) args[1];
    
    StructMeta struct = new StructMeta();
    struct.name = name.name;
    Function fieldAdder = as -> {
      struct.addMember((String) as[0], (Class<?>) as[1]);
      return null;
    };
    Function builderFunc = builder.bind(Symbol.get("field"));
    builderFunc.apply(fieldAdder);
    
    byte[] classData = StructWriter.writeStruct(struct);
    
//    // TODO: Remove.
//    Object[] constantPool = Javap.getConstantPool(classData);
//    Javap.printConstantPool(constantPool, System.err);
//    
//    // TODO: Remove.
//    ClassReader cr = new ClassReader(classData);
//    cr.accept(new TraceClassVisitor(new PrintWriter(System.err)), 0);
    
    Class<?> clazz = new CustomClassLoader().defineClass(struct.name, classData);
    
    return clazz;
  }
  
}
