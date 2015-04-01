package org.reldb.relui.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.reldb.relui.dbui.DbMain;
import org.reldb.relui.dbui.monitor.Interceptor;
import org.reldb.relui.dbui.monitor.LogWin;
import org.reldb.relui.dbui.monitor.Logger;

public class Main {
	@Inject
	public Main() {		
	}
	
	@SuppressWarnings("resource")
	@PostConstruct
	public void postConstruct(Composite parent) {
    	class Log implements Logger {
			public void log(String s) {
				LogWin.log(s);
			}        		
    	};
		Interceptor outInterceptor = new Interceptor(System.out, new Log());
		outInterceptor.attachOut();
		Interceptor errInterceptor = new Interceptor(System.err, new Log());
		errInterceptor.attachErr();
		DbMain.run(parent);
	}

}