package nl.openedge.modules.observers;

import java.util.EventObject;

/**
 * fired when all components are (re)loaded.
 * 
 * @author Eelco Hillenius
 */
public final class ComponentsLoadedEvent extends EventObject
{

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param source
	 *            sender of event
	 */
	public ComponentsLoadedEvent(Object source)
	{
		super(source);
	}

}
