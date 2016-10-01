package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.*;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;

public class MethodReference
{
//** Fields ********************************************************************
  
  public final Method method;
  public final Class<?> clazz;
  public final String methodDescriptor;
  
//** Constructors **************************************************************
  
  public MethodReference(Method method)
  {
    this.method = method;
    this.clazz = method.getDeclaringClass();
    this.methodDescriptor = Type.getMethodDescriptor(method);
  }
  
  public static MethodReference get(Class<?> clazz, String methodName, Class<?>... parameters)
  {
    try
    {
      Method m = clazz.getMethod(methodName, parameters);
      return new MethodReference(m);
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
//  public static String getDescriptor(Method m)
//  {
//    String args = Arrays.stream(m.getParameterTypes()).map(c -> getFullTypeDescriptor(c)).collect(Collectors.joining());
//    String ret = getFullTypeDescriptor(m.getReturnType());
//    
//    return "(" + args + ")" + ret;
//  }
  
  int getOpcode()
  {
    if (Modifier.isStatic(method.getModifiers()))
      return INVOKESTATIC;
    else if (Modifier.isPrivate(method.getModifiers()))
      return INVOKESPECIAL;
    else
      return clazz.isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL;
  }
  
//------------------------------------------------------------------------------
  
  public void visitInsn(MethodVisitor mv)
  {
    mv.visitMethodInsn(getOpcode(),
        Type.getInternalName(clazz), method.getName(), methodDescriptor, clazz.isInterface());
  }
  
//  public MethodReference visitDeclare(ClassVisitor cv)
//  {
//    return cv.visitMethod()
//  }

//  public static void visit(MethodVisitor mv, Class<?> clazz, String methodName)
//  {
//    
//  }

//------------------------------------------------------------------------------
}
