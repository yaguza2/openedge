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

import nl.openedge.gaps.core.groups.StructuralGroup;

import com.voicetribe.wicket.markup.html.panel.Panel;

/**
 * Panel voor structurele groepen.
 */
public class SGroupPanel extends Panel implements SGroupSelectedListener
{
    /** structuurgroep tabel. */
    private final SGroupTable sGroupTable;

    /** structuurgroep navigatie tabel. */
    private final SGroupPathNavigationTable sGroupPathNavTable;

    /**
     * Construct.
     * @param componentName
     */
    public SGroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);
        SelectCommand command = new SelectCommand();
        command.addSGroupSelectedListener(this);
	    sGroupTable = new SGroupTable("structuralGroupChilds", group, command);
	    add(sGroupTable);
	    sGroupPathNavTable = new SGroupPathNavigationTable(
	            "structuralGroupPathNav", group, command);
	    add(sGroupPathNavTable);
    }

    /**
     * @see nl.openedge.gaps.ui.web.SGroupSelectedListener#groupSelected(nl.openedge.gaps.ui.web.SGroupSelectedEvent)
     */
    public void groupSelected(SGroupSelectedEvent event)
    {
       StructuralGroup group = event.getGroup();
       sGroupTable.setCurrentGroup(group);
       sGroupPathNavTable.setCurrentGroup(group);
    }

}
