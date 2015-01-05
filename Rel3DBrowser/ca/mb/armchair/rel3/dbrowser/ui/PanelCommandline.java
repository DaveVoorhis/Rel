package ca.mb.armchair.rel3.dbrowser.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

import java.awt.Font;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Vector;

import ca.mb.armchair.rel3.client.crash.CrashTrap;
import ca.mb.armchair.rel3.client.parser.ResponseToHTML;
import ca.mb.armchair.rel3.client.parser.core.ParseException;

import ca.mb.armchair.rel3.dbrowser.style.DBrowserStyle;
import ca.mb.armchair.rel3.dbrowser.utilities.Preferences;
import ca.mb.armchair.rel3.dbrowser.version.Version;

/**
 * JPanel derivative for displaying a command-line interface.
 *
 * @author  dave
 */
public class PanelCommandline extends javax.swing.JPanel implements EditorOptions {
	private final static long serialVersionUID = 0;
	
	private javax.swing.Timer scrollTimer;
	private javax.swing.Timer silentTimer;
	private javax.swing.Timer progressTimer;

	private JTextComponent jTextAreaOutput;

	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;

	private StringBuffer formattedOut = new StringBuffer();
	private StringBuffer textOut = new StringBuffer();
	private StringBuffer serverInitialResponse = new StringBuffer();
	private String lastText;

	private boolean inputEnabled = false;

	private boolean isShowHeadings = true;
	private boolean isShowHeadingTypes = true;
	
	private SessionPanel session;
		
	private static boolean isLastNonWhitespaceCharacter(String s, char c) {
		int endPosn = s.length() - 1;
		if (endPosn < 0)
			return false;
		while (endPosn >= 0 && Character.isWhitespace(s.charAt(endPosn)))
			endPosn--;
		if (endPosn < 0)
			return false;
		return (s.charAt(endPosn) == c);
	}
	
	/** Refresh display and scroll down. */
	private void scrollDown() {
		Runnable runner = new Runnable() {
			public void run() {
				javax.swing.JScrollBar vscroller = jScrollPaneOutput.getVerticalScrollBar();
				vscroller.setValue(vscroller.getMaximum());
			}			
		};
		if (javax.swing.SwingUtilities.isEventDispatchThread())
			runner.run();
		else
			javax.swing.SwingUtilities.invokeLater(runner);
	}

	/** Set up the scrollTimer. */
	private void initScrollTimer() {
		scrollTimer = new javax.swing.Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scrollDown();
			}
		});
		scrollTimer.setRepeats(true);
	}

	/** Set up silence timer. */
	private void initSilentTimer() {
		silentTimer = new javax.swing.Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				scrollTimer.stop();
				scrollDown();
			}
		});
		silentTimer.setRepeats(false);
	}
	
	/** Set up the progress timer. */
	private void initProgressTimer() {
		progressTimer = new javax.swing.Timer(250, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateProgress();
			}
		});
		progressTimer.setRepeats(true);
	}

	/** Update output display */
	private void updateOutput() {
		final String mostRecentTextLine = lastText;
		Runnable runner = new Runnable() {
			public void run() {
				if (isEnhancedOutput())
					jTextAreaOutput.setText(formattedOut.toString());
				else
					((JTextArea) jTextAreaOutput).append(mostRecentTextLine);						
			}
		};
		if (javax.swing.SwingUtilities.isEventDispatchThread())
			runner.run();
		else
			javax.swing.SwingUtilities.invokeLater(runner);
		scrollDown();
	}

	/** Initialise timers. */
	private void initTimers() {
		initScrollTimer();
		initSilentTimer();
		initProgressTimer();
	}

	/** Start timer timers. */
	private void startOutputUpdateTimers() {
		scrollTimer.start();
		silentTimer.restart();
	}

	/** Get number of items in History. */
	private int getHistorySize() {
		return entryHistory.size();
	}

	/** Get history item. */
	private String getHistoryItemAt(int index) {
		if (index < 0 || index >= entryHistory.size())
			return null;
		return entryHistory.get(index);
	}

	/** Get previous history item. */
	private String getPreviousHistoryItem() {
		if (currentHistoryItem > 0)
			currentHistoryItem--;
		setButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Get next history item. */
	private String getNextHistoryItem() {
		currentHistoryItem++;
		if (currentHistoryItem >= entryHistory.size())
			currentHistoryItem = entryHistory.size() - 1;
		setButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Add a history item. */
	private void addHistoryItem(String s) {
		entryHistory.add(s);
		currentHistoryItem = entryHistory.size() - 1;
		setButtons();
	}

	/** Set up history button status. */
	private void setButtons() {
		jButtonPreviousCommand.setEnabled(currentHistoryItem > 0 && entryHistory.size() > 1);
		jButtonNextCommand.setEnabled(currentHistoryItem < entryHistory.size() - 1
						&& entryHistory.size() > 1);
	}

	/** True if input is enabled. */
	private boolean isInputEnabled() {
		return inputEnabled;
	}

	/** Lock the interface. */
	private void disableInput() {
		jTextAreaInput.setEnabled(false);
		jButtonRun.setEnabled(false);
		inputEnabled = false;
	}

	/** Unlock the interface. */
	private void enableInput() {
		jTextAreaInput.setEnabled(true);
		jButtonRun.setEnabled(true);
		inputEnabled = true;
	}

	/** Initialisation. */
	private void initialise() {
		initComponents();
		// React to changes in the font.
		Preferences.getInstance().addInputOutputFontChangeListener(
				new Preferences.InputOutputFontChangeListener() {
					public void fontChanged(java.awt.Font font) {
						jTextAreaInput.setFont(font);
						jTextAreaOutput.setFont(font);
					}
				});
		initTimers();
		FileNameExtensionFilter filterD = new FileNameExtensionFilter("D source files", "d");
		jFileChooserGetPath.addChoosableFileFilter(new FileNameExtensionFilter("XLS", "xls"));
		jFileChooserGetPath.addChoosableFileFilter(new FileNameExtensionFilter("XLSX", "xlsx"));
		jFileChooserGetPath.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));
		jFileChooserLoad.addChoosableFileFilter(filterD);
		jFileChooserSave.addChoosableFileFilter(filterD);
		FileNameExtensionFilter filterTXT = new FileNameExtensionFilter("Text files", "txt");
		jFileChooserSaveOutputText.addChoosableFileFilter(filterTXT);
		jFileChooserSaveOutputText.addChoosableFileFilter(filterD);
		jFileChooserSaveOutputText.setDialogTitle("Save Text");
		FileNameExtensionFilter filterHTML = new FileNameExtensionFilter("HTML files", "html", "htm");
		jFileChooserSaveOutputFormatted.addChoosableFileFilter(filterHTML);
		jFileChooserSaveOutputFormatted.setDialogTitle("Save HTML");
		setButtons();
		jCheckBoxEnhanced.setSelected(true);
		setEnhancedOutput(true);		
		disableInput();
		jSplitPaneMain.setDividerLocation(0.75);
	}
	
	/** Creates new form PanelCommandline to connect to Rel server. */
	public PanelCommandline(SessionPanel session) {
		this.session = session;
		initialise();
	}

	public void go() {
		startProcessingDisplay();
		obtainClientResponse(false, serverInitialResponse);
		enableInput();
		endProcessingDisplay();
	}
	
	/** Invoked when client hosts local server. */
	public void serverhosted() {
		systemResponse("Local server hosted.");
	}

	/** Invoked when locally hosted server is lost. */
	public void serverunhosted() {
		badResponse("Local server lost.");
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {
		jFileChooserGetPath = new javax.swing.JFileChooser();
		jFileChooserLoad = new javax.swing.JFileChooser();
		jFileChooserSave = new javax.swing.JFileChooser();
		jFileChooserSaveOutputText = new javax.swing.JFileChooser();
		jFileChooserSaveOutputFormatted = new javax.swing.JFileChooser();
		jFileChooserSaveBackup = new javax.swing.JFileChooser();
		jSplitPaneMain = new javax.swing.JSplitPane();
		jPanelOutput = new javax.swing.JPanel();
		jScrollPaneOutput = new javax.swing.JScrollPane();
		jToolBarOutput = new javax.swing.JToolBar();
		jButtonClearOutput = new javax.swing.JButton();
		jButtonSaveOutputFormatted = new javax.swing.JButton();
		jButtonSaveOutputText = new javax.swing.JButton();
		jButtonCopyToInput = new javax.swing.JButton();
		jCheckBoxEnhanced = new javax.swing.JCheckBox();
		jCheckBoxSuppressOk = new javax.swing.JCheckBox();
		jCheckBoxAutoclear = new javax.swing.JCheckBox();
		jCheckBoxWrapOutput = new javax.swing.JCheckBox();
		jPanelInput = new javax.swing.JPanel();
		jScrollPaneInput = new javax.swing.JScrollPane();
		jTextAreaInput = new javax.swing.JTextArea();
		jToolBarInput = new javax.swing.JToolBar();
		jButtonPreviousCommand = new javax.swing.JButton();
		jButtonNextCommand = new javax.swing.JButton();
		jButtonClearInput = new javax.swing.JButton();
		jButtonLoadCommand = new javax.swing.JButton();
		jButtonGetPath = new javax.swing.JButton();
		jButtonSaveCurrent = new javax.swing.JButton();
		jButtonSaveHistory = new javax.swing.JButton();
		jButtonBackup = new javax.swing.JButton();
		jCheckBoxCopyToOutput = new javax.swing.JCheckBox();
		jCheckBoxWrapInput = new javax.swing.JCheckBox();
		jCheckBoxShowHeadings = new javax.swing.JCheckBox();
		jCheckBoxShowHeadingTypes = new javax.swing.JCheckBox();
		jPanelBottom = new javax.swing.JPanel();
		jButtonRun = new javax.swing.JButton();
		jLabelCaretPosition = new javax.swing.JLabel();
		jLabelRunning = new javax.swing.JLabel();
		jTextAreaOutputFormatted = new JTextPane();
		jTextAreaOutputPlain = new JTextArea();

		jFileChooserSave.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		jFileChooserSaveOutputText.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		jFileChooserSaveOutputFormatted.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
		jFileChooserSaveBackup.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

		setLayout(new java.awt.BorderLayout());

		jSplitPaneMain.setDividerLocation(50);
		jSplitPaneMain.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPaneMain.setResizeWeight(0.75);
		jSplitPaneMain.setLastDividerLocation(50);
		jSplitPaneMain.setOneTouchExpandable(true);
		jPanelOutput.setLayout(new java.awt.BorderLayout());

		jScrollPaneOutput.setAutoscrolls(true);
		jPanelOutput.add(jScrollPaneOutput, java.awt.BorderLayout.CENTER);

		jToolBarOutput.setRollover(true);
		jToolBarOutput.setFloatable(false);
		jToolBarOutput.setName("Rel Output Toolbar");
		jButtonClearOutput.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonClearOutput.setText("Clear");
		jButtonClearOutput
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						clearOutput();
					}
				});

		jToolBarOutput.add(jButtonClearOutput);

		jButtonSaveOutputFormatted.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonSaveOutputFormatted.setText("Save as HTML");
		jButtonSaveOutputFormatted
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveOutputFormatted();
					}
				});

		jToolBarOutput.add(jButtonSaveOutputFormatted);

		jButtonSaveOutputText.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonSaveOutputText.setText("Save as text");
		jButtonSaveOutputText
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveOutputText();
					}
				});

		jToolBarOutput.add(jButtonSaveOutputText);
		
		jButtonCopyToInput.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonCopyToInput.setText("Copy to Input");
		jButtonCopyToInput
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						copyOutputToInput();
					}
				});

		jToolBarOutput.add(jButtonCopyToInput);

		jCheckBoxEnhanced.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxEnhanced.setText("Enhanced");
		jCheckBoxEnhanced
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						setEnhancedOutput(jCheckBoxEnhanced.isSelected());
					}
				});

		jToolBarOutput.add(jCheckBoxEnhanced);

		jCheckBoxSuppressOk.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxSuppressOk.setText("Suppress 'Ok.'");

		jToolBarOutput.add(jCheckBoxSuppressOk);
		
		jCheckBoxAutoclear.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxAutoclear.setSelected(true);
		jCheckBoxAutoclear.setText("Autoclear");
		jCheckBoxAutoclear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (!jCheckBoxAutoclear.isSelected())
					javax.swing.JOptionPane.showMessageDialog(null, "Allowing the output to grow without clearing it regularly may cause you to run out of memory!", "WARNING", javax.swing.JOptionPane.WARNING_MESSAGE);				
			}
		});
		jToolBarOutput.add(jCheckBoxAutoclear);

		jCheckBoxWrapOutput.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxWrapOutput.setSelected(true);
		jCheckBoxWrapOutput.setText("Wrap");
		jCheckBoxWrapOutput.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (jTextAreaOutput instanceof JTextArea)
					((JTextArea) jTextAreaOutput).setLineWrap(jCheckBoxWrapOutput.isSelected());
			}
		});
		jToolBarOutput.add(jCheckBoxWrapOutput);

		jCheckBoxShowHeadings.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxShowHeadings.setSelected(isShowHeadings);
		jCheckBoxShowHeadings.setText("Heading");
		jCheckBoxShowHeadings.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				isShowHeadings = jCheckBoxShowHeadings.isSelected();
				jCheckBoxShowHeadingTypes.setVisible(isShowHeadings);
			}
		});
		jToolBarOutput.add(jCheckBoxShowHeadings);

		jCheckBoxShowHeadingTypes.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxShowHeadingTypes.setSelected(isShowHeadingTypes);
		jCheckBoxShowHeadingTypes.setText("Types");
		jCheckBoxShowHeadingTypes.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				isShowHeadingTypes = jCheckBoxShowHeadingTypes.isSelected();
			}
		});
		jToolBarOutput.add(jCheckBoxShowHeadingTypes);
		
		jPanelOutput.add(jToolBarOutput, java.awt.BorderLayout.NORTH);

		jSplitPaneMain.setTopComponent(jPanelOutput);

		jPanelInput.setLayout(new java.awt.BorderLayout());

		jTextAreaInput.setLineWrap(true);
		jTextAreaInput.setFont(Preferences.getInstance().getInputOutputFont());
		jTextAreaInput
				.setToolTipText("Enter Tutorial D code here.  Press F5 to execute.");
		jTextAreaInput.setEnabled(false);
		jTextAreaInput.addCaretListener(new javax.swing.event.CaretListener() {
			public void caretUpdate(javax.swing.event.CaretEvent evt) {
				int offset = evt.getDot();
				try {
					int line = jTextAreaInput.getLineOfOffset(offset);
					int column = offset - jTextAreaInput.getLineStartOffset(line);
					jLabelCaretPosition.setText("" + (line + 1) + ":" + (column + 1));
				} catch (BadLocationException ble) {
					jLabelCaretPosition.setText("?:?");
				}
				if (isLastNonWhitespaceCharacter(jTextAreaInput.getText(), ';')) {
					jButtonRun.setText("Execute (F5)");
				} else {
					jButtonRun.setText("Evaluate (F5)");
				}
			}
		});
		jTextAreaInput.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				PanelCommandline.this.keyReleased(evt);
			}
		});
		Preferences.getInstance().addInputOutputFontChangeListener(new Preferences.InputOutputFontChangeListener() {
			public void fontChanged(Font font) {
				jTextAreaInput.setFont(Preferences.getInstance().getInputOutputFont());
			}
		});

		jScrollPaneInput.setViewportView(jTextAreaInput);

		jPanelInput.add(jScrollPaneInput, java.awt.BorderLayout.CENTER);

		jToolBarInput.setRollover(true);
		jToolBarInput.setFloatable(false);
		jToolBarInput.setName("Rel Input Toolbar");
		jButtonPreviousCommand.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonPreviousCommand.setText("<");
		jButtonPreviousCommand
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						previousCommand();
					}
				});

		jToolBarInput.add(jButtonPreviousCommand);

		jButtonNextCommand.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonNextCommand.setText(">");
		jButtonNextCommand
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						nextCommand();
					}
				});

		jToolBarInput.add(jButtonNextCommand);

		jButtonClearInput.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonClearInput.setText("Clear");
		jButtonClearInput
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						clearInput();
					}
				});

		jToolBarInput.add(jButtonClearInput);

		jButtonLoadCommand.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonLoadCommand.setText("Load");
		jButtonLoadCommand
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						loadCommand();
					}
				});

		jToolBarInput.add(jButtonLoadCommand);
		
		jButtonGetPath.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonGetPath.setText("Get File Path");
		jButtonGetPath
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						getPath();
					}
				});

		jToolBarInput.add(jButtonGetPath);
		
		jButtonSaveCurrent.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonSaveCurrent.setText("Save");
		jButtonSaveCurrent
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveCommandCurrent();
					}
				});

		jToolBarInput.add(jButtonSaveCurrent);

		jButtonSaveHistory.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonSaveHistory.setText("Save history");
		jButtonSaveHistory
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						saveCommandHistory();
					}
				});

		jToolBarInput.add(jButtonSaveHistory);

		jCheckBoxCopyToOutput.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxCopyToOutput.setSelected(true);
		jCheckBoxCopyToOutput.setText("Copy to output");
		
		jToolBarInput.add(jCheckBoxCopyToOutput);

		jCheckBoxWrapInput.setFont(new java.awt.Font("Dialog", 0, 10));
		jCheckBoxWrapInput.setSelected(true);
		jCheckBoxWrapInput.setText("Wrap");
		jCheckBoxWrapInput
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jTextAreaInput.setLineWrap(jCheckBoxWrapInput.isSelected());
					}
				});

		jToolBarInput.add(jCheckBoxWrapInput);

		jButtonBackup.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonBackup.setText("Backup");
		jButtonBackup.setToolTipText("Make a backup of the database.");
		jButtonBackup.setEnabled(true);
		jButtonBackup.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doBackup();
			}
		});

		jToolBarInput.add(new JSeparator());
		jToolBarInput.add(jButtonBackup);
		
		jPanelInput.add(jToolBarInput, java.awt.BorderLayout.NORTH);

		jPanelBottom.setLayout(new java.awt.BorderLayout());

		jButtonRun.setFont(new java.awt.Font("Dialog", 0, 10));
		jButtonRun.setText("Run (F5)");
		jButtonRun.setToolTipText("Execute code.");
		jButtonRun.setEnabled(false);
		jButtonRun.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				doRun();
			}
		});

		jPanelBottom.add(jButtonRun, java.awt.BorderLayout.CENTER);

		jLabelCaretPosition.setFont(new java.awt.Font("Dialog", 0, 10));
		jLabelCaretPosition.setBorder(new javax.swing.border.EtchedBorder());
		jLabelCaretPosition.setPreferredSize(new java.awt.Dimension(100, 0));
		jPanelBottom.add(jLabelCaretPosition, java.awt.BorderLayout.WEST);

		jLabelRunning.setFont(new java.awt.Font("Dialog", 0, 10));
		jLabelRunning.setBorder(new javax.swing.border.EtchedBorder());
		jLabelRunning.setPreferredSize(new java.awt.Dimension(100, 0));
		jPanelBottom.add(jLabelRunning, java.awt.BorderLayout.EAST);
		
		jPanelInput.add(jPanelBottom, java.awt.BorderLayout.SOUTH);

		jSplitPaneMain.setBottomComponent(jPanelInput);

		add(jSplitPaneMain, java.awt.BorderLayout.CENTER);

		jTextAreaOutputPlain.setToolTipText("Rel server responses are displayed here.");
		jTextAreaOutputPlain.setFont(Preferences.getInstance().getInputOutputFont());
		((JTextArea) jTextAreaOutputPlain).setText(textOut.toString());
		jTextAreaOutputFormatted.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				PanelCommandline.this.keyReleased(evt);
			}
		});
		Preferences.getInstance().addInputOutputFontChangeListener(new Preferences.InputOutputFontChangeListener() {
			public void fontChanged(Font font) {
				jTextAreaOutputPlain.setFont(Preferences.getInstance().getInputOutputFont());
			}
		});
		
		jTextAreaOutputFormatted = new JTextPane();
		jTextAreaOutputFormatted.setToolTipText("Rel server responses are displayed here.");				
		DBrowserStyle.setEnhancedOutputStyle((JTextPane)jTextAreaOutputFormatted);
		jTextAreaOutputFormatted.setDoubleBuffered(true);
		jTextAreaOutputFormatted.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				PanelCommandline.this.keyReleased(evt);
			}
		});
		Preferences.getInstance().addInputOutputFontChangeListener(new Preferences.InputOutputFontChangeListener() {
			public void fontChanged(Font font) {
				DBrowserStyle.setEnhancedOutputStyle((JTextPane)jTextAreaOutputFormatted);
				jTextAreaOutputFormatted.setText(formattedOut.toString());
			}
		});
	}

	private void keyReleased(java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_F5)
			doRun();
	}

	private boolean isEnhancedOutput() {
		return (jTextAreaOutput instanceof JTextPane);
	}

	public void save() {
		jTextAreaOutput.setFont(Preferences.getInstance().getInputOutputFont());
		jTextAreaInput.setFont(Preferences.getInstance().getInputOutputFont());
	}
	
	private void setEnhancedOutput(boolean flag) {
		if (jTextAreaOutput != null && flag == isEnhancedOutput())
			return;
		jCheckBoxShowHeadings.setVisible(flag);
		jCheckBoxShowHeadingTypes.setVisible(flag && jCheckBoxShowHeadings.isSelected());
		jCheckBoxWrapOutput.setVisible(!flag);
		if (flag) {
			jTextAreaOutputFormatted.setText(formattedOut.toString());
			jScrollPaneOutput.setViewportView(jTextAreaOutputFormatted);
			jTextAreaOutput = jTextAreaOutputFormatted;
		} else {			
			((JTextArea) jTextAreaOutputPlain).setLineWrap(jCheckBoxWrapOutput.isSelected());
			jTextAreaOutputPlain.setText(textOut.toString());
			jScrollPaneOutput.setViewportView(jTextAreaOutputPlain);
			jTextAreaOutput = jTextAreaOutputPlain;
		}
		startOutputUpdateTimers();
	}
	
	private void saveOutput(final String text, final JFileChooser chooser) {
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (chooser.getSelectedFile().isFile())
				if (JOptionPane.showConfirmDialog(this, "File "
						+ chooser.getSelectedFile()
						+ " already exists.  Overwrite it?") != JOptionPane.YES_OPTION)
					return;
			startProcessingDisplay();
			setProcessingDisplay("Saving");
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					try {
						BufferedWriter f = new BufferedWriter(new FileWriter(chooser.getSelectedFile()));
						f.write(text);
						f.close();
						systemResponse("Saved " + chooser.getSelectedFile());
					} catch (IOException ioe) {
						badResponse(ioe.toString());
					}
					endProcessingDisplay();
					return null;
				}
			}).execute();
		}		
	}
	
	private void saveOutputText() {
		saveOutput(textOut.toString(), jFileChooserSaveOutputText);
	}

	private void saveOutputFormatted() {
		BufferedReader htmlStreamed = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(jTextAreaOutput.getText().getBytes())));
		StringBuffer output = new StringBuffer();
		String line;
		try {
			while ((line = htmlStreamed.readLine()) != null) {
				if (line.trim().equalsIgnoreCase("<head>")) {
					output.append("<head>\n");
					output.append("<style type=\"text/css\">\n");
					output.append("<!--\n");
					for (String entry: DBrowserStyle.getFormattedStyle()) {
						output.append(entry);
						output.append('\n');
					}
					output.append("-->\n");
					output.append("</style>\n");
					output.append("</head>\n");
				} else if (!line.trim().equalsIgnoreCase("</head>")) {
					output.append(line);
					output.append('\n');
				}
			}
			saveOutput(output.toString(), jFileChooserSaveOutputFormatted);
		} catch (IOException e) {
			System.err.println("This cannot possibly have happened.  The universe has collapsed.");
		}
	}

	private void copyOutputToInput() {
		jTextAreaInput.setText(textOut.toString());
		jTextAreaInput.requestFocus();
	}

	private void clearOutput() {
		formattedOut = new StringBuffer();
		textOut = new StringBuffer();
		lastText = "";
		if (!isEnhancedOutput())
			jTextAreaOutput.setText("");
		updateOutput();
	}

	private void clearInput() {
		jTextAreaInput.setText("");
		jTextAreaInput.requestFocus();
	}

	private void saveCommandHistory() {
		if (jFileChooserSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (jFileChooserSave.getSelectedFile().isFile())
				if (JOptionPane.showConfirmDialog(this, "File "
						+ jFileChooserSave.getSelectedFile()
						+ " already exists.  Overwrite it?") != JOptionPane.YES_OPTION)
					return;
			startProcessingDisplay();
			setProcessingDisplay("Saving");
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					try {
						BufferedWriter f = new BufferedWriter(new FileWriter(jFileChooserSave.getSelectedFile()));
						for (int i = 0; i < getHistorySize(); i++) {
							f.write("// History item #" + (i + 1) + "\n");
							f.write(getHistoryItemAt(i));
							f.write("\n\n");
						}
						f.write("// Current entry" + "\n");
						f.write(jTextAreaInput.getText());
						f.close();
						systemResponse("Saved " + jFileChooserSave.getSelectedFile());
						jTextAreaInput.requestFocusInWindow();
					} catch (IOException ioe) {
						badResponse(ioe.toString());
					}
					endProcessingDisplay();
					return null;
				}
			}).execute();
		}
	}

	private void saveCommandCurrent() {
		if (jFileChooserSave.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (jFileChooserSave.getSelectedFile().isFile())
				if (JOptionPane.showConfirmDialog(this, "File "
						+ jFileChooserSave.getSelectedFile()
						+ " already exists.  Overwrite it?") != JOptionPane.YES_OPTION)
					return;
			startProcessingDisplay();
			setProcessingDisplay("Saving");
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					try {
						BufferedWriter f = new BufferedWriter(new FileWriter(
								jFileChooserSave.getSelectedFile()));
						f.write(jTextAreaInput.getText());
						f.close();
						systemResponse("Saved " + jFileChooserSave.getSelectedFile());
						jTextAreaInput.requestFocusInWindow();
					} catch (IOException ioe) {
						badResponse(ioe.toString());
					}
					endProcessingDisplay();
					return null;
				}
			}).execute();
		}
	}

	private void getPath() {
		if (jFileChooserGetPath.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					jTextAreaInput.append("\"" + jFileChooserGetPath.getSelectedFile().getAbsolutePath() + "\"");
					jTextAreaInput.setCaretPosition(jTextAreaInput.getText().length());
					jTextAreaInput.requestFocusInWindow();
					endProcessingDisplay();
					return null;
				}
			}).execute();
		}
	}
	
	private void loadCommand() {
		if (jFileChooserLoad.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			startProcessingDisplay();
			setProcessingDisplay("Loading");
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					try {
						BufferedReader f = new BufferedReader(new FileReader(
								jFileChooserLoad.getSelectedFile()));
						StringBuffer fileImage = new StringBuffer();
						String line;
						while ((line = f.readLine()) != null) {
							fileImage.append(line);
							fileImage.append('\n');
						}
						f.close();
						jTextAreaInput.setText(fileImage.toString());
						jFileChooserSave.setSelectedFile(jFileChooserLoad
								.getSelectedFile());
						systemResponse("Loaded " + jFileChooserLoad.getSelectedFile());
						jTextAreaInput.requestFocusInWindow();
					} catch (IOException ioe) {
						badResponse(ioe.toString());
					}
					endProcessingDisplay();
					return null;
				}
			}).execute();
		}
	}

	private void nextCommand() {
		jTextAreaInput.setText(getNextHistoryItem());
		jTextAreaInput.selectAll();
		jTextAreaInput.requestFocusInWindow();
	}

	private void previousCommand() {
		jTextAreaInput.setText(getPreviousHistoryItem());
		jTextAreaInput.selectAll();
		jTextAreaInput.requestFocusInWindow();
	}

	/** Set display to quick mode and start output timers. */
	private void outputUpdated() {
		updateOutput();
		startOutputUpdateTimers();
	}
	
	private void outputHTML(String s) {
		formattedOut.append(s);
	}
	
	/**************** Bits from VisualiserOfRel used to display HTML ****************
	
	private JTextPane getDisplayForTuples() {		
		final JTextPane pane = new JTextPane();
		pane.setToolTipText("Rel server responses are displayed here.");				
		pane.setDoubleBuffered(true);
		pane.setContentType("text/html");
		return pane;
	}

	private static class ElementHolder {
		private Element element = null;
		public void setElement(Element e) {
			element = e;
		}
		public Element getElement() {
			return element;
		}
	}
	
	private void evaluate(String query, Connection.HTMLReceiver receiver) {
		//session.getConnection().evaluate(query, receiver, new CrashTrap(query, connection.getServerAnnouncement(), Version.getVersion()));		
	}
	
	private void evaluateAndDisplay(String query) {
		final JTextPane display = getDisplayForTuples();
		final ElementHolder table = new ElementHolder();
		final HTMLDocument document = (HTMLDocument)display.getDocument();
		evaluate(query, new Connection.HTMLReceiver() {
			String initialHTML = "";
			String progressiveHTML = "";
			public void emitInitialHTML(String s) {
				initialHTML += s;
			}
			public void endInitialHTML() {
				display.setText(initialHTML);
				Element element = document.getElement("table");
				table.setElement(element);
			}
			public void emitProgressiveHTML(String s) {
				progressiveHTML += s;
			}
			public void endProgressiveHTMLRow() {
				try {
					document.insertBeforeEnd(table.getElement(), progressiveHTML);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					progressiveHTML = "";
				}
			}
		});		
	}

	***************************/
	
	/** Record formatted responses. */
	private void responseFormatted(String s, boolean parseResponse) {
		if (parseResponse) {
			try {
				setProcessingDisplay("Formatting");
				ResponseToHTML response = new ResponseToHTML(s) {
					public void emitHTML(String generatedHTML) {
						outputHTML(generatedHTML);
					}
					public boolean isEmitHeading() {
						return isShowHeadings;
					}
					public boolean isEmitHeadingTypes() {
						return isShowHeadingTypes;
					}
				};
				response.parse();
			} catch (ParseException pe) {
				outputHTML("<br>" + ResponseToHTML.textToHTML(s));
			}
		} else {
			outputHTML("<br>" + ResponseToHTML.textToHTML(s).replace(" ", "&nbsp;"));
		}
	}

	/** Record text responses. */
	private void responseText(String s) {
		lastText = s + '\n';
		textOut.append(s);
		textOut.append('\n');
	}

	/** Handle a received line of 'good' content. */
	private void goodResponse(String s) {
		formattedOut.append("<font color=\"green\">");
		responseFormatted(s, false);
		formattedOut.append("</font>");
		responseText(s);
		outputUpdated();
	}

	/** Handle user entry. */
	private void userResponse(String s) {
		formattedOut.append("<br><font color=\"gray\"><b>");
		responseFormatted(s, false);
		formattedOut.append("</b></font>");
		responseText(s);
		outputUpdated();
	}

	/** Handle a received line of system notice. */
	private void systemResponse(String s) {
		formattedOut.append("<font color=\"blue\">");
		responseFormatted(s, false);
		formattedOut.append("</font>");
		responseText(s);
		outputUpdated();
	}

	/** Handle a received line of 'bad' content. */
	void badResponse(String s) {
		formattedOut.append("<pre><font color=\"red\"><b>");
		responseFormatted(s, false);
		formattedOut.append("</b></pre></font>");
		responseText(s);
		outputUpdated();
	}
		
	/** Handle a notice. */
	private void noticeResponse(String s) {
		formattedOut.append("<font color=\"black\"><b>");
		responseFormatted(s, false);
		formattedOut.append("</b></font>");
		responseText(s);
		outputUpdated();		
	}
	
	/** Handle received data. */
	private void response(String s, boolean interpretResponse) {
		responseFormatted(s, interpretResponse);
		responseText(s);
		outputUpdated();
	}

	// Handle received response.  Return null if ok, or return error location information
	private StringBuffer obtainClientResponse(boolean responseFormatted, StringBuffer captureBuffer) {
		StringBuffer reply = new StringBuffer();
		StringBuffer errorBuffer = null;
		try {
			String r;
			while ((r = session.getClient().receive()) != null) {
				if (r.equals("\n")) {
					continue;
				} else if (r.equals("Ok.")) {
					String content = reply.toString();
					response(content, false);
					if (!jCheckBoxSuppressOk.isSelected())
						goodResponse(r);
					Splash.hideSplash();
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
			disableInput();
		}
		return errorBuffer;
	}

	private void updateProgress() {
		Runnable runner = new Runnable() {
			public void run() {
				if (jLabelRunning.getText().endsWith("..."))
					jLabelRunning.setText(jLabelRunning.getText().replace("...", "."));
				else if (jLabelRunning.getText().endsWith("."))
					jLabelRunning.setText(jLabelRunning.getText().concat("."));
			}			
		};
		if (javax.swing.SwingUtilities.isEventDispatchThread())
			runner.run();
		else
			javax.swing.SwingUtilities.invokeLater(runner);
	}
	
	private int processingDisplayCount = 0;
	
	private void startProcessingDisplay() {
		if (processingDisplayCount++ > 0)
			return;
		if (!progressTimer.isRunning())
			progressTimer.start();
		jLabelRunning.setText("Processing...");
	}
	
	private void setProcessingDisplay(final String text) {
		jLabelRunning.setText(text + "...");
	}
	
	private void endProcessingDisplay() {
		if (--processingDisplayCount > 0)
			return;
		else if (processingDisplayCount < 0)
			processingDisplayCount = 0;
		if (progressTimer.isRunning())
			progressTimer.stop();
		jLabelRunning.setText("");
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
	
	private void doRun() {
		if (!isInputEnabled())
			return;
		startProcessingDisplay();
		disableInput();
		if (jCheckBoxAutoclear.isSelected())
			clearOutput();
		addHistoryItem(jTextAreaInput.getText());
		if (jCheckBoxCopyToOutput.isSelected())
			userResponse(jTextAreaInput.getText());
		String runMe = jTextAreaInput.getText().trim();
		SwingWorker<StringBuffer, StringBuffer> worker = new javax.swing.SwingWorker<StringBuffer, StringBuffer>() {
			public StringBuffer doInBackground() {
				StringBuffer errorInformationBuffer = null;
				try {
					if (isLastNonWhitespaceCharacter(runMe, ';')) {
						session.getClient().sendExecute(runMe, new CrashTrap(runMe, serverInitialResponse.toString(), Version.getVersion()));
						setProcessingDisplay("Receiving");
						errorInformationBuffer = obtainClientResponse(false, null);
					} else {
						session.getClient().sendEvaluate(runMe, new CrashTrap(runMe, serverInitialResponse.toString(), Version.getVersion()));
						setProcessingDisplay("Receiving");
						errorInformationBuffer = obtainClientResponse(true, null);
					}
					enableInput();
					if (errorInformationBuffer != null) {
						ErrorInformation eInfo = parseErrorInformationFrom(errorInformationBuffer.toString());
						if (eInfo != null) {
							int startOffset = 0;
							try {
								if (eInfo.getLine() > 0) {
									startOffset = jTextAreaInput.getLineStartOffset(eInfo.getLine() - 1);
									if (eInfo.getColumn() > 0)
										startOffset += eInfo.getColumn() - 1;
								}
								jTextAreaInput.setCaretPosition(startOffset);
								if (eInfo.getBadToken() != null)
									jTextAreaInput.moveCaretPosition(startOffset + eInfo.getBadToken().length());
							} catch (BadLocationException e) {
								System.out.println("PanelCommandline: Unable to position to line " + eInfo.getLine() + ", column " + eInfo.getColumn());
							}
						} else
							System.out.println("PanelCommandline: Unable to locate error in " + errorInformationBuffer.toString());
					} else {
						jTextAreaInput.selectAll();
					}
					jTextAreaInput.requestFocusInWindow();
					endProcessingDisplay();					
				} catch (Throwable ioe) {
					badResponse(ioe.getMessage());
				}
				return null;
			}
		};
		worker.execute();
	}
	
	public void doBackup() {
		Backup backup = new Backup();
		jFileChooserSaveBackup.setSelectedFile(new File(backup.getSuggestedBackupFileName(session.getDbURL())));
		if (jFileChooserSaveBackup.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			if (jFileChooserSaveBackup.getSelectedFile().isFile())
				if (JOptionPane.showConfirmDialog(this, "File "
						+ jFileChooserSaveBackup.getSelectedFile()
						+ " already exists.  Overwrite it?") != JOptionPane.YES_OPTION)
					return;
			startProcessingDisplay();
			setProcessingDisplay("Saving");
			(new javax.swing.SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					BackupResponse response = backup.backupToFile(session.getDbURL(), jFileChooserSaveBackup.getSelectedFile());
					endProcessingDisplay();
					if (response.isSuccessful())
						systemResponse("Saved backup " + jFileChooserSaveBackup.getSelectedFile());
					else
						systemResponse("Backup failed: " + response.getMessage());
					response.showMessage();
					return null;
				}
			}).execute();
		}
	}
	
	private JButton jButtonClearInput;
	private JButton jButtonClearOutput;
	private JButton jButtonCopyToInput;
	private JButton jButtonLoadCommand;
	private JButton jButtonGetPath;
	private JButton jButtonNextCommand;
	private JButton jButtonPreviousCommand;
	private JButton jButtonRun;
	private JButton jButtonSaveCurrent;
	private JButton jButtonSaveHistory;
	private JButton jButtonSaveOutputText;
	private JButton jButtonSaveOutputFormatted;
	private JButton jButtonBackup;
	private JCheckBox jCheckBoxAutoclear;
	private JCheckBox jCheckBoxCopyToOutput;
	private JCheckBox jCheckBoxEnhanced;
	private JCheckBox jCheckBoxSuppressOk;
	private JCheckBox jCheckBoxWrapInput;
	private JCheckBox jCheckBoxWrapOutput;
	private JCheckBox jCheckBoxShowHeadings;
	private JCheckBox jCheckBoxShowHeadingTypes;
	private JFileChooser jFileChooserGetPath;
	private JFileChooser jFileChooserLoad;
	private JFileChooser jFileChooserSave;
	private JFileChooser jFileChooserSaveOutputText;
	private JFileChooser jFileChooserSaveOutputFormatted;
	private JFileChooser jFileChooserSaveBackup;
	private JLabel jLabelCaretPosition;
	private JLabel jLabelRunning;
	private JPanel jPanelBottom;
	private JPanel jPanelInput;
	private JPanel jPanelOutput;
	private JScrollPane jScrollPaneInput;
	private JScrollPane jScrollPaneOutput;
	private JSplitPane jSplitPaneMain;
	private JTextArea jTextAreaInput;
	private JToolBar jToolBarInput;
	private JToolBar jToolBarOutput;
	private JTextPane jTextAreaOutputFormatted;
	private JTextArea jTextAreaOutputPlain;
}
