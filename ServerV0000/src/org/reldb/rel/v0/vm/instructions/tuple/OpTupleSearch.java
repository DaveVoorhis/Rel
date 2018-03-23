package org.reldb.rel.v0.vm.instructions.tuple;

import org.reldb.rel.v0.types.TypeTuple;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpTupleSearch extends Instruction {

	private TypeTuple type;
	
	public OpTupleSearch(TypeTuple type) {
		this.type = type;
	}
	
	@Override
	public void execute(Context context) {
		ValueCharacter regex = (ValueCharacter)context.pop();
		ValueTuple tuple = (ValueTuple)context.pop();
		context.push(ValueBoolean.select(context.getGenerator(), tuple.search(type, regex.stringValue())));
	}

}
