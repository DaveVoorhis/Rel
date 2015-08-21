package org.reldb.rel.client.parser;

import java.io.InputStream;

import org.reldb.rel.client.Heading;

/** A special HTML generator intended for progressive generation of tabular results. */
public abstract class ResponseToHTMLProgressive extends ResponseToHTML {

	private boolean headingDone = false;
	private int headingCount = 0;
	private int tupleCount = 0;
	
	public ResponseToHTMLProgressive(InputStream input) {
		super(input);
	}

	public ResponseToHTMLProgressive(String input) {
		super(input);
	}

	// Override to receive initial generated HTML.  This may be invoked multiple times before endInitialHTML() is called.
	public abstract void emitInitialHTML(String s);
	
	// Override to receive notification that all emitInitialHTML() calls have been done.
	public abstract void endInitialHTML();
	
	// Override to receive additional generated HTML.  This will typically be a table row,
	// to be inserted into the HTML table (with id='table') defined via emitInitialHTML()
	public abstract void emitProgressiveHTML(String s);
	
	// Override to receive notification that a table row has ended.
	public abstract void endProgressiveHTMLRow();
	
	public void emitHTML(String s) {
		if (headingDone)
			emitProgressiveHTML(s);
		else
			emitInitialHTML(s);
	}
	
	public void beginHeading(String typeName) {
		super.beginHeading(typeName);
		headingCount++;
	}
	
	public Heading endHeading() {
		super.endHeading();
		headingCount--;
		if (headingCount == 0) {
			emitHTML("</table>");		
			endInitialHTML();
			headingDone = true;
		}
		return null;
	}
	
	public void endContainer(int depth) {
		if (depth > 0)
			super.endContainer(depth);
	}

	public void beginTuple(int depth) {
		super.beginTuple(depth);
		tupleCount++;
	}
	
	public void endTuple(int depth) {
		super.endTuple(depth);
		tupleCount--;
		if (tupleCount == 0)
			endProgressiveHTMLRow();
	}
	
}
