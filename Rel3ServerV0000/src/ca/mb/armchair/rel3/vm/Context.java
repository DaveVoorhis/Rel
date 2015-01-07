package ca.mb.armchair.rel3.vm;

import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.values.*;
import ca.mb.armchair.rel3.vm.instructions.core.OpNop;
import ca.mb.armchair.rel3.debuginfo.DebugInfo;
import ca.mb.armchair.rel3.generator.Generator;

/** Run-time context for operator execution.  Allows access to relevant parameters and variables. */
public class Context {
    
    // Operand stack size
    private final static int operandstacksize = 128;
		
    private Instruction[] code;    	    		// current set of instructions
    private int instructionPointer;             // instruction pointer

    private Context[] contextDisplay;			// this context's view of static scope
    
    private Cell[] variables;					// variables
    private Value[] arguments;					// arguments
    
    private Value[] operandStack;    			// operand stack
    private int stackPointer;                   // operand stack pointer
    
    private Context caller;						// context that spawned this one
    
    private VirtualMachine vm;					// VM in which this context lives
    
    private Generator generator;				// Generator that owns this context
    
    private int depth;							// static scope depth    
    
    /** Create a non-executable root (depth=0) context. */
    public Context(Generator generator, VirtualMachine vm) {
    	this.generator = generator;
    	this.vm = vm;
    	caller = null;
    	depth = 0;
    	// Set up scope display
    	contextDisplay = new Context[depth + 1];
        contextDisplay[depth] = this;
        // Allocate an operand stack.
        operandStack = new Value[operandstacksize];
        // Point to the executable code.
    	code = null;
    	// Initialise the instruction and stack pointers.
        instructionPointer = 0;
        stackPointer = 0;    	
    }
    
    /** Create a context for an operator invocation, where the variables and parms referenced are in a specified context */
    private Context(Context caller, Operator operator, Context varparms) {
    	this.generator = caller.generator;
    	this.vm = caller.vm;
    	this.caller = caller;
    	depth = operator.getDepth();
    	// Set up scope display
    	contextDisplay = new Context[Math.max(depth + 1, varparms.contextDisplay.length)];
		System.arraycopy(varparms.contextDisplay, 0, contextDisplay, 0, varparms.contextDisplay.length);
		// Add this context to the scope display
        contextDisplay[depth] = this;
        // Allocate space for variables.
        if (operator.getVariableCount() > 0)
        	variables = new Cell[operator.getVariableCount()];
        // Adjust the caller context's stack pointer to remove the arguments from its stack
        // and move them to this context.  This ensures continuations (such as TupleIteratorS) will 
        // work, because we no longer need to refer to the caller's operand stack.
        int parmCount = operator.getParameterCount();
        if (parmCount > 0) {
        	arguments = new Value[parmCount];
        	caller.stackPointer -= parmCount;
        	System.arraycopy(caller.operandStack, caller.stackPointer, arguments, 0, parmCount);
        }
        // Allocate an operand stack.
        operandStack = new Value[operandstacksize];
        // Point to the executable code.
    	code = operator.getExecutableCode();
    	// Initialise the instruction and stack pointers.
        instructionPointer = 0;
        stackPointer = 0;
    }
    
    /** Create a context for an operator invocation. */
    Context(Context caller, Operator operator) {
    	this(caller, operator, caller);
    }
   
    private void dumpstack() {
    	if (variables != null) {
    		System.out.println("Variables:");
    		for (int i=0; i<variables.length; i++) {
    			System.out.print("V[" + i + "] = ");
    			if (variables[i] == null)
    				System.out.println("uninitialised");
    			else
    				System.out.println(variables[i]);
    		}
    	}
    	System.out.println("Stack:");
    	for (int i=0; i<stackPointer; i++) {
			System.out.print("S[" + i + "] = ");
    		if (operandStack[i] == null)
    			System.out.println("uninitialised");
    		else
    			System.out.println(operandStack[i]);
    	}
    }

    /** Dump this context. */
    public void dump(String prompt) {
    	System.out.println("----------" + prompt + "----------");
		System.out.println("Arguments:");
    	for (int i=0; i <= depth; i++) {
    		int offset = 0;
	    	if (contextDisplay != null && contextDisplay[i] != null && contextDisplay[i].arguments != null) {
	    		for (Value v: contextDisplay[i].arguments)
	    			System.out.println("[" + i + " " + (offset++) + "] " + v);
	    	} else
	    		System.out.println("[" + i + " 0] none");
    	}
    	dumpstack();
    	System.out.println("Context ID: " + this);
    	System.out.print("Depth: " + depth);
    	(new Dumper()).dumpMachineCode(code);
    	System.out.println();
    	System.out.println("--------------------");
    }
    
    /** Get the currently-executing instruction. */
    public final Instruction getCurrentInstruction() {
    	if (code == null) {
    		Instruction i = new OpNop();
    		i.setDebugInfo(new DebugInfo("unknown location"));
    		return i;
    	}
    	return code[instructionPointer - 1];
    }
    
    /** Get the virtual machine upon which this Context is running. */
    public final VirtualMachine getVirtualMachine() {
    	return vm;
    }
    
    /** Get the Generator that owns this Context */
    public final Generator getGenerator() {
    	return generator;
    }
    
    private final void execute() {
    	vm.setCurrentContext(this);
		while (instructionPointer < code.length)
			code[instructionPointer++].execute(this);
		vm.setCurrentContext(caller);
    }
    
    // Invoke user-defined operator in its own context, i.e., call it.
    public final void call(Operator operator) {
		(new Context(this, operator)).execute();
    }

    // Invoke user-defined operator in its own context, using a specified parent context for variable/parm scope.
	public final void call(Operator operator, Context varparmContext) {
		(new Context(this, operator, varparmContext)).execute();
	}
     
    public final void doReturn() {
    	instructionPointer = code.length;
    }
    
    public final void doReturnValue() {
		caller.push(pop());
    	doReturn();
    }
    
    /** Go to a given instruction. */
    public final void jump(int newIP) {
        instructionPointer = newIP;
    }

    /** Return Value on top of the stack */
    public final Value peek() {
    	return operandStack[stackPointer - 1];
    }
    
    /** Return n values on top of the stack */
    public Value[] peek(int n) {
    	Value[] values = new Value[n];
    	System.arraycopy(operandStack, stackPointer - n, values, 0, n);
    	return values;
    }
    
    final Context getCaller() {
    	return caller;
    }
    
    final int getStackCount() {
    	return stackPointer;
    }
    
    /** Push a Value onto the operand stack. */
    public final void push(Value v) {
        operandStack[stackPointer++] = v;
    }

    /** Pop a Value from the operand stack. */
    public final Value pop() {
        return operandStack[--stackPointer];
    }
    
    // User-defined native function
    // POP(n) - Value
    // PUSH - Value
    public final void userFunction(NativeFunction function, int parmCount) {
    	Value arguments[] = new Value[parmCount];
    	for (int i=0; i<parmCount; i++)
    		arguments[parmCount - i - 1] = pop();
    	push(function.evaluate(arguments));
    }
    
    // User-defined native procedure
    // POP(n) - Value
    public final void userProcedure(NativeProcedure procedure, int parmCount) {
    	Value arguments[] = new Value[parmCount];
    	for (int i=0; i<parmCount; i++)
    		arguments[parmCount - i - 1] = pop();
    	procedure.execute(arguments);    	
    }
    
    /** Operator to set a parameter's argument.
     * 
     * RT:
     *   POP - value 
     */
    public final void parmSet(int depth, int offset) {
        contextDisplay[depth].arguments[offset] = pop();        	
    }
    
    /** Operator to get a parameter's argument.
     * RT:
     *  PUSH - value
     */
    public final void parmGet(int depth, int offset) {
        push(contextDisplay[depth].arguments[offset]);
    }
    
    /** Operator to set value of a local variable.
     * RT:
     *   POP - value 
     */
    public final void varSet(int depth, int offset) {
        contextDisplay[depth].variables[offset].setValue(generator, pop());
    }
    
    /** Operator to get value of a local variable.
     * RT:
     *  PUSH - value
     */
    public final void varGet(int depth, int offset) {
        push(contextDisplay[depth].variables[offset].getValue(generator));
    }
    
    /** Operator to define a Cell.
     * RT:
     * 
     */
    public final void varSetCell(int depth, int offset, Cell cell) {
    	contextDisplay[depth].variables[offset] = cell;
    }
    
    /** Operator to obtain the Cell at a given slot. 
     * RT:
     *  
     */
    public final Cell varGetCell(int depth, int offset) {
    	return contextDisplay[depth].variables[offset]; 
    }
    
    /** Conditional Jump operator. 
     * 
     * POP - ValueBoolean 
     * 
     */
    public final void branchIfTrue(int jumpTo) {
        if (pop().booleanValue())
            jump(jumpTo);        	
    }
    
    /** Conditional Jump operator. 
     * 
     * POP - ValueBoolean 
     * 
     */
    public final void branchIfFalse(int jumpTo) {
        if (!pop().booleanValue())
            jump(jumpTo);        	
    }
   
    // Push tuple literal
    // POP(n) - Value
    // PUSH - Value (ValueTuple)
	public final void pushTupleLiteral(int attributeCount) {
		stackPointer -= attributeCount;
		Value[] rawTuple = new Value[attributeCount];
		System.arraycopy(operandStack, stackPointer, rawTuple, 0, attributeCount);
		push(new ValueTuple(generator, rawTuple));        	
    }

    // Extract an attribute of a tuple to the stack.
    // POP - Value (ValueTuple)
    // PUSH - Value
    public final void tupleGetAttribute(int index) {
		Value tuple[] = ((ValueTuple)pop()).getValues();
		push(tuple[index]);
    }

    // Set an attribute of a tuple from the topmost value on the stack.
    // POP - Value (ValueTuple)
    // POP - Value
    // PUSH - Value (ValueTuple)
    public final void tupleSetAttribute(int index) {
		Value tuple[] = ((ValueTuple)pop()).getValues();
    	tuple[index] = pop();
    	push(new ValueTuple(generator, tuple));
    }

    // Project the ValueTuple on the stack using the provided AttributeMap.
    // POP - Value (ValueTuple)
    // PUSH - Value (ValueTuple)
    public final void tupleProject(AttributeMap map) {
		push(((ValueTuple)pop()).project(map));
    }
    
    // Join the ValueTuples on the stack.  No checks are done to see if the tuples share
    // attributes in common.  It is assumed that they do not, and the result is simply
    // the concatenation of one tuple onto another.
    //
    // POP - Value (ValueTuple)
    // POP - Value (ValueTuple)
    // PUSH - Value (ValueTuple)
    public final void tupleJoinDisjoint() {
		Value v2 = pop();
		push(((ValueTuple)pop()).joinDisjoint((ValueTuple)v2));        	
    }
    
    // Join the ValueTuples on the stack.  Checks are done to ensure that
    // merging attributes are equal.  An exception is thrown if they are not.
    //
    // POP - Value (ValueTuple)
    // POP - Value (ValueTuple)
    // PUSH - Value (ValueTuple)
    public final void tupleJoin(JoinMap map) {
		Value v2 = pop();
		push(((ValueTuple)pop()).joinChecked(map, (ValueTuple)v2));        	
    }
    
    // Insert tuple in ValueRelationLiteral.
    //
	// POP - ValueTuple
	// POP - ValueRelationLiteral
	// PUSH - ValueRelationLiteral
	public final void relationLiteralInsertTuple() {
		ValueTuple tuple = (ValueTuple)pop();
		((ValueRelationLiteral)peek()).insert(tuple);
	}
    
	// Push literal
	// PUSH - Value
    public final void pushLiteral(Value literal) {
		push(literal);        	
    }
    
	// Duplicate value on top of stack
    public final void duplicate() {
		push(peek());
    }
    
	// Duplicate value under topmost on stack.  Topmost remains unchanged.
    public final void duplicateUnder() {
		Value v = pop();
		push(peek());
		push(v);
    }
    
	// Swap values on top of stack
    public final void swap() {
		Value v1 = pop();
		Value v2 = pop();
		push(v1);
		push(v2);
    }

	// EXACTLY
	// POP - n
	// POP(countOfValues times) - Value
	// PUSH - Value
    public final void exactly(Generator generator, int countOfValues) {
		long n = pop().longValue();
		while (countOfValues-- > 0) {
			if (pop().booleanValue())
				n--;
		}
		push(ValueBoolean.select(generator, n == 0));
    }
    
    // AVERAGE
    // POP(countOfValues times) - Value
    // PUSH - ValueRational
    public final void average(Generator generator, int countOfValues) {
    	double total = 0;
    	for (int i=0; i<countOfValues; i++)
    		total += pop().doubleValue();
    	push(ValueRational.select(generator, total / (double)countOfValues));
    }

    // ||
    // POP - Value
    // POP - Value
    // PUSH - ValueString
    public final void concatenate(Generator generator) {
    	Value v2 = pop();
    	push(ValueCharacter.select(generator, pop().stringValue() + v2.stringValue()));
    }
}