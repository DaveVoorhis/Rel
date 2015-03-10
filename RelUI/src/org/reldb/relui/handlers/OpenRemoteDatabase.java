 
package org.reldb.relui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class OpenRemoteDatabase {
	@Execute
	public void execute() {
		System.out.println("handler: Open remote database");		
	}
}
