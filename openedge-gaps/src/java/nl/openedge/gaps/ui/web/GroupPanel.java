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
import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.html.HtmlContainer;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.panel.Panel;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Panel voor groepen.
 */
public class GroupPanel extends Panel
{
    /** panel voor structuurgroepen. */
    private final HtmlContainer sGroupPanel;

    /** panel voor parametergroepen. */
    private final HtmlContainer pGroupPanel;

    /** structuurgroep tabel. */
    private final SGroupTable sGroupTable;

    /** structuurgroep navigatie tabel. */
    private final SGroupPathNavigationTable sGroupPathNavTable;

    /** parametergroep tabel. */
    private final PGroupTable pGroupTable;

    /** parameter browser. */
    private final ParameterBrowser browser = new ParameterBrowser();

    /**
     * Construct.
     * @param componentName
     */
    public GroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);

	    sGroupPathNavTable = new SGroupPathNavigationTable("structuralGroupPathNav", group);
	    add(sGroupPathNavTable);

	    sGroupPanel = new HtmlContainer("structGroupPanel");
	    sGroupTable = new SGroupTable("structuralGroupChilds", group);
	    sGroupPanel.add(sGroupTable);
	    add(sGroupPanel);

	    pGroupPanel = new HtmlContainer("paramGroupPanel");
	    pGroupTable = new PGroupTable("parameterGroupChilds", group);
	    pGroupPanel.add(pGroupTable);
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
       StructuralGroup group = (StructuralGroup)result;
       sGroupTable.setCurrentGroup(group);
       sGroupPathNavTable.setCurrentGroup(group);
       pGroupTable.setCurrentGroup(group);
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
         * @param componentName
         * @param group
         */
        public SGroupTable(String componentName, StructuralGroup group)
        {
            super(componentName, null);
            setCurrentGroup(group);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModel();
    		cell.add(new Label("description", group.getDescription()));
    		Link actionLinkLocalId = new Link("groupLinkId") {

    	        public void linkClicked(RequestCycle cycle)
    	        {
    	            selectStructuralGroup(group.getId());
    	        }
    		    
    		};
            actionLinkLocalId.add(new Label("localId", group.getLocalId()));
            cell.add(actionLinkLocalId);
        }

        /**
         * Zet de huidige structuurgroep.
         * @param group de huidige structuurgroep
         */
        public void setCurrentGroup(StructuralGroup group)
        {
    	    StructuralGroup[] childs = group.getStructuralChilds();
    	    List list = Arrays.asList(childs);
    	    setModel(new MicroMap(getName(), list));
    	    removeAll();
    	    if(list.isEmpty())
    	    {
    	        setVisible(false);
    	    }
    	    else
    	    {
    	        setVisible(true); // in case we set it to false before
    	    }
        }
    }

    /**
     * Table voor structurele groepen.
     */
    class PGroupTable extends Table
    {

        /**
         * Construct.
         * @param componentName
         * @param group
         */
        public PGroupTable(String componentName, StructuralGroup group)
        {
            super(componentName, null);
            setCurrentGroup(group);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            final ParameterGroup group = (ParameterGroup)cell.getModel();
    		cell.add(new Label("description", group.getDescription()));
    		Link actionLinkLocalId = new Link("groupLinkId") {

    	        public void linkClicked(RequestCycle cycle)
    	        {
    	            selectStructuralGroup(group.getId());
    	        }
    		    
    		};
            actionLinkLocalId.add(new Label("localId", group.getLocalId()));
            cell.add(actionLinkLocalId);
        }

        /**
         * Zet de huidige structuurgroep.
         * @param group de huidige structuurgroep
         */
        public void setCurrentGroup(StructuralGroup group)
        {
    	    ParameterGroup[] childs = group.getParameterChilds();
    	    List list = Arrays.asList(childs);
    	    setModel(new MicroMap(getName(), list));
    	    removeAll();
    	    if(list.isEmpty())
    	    {
    	        setVisible(false);
    	    }
    	    else
    	    {
    	        setVisible(true); // in case we set it to false before
    	    }
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
        public SGroupPathNavigationTable(String componentName, StructuralGroup group)
        {
            super(componentName, null);
            setCurrentGroup(group);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
    		final StructuralGroup group = (StructuralGroup)cell.getModel();
    		Link actionLinkLocalId = new Link("groupLinkId") {

    	        public void linkClicked(RequestCycle cycle)
    	        {
    	            selectStructuralGroup(group.getId());
    	        }
    		    
    		};
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

        /**
         * Zet de huidige structuurgroep.
         * @param group de huidige structuurgroep
         */
        public void setCurrentGroup(StructuralGroup group)
        {
            StructuralGroup[] path = group.getPathToRoot();
    	    setModel(new MicroMap(getName(), Arrays.asList(path)));
    	    removeAll();
        }
    }
}
