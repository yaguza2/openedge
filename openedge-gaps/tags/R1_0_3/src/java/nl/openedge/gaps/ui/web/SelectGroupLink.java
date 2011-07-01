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

import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.html.link.Link;


/** Link voor selecteren groepen. */
public class SelectGroupLink extends Link
{
    /** id. */
    private final String id;

    private final SelectCommand command;

    /**
     * Construct.
     * @param componentName
     * @param id
     * @param command
     */
    public SelectGroupLink(String componentName, String id, SelectCommand command)
    {
        super(componentName);
        this.id = id;
        this.command = command;
    }

    /**
     * @see com.voicetribe.wicket.markup.html.link.Link#linkClicked(com.voicetribe.wicket.RequestCycle)
     */
    public void linkClicked(RequestCycle cycle)
    {
        command.execute(id);
    }

}