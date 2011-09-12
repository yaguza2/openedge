package nl.openedge.modules.observers;

import java.util.EventObject;

/**
 * high-level event that can be fired by implementors of ChainedEventCaster.
 * 
 * @author Eelco Hillenius
 */
public class ChainedEvent extends EventObject
{
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param source
	 *            sender of event
	 */
	public ChainedEvent(Object source)
	{
		super(source);
	}

}
