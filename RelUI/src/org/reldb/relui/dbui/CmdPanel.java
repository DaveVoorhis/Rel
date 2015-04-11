package org.reldb.relui.dbui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.reldb.rel.client.parser.ResponseToHTML;
import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.relui.dbui.html.BrowserManager;
import org.reldb.relui.dbui.preferences.PreferenceChangeAdapter;
import org.reldb.relui.dbui.preferences.PreferenceChangeEvent;
import org.reldb.relui.dbui.preferences.PreferenceChangeListener;
import org.reldb.relui.dbui.preferences.PreferencePageCmd;
import org.reldb.relui.dbui.preferences.Preferences;

public class CmdPanel extends Composite {

	private BrowserManager browser;
	private StyledText styledText;
	private CmdPanelInput cmdPanelInput;
	
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
	
	private boolean copyInputToOutput = true;
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
	 */
	public CmdPanel(DbTab dbTab, Composite parent, int style) throws NumberFormatException, ClassNotFoundException, IOException {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		outputStack = new Composite(sashForm, SWT.NONE);
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
		
		connection = new ConcurrentStringReceiverClient(this, dbTab.getURL(), false) {
			StringBuffer errorBuffer = null;
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
					} else
						outputHTML(getResponseFormatted(r, responseFormatted));
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
				if (responseFormatted && reply.length() > 0) {
					String content = reply.toString();
					outputHTML(getResponseFormatted(content, responseFormatted));
					outputUpdated();
				}
				StyledText inputTextWidget = cmdPanelInput.getInputTextWidget();
				if (errorBuffer != null) {
					ErrorInformation eInfo = parseErrorInformationFrom(errorBuffer.toString());
					if (eInfo != null) {
						int offset = 0;
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
				} else {
					inputTextWidget.selectAll();
				}
				inputTextWidget.setFocus();
				cmdPanelInput.done();
			}
		};
			
		cmdPanelInput = new CmdPanelInput(sashForm, SWT.NONE) {
			@Override
			protected void setCopyInputToOutput(boolean selection) {
				copyInputToOutput = selection;
			}
			@Override
			protected void announcement(String msg) {
				systemResponse(msg);
			}
			@Override
			protected void announceError(String msg, Throwable t) {
				badResponse(msg);
			}
			@Override
			public void notifyGo(String text) {
				reply = new StringBuffer();
				if (isAutoclear)
					clearOutput();
				if (copyInputToOutput)
					userResponse(text);
				try {
					if (isLastNonWhitespaceCharacter(text.trim(), ';')) {
						responseFormatted = false;
						connection.sendExecute(text);
					} else {
						responseFormatted = true;
						connection.sendEvaluate(text);
					}
				} catch (Throwable ioe) {
					badResponse(ioe.getMessage());
				}
			}
			@Override
			public void notifyStop() {
				connection.reset();				
			}
		};
		
		sashForm.setWeights(new int[] {2, 1});
		
		outputPlain(connection.getInitialServerResponse(), black);
		outputHTML(ResponseToHTML.textToHTML(connection.getInitialServerResponse()));
		goodResponse("Ok.");
	}
	
	public void clearOutput() {
		browser.clear();
		styledText.setText("");
	}

	public void copyOutputToInput() {
		String selection = styledText.getSelectionText();
		if (selection.length() == 0)
			cmdPanelInput.insertInputText(cmdPanelInput.getInputText() + styledText.getText());
		else
			cmdPanelInput.insertInputText(cmdPanelInput.getInputText() + selection);
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
		cmdPanelInput.dispose();
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
			return "<br>" + ResponseToHTML.textToHTML(s).replace(" ", "&nbsp;");
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
	private void goodResponse(String s) {
		response(s, "ok", green, false);
	}
	
	/** Handle a received line of 'warning' content. */
	private void warningResponse(String s) {
		response(s, "warn", yellow, true);
	}

	/** Handle user entry. */
	private void userResponse(String s) {
		response(s, "user", grey, true);
	}

	/** Handle a received line of system notice. */
	private void systemResponse(String s) {
		response(s, "note", blue, false);
	}

	/** Handle a received line of 'bad' content. */
	void badResponse(String s) {
		response(s, "bad", red, true);
	}
		
	/** Handle a notice. */
	private void noticeResponse(String s) {
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
			System.out.println("PanelCommandline: unable to parse " + eInfo + " due to " + t);
			t.printStackTrace();
		}
		return null;
	}

}
