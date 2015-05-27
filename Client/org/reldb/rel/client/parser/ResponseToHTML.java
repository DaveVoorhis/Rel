package org.reldb.rel.client.parser;

import java.io.InputStream;

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
			emitHTML("<font size=\"1\"><i>" + name + "</i></font>");
	}
	
	public void endAttributeSpec() {
		if (!emitHeadings)
			return;
		emitHTML("</th>");
	}
	
	public void beginTupleDefinition() {
		if (isEmitHeadingTypes())
			emitHTML("<font size=\"1\"><i>TUPLE</i></font>");
		emitHTML("<table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
	}
	
	public void endTupleDefinition() {
		emitHTML("</table>");
	}
	
	public void beginContainerDefinition() {
		if (isEmitHeadingTypes())
			emitHTML("<font size=\"1\"><i>RELATION</i></font>");
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
	
	public void endHeading() {
		if (!emitHeadings)
			return;
		emitHTML("</tr>");
	}
	
	public void beginContainer(int depth, String typeName) {
		if (depth == 0)
			emitHTML("<table id=\"table\" cellpadding=\"1\" cellspacing=\"0\">");
		else
			emitHTML("<td valign=\"top\"><table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
	}
	
	public void endContainer(int depth) {
		emitHTML("</table>");
		if (depth > 0)
			emitHTML("</td>");
	}
	
	public void beginTuple(int depth) {
		if (headingDisplayed && depth == 1)
			emitHTML("<td valign=\"top\">");
		if (depth != 2) {
			if (depth == 1)
				emitHTML("<table cellpadding=\"1\" cellspacing=\"0\" width=\"100%\">");
			else
				emitHTML("<table cellpadding=\"1\" cellspacing=\"0\">");
		}
		emitHTML("<tr>");
	}
	
	public void endTuple(int depth) {
		emitHTML("</tr>");
		if (depth != 2)
			emitHTML("</table>");						
		if (headingDisplayed && depth == 1)
			emitHTML("</td>");
	}
	
	public void attributeNameInTuple(int depth, String name) {
		if (!headingDisplayed) {
			emitHTML("<td valign=\"top\">");
			if (isEmitHeading()) 
				emitHTML("<i>" + name + "</i> ");
		}
	}
	
	public void beginScalar(int depth) {
		if (depth == 0)
			emitHTML("<br>");
		else if (headingDisplayed)
			emitHTML("<td valign=\"top\">");
	}
	
	public void endScalar(int depth) {
		if (depth != 0 && headingDisplayed)
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
	
	public void primitive(String value) {
		emitHTML(textToHTML(value));						
	}

	public void beginOperatorDefinition() {
		emitHTML("<font size=\"1\">OPERATOR </font>");
	}
	
	public void beginOperatorDefinitionParameters() {
		emitHTML("<font size=\"1\">(</font>");
	}
	
	public void beginOperatorParameter() {}
	
	public void endOperatorParameter() {}
	
	public void emitOperatorParameterSeparator() {
		emitHTML("<font size=\"1\">, </font>");
	}
	
	public void endOperatorDefinitionParameters() {
		emitHTML("<font size=\"1\">)</font>");
	}
	
	public void beginOperatorReturnType() {
		emitHTML("<font size=\"1\"> RETURNS </font>");
	}
	
	public void endOperatorReturnType() {}
	
	public void endOperatorDefinition() {
	}

}
