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

import com.voicetribe.util.collections.MicroMap;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.link.Link;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Table voor structurele groepen.
 */
public final class SGroupTable extends Table
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
    public SGroupTable(String componentName, StructuralGroup group, SelectCommand command)
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
		cell.add(new Label("description", group.getDescription()));
        Link actionLinkLocalId = new SelectGroupLink(
                "groupLinkId", group.getId(), command);
        actionLinkLocalId.add(new Label("localId", group.getLocalId()));
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
    public void setCurrentGroup(StructuralGroup group, boolean invalidateModel)
    {
	    StructuralGroup[] childs = group.getStructuralChilds();
	    List list = Arrays.asList(childs);
	    setModel(new MicroMap(getName(), list));
	    if(list.isEmpty())
	    {
	        setVisible(false);
	    }
	    if(invalidateModel)
	    {
	        invalidateModel();
	    }
    }
}
