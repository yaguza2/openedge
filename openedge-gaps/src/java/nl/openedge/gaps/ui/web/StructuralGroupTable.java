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
import nl.openedge.gaps.ui.web.util.ResultList;

import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Table voor structurele groepen.
 */
public final class StructuralGroupTable extends Table
{
    /** table model. */
    private final ResultList model;

    /**
     * Construct.
     * @param componentName
     * @param model
     */
    public StructuralGroupTable(String componentName, ResultList model)
    {
        super(componentName, model);
        this.model = model;
    }

    /**
     * Construct.
     * @param componentName
     * @param model
     * @param pageSizeInCells
     */
    public StructuralGroupTable(String componentName, ResultList model, int pageSizeInCells)
    {
        super(componentName, model, pageSizeInCells);
        this.model = model;
    }

    /**
     * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
     */
    protected void populateCell(Cell cell)
    {
		StructuralGroup group = (StructuralGroup) cell.getModel();
		cell.add(new Label("localId", group.getLocalId()));
		cell.add(new Label("description", group.getDescription()));
		// TODO voeg dynamische link toe voor edit & delete
    }

}
