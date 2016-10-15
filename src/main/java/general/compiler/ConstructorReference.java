package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Constructor;

import org.objectweb.asm.*;

public class ConstructorReference
{
//** Fields ********************************************************************
  
  public final Constructor<?> constructor;
  public final Class<?> clazz;
  public final String methodDescriptor;
  
//** Constructors **************************************************************
  
  public ConstructorReference(Constructor<?> constructor)
  {
    this.constructor = constructor;
    this.clazz = constructor.getDeclaringClass();
    this.methodDescriptor = Type.getConstructorDescriptor(constructor);
  }
  
  public static ConstructorReference get(Class<?> clazz, Class<?>... parameters)
  {
    try
    {
      Constructor<?> c = clazz.getConstructor(parameters);
      return new ConstructorReference(c);
    }
    catch (NoSuchMethodException | SecurityException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  
//** Methods *******************************************************************

//  public static String getFullTypeDescriptor(Class<?> clazz)
//  {
//    String clazzName = clazz.getName().replace('.', '/');
//    if (clazz.isPrimitive() || clazz.isArray())
//      return clazzName;
//    
//    return "L" + clazzName + ";";
//  }
//  
//  public static String getDescriptor(Constructor<?> c)
//  {
//    String args = Arrays.stream(c.getParameterTypes())
//        .map(cl -> getFullTypeDescriptor(cl))
//        .collect(Collectors.joining());
//    
//    return "(" + args + ")V";
//  }

//------------------------------------------------------------------------------
  
  public void visitInsn(MethodVisitor mv)
  {
    mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(clazz), "<init>", methodDescriptor, false);
  }
  
//------------------------------------------------------------------------------
}
