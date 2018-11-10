package org.reldb.dbrowser.ui.monitors;

import org.eclipse.swt.widgets.Composite;

public class UsedMemoryDisplay extends PercentDisplay {

	/** Get memory free as a percentage value between 0 and 100. */
	public static int getMemoryPercentageFreeClassic() {
		java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
		long maxmemory = runtime.totalMemory();
		int memfree = (int)((double)runtime.freeMemory() / (double)maxmemory * 100.0);
		return memfree;
	}

	/** Get memory used as a percentage value between 0 and 100. */
	public static int getMemoryPercentageUsed() {
		java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long allocatedMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long estimatedFree = freeMemory + (maxMemory - allocatedMemory);
		return 100 - (int)((double)estimatedFree / (double)maxMemory * 100.0);
	}

	public UsedMemoryDisplay(Composite parent, int style) {
		super(parent, style, "RAM", new PercentSource() {
			@Override
			public int getPercent() {
				return getMemoryPercentageUsed();
			}
		});
	}

}
