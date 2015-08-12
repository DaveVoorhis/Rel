package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;

// IConfiguration for registering a UI binding to open a menu
class MenuConfiguration extends AbstractUiBindingConfiguration {
    private final Menu menu;
    private final String gridRegion;
 
    // gridRegion can be, for example, GridRegion.COLUMN_HEADER
    public MenuConfiguration(String gridRegion, PopupMenuBuilder menuBuilder) {
        this.gridRegion = gridRegion;
        // create the menu using the PopupMenuBuilder
        menu = menuBuilder.build();
    }
    
    @Override
    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
                new MouseEventMatcher(SWT.NONE, gridRegion, MouseEventMatcher.RIGHT_BUTTON),
                new PopupMenuAction(menu));
    }
}
