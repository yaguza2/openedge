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

import com.voicetribe.wicket.Component;
import com.voicetribe.wicket.Container;
import com.voicetribe.wicket.markup.html.HtmlPage;
import com.voicetribe.wicket.markup.html.border.Border;

/**
 * This base class also creates a border for each page subclass, automatically adding 
 * children of the page to the border.  This accomplishes two important things: 
 * (1) subclasses do not have to repeat the code to create the border navigation and 
 * (2) since subclasses do not repeat this code, they are not hardwired to page 
 * navigation structure details
 */
public abstract class SimpleBorderedPage extends HtmlPage
{
    /** Border. */
    private Border border;

    /**
     * Constructor
     */
    public SimpleBorderedPage()
    {
        // Create border and add it to the page
        border = new SimpleBorder("border");
        super.add(border);
    }
    
    /**
     * Adding children to instances of this class causes those children to
     * be added to the border child instead.  
     * @see com.voicetribe.wicket.Container#add(com.voicetribe.wicket.Component)
     */
    public Container add(final Component child)
    {
        // Add children of the page to the page's border component
        border.add(child);
        return this;
    }
}
