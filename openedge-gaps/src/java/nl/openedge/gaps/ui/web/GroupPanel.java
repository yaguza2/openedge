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
import nl.openedge.gaps.core.groups.StructuralRootGroup;
import nl.openedge.gaps.support.ParameterBrowser;

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
public class GroupPanel extends Panel
{

    /** parameter browser. */
    private ParameterBrowser browser = new ParameterBrowser();

    /**
     * Construct.
     * @param componentName
     */
    public GroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);
	    addComponents(group);
    }

    /**
     * Voeg componenten toe op basis van de gegeven groep.
     * @param group de huidige structuurgroep
     */
    private void addComponents(StructuralGroup group)
    {
        addStructGroupNavigation(group);
	    addStructGroupComponents(group);
	    addParamGroupComponents(group);
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
     * Voeg componenten voor structuurgroepen toe.
     * @param group de huidige structuurgroep
     */
    private void addStructGroupComponents(StructuralGroup group)
    {
        HtmlContainer sGroupPanel = new HtmlContainer("structGroupPanel");
	    StructuralGroup[] childs = group.getStructuralChilds();
	    List list = Arrays.asList(childs);
	    SGroupTable sGroupTable = new SGroupTable("structuralGroupChilds", list);
	    if(list.isEmpty())
	    {
	        sGroupPanel.setVisible(false);
	    }
	    sGroupPanel.add(sGroupTable);
	    add(sGroupPanel);
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
     * Selecteer een structuurgroep.
     * @param expr gapspath expressie
     */
    private void selectStructuralGroup(String expr)
    {
       Object result = browser.evaluate(expr);
       if(!(result instanceof StructuralGroup))
       {
           throw new RuntimeException(result + " is geen structuurgroep");
       }
       //invalidateModel();
       removeAll();
       StructuralGroup group = (StructuralGroup)result;
       addComponents(group);
    }

    /**
     * Selecteer een parametergroep.
     * @param expr gapspath expressie
     */
    private void selectParameterGroup(String expr)
    {
       Object result = browser.evaluate(expr);
       if(!(result instanceof ParameterGroup))
       {
           throw new RuntimeException(result + " is geen structuurgroep");
       }
       //invalidateModel();
       removeAll();
       ParameterGroup group = (ParameterGroup)result;
       //TODO invullen
    }

    /**
     * Table voor structurele groepen.
     */
    class SGroupTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met structuurgroepen
         */
        public SGroupTable(String componentName, List model)
        {
            super(componentName, new MicroMap(componentName, model));
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModel();
    		cell.add(new Label("description", group.getDescription()));
    		Link actionLinkLocalId =
    		    new ExternalPageLink("groupLinkId", OnderhoudPage.class)
    		        .setParameter("browseexpr", group.getId())
    		        .setAutoEnable(false);
            actionLinkLocalId.add(new Label("localId", group.getLocalId()));
            cell.add(actionLinkLocalId);
        }
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
            super(componentName, new MicroMap(componentName, model));
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModel();
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
