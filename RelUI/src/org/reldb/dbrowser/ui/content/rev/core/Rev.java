package org.reldb.dbrowser.ui.content.rev.core;

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
import org.reldb.dbrowser.ui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Model;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

import swing2swt.layout.BorderLayout;

public class Rev extends Composite {
	private Connection connection;
	private Model model;
	private Composite detailView;
	private VisualiserOfTuples tuples;
	private SashForm revPane;
	private String[] queryOperators;
	private LinkedList<VisualiserOfView> views;
	private CrashHandler crashHandler;

	public long getUniqueNumber() {
		return DatabaseAbstractionLayer.getUniqueNumber(connection);
	}
	
	public CrashHandler getCrashHandler() {
		return crashHandler;
	}
	
	public void createTuplesVisualiser(String query, String name) {
		tuples = new VisualiserOfTuples(this, "VisTuples", "VisTuples" + getUniqueNumber(), 0, 0);
		if (tuples != null) {
//			tuples.setSize(detailView.getSize());
			tuples.setQuery(query, name);
			tuples.createNew();
		}
	}
	
	private VisualiserOfOperator getVisualiserForKind(String kind, String name, int xpos, int ypos) {
		VisualiserOfOperator visualiser = null;
		if (kind.equals("Project")) {
			visualiser = new VisualiserOfOperatorProject(this, name);
		} else if (kind.equals("Restrict")) {
			visualiser = new VisualiserOfOperatorRestrict(this, name);
		} else if (kind.equals("UNION")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("RENAME")) {
			visualiser = new VisualiserOfOperatorRename(this, name);
		} else if (kind.equals("INTERSECT")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("MINUS")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} /*else if (kind.equals("PRODUCT")) {
			visualiser = new VisualiserOfOperatorProduct(this, name);
		} else if (kind.equals("DIVIDEBY")) {
			visualiser = new VisualiserOfOperatorDivideby(this, name);
		}*/ else if (kind.equals("JOIN")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("COMPOSE")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("MATCHING")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("NOT MATCHING")) {
			visualiser = new VisualiserOfOperatorDiyadic(this, name, kind);
		} else if (kind.equals("ORDER")) {
			visualiser = new VisualiserOfOperatorOrder(this, name);
		} else if (kind.equals("GROUP")) {
			visualiser = new VisualiserOfOperatorGroup(this, name);
		} else if (kind.equals("UNGROUP")) {
			visualiser = new VisualiserOfOperatorUngroup(this, name);
		} else if (kind.equals("WRAP")) {
			visualiser = new VisualiserOfOperatorWrap(this, name);
		} else if (kind.equals("UNWRAP")) {
			visualiser = new VisualiserOfOperatorUnwrap(this, name);
		} else if (kind.equals("EXTEND")) {
			visualiser = new VisualiserOfOperatorExtend(this, name);
		} else if (kind.equals("SUMMARIZE")) {
			visualiser = new VisualiserOfOperatorSummarize(this, name);
		} else {
			System.out.println("Query kind '" + kind + "' not recognised.");
		}
		visualiser.setLocation(xpos, ypos);
		return visualiser;
	}
	
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

		model = new Model(revPane);
		model.setRev(this);

		detailView = new Composite(revPane, SWT.BORDER);

		revPane.setWeights(new int[] {8, 2});

		setupMenus();
	}

	public void setupMenus() {
		if (getMenu() != null)
			getMenu().dispose();
		
		Menu menuBar = new Menu(getShell(), SWT.POP_UP);
		
		// Custom relvars
		MenuItem customRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		customRelvarsItem.setText("Variables");
		customRelvarsItem.setMenu(obtainMenu(menuBar, "sys.Catalog", "Owner = 'User'"));
		
		//System relvars
		MenuItem systemRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		systemRelvarsItem.setText("System variables");
		systemRelvarsItem.setMenu(obtainMenu(menuBar, "sys.Catalog", "Owner = 'Rel'"));
		
		// Operators
		queryOperators = getOperators();
		MenuItem insOperatorsItem = new MenuItem(menuBar, SWT.CASCADE);
		insOperatorsItem.setText("Operators");
		Menu insOperatorsMenu = new Menu(menuBar);
		for (int i=0; i < queryOperators.length; i++) {
			MenuItem item = new MenuItem(insOperatorsMenu, SWT.PUSH);
			item.setText(queryOperators[i]);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					insertOperator(arg0);
				}
			});
		}
		insOperatorsItem.setMenu(insOperatorsMenu);
		
		// Views
		MenuItem viewMenuItem = new MenuItem(menuBar, SWT.CASCADE);
		viewMenuItem.setText("Views");
		if (hasRevExtensions() != -1) {
			viewMenuItem.setMenu(obtainMenu(menuBar, "sys.rev.View", "stored = true"));
		}
		
		// Create relvar
		MenuItem addNewRelvar = new MenuItem(menuBar, SWT.PUSH);
		addNewRelvar.setText("Create Relvar");
		addNewRelvar.addSelectionListener(new SelectionAdapter() {	
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				new VisualiserOfNewRelvar(Rev.this);
			}
		});
		
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
				model.removeEverything();	//This line twice to make sure views with content get removed correctly
				model.removeEverything();
				// Refresh combo boxes
				updateMenus();
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
			presentWithRevExtensions();
			
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
	
	private void insertOperator(SelectionEvent e) {
		MenuItem caller = (MenuItem)e.getSource();
		getVisualiserForOperator(caller.getText());
	}
	
	private void menuAction(SelectionEvent e) {
		MenuItem caller = (MenuItem)e.getSource();
		String name = caller.getText();
		Visualiser relvar = new VisualiserOfRelvar(this, name);
		//Find out whether to insert the visualiser into the default model
		//or a view model
		Visualiser[] selected = getModel().getSelected();
		if (selected != null) {
			if (selected.length > 0) {
				Visualiser target = selected[0];
				if (target instanceof VisualiserOfView) {
					LinkedList<Visualiser> justOne = new LinkedList<Visualiser>();
					justOne.add(relvar);
					((VisualiserOfView) target).moveVisualisersToModel(justOne, true);
				}
			}
		}
	}
	
	private void viewCombo(SelectionEvent e) {
		MenuItem caller = (MenuItem)e.getSource();
		String name = caller.getText();
		//Create view
		presentViewsWithRevExtensions("Name = '" + name + "'");
		//Add in the contained relvars
		presentRelvarsWithRevExtensions("model = '" + name + "'");
		//Add in the contained queries
		presentQueriesWithRevExtensions("model = '" + name + "'");
		//Update view with relvars
		for (VisualiserOfView view: views) {
			if (view.getVisualiserName().equals(name)) {
				view.commitTempList();
			}
		}
	}
	
	private void updateMenus() {
		setupMenus();
	}
	
	private Menu obtainMenu(Menu parent, String relvar, String where) {
		boolean relvarFlag = true;

		Menu subMenu = new Menu(parent);
		
		//Add the relvars from the database
		Tuples tuples = null;
		//Relvars
		if (relvar.equals("sys.Catalog")) {
			tuples = DatabaseAbstractionLayer.getRelvarsWithoutRevExtensions(connection, where);
		}
		//Views
		else if (relvar.equals("sys.rev.View")) {
			tuples = DatabaseAbstractionLayer.getViews(connection, where);
			relvarFlag = false;
		}
		//Iterate list
		if (tuples != null) {
			Iterator<Tuple> it = tuples.iterator();
			while (it.hasNext()) {
				Tuple tuple = it.next();
				if (tuple != null) {
					MenuItem item = new MenuItem(subMenu, SWT.PUSH);
					item.setText(tuple.get("Name").toString());
					//Event handler
					SelectionListener listener;
					//Relvars
					if (relvarFlag) {
						listener = new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								menuAction(arg0);
							}
						};
					}
					//Views
					else {
						listener = new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent arg0) {
								viewCombo(arg0);
							}
						};
					}
					item.addSelectionListener(listener);
				}
			}
		}
		return subMenu;
	}
	
	public void createNewRelvarVisualier(String name) {
		Visualiser relvar = new VisualiserOfRelvar(this, name);
		relvar.setLocation(50, 50);
		updateMenus();
	}
	
	public void deleteVisualiser(Visualiser connected, Visualiser deleteOperator) {
		if (connected == null) {
			return;
		}
		model.removeVisualiser(connected);
		model.removeVisualiser(deleteOperator);
		updateMenus();
	}
	
	public void removeVisualiser(Visualiser visualiser, boolean transfer) {
		visualiser.setVisible(false);
		model.removeVisualiser(visualiser, transfer);
		updateMenus();
	}
	
	public VisualiserOfView createView(int x, int y, Point size, String uniqueNumber) {
		//Don't create view visualiser when rev extension are not installed
		if (hasRevExtensions() == -1) {
			return null;
		}
		//If there is no unique number specified, create one
		if (uniqueNumber == null) {
			uniqueNumber = "View" + Long.toString(getUniqueNumber());
		}
		//Create a normal and a small view
		VisualiserOfView visualiser = new VisualiserOfView(this, "View", uniqueNumber, x, y, size.x, size.y, true);
		VisualiserOfMinimizedView small = new VisualiserOfMinimizedView(this, uniqueNumber);
		small.setVisible(false);
		//Link to each other
		visualiser.setMinimized(small);
		small.setView(visualiser);
		updateMenus();
		return visualiser;
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
				"DELETE / DROP"
			};
		return operators;
	}
	
	private Visualiser getVisualiserForOperator(String id) {
		if (queryOperators.length == 0) {
			return null;
		}
		switch (id) {
			case "Project":
				return new VisualiserOfOperatorProject(this);
			case "Restrict WHERE":
				return new VisualiserOfOperatorRestrict(this);
			case "UNION":
				return new VisualiserOfOperatorDiyadic(this, id);
			case "RENAME":
				return new VisualiserOfOperatorRename(this);
			case "D_UNION":
				return null;
			case "INTERSECT":
				return new VisualiserOfOperatorDiyadic(this, id);
			case "MINUS":
				return new VisualiserOfOperatorDiyadic(this, id);
			case "PRODUCT":
				return null;
			case "DIVIDEBY":
				return null;
			case "JOIN":
				return new VisualiserOfOperatorDiyadic(this, id);
			case "COMPOSE":
				return new VisualiserOfOperatorDiyadic(this, id);
			case "MATCHING (SEMIJOIN)":
				return new VisualiserOfOperatorDiyadic(this, "MATCHING");
			case "NOT MATCHING (SEMIMINUS)":
				return new VisualiserOfOperatorDiyadic(this, "NOT MATCHING");
			case "EXTEND":
				return new VisualiserOfOperatorExtend(this);
			case "GROUP":
				return new VisualiserOfOperatorGroup(this);
			case "UNGROUP":
				return new VisualiserOfOperatorUngroup(this);
			case "WRAP":
				return new VisualiserOfOperatorWrap(this);
			case "UNWRAP":
				return new VisualiserOfOperatorUnwrap(this);
			case "TCLOSE":
				return null;
			case "ORDER":
				return new VisualiserOfOperatorOrder(this);
			case "SUMMARIZE":
				return new VisualiserOfOperatorSummarize(this);
			case "DEE (TABLE_DEE)":
				return new VisualiserOfTableDee(this);
			case "DUM (TABLE_DUM)":
				return new VisualiserOfTableDum(this);
			case "DELETE / DROP":
				return new VisualiserOfOperatorDelete(this); 
		}
		return null;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Model getModel() {
		return model;
	}
	
	public void setDetailView(Composite detail) {
//		detailView.removeAll();
		detailView.setLayoutData(BorderLayout.CENTER);
		detailView.redraw();
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
			String modelName = tuple.get("model").toString();
			//Reject any relvars which are contained within stored views
			String rejectQuery = "sys.rev.View WHERE Name = '" + modelName + "'";
			boolean stored = false;
			for (Tuple view: DatabaseAbstractionLayer.evaluate(connection, rejectQuery)) {
				stored = view.get("stored").toBoolean();
			}
			if (stored) {
				continue;
			}
			// Create a new relvar
			Visualiser relvar = new VisualiserOfRelvar(this, tuple.get("relvarName").toString());
			relvar.setVisualiserName(tuple.get("Name").toString());
			// Set up its position
			if (xpos == -1 && ypos == -1) {
				relvar.setLocation(nextX, nextY);
				nextY += relvar.getBounds().height + 10;
			} else {
				relvar.setLocation(xpos, ypos);
			}
			// Find a list of relvars to move
			for (VisualiserOfView vis: views) {
				if (modelName.equals(vis.getVisualiserName())) {
					vis.addTemp(relvar);
				}
			}
		}
	}
	
	private void presentQueriesWithRevExtensions(String where) {
		HashMap<String, LinkedList<Parameter>> unresolved = new HashMap<String, LinkedList<Parameter>>();
		//Load in the regular query visualisers
		for (Tuple tuple: DatabaseAbstractionLayer.getQueries(connection, where)) {
			String name = tuple.get("Name").toString();
			int xpos = tuple.get("xpos").toInt();
			int ypos = tuple.get("ypos").toInt();
			String kind = tuple.get("kind").toString();
			String modelName = tuple.get("model").toString();
			//Reject any queries which are contained within stored views
			String rejectQuery = "sys.rev.View WHERE Name = '" + modelName + "'";
			boolean stored = false;
			for (Tuple view: DatabaseAbstractionLayer.evaluate(connection, rejectQuery)) {
				stored = view.get("stored").toBoolean();
			}
			if (stored) {
				continue;
			}
			VisualiserOfOperator visualiser = getVisualiserForKind(kind, name, xpos, ypos);
			if (visualiser == null)
				continue;
			//Add the query to the view model
			for (VisualiserOfView vis: views) {
				if (modelName.equals(vis.getVisualiserName())) {
					vis.addTemp(visualiser);
				}
			}
			LinkedList<Parameter> unresolvedParms = unresolved.get(name);
			if (unresolvedParms != null) {
				unresolved.remove(name);
				for (Parameter parm: unresolvedParms) {
					parm.removeConnections();
					new Argument(parm, visualiser, Argument.ARROW_FROM_VISUALISER);
				}
			}
			for (Tuple connection: (Tuples)tuple.get("connections")) {
				int parameterNumber = connection.get("parameter").toInt();
				Parameter parameter = visualiser.getParameter(parameterNumber);
				String visualiserName = connection.get("Name").toString();
				Visualiser operand = model.getVisualiser(visualiserName);
				if (operand != null) {
					parameter.removeConnections();
					new Argument(parameter, operand, Argument.ARROW_FROM_VISUALISER);
				} else {
					LinkedList<Parameter> unresolvedParameters = unresolved.get(visualiserName);
					if (unresolvedParameters == null)
						unresolvedParameters = new LinkedList<Parameter>();
					unresolvedParameters.add(parameter);						
					unresolved.put(visualiserName, unresolvedParameters);
				}
			}
		}
		//Refresh the visualisers
		for (int v = 0; v < getModel().getVisualiserCount(); v++) {
			Visualiser vis = getModel().getVisualiser(v);
			if (vis instanceof VisualiserOfRelvar)
				((VisualiserOfRelvar)vis).refresh();
		}
	}
	
	private void presentViewsWithRevExtensions(String where) {
		//Only initialise at start up
		boolean update = false;
		if (where.length() > 0) {
			update = true;
		}
		if (update) {
			views = new LinkedList<VisualiserOfView>();
		}
		//Load in the view panels
		for (Tuple tuple: DatabaseAbstractionLayer.getViews(connection, where)) {
			String name = tuple.get("Name").toString();
			int xpos = tuple.get("xpos").toInt();
			int ypos = tuple.get("ypos").toInt();
			int width = tuple.get("width").toInt();
			int height = tuple.get("height").toInt();
			boolean enabled = tuple.get("enabled").toBoolean();
			//Create normal view
			VisualiserOfView visualiser = new VisualiserOfView(this, "View", name, xpos, ypos, width, height, enabled);
			views.add(visualiser);
			//Create minimized view
			VisualiserOfMinimizedView small = new VisualiserOfMinimizedView(this, name);
			//Give each other a reference
			visualiser.setMinimized(small);
			small.setView(visualiser);
			//Set which is visible
			if (enabled) {
				visualiser.setVisible(true);
				small.setVisible(false);
			} else {
				visualiser.setVisible(false);
				small.setVisible(true);
			}
			//Update
			if (update) {
				visualiser.updatePositionInDatabase();
				updateMenus();
			}
		}
	}
	
	private void presentWithRevExtensions() {
		presentViewsWithRevExtensions("stored = false");
		presentRelvarsWithRevExtensions("");
		presentQueriesWithRevExtensions("");
		// Add the relvar to the view model
		for (VisualiserOfView vis: views) {
			vis.commitTempList();
		}
		redraw();
	}
	
	// Return version number of Rev extensions.  Return -1 if not installed.
	private int hasRevExtensions() {
		return DatabaseAbstractionLayer.hasRevExtensions(connection);
	}
	
	private boolean installRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.installRevExtensions(connection);
		if (pass) {
			updateMenus();
		}
		return pass;
	}

	private boolean removeRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.removeRevExtensions(connection);
		if (pass) {
			updateMenus();
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
		// Refresh the model
		model.refresh();
		// Refresh the combo boxes
		updateMenus();
	}
	
	private void upgrade(int currentVersionOfRevFromDatabase) {
		// Perform upgrade from currentVersionOfRevFromDatabase to EXPECTED_REV_VERSION
	}
	
}
