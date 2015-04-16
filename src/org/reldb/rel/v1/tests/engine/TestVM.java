package org.reldb.rel.v1.tests.engine;

import org.junit.Test;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.interpreter.Instance;
import org.reldb.rel.v1.interpreter.Interpreter;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.values.*;
import org.reldb.rel.v1.vm.*;
import org.reldb.rel.v1.vm.instructions.core.*;

public class TestVM {

	private Instance instance;
	private Generator generator;
	private VirtualMachine machine;
	
	public TestVM() {
		instance = new Instance("./", true, System.out);
		RelDatabase database = instance.getDatabase();
		Interpreter interpreter = new Interpreter(database, System.out);
		generator = interpreter.getGenerator();
		machine = new VirtualMachine(generator, null, System.out);
	}
	
	private CellMutableFactory cellGenerator = new CellMutableFactory();
	
	@Test
	public void testVMSimple() {
		machine.reset();
		Operator code = new Operator(0);
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 60)));		// PUSH 60
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 45)));		// PUSH 45
		code.compile(new OpAdd());									// +
		code.compile(new OpWriteRaw());								// WRITE
		code.compile(new OpReturn());								// RETURN
		(new Dumper()).dumpMachineCode(code);
		machine.execute(code);
	}

	@Test
	public void testVMComplex() {
		machine.reset();
		Operator code = new Operator(0, 1);
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 200)));			// PUSH 200
		code.compile(new OpVariableInitialise(0, 0, cellGenerator));	// assign to COUNTER
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 3)));		// <HERE> PUSH 3
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 4)));		// PUSH 4
		code.compile(new OpAdd());									// +
		code.compile(new OpWriteRaw());								// WRITE
		code.compile(new OpVariableGet(0, 0));						// COUNTER
		code.compile(new OpWriteRaw());								// WRITE
		code.compile(new OpVariableGet(0, 0));						// COUNTER
		code.compile(new OpPushLiteral(ValueInteger.select(generator, -1)));		// PUSH -1
		code.compile(new OpAdd());									// +
		code.compile(new OpVariableSet(0, 0));						// assign to COUNTER
		code.compile(new OpVariableGet(0, 0));						// COUNTER
		code.compile(new OpPushLiteral(ValueInteger.select(generator, 0)));		// PUSH 0
		code.compile(new OpLte());									// <=
		code.compile(new OpBranchIfTrue(17));						// Jump if true to <DONE>
		code.compile(new OpJump(2));								// Jump to <HERE>
		code.compile(new OpReturn());								// <DONE> RETURN
		(new Dumper()).dumpMachineCode(code);
		machine.execute(code);
	}

	@Test
	public void testVMFnCall() {
		machine.reset();
		
		// void writeInt(int x)
		Operator writeInt = new Operator(1);
		writeInt.setParameterCount(1);
		writeInt.compile(new OpParameterGet(1, 0));					// PUSH x
		writeInt.compile(new OpWriteRaw());							// write X
		writeInt.compile(new OpReturn());							// RETURN
		
		// main
		Operator main = new Operator(0, 1);
		main.compile(new OpPushLiteral(ValueInteger.select(generator, 200)));			// PUSH 200
		main.compile(new OpVariableInitialise(0, 0, cellGenerator));	// assign to COUNTER
		main.compile(new OpNop());									// <HERE> writeInt(
		main.compile(new OpVariableGet(0, 0));						// COUNTER
		main.compile(new OpInvoke(writeInt));					// );
		main.compile(new OpVariableGet(0, 0));						// COUNTER
		main.compile(new OpPushLiteral(ValueInteger.select(generator, -1)));		// PUSH -1
		main.compile(new OpAdd());									// +
		main.compile(new OpVariableSet(0, 0));						// assign to COUNTER
		main.compile(new OpVariableGet(0, 0));						// COUNTER
		main.compile(new OpPushLiteral(ValueInteger.select(generator, 0)));		// PUSH 0
		main.compile(new OpLte());									// <=
		main.compile(new OpBranchIfTrue(14));						// Jump if true to <DONE>
		main.compile(new OpJump(2));								// Jump to <HERE>
		main.compile(new OpReturn());								// <DONE> RETURN
		(new Dumper()).dumpMachineCode(main);
		machine.execute(main);		
	}

}
