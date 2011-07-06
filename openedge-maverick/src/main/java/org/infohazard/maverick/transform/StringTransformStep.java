/*
 * $Id: StringTransformStep.java,v 1.3 2004/06/07 20:38:14 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/StringTransformStep.java,v $
 */

package org.infohazard.maverick.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.xml.transform.OutputKeys;
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
import org.infohazard.maverick.flow.TransformContext;
import org.xml.sax.ContentHandler;

/**
 * Helper class for transform steps that are basically text by nature.
 * Funnels all method calls into go(String).
 */
public abstract class StringTransformStep extends AbstractTransformStep
{
	/**
	 * Logger.
	 */
    private static Logger log = LoggerFactory.getLogger(StringTransformStep.class);
	
    /**
     * Construct with transform context.
     * @param tctx transform context
     * @throws ServletException
     */
	public StringTransformStep(TransformContext tctx) throws ServletException
	{
		super(tctx);
	}
	
	/**
	 * @see org.infohazard.maverick.flow.TransformStep#getSAXHandler()
	 */
	public ContentHandler getSAXHandler() throws IOException, ServletException
	{
		log.debug("Creating TransformerHandler which sends to next step output stream.");
		
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
	 * Funnels output to go(String).
	 * @see org.infohazard.maverick.flow.TransformStep#done()
	 */
	public void done() throws IOException, ServletException
	{
		log.debug("Done being written to");
		
		if (this.fakeResponse == null)
		{
			throw new IllegalStateException("done() called illegally");
		}
		else
		{
			this.go(this.fakeResponse.getOutputAsString());
			this.fakeResponse = null;
		}
	}

	/**
	 * Funnels output to go(String).
	 * @see org.infohazard.maverick.flow.TransformStep#go(javax.xml.transform.Source)
	 */
	public void go(Source input) throws IOException, ServletException
	{
		log.debug("Building String from Source");
		
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer trans = tFactory.newTransformer();
			
			trans.setOutputProperty(OutputKeys.INDENT, "no");

			StringWriter output = new StringWriter();
			Result res = new javax.xml.transform.stream.StreamResult(output);

			trans.transform(input, res);

			this.go(output.toString());
		}
		catch (TransformerException ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * Funnels output to go(String).
	 * @see org.infohazard.maverick.flow.TransformStep#go(java.io.Reader)
	 */
	public void go(Reader input) throws IOException, ServletException
	{
		log.debug("Building String from Reader");
		
		StringWriter output = new StringWriter();
		while (input.ready())
			output.write(input.read());

		this.go(output.toString());
	}

	/**
	 * You implement this.
	 * @see org.infohazard.maverick.flow.TransformStep#go(java.lang.String)
	 */
	public abstract void go(String input) throws IOException, ServletException;
}
