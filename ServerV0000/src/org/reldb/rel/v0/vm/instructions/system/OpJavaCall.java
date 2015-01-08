/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.system;

import java.lang.reflect.*;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

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
