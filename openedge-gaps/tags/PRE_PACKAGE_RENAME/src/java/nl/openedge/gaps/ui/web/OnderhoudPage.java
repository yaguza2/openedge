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

import nl.openedge.gaps.core.Entity;
import nl.openedge.gaps.core.groups.Group;
import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.support.ParameterBrowser;

import com.voicetribe.wicket.PageParameters;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.ExternalPageLink;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.table.ListItem;
import com.voicetribe.wicket.markup.html.table.ListView;

/**
 * De GAPS onderhoud pagina.
 */
public final class OnderhoudPage extends SimpleBorderedPage
{
    /** browser. */
    private ParameterBrowser browser = new ParameterBrowser();

	/**
	 * Constructor.
	 * @param parameters pagina parameters
	 */
	public OnderhoudPage(final PageParameters parameters)
	{
	    super();
	    String expr = parameters.getString("browseexpr");
	    if(expr == null)
	    {
	        expr = "/";
	    }
	    Object pos = browser.navigate(expr);
	    addTitle((Entity)pos);
	    Group group = (Group)pos;
	    addStructGroupNavigation(group);
	    StructuralGroup sgroup = null;
	    ParameterGroup pgroup = null;
	    if(pos instanceof StructuralGroup)
	    {
	        sgroup = (StructuralGroup)pos;
	    }
	    else
	    {
	        pgroup = (ParameterGroup)pos;
	    }
	    StructuralGroupPanel structGroupPanel = new StructuralGroupPanel(
	            "structuralGroupPanel", sgroup);
	    add(structGroupPanel);
	    ParameterGroupPanel paramGroupPanel = new ParameterGroupPanel(
	            "parameterGroupPanel", sgroup);
	    add(paramGroupPanel);
	    ParameterPanel paramPanel = new ParameterPanel(
	            "parameterPanel", pgroup);
	    add(paramPanel);
	}

	/**
	 * voeg titel toe.
	 * @param pos positie
	 */
	private void addTitle(Entity pos)
	{
	    
	    add(new Label("title", pos.getLocalId()));	    
	}

    /**
     * Voeg navigatiecomponenten voor navigatie structuurgroepen toe.
     * @param group de huidige structuurgroep
     */
    private void addStructGroupNavigation(Group group)
    {
        final StructuralGroup sgroup;
        if(group instanceof StructuralGroup)
        {
            sgroup = (StructuralGroup)group;
        }
        else
        {
            sgroup = ((ParameterGroup)group).getParent();
        }
        StructuralGroup[] path = sgroup.getPathToRoot();
        SGroupPathNavigationTable sGroupPathNavTable =
            new SGroupPathNavigationTable("structuralGroupPathNav", Arrays.asList(path));
	    add(sGroupPathNavTable);
    }

    /**
     * Tabel voor pad navigatie.
     */
    class SGroupPathNavigationTable extends ListView
    {
        /**
         * Construct.
         * @param componentName
         * @param group
         * @param command
         */
        public SGroupPathNavigationTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateItem(ListItem cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModelObject();
    		Link actionLinkLocalId =
    		    new ExternalPageLink("groupLinkId", OnderhoudPage.class)
    		        .setParameter("browseexpr", group.getId())
    		        .setAutoEnable(false);
            String labelText;
            if(group instanceof StructuralRootGroup)
            {
                labelText = "root";
            }
            else
            {
                labelText = group.getLocalId();
            }
            actionLinkLocalId.add(new Label("localId", labelText));
            cell.add(actionLinkLocalId);
        }
    }
}
