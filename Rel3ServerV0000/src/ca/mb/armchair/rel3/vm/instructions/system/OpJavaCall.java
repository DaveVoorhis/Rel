/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import java.lang.reflect.*;

import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.exceptions.*;

public final class OpJavaCall extends Instruction {

	private Method method;
	
	public OpJavaCall(Method method) {
		this.method = method;
	}
	
	public void execute(Context context) {
		try {
			method.invoke(null, new Object [] {context});
		} catch (Throwable error) {
			String trace = "";
			if (error.getCause() != null) {
				trace = error.getCause().getMessage();
				StackTraceElement traceElements[] = error.getCause().getStackTrace();
				for (StackTraceElement element: traceElements)
					if (element.toString().contains(RelDatabase.getRelUserCodePackage())) {
						trace += " in " + element.toString();
						break;
					}
			} else
				trace = error.getMessage();
			throw new ExceptionSemantic("RS0287: Failure executing external function: " + trace);
		}
	}
}
