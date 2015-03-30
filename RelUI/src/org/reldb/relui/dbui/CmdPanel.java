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
	
	private Color red = new Color(getDisplay(), 128, 0, 0);
	private Color green = new Color(getDisplay(), 0, 128, 0);
	private Color blue = new Color(getDisplay(), 0, 0, 128);
	private Color black = new Color(getDisplay(), 0, 0, 0);
	private Color grey = new Color(getDisplay(), 128, 128, 128);
	private Color yellow = new Color(getDisplay(), 255, 215, 0);

	private FileDialog saveDialog;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanel(DbTab dbTab, Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
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
			boolean responseFormatted = false;
			ConcurrentStringReceiverClient connection;
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
				if (isAutoclear)
					clearOutput();
				if (copyInputToOutput)
					userResponse(text);
				try {
					connection = new ConcurrentStringReceiverClient(dbTab) {
						StringBuffer errorBuffer = null;
						StringBuffer reply = new StringBuffer();
						@Override
						public void received(String r) {
							if (r.equals("\n")) {
								return;
							} else if (r.equals("Ok.")) {
								if (showOk)
									goodResponse(r);
								reply = new StringBuffer();
							} else if (r.equals("Cancel.")) {
								System.out.println("CmdPanel: Warning response");
								warningResponse(r);
								reply = new StringBuffer();
						 	} else if (r.startsWith("ERROR:")) {
								badResponse(r);
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
							StyledText inputTextWidget = getInputTextWidget();
							if (errorBuffer != null) {
								ErrorInformation eInfo = parseErrorInformationFrom(errorBuffer.toString());
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
									System.out.println("CmdPanel: Unable to locate error in " + errorBuffer.toString());
							} else {
								inputTextWidget.selectAll();
							}
							inputTextWidget.setFocus();
							done();
						}
					};
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
		
		outputPlain(dbTab.getInitialServerResponse(), black);
		outputHTML(ResponseToHTML.textToHTML(dbTab.getInitialServerResponse()));
		goodResponse("Ok.");
	}
	
	public void dispose() {
		clearOutput();
		red.dispose();
		green.dispose();
		blue.dispose();
		black.dispose();
		grey.dispose();
		yellow.dispose();
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

	/** Handle a received line of 'good' content. */
	private void goodResponse(String s) {
		outputHTML("<div class=\"ok\">" + getResponseFormatted(s, false) + "</div>");
		responseText(s, green);
		outputUpdated();
	}
	
	/** Handle a received line of 'warning' content. */
	private void warningResponse(String s) {
		outputHTML("<div class=\"warn\"><b>" + getResponseFormatted(s, false) + "</b></div>");
		responseText(s, yellow);
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

	private void ensureSaveDialogExists() {
		if (saveDialog == null) {
			saveDialog = new FileDialog(getShell(), SWT.SAVE);
			saveDialog.setFilterPath(System.getProperty("user.home"));
			saveDialog.setText("Save Output");
			saveDialog.setOverwrite(true);
		}		
	}
	
	public void saveOutputAsHtml() {
		ensureSaveDialogExists();
		saveDialog.setFilterExtensions(new String[] {"*.html", "*.*"});
		saveDialog.setFilterNames(new String[] {"HTML", "All Files"});
		String fname = saveDialog.open();
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
		ensureSaveDialogExists();
		saveDialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
		saveDialog.setFilterNames(new String[] {"ASCII text", "All Files"});
		String fname = saveDialog.open();
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

}
