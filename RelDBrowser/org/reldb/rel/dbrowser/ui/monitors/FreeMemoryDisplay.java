package org.reldb.rel.dbrowser.ui.monitors;

public class FreeMemoryDisplay extends PercentDisplay {

	private static final long serialVersionUID = 1L;

	/** Get memory free as a percentage value between 0 and 100. */
    public static int getMemoryPercentageFreeClassic() {
    	java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
    	long maxmemory = runtime.totalMemory();
    	int memfree = (int)((double)runtime.freeMemory() / (double)maxmemory * 100.0);
    	return memfree;
    }
	
    /** Get memory free as a percentage value between 0 and 100. */
	public static int getMemoryPercentageFree() {
    	java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long estimatedFree = freeMemory + (maxMemory - allocatedMemory);
    	int memfree = (int)((double)estimatedFree / (double)maxMemory * 100.0);
    	return memfree;
    }
	
	public FreeMemoryDisplay() {
		super("free memory", new PercentSource() {
			@Override
			public int getPercent() {
				return getMemoryPercentageFree();
			}
		});
	}

}
