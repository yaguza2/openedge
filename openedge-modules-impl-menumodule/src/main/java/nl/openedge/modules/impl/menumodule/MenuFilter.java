package nl.openedge.modules.impl.menumodule;

import java.util.Map;

/**
 * A menu filter can be used to filter menu items from the menu tree.
 * 
 * @author Eelco Hillenius
 */
public interface MenuFilter extends AttributeEnabledObject
{
	/** special key to store a JAAS subject with in the context. */
	String CONTEXT_KEY_SUBJECT = "subject";

	/** key to store request filters, value = 'request_filters'. */
	String CONTEXT_KEY_REQUEST_FILTERS = "request_filters";

	/** key to store session filters, value = 'session_filters'. */
	String CONTEXT_KEY_SESSION_FILTERS = "session_filters";

	/**
	 * should the provided menu item, based on the given context, be a part of the result
	 * tree.
	 * 
	 * @param menuItem
	 *            the current menu item. This is a reference to the item that will be part
	 *            of the result tree if accepted. You can change attributes/ properties if
	 *            this item for later use without affecting the original tree.
	 * @param context
	 *            the current context. This context is unique for this thread, but is
	 *            global within this thread.
	 * @return boolean true if the item should be part of the result tree, false if not
	 */
	boolean accept(MenuItem menuItem, Map<Object, Object> context);
}
