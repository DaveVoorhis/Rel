package org.reldb.rel.v0.storage.relvars.external.xls;

import org.reldb.rel.v0.storage.relvars.external.Info;

public class InfoXLS extends Info {
	
	@Override
	public String getIdentifier() {
		return "XLS";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
			"Specify the file name.\n" +
			"Examples:\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test." + getAppropriateFileExtension()[0] + "\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test." + getAppropriateFileExtension()[1] + "\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test." + getAppropriateFileExtension()[0] + ",1\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test." + getAppropriateFileExtension()[1] + ",2,NOHEADING\";\n" +
			"Use optional NOHEADING to not treat first row as a heading row and use it as a data row.\n" +
	        "An optional number after the file name indicates the zero-based tab number.";
	}

	@Override
	public boolean isConnectionStringAFile() {
		return true;
	}

	@Override
	public String[] getAppropriateFileExtension() {
		return new String[] {"xls", "xlsx"};
	}

}
