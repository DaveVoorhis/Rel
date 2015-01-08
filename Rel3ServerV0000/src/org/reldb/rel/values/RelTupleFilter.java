package org.reldb.rel.values;

import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Operator;

public class RelTupleFilter extends TupleFilter {

	private Context context;
	private Operator filterOperator;
	
	public RelTupleFilter(Context context, Operator filterOperator) {
		this.context = context;
		this.filterOperator = filterOperator;
	}
	
	@Override
	public boolean filter(ValueTuple tuple) {
		context.push(tuple);
		context.call(filterOperator);
		return (((ValueBoolean)context.pop()).booleanValue());				
	}

}
