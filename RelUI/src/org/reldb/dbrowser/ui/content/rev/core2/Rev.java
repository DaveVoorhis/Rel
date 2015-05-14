package org.reldb.dbrowser.ui.content.rev.core2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Diadic;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Extend;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Group;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Order;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Project;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Rename;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Restrict;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Summarize;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Ungroup;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Unwrap;
import org.reldb.dbrowser.ui.content.rev.core2.operators.Wrap;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class Rev extends Composite {
	private Connection connection;
	private Model model;
	private Composite detailView;
	private SashForm revPane;
	private String[] queryOperators;
	private CrashHandler crashHandler;
	
	public Rev(Composite parent, String dbURL, CrashHandler crashHandler) {
		super(parent, SWT.None);
		this.crashHandler = crashHandler;
		
		try {
			connection = new Connection(dbURL, false, crashHandler, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setLayout(new FillLayout());
				
		revPane = new SashForm(this, SWT.NONE);
		revPane.setOrientation(SWT.VERTICAL);

		model = new Model(this, "Rev", revPane);

		detailView = new Composite(revPane, SWT.BORDER);

		revPane.setWeights(new int[] {8, 2});

		setupMenus();
	}

	public long getUniqueNumber() {
		return DatabaseAbstractionLayer.getUniqueNumber(connection);
	}
	
	public CrashHandler getCrashHandler() {
		return crashHandler;
	}

	public void setupMenus() {
		if (getMenu() != null)
			getMenu().dispose();
		
		Menu menuBar = new Menu(getShell(), SWT.POP_UP);
		
		// Custom relvars
		MenuItem customRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		customRelvarsItem.setText("Variables");
		customRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, "Owner = 'User'"));
		
		//System relvars
		MenuItem systemRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		systemRelvarsItem.setText("System variables");
		systemRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, "Owner = 'Rel'"));
		
		// Operators
		queryOperators = getOperators();
		MenuItem insOperatorsItem = new MenuItem(menuBar, SWT.CASCADE);
		insOperatorsItem.setText("Operators");
		Menu insOperatorsMenu = new Menu(menuBar);
		for (int i=0; i < queryOperators.length; i++) {
			MenuItem item = new MenuItem(insOperatorsMenu, SWT.PUSH);
			String queryName = queryOperators[i];
			item.setText(queryName);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					Point lastMousePosition = model.getLastMousePosition();
					obtainOperatorForKind(queryName, queryName + getUniqueNumber(), lastMousePosition.x, lastMousePosition.y);
				}
			});
		}
		insOperatorsItem.setMenu(insOperatorsMenu);
		
		// Refresh
		MenuItem refreshRev = new MenuItem(menuBar, SWT.PUSH);
		refreshRev.setText("Refresh");
		refreshRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				refresh();
			}
		});
		
		// Clear
		MenuItem clearRev = new MenuItem(menuBar, SWT.PUSH);
		clearRev.setText("Clear");
		clearRev.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// Remove the visualisers
				model.removeEverything();
				// Refresh combo boxes
				refreshMenus();
			}
		});

		int version = hasRevExtensions();
		if (version < 0) {
			System.out.println("Rev: extensions are not present.");
			MenuItem installRev = new MenuItem(menuBar, SWT.PUSH);
			installRev.setText("Install Rev extensions");
			installRev.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					installRevExtensions();
					refresh();
				}
			});
		} else if (version < DatabaseAbstractionLayer.EXPECTED_REV_VERSION) {
			upgrade(version);
		} else {
			System.out.println("Rev: extensions are present.");
			presentRelvarsWithRevExtensions("");
			presentQueriesWithRevExtensions("");
			
			// Uninstall
			MenuItem uninstallRev = new MenuItem(menuBar, SWT.PUSH);
			uninstallRev.setText("Uninstall Rev extensions");
			uninstallRev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					uninstall();
				}
			});			
		}
				
		setMenu(menuBar);
	}
	
	private void refreshMenus() {
		setupMenus();
	}
	
	private Menu obtainRelvarsMenu(Menu parent, String where) {
		Menu subMenu = new Menu(parent);
		Tuples tuples = DatabaseAbstractionLayer.getRelvarsWithoutRevExtensions(connection, where);
		if (tuples != null) {
			Iterator<Tuple> it = tuples.iterator();
			while (it.hasNext()) {
				Tuple tuple = it.next();
				if (tuple != null) {
					MenuItem item = new MenuItem(subMenu, SWT.PUSH);
					String name = tuple.get("Name").toString();
					item.setText(name);
					SelectionListener listener = new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent evt) {
							Point lastMousePosition = model.getLastMousePosition();
							new Relvar(Rev.this, name + getUniqueNumber(), name, lastMousePosition.x, lastMousePosition.y);
						}
					};
					item.addSelectionListener(listener);
				}
			}
		}
		return subMenu;
	}
	
	public void deleteVisualiser(Visualiser connected, Visualiser deleteOperator) {
		if (connected == null) {
			return;
		}
		model.removeVisualiser(connected);
		model.removeVisualiser(deleteOperator);
		refreshMenus();
	}
	
	private String[] getOperators() {
		String[] operators = { 
				"Project",
				"Restrict WHERE",
				"UNION",
				"RENAME",
				"D_UNION",
				"INTERSECT",
				"MINUS",
				"PRODUCT",
				"DIVIDEBY",
				"JOIN",
				"COMPOSE",
				"MATCHING (SEMIJOIN)",
				"NOT MATCHING (SEMIMINUS)",
				"EXTEND",
				"GROUP",
				"UNGROUP",
				"WRAP",
				"UNWRAP",
				"TCLOSE",
				"ORDER",
				"SUMMARIZE",
				"DEE (TABLE_DEE)",
				"DUM (TABLE_DUM)",
			};
		return operators;
	}
	
	private Operator obtainOperatorForKind(String kind, String name, int xpos, int ypos) {
		Operator visualiser = null;
		if (kind.equals("Project")) {
			visualiser = new Project(this, name, xpos, ypos);
		} else if (kind.equals("Restrict")) {
			visualiser = new Restrict(this, name, xpos, ypos);
		} else if (kind.equals("UNION")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("RENAME")) {
			visualiser = new Rename(this, name, xpos, ypos);
		} else if (kind.equals("INTERSECT")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("MINUS")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} /*else if (kind.equals("PRODUCT")) {
			visualiser = new VisualiserOfOperatorProduct(this, name, xpos, ypos);
		} else if (kind.equals("DIVIDEBY")) {
			visualiser = new VisualiserOfOperatorDivideby(this, name, xpos, ypos);
		}*/ else if (kind.equals("JOIN")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("COMPOSE")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("MATCHING")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("NOT MATCHING")) {
			visualiser = new Diadic(this, name, kind, xpos, ypos);
		} else if (kind.equals("ORDER")) {
			visualiser = new Order(this, name, xpos, ypos);
		} else if (kind.equals("GROUP")) {
			visualiser = new Group(this, name, xpos, ypos);
		} else if (kind.equals("UNGROUP")) {
			visualiser = new Ungroup(this, name, xpos, ypos);
		} else if (kind.equals("WRAP")) {
			visualiser = new Wrap(this, name, xpos, ypos);
		} else if (kind.equals("UNWRAP")) {
			visualiser = new Unwrap(this, name, xpos, ypos);
		} else if (kind.equals("EXTEND")) {
			visualiser = new Extend(this, name, xpos, ypos);
		} else if (kind.equals("SUMMARIZE")) {
			visualiser = new Summarize(this, name, xpos, ypos);
		} else {
			System.out.println("Query kind '" + kind + "' not recognised.");
		}
		return visualiser;
	}
	
	private void presentRelvarsWithRevExtensions(String where) {
		int nextX = 10;
		int nextY = 10;
		Tuples tuples = DatabaseAbstractionLayer.getRelvars(connection, where);
		if (tuples == null)
			return;
		for (Tuple tuple: tuples) {
			int xpos = tuple.get("xpos").toInt();
			int ypos = tuple.get("ypos").toInt();
			// String modelName = tuple.get("model").toString();
			// Set up relvar position
			if (xpos == -1 && ypos == -1) {
				xpos = nextX;
				ypos = nextY;
			}
			// Create a new relvar
			Visualiser relvar = new Relvar(this, tuple.get("Name").toString(), tuple.get("relvarName").toString(), xpos, ypos);
			nextY += relvar.getBounds().height + 10;
		}
	}
	
	private void presentQueriesWithRevExtensions(String where) {
		HashMap<String, LinkedList<Parameter>> unresolved = new HashMap<String, LinkedList<Parameter>>();
		// Load in the regular query visualisers
		for (Tuple tuple: DatabaseAbstractionLayer.getQueries(connection, where)) {
			String name = tuple.get("Name").toString();
			int xpos = tuple.get("xpos").toInt();
			int ypos = tuple.get("ypos").toInt();
			String kind = tuple.get("kind").toString();
			// String modelName = tuple.get("model").toString();
			System.out.println("Rev: create operator " + kind);
			Operator operator = obtainOperatorForKind(kind, name, xpos, ypos);
			if (operator == null)
				continue;
			LinkedList<Parameter> unresolvedParms = unresolved.get(name);
			if (unresolvedParms != null) {
				unresolved.remove(name);
				for (Parameter parm: unresolvedParms) {
					parm.getArgument().setOperand(operator);
				}
			}
			for (Tuple connection: (Tuples)tuple.get("connections")) {
				int parameterNumber = connection.get("parameter").toInt();
				Parameter parameter = operator.getParameter(parameterNumber);
				String visualiserName = connection.get("Name").toString();
				Visualiser operand = model.getVisualiser(visualiserName);
				if (operand != null && !(operand instanceof Connector)) {
					parameter.getArgument().setOperand(operand);
				} else {
					LinkedList<Parameter> unresolvedParameters = unresolved.get(visualiserName);
					if (unresolvedParameters == null)
						unresolvedParameters = new LinkedList<Parameter>();
					unresolvedParameters.add(parameter);						
					unresolved.put(visualiserName, unresolvedParameters);
				}
			}
		}
	}
	
	// Return version number of Rev extensions.  Return -1 if not installed.
	private int hasRevExtensions() {
		return DatabaseAbstractionLayer.hasRevExtensions(connection);
	}
	
	private boolean installRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.installRevExtensions(connection);
		if (pass) {
			refreshMenus();
		}
		return pass;
	}

	private boolean removeRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.removeRevExtensions(connection);
		if (pass) {
			refreshMenus();
		}
		return pass;
	}
	
	private void uninstall() {
		if (hasRevExtensions() < 0) {
        	MessageDialog.openInformation(getShell(), "Rev", "Rev is not installed.");
		}
		if (!MessageDialog.openConfirm(getShell(), "Rev", "Uninstall Rev?"))
			return;
		if (removeRevExtensions()) {
			refresh();
			setVisible(false);
			setVisible(true);
		}
	}
	
	public void refresh() {
		refreshMenus();
	}
	
	private void upgrade(int currentVersionOfRevFromDatabase) {
		// Perform upgrade from currentVersionOfRevFromDatabase to EXPECTED_REV_VERSION
	}

	public Model getModel() {
		return model;
	}

	public Connection getConnection() {
		return connection;
	}
	
}
