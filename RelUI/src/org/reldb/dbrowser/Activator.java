package org.reldb.dbrowser;

import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.ResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.reldb.dbrowser.hooks.OpenDocumentEventProcessor;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private static OpenDocumentEventProcessor openDocProcessor = new OpenDocumentEventProcessor();

	static BundleContext getContext() {
		return context;
	}

	static String[] args = null;
	
	public static String[] getApplicationArguments() {
		return args;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
		Display.getDefault().addListener(SWT.OpenDocument, openDocProcessor);
		
		ServiceReference<?> ser = context.getServiceReference(IApplicationContext.class);
        IApplicationContext iac = (IApplicationContext)context.getService(ser);
        args = (String[])iac.getArguments().get("application.args");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		context = null;
		ResourceManager.dispose();
		System.out.println("RelUI has left the building.");
	}

}
