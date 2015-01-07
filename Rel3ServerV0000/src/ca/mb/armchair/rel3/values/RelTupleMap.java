package ca.mb.armchair.rel3.values;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;

public class RelTupleMap extends TupleMap {

	private Context context;
	private Operator mapOperator;
	
	public RelTupleMap(Context context, Operator mapOperator) {
		this.context = context;
		this.mapOperator = mapOperator;
	}
	
	@Override
	public ValueTuple map(ValueTuple tuple) {
		context.push(tuple);
		context.call(mapOperator);
		return (ValueTuple)context.pop();				
	}

}
