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
import java.util.List;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;

import com.voicetribe.util.collections.MicroMap;
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
	    ParameterGroup[] childs = group.getParameterChilds();
	    List list = Arrays.asList(childs);
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
    class PGroupTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met parametergroepen
         */
        public PGroupTable(String componentName, List model)
        {
            super(componentName, new MicroMap(componentName, model));
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            final ParameterGroup group = (ParameterGroup)cell.getModel();
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
