/*
 * $Id: ViewRegistrySimple.java,v 1.3 2003/10/27 11:00:46 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewRegistrySimple.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;
import java.util.HashMap;

/**
 * Only creates simple, non-shunted renderers.
 */
class ViewRegistrySimple extends ViewRegistry
{
	/**
	 * Maps String id to Renderer
	 */
	protected Map globalViews = new HashMap();

	/**
	 */
	public ViewRegistrySimple(MasterFactory masterFact)
	{
		super(masterFact);
	}

	/**
	 */
	protected void defineGlobalView(String id, String mode, View v) throws ConfigException
	{
		this.globalViews.put(id, v);
	}

	/**
	 */
	protected void addView(Map target, String viewName, String ref) throws ConfigException
	{
		View v = (View)this.globalViews.get(ref);
		if (v == null)
			throw new ConfigException("Reference to unknown global view \"" + ref + "\".");

		target.put(viewName, v);
	}

	/**
	 */
	protected void addView(Map target, String viewName, String mode, View v) throws ConfigException
	{
		target.put(viewName, v);
	}
}
