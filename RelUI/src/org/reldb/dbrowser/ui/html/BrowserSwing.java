package org.reldb.dbrowser.ui.html;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.ResourceManager;

public class BrowserSwing implements HtmlBrowser {

	private BrowserSwingWidget browserPanel;
	private Frame frame;
	private JTextPane browser;
	private Style style;
	private StringBuffer text;

	private void setEnhancedOutputStyle(JTextPane pane) {
		pane.setContentType("text/html");
		pane.setEditable(false);
		pane.setEnabled(true);
		HTMLEditorKit editorKit = new HTMLEditorKit();
		HTMLDocument defaultDocument = (HTMLDocument) editorKit.createDefaultDocument();
		pane.setEditorKit(editorKit);
		pane.setDocument(defaultDocument);
		StyleSheet css = editorKit.getStyleSheet();
		for (String entry : style.getFormattedStyle())
			css.addRule(entry);
	}

	private int zoom;
	
	private void setZoomFactor(int zoom) {
		this.zoom = zoom;
	}
	
	private int getZoom() {
		final ImageDataProvider imageDataProvider = zoom -> {
			setZoomFactor(zoom);
			return ResourceManager.getPluginImage("RelUI", "icons/noimage.png").getImageData();
		};
		new Image(Display.getCurrent(), imageDataProvider);
		return zoom;
	}
	
	@Override
	public boolean createWidget(Composite parent) {
		browserPanel = new BrowserSwingWidget(parent);		
		frame = SWT_AWT.new_Frame(browserPanel);
		
		// seize focus
		browserPanel.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						browser.requestFocus();
					}
				});
			}
		});
		
		if (Util.isWin32())
			style = new Style(3, (double)getZoom() / 100.0);
		else
			style = new Style(0);

		Panel root = new Panel();
		root.setLayout(new GridLayout());
		
		browser = new JTextPane();
		KeyListener[] listeners = browser.getKeyListeners();
		for (KeyListener listener: listeners)
			browser.removeKeyListener(listener);
		browser.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!(e.getKeyCode() == KeyEvent.VK_C))
					return;
				if (!(e.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
					return;
				e.consume();
				browserPanel.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						browserPanel.copy();
					}
				});
			}
		});
		
		browserPanel.setJTextPane(browser);
		
		setEnhancedOutputStyle(browser);
		browser.setDoubleBuffered(true);
		DefaultCaret caret = (DefaultCaret) browser.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane jScrollPaneOutput = new JScrollPane();
		jScrollPaneOutput.setAutoscrolls(true);
		jScrollPaneOutput.setViewportView(browser);

		root.add(jScrollPaneOutput);
		
		frame.add(root);

		clear();

		return true;
	}

	@Override
	public boolean setFocus() {
		return (browserPanel.isDisposed()) ? false : browserPanel.setFocus();
	}
	
	@Override
	public void clear() {
		text = new StringBuffer();
		browser.setText(style.getEmptyHTMLDocument());
	}

	@Override
	public void appendHtml(String s) {
		HTMLDocument doc = (HTMLDocument) browser.getDocument();
		HTMLEditorKit kit = (HTMLEditorKit) browser.getEditorKit();
		try {
			kit.insertHTML((HTMLDocument) doc, doc.getLength(), s, 0, 0, null);
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
		text.append(s);
	}

	@Override
	public void scrollToBottom() {
	}

	@Override
	public Control getWidget() {
		return browserPanel;
	}

	@Override
	public String getText() {
		return style.getHTMLDocument(text.toString());
	}

	@Override
	public String getSelectedText() {
		return browser.getSelectedText();
	}

	@Override
	public boolean isSelectedTextSupported() {
		return true;
	}

	@Override
	public Style getStyle() {
		return style;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setContent(String content) {
		text = new StringBuffer(content);
		browser.setText(style.getHTMLDocument(content));
	}

	@Override
	public String getContent() {
		return text.toString();
	}

}
