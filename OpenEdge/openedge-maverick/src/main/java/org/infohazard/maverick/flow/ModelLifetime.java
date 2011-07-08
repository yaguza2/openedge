/*
 * $Id: ModelLifetime.java,v 1.1 2002/03/24 22:45:05 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ModelLifetime.java,v $
 */

package org.infohazard.maverick.flow;

/**
 * If a model implements this interface, method(s) will be called so that
 * the model object can manage internal resources.
 */
public interface ModelLifetime
{
	/**
	 * Called when Maverick is completely finished with the model.
	 */
	public void discard();
}
