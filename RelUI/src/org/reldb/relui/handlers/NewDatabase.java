 
package org.reldb.relui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class NewDatabase {
	@Execute
	public void execute() {
		System.out.println("handler: New database");		
	}
}