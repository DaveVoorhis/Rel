package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.dbrowser.ui.html.BrowserManager;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuples;

import swing2swt.layout.BorderLayout;

/** Visualiser of anything that produces a relation. */
public abstract class VisualiserOfRelation extends Visualiser {	
    private final String InvokeIconFile = "PlayIconTiny";
    private final String EditorIconFile = "EditIconTiny";

	public VisualiserOfRelation(Rev rev, String name) {
		super(rev);
		setVisualiserName(name + rev.getUniqueNumber());
		setLabel();
	}

	public VisualiserOfRelation(Rev rev, String name, String id) {
		super(rev);
		setVisualiserName(id);
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
				if (v != null && v instanceof VisualiserOfRelation)
					((VisualiserOfRelation)v).refresh();
			}
			arg.pulse();
		}
		recursionGate = false;
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
    	if (draggedVisualiser instanceof Operand) {
    		return true;
    	}
    	if (draggedVisualiser instanceof VisualiserOfView) {
    		return true;
    	}
    	return false;
    }
	
    private BrowserManager browsermgr = new BrowserManager();
    
	private Control getDisplayForTuples(Composite parent) {
		browsermgr.createWidget(parent);
		return browsermgr.getWidget();
	}
	
	public abstract String getQuery();

	public void evaluateAndDisplay(String query) {
		/*
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
			*/
	}
	
	protected void invokeLeft() {
		evaluateAndDisplay(getQuery());
	}
    
    protected void invokeRight() {
		getRev().createTuplesVisualiser(getQuery(), getVisualiserName());
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
    	addShowButton();
    	addInvokeButton();
    }
    
    protected void addShowButton() {
    	// Set up the show button
        Button buttonShow = new Button(this, SWT.None);
        buttonShow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(getShell(), "Query", getQuery());
			}
        });
        buttonShow.setText("?");
        buttonShow.setLayoutData(BorderLayout.WEST);
    }

    protected void addInvokeButton() {
    	// Set up the invoke button
        Button buttonInvoke = new Button(this, SWT.None);
        buttonInvoke.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				invokeLeft();
			}
        });
        try {
            buttonInvoke.setImage(IconLoader.loadIconSmall(InvokeIconFile));
        } catch (Exception e) {
        	buttonInvoke.setText(">");
        }
        buttonInvoke.setLayoutData(BorderLayout.CENTER);
    }
	
    protected void addEditButton() {
        // Set up the edit button
        Button buttonEdit = new Button(this, SWT.None);
        buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		    	invokeRight(); 
			}
        });
        try {
        	buttonEdit.setImage(IconLoader.loadIconSmall(EditorIconFile));
        } catch (Exception e) {
        	buttonEdit.setText("+");
        }
        buttonEdit.setLayoutData(BorderLayout.EAST);
    }
}
