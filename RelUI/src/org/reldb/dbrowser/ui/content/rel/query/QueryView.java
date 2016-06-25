package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.ModelChangeListener;

public class QueryView extends DbTreeAction {
	
	private int revstyle;
	
	public QueryView(RelPanel relPanel, int revstyle) {
		super(relPanel);
		this.revstyle = revstyle;
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof RevTab) {
				RevTab revtab = (RevTab)tab;
				if (revtab.getRevStyle() != revstyle) {
					revtab.dispose();
					tab = null;
				}
			}
		}
		if (tab == null) {
			RevTab revtab = new RevTab(relPanel, item, revstyle);
			revtab.addModelChangeListener(new ModelChangeListener() {
				public void modelChanged() {
					relPanel.redisplayed();
				}
			});
			tab = revtab;
		}
		tab.setImage(image);
		relPanel.getTabFolder().setSelection(tab);
	}

}
