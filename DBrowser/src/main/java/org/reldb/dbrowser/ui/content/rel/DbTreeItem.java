package org.reldb.dbrowser.ui.content.rel;

public class DbTreeItem {
	private DbTreeAction player;
	private DbTreeAction editor;
	private DbTreeAction creator;
	private DbTreeAction dropper;
	private DbTreeAction designer;
	private DbTreeAction renamer;
	private DbTreeAction exporter;
	private String section;
	private String name;

	DbTreeItem(String section, DbTreeAction player, DbTreeAction editor, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter, String name) {
		this.section = section;
		this.player = player;
		this.editor = editor;
		this.creator = creator;
		this.dropper = dropper;
		this.designer = designer;
		this.renamer = renamer;
		this.exporter = exporter;
		this.name = name;
	}
	
	DbTreeItem(String section, DbTreeAction player, DbTreeAction editor, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter) {
		this(section, player, editor, creator, dropper, designer, renamer, exporter, null);
	}
	
	DbTreeItem() {
		this(null, null, null, null, null, null, null, null, null);
	}
	
	public DbTreeItem(DbTreeItem item, String name) {
		this(item.section, item.player, item.editor, item.creator, item.dropper, item.designer, item.renamer, item.exporter, name);
	}

	public boolean canPlay() {
		return player != null;
	}

	public boolean canEdit() {
		return editor != null;
	}

	public boolean canCreate() {
		return creator != null;
	}

	public boolean canDrop() {
		return dropper != null;
	}

	public boolean canDesign() {
		return designer != null;
	}
	
	public boolean canRename() {
		return renamer != null;
	}

	public boolean canExport() {
		return exporter != null;
	}
	
	public void play(String imageName) {
		player.go(this, imageName);
	}

	public void edit(String imageName) {
		editor.go(this, imageName);
	}
	
	public void create(String imageName) {
		creator.go(this, imageName);
	}

	public void drop(String imageName) {
		dropper.go(this, imageName);
	}

	public void design(String imageName) {
		designer.go(this, imageName);
	}

	public void rename(String imageName) {
		renamer.go(this, imageName);
	}
	
	public void export(String imageName) {
		exporter.go(this, imageName);
	}
	
	public String getSection() {
		return section;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTabName() {
		if (section != null && name != null)
			return section + ": " + name;
		else if (section != null)
			return section;
		else
			return "<none>";		
	}
	
	public String toString() {
		return getTabName();
	}
	
};
