package org.reldb.dbrowser.ui;

public class Loading {

	private static final int expectedMessageCount = 4;
	private static int msgCount = 0;
	
	public static void start() {
		msgCount = 0;
	}

	public static void action(String message) {
		msgCount++;
	}

	public static int getPercentageOfExpectedMessages() {
		return msgCount * 100 / expectedMessageCount;
	}

}
