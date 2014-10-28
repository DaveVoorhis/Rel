package ca.mb.armchair.rel3.interpreter;

import java.io.*;

import ca.mb.armchair.rel3.languages.tutoriald.parser.*;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.generator.OperatorDefinitionRel;
import ca.mb.armchair.rel3.generator.OperatorDefinition;
import ca.mb.armchair.rel3.generator.OperatorSignature;
import ca.mb.armchair.rel3.values.ValueOperator;
import ca.mb.armchair.rel3.vm.VirtualMachine;
import ca.mb.armchair.rel3.vm.Dumper;
import ca.mb.armchair.rel3.debuginfo.DebugInfo;
import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.storage.*;
import ca.mb.armchair.rel3.types.Heading;
import ca.mb.armchair.rel3.types.Type;

public class Interpreter {

	private Generator generator;
	private VirtualMachine vm;

	private boolean debugOnRun = false;
	private boolean debugAST = false;
	
	public Interpreter(RelDatabase database, PrintStream outputStream) {
		generator = new Generator(database, outputStream);
		vm = new VirtualMachine(generator, database, outputStream);
	}

	public void setDebugOnRun(boolean flag) {
		debugOnRun = flag;
	}
	
	public void setDebugAST(boolean flag) {
		debugAST = flag;
	}
	
	public void reset() {
		generator.reset();
	}
	
	public Generator getGenerator() {
		return generator;
	}
	
	private void beginCompile() {
		if (debugOnRun)
			System.out.println("Compiling...");		
	}

	private void execute(OperatorDefinitionRel mainOperatorDefinition) {
		// Dump if debugging
		if (debugOnRun) {
			System.out.println("Compiled:");
			(new Dumper()).dumpMachineCode(mainOperatorDefinition.getOperator());
			System.out.println("Executing...");
		}
		// Go
		if (mainOperatorDefinition != null)
			vm.execute(mainOperatorDefinition.getOperator());
		else
			System.out.println("No execution performed.");
	}

	private void run(TutorialD parseEngine, OperatorDefinitionRel mainOperatorDefinition) {
		try {
			execute(mainOperatorDefinition);
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + vm.getCurrentInstruction().getDebugInfo().toString(), es);
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + vm.getCurrentInstruction().getDebugInfo().toString(), ef);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ExceptionFatal(t.toString() + "\n" + vm.getCurrentInstruction().getDebugInfo().toString(), t);
		}
	}
	
	// TODO - make error trapping more location-specific.  Current mechanism, using getCurrentNode(), is a hack.
	
	private static DebugInfo getDebugInfo(Generator generator, TutorialDParser parser) {
		return new DebugInfo(parser.getCurrentNode(), generator.getOperatorDefinitionLineReferenceStack(parser.getCurrentNode().first_token.beginLine));
	}
	
	/** Compile and evaluate an expression. */
	public static Evaluation evaluateExpression(RelDatabase database, String source, PrintStream outputStream) throws ParseException {
		return (new Interpreter(database, outputStream)).evaluate(source);
	}
	
	/** Compile and execute a statement. */
	public static void executeStatement(RelDatabase database, String source, PrintStream outputStream) throws ParseException {
		(new Interpreter(database, outputStream)).interpret(source);
	}
	
	public Evaluation evaluate(TutorialDVisitor parser, InputStream input) throws ParseException {
		beginCompile();
		TutorialD parseEngine = new TutorialD(input);
		OperatorDefinitionRel mainOperatorDefinition = null;
		try {
			mainOperatorDefinition = (OperatorDefinitionRel)(parseEngine.evaluate().jjtAccept(parser, null));
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString(), es);
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString(), ef);
		}
		if (mainOperatorDefinition != null) {
			run(parseEngine, mainOperatorDefinition);
			if (vm.getStackCount() != 1)
				throw new ExceptionFatal("Invalid count of values on the stack: " + vm.getStackCount());
			return new Evaluation(vm.getCurrentContext(), mainOperatorDefinition.getDeclaredReturnType(), vm.pop());
		} else {
			System.out.println("No evaluation performed.");
			return null;
		}
	}
	
	/** Compile a statement. */
	public void compileStatement(TutorialDVisitor parser, InputStream input) throws ParseException {
		beginCompile();
		TutorialD parseEngine = new TutorialD(input);
		try {
			parseEngine.statement().jjtAccept(parser, null);
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		}		
	}

	/** Compile a statement. */
	public void compileStatement(TutorialDVisitor parser, String input) throws ParseException {
		compileStatement(parser, new ByteArrayInputStream(input.getBytes()));
	}
	
	/** Compile a statement. */
	public void compileStatement(String input) throws ParseException {
		compileStatement(new TutorialDParser(generator), input);
	}
	
	/** Compile a top-level operator definition, but instead of persisting it, return it. */
	public OperatorDefinition compileOperator(String input) throws ParseException {
		TutorialDParser parser = new TutorialDParser(generator);
		parser.beginOperatorsNonStorable();
		compileStatement(parser, input);
		parser.endOperatorsNonStorable();
		return parser.getLastPersistentOperatorDefinition();
	}

	/** Compile an anonymous operator definition. */
	public ValueOperator compileAnonymousOperator(String source) throws ParseException {
		TutorialDParser parser = new TutorialDParser(generator);
		return (ValueOperator)evaluate(parser, source).getValue();
	}
	
	/** Obtain the return type of an operator */
	public Type getOperatorReturnType(String input) throws ParseException {
		TutorialDParser parser = new TutorialDParser(generator);
		parser.beginOperatorsNonStorable();
		beginCompile();
		parser.endOperatorsNonStorable();
		TutorialD parseEngine = new TutorialD(new ByteArrayInputStream(input.getBytes()));
		try {
			return (Type)parseEngine.getoperatorreturntype().jjtAccept(parser, null);
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		}		
	}
	
	/** Compile a heading and return it. */
	public Heading getHeading(String input) throws ParseException {
		TutorialDParser parser = new TutorialDParser(generator);
		beginCompile();
		TutorialD parseEngine = new TutorialD(new ByteArrayInputStream(input.getBytes()));
		try {
			return (Heading)parseEngine.getheading().jjtAccept(parser, null);
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		}		
	}
	
	/** Obtain an operator signature. */
	public OperatorSignature getOperatorSignature(String input) throws ParseException {
		TutorialDParser parser = new TutorialDParser(generator);
		beginCompile();
		TutorialD parseEngine = new TutorialD(new ByteArrayInputStream(input.getBytes()));
		try {
			return (OperatorSignature)parseEngine.getsignature().jjtAccept(parser, null);
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		}		
	}
	
	public void interpret(TutorialDVisitor parser, InputStream input) throws ParseException {
		beginCompile();
		TutorialD parseEngine = new TutorialD(input);
		OperatorDefinitionRel mainOperatorDefinition;
		try {
			mainOperatorDefinition = (OperatorDefinitionRel)(parseEngine.code().jjtAccept(parser, null));
		} catch (ExceptionSemantic es) {
			throw new ExceptionSemantic(es.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		} catch (ExceptionFatal ef) {
			throw new ExceptionFatal(ef.getMessage() + "\n" + getDebugInfo(generator, (TutorialDParser)parser).toString());
		}
		run(parseEngine, mainOperatorDefinition);
		vm.outputTupleUpdateNotices();
	}
	
	public void interpret(InputStream input) throws ParseException {
		TutorialDVisitor parser;
		if (debugAST)
			parser = new TutorialDDebugger();
		else
			parser = new TutorialDParser(generator);
		interpret(parser, input);
	}
	
	public void interpret(String input) throws ParseException {
		interpret(new ByteArrayInputStream(input.getBytes()));
	}

	public void interpret(TutorialDVisitor parser, String input) throws ParseException {
		interpret(parser, new ByteArrayInputStream(input.getBytes()));
	}
	
	public Evaluation evaluate(InputStream input) throws ParseException {
		TutorialDVisitor parser;
		if (debugAST)
			parser = new TutorialDDebugger();
		else
			parser = new TutorialDParser(generator);
		return evaluate(parser, input);
	}
	
	public Evaluation evaluate(String input) throws ParseException {
		return evaluate(new ByteArrayInputStream(input.getBytes()));
	}

	public Evaluation evaluate(TutorialDVisitor parser, String input) throws ParseException {
		return evaluate(parser, new ByteArrayInputStream(input.getBytes()));
	}
	
}
