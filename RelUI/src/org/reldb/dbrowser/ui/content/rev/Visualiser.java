package org.reldb.dbrowser.ui.content.rev;

import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.RevDatabase;

public abstract class Visualiser extends Composite implements Comparable<Visualiser> {
	
	protected final static Color BaseColor = new Color(Display.getDefault(), 200, 200, 255);
	protected final static Color WarningColor = new Color(Display.getDefault(), 255, 255, 200);
    protected final static Color BackgroundColor = new Color(Display.getDefault(), 198, 198, 198);
    protected final static Color DropCandidateColor = new Color(Display.getDefault(), 100, 255, 100);
	   	
    private Color titleColor = BaseColor;
    private Label lblTitle;
    
    private int mouseOffsetX;
    private int mouseOffsetY;
    private boolean dragging = false;
    private Visualiser dropCandidate = null;
    
    private Model model;
       
    private String id;
    private String title;
    
    protected Button btnInfo;
    protected Button btnRun;
    protected Button btnEdit;

    private Vector<Argument> arguments = new Vector<Argument>();
    
    /** Ctor */
    protected Visualiser(Model model, String id, String title, int xpos, int ypos) {
    	super(model, SWT.NONE);
    	
    	this.id = id;
    	this.model = model;
    	this.title = title;
    	
    	setBackground(BackgroundColor);

    	setLocation(xpos, ypos);

		setLayout(new FormLayout());
		
		lblTitle = new Label(this, SWT.NONE);
		lblTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		lblTitle.setBackground(titleColor);
		lblTitle.setAlignment(SWT.CENTER);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		fd_lblTitle.right = new FormAttachment(100);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title);

		if (!model.getRev().isReadOnly())
			setupPopupMenu();
		
		Composite mainPanel = new Composite(this, SWT.NONE);
		mainPanel.setBackground(BackgroundColor);
		mainPanel.setLayout(new FormLayout());
		FormData fd_mainPanel = new FormData();
		fd_mainPanel.top = new FormAttachment(lblTitle);
		fd_mainPanel.left = new FormAttachment(0);
		fd_mainPanel.right = new FormAttachment(100);
		mainPanel.setLayoutData(fd_mainPanel);
		
		Control controlPanel = obtainControlPanel(this);
		if (controlPanel != null) {
			FormData fd_controlPanel = new FormData();
			fd_controlPanel.top = new FormAttachment(mainPanel);
			fd_controlPanel.left = new FormAttachment(0);
			fd_controlPanel.right = new FormAttachment(100);
			fd_controlPanel.bottom = new FormAttachment(100);
			controlPanel.setLayoutData(fd_controlPanel);			
		}
		
		Composite buttonPanel = new Composite(mainPanel, SWT.NONE);
		buttonPanel.setBackground(BackgroundColor);
		FormData fd_buttonPanel = new FormData();
		fd_buttonPanel.top = new FormAttachment(0);
		fd_buttonPanel.left = new FormAttachment(0);
		fd_buttonPanel.right = new FormAttachment(100);
		buttonPanel.setLayoutData(fd_buttonPanel);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnInfo = new Button(buttonPanel, SWT.FLAT);
		btnInfo.setText("?");
		btnInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
	        	(new ViewQueryDialog(getShell(), getQuery())).open();
			}
		});
		
		btnRun = new Button(buttonPanel, SWT.FLAT);
		btnRun.setText(">");
		btnRun.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				String query = getQuery();
				if (query != null)
					model.getRev().getCmdPanelOutput().go(query, true);
				else
					model.getRev().getCmdPanelOutput().badResponse("Unable to produce query.");
			}
		});
		
		btnEdit = new Button(buttonPanel, SWT.FLAT);
		btnEdit.setText("+");
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("Visualiser: edit " + Visualiser.this.toString());
			}
		});
		
        if (!model.getRev().isReadOnly()) {
	        lblTitle.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (e.button != 1)
						return;
					mouseOffsetX = e.x;
					mouseOffsetY = e.y;
					dragging = true;
				}
				@Override
				public void mouseUp(MouseEvent e) {
					if (e.button != 1)
						return;
					dragging = false;
	        		if (dropCandidate!=null) {
	        			Point location = getLocation();
	        			if (dropCandidate == model.getPossibleDropTarget(e.x + location.x, e.y + location.y, Visualiser.this)) {
	        				dropCandidate.setDropCandidate(false);
	        				dropCandidate.receiveDropOf(Visualiser.this);
	        			}
	        			dropCandidate = null;
	        		}
				}
	        });
	        lblTitle.addMouseMoveListener(new MouseMoveListener() {
				@Override
				public void mouseMove(MouseEvent e) {
					model.disablePopupMenu();
					if (dragging) {
						Point location = getLocation();
						int newX = location.x + e.x - mouseOffsetX;
						int newY = location.y + e.y - mouseOffsetY;
						setLocation(newX, newY);
						// drop?
		                Visualiser dropTarget = model.getPossibleDropTarget(location.x + e.x, location.y + e.y, Visualiser.this);
		                if (dropCandidate != null && dropCandidate != dropTarget)
		                    dropCandidate.setDropCandidate(false);
		                if (dropTarget != null) {
		                    dropCandidate = dropTarget;
		                    dropCandidate.setDropCandidate(true);
		                }		
					} else
						bringToFront();
				}
	        });

	        addControlListener(new ControlAdapter() {
				@Override
				public void controlMoved(ControlEvent e) {
					dispatchMovement();
				}
	        });
	        dispatchMovement();
        }
        
        pack();
    }

    public void setWarningColour() {
    	titleColor = WarningColor;
    	lblTitle.setBackground(titleColor);
    }
    
    public void setReadyColour() {
    	titleColor = BaseColor;
    	lblTitle.setBackground(titleColor);
    }
    
    protected void setupPopupMenu() {
		Menu menuBar = new Menu(getShell(), SWT.POP_UP);
		
		MenuItem disconnect = new MenuItem(menuBar, SWT.PUSH);
		disconnect.setText("Disconnect");
		disconnect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				disconnect();
			}
		});

		MenuItem delete = new MenuItem(menuBar, SWT.PUSH);
		delete.setText("Delete");
		delete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				delete();
			}
		});

		lblTitle.setMenu(menuBar);
    }
    
    public int compareTo(Visualiser v) {
    	return getID().compareTo(v.getID());
    }
    
    public boolean equals(Object o) {
    	return compareTo((Visualiser)o) == 0;
    }
    
    public int hashCode() {
    	return getID().hashCode();
    }
    
	protected Control obtainControlPanel(Visualiser parent) {
    	return null;
    }
    
    protected void disconnect() {
    	Argument argumentArray[] = arguments.toArray(new Argument[0]);
    	for (Argument argument: argumentArray)
			argument.setOperand(null);    	
    	arguments.clear();
    }

    protected void delete() {
    	disconnect();
		getDatabase().removeRelvar(getID());
		dispose();
        // If this visualiser was the last in this model, fireModelChangeEvent.
        if (model.getVisualiserCount() == 0)
        	model.getRev().fireModelChangeEvent();		
	}

	private void bringToFront() {
		for (Argument argument: arguments)
			argument.bringToFront();
		moveAbove(null);
    }
    
    public abstract String getQuery();
    
    public RevDatabase getDatabase() {
    	return model.getDatabase();
	}
    
	public String getID() {
    	return id;
    }
    
    public Model getModel() {
    	return model;
    }
    
    public String getTitle() {
    	return title;
    }
    
    public String toString() {
    	return "Visualiser " + getTitle() + " (" + getID() + ")";
    }
    
    /** Without running getQuery(), return true if getQuery() should return non-null. */
    public boolean isQueryable() {
    	return true;
    }
    
    public Argument[] getArguments() {
    	return arguments.toArray(new Argument[0]);
    }
    
    protected void setDropCandidate(boolean b) {
    	lblTitle.setBackground((b) ? DropCandidateColor : titleColor);
	}

	private boolean isSelfReference(Connector connector) {
    	Argument argument = connector.getArguments()[0];
		return (argument.getOperator() == this);
	}

	protected Collection<Visualiser> collectAllConnectedSources() {
		return new HashSet<Visualiser>();
	}
	
	private boolean isCircularReference(Connector connector) {
		return collectAllConnectedSources().contains(connector.getArguments()[0].getOperator());
	}
	
	protected boolean canReceiveDropOf(Visualiser visualiser) {
		if (!(visualiser instanceof Connector))
			return false;
		Connector connector = (Connector)visualiser;
		return !(this instanceof Connector) && !isSelfReference(connector) && !isCircularReference(connector);
	}

    protected void receiveDropOf(Visualiser visualiser) {
    	Connector connector = (Connector)visualiser;
    	Argument argument = connector.getArguments()[0];
		argument.setOperand(this);
	}

    private boolean fireModelChangeEventCheckDone = false;
    
	private void dispatchMovement() {
        (new Timer()).schedule(new TimerTask() {
        	public void run() {
        		if (isDisposed())
        			return;
        		getDisplay().asyncExec(new Runnable() {
        			public void run() {
        				if (!isDisposed()) {
        					visualiserMoved();        			        
        			        // If this visualiser is the first in this model, fireModelChangeEvent.
        					if (fireModelChangeEventCheckDone)
        						return;
        			        if (model.getVisualiserCount() == 1)
        			        	model.getRev().fireModelChangeEvent();
        			        fireModelChangeEventCheckDone = true;
        				}
        			}
        		});
        	}
        }, 250);
        movement();
    }
    
	/** Override to be notified of every movement. */
	protected void movement() {
		if (arguments != null)
			for (Argument argument: arguments)
				argument.redraw();		
	}
	
    /** Override to be notified 250 milliseconds after movement has stopped. */
    protected void visualiserMoved() {
    }
    
	public void addArgumentReference(Argument argument) {
		arguments.add(argument);
		movement();
		visualiserMoved();
	}
	
	public void removeArgumentReference(Argument argument) {
		arguments.remove(argument);
		movement();
		visualiserMoved();
	}
	
	private int getArgumentIndex(Argument argument) {
		int index = arguments.indexOf(argument);
		if (index == -1)
			System.out.println("Visualiser: can't find argument " + argument + " in visualiser " + this);
		return index;
	}
	
	public int getArgumentX(Argument argument) {
		if (isDisposed())
			return 0;
    	Rectangle bounds = getBounds();
        int indexOfArgument = arguments.indexOf(argument);
        if (indexOfArgument==-1)
            return bounds.x + bounds.width / 2;
        int marginWidth = bounds.width / 5;               // 5%
        int drawWidth = bounds.width - marginWidth;
        int drawStep = drawWidth / arguments.size();
        if (drawStep < 0)
            return bounds.x + bounds.width / 2;
        return bounds.x + (bounds.width - drawWidth) / 2 + drawStep / 2 + indexOfArgument * drawStep;
	}

	public int getArgumentY(Argument argument) {
		if (isDisposed())
			return 0;
		if (isConnectionAtBottom(argument))
			return getBounds().y + getBounds().height;
		else
			return getBounds().y;
	}

	public int getExtensionLength(Argument argument) {
		return 10 + getArgumentIndex(argument) * 4;
	}

	public boolean isConnectionAtBottom(Argument argument) {
		if (isDisposed())
			return false;
		return argument.getOperator().getBounds().y > getBounds().y + getBounds().height;
	}
	
	/** Invoked when the model changes, to allow the visualiser to update itself accordingly. */
	public void verify() {
		for (Argument argument: arguments)
			argument.getOperator().verify();
	}
}
