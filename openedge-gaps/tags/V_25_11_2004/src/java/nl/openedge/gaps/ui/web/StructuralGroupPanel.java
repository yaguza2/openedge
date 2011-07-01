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

import com.voicetribe.wicket.markup.html.HtmlContainer;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.ExternalPageLink;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.panel.Panel;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

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
    private static class SGroupTable extends Table
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
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModelObject();
    		cell.add(new Label("description", group.getDescription()));
    		Link actionLinkLocalId =
    		    new ExternalPageLink("groupLinkId", OnderhoudPage.class)
    		        .setParameter("browseexpr", group.getId())
    		        .setAutoEnable(false);
            actionLinkLocalId.add(new Label("localId", group.getLocalId()));
            cell.add(actionLinkLocalId);
        }
    }

}
