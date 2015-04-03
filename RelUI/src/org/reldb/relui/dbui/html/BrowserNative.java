package org.reldb.relui.dbui.html;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BrowserNative implements HtmlBrowser {

	private Browser browser;
	private Style style;
	private StringBuffer text = new StringBuffer();
	
	private static class Message {
		public String s;
		private boolean scroll = false;
		public Message(String s) {this.s = s;}
		public Message() {this.s = null;}
		public Message(boolean scroll) {this.scroll = scroll; this.s = null;}
		public boolean isNull() {return this.s == null;}
		public boolean isScroll() {return scroll;}
	}
	
	private BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
	
	private static String cleanForJavascriptInsertion(String s) {
		return s.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"").replace("\n", "");
	}
	
	@Override
	public boolean createWidget(Composite parent, Font font) {
		if (Util.isMac())
			style = new Style(font, -3);
		else
			style = new Style(font, 0);
		try {
			browser = new Browser(parent, SWT.BORDER);
			browser.setJavascriptEnabled(true);
			browser.addProgressListener(new ProgressAdapter() {
				@Override
				public void completed(ProgressEvent event) {
					pumpQueue();
				}
			});
		} catch (Throwable t) {
			System.out.println("BrowserNative: Native browser not available: " + t);
			return false;
		}
		clear();
		return true;
	}

	private boolean busy = false;
	
	private synchronized void pumpQueue() {
		busy = false;
		while (!messageQueue.isEmpty()) {
			Message message = messageQueue.poll();
			if (message.isScroll())
				scrollToBottom();
			else if (message.isNull())
				clear();
			else
				appendHtml(message.s);
		}
	}

	private synchronized boolean enqueueClear() {
		if (busy) {
			try {
				messageQueue.put(new Message());
			} catch (InterruptedException e) {
			}
			return true;
		}
		busy = true;
		return false;
	}
	
	private synchronized boolean enqueue(Message m) {
		if (busy) {
			try {
				messageQueue.put(m);
			} catch (InterruptedException e) {
			}
			return true;
		}
		return false;		
	}
	
	@Override
	public void clear() {
		if (enqueueClear())
			return;
		text = new StringBuffer();
		browser.setText(style.getEmptyHTMLDocument());
	}
	
	@Override
	public void appendHtml(String s) {
		if (enqueue(new Message(s)))
			return;
		browser.execute("document.getElementsByTagName('body')[0].innerHTML += '" + cleanForJavascriptInsertion(s) + "'");
		text.append(s);		
	}

	@Override
	public void scrollToBottom() {
		if (enqueue(new Message(true)))
			return;
		browser.execute("window.scrollTo(0, document.body.scrollHeight)");
	}

	@Override
	public Control getWidget() {
		return browser;
	}

	@Override
	public String getText() {
		return style.getHTMLDocument(text.toString());
	}

	@Override
	public String getSelectedText() {
		return null;
	}

	@Override
	public boolean isSelectedTextSupported() {
		return false;
	}

	@Override
	public Style getStyle() {
		return style;
	}

}
