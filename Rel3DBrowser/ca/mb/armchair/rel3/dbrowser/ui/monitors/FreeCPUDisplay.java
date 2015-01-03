package ca.mb.armchair.rel3.dbrowser.ui.monitors;

import java.lang.management.ManagementFactory;

public class FreeCPUDisplay extends PercentDisplay {

	private static final long serialVersionUID = 1L;

	public static int getProcessCPULoad() {
		com.sun.management.OperatingSystemMXBean operatingSystemMXBean = 
		         (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	    
	    operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	    
	    double processCpuLoad = operatingSystemMXBean.getProcessCpuLoad();

	    return 	(int)(processCpuLoad * 100.0);
	}
	
	public FreeCPUDisplay() {
		super("free CPU", new PercentSource() {
			@Override
			public int getPercent() {
				return 100 - getProcessCPULoad();
			}
		});
	}

}
