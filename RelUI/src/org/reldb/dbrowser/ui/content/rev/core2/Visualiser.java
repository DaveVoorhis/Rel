package org.reldb.dbrowser.ui.content.rev.core2;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
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

public abstract class Visualiser extends Composite {
	
	private final static Color BaseColor = new Color(Display.getDefault(), 200, 200, 255);
    private final static Color BackgroundColor = new Color(Display.getDefault(), 198, 198, 198);
    private final static Color DropCandidateColor = new Color(Display.getDefault(), 100, 255, 100);
	   	
    private Label lblTitle;
    
    private int mouseOffsetX;
    private int mouseOffsetY;
    private boolean dragging = false;
    private Visualiser dropCandidate = null;
    
    private Model model;
       
    private String id;
    private String title;
    
    protected Composite leftSide;
    protected Composite rightSide;
    
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
		lblTitle.setBackground(BaseColor);
		lblTitle.setAlignment(SWT.CENTER);
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0);
		fd_lblTitle.left = new FormAttachment(0);
		fd_lblTitle.right = new FormAttachment(100);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText(title);
        
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

		leftSide = new Composite(mainPanel, SWT.NONE);
		FormData fd_leftSide = new FormData();
		fd_leftSide.top = new FormAttachment(0);
		fd_leftSide.left = new FormAttachment(0);
		leftSide.setLayoutData(fd_leftSide);
		leftSide.setLayout(new FillLayout(SWT.VERTICAL));
		
		rightSide = new Composite(mainPanel, SWT.NONE);
		FormData fd_rightSide = new FormData();
		fd_rightSide.top = new FormAttachment(0);
		fd_rightSide.right = new FormAttachment(100);
		rightSide.setLayoutData(fd_rightSide);
		rightSide.setLayout(new FillLayout(SWT.VERTICAL));
		
		Composite buttonPanel = new Composite(mainPanel, SWT.NONE);
		buttonPanel.setBackground(BackgroundColor);
		FormData fd_buttonPanel = new FormData();
		fd_buttonPanel.top = new FormAttachment(0);
		fd_buttonPanel.left = new FormAttachment(leftSide);
		fd_buttonPanel.right = new FormAttachment(rightSide);
		buttonPanel.setLayoutData(fd_buttonPanel);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnInfo = new Button(buttonPanel, SWT.FLAT);
		btnInfo.setText("?");
		btnInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
	        	MessageDialog.openInformation(getShell(), getTitle() + " Query", getQuery());
			}
		});
		
		btnRun = new Button(buttonPanel, SWT.FLAT);
		btnRun.setText(">");
		btnRun.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("Visualiser: run " + Visualiser.this.toString());
			}
		});
		
		btnEdit = new Button(buttonPanel, SWT.FLAT);
		btnEdit.setText("+");
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				System.out.println("Visualiser: edit " + Visualiser.this.toString());
			}
		});
		
        lblTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mouseOffsetX = e.x;
				mouseOffsetY = e.y;
				dragging = true;
				setCapture(true);
			}
			@Override
			public void mouseUp(MouseEvent e) {
				setCapture(false);
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
        
        pack();
    }

	protected Control obtainControlPanel(Visualiser parent) {
    	return null;
    }
    
    protected void disconnect() {
		for (Argument argument: arguments)
			argument.setOperand(null);    	
    }

    protected void delete() {
    	disconnect();
		arguments.clear();
		DatabaseAbstractionLayer.removeRelvar(model.getConnection(), getID());
		dispose();
	}
    
    private void bringToFront() {
		for (Argument argument: arguments)
			argument.bringToFront();
		moveAbove(null);
    }
    
    public abstract String getQuery();
    
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
    
    public Argument[] getArguments() {
    	return arguments.toArray(new Argument[0]);
    }
    
    protected void setDropCandidate(boolean b) {
    	lblTitle.setBackground((b) ? DropCandidateColor : BaseColor);
	}

	private boolean isSelfReference(Visualiser maybeConnector, Visualiser visualiser) {
		if (!(maybeConnector instanceof Connector))
			return false;
		Connector connector = (Connector)maybeConnector;
		for (Argument argument: connector.getArguments())
			if (argument.getOperator() == visualiser)
				return true;
		return false;
	}
	
	protected boolean canReceiveDropOf(Visualiser visualiser) {
		return 
			visualiser instanceof Connector &&
			!(this instanceof Connector) &&
			this != visualiser &&
			!isSelfReference(visualiser, this);
	}

    protected void receiveDropOf(Visualiser visualiser) {
    	Connector connector = (Connector)visualiser;
    	for (Argument argument: connector.getArguments())
    		if (argument.getOperand() == visualiser)
    			argument.setOperand(this);
	}

	private void dispatchMovement() {
        (new Timer()).schedule(new TimerTask() {
        	public void run() {
        		if (isDisposed())
        			return;
        		getDisplay().asyncExec(new Runnable() {
        			public void run() {
        				if (!isDisposed())
        					visualiserMoved();		        				
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
}
