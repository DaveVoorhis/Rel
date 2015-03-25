package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;

import org.reldb.rel.client.parser.ResponseToHTML;
import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.rel.client.string.StringReceiverClient;
import org.reldb.relui.dbui.html.BrowserManager;

public class CmdPanel extends Composite {

	private BrowserManager browser;
	private StyledText styledText;
	private CmdPanelInput cmdPanelInput;
	private StringReceiverClient connection;
	
	private Composite outputStack;
	private StackLayout outputStackLayout;

	private StringBuffer serverInitialResponse;
	
	private boolean showOk = true;
	private boolean isShowHeadings = true;
	private boolean isShowHeadingTypes = true;
	private boolean isAutoclear = true;
	
	private Color red = new Color(getDisplay(), 128, 0, 0);
	private Color green = new Color(getDisplay(), 0, 128, 0);
	private Color blue = new Color(getDisplay(), 0, 0, 128);
	private Color black = new Color(getDisplay(), 0, 0, 0);
	private Color grey = new Color(getDisplay(), 128, 128, 128);
	
	public void dispose() {
		clearOutput();
		red.dispose();
		green.dispose();
		blue.dispose();
		black.dispose();
		grey.dispose();
		super.dispose();
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

	private void outputUpdated() {
		cmdPanelInput.done();
		styledText.setCaretOffset(styledText.getCharCount());
		styledText.setSelection(styledText.getCaretOffset(), styledText.getCaretOffset());
		browser.scrollToBottom();
	}
	
	/** Record text responses. */
	private void responseText(String s, Color color) {
		outputPlain(s + '\n', color);
	}

	/** Handle a received line of 'good' content. */
	private void goodResponse(String s) {
		outputHTML("<div class=\"ok\">" + getResponseFormatted(s, false) + "</div>");
		responseText(s, green);
		outputUpdated();
	}

	/** Handle user entry. */
	private void userResponse(String s) {
		outputHTML("<div class=\"user\"><b>" + getResponseFormatted(s, false) + "</b></div>");
		responseText(s, grey);
		outputUpdated();
	}

	/** Handle a received line of system notice. */
	private void systemResponse(String s) {
		outputHTML("<div class=\"note\">" + getResponseFormatted(s, false) + "</div>");
		responseText(s, blue);
		outputUpdated();
	}

	/** Handle a received line of 'bad' content. */
	void badResponse(String s) {
		outputHTML("<div class=\"bad\"><pre><b>" + getResponseFormatted(s, false) + "</b></pre></div>");
		responseText(s, red);
		outputUpdated();
	}
		
	/** Handle a notice. */
	private void noticeResponse(String s) {
		outputHTML("<font color=\"black\"><b>" + getResponseFormatted(s, false) + "</b></font>");
		responseText(s, black);
		outputUpdated();
	}
	
	/** Handle received data. */
	private void response(String s, boolean interpretResponse) {
		outputHTML(getResponseFormatted(s, interpretResponse));
		responseText(s, black);
		outputUpdated();
	}

	// Handle received response.  Return null if ok, or return error location information
	private StringBuffer obtainClientResponse(boolean responseFormatted, StringBuffer captureBuffer) {
		StringBuffer reply = new StringBuffer();
		StringBuffer errorBuffer = null;
		try {
			String r;
			while ((r = connection.receive()) != null) {
				if (r.equals("\n")) {
					continue;
				} else if (r.equals("Ok.")) {
					String content = reply.toString();
					response(content, false);
					if (showOk)
						goodResponse(r);
					reply = new StringBuffer();
					if (captureBuffer != null)
						captureBuffer.append(content);
				} else if (r.startsWith("ERROR:")) {
					String content = reply.toString();
					response(content, false);
					badResponse(r);
					reply = new StringBuffer();
					if (captureBuffer != null)
						captureBuffer.append(content);
					errorBuffer = new StringBuffer();
					if (r.contains(", column")) {
						errorBuffer.append(r);
						errorBuffer.append('\n');
					}
				} else if (r.startsWith("NOTICE")) {
					String content = reply.toString();
					response(content, false);
					noticeResponse(r);
					reply = new StringBuffer();
					if (captureBuffer != null)
						captureBuffer.append(content);
				} else {
					reply.append(r);
					reply.append("\n");
					if (errorBuffer != null) {
						errorBuffer.append(r);
						errorBuffer.append('\n');
					}
				}
			}
			if (reply.length() > 0) {
				String content = reply.toString();
				response(content, responseFormatted);
				if (captureBuffer != null)
					captureBuffer.append(content);
			}
		} catch (IOException ioe) {
			badResponse(ioe.toString());
		}
		return errorBuffer;
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
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanel(DbTab dbTab, Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		this.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println("CmdPanel: resized");
			}
		});
		
		connection = dbTab.getConnection();
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		outputStack = new Composite(sashForm, SWT.NONE);
		outputStackLayout = new StackLayout();
		outputStack.setLayout(outputStackLayout);
		
		styledText = new StyledText(outputStack, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		styledText.setEditable(false);
		
		browser = new BrowserManager();
		browser.createWidget(outputStack, styledText.getFont());
		
		outputStackLayout.topControl = browser.getWidget();
		
		cmdPanelInput = new CmdPanelInput(sashForm, SWT.NONE) {
			boolean copyInputToOutput = true;
			protected void setCopyInputToOutput(boolean selection) {
				copyInputToOutput = selection;
			}
			public void notifyGo(String text) {
				if (isAutoclear)
					clearOutput();
				if (copyInputToOutput)
					userResponse(text);
				String runMe = text.trim();
				StringBuffer errorInformationBuffer = null;
				try {
					if (isLastNonWhitespaceCharacter(runMe, ';')) {
						connection.sendExecute(runMe);
						errorInformationBuffer = obtainClientResponse(false, null);
					} else {
						connection.sendEvaluate(runMe);
						errorInformationBuffer = obtainClientResponse(true, null);
					}
					StyledText inputTextWidget = getInputTextWidget();
					if (errorInformationBuffer != null) {
						ErrorInformation eInfo = parseErrorInformationFrom(errorInformationBuffer.toString());
						if (eInfo != null) {
							int startOffset = 0;
							try {
								if (eInfo.getLine() > 0) {
									startOffset = inputTextWidget.getOffsetAtLine(eInfo.getLine() - 1);
									if (eInfo.getColumn() > 0)
										startOffset += eInfo.getColumn() - 1;
								}
								inputTextWidget.setCaretOffset(startOffset);
								if (eInfo.getBadToken() != null)
									inputTextWidget.setSelection(startOffset, startOffset + eInfo.getBadToken().length());
							} catch (Exception e) {
								System.out.println("CmdPanel: Unable to position to line " + eInfo.getLine() + ", column " + eInfo.getColumn());
							}
						} else
							System.out.println("CmdPanel: Unable to locate error in " + errorInformationBuffer.toString());
					} else {
						inputTextWidget.selectAll();
					}
					inputTextWidget.setFocus();
					done();
				} catch (Throwable ioe) {
					badResponse(ioe.getMessage());
				}
			}
		};
		sashForm.setWeights(new int[] {2, 1});
		
		serverInitialResponse = new StringBuffer();
		obtainClientResponse(false, serverInitialResponse);
		dbTab.getCrashTrap().setServerInitialResponse(serverInitialResponse.toString());
	}

	public void clearOutput() {
		browser.clear();
		styledText.setText("");
	}

	public void copyOutputToInput() {
		cmdPanelInput.setInputText("this is the text");
	}

	public void setEnhancedOutput(boolean selection) {
		outputStackLayout.topControl = (selection) ? browser.getWidget() : styledText;
		outputStack.layout();
	}

	public void setShowOk(boolean selection) {
		showOk = selection;
	}

	public void setHeadingVisible(boolean selection) {
		isShowHeadings = selection;
	}

	public void setHeadingTypesVisible(boolean selection) {
		isShowHeadingTypes = selection;
	}

	public void setAutoclear(boolean selection) {
		isAutoclear = selection;
	}

	public void saveOutputAsHtml() {
		// TODO Auto-generated method stub
		
	}

	public void saveOutputAsText() {
		// TODO Auto-generated method stub
		
	}

}
