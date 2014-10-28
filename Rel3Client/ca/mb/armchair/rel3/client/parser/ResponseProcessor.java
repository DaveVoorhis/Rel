package ca.mb.armchair.rel3.client.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import ca.mb.armchair.rel3.client.parser.core.ResponseParser;
import ca.mb.armchair.rel3.client.parser.core.ParseException;

public class ResponseProcessor extends ResponseAdapter {
	
	private InputStream input;
	
	public ResponseProcessor(InputStream input) {
		this.input = input;
	}
	
	public ResponseProcessor(String s) {
		this(new ByteArrayInputStream(s.getBytes()));
	}
	
	public void parse() throws ParseException {
		ResponseParser parser = new ResponseParser(input);
		parser.setResponseHandler(this);
		parser.parse();
	}
	
}
