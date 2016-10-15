package general.compiler;

import static org.objectweb.asm.Opcodes.*;

import java.util.*;

import org.giandomenico.stephen.util.NotYetImplementedException;
import org.objectweb.asm.*;
import org.objectweb.asm.Type;

import general.ast.*;
import general.engine.*;

public class MethodWriter2
{
//** Fields ********************************************************************

//  @SuppressWarnings("hiding")
//  public static enum ArrayOps
//  {
//    BOOLEAN(T_BOOLEAN, BASTORE, BALOAD),
//    BYTE(T_BYTE, BASTORE, BALOAD),
//    CHAR(T_CHAR, CASTORE, CALOAD),
//    SHORT(T_SHORT, SASTORE, SALOAD),
//    INT(T_INT, IASTORE, IALOAD),
//    LONG(T_LONG, LASTORE, LALOAD),
//    FLOAT(T_FLOAT, FASTORE, FALOAD),
//    DOUBLE(T_DOUBLE, DASTORE, DALOAD),
//    OBJECT(-1, AASTORE, AALOAD);
//    
//    public final int typeId;
//    public final int storeOp, loadOp;
//    
//    private ArrayOps(int typeId, int storeOp, int loadOp)
//    {
//      this.typeId = typeId;
//      this.storeOp = storeOp;
//      this.loadOp = loadOp;
//    }
//    
//    public static ArrayOps forType(Class<?> elementType)
//    {
//      if (Boolean.TYPE.equals(elementType))
//        return BOOLEAN;
//      else
//        return OBJECT;
//    }
//  }

//------------------------------------------------------------------------------

//  protected final static Type FUNCTION_TYPE = Type.getType(Function.class);
//  protected final static MethodReference FUNCTION_APPLY = MethodReference.get(Function.class, "apply", Object[].class);

//  protected final static Map<Class<?>, Integer> p

//------------------------------------------------------------------------------
  
  protected final MethodVisitor mv;
//  protected final String className;
//  protected final List<Symbol> parameters;

//** Constructors **************************************************************
  
  public MethodWriter2(MethodVisitor mv)
  {
//    super(ASM5, mv);
    
    this.mv = mv;
//    this.className = className;
//    this.parameters = parameters;
  }
  
//** Methods *******************************************************************
  
  public void writeAsMethod(Expression exp)
  {
    mv.visitCode();
    writeBody(exp);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();
  }
  
  void writeBody(Expression exp)
  {
    writeExpression(exp);
  }
  
//------------------------------------------------------------------------------
  
  void writeExpression(Expression exp)
  {
    if (exp instanceof Block)
    {
      writeBlock((Block) exp);
    }
    else if (exp instanceof Clause)
    {
      writeClause((Clause) exp);
    }
    else if (exp instanceof Literal)
    {
      writeLiteral((Literal) exp);
    }
    else if (exp instanceof SymbolicReference)
    {
      writeSymbolicReference((SymbolicReference) exp);
    }
//    else if (exp instanceof Parameter)
//    {
//      writeParameter((Parameter) exp);
//    }
    else if (exp instanceof Assignment)
    {
      writeAssignment((Assignment) exp);
    }
    else if (exp instanceof Declaration)
    {
      writeDeclaration((Declaration) exp);
    }
    else if (exp instanceof New)
    {
      writeNew((New) exp);
    }
    else
    {
      throw new RuntimeException("Unrecognized expression type: " + exp);
    }
  }
  
  void writeBlock(Block block)
  {
    // TODO: Optimize (a little).
    writeExpression(block.body[0]);
    for (Expression exp : Arrays.copyOfRange(block.body, 1, block.body.length, Expression[].class))
    {
      mv.visitInsn(POP);
      writeExpression(exp);
    }
  }
  
  void writeClause(Clause sexp)
  {
//    writeExpression(sexp.verb);
//    writeTypeInsn(CHECKCAST, FUNCTION_TYPE); // Necessary?
//    
//    writeArray(Object.class, sexp.args);
//    
//    FUNCTION_APPLY.visitInsn(mv);
    
    if (!(sexp.verb instanceof SymbolicReference))
      throw new NotYetImplementedException();
    
    SymbolicReference symbol = (SymbolicReference) sexp.verb;
    NameReference ref = symbol.reference;
    if (!(ref instanceof general.ast.MethodReference))
      throw new NotYetImplementedException();
    
    general.ast.MethodReference methodRef = (general.ast.MethodReference) ref;
    
    for (Expression e : sexp.args)
      writeExpression(e);
    
    mv.visitMethodInsn(methodRef.isStatic ? INVOKESTATIC : INVOKEVIRTUAL,
        methodRef.definingClass.getClassFileName(),
        methodRef.getID(),
        methodRef.type.getDescriptor(), false);
  }
  
  void writeLiteral(Literal c)
  {
    writeConstant(c.value, true);
  }
  
  void writeSymbolicReference(SymbolicReference var)
  {
    NameReference ref = var.reference;
    
    if (ref instanceof LocalReference)
    {
      LocalReference localRef = (LocalReference) ref;
      mv.visitVarInsn(ALOAD, localRef.getIndex());
    }
    else if (ref instanceof FieldReference)
    {
      FieldReference fieldRef = (FieldReference) ref;
      // FIXME
      mv.visitFieldInsn(fieldRef.isStatic ? GETSTATIC : GETFIELD, fieldRef.getDefiningClass().getClassFileName(),
          fieldRef.getID(), fieldRef.getType().getDescriptor());
    }
    else if (ref instanceof ClassReference)
    {
      // TODO: Verify.
      ClassReference classRef = (ClassReference) ref;
      mv.visitLdcInsn(Type.getType(classRef.getType().getDescriptor()));
    }
    else
    {
      throw new NotYetImplementedException();
    }
    
//    int parameterIndex = parameters.indexOf(var.symbol);
//    if (parameterIndex < 0)
//      writeLocalVariable(var);
//    else
//      writeParameter(parameterIndex + 1);
  }
  
//  void writeLocalVariable(SymbolicReference var)
//  {
//    // FIXME
//  }
//  
//  void writeParameter(int index)
//  {
//    // FIXME
//  }
  
  void writeAssignment(Assignment assign)
  {
    writeExpression(assign.value);
    
    NameReference ref = assign.symRef.reference;
    
    if (ref instanceof LocalReference)
    {
      LocalReference localRef = (LocalReference) ref;
      mv.visitVarInsn(ASTORE, localRef.getIndex());
    }
    else if (ref instanceof FieldReference)
    {
      FieldReference fieldRef = (FieldReference) ref;
      // FIXME
      mv.visitFieldInsn(fieldRef.isStatic ? PUTSTATIC : PUTFIELD, fieldRef.getDefiningClass().getClassFileName(),
          fieldRef.getID(), fieldRef.getType().getDescriptor());
    }
    else
    {
      throw new NotYetImplementedException();
    }
    
    // FIXME: To maintain expression property.
    writeSymbolicReference(assign.symRef);
  }
  
  void writeDeclaration(Declaration decl)
  {
    throw new UnsupportedOperationException();
  }
  
  void writeNew(New newObj)
  {
    throw new NotYetImplementedException();
    
//    String typeDesc = Type.getInternalName(newObj.type);
////    Constructor c = 
//    
//    writeTypeInsn(NEW, newObj.type);
//    for (Expression arg : newObj.constructorArgs)
//      writeExpression(arg);
//    // FIXME: Correct args.
//    mv.visitMethodInsn(INVOKESPECIAL, typeDesc, "<init>", "()V", false);
  }
  
//------------------------------------------------------------------------------
  
  @Deprecated
  void writeTypeInsn(int opcode, Type type)
  {
    mv.visitTypeInsn(opcode, type.getInternalName());
  }
  
  @Deprecated
  void writeTypeInsn(int opcode, Class<?> clazz)
  {
    writeTypeInsn(opcode, Type.getType(clazz));
  }
  
//------------------------------------------------------------------------------
  
  void writeConstant(Object c)
  {
    writeConstant(c, false);
  }
  
  void writeConstant(Object c, boolean box)
  {
//      if (c instanceof Integer) {
//        // ...
//    } else if (c instanceof Float) {
//        // ...
//    } else if (c instanceof Long) {
//        // ...
//    } else if (c instanceof Double) {
//        // ...
//    } else if (c instanceof String) {
//        // ...
//    } else if (c instanceof Type) {
//        int sort = ((Type) c).getSort();
//        if (sort == Type.OBJECT) {
//            // ...
//        } else if (sort == Type.ARRAY) {
//            // ...
//        } else if (sort == Type.METHOD) {
//            // ...
//        } else {
//            // throw an exception
//        }
//    } else if (c instanceof Handle) {
//        // ...
//    } else {
//        // throw an exception
//    }
    if (c == null)
    {
      mv.visitInsn(ACONST_NULL);
    }
    else if (c instanceof Boolean)
    {
      mv.visitInsn(ICONST_0 + ((boolean) c ? 1 : 0));
      
      if (box)
        writeMethodCall(Boolean.class, "valueOf", Boolean.TYPE);
    }
    else if (c instanceof Integer)
    {
      int i = (int) c;
      if (i >= -1 && i <= 5)
        mv.visitInsn(ICONST_0 + i);
      else
        mv.visitLdcInsn(c);
      
      if (box)
        writeMethodCall(Integer.class, "valueOf", Integer.TYPE);
//        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
    }
    else if (c instanceof Double)
    {
      double d = (double) c;
      if (d == 0)
        mv.visitInsn(DCONST_0);
      else if (d == 1)
        mv.visitInsn(DCONST_1);
      else
        mv.visitLdcInsn(c);
      
      if (box)
        writeMethodCall(Double.class, "valueOf", Double.TYPE);
//        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
    }
    else if (c instanceof Symbol)
    {
      Symbol s = (Symbol) c;
      mv.visitLdcInsn(s.name);
      writeMethodCall(Symbol.class, "get", String.class);
    }
    else if (c instanceof Class)
    {
      mv.visitLdcInsn(Type.getType((Class<?>) c));
    }
    else
    {
      mv.visitLdcInsn(c);
    }
  }
  
  @Deprecated
  void writeArrayNew(Class<?> elementType)
  {
    if (!elementType.isPrimitive())
    {
      writeTypeInsn(ANEWARRAY, elementType);
    }
    else
    {
      throw new NotYetImplementedException();
//      int primitiveType;
//      if (Boolean.TYPE.equals(elementType))
//        primitiveType = T_BOOLEAN;
//      else if ()

//      mv.visitIntInsn(NEWARRAY, primitiveType);
    }
  }
  
  @Deprecated
  void writeArray(Class<?> elementType, Expression... exps)
  {
    // Create array.
    writeConstant(exps.length);
//    writeTypeInsn(ANEWARRAY, elementType);
    writeArrayNew(elementType);
    
    // Fill array.
    for (int i = 0; i < exps.length; i++)
    {
      mv.visitInsn(DUP);
      writeConstant(i);
      writeExpression(exps[i]);
      mv.visitInsn(AASTORE);
    }
  }
  
//------------------------------------------------------------------------------
  
  void writeMethodCall(MethodReference method)
  {
    method.visitInsn(mv);
  }
  
  void writeMethodCall(Class<?> clazz, String methodName, Class<?>... parameters)
  {
    writeMethodCall(MethodReference.get(clazz, methodName, parameters));
  }
  
//  void writeMethodCall(MethodReference method, Expression... args)
//  {
//    
//  }

//------------------------------------------------------------------------------
}
