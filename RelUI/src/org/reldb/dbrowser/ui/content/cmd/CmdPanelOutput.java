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
import org.reldb.dbrowser.ui.DbConnection;
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
	public CmdPanelOutput(Composite parent, DbConnection dbConnection, int style) throws NumberFormatException, ClassNotFoundException, IOException, DatabaseFormatVersionException {
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
		
		connection = new ConcurrentStringReceiverClient(this, dbConnection.obtainStringReceiverClient()) {
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
				errorBuffer = null;
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
	
	protected void notifyInputDone() {}

	protected void notifyInputOfSuccess() {}

	protected void notifyInputOfError(StringBuffer errorBuffer) {}

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
