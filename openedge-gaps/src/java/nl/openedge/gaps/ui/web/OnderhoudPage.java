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

import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.support.ParameterBrowser;

import com.voicetribe.wicket.PageParameters;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.ExternalPageLink;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

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
	    if(!(pos instanceof StructuralGroup))
	    {
	        throw new RuntimeException("expressie niet geldig voor structuurgroep");
	        // TODO los op met msg panel ofzo
	    }
	    StructuralGroup group = (StructuralGroup)pos;
	    addStructGroupNavigation(group);
	    StructuralGroupPanel structGroupPanel = new StructuralGroupPanel(
	            "structuralGroupPanel", group);
	    add(structGroupPanel);
	    ParameterGroupPanel paramGroupPanel = new ParameterGroupPanel(
	            "parameterGroupPanel", group);
	    add(paramGroupPanel);
	}

    /**
     * Voeg navigatiecomponenten voor navigatie structuurgroepen toe.
     * @param group de huidige structuurgroep
     */
    private void addStructGroupNavigation(StructuralGroup group)
    {
        StructuralGroup[] path = group.getPathToRoot();
        SGroupPathNavigationTable sGroupPathNavTable =
            new SGroupPathNavigationTable("structuralGroupPathNav", Arrays.asList(path));
	    add(sGroupPathNavTable);
    }

    /**
     * Tabel voor pad navigatie.
     */
    class SGroupPathNavigationTable extends Table
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
        protected void populateCell(Cell cell)
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
