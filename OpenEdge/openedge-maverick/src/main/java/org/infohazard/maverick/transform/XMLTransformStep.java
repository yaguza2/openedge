/*
 * $Id: XMLTransformStep.java,v 1.3 2003/10/27 11:00:50 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/transform/XMLTransformStep.java,v $
 */

package org.infohazard.maverick.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.xml.transform.Source;

import org.infohazard.maverick.flow.TransformContext;
import org.xml.sax.ContentHandler;

/**
 * Helper class for transform steps that are basically XML by nature. Funnels all method
 * calls into the XML routines.
 */
public abstract class XMLTransformStep extends AbstractTransformStep
{
	public XMLTransformStep(TransformContext tctx)
	{
		super(tctx);
	}

	@Override
	public abstract ContentHandler getSAXHandler() throws IOException, ServletException;

	@Override
	public abstract void done() throws IOException, ServletException;

	@Override
	public abstract void go(Source input) throws IOException, ServletException;

	@Override
	public void go(Reader input) throws IOException, ServletException
	{
		this.go(new javax.xml.transform.stream.StreamSource(input));
	}

	@Override
	public void go(String input) throws IOException, ServletException
	{
		this.go(new StringReader(input));
	}
}
