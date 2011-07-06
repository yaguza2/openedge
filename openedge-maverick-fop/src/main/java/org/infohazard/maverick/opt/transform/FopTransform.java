package org.infohazard.maverick.opt.transform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.fop.apps.Driver;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Options;
import org.apache.fop.messaging.MessageHandler;
import org.apache.log4j.Category;
import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Transform;
import org.infohazard.maverick.flow.TransformContext;
import org.infohazard.maverick.flow.TransformStep;
import org.infohazard.maverick.transform.AbstractTransformStep;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;


public class FopTransform implements Transform
{

	private static Category log = Category.getInstance(FopTransform.class.getName());
	private static Category fopDriverLog = Category.getInstance(Driver.class.getName());

	protected static final String ATTR_OUTPUT = "output";
	protected static final String ATTR_DISPOSITION_TYPE = "disposition-type";
	protected static final String ATTR_FILENAME = "filename";
	protected static final String ATTR_CONFIG = "config";
	
	protected TransformerFactory tFactory = TransformerFactory.newInstance();

	protected int output = Driver.RENDER_PDF;
	
	/**
	 * For Content-Disposition header. Ifthis is set, this transform
	 * will override any previous value for the Content-Disposition header
	 * and set it to "attachment; filename={whatever you set for filename}"
	 */
	protected String filename;

	
	protected String configPath = null;

	public FopTransform(Element transformNode, ServletConfig servletCfg) throws ConfigException
	
	{
		this.setOutput(XML.getValue(transformNode, ATTR_OUTPUT));
		this.filename = XML.getValue(transformNode, ATTR_FILENAME);

		String path = XML.getValue(transformNode, ATTR_CONFIG);
		if (path!=null)
		{
			this.configPath = servletCfg.getServletContext().getRealPath(path);
		}
	}

	public void setOutput(String output) throws ConfigException {
		if (output != null)
		{
			if (output.equalsIgnoreCase("pdf"))
			{
				this.output = Driver.RENDER_PDF;
			}
			else if (output.equalsIgnoreCase("ps"))
			{
				this.output = Driver.RENDER_PS;
			}
			else if (output.equalsIgnoreCase("postscript"))
			{
				this.output = Driver.RENDER_PS;
			}
			else if (output.equalsIgnoreCase("pcl"))
			{
				this.output = Driver.RENDER_PCL;
			}
			else if (output.equalsIgnoreCase("svg"))
			{
				this.output = Driver.RENDER_SVG;
			}
			else if (output.equalsIgnoreCase("txt"))
			{
				this.output = Driver.RENDER_TXT;
			}
			else if (output.equalsIgnoreCase("text"))
			{
				this.output = Driver.RENDER_TXT;
			}
			else if (output.equalsIgnoreCase("xml"))
			{
				this.output = Driver.RENDER_XML;
			}
			else if (output.equalsIgnoreCase("mif"))
			{
				this.output = Driver.RENDER_MIF;
			}
			else
			{
				throw new ConfigException("Unsupported Output:  " + output);
			}
		}
	}

	/**
	 */
	public TransformStep createStep(TransformContext tctx)
		throws ServletException
	{
		return new Step(tctx);
	}

	protected class Step extends AbstractTransformStep
	{

		public Step(TransformContext tctx) throws ServletException
		{
			super(tctx);
		}

		/**
		 * Funnels output to go(String)
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
		*/
		public ContentHandler getSAXHandler()
			throws IOException, ServletException
		{
			log.debug(
				"Creating TransformerHandler which sends to next step output stream.");

			try
			{
				SAXTransformerFactory saxTFact =
					(SAXTransformerFactory) TransformerFactory.newInstance();
				TransformerHandler tHandler = saxTFact.newTransformerHandler();

				Result res =
					new StreamResult(this.getResponse().getOutputStream());
				tHandler.setResult(res);

				return tHandler;
			}
			catch (TransformerConfigurationException ex)
			{
				throw new ServletException(ex);
			}
		}

		public void go(String input) throws IOException, ServletException
		{
			log.debug("Handling input as String");
			//read the encoding from the xml file

			//find the first occurance of the word "encoding"
			int start = input.indexOf("encoding");
			//find the start quote and add 1
			//(we don't want to include the quote in the string)
			start = input.indexOf("\"",start)+1;
			//find the end quote
			int end = input.indexOf("\"",start);
			//pull out the encoding
			String encoding = input.substring(start,end).trim();

			log.debug("Reading string with \"" + encoding + "\" encoding.");
			byte[] bytes = input.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			this.go(new InputStreamReader(bais, encoding));
		}

		public void go(Reader input) throws IOException, ServletException
		{
			log.debug("Handling input as Reader");
			this.go(new InputSource(input));
		}

		public void go(Source input) throws IOException, ServletException
		{
			log.debug("Handling input as Source");
			InputSource is = SAXSource.sourceToInputSource(input);
			if (is != null)
			{
				this.go(is);
			}
			else
			{ //unable to convert input to InputSource
				// Use JAXP 1.1 transform api to make copy of dom to String
				log.debug(
					"Unable to handle input as Source, switching to String");
				try
				{
					Transformer trans = tFactory.newTransformer();
					trans.setOutputProperty(OutputKeys.INDENT, "no");

					StringWriter output = new StringWriter();
					Result res = new StreamResult(output);

					trans.transform(input, res);

					this.go(output.toString());
				}
				catch (TransformerException ex)
				{
					throw new ServletException(ex);
				}
			}
		}

		private void go(InputSource input) throws IOException, ServletException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Log4JLogger dlog = new Log4JLogger(fopDriverLog);
			MessageHandler.setScreenLogger(dlog);
			
			try
			{
				Options options = null;

				//load userconfig file if specified
				if (configPath != null)
				{
					//load the user config file if specified
					options = new Options(new File(configPath));
				} else {
					//try and load the standard options off of the classpath
					options = new Options();
				}
				Driver driver = new Driver(input, baos);
				driver.setLogger(dlog);
				driver.setRenderer(output);
				driver.run();
			}
			catch (FOPException ex)
			{
				throw new ServletException(ex);
			}

			String contentType;
			switch (output)
			{
				case Driver.RENDER_PS :
					contentType = "application/postscript";
					break;
				case Driver.RENDER_PCL :
					contentType = "application/vnd.hp-PCL";
					break;
				case Driver.RENDER_SVG :
					contentType = "image/svg-xml";
					break;
				case Driver.RENDER_TXT :
					contentType = "text/txt";
					break;
				case Driver.RENDER_XML :
					contentType = "text/xml";
					break;
				case Driver.RENDER_MIF :
					contentType = "application/vnd.mif";
					break;
				default : //case: Driver.RENDER_PDF
					contentType = "application/pdf";
					break;
			}

			log.debug("setting response content type to: " + contentType);

			HttpServletResponse response = this.getNext().getResponse();

			OutputStream out = response.getOutputStream();

			response.setContentType(contentType);
			response.setContentLength(baos.size());
			
			// some IE/ Adobe ActiveX control specific hacks
            response.setHeader("Expires", "");
            response.setHeader("Cache-Control", "");
            response.setHeader("Pragma", "");

            String disposition = null;
            if (filename != null)
            {
                disposition = "attachment; filename=" + filename;
                if(log.isDebugEnabled())
                {
                    log.debug("setting response header Content-Disposition to: " + disposition);
                }
                response.setHeader("Content-Disposition", disposition);
            }
            else
            {
                disposition = "inline; filename=output.pdf"; // filename does nothing really,
                	// but solves a bug in some IE versions
                if(log.isDebugEnabled())
                {
                    log.debug("setting response header Content-Disposition to: " + disposition);
                }
                response.setHeader("Content-Disposition", disposition);
            }

			baos.writeTo(out);
		}

	}
}