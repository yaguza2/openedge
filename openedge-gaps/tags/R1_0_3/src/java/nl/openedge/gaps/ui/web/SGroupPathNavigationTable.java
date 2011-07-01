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

import nl.openedge.gaps.core.groups.StructuralGroup;
import nl.openedge.gaps.core.groups.StructuralRootGroup;

import com.voicetribe.util.collections.MicroMap;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Tabel voor pad navigatie.
 */
public final class SGroupPathNavigationTable extends Table
{
    /**
     * Command.
     */
    private final SelectCommand command;

    /**
     * Construct.
     * @param componentName
     * @param group
     * @param command
     */
    public SGroupPathNavigationTable(String componentName,
            StructuralGroup group, SelectCommand command)
    {
        super(componentName, null);
        setCurrentGroup(group, false);
        this.command = command;
    }

    /**
     * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
     */
    protected void populateCell(Cell cell)
    {
		StructuralGroup group = (StructuralGroup)cell.getModel();
        Link actionLinkLocalId = new SelectGroupLink(
                "groupLinkId", group.getId(), command);
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
        setCurrentGroup(group, true);
    }

    /**
     * Zet de huidige structuurgroep.
     * @param group de huidige structuurgroep
     * @param invalidateModel of invalidateModel dient te worden aangeroepen
     */
    private void setCurrentGroup(StructuralGroup group, boolean invalidateModel)
    {
        StructuralGroup[] path = group.getPathToRoot();
	    setModel(new MicroMap(getName(), Arrays.asList(path)));
	    if(invalidateModel)
	    {
	        invalidateModel();
	    }
    }
}
