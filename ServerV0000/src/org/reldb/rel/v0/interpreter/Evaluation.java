package org.reldb.rel.v0.interpreter;

import java.io.*;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.vm.Context;

public class Evaluation {
	private Context context;
	private Type type;
	private Value value;
	
	public Evaluation(Context context, Type type, Value value) {
		this.context = context;
		this.type = type;
		this.value = value;
	}
	
	public void toStream(PrintStream stream) {
		value.toStream(context, type, stream, (type instanceof TypeCharacter) ? 1 : 0);
	}
	
	public Type getType() {
		return type;
	}
	
	public Value getValue() {
		return value;
	}
}
