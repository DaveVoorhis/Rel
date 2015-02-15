package org.reldb.relui.monitors;

import java.util.Deque;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class PercentDisplay extends org.eclipse.swt.widgets.Canvas {
	private Deque<Integer> percentageHistory;
	
	private int middleLimit = 50;
	private int lowerLimit = 20;
	
	private Color goodColor;
	private Color okColor;
	private Color badColor;
	private Color black;
	private Color lightGray;
	
	private int delay = 500;
	private boolean running = true;
	
	private int displayWidth;
	private String emitText = "";

	private final Runnable refresh = new Runnable() {
		public void run() {
			if (!isDisposed())
				redraw();	
		}
	};
	
	private void refresh() {
		if (isDisposed())
			return;
		getDisplay().asyncExec(refresh);
	}
	
	public void setMiddleLimit(int middleLimit) {
		if (this.middleLimit < 0 || this.middleLimit > 100)
			throw new IllegalArgumentException("MiddleLimit must be between 0 and 100, inclusive.");
		this.middleLimit = middleLimit;
		refresh();
	}
	
	public int getMiddleLimit() {
		return middleLimit;
	}
	
	public void setLowerLimit(int lowerLimit) {
		if (this.lowerLimit < 0 || this.lowerLimit > 100)
			throw new IllegalArgumentException("LowerLimit must be between 0 and 100, inclusive.");
		this.lowerLimit = lowerLimit;
		refresh();
	}
	
	public int getLowerLimit() {
		return lowerLimit;
	}

	public void setDelay(int milliseconds) {
		delay = milliseconds;
	}
	
	public int getDelay() {
		return delay;
	}
		
	public void dispose() {
		running = false;
		goodColor.dispose();
		badColor.dispose();
		okColor.dispose();
		black.dispose();
		lightGray.dispose();
		super.dispose();
	}
	
	// Force color value between 0 and 255.
	private static int cr(int x) {
		return Math.max(Math.min(x, 255), 0);
	}
	
	// Make a Color redder
	private static Color redder(Color c) {
		return new Color(c.getDevice(), cr(c.getRed() + 30), cr(c.getGreen() - 30), cr(c.getBlue() - 30));
	}
	
	// Make a color yellower
	private static Color yellower(Color c) {
		return new Color(c.getDevice(), cr(c.getRed() + 30), cr(c.getGreen() + 30), cr(c.getBlue() - 30));
	}
	
	// Make a Color greener
	private static Color greener(Color c) {
		return new Color(c.getDevice(), cr(c.getRed() - 30), cr(c.getGreen() + 30), cr(c.getBlue() - 30));		
	}
	
	private synchronized Integer[] getPercentages() {
		return percentageHistory.toArray(new Integer[0]);
	}
	
	private synchronized void addPercentage(int percentValue) {
		percentageHistory.add(percentValue);
		while (percentageHistory.size() > displayWidth)
			percentageHistory.removeFirst();
	}
	
	public PercentDisplay(Composite parent, int style, String displaytext, PercentSource percent) {
		super(parent, style);
		
		percentageHistory = new LinkedList<Integer>();
		
		goodColor = greener(getBackground());
		badColor = redder(getBackground());
		okColor = yellower(getBackground());
		black = new Color(parent.getDisplay(), 0, 0, 0);
		lightGray = new Color(parent.getDisplay(), 200, 200, 200);
		
		addListener (SWT.Paint, new Listener () {
			@Override
			public void handleEvent (Event e) {
				GC gc = e.gc;
				Rectangle rect = getClientArea();
				displayWidth = rect.width;
				Integer[] percentages = getPercentages();
				int lastX = rect.x;
				int lastY = rect.y;
				for (int index=0; index<percentages.length; index++) {
					int barY = (100 - percentages[index]) * rect.height / 100 + 2;
					int barX = rect.x + index;
					gc.setForeground(getBackground());
					gc.drawLine(barX, rect.y, barX, barY);
					if (percentages[index] < lowerLimit)
						gc.setForeground(badColor);
					else if (percentages[index] < middleLimit)
						gc.setForeground(okColor);
					else
						gc.setForeground(goodColor);
					gc.drawLine(barX, rect.height, barX, barY);
					if (index > 0) {
						int dontDrawLineBelow = 5;
						if (lastY < dontDrawLineBelow && barY < dontDrawLineBelow)
							gc.setForeground(black);
						else
							gc.setForeground(lightGray);
						gc.drawLine(lastX, lastY, barX, barY);
					}
					lastX = barX;
					lastY = barY;
				}
				gc.setForeground(black);
				gc.drawText(emitText, 2, 5, true);
			}
		});
		
		Thread painter = new Thread() {
			public void run() {
				while (running) {
					try {sleep(delay);} catch (InterruptedException ie) {}
					int percentValue = percent.getPercent(); 
					addPercentage(percentValue);
					emitText = String.format("%3d%% ", percentValue) + displaytext; 
					refresh();
				}
			}
		};
		painter.start();
	}

}
