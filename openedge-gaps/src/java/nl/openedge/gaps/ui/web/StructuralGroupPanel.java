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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.openedge.gaps.core.groups.StructuralGroup;

import com.voicetribe.util.collections.MicroMap;
import com.voicetribe.wicket.markup.html.panel.Panel;

/**
 * Panel voor structurele groepen.
 */
public class StructuralGroupPanel extends Panel
{
    /**
     * Construct.
     * @param componentName
     */
    public StructuralGroupPanel(String componentName, StructuralGroup group)
    {
        super(componentName);
		MicroMap results = new MicroMap();
		addComponents(results, group);
    }

	/**
	 * Add components.
	 * @param results results wrapper
	 */
	private void addComponents(MicroMap results, StructuralGroup group)
	{
	    StructuralGroup[] childs = group.getStructuralChilds();
	    List list = (childs != null) ? Arrays.asList(childs) :  new ArrayList();
	    String tableName = "structuralGroupChilds";
	    results.put(tableName, group);
	    StructuralGroupTable sGroupTable = new StructuralGroupTable(
	            "results", results);
	    add(sGroupTable);
	}

}
