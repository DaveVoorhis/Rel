package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rev.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Rename extends OperatorWithControlPanel {
	
	private static class Renaming { 
		enum RenameType {NORMAL, PREFIX, SUFFIX};
		
		private RenameType type;
		private String from;
		private String to;
		
		Renaming(RenameType type, String from, String to) {
			this.type = type;
			this.from = from;
			this.to = to;
		}
		
		public Renaming(String attributeName) {
			this(RenameType.NORMAL, attributeName, "");
		}

		RenameType getType() {return type;}
		
		void setType(RenameType type) {this.type = type;}
		
		void setType(String txt) {
			if (txt.equals("PREFIX"))
				setType(RenameType.PREFIX);
			else if (txt.equals("SUFFIX"))
				setType(RenameType.SUFFIX);
			else
				setType(RenameType.NORMAL);
		}
		
		String getFrom() {return from;}
		
		void setFrom(String from) {this.from = from;}
		
		String getTo() {return to;}
		
		void setTo(String to) {this.to = to;}
		
		public String toString() {
			String out = "";
			switch (type) {
			case NORMAL: out = from + " AS " + to; break;
			case PREFIX: out = "PREFIX \"" + from + "\" AS \"" + to + "\""; break;
			case SUFFIX: out += "SUFFIX  \"" + from + "\" AS \"" + to + "\""; break;
			}
			return out;
		}
	}
	
	private Vector<Renaming> renamings;
	
	public Rename(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "RENAME", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
		load();
		pack();
	}
	
	private void load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateRename(getModel().getConnection(), getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}
	
	private void save() {
		DatabaseAbstractionLayer.updatePreservedStateRename(getModel().getConnection(), getID(), operatorLabel.getText());
	}
	
	private void addRow(Composite parent, Renaming r) {
		Combo renamingTypes = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		renamingTypes.add("");
		renamingTypes.add("PREFIX");
		renamingTypes.add("SUFFIX");
		renamingTypes.select(r.getType().ordinal());
		renamingTypes.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setType(renamingTypes.getText());
			}
		});
		
		Text from = new Text(parent, SWT.NONE);
		from.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		from.setText(r.getFrom());
		from.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setFrom(from.getText());
			}
		});

		Text to = new Text(parent, SWT.NONE);
		to.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		to.setText(r.getTo());		
		to.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				r.setTo(to.getText());
			}
		});
		
		renamings.add(r);
	}
	
	private Vector<Renaming> getDefinitionRenamings() {
		String definition = operatorLabel.getText().trim();
		Vector<Renaming> output = new Vector<Renaming>();
		if (definition.length() > 0) {
			String[] clauses = definition.split(",");
			for (String clauseRaw: clauses) {
				String clause = clauseRaw.trim();
				Renaming.RenameType type = Renaming.RenameType.NORMAL;
				if (clause.startsWith("PREFIX")) {
					type = Renaming.RenameType.PREFIX;
					clause = clause.substring("PREFIX ".length());
				} else if (clause.startsWith("SUFFIX")) {
					type = Renaming.RenameType.SUFFIX;
					clause = clause.substring("SUFFIX ".length());
				}
				String[] fromto = clause.split("AS");
				String from = fromto[0].trim();
				String to = fromto[1].trim();
				output.add(new Renaming(type, from, to));
			}
		}
		return output;
	}
	
	private Renaming findRenaming(String fromName, Vector<Renaming> renamings) {
		for (Renaming renaming: renamings)
			if (renaming.getFrom().equals(fromName))
				return renaming;
		return null;
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(3, false));
		
		renamings = new Vector<Renaming>();
		Vector<String> availableAttributes = getAttributesOfParameter(0);
		Vector<Renaming> definitionRenamings = getDefinitionRenamings();
		for (String attributeName: availableAttributes) {
			Renaming renaming = findRenaming(attributeName, definitionRenamings);
			if (renaming == null)
				addRow(container, new Renaming(attributeName));
			else
				addRow(container, renaming);
		}
	}

	public String getAttributeSpecification() {
		String attributeSpec = "";
		for (Renaming renaming: renamings) {
			if (renaming.getTo().trim().length() == 0)
				continue;
			if (attributeSpec.length() > 0)
				attributeSpec += ", ";
			attributeSpec += renaming.toString();
		}
		return attributeSpec;
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(getAttributeSpecification());
		save();
		pack();
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "(" + source + ") RENAME {" + operatorLabel.getText() + "}";		
	}

}
