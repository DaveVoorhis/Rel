package org.reldb.dbrowser.ui.content.rev;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.CommandActivator;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelOutput;
import org.reldb.dbrowser.ui.content.rev.operators.Comment;
import org.reldb.dbrowser.ui.content.rev.operators.Diadic;
import org.reldb.dbrowser.ui.content.rev.operators.Expression;
import org.reldb.dbrowser.ui.content.rev.operators.Extend;
import org.reldb.dbrowser.ui.content.rev.operators.From;
import org.reldb.dbrowser.ui.content.rev.operators.GroupOrWrap;
import org.reldb.dbrowser.ui.content.rev.operators.Order;
import org.reldb.dbrowser.ui.content.rev.operators.Project;
import org.reldb.dbrowser.ui.content.rev.operators.Rename;
import org.reldb.dbrowser.ui.content.rev.operators.Restrict;
import org.reldb.dbrowser.ui.content.rev.operators.Summarize;
import org.reldb.dbrowser.ui.content.rev.operators.TableDee;
import org.reldb.dbrowser.ui.content.rev.operators.TableDum;
import org.reldb.dbrowser.ui.content.rev.operators.TupleFrom;
import org.reldb.dbrowser.ui.content.rev.operators.UngroupOrUnwrap;
import org.reldb.dbrowser.ui.content.rev.operators.Unorder;
import org.reldb.dbrowser.ui.content.rev.operators.Update;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class Rev extends Composite {
	public static final int NONE = 0;
	public static final int EDITABLE = 0;
	public static final int READONLY = 1;
	public static final int SAVE_AND_LOAD_BUTTONS = 2;

	private DbConnection connection;
	private Model model;
	private CmdPanelOutput outputView;
	private SashForm sashForm;
	private RevDatabase database;

	private PreferenceChangeListener preferenceChangeListener;

	private CommandActivator loadBtn = null;
	private CommandActivator saveBtn = null;
	private CommandActivator stopBtn = null;

	private Label modelLabel;

	private Vector<ModelChangeListener> modelChangeListeners = new Vector<ModelChangeListener>();

	private int revstyle;

	private Composite inputView;

	private DbTab dbTab;

	public Rev(Composite parent, DbTab dbTab, DbConnection connection, CrashHandler crashHandler, String modelName,
			int revstyle) {
		super(parent, SWT.None);

		this.dbTab = dbTab;

		this.connection = connection;
		this.revstyle = revstyle;

		database = new RevDatabase(connection);

		setLayout(new FillLayout());

		sashForm = new SashForm(this, SWT.NONE);
		sashForm.setOrientation(SWT.VERTICAL);

		try {
			outputView = new CmdPanelOutput(sashForm, connection, CmdPanelOutput.SHOW_SERVER_RESPONSE) {
				@Override
				protected void changeToolbar() {
					Rev.this.changeToolbar();
				}

				@Override
				protected void notifyInputDone() {
					stopBtn.setEnabled(false);
				}

				@Override
				public void go(String text, boolean copyInputToOutput) {
					if (outputView.getRelvarEditorView() != null)
						outputView.removeRelvarEditorView();
					stopBtn.setEnabled(true);
					super.go(text, copyInputToOutput);
				}

				@Override
				protected void zoom() {
					if (sashForm.getMaximizedControl() == null)
						sashForm.setMaximizedControl(inputView);
					else if (sashForm.getMaximizedControl() == inputView)
						sashForm.setMaximizedControl(outputView);
					else
						sashForm.setMaximizedControl(null);
				}
			};
		} catch (Exception e) {
			System.out.println("Rev: Unable to open output panel.");
			e.printStackTrace();
		}

		inputView = new Composite(sashForm, SWT.NONE);
		inputView.setLayout(new FormLayout());

		inputView.addListener(SWT.FocusIn, e -> setTitle());

		ToolBar revTools = new ToolBar(inputView, SWT.NONE);

		if ((revstyle & SAVE_AND_LOAD_BUTTONS) != 0) {
			loadBtn = new CommandActivator(null, revTools, SWT.PUSH);
			loadBtn.setToolTipText("Load");
			loadBtn.addListener(SWT.Selection, e -> doLoad());

			saveBtn = new CommandActivator(null, revTools, SWT.PUSH);
			saveBtn.setToolTipText("Save as");
			saveBtn.addListener(SWT.Selection, e -> doSaveAs());
		}

		stopBtn = new CommandActivator(null, revTools, SWT.PUSH);
		stopBtn.setToolTipText("Cancel running query.");
		stopBtn.addListener(SWT.Selection, e -> outputView.notifyStop());
		stopBtn.setEnabled(false);

		modelLabel = new Label(inputView, SWT.NONE);
		modelLabel.setAlignment(SWT.CENTER);

		ScrolledComposite scrollPanel = new ScrolledComposite(inputView, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		FormData fd_revTools = new FormData();
		fd_revTools.left = new FormAttachment(0);
		fd_revTools.top = new FormAttachment(0);
		revTools.setLayoutData(fd_revTools);

		FormData fd_modelLabel = new FormData();
		fd_modelLabel.left = new FormAttachment(0);
		fd_modelLabel.top = new FormAttachment(0);
		fd_modelLabel.right = new FormAttachment(100);
		fd_modelLabel.bottom = new FormAttachment(scrollPanel);
		modelLabel.setLayoutData(fd_modelLabel);

		FormData fd_scrollPanel = new FormData();
		fd_scrollPanel.left = new FormAttachment(0);
		fd_scrollPanel.top = new FormAttachment(revTools);
		fd_scrollPanel.right = new FormAttachment(100);
		fd_scrollPanel.bottom = new FormAttachment(100);
		scrollPanel.setLayoutData(fd_scrollPanel);

		model = new Model(this, modelName, scrollPanel);
		model.setSize(10000, 10000);

		scrollPanel.setContent(model);
		scrollPanel.setExpandHorizontal(true);
		scrollPanel.setExpandVertical(true);
		scrollPanel.setMinSize(model.getSize());

		sashForm.setWeights(new int[] { 1, 1 });

		setupIcons();

		preferenceChangeListener = new PreferenceChangeAdapter("Rev") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, preferenceChangeListener);

		pack();

		loadModel();
	}

	public DbTab getDbTab() {
		return dbTab;
	}

	/**
	 * Invoke to force toolbar holder to reload our toolbar, which has probably
	 * changed.
	 */
	protected void changeToolbar() {
	}

	/**
	 * Invoke to force holder of this widget to reload any display of the database
	 * catalog, which has changed.
	 */
	protected void changeCatalog(String category, String name) {
	}

	public void showEditorFor(String title) {
		outputView.useRelvarEditorView(connection, title, SWT.BORDER);
	}

	private void setTitle() {
		String title;
		if (isReadOnly())
			title = "Showing " + model.getModelName()
					+ ".  Queries may be evaluated but not be edited. Use Design view to edit.";
		else
			title = "Edit " + model.getModelName() + ".  Right-click for options.";
		if (database.hasRevExtensions() < 0)
			title += " WARNING: Rev extensions are not installed, so nothing will be saved!";
		if (isDisposed() || modelLabel.isDisposed())
			return;
		modelLabel.setText(title);
	}

	private void setZoomedParent(Composite parent, Composite setTo) {
		Composite parentparent = parent.getParent();
		if (parentparent instanceof SashForm) {
			SashForm parentSash = (SashForm) parentparent;
			parentSash.setMaximizedControl(setTo);
		}
	}

	private void zoomInParent() {
		Composite parent = getParent();
		setZoomedParent(parent, parent);
	}

	private void unzoomInParent() {
		Composite parent = getParent();
		setZoomedParent(parent, null);
	}

	public void zoom() {
		if (sashForm.getMaximizedControl() == null) {
			sashForm.setMaximizedControl(inputView);
			zoomInParent();
		} else if (sashForm.getMaximizedControl() == inputView) {
			sashForm.setMaximizedControl(outputView);
			zoomInParent();
		} else {
			sashForm.setMaximizedControl(null);
			unzoomInParent();
		}
	}

	public int getRevStyle() {
		return revstyle;
	}

	public boolean isReadOnly() {
		return (revstyle & READONLY) != 0;
	}

	/**
	 * Add listener to be notified when a newly-created Rev model gets its first
	 * Visualiser, or when it loses its last Visualiser.
	 */
	public void addModelChangeListener(ModelChangeListener modelChangeListener) {
		modelChangeListeners.add(modelChangeListener);
	}

	public void removeModelChangeListener(ModelChangeListener modelChangeListener) {
		modelChangeListeners.remove(modelChangeListener);
	}

	protected void doSaveAs() {
		if (database.hasRevExtensions() < 0) {
			MessageDialog.openError(this.getShell(), "Rev",
					"Rev extensions are not installed, so queries can't be saved.");
			return;
		}
		String oldName = model.getModelName();
		SaveQueryAsDialog saveAs = new SaveQueryAsDialog(getShell(), oldName);
		if (saveAs.open() == Dialog.OK) {
			String newName = saveAs.getName();
			if (newName.trim().length() == 0)
				return;
			if (oldName.equals(newName)) {
				MessageDialog.openInformation(getShell(), "No need to save",
						"No need to save.  Changes are automatically saved while you edit.");
				return;
			}
			if (database.modelExists(newName)) {
				if (!MessageDialog.openConfirm(getShell(), "Overwrite?",
						"A query named '" + newName + "' already exists.  Overwrite it?"))
					return;
			}
			if (saveAs.keepOriginal())
				database.modelCopyTo(oldName, newName);
			else
				database.modelRename(oldName, newName);
			model.setModelName(newName);
			modelLabel.setText(model.getModelName());
			fireModelChangeEvent();
		}
	}

	void fireModelChangeEvent() {
		for (ModelChangeListener listener : modelChangeListeners)
			listener.modelChanged();
	}

	protected void doLoad() {
		if (database.hasRevExtensions() < 0) {
			MessageDialog.openError(this.getShell(), "Rev",
					"Rev extensions are not installed, so queries can't be loaded.");
			return;
		}
		LoadQueryDialog load = new LoadQueryDialog(getShell(), database.getModels());
		if (load.open() == Dialog.OK && load.getSelectedItem() != null && load.getSelectedItem().trim().length() > 0) {
			model.setModelName(load.getSelectedItem());
			loadModel();
		}
	}

	private void setupIcons() {
		if (loadBtn != null)
			loadBtn.setImage(IconLoader.loadIcon("loadIcon"));
		if (saveBtn != null)
			saveBtn.setImage(IconLoader.loadIcon("saveIcon"));
		stopBtn.setImage(IconLoader.loadIcon("stopIcon"));
	}

	public CmdPanelOutput getCmdPanelOutput() {
		return outputView;
	}

	public RevDatabase getDatabase() {
		return database;
	}

	public long getUniqueNumber() {
		return database.getUniqueNumber();
	}

	public void refresh() {
		refreshMenus();
	}

	public Model getModel() {
		return model;
	}

	private void refreshMenus() {
		if (getMenu() != null)
			getMenu().dispose();

		Menu menuBar = new Menu(getShell(), SWT.POP_UP);
		if (!isReadOnly())
			model.setMenu(menuBar);

		// Custom relvars
		MenuItem customRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		customRelvarsItem.setText("Variables");
		customRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, false));

		// System relvars
		MenuItem systemRelvarsItem = new MenuItem(menuBar, SWT.CASCADE);
		systemRelvarsItem.setText("System variables");
		systemRelvarsItem.setMenu(obtainRelvarsMenu(menuBar, true));

		// Operators
		OpSelector[] queryOperators = getOperators();
		MenuItem insOperatorsItem = new MenuItem(menuBar, SWT.CASCADE);
		insOperatorsItem.setText("Operators and constants");
		Menu insOperatorsMenu = new Menu(menuBar);
		for (int i = 0; i < queryOperators.length; i++) {
			OpSelector selector = queryOperators[i];
			if (selector.toString() == null)
				new MenuItem(insOperatorsMenu, SWT.SEPARATOR);
			else {
				MenuItem item = new MenuItem(insOperatorsMenu, SWT.PUSH);
				item.setText(selector.getMenuTitle());
				item.addListener(SWT.Selection, e -> {
					Point lastMousePosition = model.getLastMousePosition();
					obtainOperatorForKind(selector.toString(), selector.toString() + getUniqueNumber(), lastMousePosition.x, lastMousePosition.y);
				});
			}
		}
		insOperatorsItem.setMenu(insOperatorsMenu);

		// Refresh
		MenuItem refreshRev = new MenuItem(menuBar, SWT.PUSH);
		refreshRev.setText("Refresh");
		refreshRev.addListener(SWT.Selection, e -> refreshMenus());

		// Clear
		MenuItem clearRev = new MenuItem(menuBar, SWT.PUSH);
		clearRev.setText("Clear");
		clearRev.addListener(SWT.Selection, e -> {
			if (!MessageDialog.openConfirm(getShell(), "Rev", "Remove everything from this query?"))
				return;
			model.removeEverything();
		});
	}

	private void loadModel() {
		model.clear();

		setTitle();

		refreshMenus();

		// load
		int version = hasRevExtensions();
		if (version >= 0) {
			if (version < RevDatabase.EXPECTED_REV_VERSION) {
				upgrade(version);
			} else {
				presentRelvars();
				presentQueries();
			}
		}
	}

	private Menu obtainRelvarsMenu(Menu parent, boolean systemOnly) {
		Menu subMenu = new Menu(parent);
		Tuples tuples = database.getRelvars();
		if (tuples != null) {
			Iterator<Tuple> it = tuples.iterator();
			while (it.hasNext()) {
				Tuple tuple = it.next();
				if (tuple != null) {
					String owner = tuple.get("Owner").toString();
					String name = tuple.get("Name").toString();
					boolean isSystemVar = owner.equals("Rel") || name.startsWith("sys.rev");
					if (systemOnly != isSystemVar)
						continue;
					MenuItem item = new MenuItem(subMenu, SWT.PUSH);
					item.setText(name);
					item.addListener(SWT.Selection, e -> {
						Point lastMousePosition = model.getLastMousePosition();
						new Relvar(Rev.this, name + getUniqueNumber(), name, lastMousePosition.x, lastMousePosition.y);
					});
				}
			}
		}
		return subMenu;
	}

	@FunctionalInterface
	private static interface OpSelectorRun {
		public Operator obtain(Rev rev, String name, int xpos, int ypos);
	}

	private static class OpSelector {
		private String menuTitle;
		private String opName;
		private OpSelectorRun run;

		public OpSelector(String menuTitle, String opName, OpSelectorRun run) {
			this.menuTitle = menuTitle;
			this.opName = opName;
			this.run = run;
		}
		
		public OpSelector(String menuTitle, OpSelectorRun run) {
			this(menuTitle, menuTitle, run);
		}

		public OpSelector() {
			this.menuTitle = null;
			this.opName = null;
			this.run = null;
		}

		public String getMenuTitle() {
			return menuTitle;
		}
		
		public String toString() {
			return opName;
		}

		public Operator getOperator(Rev rev, String name, int xpos, int ypos) {
			return run.obtain(rev, name, xpos, ypos);
		}
	}

	private OpSelector[] getOperators() {
		OpSelector[] operators = { 
			new OpSelector("Project {}", "Project", (rev, name, xpos, ypos) -> new Project(rev, name, xpos, ypos)), 
			new OpSelector("Restrict (WHERE)", "Restrict", (rev, name, xpos, ypos) -> new Restrict(rev, name, xpos, ypos)),
			new OpSelector(), new OpSelector("COMPOSE", (rev, name, xpos, ypos) -> new Diadic(rev, name, "COMPOSE", xpos, ypos)),
			// new OpSelector("DIVIDEBY", (rev, name, xpos, ypos) -> new VisualiserOfOperatorDivideby(rev, name, xpos, ypos)),
			new OpSelector("EXTEND", (rev, name, xpos, ypos) -> new Extend(rev, name, xpos, ypos)), 
			new OpSelector("GROUP", (rev, name, xpos, ypos) -> new GroupOrWrap(rev, name, "GROUP", xpos, ypos)), 
			new OpSelector("INTERSECT", (rev, name, xpos, ypos) -> new Diadic(rev, name, "INTERSECT", xpos, ypos)), 
			new OpSelector("JOIN", (rev, name, xpos, ypos) -> new Diadic(rev, name, "JOIN", xpos, ypos)), 
			new OpSelector("MATCHING", (rev, name, xpos, ypos) -> new Diadic(rev, name, "MATCHING", xpos, ypos)), 
			new OpSelector("MINUS", (rev, name, xpos, ypos) -> new Diadic(rev, name, "MINUS", xpos, ypos)), 
			new OpSelector("NOT MATCHING", (rev, name, xpos, ypos) -> new Diadic(rev, name, "NOT MATCHING", xpos, ypos)), 
			new OpSelector("ORDER", (rev, name, xpos, ypos) -> new Order(rev, name, xpos, ypos)), 
			new OpSelector("RENAME", (rev, name, xpos, ypos) -> new Rename(rev, name, xpos, ypos)), 
			new OpSelector("SUMMARIZE", (rev, name, xpos, ypos) -> new Summarize(rev, name, xpos, ypos)), 
			new OpSelector("TIMES", (rev, name, xpos, ypos) -> new Diadic(rev, name, "TIMES", xpos, ypos)), 
			new OpSelector("UNGROUP", (rev, name, xpos, ypos) -> new UngroupOrUnwrap(rev, name, "UNGROUP", "RELATION", xpos, ypos)), 
			new OpSelector("UNION", (rev, name, xpos, ypos) -> new Diadic(rev, name, "UNION", xpos, ypos)), 
			new OpSelector("UNORDER", (rev, name, xpos, ypos) -> new Unorder(rev, name, xpos, ypos)), 
			new OpSelector("UNWRAP", (rev, name, xpos, ypos) -> new UngroupOrUnwrap(rev, name, "UNWRAP", "TUPLE", xpos, ypos)), 
			new OpSelector("UPDATE", (rev, name, xpos, ypos) -> new Update(rev, name, xpos, ypos)), 
			new OpSelector("WRAP", (rev, name, xpos, ypos) -> new GroupOrWrap(rev, name, "WRAP", xpos, ypos)), 
			new OpSelector(), 
			new OpSelector("TABLE_DEE", (rev, name, xpos, ypos) -> new TableDee(rev, name, xpos, ypos)), 
			new OpSelector("TABLE_DUM", (rev, name, xpos, ypos) -> new TableDum(rev, name, xpos, ypos)), 
			new OpSelector(), 
			new OpSelector("=", (rev, name, xpos, ypos) -> new Diadic(rev, name, "=", xpos, ypos)), 
			new OpSelector("<>", (rev, name, xpos, ypos) -> new Diadic(rev, name, "<>", xpos, ypos)), 
			new OpSelector("<", (rev, name, xpos, ypos) -> new Diadic(rev, name, "<", xpos, ypos)), 
			new OpSelector(">", (rev, name, xpos, ypos) -> new Diadic(rev, name, ">", xpos, ypos)), 
			new OpSelector("<=", (rev, name, xpos, ypos) -> new Diadic(rev, name, "<=", xpos, ypos)), 
			new OpSelector(">=", (rev, name, xpos, ypos) -> new Diadic(rev, name, ">=", xpos, ypos)), 
			new OpSelector("IN", (rev, name, xpos, ypos) -> new Diadic(rev, name, "IN", xpos, ypos)), 
			new OpSelector(), 
			new OpSelector("FROM", (rev, name, xpos, ypos) -> new From(rev, name, xpos, ypos)), 
			new OpSelector("TUPLE FROM", (rev, name, xpos, ypos) -> new TupleFrom(rev, name, xpos, ypos)), 
			new OpSelector(),
			new OpSelector("Comment", (rev, name, xpos, ypos) -> new Comment(rev, name, xpos, ypos)),
			new OpSelector("Expression", (rev, name, xpos, ypos) -> new Expression(rev, name, xpos, ypos)) 
		};
		return operators;
	}

	private Operator obtainOperatorForKind(String kind, String name, int xpos, int ypos) {
		for (OpSelector selector : getOperators())
			if (selector.toString() != null && selector.toString().compareTo(kind) == 0)
				return selector.getOperator(Rev.this, name, xpos, ypos);
		System.out.println("Query kind '" + kind + "' not recognised.");
		return null;
	}

	private void presentRelvars() {
		int nextX = 10;
		int nextY = 10;
		Tuples tuples = database.getRelvars(model.getModelName());
		if (tuples == null)
			return;
		for (Tuple tuple : tuples) {
			int xpos = tuple.get("xpos").toInt();
			int ypos = tuple.get("ypos").toInt();
			// String modelName = tuple.get("model").toString();
			// Set up relvar position
			if (xpos == -1 && ypos == -1) {
				xpos = nextX;
				ypos = nextY;
			}
			// Create a new relvar
			Visualiser relvar = new Relvar(this, tuple.get("Name").toString(), tuple.get("relvarName").toString(), xpos,
					ypos);
			nextY += relvar.getBounds().height + 10;
		}
	}

	private void presentQueries() {
		HashMap<String, LinkedList<Parameter>> unresolved = new HashMap<String, LinkedList<Parameter>>();
		// Load in the regular query visualisers
		for (Tuple tuple : database.getQueries(model.getModelName())) {
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
				for (Parameter parm : unresolvedParms) {
					parm.getArgument().setOperand(operator);
				}
			}
			for (Tuple connection : (Tuples) tuple.get("connections")) {
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

	// Return version number of Rev extensions. Return -1 if not installed.
	private int hasRevExtensions() {
		return database.hasRevExtensions();
	}

	private void upgrade(int currentVersionOfRevFromDatabase) {
		// Perform upgrade from currentVersionOfRevFromDatabase to EXPECTED_REV_VERSION
	}

}
