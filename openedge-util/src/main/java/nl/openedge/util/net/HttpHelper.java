package nl.openedge.util.net;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for Http related things.
 * 
 * @author Eelco Hillenius
 */
public final class HttpHelper
{
	/** Log. */
	private static Logger log = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * Hidden constructor.
	 */
	private HttpHelper()
	{
		super();
	}

	/**
	 * Execute http get with given url and return the result body as a string.
	 * 
	 * @param url
	 *            url to get
	 * @return result body as a string (or null)
	 * @throws HttpHelperException
	 *             when an unexpected exception occurs
	 */
	public static String get(String url) throws HttpHelperException
	{
		GetMethod get = null;
		HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
		String body = null;
		get = new GetMethod(url);
		try
		{
			int resultcode = client.executeMethod(get);
			body = get.getResponseBodyAsString();
			if (resultcode != HttpStatus.SC_OK)
			{
				throw new HttpHelperException("resultcode was " + resultcode + ", error:\n" + body);
			}
		}
		catch (HttpException e)
		{
			log.error(e.getMessage(), e);
			throw new HttpHelperException(e);
		}
		catch (IOException e)
		{
			log.error(e.getMessage(), e);
			throw new HttpHelperException(e);
		}
		finally
		{
			get.releaseConnection();
		}
		return body;
	}
}
