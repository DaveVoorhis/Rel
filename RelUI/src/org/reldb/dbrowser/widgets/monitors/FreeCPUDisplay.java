package org.reldb.dbrowser.widgets.monitors;

import java.lang.management.ManagementFactory;

import org.eclipse.swt.widgets.Composite;

public class FreeCPUDisplay extends PercentDisplay {

	public static int getProcessCPULoad() {
		java.lang.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	    
	    operatingSystemMXBean =  ManagementFactory.getOperatingSystemMXBean();
	    
	    @SuppressWarnings("restriction")
		double processCpuLoad = ((com.sun.management.OperatingSystemMXBean)operatingSystemMXBean).getProcessCpuLoad();

	    return 	(int)(processCpuLoad * 100.0);
	}
	
	public FreeCPUDisplay(Composite parent, int style) {
		super(parent, style, "CPU", new PercentSource() {
			@Override
			public int getPercent() {
				return 100 - getProcessCPULoad();
			}
		});
	}

}
