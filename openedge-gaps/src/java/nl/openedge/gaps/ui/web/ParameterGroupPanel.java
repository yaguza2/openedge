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

import wicket.markup.html.HtmlContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.link.ExternalPageLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.table.ListItem;
import wicket.markup.html.table.ListView;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;


/**
 * Panel voor groepen.
 */
public class ParameterGroupPanel extends Panel
{

    /**
     * Construct.
     * @param componentName
     */
    public ParameterGroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);
        addParamGroupComponents(group);
    }

    /**
     * Voeg componenten voor parametergroepen toe.
     * @param group de huidige structuurgroep
     */
    private void addParamGroupComponents(StructuralGroup group)
    {
        HtmlContainer pGroupPanel = new HtmlContainer("paramGroupPanel");
	    final List list;
	    if(group != null)
	    {
	        ParameterGroup[] childs = group.getParameterChilds();
		    list = Arrays.asList(childs);
	    }
	    else
	    {
	        list = Collections.EMPTY_LIST;
	    }
	    PGroupTable sGroupTable = new PGroupTable("parameterGroupChilds", list);
	    if(list.isEmpty())
	    {
	        pGroupPanel.setVisible(false);
	    }
	    pGroupPanel.add(sGroupTable);
	    add(pGroupPanel);
    }

    /**
     * Table voor structurele groepen.
     */
    private static class PGroupTable extends ListView
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met parametergroepen
         */
        public PGroupTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see wicket.markup.html.table.Table#populateCell(wicket.markup.html.table.Cell)
         */
        protected void populateItem(ListItem cell)
        {
            final ParameterGroup group = (ParameterGroup)cell.getModelObject();
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
