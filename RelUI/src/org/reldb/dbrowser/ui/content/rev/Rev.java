package org.reldb.dbrowser.ui.content.rev;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelOutput;
import org.reldb.dbrowser.ui.content.rev.operators.Diadic;
import org.reldb.dbrowser.ui.content.rev.operators.Extend;
import org.reldb.dbrowser.ui.content.rev.operators.Group;
import org.reldb.dbrowser.ui.content.rev.operators.Order;
import org.reldb.dbrowser.ui.content.rev.operators.Project;
import org.reldb.dbrowser.ui.content.rev.operators.Rename;
import org.reldb.dbrowser.ui.content.rev.operators.Restrict;
import org.reldb.dbrowser.ui.content.rev.operators.Summarize;
import org.reldb.dbrowser.ui.content.rev.operators.TableDee;
import org.reldb.dbrowser.ui.content.rev.operators.TableDum;
import org.reldb.dbrowser.ui.content.rev.operators.Ungroup;
import org.reldb.dbrowser.ui.content.rev.operators.Unwrap;
import org.reldb.dbrowser.ui.content.rev.operators.Wrap;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class Rev extends Composite {
	private Connection connection;
	private Model model;
	private CmdPanelOutput outputView;
	private SashForm revPane;
	private CrashHandler crashHandler;
	
	public Rev(Composite parent, DbTab parentTab, CrashHandler crashHandler) {
		super(parent, SWT.None);
		this.crashHandler = crashHandler;
		
		try {
			connection = new Connection(parentTab.getURL(), false, crashHandler, null);
		} catch (Exception e) {
			System.out.println("Rev: Unable to establish connection.");
			e.printStackTrace();
		}
		
		setLayout(new FillLayout());
				
		revPane = new SashForm(this, SWT.NONE);
		revPane.setOrientation(SWT.VERTICAL);
		
		try {
			outputView = new CmdPanelOutput(revPane, parentTab, SWT.NONE);
		} catch (Exception e) {
			System.out.println("Rev: Unable to open output panel.");
			e.printStackTrace();
		}

		ScrolledComposite scrollPanel = new ScrolledComposite(revPane, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		model = new Model(this, "Rev", scrollPanel);
		model.setSize(10000, 10000);
		
		scrollPanel.setContent(model);
		scrollPanel.setExpandHorizontal(true);
		scrollPanel.setExpandVertical(true);
		scrollPanel.setMinSize(model.getSize());

		revPane.setWeights(new int[] {1, 1});

		setupMenus();
	}

	public CmdPanelOutput getCmdPanelOutput() {
		return outputView;
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

		model.clear();
		
		Menu menuBar = new Menu(getShell(), SWT.POP_UP);
		model.setMenu(menuBar);

		// Custom relvars
		MenuItem customRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		customRelvarsItem.setText("Variables");
		customRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, "Owner = 'User'"));
		
		// System relvars
		MenuItem systemRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		systemRelvarsItem.setText("System variables");
		systemRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, "Owner = 'Rel'"));
		
		// Operators
		OpSelector[] queryOperators = getOperators();
		MenuItem insOperatorsItem = new MenuItem(menuBar, SWT.CASCADE);
		insOperatorsItem.setText("Operators and constants");
		Menu insOperatorsMenu = new Menu(menuBar);
		for (int i=0; i < queryOperators.length; i++) {
			String opName = queryOperators[i].toString();
			if (opName == null)
				new MenuItem(insOperatorsMenu, SWT.SEPARATOR);
			else {
				MenuItem item = new MenuItem(insOperatorsMenu, SWT.PUSH);
				item.setText(opName);
				item.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent evt) {
						Point lastMousePosition = model.getLastMousePosition();
						obtainOperatorForKind(opName, opName + getUniqueNumber(), lastMousePosition.x, lastMousePosition.y);
					}
				});
			}
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
				if (!MessageDialog.openConfirm(getShell(), "Rev", "Remove everything from this model?"))
					return;
				model.removeEverything();
			}
		});

		int version = hasRevExtensions();
		if (version < 0) {
			installRevExtensions();
			refresh();
		} else if (version < DatabaseAbstractionLayer.EXPECTED_REV_VERSION) {
			upgrade(version);
		} else {
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
	
	private static interface OpSelectorRun {
		public Operator obtain(Rev rev, String name, int xpos, int ypos);
	}
	
	private static class OpSelector {
		private String menuTitle;
		private OpSelectorRun run;
		public OpSelector(String menuTitle, OpSelectorRun run) {
			this.menuTitle = menuTitle;
			this.run = run;
		}
		public OpSelector() {
			this.menuTitle = null;
			this.run = null;
		}
		public String toString() {
			return menuTitle;
		}
		public Operator getOperator(Rev rev, String name, int xpos, int ypos) {
			return run.obtain(rev, name, xpos, ypos);
		}
	}

	private OpSelector[] getOperators() {
		OpSelector[] operators = {
			new OpSelector("Project", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Project(rev, name, xpos, ypos);
			}}),
			new OpSelector("Restrict", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Restrict(rev, name, xpos, ypos);
			}}),
			new OpSelector("UNION", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "UNION", xpos, ypos);
				}}),
			new OpSelector("RENAME", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Rename(rev, name, xpos, ypos);
				}}),
			new OpSelector("INTERSECT", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "INTERSECT", xpos, ypos);
				}}),
			new OpSelector("MINUS", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "MINUS", xpos, ypos);
				}}),
			/*
			new OpSelector("PRODUCT", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new VisualiserOfOperatorProduct(rev, name, xpos, ypos);
				}}),
			new OpSelector("DIVIDEBY", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new VisualiserOfOperatorDivideby(rev, name, xpos, ypos);
				}}),
			*/
			new OpSelector("JOIN", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "JOIN", xpos, ypos);
				}}),
			new OpSelector("COMPOSE", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "COMPOSE", xpos, ypos);
				}}),
			new OpSelector("MATCHING", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "MATCHING", xpos, ypos);
				}}),
			new OpSelector("NOT MATCHING", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Diadic(rev, name, "NOT MATCHING", xpos, ypos);
				}}),
			new OpSelector("ORDER", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Order(rev, name, xpos, ypos);
				}}),
			new OpSelector("GROUP", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Group(rev, name, xpos, ypos);
				}}),
			new OpSelector("UNGROUP", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Ungroup(rev, name, xpos, ypos);
				}}),
			new OpSelector("WRAP", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Wrap(rev, name, xpos, ypos);
				}}),
			new OpSelector("UNWRAP", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Unwrap(rev, name, xpos, ypos);
				}}),
			new OpSelector("EXTEND", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Extend(rev, name, xpos, ypos);
				}}),
			new OpSelector("SUMMARIZE", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new Summarize(rev, name, xpos, ypos);
				}}),
			new OpSelector(),
			new OpSelector("TABLE_DEE", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new TableDee(rev, name, xpos, ypos);
				}}),
			new OpSelector("TABLE_DUM", new OpSelectorRun() {
				public Operator obtain(Rev rev, String name, int xpos, int ypos) {
					return new TableDum(rev, name, xpos, ypos);
				}})
		};
		return operators;
	}
	
	private Operator obtainOperatorForKind(String kind, String name, int xpos, int ypos) {
		for (OpSelector selector: getOperators())
			if (selector.toString() != null && selector.toString().compareTo(kind) == 0)
				return selector.getOperator(Rev.this, name, xpos, ypos);
		System.out.println("Query kind '" + kind + "' not recognised.");
		return null;
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
		if (pass)
			setupMenus();
		return pass;
	}

	private boolean removeRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.removeRevExtensions(connection);
		if (pass)
			setupMenus();
		return pass;
	}
	
	private void uninstall() {
		if (hasRevExtensions() < 0)
        	MessageDialog.openInformation(getShell(), "Rev", "Rev is not installed.");
		if (!MessageDialog.openConfirm(getShell(), "Rev", "Are you sure?  This will remove all Rev query definitions."))
			return;
		if (removeRevExtensions())
			refresh();
	}
	
	public void refresh() {
		setupMenus();
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
