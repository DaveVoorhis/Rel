package org.reldb.relui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.reldb.relui.dbui.monitor.Interceptor;
import org.reldb.relui.dbui.monitor.LogWin;
import org.reldb.relui.dbui.monitor.Logger;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("resource")
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
    	class LogMessages implements Logger {
			public void log(String s) {
				LogWin.logMessage(s);
			}
    	};
    	class LogErrors implements Logger {
			public void log(String s) {
				LogWin.logError(s);
			}
    	};
		Interceptor outInterceptor = new Interceptor(System.out, new LogMessages());
		outInterceptor.attachOut();
		Interceptor errInterceptor = new Interceptor(System.err, new LogErrors());
		errInterceptor.attachErr();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		LogWin.remove();
	}

}
