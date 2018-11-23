package org.reldb.dbrowser.loading;

public class Loading {

	private static final int expectedMessageCount = 4;
	private static int msgCount = 0;
	
	public static void open() {
		msgCount = 0;
	}

	public static void close() {
	}

	public static void action(String message) {
		msgCount++;
	}

	public static int getPercentageOfExpectedMessages() {
		return msgCount * 100 / expectedMessageCount;
	}

}
