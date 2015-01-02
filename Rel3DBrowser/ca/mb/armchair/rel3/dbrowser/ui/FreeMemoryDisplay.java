package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Deque;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FreeMemoryDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel jLabelMemory;
	private JLabel jLabelShim1;
	private JLabel jLabelShim2;
	private Deque<Integer> percentageHistory;

	private Timer memoryCheckTimer;
	
	private int middleLimit = 50;
	private int lowerLimit = 20;
	
	private Color goodColor;
	private Color okColor;
	private Color badColor;

    /** Get memory free as a percentage value between 0 and 100. */
    public static int getMemoryPercentageFreeClassic() {
    	java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
    	long maxmemory = runtime.totalMemory();
    	int memfree = (int)((double)runtime.freeMemory() / (double)maxmemory * 100.0);
    	return memfree;
    }
	
    /** Get memory free as a percentage value between 0 and 100. */
	public static int getMemoryPercentageFree() {
    	java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long estimatedFree = freeMemory + (maxMemory - allocatedMemory);
    	int memfree = (int)((double)estimatedFree / (double)maxMemory * 100.0);
    	return memfree;
    }
	
	public void setMiddleLimit(int middleLimit) {
		if (this.middleLimit < 0 || this.middleLimit > 100)
			throw new IllegalArgumentException("MiddleLimit must be between 0 and 100, inclusive.");
		this.middleLimit = middleLimit;
    	repaint();
	}
	
	public int getMiddleLimit() {
		return middleLimit;
	}
	
	public void setLowerLimit(int lowerLimit) {
		if (this.lowerLimit < 0 || this.lowerLimit > 100)
			throw new IllegalArgumentException("LowerLimit must be between 0 and 100, inclusive.");
		this.lowerLimit = lowerLimit;
    	repaint();
	}
	
	public int getLowerLimit() {
		return lowerLimit;
	}
	
	public void setDelay(int milliseconds) {
		memoryCheckTimer.setDelay(milliseconds);
	}
	
	public int getDelay() {
		return memoryCheckTimer.getDelay();
	}
	
	public void setFont(Font f) {
		super.setFont(f);
		if (jLabelMemory != null)
			jLabelMemory.setFont(f);
		if (jLabelShim1 != null)
			jLabelShim1.setFont(f);
		if (jLabelShim2 != null)
			jLabelShim2.setFont(f);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();
		g.setColor(getBackground());
		g.fillRect(0, 0, d.width, d.height);
		Integer[] percentages = percentageHistory.toArray(new Integer[0]);
		int lastX = 0;
		int lastY = 0;
		for (int i=0; i<percentages.length; i++) {
			if (percentages[i] < lowerLimit)
				g.setColor(badColor);
			else if (percentages[i] < middleLimit)
				g.setColor(okColor);
			else
				g.setColor(goodColor);
			int bartop = (100 - percentages[i]) * d.height / 100 + 2;
			g.drawLine(i, d.height, i, bartop);
			int dontDrawLineBelow = jLabelMemory.getY();
			if (lastY < dontDrawLineBelow && bartop < dontDrawLineBelow)
				g.setColor(Color.BLACK);
			else
				g.setColor(Color.LIGHT_GRAY);
			g.drawLine(lastX, lastY, i, bartop);
			lastX = i;
			lastY = bartop;
		}
    }
	
	// Force color value between 0 and 255.
	private static int cr(int x) {
		return Math.max(Math.min(x, 255), 0);
	}
	
	// Make a Color redder
	private static Color redder(Color c) {
		return new Color(cr(c.getRed() + 30), cr(c.getGreen() - 30), cr(c.getBlue() - 30));
	}
	
	// Make a color yellower
	private static Color yellower(Color c) {
		return new Color(cr(c.getRed() + 30), cr(c.getGreen() + 30), cr(c.getBlue() - 30));
	}
	
	// Make a Color greener
	private static Color greener(Color c) {
		return new Color(cr(c.getRed() - 30), cr(c.getGreen() + 30), cr(c.getBlue() - 30));		
	}
	
	public FreeMemoryDisplay() {
		super();
		MouseListener clickListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setPreferredSize(FreeMemoryDisplay.this.getSize());
				jLabelMemory.setVisible(!jLabelMemory.isVisible());				
			}
		};
		this.addMouseListener(clickListener);
		setLayout(new BorderLayout());
		goodColor = greener(getBackground());
		okColor = yellower(getBackground());
		badColor = redder(getBackground());
		percentageHistory = new LinkedList<Integer>();
		jLabelMemory = new JLabel();
		jLabelMemory.setBorder(null);
		jLabelMemory.setOpaque(false);
		jLabelMemory.addMouseListener(clickListener);
		jLabelShim1 = new JLabel(" ");
		jLabelShim1.addMouseListener(clickListener);
		jLabelShim2 = new JLabel(" ");
		jLabelShim2.addMouseListener(clickListener);
		add(jLabelShim1, BorderLayout.NORTH);
		add(jLabelShim2, BorderLayout.EAST);
		add(jLabelMemory, BorderLayout.CENTER);
        memoryCheckTimer = new Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int memoryFree = getMemoryPercentageFree(); 
				percentageHistory.add(memoryFree);
				while (percentageHistory.size() > FreeMemoryDisplay.this.getWidth())
					percentageHistory.removeFirst();
				jLabelMemory.setText(memoryFree + "% memory free");
		    	FreeMemoryDisplay.this.repaint();
			}
		});
		memoryCheckTimer.setRepeats(true);
		memoryCheckTimer.start();
	}

}
