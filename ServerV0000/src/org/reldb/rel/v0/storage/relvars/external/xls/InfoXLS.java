package org.reldb.rel.v0.storage.relvars.external.xls;

import org.reldb.rel.v0.storage.relvars.external.Info;
import org.reldb.rel.v0.storage.relvars.external.InfoComponent;
import org.reldb.rel.v0.storage.relvars.external.InfoComponentOption;

public class InfoXLS extends Info {
	
	@Override
	public String getIdentifier() {
		return "XLS";
	}

	@Override
	public String getDescription() {
		return "Connection to an XLS or XLSX spreadsheet file.";
	}

	@Override
	public String getConnectionStringDocumentation() {
		return
			"Specify the file name.\n" +
			"Examples:\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test.xlsx\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test.xls\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test.xlsx,1\";\n" +
		    "\tVAR myvar EXTERNAL XLS \"/home/dave/test.xls,2,NOHEADING\";\n" +
			"Use optional NOHEADING to not treat first row as a heading row and use it as a data row.\n" +
	        "An optional number after the file name indicates the zero-based tab number.";
	}

	@Override
	public InfoComponent[] getConnectionStringComponents() {
		return new InfoComponent[] {
			new InfoComponent(0) {
				@Override
				public boolean isOptional() {
					return false;
				}
				@Override
				public boolean isAFile() {
					return true;
				}
				@Override
				public String[] getAppropriateFileExtension() {
					return new String[] {"xls", "xlsx"};
				}
				@Override
				public String getDocumentation() {
					return "Path to spreadsheet file.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return null;
				}
			},
			new InfoComponent(1) {
				@Override
				public boolean isOptional() {
					return true;
				}
				@Override
				public boolean isAFile() {
					return false;
				}
				@Override
				public String[] getAppropriateFileExtension() {
					return null;
				}
				@Override
				public String getDocumentation() {
					return "Zero-based tab number. E.g., 0 for first tab, 1 for second, etc.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return null;
				}
			},
			new InfoComponent(2) {
				@Override
				public boolean isOptional() {
					return true;
				}
				@Override
				public boolean isAFile() {
					return false;
				}
				@Override
				public String[] getAppropriateFileExtension() {
					return null;
				}
				@Override
				public String getDocumentation() {
					return "Select NOHEADING if the spreadsheet does not start with a heading row.";
				}
				@Override
				public InfoComponentOption[] getOptions() {
					return new InfoComponentOption[] {
						new InfoComponentOption() {
							@Override
							public String getDocumentation() {
								return "Spreadsheet does not start with a heading row.";
							}
							@Override
							public String getOptionText() {
								return "NOHEADING";
							}
						}
					};
				}
			}
		};
	}

}
