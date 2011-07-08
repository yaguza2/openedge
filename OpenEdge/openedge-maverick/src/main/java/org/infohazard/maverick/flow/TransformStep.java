/*
 * $Id: TransformStep.java,v 1.4 2003/04/12 06:10:51 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/TransformStep.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;
import java.io.Writer;
import java.io.Reader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import org.xml.sax.ContentHandler;

/**
 * The TransformStep defines a transformation step in a single request.  It
 * is used and then disposed of.  It provides convenient facades for most
 * of the usual output mechanisms that views and previous transforms might
 * want.
 *
 * Unlike a Transform, a TransformStep is used in a single request and
 * then thrown away.
 */
public interface TransformStep
{
	/**
	 * Your steps should always return false.  The last step, which
	 * is not really a step at all - it's the real output stream,
	 * will return true.
	 */
	public boolean isLast();
	
	/**
	 * This method allows content-type information to be passed into the
	 * subsequent step.  It is equivalent to getResponse().setContentType(),
	 * however it can be used for all the other output methods (which
	 * do not provide a HttpServletResponse interface.  Calling this method
	 * is optional but recommended so that prematurely halted transformations
	 * can have an intelligently set content-type.
	 */
	public void setContentType(String contentType);
	
	/**
	 * Must call done() when finished.
	 */
	public ContentHandler getSAXHandler() throws IOException, ServletException;
	
	/**
	 * Must call done() when finished.
	 */
	public HttpServletResponse getResponse() throws IOException, ServletException;

	/**
	 * Must call done() when finished.
	 */
	public Writer getWriter() throws IOException, ServletException;
	
	/**
	 * This should be called after writing is complete.
	 */
	public void done() throws IOException, ServletException;

	//
	//
	// OR one of the below methods can be called.  Note that these
	// do not require the use of done(), although it doesn't hurt.
	//
	//

	/**
	 * This is available if it is more convenient.
	 */
	public void go(Source input) throws IOException, ServletException;

	/**
	 * This is available if it is more convenient.
	 */
	public void go(Reader input) throws IOException, ServletException;
	
	/**
	 * This is available if it is more convenient.
	 */
	public void go(String input) throws IOException, ServletException;
}
