package ca.mb.armchair.rel3.values;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;

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
