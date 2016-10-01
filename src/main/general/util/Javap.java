package general.util;

import java.io.*;
import java.util.Arrays;

import org.objectweb.asm.*;
import org.objectweb.asm.util.*;

public class Javap
{
//** Inner Classes *************************************************************

//  public static class ConstantPool
//  {
//    private final Object[] pool;
//    
//    public ConstantPool()
//    {
//      
//    }
//    
//    
//  }

//  public static class ClassDumper extends ClassVisitor
//  {
//    public final PrintStream out;
//    
//    public ClassDumper(PrintStream out)
//    {
//      super(Opcodes.ASM5, new TraceClassVisitor(new PrintWriter(out)));
//      this.out = out;
//    }
//    
//    @Override
//    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
//    {
//      out.println("Hello!");
//      
//      super.visit(version, access, name, signature, superName, interfaces);
//      
//      out.println("World!");
//    }
//  }
//  
//  public static class ClassPrinter extends Textifier
//  {
//    public ClassPrinter()
//    {
//      super(Opcodes.ASM5);
//    }
//    
//    @Override
//    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
//    {
//      super.visit(version, access, name, signature, superName, interfaces);
//      
//      buf.setLength(0);
//      buf.append("Hello World!");
//      text.add(buf.toString());
//    }
//  }

//** Methods *******************************************************************
  
  @SuppressWarnings("resource")
  public static void printClass(byte[] classData, PrintStream out)
  {
    Object[] constantPool = getConstantPool(classData);
    printConstantPool(constantPool, out);
    
    ClassReader cr = new ClassReader(classData);
    cr.accept(new TraceClassVisitor(new PrintWriter(out)), 0);
    
//    ClassReader cr = new ClassReader(classData);
//    cr.accept(new ClassDumper(out), 0);
//    cr.accept(new TraceClassVisitor(null, new ClassPrinter(), new PrintWriter(out)), 0);

//    clazz.
//    Runtime.getRuntime().exec("javap -v ");
  }
  
  public static Object[] getConstantPool(byte[] classData)
  {
    ClassReader cr = new ClassReader(classData);
    char[] readBuffer = new char[cr.getMaxStringLength()];
    
    Object[] constantPool = new Object[cr.getItemCount()];
//    ArrayList<Object> pool = new ArrayList<>();
    
    final boolean[] isKnownType = new boolean[256];
//    isKnownType[1] = true; // UTF8
    isKnownType[3] = true; // int
    isKnownType[4] = true; // float
    isKnownType[5] = true; // long
    isKnownType[6] = true; // double
    isKnownType[7] = true; // Class
    isKnownType[8] = true; // String
    isKnownType[16] = true; // MType
    for (int i = 0; i < 9; i++)
      isKnownType[20 + i] = true; // Handles
      
    for (int i = 0; i < constantPool.length; i++)
    {
      int pos = cr.getItem(i);
//      if (pos == 0 || cr.b[pos - 1] == 1)
      if (pos == 0 || !isKnownType[cr.b[pos - 1]])
        continue;
      Arrays.fill(readBuffer, (char) 0);
//      if (cr.b[pos - 1] == 1)
//        pool.add(cr.readUTF8(pos, readBuffer).trim());
//      else
//      pool.add(cr.readConst(i, readBuffer));
      constantPool[i] = cr.readConst(i, readBuffer);
    }
    
    return constantPool;
//    return pool.toArray(new Object[pool.size()]);
  }
  
  public static void printConstantPool(Object[] constantPool, PrintStream out)
  {
    out.println("Constant pool:");
    for (int i = 0; i < constantPool.length; i++)
    {
//      if (constantPool[i] == null)
//        out.printf("%4d = ?\n", i);
//      else
      if (constantPool[i] != null)
        out.printf("%4d = %-16s %s\n", i, constantPool[i].getClass().getSimpleName(), constantPool[i].toString());
    }
  }
  
//------------------------------------------------------------------------------

//  public static class ConstantPoolPeeker extends ClassWriter
//  {
//    public ConstantPoolPeeker(ClassReader classReader, int flags)
//    {
//      super(classReader, flags);
//    }
//    
//    public Object[] getConstantPool()
//    {
//      for (Item i : items)
//      {
//        
//      }
//    }
//    
//  }

//------------------------------------------------------------------------------
}
