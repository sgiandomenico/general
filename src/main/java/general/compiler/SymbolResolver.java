package general.compiler;

import org.giandomenico.stephen.util.NotYetImplementedException;

import general.ast.*;

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
  
  void resolveExpression(Expression exp)
  {
    if (exp instanceof Block)
    {
      resolveBlock((Block) exp);
    }
    else if (exp instanceof Clause)
    {
      resolveClause((Clause) exp);
    }
    else if (exp instanceof Literal)
    {
      resolveLiteral((Literal) exp);
    }
    else if (exp instanceof SymbolicReference)
    {
      resolveSymbolicReference((SymbolicReference) exp);
    }
    else if (exp instanceof Assignment)
    {
      resolveAssignment((Assignment) exp);
    }
    else if (exp instanceof Declaration)
    {
      resolveDeclaration((Declaration) exp);
    }
    else if (exp instanceof New)
    {
      resolveNew((New) exp);
    }
    else
    {
      throw new RuntimeException("Unrecognized expression type: " + exp);
    }
  }
  
  void resolveBlock(Block block)
  {
    SymbolTable blockSymTable = new SymbolTable(nameServer);
    SymbolResolver blockResolver = new SymbolResolver(blockSymTable);
    blockResolver.nextLocal = nextLocal;
    for (Expression exp : block.body)
      blockResolver.resolveExpression(exp);
    
    // FIXME: Terrible hack!
    nextLocal = blockResolver.nextLocal;
  }
  
  void resolveClause(Clause sexp)
  {
    resolveExpression(sexp.verb);
    
    for (Expression e : sexp.args)
      resolveExpression(e);
  }
  
  void resolveLiteral(Literal c)
  {
    ; // Do nothing.
  }
  
  void resolveSymbolicReference(SymbolicReference var)
  {
    NameReference ref = nameServer.lookup(var.symbol.name);
    
    var.reference = ref;
  }
  
  void resolveAssignment(Assignment assign)
  {
    resolveExpression(assign.location);
    resolveExpression(assign.value);
  }
  
  void resolveDeclaration(Declaration decl)
  {
    resolveSymbolicReference(decl.typeExpr);
    
    if (!(decl.typeExpr.reference instanceof ClassReference))
      throw new UnsupportedOperationException();
    
    ClassReference typeRef = (ClassReference) decl.typeExpr.reference;
    
    LocalReference localRef = new LocalReference(decl.symbol.name);
    localRef.type = typeRef.getType();
    localRef.index = nextLocal++;
    
    nameServer.add(localRef);
  }
  
  void resolveNew(New newObj)
  {
    throw new NotYetImplementedException();
  }
  
//------------------------------------------------------------------------------
}
