package general.interpreter;

import java.util.*;
import java.util.regex.*;

import org.giandomenico.stephen.util.*;

import general.ast.*;
import general.engine.*;

public class Parser implements Function
{
//** Variables *****************************************************************

//  private static final Class<?>[] argumentTypes = new Class<?>[] { String[].class };
//  private static final Class<?>[] resultTypes = new Class<?>[] { ParseResult.class };

//------------------------------------------------------------------------------
  
  public static final Pattern WHITESPACE = Pattern.compile("\\s+");
  public static final Pattern WHITESPACE_OR_EMPTY = Pattern.compile("\\s*");
  
  public static final Pattern COMMENT_SINGLE_LINE = Pattern.compile("(?-s)//.*");
  public static final Pattern COMMENT_MULTI_LINE = Pattern.compile("(?s)/\\*.*?\\*/");
  
  public static final Expression[] EMPTY_STATEMENT_EXPRESSIONS = new Expression[0];
  
  public static final String DEFAULT_TERMINATOR = ";";
  
//------------------------------------------------------------------------------
  
  protected static class Context
  {
    public final Parser parser;
//    public final FlowRuntime runtime;
    
    public Context(Parser parser)
    {
      this.parser = parser;
//      this.runtime = runtime;
    }
  }
  
  public static class ParseResult
  {
    public final Expression[] statements;
    
    public ParseResult(Expression... statements)
    {
      this.statements = statements;
    }
  }
  
  protected static class Parenthetical
  {
    public final String initiator, terminator;
    
    public Parenthetical(String initiator, String terminator)
    {
      this.initiator = initiator;
      this.terminator = terminator;
    }
    
    public boolean isInitiator(String str)
    {
      return ObjectUtil.deepEquals(initiator, str);
    }
    
    public boolean isTerminator(String str)
    {
      return ObjectUtil.deepEquals(terminator, str);
    }
  }
  
//------------------------------------------------------------------------------
  
  public static final RegexTokenFactory<Literal, Context> SYMBOL_FACTORY =
      new RegexSingleTokenFactory<Literal, Context>(
          Pattern.compile(":([^\\s]+)")) {
        @Override
        public Literal parseToken(Context c, Matcher m)
        {
          return new Literal(Symbol.get(m.group(1)));
        }
      };
  
  // TODO: Add special character handling, like newlines.
  public static final Pattern ESCAPED_CHARS = Pattern.compile("\\\\(.)");
  public static final RegexTokenFactory<Literal, Context> STRING_FACTORY =
      new RegexSingleTokenFactory<Literal, Context>(
          Pattern.compile("(?s)'(([^'/]|/.)*)'".replace("/", "\\\\").replace('\'', '"'))) {
        
        @Override
        public Literal parseToken(Context c, Matcher m)
        {
          String rawString = m.group(1);
          String str = ESCAPED_CHARS.matcher(rawString).replaceAll("$1");
          
          return new Literal(str);
        }
      };
  
  public static final RegexTokenFactory<Literal, Context> NUMERIC_FACTORY =
      new RegexSingleTokenFactory<Literal, Context>(
          Pattern.compile("#([^\\s]+)")) {
        @Override
        public Literal parseToken(Context c, Matcher m)
        {
          String valueStr = m.group(1);
          
          if ("null".equals(valueStr))
            return new Literal(null);
          
          if ("true".equals(valueStr) || "false".equals(valueStr))
            return new Literal(Boolean.valueOf(valueStr));
          
          return new Literal(Double.valueOf(valueStr));
        }
      };
  
  public static final RegexTokenFactory<SymbolicReference, Context> REFERENCE_FACTORY =
      new RegexSingleTokenFactory<SymbolicReference, Context>(
          Pattern.compile("([^\\s]+)")) {
        @Override
        public SymbolicReference parseToken(Context c, Matcher m)
        {
          return new SymbolicReference(m.group(1));
        }
      };
  
//------------------------------------------------------------------------------
  
  public final RegexTokenizer<Expression, Context> tokenizer = new RegexTokenizer<>(Expression.class);
  
  public final Map<String, Parenthetical> parentheticals = new HashMap<>();
  
  public VariableScope localScope;
  
//** Constructors **************************************************************
  
  public Parser(VariableScope localScope)
  {
    tokenizer.ignorePatterns.add(WHITESPACE);
    
    tokenizer.tokenFactories.add(SYMBOL_FACTORY);
    tokenizer.tokenFactories.add(STRING_FACTORY);
    tokenizer.tokenFactories.add(NUMERIC_FACTORY);
    
    tokenizer.tokenFactories.add(REFERENCE_FACTORY);
    
//    registerParethentical(new Parenthetical(FlowRuntime.EXECUTE_COMMAND_NAME, DEFAULT_TERMINATOR));
    this.localScope = localScope;
  }
  
//** Methods *******************************************************************
  
  @Override
  public String toString()
  {
    return "BuiltinParser";
  }
  
//------------------------------------------------------------------------------
  
  public void registerParethentical(Parenthetical parenthetical)
  {
    // TODO: Add proper message.
    if (parentheticals.containsKey(parenthetical.initiator) || parentheticals.containsKey(parenthetical.terminator))
      throw new IllegalArgumentException();
    
    parentheticals.put(parenthetical.initiator, parenthetical);
    parentheticals.put(parenthetical.terminator, parenthetical);
  }
  
  public boolean isParethenticalToken(String token)
  {
    for (Parenthetical p : parentheticals.values())
    {
      if (ObjectUtil.deepEquals(p.initiator, token) || ObjectUtil.deepEquals(p.terminator, token))
        return true;
    }
    
    return false;
  }
  
//------------------------------------------------------------------------------
  
  @Override
  public Class<?> getType()
  {
    return ParseResult.class;
  }
  
  @Override
  public Object apply(Object... args)
  {
    String[] statement = (String[]) args[0];
    
    return parse(statement);
  }
  
//------------------------------------------------------------------------------
  
  public Expression[] tokenize(String statement)
  {
    return tokenizer.tokenize(new Context(this), statement);
  }
  
  public Expression[] tokenize(String[] tokenizedStatement)
  {
    List<Expression> tokens = new LinkedList<>();
    
    for (String rawToken : tokenizedStatement)
      tokens.addAll(Arrays.asList(tokenize(rawToken)));
    
    return tokens.toArray(EMPTY_STATEMENT_EXPRESSIONS);
  }
  
  protected static class SubClause
  {
    public final Parenthetical parenthetical;
    public final int start;
    
    public SubClause(Parenthetical parenthetical, int start)
    {
      this.parenthetical = parenthetical;
      this.start = start;
    }
  }
  
  public List<Expression> collapse(Expression[] tokens)
  {
    List<Expression> remainingTokens = new ArrayList<>(Arrays.asList(tokens));
    List<Expression> completedStatements = new LinkedList<>();
    
    Stack<SubClause> subClauses = new Stack<>();
//    SubClause completeStatementMarker = new SubClause(parentheticals.get(":"), -1);

//    subClauses.push(completeStatementMarker);
    
    for (int i = 0; i < remainingTokens.size(); i++)
    {
      // FIXME: Prevent name pollution.
      String str = remainingTokens.get(i).getName();
      
      Parenthetical testMarker = parentheticals.get(str);
      if (testMarker == null)
        continue;
      
      if (testMarker.isTerminator(str))
      {
        SubClause marker;
        
        // FIXME
        if (subClauses.isEmpty() || (marker = subClauses.pop()).parenthetical != testMarker)
          throw new IncompleteStatementException(null);
        
        List<Expression> subExpression = remainingTokens.subList(marker.start, i);
        Clause subClause = Clause.create(subExpression);
        subClause = new Clause(subClause.verb, Expression.quote(subClause.args));
        
//        Clause subClause = new Clause(subExpression.get(0), subExpression.subList(1, subExpression.size()));
        
        subExpression.clear();
        
//      ((ExpressionTree) remainingTokens.get(i)).argumentEvalutationType = parenthetical.priority;
//      ((ExpressionTree) subExpression.get(0)).argumentEvalutationType = parenthetical.priority;

//        if (marker.parenthetical.priority == ExecutionPriority.IMMEDIATE
//            || marker.parenthetical.priority == ExecutionPriority.DELAYED)
//        {
//          List<Expression> results = new LinkedList<>();
//          for (Object res : statement.evaluate(runtime))
//            results.add(new Literal(res));
//          
//          remainingTokens.remove(start);
//          remainingTokens.addAll(start, results);
        
        Expression results = (Expression) new Executor().evaluate(subClause, localScope);
        
        remainingTokens.set(marker.start, results);
//        }
//        else
//        {
//          remainingTokens.set(marker.start, subClause);
//        }
        
        if (subClauses.isEmpty())
        {
//          Clause statement = (Clause) remainingTokens.remove(marker.start).evaluate(null).values[0];
          Expression statement = remainingTokens.remove(marker.start);
          
          completedStatements.add(statement);
          i = -1;
          
//        subClauses.push(completeStatementMarker);
        }
        else
        {
          i = marker.start;
        }
      }
      else
      {
        subClauses.push(new SubClause(testMarker, i));
      }
    }
    
    if (!remainingTokens.isEmpty())
      throw new IncompleteStatementException(null);
    
    return completedStatements;
  }
  
  /*
  @Deprecated
  public Clause collapseOld(FlowRuntime runtime, Expression[] tokens)
  {
    List<Expression> remainingTokens = new ArrayList<>(Arrays.asList(tokens));
    
    reduction: while (true)
    {
      Parenthetical parenthetical = null;
      int start = 0;
      
      int i = 0;
      for (Expression token : remainingTokens)
      {
        // FIXME: Prevent name pollution.
        String str = token.getName();
        
        if (parenthetical != null && parenthetical.isTerminator(str))
        {
          List<Expression> subExpression = remainingTokens.subList(start, i);
          Clause statement = Clause.create(subExpression);
          subExpression.clear();
          
  //        ((ExpressionTree) remainingTokens.get(i)).argumentEvalutationType = parenthetical.priority;
  //        ((ExpressionTree) subExpression.get(0)).argumentEvalutationType = parenthetical.priority;
          
          if (parenthetical.priority == ExecutionPriority.IMMEDIATE
              || parenthetical.priority == ExecutionPriority.DELAYED)
          {
  //            List<Expression> results = new LinkedList<>();
  //            for (Object res : statement.evaluate(runtime))
  //              results.add(new Literal(res));
  //            
  //            remainingTokens.remove(start);
  //            remainingTokens.addAll(start, results);
            
            Expression results = statement.evaluate(runtime);
            
            remainingTokens.set(start, results);
          }
          else
          {
            remainingTokens.set(start, statement);
          }
          
          continue reduction;
        }
        
        Parenthetical testTerm = parentheticals.get(str);
        
        if (testTerm != null)
        {
          parenthetical = testTerm;
          start = i;
        }
        
        i++;
      }
      
      // FIXME
      if (remainingTokens.size() != 1) //|| !(remainingTokens.get(0) instanceof Literal))
        throw new IncompleteStatementException(null);
        
      Clause statement = (Clause) remainingTokens.get(0).evaluate(null).values[0];
      
      return statement;
      
  //      return asStatement(remainingTokens);
    }
  }
  
  @Deprecated
  public Expression collapse(Expression[] tokens)
  {
    List<Expression> remainingTokens = new ArrayList<>(Arrays.asList(tokens));
    
    reduction: while (true)
    {
      Parenthetical parenthetical = null;
      int start = 0;
      
      int i = 0;
      for (Expression token : remainingTokens)
      {
        String str = token.getName();
        
        if (parenthetical != null && parenthetical.isTerminator(str))
        {
          List<Expression> subExpression = remainingTokens.subList(start, i);
          remainingTokens.set(i, Clause.create(subExpression));
          
  //          ((ExpressionTree) remainingTokens.get(i)).argumentEvalutationType = parenthetical.priority;
  //          ((ExpressionTree) subExpression.get(0)).argumentEvalutationType = parenthetical.priority;
          
          subExpression.clear();
          
          continue reduction;
        }
        
        Parenthetical testTerm = parentheticals.get(str);
        
        if (testTerm != null)
        {
          parenthetical = testTerm;
          start = i;
        }
        
        i++;
      }
      
      // FIXME
      if (remainingTokens.size() > 1 || !(remainingTokens.get(0) instanceof Clause))
        throw new IncompleteStatementException(null);
        
      return remainingTokens.get(0);
      
  //      return asStatement(remainingTokens);
    }
  }
  
  @Deprecated
  public static Expression wrap(Object obj)
  {
    return obj instanceof Expression ? (Expression) obj : new Literal(obj);
  }
  */
  
//  // TODO: Generalize.
//  @Deprecated
//  public static Clause asStatement(List<Expression> expressions)
//  {
//    if (expressions.size() == 0)
//      return Clause.EMPTY_STATEMENT;
//    
//    return new Clause(expressions.get(0), expressions.subList(1, expressions.size()).toArray(
//        EMPTY_STATEMENT_EXPRESSIONS));
//  }

//------------------------------------------------------------------------------
  
  // TODO: Handle empty expressions properly.
  public ParseResult parse(String... rawStatement)
  {
    Expression[] tokens = tokenize(rawStatement);
    
    if (tokens.length == 0)
      return new ParseResult(); // Clause.EMPTY_STATEMENT
      
    try
    {
      List<Expression> statements = collapse(tokens);
      
      return new ParseResult(CollectionUtil.toArray(statements, Expression.class));
    }
    catch (IncompleteStatementException e)
    {
      throw ExceptionUtil.rethrowAs(e, new IncompleteStatementException(StringUtil.concatenate(" ", rawStatement)));
    }
  }
  
//------------------------------------------------------------------------------
}
