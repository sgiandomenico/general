package general.compiler;

import java.lang.reflect.Method;

import general.ast.*;
import general.ast.MethodReference;
import general.engine.*;
import general.type.*;

public class TestCompiler
{
  public static String toString(Integer x, Integer radix)
  {
    return Integer.toString(x, radix);
  }
  
  public static void main(String[] args)
  {
    SymbolTable symTable = new SymbolTable(null);
    
    ClassMeta classMeta = new ClassMeta("general.SampleClass");
    
    FieldMeta someField = new FieldMeta();
    someField.name = "someField";
    someField.type = Type.fromClass(Integer.class);
    someField.isStatic = true;
    
    MethodMeta doSomething = new MethodMeta();
    doSomething.name = "doSomething";
    doSomething.type = new FunctionType(Type.fromClass(String.class), Type.fromClass(Integer.class));
    doSomething.isStatic = true;
    
    MethodReference toString = new MethodReference("toString");
    toString.type = new FunctionType(Type.fromClass(String.class),
        Type.fromClass(Integer.class), Type.fromClass(Integer.class));
    toString.isStatic = true;
    toString.definingClass = Type.fromClass(TestCompiler.class);
    symTable.add(toString);
    
    SymbolicReference toStringSym = new SymbolicReference(toString.getID());
//    toStringSym.reference = toString;
    
    LocalReference param0 = new LocalReference("x");
    param0.index = 0;
    param0.type = Type.fromClass(String.class);
    symTable.add(param0);
    
    SymbolicReference paramSym = new SymbolicReference(param0.getID());
//    paramSym.reference = param0;
    
    FieldReference field = new FieldReference(someField.name);
    field.type = someField.type;
    field.definingClass = new ClassType(classMeta.binaryName);
    field.isStatic = someField.isStatic;
    symTable.add(field);
    
    SymbolicReference fieldSym = new SymbolicReference(someField.name);
//    fieldSym.reference = field;
    
    doSomething.body = new Block(
        new Assignment(fieldSym, new Literal(16)),
        new Clause(toStringSym, paramSym, fieldSym));
    
    classMeta.addField(someField);
    classMeta.addMethod(doSomething);

    new SymbolResolver(symTable).resolveExpression(doSomething.body);
    
    System.out.println(doSomething.body);
    
    byte[] classData = GeneralClassWriter.write(classMeta);
    
//    // TODO: Remove.
//    Object[] constantPool = Javap.getConstantPool(classData);
//    Javap.printConstantPool(constantPool, System.err);
//    
//    // TODO: Remove.
//    ClassReader cr = new ClassReader(classData);
//    cr.accept(new TraceClassVisitor(new PrintWriter(System.err)), 0);
    
    Class<?> clazz = new CustomClassLoader().defineClass(classMeta.binaryName, classData);
    
    try
    {
      Method m = clazz.getMethod(doSomething.name, Integer.class);
      System.out.println("Result: " + m.invoke(null, 31));
    }
    catch (ReflectiveOperationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
