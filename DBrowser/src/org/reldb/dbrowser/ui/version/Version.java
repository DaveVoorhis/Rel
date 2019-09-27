package org.reldb.dbrowser.ui.version;

public class Version {
	
	public static double getVersionNumber() {
		return 3.015;
	}
	
	public static String getVersion() {
		return String.format("Version %.3f", getVersionNumber());
	}

	public static String getCopyright() {
		return "Copyright 2004 - 2019 Dave Voorhis";
	}
	
	public static String getURL() {
		return "https://reldb.org";
	}
	
	public static String getPreferencesRepositoryName() {
		return ".rel";
	}

	public static String getReportLogURL() {
	    return "http://reldb.org/feedback/";		
	}
	
	public static String getUpdateURL() {
		return "http://reldb.org/updates/";
	}

	public static String getAppName() {
		return "Rel";
	}

	public static String getAppID() {
		return getAppName() + " " + getVersion();
	}
	
	public static String[] getIconsPaths() {
		final String base = "org/reldb/dbrowser/icons/appicon/";
		return new String[] {
			base + "16x16.png",
			base + "24x24.png",
			base + "32x32.png",
			base + "48x48.png",
			base + "64x64.png",
			base + "96x96.png",
			base + "128x128.png",
			base + "256x256.png",
		};
	}
	
}
