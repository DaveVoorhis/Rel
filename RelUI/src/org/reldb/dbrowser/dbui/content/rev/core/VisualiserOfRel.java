package org.reldb.dbrowser.dbui.content.rev.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Argument;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

/** Visualiser of anything that produces a relation. */
public abstract class VisualiserOfRel extends Visualiser {
	
	private static final long serialVersionUID = 1L;
	
    private final String InvokeIconFile = "org/reldb/rel/resources/PlayIconTiny.png";
    private final String EditorIconFile = "org/reldb/rel/resources/EditIconTiny.png";
    private javax.swing.ImageIcon InvokeIcon = null;
    private javax.swing.ImageIcon EditorIcon = null;
    private final int ButtonWidth = 18;
    private final int ButtonHeight = 18;
    private javax.swing.JButton jButtonInvoke;
    private javax.swing.JButton jButtonEdit;
    private Rev rev;

	public VisualiserOfRel(Rev rev, String name) {
		super(rev.getModel());
		this.rev = rev;
		setName(name);
		setLabel();
	}
    
	boolean recursionGate = false;
	
	public void refresh() {
		if (recursionGate)
			return;
		recursionGate = true;
		updateVisualiser();
		for (int i=0; i<getArgumentCount(); i++) {
			Argument arg = getArgument(i);			
			Parameter p = arg.getConnector();
			if (p == null)
				continue;
			for (int j=0; j<p.getConnectionCount(); j++) {
				Visualiser v = p.getVisualiser();
				if (v != null && v instanceof VisualiserOfRel)
					((VisualiserOfRel)v).refresh();
			}
			arg.pulse();
		}
		recursionGate = false;
	}
	
	public Rev getRev() {
		return rev;
	}

	/** Evaluate and return Tuples. */
	public Tuples evaluate(String query) {
		return DatabaseAbstractionLayer.evaluate(getRev().getConnection(), query);
	}
	
	/** Evaluate and emit to Connection.HTMLReceiver. */
	public void evaluate(String query, Connection.HTMLReceiver htmlReceiver) {
		DatabaseAbstractionLayer.evaluate(getRev().getConnection(), query, htmlReceiver);
	}
	
    /** Return true if a given visualiser can be dropped on this one, with something
     * good possibly taking place thereafter via a receiveDrop() operation. */
    public boolean isDropCandidateFor(Visualiser draggedVisualiser) {
    	if (draggedVisualiser.getArgumentCount() > 0 && draggedVisualiser.getArgument(0).getConnector().getVisualiser() == this)
    		return false;
    	if (draggedVisualiser instanceof VisualiserOfOperand) {
    		return true;
    	}
    	if (draggedVisualiser instanceof VisualiserOfView) {
    		return true;
    	}
    	return false;
    }
	
	private JTextPane getDisplayForTuples() {		
		final JTextPane pane = new JTextPane();
		pane.setToolTipText("Rel server responses are displayed here.");				
		pane.setDoubleBuffered(true);
		pane.setContentType("text/html");
		return pane;
	}
	
	protected abstract String getQuery();

	private static class ElementHolder {
		private Element element = null;
		public void setElement(Element e) {
			element = e;
		}
		public Element getElement() {
			return element;
		}
	}

	public void evaluateAndDisplay(String query) {
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
			getRev().setDetailView(new JScrollPane(display));
	}
	
	protected void invokeLeft() {
		evaluateAndDisplay(getQuery());
	}
    
    protected void invokeRight() {
		getRev().createTuplesVisualiser(getQuery(), getName());
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
    	addInvokeButton();
    }
    
    protected void addInvokeButton() {
    	//Set up the invoke button
        jButtonInvoke = new javax.swing.JButton();
        jButtonInvoke.setBounds(0, 0, ButtonWidth, ButtonHeight);
        jButtonInvoke.setMinimumSize(new java.awt.Dimension(ButtonWidth, ButtonHeight));
        jButtonInvoke.setMaximumSize(new java.awt.Dimension(ButtonWidth, ButtonHeight));
        jButtonInvoke.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown())
					JOptionPane.showMessageDialog(VisualiserOfRel.this, getQuery(), "Query", JOptionPane.INFORMATION_MESSAGE);
			}
        });
        jButtonInvoke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	invokeLeft(); 
			}
        });
        jButtonInvoke.setEnabled(true);
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	InvokeIcon = new javax.swing.ImageIcon(cl.getResource(InvokeIconFile));
            jButtonInvoke.setIcon(InvokeIcon);
        } catch (Exception e) {
        	jButtonInvoke.setText(">");
        }
        add(jButtonInvoke, java.awt.BorderLayout.CENTER);
    }
	
    protected void addEditButton() {
        //Set up the edit button
        jButtonEdit = new javax.swing.JButton();
        jButtonEdit.setBounds(0, 0, ButtonWidth, ButtonHeight);
        jButtonEdit.setMinimumSize(new java.awt.Dimension(ButtonWidth, ButtonHeight));
        jButtonEdit.setMaximumSize(new java.awt.Dimension(ButtonWidth, ButtonHeight));
        jButtonEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    	invokeRight(); 
			}
        });
        jButtonEdit.setEnabled(true);
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	EditorIcon = new javax.swing.ImageIcon(cl.getResource(EditorIconFile));
        	jButtonEdit.setIcon(EditorIcon);
        } catch (Exception e) {
        	jButtonEdit.setText("+");
        }
        add(jButtonEdit, java.awt.BorderLayout.EAST);
    }
}
