package ca.mb.armchair.rel3.interpreter;

import java.io.*;

import ca.mb.armchair.rel3.values.Value;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.types.Type;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;

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
