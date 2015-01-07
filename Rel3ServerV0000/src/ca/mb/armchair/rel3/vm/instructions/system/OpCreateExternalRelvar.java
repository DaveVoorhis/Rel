/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import java.util.HashMap;

import ca.mb.armchair.rel3.storage.relvars.RelvarDefinition;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.VirtualMachine;

public final class OpCreateExternalRelvar extends Instruction {

	private RelvarDefinition information;
	private HashMap<String, RelvarDefinition> pendingRelvars;
	
	public OpCreateExternalRelvar(HashMap<String, RelvarDefinition> pendingRelvars, RelvarDefinition information) {
		this.information = information;
		this.pendingRelvars = pendingRelvars;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createExternalRelvar(context.getGenerator(), information);
    	pendingRelvars.remove(information.getName());
	}
}
