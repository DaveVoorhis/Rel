package ca.mb.armchair.rel3.client.parser;

public interface ResponseHandler {
	public boolean isEmitHeading();
	public boolean isEmitHeadingTypes();
	public void beginAttributeSpec();
	public void attributeName(String name);
	public void typeReference(String name);
	public void endAttributeSpec();
	public void beginTupleDefinition();
	public void endTupleDefinition();
	public void beginContainerDefinition();
	public void endContainerDefinition();
	public void beginHeading();
	public void endHeading();
	public void attributeNameInTuple(int depth, String name);
	public void beginScalar(int depth);
	public void endScalar(int depth);
	public void beginPossrep(String name);
	public void endPossrep();
	public void separatePossrepComponent();
	public void primitive(String value);
	public void beginContainer(int depth);
	public void endContainer(int depth);
	public void beginTuple(int depth);
	public void endTuple(int depth);
	public void beginOperatorDefinition();
	public void beginOperatorDefinitionParameters();
	public void beginOperatorParameter();
	public void endOperatorParameter();
	public void emitOperatorParameterSeparator();
	public void endOperatorDefinitionParameters();
	public void endOperatorDefinition();
	public void beginOperatorReturnType();
	public void endOperatorReturnType();
}
