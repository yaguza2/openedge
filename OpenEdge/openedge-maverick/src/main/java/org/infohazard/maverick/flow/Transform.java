/*
 * $Id: Transform.java,v 1.5 2002/06/12 09:09:05 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/Transform.java,v $
 */

package org.infohazard.maverick.flow;

import javax.servlet.ServletException;

/**
 * The Transform interface allows some sort of arbitrary transformation on
 * a set of input data.  It is the relatively static object in the Maverick
 * configuration object graph which represents a specific transform node
 * in the config file.
 *
 * The actual work of performing a transformation step during actual
 * request procesing is done by an instance of TransformationStep,
 * which Maverick asks the Transform object to create.
 */
public interface Transform
{
	/**
	 * Create a step for servicing a single transformation.
	 */
	TransformStep createStep(TransformContext tctx) throws ServletException;
}
