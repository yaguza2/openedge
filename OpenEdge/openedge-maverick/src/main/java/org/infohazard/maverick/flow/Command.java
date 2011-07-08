/*
 * $Id: Command.java,v 1.5 2003/10/21 14:03:27 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/Command.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 * <p>
 * The Command is the highest entry point of workflow outside of the
 * {@link org.infohazard.maverick.Dispatcher Dispatcher}.
 * </p>
 * <p>
 * For a base implementation, see {@link CommandBase}.
 * </p>
 */
public interface Command
{
	/**
     * <p>
	 * Execute the Command and render the results!
     * </p>
	 */
	public void go(MaverickContext mctx) throws IOException, ServletException;
}
