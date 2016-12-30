package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.graphics.Image;

public class DbTreeItem {
	private DbTreeAction player;
	private DbTreeAction creator;
	private DbTreeAction dropper;
	private DbTreeAction designer;
	private DbTreeAction renamer;
	private DbTreeAction exporter;
	private String section;
	private String name;

	DbTreeItem(String section, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter, String name) {
		this.section = section;
		this.player = player;
		this.creator = creator;
		this.dropper = dropper;
		this.designer = designer;
		this.renamer = renamer;
		this.exporter = exporter;
		this.name = name;
	}
	
	DbTreeItem(String section, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, DbTreeAction renamer, DbTreeAction exporter) {
		this(section, player, creator, dropper, designer, renamer, exporter, null);
	}
	
	DbTreeItem() {
		this(null, null, null, null, null, null, null, null);
	}
	
	public DbTreeItem(DbTreeItem item, String name) {
		this(item.section, item.player, item.creator, item.dropper, item.designer, item.renamer, item.exporter, name);
	}

	public boolean canPlay() {
		return player != null;
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
	
	public void play(Image image) {
		player.go(this, image);
	}
	
	public void create(Image image) {
		creator.go(this, image);
	}

	public void drop(Image image) {
		dropper.go(this, image);
	}

	public void design(Image image) {
		designer.go(this, image);
	}

	public void rename(Image image) {
		renamer.go(this, image);
	}
	
	public void export(Image image) {
		exporter.go(this, image);
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
