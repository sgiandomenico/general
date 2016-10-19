package general.compiler;

import java.lang.reflect.*;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.ast.*;
import general.ast.MethodReference;
import general.type.*;
import general.type.Type;

public class SymbolResolver
{
//** Fields ********************************************************************
  
  NameServer nameServer;
  
  int nextLocal = 0;
  
//** Constructors **************************************************************
  
  public SymbolResolver(NameServer baseNameServer)
  {
    this.nameServer = baseNameServer;
  }
  
//** Methods *******************************************************************
  
  Type resolveExpression(Expression exp, Type expectedType)
  {
    if (exp instanceof Block)
    {
      return resolveBlock((Block) exp, expectedType);
    }
    else if (exp instanceof Clause)
    {
      return resolveClause((Clause) exp, expectedType);
    }
    else if (exp instanceof Literal)
    {
      return resolveLiteral((Literal) exp, expectedType);
    }
    else if (exp instanceof SymbolicReference)
    {
      return resolveSymbolicReference((SymbolicReference) exp, expectedType);
    }
    else if (exp instanceof Assignment)
    {
      return resolveAssignment((Assignment) exp, expectedType);
    }
    else if (exp instanceof Declaration)
    {
      return resolveDeclaration((Declaration) exp, expectedType);
    }
    else if (exp instanceof MemberReference)
    {
      return resolveMember((MemberReference) exp, expectedType);
    }
    else if (exp instanceof New)
    {
      return resolveNew((New) exp, expectedType);
    }
    else
    {
      throw new RuntimeException("Unrecognized expression type: " + exp);
    }
  }
  
//------------------------------------------------------------------------------
  
  Type resolveBlock(Block block, Type expectedType)
  {
    SymbolTable blockSymTable = new SymbolTable(nameServer);
    SymbolResolver blockResolver = new SymbolResolver(blockSymTable);
    blockResolver.nextLocal = nextLocal;
    
    Type result = null; // FIXME: Use more sensible empty block type.
    for (int i = 0; i < block.body.length; i++)
      result = blockResolver.resolveExpression(block.body[i], i == (block.body.length - 1) ? expectedType : Type.ANY);
    
    // FIXME: Terrible hack!
    nextLocal = blockResolver.nextLocal;
    return result;
  }
  
  Type resolveClause(Clause sexp, Type expectedType)
  {
    Type[] argTypes = new Type[sexp.args.length];
    for (int i = 0; i < sexp.args.length; i++)
      argTypes[i] = resolveExpression(sexp.args[i], Type.ANY);
    
    // TODO: Add mechanism for determining possible types of expression?
    Type funcType = new FunctionType(expectedType, argTypes);
    FunctionType resultFuncType = (FunctionType) resolveExpression(sexp.verb, funcType);
    
    return resultFuncType.returnType;
  }
  
  Type resolveLiteral(Literal c, Type expectedType)
  {
    // TODO: Check that result type matches expected type.
    return Type.fromClass(c.value.getClass());
  }
  
  Type resolveSymbolicReference(SymbolicReference var, Type expectedType)
  {
    NameReference ref = nameServer.lookup(var.symbol.name);
    if (ref == null)
      throw new RuntimeException("Could not resolve " + var.symbol.name);
    
    var.reference = ref;
    
    return ref.getType();
  }
  
  Type resolveAssignment(Assignment assign, Type expectedType)
  {
    resolveExpression(assign.location, expectedType);
    return resolveExpression(assign.value, expectedType);
  }
  
  Type resolveDeclaration(Declaration decl, Type expectedType)
  {
    // TODO: Use Type/Class itself as expected type.
    resolveSymbolicReference(decl.typeExpr, Type.ANY);
    
    if (!(decl.typeExpr.reference instanceof ClassReference))
      throw new UnsupportedOperationException();
    
    ClassReference typeRef = (ClassReference) decl.typeExpr.reference;
    
    LocalReference localRef = new LocalReference(decl.symbol.name);
    localRef.type = typeRef.getType();
    localRef.index = nextLocal++;
    
    nameServer.add(localRef);
    
    // FIXME?
    return null;
  }
  
  Type resolveMember(MemberReference member, Type expectedType)
  {
//    if (!(member.target instanceof SymbolicReference))
//      throw new NotYetImplementedException();
//    
//    resolveExpression(member.target, Type.ANY);
//    SymbolicReference targetRef = (SymbolicReference) member.target;
//    boolean isStatic = (targetRef.reference instanceof ClassReference);
//    Type targetType = targetRef.reference.getType();
    
    Type subjectType = resolveExpression(member.subject, Type.ANY);
    boolean isStatic = (member.subject instanceof SymbolicReference)
        && (((SymbolicReference) member.subject).reference instanceof ClassReference);
    
    if (!(member.member instanceof SymbolicReference))
      throw new NotYetImplementedException();
    
    SymbolicReference memberRef = (SymbolicReference) member.member;
    String memberName = memberRef.symbol.name;
    
    try
    {
      Class<?> clazz = Class.forName(subjectType.getBinaryName());
      
      if (expectedType instanceof FunctionType)
      {
        FunctionType funcType = (FunctionType) expectedType;
        
        Method[] methods = clazz.getMethods();
        for (Method m : methods)
        {
          if (m.getName().equals(memberName) && methodMatchesType(m, funcType))
          {
            if (isStatic && !Modifier.isStatic(m.getModifiers()))
              throw new RuntimeException("Cannot make static reference to non-static method " + m);
            
            MethodReference methodRef = new MethodReference(m.getName());
            methodRef.definingClass = subjectType;
            methodRef.isStatic = Modifier.isStatic(m.getModifiers());
            methodRef.type = new FunctionType(m); // FIXME: Use funcType somehow?
            
            memberRef.reference = methodRef;
            return methodRef.type;
          }
        }
        
        throw new RuntimeException(
            "Could not resolve " + subjectType.getBinaryName() + "." + memberName + expectedType.getDescriptor());
      }
      else
      {
        Field f = clazz.getField(memberName);
        // TODO: Check that field type matches expected type.
        
        if (f == null)
        {
          throw new RuntimeException("Could not resolve " + subjectType.getBinaryName()
              + "." + memberName + ":" + expectedType.getDescriptor());
        }
        else if (isStatic && !Modifier.isStatic(f.getModifiers()))
        {
          throw new RuntimeException("Cannot make static reference to non-static field " + f);
        }
        
        FieldReference fieldRef = new FieldReference(f.getName());
        fieldRef.definingClass = subjectType;
        fieldRef.isStatic = Modifier.isStatic(f.getModifiers());
        fieldRef.type = Type.fromClass(f.getType()); // FIXME: Use expectedType somehow?
        
        memberRef.reference = fieldRef;
        return fieldRef.type;
      }
      
    }
    catch (ReflectiveOperationException e)
    {
      throw new RuntimeException(e);
    }
  }
  
  Type resolveNew(New newObj, Type expectedType)
  {
    throw new NotYetImplementedException();
  }
  
//------------------------------------------------------------------------------
  
  boolean methodMatchesType(Method m, FunctionType type)
  {
    if (m.isVarArgs())
      throw new NotYetImplementedException();
    
    if (type.returnType != Type.ANY && !m.getReturnType().equals(type.returnType.getBinaryName()))
      return false;
    
    Class<?>[] paramTypes = m.getParameterTypes();
    
    if (type.argumentTypes.length != paramTypes.length)
      return false;
    
    for (int i = 0; i < paramTypes.length; i++)
    {
      Type argType = type.argumentTypes[i];
      if (argType != Type.ANY && !paramTypes[i].getName().equals(argType.getBinaryName()))
        return false;
    }
    
    return true;
  }
  
//------------------------------------------------------------------------------
}
