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
import nl.openedge.gaps.support.ParameterBrowser;
import nl.openedge.gaps.ui.web.util.ResultList;

import com.voicetribe.wicket.PageParameters;

/**
 * De GAPS Home page.
 */
public final class Onderhoud extends SimpleBorderedPage
{
	/**
	 * Constructor.
	 * @param parameters pagina parameters
	 */
	public Onderhoud(final PageParameters parameters)
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
	    StructuralGroup[] childs = group.getStructuralChilds();
	    ResultList wrapper = new ResultList();
	    List list = (childs != null) ? Arrays.asList(childs) :  new ArrayList();
	    wrapper.setResults(list);
	    StructuralGroupTable sGroupTable = new StructuralGroupTable(
	            "structuralGroupChilds", wrapper);
	}

}
