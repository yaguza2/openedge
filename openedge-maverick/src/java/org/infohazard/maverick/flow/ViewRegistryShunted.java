/*
 * $Id: ViewRegistryShunted.java,v 1.3 2003/10/27 11:00:45 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewRegistryShunted.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Creates Shunted views.
 */
class ViewRegistryShunted extends ViewRegistry
{
	/**
	 * Used to hold global view information.
	 */
	static class ModeData
	{
		public String mode;
		public View view;

		public ModeData(String mode, View v)
		{
			this.mode = mode;
			this.view = v;
		}
	}

	/**
	 * Maps String id to List of ModeData objects.
	 */
	protected Map globalModeLists = new HashMap();

	/**
	 */
	protected ShuntFactory shuntFact;

	/**
	 */
	public ViewRegistryShunted(MasterFactory masterFact, ShuntFactory shuntFact)
	{
		super(masterFact);

		this.shuntFact = shuntFact;
	}

	/**
	 */
	protected void defineGlobalView(String id, String mode, View v) throws ConfigException
	{
		List modesForId = (List)this.globalModeLists.get(id);
		if (modesForId == null)
		{
			modesForId = new ArrayList();
			this.globalModeLists.put(id, modesForId);
		}

		modesForId.add(new ModeData(mode, v));
	}

	/**
	 */
	protected void addView(Map target, String viewName, String ref) throws ConfigException
	{
		List modesForId = (List)this.globalModeLists.get(ref);
		if (modesForId == null)
			throw new ConfigException("Unknown global view \"" + ref + "\" was referenced.");

		Iterator it = modesForId.iterator();
		while (it.hasNext())
		{
			ModeData data = (ModeData)it.next();

			this.addView(target, viewName, data.mode, data.view);
		}
	}

	/**
	 */
	protected void addView(Map target, String viewName, String mode, View v) throws ConfigException
	{
		ViewShunted shunted = (ViewShunted)target.get(viewName);
		if (shunted == null)
		{
			shunted = new ViewShunted(this.shuntFact.createShunt());
			target.put(viewName, shunted);
		}

		shunted.defineMode(mode, v);
	}
}