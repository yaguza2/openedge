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
import nl.openedge.gaps.support.ParameterBrowser;

import com.voicetribe.wicket.PageParameters;

/**
 * De GAPS onderhoud pagina.
 */
public final class OnderhoudPage extends SimpleBorderedPage
{
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
	    ParameterBrowser browser = new ParameterBrowser();
	    Object pos = browser.navigate(expr);
	    if(!(pos instanceof StructuralGroup))
	    {
	        throw new RuntimeException("expressie niet geldig voor structuurgroep");
	        // TODO los op met msg panel ofzo
	    }
	    StructuralGroup group = (StructuralGroup)pos;
	    GroupPanel panel = new GroupPanel(
	            "structuralGroupPanel", group);
	    add(panel);
	}

}
