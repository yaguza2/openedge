/*
 * $Id: View.java,v 1.5 2002/06/06 12:23:54 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/View.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;

import javax.servlet.ServletException;

import org.infohazard.maverick.ViewDefinition;

/**
 * Views do the work of actually rendering the model.
 */
public interface View
{
	/**
	 * Renders the specified model to the response.
	 */
	public void go(ViewContext vctx) throws ServletException, IOException;

	public ViewDefinition getViewDefinition();
}
