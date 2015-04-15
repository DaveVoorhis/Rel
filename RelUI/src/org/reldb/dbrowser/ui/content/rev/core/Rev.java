package org.reldb.dbrowser.ui.content.rev.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Model;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.connection.CrashHandler;

public class Rev extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Connection connection;
	private Model model;
	private JPanel detailView;
	private VisualiserOfTuples tuples;
	private VisualiserOfNewRelvar newRelvar;
	private JSplitPane revPane;
	private JToolBar controlPanel;
	private JMenuBar menuBar;
	private JMenu systemRelvars;
	private JMenu customRelvars;
	private JMenu viewMenu;
	private JPopupMenu popup;
	private MouseListener popupListener;
	private String[] queryOperators;
	private LinkedList<VisualiserOfView> views;
	private CrashHandler crashHandler;
	
	private int popupX;
	private int popupY;
	
	private class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    private void maybeShowPopup(MouseEvent e) {
			if (getModel().getVisualiserUnderMouse(e) == null) {
				return;
			}
	        if (e.isPopupTrigger()) {
	        	popupX = e.getX();
	        	popupY = e.getY();
	            popup.show(e.getComponent(), popupX, popupY);
	        }
	    }
	}
	
	private static abstract class Item {
		public Item(JPopupMenu parent, String title) {
			JMenuItem menuItem = new JMenuItem(title);
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					run();
				}
			});
			parent.add(menuItem);
		}
		public abstract void run();
	}
	
	public JPopupMenu getPopup() {
		return popup;
	}

	private long getUniqueNumber() {
		return DatabaseAbstractionLayer.getUniqueNumber(connection);
	}
	
	public CrashHandler getCrashHandler() {
		return crashHandler;
	}
	
	public void createTuplesVisualiser(String query, String name) {
		tuples = new VisualiserOfTuples(Rev.this, "VisTuples", "VisTuples" + getUniqueNumber(), 0, 0);
		if (tuples != null) {
			tuples.setSize(detailView.getSize());
			tuples.setQuery(query, name);
			tuples.createNew();
		}
	}
	
	private JPopupMenu getPopupMenu() {
	    final JPopupMenu popup = new JPopupMenu();
	    new Item(popup, "Delete Selected") {
	    	public void run() {
	    		getModel().doSelectedDelete();
	    	}
	    };
	    return popup;
	}
	
	private VisualiserOfOperator getVisualiserForKind(String kind, String name, int xpos, int ypos) {
		VisualiserOfOperator visualiser = null;
		if (kind.equals("Project")) {
			visualiser = new VisualiserOfOperatorProject(this, kind, name, xpos, ypos);
		} else if (kind.equals("Restrict")) {
			visualiser = new VisualiserOfOperatorRestrict(this, kind, name, xpos, ypos);
		} else if (kind.equals("UNION")) {
			visualiser = new VisualiserOfOperatorUnion(this, kind, name, xpos, ypos);
		} else if (kind.equals("RENAME")) {
			visualiser = new VisualiserOfOperatorRename(this, kind, name, xpos, ypos);
		} else if (kind.equals("INTERSECT")) {
			visualiser = new VisualiserOfOperatorIntersect(this, kind, name, xpos, ypos);
		} else if (kind.equals("MINUS")) {
			visualiser = new VisualiserOfOperatorMinus(this, kind, name, xpos, ypos);
		} /*else if (kind.equals("PRODUCT")) {
			visualiser = new VisualiserOfOperatorProduct(this, kind, name, xpos, ypos);
		} else if (kind.equals("DIVIDEBY")) {
			visualiser = new VisualiserOfOperatorDivideby(this, kind, name, xpos, ypos);
		}*/ else if (kind.equals("JOIN")) {
			visualiser = new VisualiserOfOperatorJoin(this, kind, name, xpos, ypos);
		} else if (kind.equals("COMPOSE")) {
			visualiser = new VisualiserOfOperatorCompose(this, kind, name, xpos, ypos);
		} else if (kind.equals("MATCHING")) {
			visualiser = new VisualiserOfOperatorMatching(this, kind, name, xpos, ypos);
		} else if (kind.equals("NOT MATCHING")) {
			visualiser = new VisualiserOfOperatorNotMatching(this, kind, name, xpos, ypos);
		} else if (kind.equals("ORDER")) {
			visualiser = new VisualiserOfOperatorOrder(this, kind, name, xpos, ypos);
		} else if (kind.equals("GROUP")) {
			visualiser = new VisualiserOfOperatorGroup(this, kind, name, xpos, ypos);
		} else if (kind.equals("UNGROUP")) {
			visualiser = new VisualiserOfOperatorUngroup(this, kind, name, xpos, ypos);
		} else if (kind.equals("WRAP")) {
			visualiser = new VisualiserOfOperatorWrap(this, kind, name, xpos, ypos);
		} else if (kind.equals("UNWRAP")) {
			visualiser = new VisualiserOfOperatorUnwrap(this, kind, name, xpos, ypos);
		} else if (kind.equals("EXTEND")) {
			visualiser = new VisualiserOfOperatorExtend(this, kind, name, xpos, ypos);
		} else if (kind.equals("SUMMARIZE")) {
			visualiser = new VisualiserOfOperatorSummarize(this, kind, name, xpos, ypos);
		}
		else {
			System.out.println("Query kind '" + kind + "' not recognised.");
		}
		return visualiser;
	}
	
	public Rev(String dbURL, CrashHandler crashHandler) {
		setLayout(new BorderLayout());
		this.crashHandler = crashHandler;
		model = new Model();
		model.setRev(this);
		try {
			connection = new Connection(dbURL, false, crashHandler, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		revPane = new JSplitPane(); 
		revPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		revPane.setTopComponent(model);
		controlPanel = new JToolBar();
		controlPanel.setFloatable(false);
		detailView = new JPanel();
		detailView.setLayout(new BorderLayout());
		detailView.add(new JPanel(), BorderLayout.CENTER);
		revPane.setBottomComponent(detailView);
		revPane.setResizeWeight(0.8);
		add(controlPanel, BorderLayout.NORTH);
		add(revPane, BorderLayout.CENTER);
		popup = getPopupMenu();
		popupListener = new PopupListener();
		model.getModelPane().addMouseListener(popupListener);
		setupControlPanel();
	}
	
	public void setupControlPanel() {
		controlPanel.removeAll();
		JPanel menus = new JPanel();
		//menus.setSize(new Dimension(controlPanel.getWidth(), 32));
		menuBar = new JMenuBar();
		//System relvars
		systemRelvars = new JMenu("Insert System Relvar");
		updateComboBox(systemRelvars, "sys.Catalog", "Owner = 'Rel'");
		menuBar.add(systemRelvars);
		systemRelvars.setBorder(BorderFactory.createBevelBorder(0));
		//Custom relvars
		customRelvars = new JMenu("Insert Custom Relvar");
		updateComboBox(customRelvars, "sys.Catalog", "Owner = 'User'");
		menuBar.add(customRelvars);
		customRelvars.setBorder(BorderFactory.createBevelBorder(0));
		//Operators
		queryOperators = getOperators();
		JMenu insOperators = new JMenu("Insert Operator");
		for (int i=0; i < queryOperators.length; i++) {
			JMenuItem item = new JMenuItem(queryOperators[i]);
			insOperators.add(item);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					insertOperator(arg0);
				}
			});
		}
		insOperators.setBorder(BorderFactory.createBevelBorder(0));
		menuBar.add(insOperators);
		//Views
		viewMenu = new JMenu("Insert View");
		if (hasRevExtensions() != -1) {
			updateComboBox(viewMenu, "sys.rev.View", "stored = true");
		}
		menuBar.add(viewMenu);
		viewMenu.setBorder(BorderFactory.createBevelBorder(0));
		//Add the drop downs to the menu bar
		menus.add(menuBar);
		
		//Create relvar
		JButton addNewRelation = addButton("Create Relvar");
		menus.add(addNewRelation);
		addNewRelation.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newRelvar = new VisualiserOfNewRelvar(Rev.this, "NewRelvar", "NewRelvar" + getUniqueNumber(), 0, 0);
				newRelvar.setSize(detailView.getSize());
			}
		});
		//Refresh button
		JButton refreshRev = addButton("Refresh");
		menus.add(refreshRev);
		refreshRev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		//Clear button
		JButton clearRev = addButton("Clear");
		menus.add(clearRev);
		clearRev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Remove the visualisers
				model.removeEverything();
				model.removeEverything();	//This line twice to make sure views with content
											//get removed correctly
				//Refresh combo boxes
				updateComboBoxes();
			}
		});
		//Uninstall button
		JButton uninstallRev = addButton("Uninstall");
		menus.add(uninstallRev );
		uninstallRev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				uninstall();
			}
		});
		//Add the menu panel to the system
		controlPanel.add(menus);
	}
	
	private JButton addButton(String name) {
		JButton button = new JButton(name);
		Color bgColor = new Color(238, 238, 238);
		int boxHeight = 50;
		button.setBackground(bgColor);
		button.setBorder(BorderFactory.createBevelBorder(0));
		button.setMinimumSize(new Dimension(button.getWidth() + 20, boxHeight));
		return button;
	}
	
	private void insertOperator(ActionEvent e) {
		JMenuItem caller = (JMenuItem)e.getSource();
		getVisualiserForOperator(caller.getText());
	}
	
	private void menuAction(ActionEvent e) {
		JMenuItem caller = (JMenuItem)e.getSource();
		String name = caller.getText();
		Visualiser relvar = new VisualiserOfRelvar(Rev.this, name);
		relvar.setLocation(50, 50);
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
	
	private void viewCombo(ActionEvent e) {
		JMenuItem caller = (JMenuItem)e.getSource();
		String name = caller.getText();
		//Create view
		presentViewsWithRevExtensions("Name = '" + name + "'");
		//Add in the contained relvars
		presentRelvarsWithRevExtensions("model = '" + name + "'");
		//Add in the contained queries
		presentQueriesWithRevExtensions("model = '" + name + "'");
		//Update view with relvars
		for (VisualiserOfView view: views) {
			if (view.getName().equals(name)) {
				view.commitTempList();
			}
		}
	}
	
	private void updateComboBoxes() {
		updateComboBox(systemRelvars, "sys.Catalog", "Owner = 'Rel'");
		updateComboBox(customRelvars, "sys.Catalog", "Owner = 'User'");
		if (hasRevExtensions() != -1) {
			updateComboBox(viewMenu, "sys.rev.View", "stored = true");
		}
	}
	
	private void updateComboBox(JMenu box, String relvar, String where) {
		if (box == null) {
			return;
		}
		boolean relvarFlag = true;
		box.removeAll();
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
			try {
				while (it.hasNext()) {
					Tuple tuple = it.next();
					if (tuple != null) {
						JMenuItem item = new JMenuItem(tuple.get("Name").toString());
						//Event handler
						ActionListener listener;
						//Relvars
						if (relvarFlag) {
							listener = new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									menuAction(arg0);
								}
							};
						}
						//Views
						else {
							listener = new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									viewCombo(arg0);
								}
							};
						}
						item.addActionListener(listener);
						box.add(item);
					}
				}
			} finally {
				tuples.close();
			}
		}
	}
	
	public void createNewRelvarVisualier(String name) {
		Visualiser relvar = new VisualiserOfRelvar(Rev.this, name);
		relvar.setLocation(50, 50);
		updateComboBoxes();
	}
	
	public void deleteVisualiser(Visualiser connected, Visualiser deleteOperator) {
		if (connected == null) {
			return;
		}
		model.removeVisualiser(connected);
		model.removeVisualiser(deleteOperator);
		updateComboBoxes();
	}
	
	public void removeVisualiser(Visualiser visualiser, boolean transfer) {
		visualiser.setVisible(false);
		model.removeVisualiser(visualiser, transfer);
		updateComboBoxes();
	}
	
	public VisualiserOfView createView(int x, int y, Dimension size, String uniqueNumber) {
		//Don't create view visualiser when rev extension are not installed
		if (hasRevExtensions() == -1) {
			return null;
		}
		//If there is no unique number specified, create one
		if (uniqueNumber == null) {
			uniqueNumber = "View" + Long.toString(getUniqueNumber());
		}
		//Create a normal and a small view
		VisualiserOfView visualiser = new VisualiserOfView(Rev.this, "View", uniqueNumber, x, y, size.width, size.height, true);
		VisualiserOfMinimizedView small = new VisualiserOfMinimizedView(Rev.this, uniqueNumber);
		small.setVisible(false);
		//Link to each other
		visualiser.setMinimized(small);
		small.setView(visualiser);
		updateComboBoxes();
		return visualiser;
	}
	
	private String[] getOperators() {
		String[] operators = { "Project",
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
		popupX = 400;
		popupY = 50;
		switch (id) {
		case "Project":
			return new VisualiserOfOperatorProject(Rev.this, "Project", "Project" + getUniqueNumber(), popupX, popupY);
		case "Restrict WHERE":
			return new VisualiserOfOperatorRestrict(Rev.this, "Restrict", "Restrict" + getUniqueNumber(), popupX, popupY);
		case "UNION":
			return new VisualiserOfOperatorUnion(Rev.this, "UNION", "UNION" + getUniqueNumber(), popupX, popupY);
		case "RENAME":
			return new VisualiserOfOperatorRename(Rev.this, "RENAME", "RENAME" + getUniqueNumber(), popupX, popupY);
		case "D_UNION":
			return null;
		case "INTERSECT":
			return new VisualiserOfOperatorIntersect(Rev.this, "INTERSECT", "INTERSECT" + getUniqueNumber(), popupX, popupY);
		case "MINUS":
			return new VisualiserOfOperatorMinus(Rev.this, "MINUS", "MINUS" + getUniqueNumber(), popupX, popupY);
		case "PRODUCT":
			return null;
		case "DIVIDEBY":
			return null;
		case "JOIN":
			return new VisualiserOfOperatorJoin(Rev.this, "JOIN", "JOIN" + getUniqueNumber(), popupX, popupY);
		case "COMPOSE":
			return new VisualiserOfOperatorCompose(Rev.this, "COMPOSE", "COMPOSE" + getUniqueNumber(), popupX, popupY);
		case "MATCHING (SEMIJOIN)":
			return new VisualiserOfOperatorMatching(Rev.this, "MATCHING", "MATCHING" + getUniqueNumber(), popupX, popupY);
		case "NOT MATCHING (SEMIMINUS)":
			return new VisualiserOfOperatorNotMatching(Rev.this, "NOT MATCHING", "NOT MATCHING" + getUniqueNumber(), popupX, popupY);
		case "EXTEND":
			return new VisualiserOfOperatorExtend(Rev.this, "EXTEND", "EXTEND" + getUniqueNumber(), popupX, popupY);
		case "GROUP":
			return new VisualiserOfOperatorGroup(Rev.this, "GROUP", "GROUP" + getUniqueNumber(), popupX, popupY);
		case "UNGROUP":
			return new VisualiserOfOperatorUngroup(Rev.this, "UNGROUP", "UNGROUP" + getUniqueNumber(), popupX, popupY);
		case "WRAP":
			return new VisualiserOfOperatorWrap(Rev.this, "WRAP", "WRAP" + getUniqueNumber(), popupX, popupY);
		case "UNWRAP":
			return new VisualiserOfOperatorUnwrap(Rev.this, "UNWRAP", "UNWRAP" + getUniqueNumber(), popupX, popupY);
		case "TCLOSE":
			return null;
		case "ORDER":
			return new VisualiserOfOperatorOrder(Rev.this, "ORDER", "ORDER" + getUniqueNumber(), popupX, popupY);
		case "SUMMARIZE":
			return new VisualiserOfOperatorSummarize(Rev.this, "SUMMARIZE", "SUMMARIZE" + getUniqueNumber(), popupX, popupY);
		case "DEE (TABLE_DEE)":
			return new VisualiserOfTableDee(Rev.this, "DEE", "DEE" + getUniqueNumber(), popupX, popupY);
		case "DUM (TABLE_DUM)":
			return new VisualiserOfTableDum(Rev.this, "DUM", "DUM" + getUniqueNumber(), popupX, popupY);
		case "DELETE / DROP":
			return new VisualiserOfOperatorDelete(Rev.this, "DELETE", "DELETE" + getUniqueNumber(), popupX, popupY); 
		}
		return null;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public Model getModel() {
		return model;
	}
	
	public void setDetailView(JComponent detail) {
		detailView.removeAll();
		detailView.add(detail, BorderLayout.CENTER);
		detailView.validate();
	}
	
	private void presentRelvarsWithRevExtensions(String where) {
		int nextX = 10;
		int nextY = 10;
		for (Tuple tuple: DatabaseAbstractionLayer.getRelvars(connection, where)) {
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
			//Create a new relvar
			Visualiser relvar = new VisualiserOfRelvar(this, tuple.get("Name").toString());
			//Set up its position
			if (xpos == -1 && ypos == -1) {
				relvar.setLocation(nextX, nextY);
				nextY += relvar.getHeight() + 10;
			} else {
				relvar.setLocation(xpos, ypos);
			}
			//Find a list of relvars to move
			for (VisualiserOfView vis: views) {
				if (modelName.equals(vis.getName())) {
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
				if (modelName.equals(vis.getName())) {
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
				updateComboBoxes();
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
		validate();
	}
	
	// Return version number of Rev extensions.  Return -1 if not installed.
	private int hasRevExtensions() {
		return DatabaseAbstractionLayer.hasRevExtensions(connection);
	}
	
	private boolean installRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.installRevExtensions(connection);
		if (pass) {
			updateComboBoxes();
		}
		return pass;
	}

	private boolean removeRevExtensions() {
		boolean pass = DatabaseAbstractionLayer.removeRevExtensions(connection);
		if (pass) {
			updateComboBoxes();
		}
		return pass;
	}
	
	private void uninstall() {
		if (hasRevExtensions() < 0) {
			JOptionPane.showMessageDialog(this, "Rev is not installed.", "Rev", JOptionPane.INFORMATION_MESSAGE);
		}
		if (JOptionPane.showConfirmDialog(this, "Uninstall Rev?", "Rev", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return;
		if (removeRevExtensions()) {
			refresh();
			setVisible(false);
			setVisible(true);
		}
	}
	
	private void setup() {
		model.removeEverything();
		int version = hasRevExtensions();
		if (version < 0) {
			add(getRevExtensionInstallPanel(), BorderLayout.EAST);
			//presentWithoutRevExtensions();
			return;
		} else if (version < DatabaseAbstractionLayer.EXPECTED_REV_VERSION) {
			upgrade(version);
		}
		presentWithRevExtensions();
	}
	
	public void refresh() {
		//Refresh the model
		model.refresh();
		//Refresh the combo boxes
		updateComboBoxes();
	}
	
	public void go() {
		setup();
	}
	
	private JPanel getRevExtensionInstallPanel() {
		final JPanel installationControlPanel = new JPanel();
		installationControlPanel.setLayout(new FlowLayout());
		JButton installBtn = new JButton("Install Rev extensions");
		installBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (installRevExtensions())
					installationControlPanel.setVisible(false);
				refresh();
			}
		});
		installationControlPanel.add(installBtn);
		JButton noinstallBtn = new JButton("Do not install Rev extensions");
		noinstallBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				installationControlPanel.setVisible(false);
			}
		});
		installationControlPanel.add(noinstallBtn);
		return installationControlPanel;
	}
	
	private void upgrade(int currentVersionOfRevFromDatabase) {
		// Perform upgrade from currentVersionOfRevFromDatabase to EXPECTED_REV_VERSION
	}
	
}
