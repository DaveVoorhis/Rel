package org.reldb.rel.client.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.rel.client.parser.core.ResponseParser;

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
