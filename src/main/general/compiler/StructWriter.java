package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.lang.invoke.MethodType;
import java.util.*;

import org.objectweb.asm.*;

import general.engine.StructCreator.*;

public class StructWriter
{
//** Fields ********************************************************************
  
  static final Type SUPER_TYPE = Type.getType(Object.class);
  
//------------------------------------------------------------------------------
  
  final StructMeta struct;
  
  final String className;
  final ClassWriter cw;
  
//** Constructors **************************************************************
  
  private StructWriter(StructMeta struct)
  {
    this.struct = struct;
    this.className = struct.name.replace('.', '/');
    this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
  }
  
  public static byte[] writeStruct(StructMeta struct)
  {
    return new StructWriter(struct).write();
  }
  
//** Methods *******************************************************************
  
  public byte[] write()
  {
    cw.visit(V1_8, ACC_PUBLIC, className, null, SUPER_TYPE.getInternalName(), null);
    
    for (MemberMeta meta : struct.members.values())
    {
      // + ACC_FINAL
      cw.visitField(ACC_PUBLIC, meta.name, Type.getType(meta.type).getDescriptor(), null, null);
    }
    
    writeConstructor();
    writeToString();
    
//    ClosureWriter closureWriter = new ClosureWriter(className);
//    closureWriter.writeDefaultConstructor(cw, "java/lang/Object");

//    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "apply", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
//    ClosureMethodWriter mw = closureWriter.new ClosureMethodWriter(mv, parameters);
//    mw.writeAsMethod(exp);
    
    cw.visitEnd();
    return cw.toByteArray();
  }
  
//------------------------------------------------------------------------------
  
  public String getClassName()
  {
    return struct.name.replace('.', '/');
  }
  
  public void writeConstructor()
  {
    List<MemberMeta> members = new ArrayList<>(struct.members.values());
    MethodType ctype = MethodType.methodType(void.class, members.stream().map(meta -> meta.type).toArray(Class[]::new));
    
    MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", ctype.toMethodDescriptorString(), null, null);
    init.visitCode();
    init.visitVarInsn(ALOAD, 0);
    init.visitMethodInsn(INVOKESPECIAL, SUPER_TYPE.getInternalName(), "<init>", "()V", false);
    for (int i = 0; i < members.size(); i++)
    {
      init.visitVarInsn(ALOAD, 0);
      init.visitVarInsn(ALOAD, i + 1);
      init.visitFieldInsn(PUTFIELD, className, members.get(i).name, Type.getType(members.get(i).type).getDescriptor());
    }
    init.visitInsn(RETURN);
    init.visitMaxs(1, 1);
    init.visitEnd();
  }
  
  public void writeToString()
  {
    final MethodReference appendStr = MethodReference.get(StringBuilder.class, "append", String.class);
    final MethodReference appendObj = MethodReference.get(StringBuilder.class, "append", Object.class);
    List<MemberMeta> members = new ArrayList<>(struct.members.values());
    
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
    mv.visitCode();
    
    // Create StringBuilder.
    mv.visitTypeInsn(NEW, Type.getInternalName(StringBuilder.class));
    mv.visitInsn(DUP);
    mv.visitLdcInsn(struct.name);
    ConstructorReference.get(StringBuilder.class, String.class).visitInsn(mv);
    
    mv.visitLdcInsn("(");
    appendStr.visitInsn(mv);
    
    boolean first = true;
    for (MemberMeta member : members)
    {
      if (first)
      {
        first = !first;
      }
      else
      {
        mv.visitLdcInsn(", ");
        appendStr.visitInsn(mv);
      }
      
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, member.name, Type.getType(member.type).getDescriptor());
      appendObj.visitInsn(mv);
    }
    
    mv.visitLdcInsn(")");
    appendStr.visitInsn(mv);
    
    MethodReference.get(StringBuilder.class, "toString").visitInsn(mv);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
  
//------------------------------------------------------------------------------
}
