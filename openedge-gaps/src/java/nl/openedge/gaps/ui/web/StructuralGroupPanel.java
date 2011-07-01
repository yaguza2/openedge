/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.openedge.gaps.core.groups.StructuralGroup;
import wicket.markup.html.HtmlContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.table.ListItem;
import wicket.markup.html.table.ListView;


/**
 * Panel voor groepen.
 */
public class StructuralGroupPanel extends Panel
{
    /**
     * Construct.
     * @param componentName
     */
    public StructuralGroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);
        addStructGroupComponents(group);
    }

    /**
     * Voeg componenten voor structuurgroepen toe.
     * @param group de huidige structuurgroep
     */
    private void addStructGroupComponents(StructuralGroup group)
    {
        HtmlContainer sGroupPanel = new HtmlContainer("structGroupPanel");
	    final List list;
	    if(group != null)
	    {
		    StructuralGroup[] childs = group.getStructuralChilds();
		    list = Arrays.asList(childs);
	    }
	    else
	    {
	        list = Collections.EMPTY_LIST;
	    }
	    SGroupTable sGroupTable = new SGroupTable("structuralGroupChilds", list);
	    if(list.isEmpty())
	    {
	        sGroupPanel.setVisible(false);
	    }
	    sGroupPanel.add(sGroupTable);
	    add(sGroupPanel);
    }

    /**
     * Table voor structurele groepen.
     */
    private static class SGroupTable extends ListView
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met structuurgroepen
         */
        public SGroupTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
         */
        protected void populateItem(ListItem cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModelObject();
    		cell.add(new Label("description", group.getDescription()));
    		Link actionLinkLocalId =
    		    new BookmarkablePageLink("groupLinkId", OnderhoudPage.class)
    		        .setParameter("browseexpr", group.getId())
    		        .setAutoEnable(false);
            actionLinkLocalId.add(new Label("localId", group.getLocalId()));
            cell.add(actionLinkLocalId);
        }
    }

}
