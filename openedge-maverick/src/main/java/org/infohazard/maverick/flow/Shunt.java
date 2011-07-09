/*
 * $Id: Shunt.java,v 1.6 2003/10/27 11:00:44 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/Shunt.java,v $
 */
package org.infohazard.maverick.flow;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * The Shunt interface allows Maverick to automagically determine which of a set of views
 * should be executed based on some arbitrary characteristic of the request. Views are
 * associated with modes in the Maverick configuration, and the Shunt is responsible for
 * identifying the proper mode based on the request.
 * </p>
 * 
 * <p>
 * The canonical example of a Shunt is the LanguageShunt, which uses the Accept-Language
 * header to choose among views with modes like "en", "fr", etc. More complicated Shunts
 * might allow regexes or other sophisticated expressions in the mode string.
 * </p>
 * 
 * <p>
 * Individual Shunt instances are associated with a particular view name, so there can be
 * many modes for each of "success", "error", etc.
 * </p>
 * 
 * <p>
 * As the Maverick config file is loaded, Shunts are created and modes are defined with
 * defineMode(). Then, during execution, getView() is called. Thus, defineMode() can be
 * slow, but geView() should be fast.
 * </p>
 * 
 * @author Jeff Schnitzer
 * @version $Revision: 1.6 $ $Date: 2003/10/27 11:00:44 $
 */
public interface Shunt
{
	/**
	 * As the Maverick config file is loaded, this method will be called to associate
	 * modes with particular views. If the configuration for a view did not specify a
	 * mode, the mode will be null. Shunts are free to interpret the mode in any way they
	 * choose.
	 * 
	 * @param mode
	 *            The mode associated with this view. Can be null.
	 * @param v
	 *            The view which should be rendered when this mode is active.
	 * @exception ConfigException
	 *                If modes clash (such as a duplicate mode).
	 */
	public void defineMode(String mode, View v) throws ConfigException;

	/**
	 * This is called during runtime to obtain a view based on some arbitrary
	 * characteristic of the request. All modes will already be defined.
	 * 
	 * @param request
	 *            The state of the request is used to determine the proper mode.
	 * @return A view appropriate for the request, based on mode.
	 * @exception NoSuitableModeException
	 *                if the shunt could not pick a view from the modes which were
	 *                defined.
	 */
	public View getView(HttpServletRequest request) throws NoSuitableModeException;
}
