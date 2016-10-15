package general.interpreter;

import java.io.*;

import org.giandomenico.stephen.util.*;

import general.ast.Block;
import general.compiler.ClassFileWriter;
import general.core.*;
import general.engine.*;

public class Interpreter
{
//** Variables *****************************************************************
  
  public static final String PARSER = " ";
  public static final String EXECUTOR = "";
  public static final String CORE_FILE = "core.gnl";
  
  public static final String CLASSFILE_OUTPUT_DIR = "newbin";
  
  @SuppressWarnings("serial")
  public static class FatalException extends RuntimeException
  {
    public FatalException(String message)
    {
      super(message);
    }
    
    public FatalException(Throwable cause)
    {
      super(cause);
    }
  }
  
  /*
  @Deprecated
  public static class BlockVerb extends AbstractVerb
  {
    public final Block block;
    
    public BlockVerb(Block block)
    {
      super(EMPTY_TYPES, EMPTY_TYPES);
      
      this.block = block;
    }
    
    public BlockVerb(VariableScope scope, Clause... statements)
    {
      this(new Block(scope, statements));
    }
    
    @Override
    public boolean areResultsValid(Object[] results)
    {
      return true;
    }
    
    @Override
    public ValuePackage eval(FlowRuntime runtime, ValuePackage args)
    {
      return block.evaluate(runtime);
    }
  }
  */
  
//------------------------------------------------------------------------------
  
  public final boolean debugMode;
  
//  public FlowRuntime runtime;
  public VariableScope global;
  
//** Constructors **************************************************************
  
  public Interpreter(boolean useDebugMode)
  {
    this.debugMode = useDebugMode;
  }
  
//** Methods *******************************************************************
  
  public static void main(String[] args)
  {
//    try
//    {
    boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean()
        .getInputArguments()
        .toString()
        .contains("-agentlib:jdwp");
    
    new Interpreter(isDebug).runInteractive();
//    }
//    catch (Exception e)
//    {
//      e.printStackTrace();
//      System.exit(1);
//    }
  }
  
//------------------------------------------------------------------------------
  
  public void addVerb(String name, Function verb)
  {
    global.declare(name);
    global.assign(name, verb);
  }
  
  /*
  @Deprecated
  public Clause makeBlock(String... statements)
  {
    String block = "{ : " + StringUtil.concatenate(" ; : ", statements) + "; }";
    
  //    return (Clause) runtime.getParser().getValue().evaluate(runtime, new ValuePackage((Object) new String[] { block }));
    
    return runtime.makeStatement(block);
  }
  */
  
  public Parser getParser()
  {
    return (Parser) global.resolve(PARSER);
  }
  
  public Executor getExecutor()
  {
    return (Executor) global.resolve(EXECUTOR);
  }
  
  public Parser.ParseResult parse(String str)
  {
    return getParser().parse(str);
  }
  
  public Object execute(String str)
  {
    Parser.ParseResult parseResult = parse(str);
    Block b = new Block(parseResult.statements);
    return getExecutor().evaluate(b, global);
  }
  
//------------------------------------------------------------------------------
  
  protected void initializeRuntime()
  {
    System.out.println("Initializing General runtime...");
    
//  runtime = new FlowRuntime(parser, new Executor());
    global = new VariableScope(null);
    
    Parser parser = new Parser(global);
    parser.registerParethentical(new Parser.Parenthetical(":", ";"));
    parser.registerParethentical(new Parser.Parenthetical("(", ")"));
    parser.registerParethentical(new Parser.Parenthetical("[", "]"));
    parser.registerParethentical(new Parser.Parenthetical("{", "}"));
    parser.registerParethentical(new Parser.Parenthetical("{{", "}}"));
    parser.tokenizer.ignorePatterns.add(Parser.COMMENT_SINGLE_LINE);
    parser.tokenizer.ignorePatterns.add(Parser.COMMENT_MULTI_LINE);
    
    addVerb(PARSER, parser);
    addVerb(EXECUTOR, new Executor());
    
    addVerb(":", new StatementCreator());
//    addVerb(":", new SubExecutor());
    addVerb("(", new SubCreator());
    addVerb("[", new BinaryCreator());
    addVerb("{", new BlockCreator());
    addVerb("{{", new BlockExpressionCreator());
    
//    addVerb("exec", new BlockExecutor());
//    addVerb("exec", new Executor());
    addVerb("exec", Builtin.BuiltinExecute);
    
//    addVerb("here", FlowRuntime.BuiltinScopeAccessor);
    addVerb(".", new MemberAccessor());
//    addVerb("'", new MemberReferencer());

//    addVerb("types", new TypeSpecifier());
//    addVerb("function", new VerbCreator());
    addVerb("func", new FunctionCreator());
    addVerb("struct", new StructCreator());
    
    addVerb("load", Builtin.BuiltinClassLoader);
    addVerb("print", Builtin.BuiltinPrint);
//    addVerb("assign", Builtin.BuiltinAssign);
//    addVerb("declare", Builtin.BuiltinDeclare);
//    addVerb("member", new MemberDeclarator());
//    addVerb("begin", FlowRuntime.BuiltinBeginScope);
//    addVerb("end", FlowRuntime.BuiltinEndScope);
    
    addVerb("if", new ConditionalFlow());
    addVerb("while", new Loop());
    
    addVerb(ObjectCreator.NEW, new ObjectCreator());
    
    addVerb("read", new SourceReader(parser, Builtin.BuiltinExecute, global));
    addVerb("writeClass", new ClassFileWriter(CLASSFILE_OUTPUT_DIR));
    
    execute(": read \"" + CORE_FILE + "\" ;");
    
    addVerb("parse", parser);
    addVerb("execute", Builtin.BuiltinExecute);
    addVerb("quit", Builtin.BuiltinExit);
    addVerb("exit", Builtin.BuiltinExit);
    // TODO: Note: inspect needs to access the current dynamic scope, not lexical scope.
    // E.g. in { int x = 1; inspect; }, inspect needs access to the variable x.
    addVerb("inspect", Builtin.BuiltinInspect);
    
    addVerb("+", BinaryArithmeticVerb.Add);
    addVerb("-", BinaryArithmeticVerb.Subtract);
    addVerb("*", BinaryArithmeticVerb.Multiply);
    addVerb("/", BinaryArithmeticVerb.Divide);
    addVerb("%", BinaryArithmeticVerb.Remainder);
    
    // FIXME: Allow equality comparison on all objects.
    addVerb("==", new ComparisonVerb(Comparison.EQUAL));
    addVerb("<", new ComparisonVerb(Comparison.LESS_THAN));
    addVerb("<=", new ComparisonVerb(Comparison.LESS_THAN_OR_EQUAL));
    addVerb("!=", new ComparisonVerb(Comparison.NOT_EQUAL));
    addVerb(">=", new ComparisonVerb(Comparison.GREATER_THAN_OR_EQUAL));
    addVerb(">", new ComparisonVerb(Comparison.GREATER_THAN));
    
//    addVerb(",", ValuePackage.Packager);
//    addVerb("|", new FunctionPipe());
  }
  
  public void runInteractive()
  {
    initializeRuntime();
    
//    addVerb("init", new BlockVerb(runtime.getCurrentScope(), runtime.makeStatements(new String[] {
//        //        ": begin ;",
////        ": load \"java.lang.Boolean\" :boolean ;",
////        ": load \"java.lang.Byte\" :byte ;",
////        ": load \"java.lang.Integer\" :int ;",
////        ": load \"java.lang.Long\" :long ;",
////        ": load \"java.lang.Float\" :float ;",
////        ": load \"java.lang.Double\" :double ;",
////        ": load \"java.lang.String\" :String ;",
////        ": load \"org.giandomenico.stephen.flow.engine.Verb\" :verb ;",
//        ": begin ;",
//        ": declare :x int ;",
//        ": assign :x #5 ;",
//        ": declare :y String ;",
//        ": assign :y \"abc\" ;",
//        ": print \"Done.\" ;",
//    })));

//    addVerb("test", new BlockVerb(runtime.getCurrentScope(), runtime.makeStatements(new String[] {
//        ": init ;",
//        //": declare :z int",
//        // ": assign :z [ [ #10 + #2 ] * [ #10 - #2 ] + ( * #4 #6 ) ]",
//        ": declare :z verb ;",
//        //": assign :z { : print x ; : print y ; } ;",
//        //": if #true { : print x ; } { : print y ; } ;",
//        // ": assign :x #10",
//        // ": assign :z ( function ( types double :a :b ) ( types double :result ) { : assign :result [ a + b ] } )",
//        // ": z #1 #2",
//        // ": assign :z ( function ( types double :a ) ( types verb :result ) { : assign :result ( function ( types double :b ) ( types double :result2 ) { : assign :result2 [ a + b ] } ) } )",
//        // ": assign :z ( function ( types double :a ) ( types verb :result ) {\n" +
//        //     "    : assign :result ( function ( types double :b ) ( types double :result2 ) {\n" +
//        //     "    : assign :result2 [ a + b ] ;\n" +
//        //     "} ) ; } ) ;",
//        // ": ( z #1 ) #2 ;",
//        ": assign :z ( function ( types boolean :b ) ( types ) { : if b { : print x ; } { : print y ; } ; } ) ;",
//        ": z #true ;",
//        ": z #false ;",
//    })));

//    addVerb("testfile",
//        new BlockVerb(runtime.getCurrentScope(), runtime.makeStatements(": read \"HelloWorld.flo\" ;")));
//    addVerb("testoo", new BlockVerb(runtime.getCurrentScope(), runtime.makeStatements(": read \"oo.flo\" ;")));
    
    System.out.println("Welcome to the General interpreter.");
    
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while (true)
    {
      try
      {
        System.out.print("> ");
        
        String line;
        try
        {
          line = in.readLine();
        }
        catch (IOException e)
        {
          throw new FatalException(e);
        }
        
        Object result = execute(line);
        
        System.err.println(" => " + ObjectUtil.deepToString(result));
//        if (result > 0)
//          System.err.println(" => " + StringUtil.concatenate(", ", result.values));
      }
      catch (Exception e)
      {
//        if (debugMode)
//          throw e;
        
        e.printStackTrace(System.err);
        
        if (e instanceof FatalException)
          System.exit(1);
      }
      
      System.err.flush();
      try
      {
        Thread.sleep(10);
      }
      catch (InterruptedException e1)
      {
        ;
      }
    }
  }
  
//------------------------------------------------------------------------------
}
