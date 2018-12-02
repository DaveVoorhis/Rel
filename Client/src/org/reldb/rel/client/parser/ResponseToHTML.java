package org.reldb.rel.client.parser;

import java.io.InputStream;

import org.reldb.rel.client.Heading;

public abstract class ResponseToHTML extends ResponseProcessor {

	private boolean emitHeadings = true;
	private boolean emitHeadingTypes = true;
	
	public static String textToHTML(String s) {
		return s.replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	}

	public ResponseToHTML(InputStream input) {
		super(input);
	}

	public ResponseToHTML(String input) {
		super(input);
	}

	public void setEmitHeadings(boolean isShowHeadings) {
		emitHeadings = isShowHeadings;
	}

	public void setEmitHeadingTypes(boolean isShowHeadingTypes) {
		emitHeadingTypes = isShowHeadingTypes;
	}
	
	// Override to receive generated HTML.
	public abstract void emitHTML(String s);
	
	private boolean headingDisplayed = false;

	public void beginAttributeSpec() {
		if (!emitHeadings)
			return;
		emitHTML("<th valign=\"top\">");
	}
	
	public void attributeName(String name) {
		emitHTML(name + "<br>");
	}
	
	public void typeReference(String name) {
		if (emitHeadingTypes)
			emitHTML("<small><i>" + name + "</i></small>");
	}
	
	public void endAttributeSpec() {
		if (!emitHeadings)
			return;
		emitHTML("</th>");
	}
	
	public void beginTupleDefinition() {
		if (isEmitHeadingTypes())
			emitHTML("<small><i>TUPLE</i></small>");
		emitHTML("<table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
	}
	
	public void endTupleDefinition() {
		emitHTML("</table>");
	}
	
	public void beginContainerDefinition() {
		if (isEmitHeadingTypes())
			emitHTML("<small><i>RELATION</i></small>");
		emitHTML("<table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
	}
	
	public void endContainerDefinition() {
		emitHTML("</table>");
	}
	
	public void beginHeading(String typeName) {
		if (!emitHeadings)
			return;
		emitHTML("<tr>");
		headingDisplayed = true;
	}
	
	public Heading endHeading() {
		if (!emitHeadings)
			return null;
		emitHTML("</tr>");
		return null;
	}
	
	public void beginContainer(int depth) {
		emitHTML("<table id=\"table\" cellpadding=\"1\" cellspacing=\"0\">");
	}
	
	public void endContainer(int depth) {
		emitHTML("</table>");
	}
	
	public void beginTuple(int depth) {
		if (depth != 2)
			if (depth == 1)
				emitHTML("<table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
			else
				emitHTML("<table cellpadding=\"1\" cellspacing=\"0\">");
		emitHTML("<tr>");
	}
	
	public void endTuple(int depth) {
		emitHTML("</tr>");
		if (depth != 2)
			emitHTML("</table>");						
	}
	
	public void attributeNameInTuple(int depth, String name) {
		emitHTML("<td valign=\"top\">");
		if (isEmitHeading() && (!headingDisplayed || depth < 1))
			emitHTML("<i>" + name + "</i> ");
	}
	
	public void beginScalar(int depth) {
	}
	
	public void endScalar(int depth) {
		emitHTML("</td>");
	}
	
	public void beginPossrep(String name) {
		emitHTML(name + "(");
	}
	
	public void endPossrep() {
		emitHTML(")");
	}
	
	public void separatePossrepComponent() {
		emitHTML(", ");
	}
	
	public void primitive(String value, boolean quoted) {
		emitHTML(textToHTML(value));						
	}

	public void beginOperatorDefinition() {
		emitHTML("<small>OPERATOR </small>");
	}
	
	public void beginOperatorDefinitionParameters() {
		emitHTML("<small>(</small>");
	}
	
	public void beginOperatorParameter() {}
	
	public void endOperatorParameter() {}
	
	public void emitOperatorParameterSeparator() {
		emitHTML("<small>, </small>");
	}
	
	public void endOperatorDefinitionParameters() {
		emitHTML("<small>)</small>");
	}
	
	public void beginOperatorReturnType() {
		emitHTML("<small> RETURNS </small>");
	}
	
	public void endOperatorReturnType() {}
	
	public void endOperatorDefinition() {
	}

}
