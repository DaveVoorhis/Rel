package org.reldb.rel.v0.values;

import java.io.PrintStream;
import java.io.Serializable;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;

public class ValueOperator extends ValueAbstract implements Serializable {

	private static final long serialVersionUID = 0;
	
	private String source;

	private volatile Operator operator;
	private volatile Context enclosingContext;
	
	/** Create a new operator Value. */
	public ValueOperator(Generator generator, Operator operator, String source) {
		super(generator);
		this.operator = operator;
		this.source = source;
		enclosingContext = null;
		loaded(generator);
	}
	
	public void loaded(Generator generator) {
		super.loaded(generator);
	}

	public void setEnclosingContext(Context context) {
		enclosingContext = context;
	}
	
	public Context getEnclosingContext() {
		return enclosingContext;
	}
	
	/** Obtain a serializable clone of this value. */
	public Value getSerializableClone() {
		return new ValueOperator(getGenerator(), null, source);
	}

	public String getTypeName() {
		return "OPERATOR";
	}

	public Operator getOperator(Generator generator) {
		if (operator == null)
			operator = generator.getDatabase().compileAnonymousOperator(getSource(), generator.getPrintStream()).operator;
		return operator;
	}
	
	/** Output this Value to a PrintStream. */
	public void toStream(Generator generator, Context context, Type type, PrintStream p, int depth) {
		p.print(toString());
	}

	public int hashCode() {
		return toString().hashCode();
	}
	
	public int compareTo(Value v) {
		// questionable to compare on operator text, but it's the best we've got
		return getSource().compareTo(((ValueOperator)v).getSource());
	}

	public String getSource() {
		return "OPERATOR " + source + " END OPERATOR";		
	}
	
	public String toString() {
		return getSource();
	}

}
