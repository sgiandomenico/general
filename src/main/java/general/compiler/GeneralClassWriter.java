package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;

import org.giandomenico.stephen.util.NotYetImplementedException;
import org.objectweb.asm.*;
import org.objectweb.asm.Type;

import general.ast.*;
import general.engine.*;

public class GeneralClassWriter
{
//** Inner Classes *************************************************************
  
  @Deprecated
  class ClassMethodWriter extends MethodWriter
  {
    public ClassMethodWriter(MethodVisitor mv, List<Symbol> parameters)
    {
      super(mv, parameters);
    }
    
    @Override
    void writeLocalVariable(SymbolicReference var)
    {
      // TODO Auto-generated method stub
      throw new NotYetImplementedException();
    }
    
    @Override
    void writeParameter(int index)
    {
      // TODO Auto-generated method stub
      throw new NotYetImplementedException();
    }
    
    @Override
    void writeAssignment(Assignment assign)
    {
      // TODO Auto-generated method stub
      throw new NotYetImplementedException();
    }
    
    @Override
    void writeDeclaration(Declaration decl)
    {
      // TODO Auto-generated method stub
      throw new NotYetImplementedException();
    }
  }
  
//** Fields ********************************************************************
  
  static final Type SUPER_TYPE = Type.getType(Object.class);
  
  final ClassMeta classMeta;
  
  final String className;
  final ClassWriter cw;
  
//** Constructors **************************************************************
  
  public GeneralClassWriter(ClassMeta classMeta)
  {
    this.classMeta = classMeta;
    this.className = classMeta.binaryName.replace('.', '/');
    this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
  }
  
//** Methods *******************************************************************
  
  public static byte[] write(ClassMeta classMeta)
  {
    return new GeneralClassWriter(classMeta).write();
  }
  
  public byte[] write()
  {
    cw.visit(V1_8, ACC_PUBLIC, className, null, SUPER_TYPE.getInternalName(), null);
    
    writeFields();
    writeConstructor();
    writeMethods();
    
//    ClosureWriter closureWriter = new ClosureWriter(className);
//    closureWriter.writeDefaultConstructor(cw, "java/lang/Object");

//    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "apply", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
//    ClosureMethodWriter mw = closureWriter.new ClosureMethodWriter(mv, parameters);
//    mw.writeAsMethod(exp);
    
    cw.visitEnd();
    return cw.toByteArray();
  }
  
//------------------------------------------------------------------------------
  
  public void writeFields()
  {
    for (FieldMeta field : classMeta.fields.values())
    {
      int flags = ACC_PUBLIC;
      if (field.isStatic)
        flags |= ACC_STATIC;
      
      cw.visitField(flags, field.name, field.type.getDescriptor(), null, null);
    }
  }
  
  // TODO: Remove.
  public void writeConstructor()
  {
//    List<MethodMeta> members = new ArrayList<>(classMeta.methods.values());
//    MethodType ctype = MethodType.methodType(void.class, members.stream().map(meta -> meta.type).toArray(Class[]::new));

//    MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", ctype.toMethodDescriptorString(), null, null);
    MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    init.visitCode();
    init.visitVarInsn(ALOAD, 0);
    init.visitMethodInsn(INVOKESPECIAL, SUPER_TYPE.getInternalName(), "<init>", "()V", false);
//    for (int i = 0; i < members.size(); i++)
//    {
//      init.visitVarInsn(ALOAD, 0);
//      init.visitVarInsn(ALOAD, i + 1);
//      init.visitFieldInsn(PUTFIELD, className, members.get(i).name, Type.getType(members.get(i).type).getDescriptor());
//    }
    init.visitInsn(RETURN);
    init.visitMaxs(1, 1);
    init.visitEnd();
  }
  
  public void writeMethods()
  {
    for (MethodMeta method : classMeta.methods.values())
    {
      // FIXME
//      ClassMethodWriter mw = new ClassMethodWriter(cw.visitMethod(ACC_PUBLIC, method.name, "()V", null, null), null);
//      mw.writeAsMethod(method.body);
      int flags = ACC_PUBLIC;
      if (method.isStatic)
        flags |= ACC_STATIC;
      
      MethodWriter2 mw =
          new MethodWriter2(cw.visitMethod(flags, method.name, method.type.getDescriptor(), null, null));
      mw.writeAsMethod(method.body);
    }
  }
  
//------------------------------------------------------------------------------
}
