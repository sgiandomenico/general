package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;

import org.giandomenico.stephen.util.NotYetImplementedException;
import org.objectweb.asm.*;
import org.objectweb.asm.Type;

import general.ast.*;
import general.engine.*;
import general.interpreter.ExpressionQuoter;

public class ClosureWriter
{
//** Fields ********************************************************************

//  protected final static String INTERFACE_DESC = Descriptors.getShortDescriptor(Function.class);
//  protected final static String VAR_SCOPE_DESC = Descriptors.getShortDescriptor(VariableScope.class);
//  protected final static String INTERFACE_DESC = Type.getInternalName(Function.class);
//  protected final static String VAR_SCOPE_DESC = Type.getInternalName(VariableScope.class);
  protected final static Type INTERFACE_TYPE = Type.getType(Function.class);
  protected final static Type VAR_SCOPE_TYPE = Type.getType(VariableScope.class);
  
  protected final static String VAR_SCOPE_VAR = "lexicalScope";
  
  protected final static MethodReference VAR_EXTEND = MethodReference.get(VariableScope.class, "extend");
  protected final static MethodReference VAR_DECLARE =
      MethodReference.get(VariableScope.class, "declare", String.class);
  protected final static MethodReference VAR_ASSIGN =
      MethodReference.get(VariableScope.class, "assign", String.class, Object.class);
  protected final static MethodReference VAR_GET =
      MethodReference.get(VariableScope.class, "resolve", String.class);
  
//------------------------------------------------------------------------------
  
  public static byte[] writeSexpClass(String name, Expression exp, Symbol[] parameters)
  {
    String className = name.replace('.', '/');
    
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    cw.visit(V1_8, ACC_PUBLIC, className, null,
        "java/lang/Object", new String[] { INTERFACE_TYPE.getInternalName() });
    
    cw.visitField(ACC_PRIVATE, VAR_SCOPE_VAR, VAR_SCOPE_TYPE.getDescriptor(), null, null);
    
    ClosureWriter closureWriter = new ClosureWriter(className);
    closureWriter.writeDefaultConstructor(cw, "java/lang/Object");
    
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "apply", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
    ClosureMethodWriter mw = closureWriter.new ClosureMethodWriter(mv, parameters);
    mw.writeAsMethod(exp);
    
    cw.visitEnd();
    return cw.toByteArray();
  }
  
//------------------------------------------------------------------------------
  
  String className;
//  List<Symbol> parameters;

//** Constructors **************************************************************
  
  ClosureWriter(String className)
  {
    this.className = className;
//    this.parameters = Arrays.asList(parameters);
  }
  
//** Methods *******************************************************************
  
  void writeDefaultConstructor(ClassWriter cw, String parentClass)
  {
    MethodVisitor init = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + VAR_SCOPE_TYPE.getDescriptor() + ")V", null, null);
    init.visitCode();
    init.visitVarInsn(ALOAD, 0);
    init.visitMethodInsn(INVOKESPECIAL, parentClass, "<init>", "()V", false);
    init.visitVarInsn(ALOAD, 0);
    init.visitVarInsn(ALOAD, 1);
    init.visitFieldInsn(PUTFIELD, className, VAR_SCOPE_VAR, VAR_SCOPE_TYPE.getDescriptor());
    init.visitInsn(RETURN);
    init.visitMaxs(1, 1);
    init.visitEnd();
  }
  
//------------------------------------------------------------------------------
  
  protected class ClosureMethodWriter extends MethodWriter
  {
    //** Fields ****************************************************************
    
    //** Constructors **********************************************************
    
    public ClosureMethodWriter(MethodVisitor mv, Symbol[] parameters)
    {
      super(mv, Arrays.asList(parameters));
    }
    
    //** Methods ***************************************************************
    
    @Override
    void writeBody(Expression exp)
    {
      writeExtendScope();
      writeLoadParametersIntoScope();
      super.writeBody(exp);
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    void writeLiteral(Literal c)
    {
      if (c.value instanceof Expression)
      {
        Expression quoted = new ExpressionQuoter().quote((Expression) c.value);
        writeExpression(quoted);
      }
      else if (c.value instanceof Expression[])
      {
        Expression[] quoted = new ExpressionQuoter().quote((Expression[]) c.value);
        writeArray(Expression.class, quoted);
      }
      else
        super.writeLiteral(c);
    }
    
    //--------------------------------------------------------------------------
    
    void writeExtendScope()
    {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, className, VAR_SCOPE_VAR, VAR_SCOPE_TYPE.getDescriptor());
      VAR_EXTEND.visitInsn(mv);
      mv.visitVarInsn(ASTORE, parameters.size() + 1);
    }
    
    void writeGetScope()
    {
      mv.visitVarInsn(ALOAD, parameters.size() + 1);
    }
    
    // TODO: Optimize.
    void writeLoadParametersIntoScope()
    {
      for (int i = 0; i < parameters.size(); i++)
      {
        // Declare variable.
        writeGetScope();
        writeConstant(parameters.get(i).name);
        VAR_DECLARE.visitInsn(mv);
        
        // Assign variable.
        writeGetScope();
        writeConstant(parameters.get(i).name);
        writeParameter(i + 1);
        VAR_ASSIGN.visitInsn(mv);
      }
    }
    
    //--------------------------------------------------------------------------
    
    @Override
    void writeLocalVariable(SymbolicReference var)
    {
      writeGetScope();
      mv.visitLdcInsn(var.symbol.name);
      VAR_GET.visitInsn(mv);
    }
    
    @Override
    void writeParameter(int index)
    {
      mv.visitVarInsn(ALOAD, 1);
      writeConstant(index - 1);
      mv.visitInsn(AALOAD);
    }
    
    @Override
    void writeAssignment(Assignment assign)
    {
      if (!(assign.location instanceof SymbolicReference))
        throw new NotYetImplementedException();
      
      SymbolicReference symRef = (SymbolicReference) assign.location;
      
      writeGetScope();
      mv.visitLdcInsn(symRef.symbol.name);
      writeExpression(assign.value);
      VAR_ASSIGN.visitInsn(mv);
      
      // TODO: Clean up.
      // Copy value, to be left on stack.
      writeLocalVariable(symRef);
    }
    
    @Override
    void writeDeclaration(Declaration decl)
    {
      writeGetScope();
      mv.visitLdcInsn(decl.symbol.name);
//      mv.visitLdcInsn(null);
      VAR_DECLARE.visitInsn(mv);
      
      // TODO: Clean up.
      // Dummy value left on stack.
//      mv.visitInsn(ACONST_NULL);
      writeConstant(null);
    }
    
    //--------------------------------------------------------------------------
  }
  
//------------------------------------------------------------------------------
}

//class ClosureMethodWriter
//{
////** Fields ********************************************************************
//  
//  private final static MethodReference VAR_EXTEND = MethodReference.get(VariableScope.class, "extend");
//  private final static MethodReference VAR_DECLARE =
//      MethodReference.get(VariableScope.class, "declare", String.class);
//  private final static MethodReference VAR_ASSIGN =
//      MethodReference.get(VariableScope.class, "assign", String.class, Object.class);
//  private final static MethodReference VAR_GET =
//      MethodReference.get(VariableScope.class, "resolve", String.class);
//  
////------------------------------------------------------------------------------
//  
//  MethodVisitor mv;
//  
//  String className;
//  List<Symbol> parameters;
//  
////** Constructors **************************************************************
//  
//  ClosureMethodWriter(MethodVisitor mv, String className, Symbol[] parameters)
//  {
//    this.mv = mv;
//    this.className = className;
//    this.parameters = Arrays.asList(parameters);
//  }
//  
////** Methods *******************************************************************
//  
//  void writeApply(Expression exp)
//  {
//    mv.visitCode();
//    writeExtendScope();
//    writeExpression(exp);
//    mv.visitInsn(ARETURN);
//    mv.visitMaxs(1, 1);
//    mv.visitEnd();
//  }
//  
////------------------------------------------------------------------------------
//  
//  void writeExpression(Expression exp)
//  {
//    if (exp instanceof Block)
//    {
//      writeBlock((Block) exp);
//    }
//    else if (exp instanceof Clause)
//    {
//      writeClause((Clause) exp);
//    }
//    else if (exp instanceof Literal)
//    {
//      writeConstant((Literal) exp);
//    }
//    else if (exp instanceof SymbolicReference)
//    {
//      writeSymbolicReference((SymbolicReference) exp);
//    }
////    else if (exp instanceof Parameter)
////    {
////      writeParameter((Parameter) exp);
////    }
//    else if (exp instanceof Assignment)
//    {
//      writeAssignment((Assignment) exp);
//    }
//    else if (exp instanceof Declaration)
//    {
//      writeDeclaration((Declaration) exp);
//    }
//    else if (exp instanceof New)
//    {
//      writeNew((New) exp);
//    }
//    else
//    {
//      throw new RuntimeException("Unrecognized expression type: " + exp);
//    }
//  }
//  
//  void writeBlock(Block block)
//  {
//    // TODO: Optimize (a little).
//    writeExpression(block.body[0]);
//    for (Expression exp : Arrays.copyOfRange(block.body, 1, block.body.length, Expression[].class))
//    {
//      mv.visitInsn(POP);
//      writeExpression(exp);
//    }
//  }
//  
//  void writeClause(Clause sexp)
//  {
//    writeExpression(sexp.verb);
//    mv.visitTypeInsn(CHECKCAST, ClosureWriter.INTERFACE_TYPE.getInternalName());
//    
////    mv.visitLdcInsn(sexp.args.length);
////    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
////    for (int i = 0; i < sexp.args.length; i++)
////    {
////      mv.visitInsn(DUP);
////      mv.visitLdcInsn(i);
////      writeExpression(sexp.args[i]);
////      mv.visitInsn(AASTORE);
////    }
//    
//    writeArray(Object.class, sexp.args);
//    
//    mv.visitMethodInsn(INVOKEINTERFACE, ClosureWriter.INTERFACE_TYPE.getInternalName(), "apply",
//        "([Ljava/lang/Object;)Ljava/lang/Object;", true);
//  }
//  
//  void writeConstant(Literal c)
//  {
//    mv.visitLdcInsn(c.value);
//    
//    if (c.value instanceof Integer)
//      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//    else if (c.value instanceof Double)
//      mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
//    else if (c.value instanceof String)
//      ;
//    else
//      throw new NotYetImplementedException();
//  }
//  
//  void writeSymbolicReference(SymbolicReference var)
//  {
//    int parameterIndex = parameters.indexOf(var.symbol);
//    if (parameterIndex < 0)
//      writeGetVariable(var);
//    else
//      writeParameter(parameterIndex + 1);
//  }
//  
//  void writeGetVariable(SymbolicReference var)
//  {
//    writeGetScope();
//    mv.visitLdcInsn(var.symbol.name);
//    VAR_GET.visitInsn(mv);
//  }
//  
//  void writeParameter(int index)
//  {
//    mv.visitVarInsn(ALOAD, 1);
//    mv.visitLdcInsn(index - 1);
//    mv.visitInsn(AALOAD);
//  }
//  
//  void writeAssignment(Assignment assign)
//  {
//    writeGetScope();
//    mv.visitLdcInsn(assign.symbol.name);
//    writeExpression(assign.value);
//    VAR_ASSIGN.visitInsn(mv);
//    
//    // TODO: Clean up.
//    // Copy value, to be left on stack.
//    writeGetVariable(new SymbolicReference(assign.symbol));
//  }
//  
//  void writeDeclaration(Declaration decl)
//  {
//    writeGetScope();
//    mv.visitLdcInsn(decl.symbol.name);
////    mv.visitLdcInsn(null);
//    VAR_DECLARE.visitInsn(mv);
//    
//    // TODO: Clean up.
//    // Dummy value left on stack.
//    mv.visitInsn(ACONST_NULL);
//  }
//  
//  void writeNew(New newObj)
//  {
//    String typeDesc = newObj.type.getName().replace('.', '/');
////    Constructor c = 
//    
//    mv.visitTypeInsn(NEW, typeDesc);
//    for (Expression arg : newObj.constructorArgs)
//      writeExpression(arg);
//    // FIXME: Correct args.
//    mv.visitMethodInsn(INVOKESPECIAL, typeDesc, "<init>", "()V", false);
//  }
//  
////------------------------------------------------------------------------------
//  
//  void writeExtendScope()
//  {
//    mv.visitVarInsn(ALOAD, 0);
//    mv.visitFieldInsn(GETFIELD, className, ClosureWriter.VAR_SCOPE_VAR, ClosureWriter.VAR_SCOPE_TYPE.getDescriptor());
//    VAR_EXTEND.visitInsn(mv);
//    mv.visitVarInsn(ASTORE, parameters.size() + 1);
//  }
//  
//  void writeGetScope()
//  {
////    mv.visitVarInsn(ALOAD, 0);
////    mv.visitFieldInsn(GETFIELD, className, VAR_SCOPE_VAR, "L" + VAR_SCOPE_DESC + ";");
//    
//    mv.visitVarInsn(ALOAD, parameters.size() + 1);
//  }
//  
////------------------------------------------------------------------------------
//  
//  void writeConstant(Object c)
//  {
////      if (c instanceof Integer) {
////        // ...
////    } else if (c instanceof Float) {
////        // ...
////    } else if (c instanceof Long) {
////        // ...
////    } else if (c instanceof Double) {
////        // ...
////    } else if (c instanceof String) {
////        // ...
////    } else if (c instanceof Type) {
////        int sort = ((Type) c).getSort();
////        if (sort == Type.OBJECT) {
////            // ...
////        } else if (sort == Type.ARRAY) {
////            // ...
////        } else if (sort == Type.METHOD) {
////            // ...
////        } else {
////            // throw an exception
////        }
////    } else if (c instanceof Handle) {
////        // ...
////    } else {
////        // throw an exception
////    }
//    if (c == null)
//      mv.visitInsn(ACONST_NULL);
//    else if (c.equals(0))
//      mv.visitInsn(ICONST_0);
//    else
//      mv.visitLdcInsn(c);
//  }
//  
//  void writeArray(Class<?> elementType, Expression... exps)
//  {
//    // Create array.
//    mv.visitLdcInsn(exps.length);
////    mv.visitTypeInsn(ANEWARRAY, Descriptors.getShortDescriptor(elementType));
//    mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(elementType));
//    
//    // Fill array.
//    for (int i = 0; i < exps.length; i++)
//    {
//      mv.visitInsn(DUP);
////      mv.visitLdcInsn(i);
//      writeConstant(i);
//      writeExpression(exps[i]);
//      mv.visitInsn(AASTORE);
//    }
//  }
//  
////------------------------------------------------------------------------------
//}
