package org.reldb.dbrowser.ui.version;

public class Version {
	
	public static double getVersionNumber() {
		return 3.005;
	}
	
	public static String getVersion() {
		return String.format("Version %.3f", getVersionNumber());
	}

	public static String getCopyright() {
		return "Copyright 2004 - 2017 Dave Voorhis";
	}
	
	public static String getURL() {
		return "https://reldb.org";
	}
	
	public static String getPreferencesRepositoryName() {
		return "rel.reldb.org";
	}

	public static String getReportLogURL() {
	    return "http://reldb.org/feedback/";		
	}
	
	public static String getUpdateURL() {
		return "http://reldb.org/updates/";
	}
	
}
