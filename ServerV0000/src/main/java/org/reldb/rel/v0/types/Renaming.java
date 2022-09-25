package org.reldb.rel.v0.types;

import java.util.LinkedList;

/** Specify a parallel renaming of attributes in a Heading. */
public class Renaming {
	
	private LinkedList<FromTo> renamings = new LinkedList<>();
	
	public static class FromTo {
		private String from;
		private String to;
		public FromTo(String from, String to) {
			this.from = from;
			this.to = to;
		}
		public String getFrom() {
			return from;
		}
		public String getTo() {
			return to;
		}
	}
	
	public static class FromToPrefix extends FromTo {
		public FromToPrefix(String from, String to) {
			super(from, to);
		}
	}
	
	public static class FromToSuffix extends FromTo {
		public FromToSuffix(String from, String to) {
			super(from, to);
		}
	}
	
	public void addRename(String from, String to) {
		renamings.add(new FromTo(from, to));
	}
	
	public void addRenamePrefix(String from, String to) {
		renamings.add(new FromToPrefix(from, to));
	}
	
	public void addRenameSuffix(String from, String to) {
		renamings.add(new FromToSuffix(from, to));
	}
	
	public FromTo[] getRenamings() {
		return renamings.toArray(new FromTo[0]);
	}
}
