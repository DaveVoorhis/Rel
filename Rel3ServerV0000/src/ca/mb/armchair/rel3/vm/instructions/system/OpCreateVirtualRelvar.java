/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import java.util.HashMap;

import ca.mb.armchair.rel3.storage.relvars.RelvarDefinition;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.VirtualMachine;

public final class OpCreateVirtualRelvar extends Instruction {

	private RelvarDefinition information;
	private HashMap<String, RelvarDefinition> pendingRelvars;
	
	public OpCreateVirtualRelvar(HashMap<String, RelvarDefinition> pendingRelvars, RelvarDefinition information) {
		this.information = information;
		this.pendingRelvars = pendingRelvars;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createVirtualRelvar(context.getGenerator(), information);
    	pendingRelvars.remove(information.getName());
	}
}
