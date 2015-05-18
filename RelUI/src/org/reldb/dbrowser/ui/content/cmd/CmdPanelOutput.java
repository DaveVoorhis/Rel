package org.reldb.dbrowser.ui.content.cmd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.reldb.dbrowser.ui.ConcurrentStringReceiverClient;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.html.BrowserManager;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageCmd;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.rel.client.parser.ResponseToHTML;
import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdPanelOutput extends Composite {

	private BrowserManager browser;
	private StyledText styledText;
	
	private Composite outputStack;
	private StackLayout outputStackLayout;
	
	private boolean showOk = true;
	private boolean isEnhancedOutput = true;
	private boolean isShowHeadings = true;
	private boolean isShowHeadingTypes = true;
	private boolean isAutoclear = true;
	
	private Color red = new Color(getDisplay(), 200, 0, 0);
	private Color green = new Color(getDisplay(), 0, 128, 0);
	private Color blue = new Color(getDisplay(), 0, 0, 128);
	private Color black = new Color(getDisplay(), 0, 0, 0);
	private Color grey = new Color(getDisplay(), 128, 128, 128);
	private Color yellow = new Color(getDisplay(), 255, 215, 0);

	private FileDialog saveHtmlDialog;
	private FileDialog saveTextDialog;
	
	private boolean responseFormatted = false;

	private ConcurrentStringReceiverClient connection;

	private StringBuffer reply = new StringBuffer();
			
	private PreferenceChangeListener browserPreferenceChangeListener;
	private PreferenceChangeListener fontPreferenceChangeListener;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NumberFormatException 
	 * @throws DatabaseFormatVersionException 
	 */
	public CmdPanelOutput(Composite parent, DbTab dbTab, int style) throws NumberFormatException, ClassNotFoundException, IOException, DatabaseFormatVersionException {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		outputStack = new Composite(this, SWT.NONE);
		outputStackLayout = new StackLayout();
		outputStack.setLayout(outputStackLayout);
		
		styledText = new StyledText(outputStack, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		styledText.setEditable(false);
		
		browser = new BrowserManager();
		browser.createWidget(outputStack);
		
		browserPreferenceChangeListener = new PreferenceChangeAdapter("CmdPanel_browser") {
			@Override
			public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent) {
				browser.changeWidget(outputStack);
				setEnhancedOutput(getEnhancedOutput());
			}
		};
		
		Preferences.addPreferenceChangeListener(PreferencePageCmd.CMD_BROWSER_SWING, browserPreferenceChangeListener);

		styledText.setFont(Preferences.getPreferenceFont(getDisplay(), PreferencePageCmd.CMD_FONT));
		fontPreferenceChangeListener = new PreferenceChangeAdapter("CmdPanel_font") {
			@Override
			public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent) {
				styledText.setFont(Preferences.getPreferenceFont(getDisplay(), PreferencePageCmd.CMD_FONT));
				browser.setContent(browser.getContent());
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
				
		outputStackLayout.topControl = browser.getWidget();
		
		connection = new ConcurrentStringReceiverClient(this, dbTab.getConnection().obtainStringReceiverClient()) {
			StringBuffer errorBuffer = null;
			StringBuffer compilerErrorBuffer = null;
			@Override
			public void received(String r) {
				if (r.equals("\n")) {
					return;
				} else if (r.equals("Ok.")) {
					if (showOk)
						goodResponse(r);
					reply = new StringBuffer();
				} else if (r.equals("Cancel.")) {
					warningResponse(r);
					reply = new StringBuffer();
			 	} else if (r.startsWith("ERROR:")) {
					badResponse(r);
					outputPlain("\n", black);
			 		if (r.startsWith("ERROR: RS0005"))
			 			compilerErrorBuffer = new StringBuffer();
					reply = new StringBuffer();
					errorBuffer = new StringBuffer();
					if (r.contains(", column")) {
						errorBuffer.append(r);
						errorBuffer.append('\n');
					}
			 	} else if (r.startsWith("NOTICE")) {
					noticeResponse(r);
					reply = new StringBuffer();
				} else {
					if (responseFormatted) {
						reply.append(r);
						reply.append("\n");
					} else if (compilerErrorBuffer == null) {
						outputHTML(getResponseFormatted(r, responseFormatted));
					} else {
						compilerErrorBuffer.append(r);
						compilerErrorBuffer.append("<br>");
					}
					responseText(r, black);
					if (errorBuffer != null) {
						errorBuffer.append(r);
						errorBuffer.append('\n');
					}
				}
			}
			@Override
			public void received(Exception e) {
				badResponse(e.toString());
			}
			@Override
			public void update() {
				outputUpdated();
			}
			@Override
			public void finished() {
				if (compilerErrorBuffer != null) {
					outputHTML("<pre>" + compilerErrorBuffer.toString() + "</pre>");
					compilerErrorBuffer = null;
				}
				if (responseFormatted && reply.length() > 0) {
					String content = reply.toString();
					outputHTML(getResponseFormatted(content, responseFormatted));
				}
				outputUpdated();
				if (errorBuffer != null)
					notifyInputOfError(errorBuffer);
				else
					notifyInputOfSuccess();
				notifyInputDone();
			}
		};
		
		outputPlain(connection.getInitialServerResponse(), black);
		outputHTML(ResponseToHTML.textToHTML(connection.getInitialServerResponse()));
		goodResponse("Ok.");
	}

	public static boolean isLastNonWhitespaceCharacter(String s, char c) {
		int endPosn = s.length() - 1;
		if (endPosn < 0)
			return false;
		while (endPosn >= 0 && Character.isWhitespace(s.charAt(endPosn)))
			endPosn--;
		if (endPosn < 0)
			return false;
		return (s.charAt(endPosn) == c);
	}
	
	protected void notifyInputDone() {
		/*
		cmdPanelInput.done();
		*/
	}

	protected void notifyInputOfSuccess() {
		/*
		inputTextWidget.selectAll();
		*/
	}

	protected void notifyInputOfError(StringBuffer errorBuffer) {
		/*
		ErrorInformation eInfo = parseErrorInformationFrom(errorBuffer.toString());
		if (eInfo != null) {
			int offset = 0;
			StyledText inputTextWidget = cmdPanelInput.getInputTextWidget();
			try {
				if (eInfo.getLine() > 0) {
					int row = eInfo.getLine() - 1;
					offset = inputTextWidget.getOffsetAtLine(row);
					if (eInfo.getColumn() > 0) {
						int outputTabSize = 4;	// should match parserEngine.setTabSize() in org.reldb.rel.v<n>.interpreter.Interpreter
						String inputLine = inputTextWidget.getLine(row);
						int characterIndex = Tabs.displayColumnToCharacterIndex(outputTabSize, inputLine, eInfo.getColumn() - 1);
						offset = characterIndex + inputTextWidget.getOffsetAtLine(row);
					}
				}
				inputTextWidget.setCaretOffset(offset);
				if (eInfo.getBadToken() != null)
					inputTextWidget.setSelection(offset, offset + eInfo.getBadToken().length());
			} catch (Exception e) {
				System.out.println("CmdPanel: Unable to position to line " + eInfo.getLine() + ", column " + eInfo.getColumn());
			}
		} else
			System.out.println("CmdPanel: Unable to locate error in " + errorBuffer.toString());
		errorBuffer = null;
		*/
	}

	public void clearOutput() {
		browser.clear();
		styledText.setText("");
	}

	public void setEnhancedOutput(boolean selection) {
		outputStackLayout.topControl = (selection) ? browser.getWidget() : styledText;
		outputStack.layout();
		isEnhancedOutput = selection;
	}

	public boolean getEnhancedOutput() {
		return isEnhancedOutput;
	}

	public void setShowOk(boolean selection) {
		showOk = selection;
	}

	public boolean getShowOk() {
		return showOk;
	}

	public void setHeadingVisible(boolean selection) {
		isShowHeadings = selection;
	}

	public boolean getHeadingVisible() {
		return isShowHeadings;
	}

	public void setHeadingTypesVisible(boolean selection) {
		isShowHeadingTypes = selection;
	}

	public boolean getHeadingTypesVisible() {
		return isShowHeadingTypes;
	}

	public void setAutoclear(boolean selection) {
		isAutoclear = selection;
	}

	public boolean getAutoclear() {
		return isAutoclear;
	}
	
	public void saveOutputAsHtml() {
		ensureSaveHtmlDialogExists();
		String fname = saveHtmlDialog.open();
		if (fname == null)
			return;
		try {
			BufferedWriter f = new BufferedWriter(new FileWriter(fname));
			f.write(browser.getText());
			f.close();
			systemResponse("Saved " + fname);
		} catch (IOException ioe) {
			badResponse(ioe.toString());
		}
	}

	public void saveOutputAsText() {
		ensureSaveTextDialogExists();
		String fname = saveTextDialog.open();
		if (fname == null)
			return;
		try {
			BufferedWriter f = new BufferedWriter(new FileWriter(fname));
			f.write(styledText.getText());
			f.close();
			systemResponse("Saved " + fname);
		} catch (IOException ioe) {
			badResponse(ioe.toString());
		}
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageCmd.CMD_BROWSER_SWING, browserPreferenceChangeListener);
		Preferences.removePreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
		connection.close();
		clearOutput();
		red.dispose();
		green.dispose();
		blue.dispose();
		black.dispose();
		grey.dispose();
		yellow.dispose();
		super.dispose();
	}

	private void ensureSaveHtmlDialogExists() {
		if (saveHtmlDialog == null) {
			saveHtmlDialog = new FileDialog(getShell(), SWT.SAVE);
			saveHtmlDialog.setFilterPath(System.getProperty("user.home"));
			saveHtmlDialog.setFilterExtensions(new String[] {"*.html", "*.*"});
			saveHtmlDialog.setFilterNames(new String[] {"HTML", "All Files"});
			saveHtmlDialog.setText("Save Output");
			saveHtmlDialog.setOverwrite(true);
		}		
	}
	
	private void ensureSaveTextDialogExists() {
		if (saveTextDialog == null) {
			saveTextDialog = new FileDialog(getShell(), SWT.SAVE);
			saveTextDialog.setFilterPath(System.getProperty("user.home"));
			saveTextDialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
			saveTextDialog.setFilterNames(new String[] {"Text", "All Files"});
			saveTextDialog.setText("Save Output");
			saveTextDialog.setOverwrite(true);
		}		
	}
	
	private void outputPlain(String s, Color color) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = styledText.getCharCount();
		styleRange.length = s.length();
		styleRange.fontStyle = SWT.NORMAL;
		styleRange.foreground = color;		
		styledText.append(s);
		styledText.setStyleRange(styleRange);
	}
	
	private void outputHTML(String s) {
		browser.appendHtml(s);
	}
	
	/** Get formatted response. */
	private String getResponseFormatted(String s, boolean parseResponse) {
		if (parseResponse) {
			try {
				StringBuffer sb = new StringBuffer();
				ResponseToHTML response = new ResponseToHTML(s) {
					public void emitHTML(String generatedHTML) {
						sb.append(generatedHTML);
					}
					public boolean isEmitHeading() {
						return isShowHeadings;
					}
					public boolean isEmitHeadingTypes() {
						return isShowHeadingTypes;
					}
				};
				response.parse();
				return sb.toString();
			} catch (ParseException pe) {
				return "<br>" + ResponseToHTML.textToHTML(s);
			}
		} else {
			return "<br>" + ResponseToHTML.textToHTML(s); // .replace(" ", "&nbsp;");
		}
	}

	private void outputTextUpdated() {
		styledText.setCaretOffset(styledText.getCharCount());
		styledText.setSelection(styledText.getCaretOffset(), styledText.getCaretOffset());		
	}
	
	private void outputHtmlUpdated() {
		browser.scrollToBottom();		
	}
	
	private void outputUpdated() {
		outputTextUpdated();
		outputHtmlUpdated();
	}
	
	/** Record text responses. */
	private void responseText(String s, Color color) {
		outputPlain(s + '\n', color);
	}

	private void response(String msg, String htmlClass, Color colour, boolean bold) {
		String msgPrefixTag;
		String msgSuffixTag;
		if (bold) {
			msgPrefixTag = "<b>";
			msgSuffixTag = "</b>";
		} else {
			msgPrefixTag = "";
			msgSuffixTag = "";			
		}
		outputHTML("<div class=\"" + htmlClass + "\">" + msgPrefixTag + getResponseFormatted(msg, false) + msgSuffixTag + "</div>");
		responseText("\n" + msg, colour);
		outputUpdated();	
	}
	
	/** Handle a received line of 'good' content. */
	public void goodResponse(String s) {
		response(s, "ok", green, false);
	}
	
	/** Handle a received line of 'warning' content. */
	public void warningResponse(String s) {
		response(s, "warn", yellow, true);
	}

	/** Handle user entry. */
	public void userResponse(String s) {
		response(s, "user", grey, true);
	}

	/** Handle a received line of system notice. */
	public void systemResponse(String s) {
		response(s, "note", blue, false);
	}

	/** Handle a received line of 'bad' content. */
	public void badResponse(String s) {
		response(s, "bad", red, true);
	}
		
	/** Handle a notice. */
	public void noticeResponse(String s) {
		response(s, "notice", black, true);
	}
	
	private static class ErrorInformation {
		private int line;
		private int column;
		private String badToken;
		ErrorInformation(int line, int column, String badToken) {
			this.line = line;
			this.column = column;
			this.badToken = badToken;
		}
		int getLine() {
			return line;
		}
		int getColumn() {
			return column;
		}
		String getBadToken() {
			return badToken;
		}
		public String toString() {
			String output = "Error in line " + line;
			if (column >= 0)
				output += ", column " + column;
			if (badToken != null)
				output += " near " + badToken;
			return output;
		}
	}
	
	private ErrorInformation parseErrorInformationFrom(String eInfo) {
		try {
			String badToken = null;
			String errorEncountered = "ERROR: Encountered ";
			if (eInfo.startsWith(errorEncountered)) {
				String atLineText = "at line ";
				int atLineTextPosition = eInfo.indexOf(atLineText);
				int lastBadTokenCharPosition = eInfo.lastIndexOf('"', atLineTextPosition);
				if (lastBadTokenCharPosition >= 0)
					badToken = eInfo.substring(errorEncountered.length() + 1, lastBadTokenCharPosition);
			}
			String lineText = "line ";
			int locatorStart = eInfo.toLowerCase().indexOf(lineText);
			if (locatorStart >= 0) {
				int line = 0;
				int column = 0;
				eInfo = eInfo.substring(locatorStart + lineText.length());
				int nonNumberPosition = 0;
				while (nonNumberPosition < eInfo.length() && Character.isDigit(eInfo.charAt(nonNumberPosition)))
					nonNumberPosition++;
				String lineString = eInfo.substring(0, nonNumberPosition);
				try {
					line = Integer.parseInt(lineString);			
				} catch (NumberFormatException nfe) {
					return null;
				}
				int commaPosition = eInfo.indexOf(',');
				if (commaPosition > 0) {
					eInfo = eInfo.substring(commaPosition + 2);
					String columnText = "column ";
					if (eInfo.startsWith(columnText)) {
						eInfo = eInfo.substring(columnText.length());
						int endOfNumber = 0;
						while (endOfNumber < eInfo.length() && Character.isDigit(eInfo.charAt(endOfNumber)))
							endOfNumber++;
						String columnString = "";
						if (endOfNumber > 0 && endOfNumber < eInfo.length())
							columnString = eInfo.substring(0, endOfNumber);
						else
							columnString = eInfo;
						try {
							column = Integer.parseInt(columnString);
						} catch (NumberFormatException nfe) {
							return null;
						}
						String nearText = "near ";
						int nearTextPosition = eInfo.indexOf(nearText, endOfNumber);
						if (nearTextPosition > 0) {
							int lastQuotePosition = eInfo.lastIndexOf('\'');
							badToken = eInfo.substring(nearTextPosition + nearText.length() + 1, lastQuotePosition);
						}
						return new ErrorInformation(line, column, badToken);
					} else
						return new ErrorInformation(line, -1, badToken);
				} else
					return new ErrorInformation(line, -1, badToken);
			}
		} catch (Throwable t) {
			System.out.println("CmdPanelOutput: unable to parse " + eInfo + " due to " + t);
			t.printStackTrace();
		}
		return null;
	}

	public String getSelectionText() {
		return styledText.getSelectionText();
	}

	public String getText() {
		return styledText.getText();
	}

	public void notifyStop() {
		connection.reset();		
	}

	public void clearReplyBuffer() {
		reply = new StringBuffer();
	}

	public void sendExecute(String text) {
		try {
			clearReplyBuffer();
			responseFormatted = false;
			connection.sendExecute(text);
		} catch (Throwable ioe) {
			badResponse(ioe.getMessage());
		}	
	}

	public void sendEvaluate(String text) {
		try {
			clearReplyBuffer();
			responseFormatted = true;
			connection.sendEvaluate(text);
		} catch (Throwable ioe) {
			badResponse(ioe.getMessage());
		}	
	}

	public void go(String text, boolean copyInputToOutput) {
		if (getAutoclear())
			clearOutput();
		if (copyInputToOutput)
			userResponse(text);
		if (isLastNonWhitespaceCharacter(text.trim(), ';')) {
			sendExecute(text);
		} else {
			sendEvaluate(text);
		}
	}

}
