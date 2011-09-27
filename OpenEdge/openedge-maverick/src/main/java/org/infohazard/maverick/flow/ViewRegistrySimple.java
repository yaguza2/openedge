/*
 * $Id: ViewRegistrySimple.java,v 1.3 2003/10/27 11:00:46 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewRegistrySimple.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.HashMap;
import java.util.Map;

/**
 * Only creates simple, non-shunted renderers.
 */
class ViewRegistrySimple extends ViewRegistry
{
	/**
	 * Maps String id to Renderer
	 */
	protected Map<String, View> globalViews = new HashMap<String, View>();

	/**
	 */
	public ViewRegistrySimple(MasterFactory masterFact)
	{
		super(masterFact);
	}

	@Override
	protected void defineGlobalView(String id, String mode, View v)
	{
		this.globalViews.put(id, v);
	}

	@Override
	protected void addView(Map<String, View> target, String viewName, String ref)
			throws ConfigException
	{
		View v = this.globalViews.get(ref);
		if (v == null)
			throw new ConfigException("Reference to unknown global view \"" + ref + "\".");

		target.put(viewName, v);
	}

	@Override
	protected void addView(Map<String, View> target, String viewName, String mode, View v)
	{
		target.put(viewName, v);
	}
}
