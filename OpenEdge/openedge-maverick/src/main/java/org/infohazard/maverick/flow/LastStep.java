/*
 * $Id: LastStep.java,v 1.4 2004/06/07 20:39:00 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/LastStep.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

/**
 * Simple transformation step which dumps to the real output.  There
 * are NO subsequent steps!  This is the end of the line.
 */
class LastStep implements TransformStep
{
	/**
	 * Logger.
	 */
    private static Logger log = LoggerFactory.getLogger(LastStep.class);

	/**
	 */
	protected MaverickContext mavCtx;
	
	/**
	 */
	public LastStep(MaverickContext mctx)
	{
		this.mavCtx = mctx;
	}
	
	/**
	 */
	public boolean isLast()
	{
		return true;
	}
	
	/**
	 * Actually set this value, we are the last element in the chain.
	 */
	public void setContentType(String contentType)
	{
		this.getResponse().setContentType(contentType);
	}
	
	/**
	 * Dumps directly to the real response
	 */
	public ContentHandler getSAXHandler() throws IOException, ServletException
	{
		try
		{
			SAXTransformerFactory saxTFact = (SAXTransformerFactory)TransformerFactory.newInstance();
			TransformerHandler tHandler = saxTFact.newTransformerHandler();
	
			Result res = new StreamResult(this.getResponse().getOutputStream());
			tHandler.setResult(res);
			
			return tHandler;
		}
		catch (TransformerConfigurationException ex)
		{
			throw new ServletException(ex);
		}
	}
	
	/**
	 * @return the real response!
	 */
	public HttpServletResponse getResponse()
	{
		if(log.isDebugEnabled()) log.debug("Getting real response");
		
		return this.mavCtx.getRealResponse();
	}

	/**
	 * @return th real response writer!
	 */
	public Writer getWriter() throws IOException, ServletException
	{
		return this.getResponse().getWriter();
	}
	
	/**
	 */
	public void done() throws IOException, ServletException
	{
		// Do nothing
	}
	
	
	
	

	/**
	 * Serializes the XML Source to the response.
	 */
	public void go(Source input) throws IOException, ServletException
	{
		try
		{
			// TransformerFactory and Transformer are are not thread-safe so
			// we must create new ones each time.
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer trans = tFactory.newTransformer();

			Result res = new StreamResult(this.getResponse().getWriter());

			trans.transform(input, res);
		}
		catch (TransformerException ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * Writes the reader to the response.
	 */
	public void go(Reader input) throws IOException, ServletException
	{
		Writer output = this.getResponse().getWriter();
		while (input.ready())
			output.write(input.read());
	}

	/**
	 * Writes the String to the response.
	 */
	public void go(String input) throws IOException, ServletException
	{
		Writer output = this.getResponse().getWriter();

		output.write(input);
	}
}
