/*
 * $Id: ViewRegistry.java,v 1.9 2004/06/07 20:39:00 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewRegistry.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * Factory for defining global view objects which can then be referenced
 * for the creation of individual commands.
 */
abstract class ViewRegistry
{
	/**
	 * The name assigned to anonymous views.  No risk of conflicts because
	 * anonymous views are only allowed when there are no other views
	 * specified.
	 */
	public static final String ANONYMOUS_VIEW_NAME = "anonymous view";

	/**
	 */
	protected static final String TAG_VIEW = "view";
	protected static final String ATTR_VIEW_ID = "id";
	protected static final String ATTR_VIEW_MODE = "mode";
	protected static final String ATTR_VIEW_NAME = "name";
	protected static final String ATTR_VIEW_REF = "ref";

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(ViewRegistry.class);

	/**
	 */
	protected MasterFactory masterFact;

	/**
	 */
	public ViewRegistry(MasterFactory masterFact)
	{
		this.masterFact = masterFact;
	}

	/**
	 */
	public void defineGlobalViews(Element viewsNode) throws ConfigException
	{
		String defaultMode = viewsNode.getAttributeValue(ATTR_VIEW_MODE);

		Iterator it = viewsNode.getChildren(TAG_VIEW).iterator();
		while (it.hasNext())
		{
			Element viewNode = (Element)it.next();

			String id = viewNode.getAttributeValue(ATTR_VIEW_ID);
			String mode = viewNode.getAttributeValue(ATTR_VIEW_MODE);
			if (mode == null)
				mode = defaultMode;

			View v = this.masterFact.createView(viewNode);

			this.defineGlobalView(id, mode, v);
		}
	}

	/**
	 * Creates a mapping from view name to View object.  Nameless
	 * views are given the name ANONYMOUS_VIEW_NAME, in which case
	 * it will be the only view available.
	 */
	public Map createViewsMap(List viewNodes) throws ConfigException
	{
		Map result = new HashMap();

		Iterator it = viewNodes.iterator();
		while (it.hasNext())
		{
			Element viewNode = (Element)it.next();

			String viewName = viewNode.getAttributeValue(ATTR_VIEW_NAME);
			String ref = viewNode.getAttributeValue(ATTR_VIEW_REF);
			
			// viewName can default to the ref name
			if (viewName == null)
				viewName = ref;
			
			// Maybe no name was specified
			if (viewName == null)
			{
				if (viewNodes.size() > 1)
					throw new ConfigException("You cannot have views without names if there are more than one:  " + XML.toString(viewNode));
				else
					viewName = ANONYMOUS_VIEW_NAME;

				log.info("Has single unnamed view");
			}
			else
			{
				log.info("Has view named:  " + viewName);
			}

			// Hook up any references
			if (ref != null)
			{
				this.addView(result, viewName, ref);
			}
			else	// not a reference, thus view is defined in-place
			{
				String mode = viewNode.getAttributeValue(ATTR_VIEW_MODE);

				View v = this.masterFact.createView(viewNode);

				this.addView(result, viewName, mode, v);
			}
		}

		if (result.isEmpty())
			throw new ConfigException("No views defined.");

		return result;
	}

	/**
	 * Defines a global view which can later be used by calling addView()
	 * with the ref parameter.
	 *
	 * @param mode can be null
	 */
	abstract protected void defineGlobalView(String id, String mode, View v) throws ConfigException;

	/**
	 * Adds any views to the target Map which are associated with the viewName.
	 */
	abstract protected void addView(Map target, String viewName, String ref) throws ConfigException;

	/**
	 * Adds one view to the target Map, using the specified mode.
	 *
	 * @param mode can be null
	 */
	abstract protected void addView(Map target, String viewName, String mode, View v) throws ConfigException;
}
