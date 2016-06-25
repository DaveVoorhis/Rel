package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.graphics.Image;

public class DbTreeItem {
	private DbTreeAction player;
	private DbTreeAction creator;
	private DbTreeAction dropper;
	private DbTreeAction designer;
	private String section;
	private String name;

	DbTreeItem(String section, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer, String name) {
		this.section = section;
		this.player = player;
		this.creator = creator;
		this.dropper = dropper;
		this.designer = designer;
		this.name = name;
	}
	
	DbTreeItem(String section, DbTreeAction player, DbTreeAction creator, DbTreeAction dropper, DbTreeAction designer) {
		this(section, player, creator, dropper, designer, null);
	}
	
	DbTreeItem() {
		this(null, null, null, null, null, null);
	}
	
	public DbTreeItem(DbTreeItem item, String name) {
		this(item.section, item.player, item.creator, item.dropper, item.designer, name);
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
