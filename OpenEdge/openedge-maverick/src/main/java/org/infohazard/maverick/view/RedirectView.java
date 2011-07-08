/*
 * $Id: RedirectView.java,v 1.8 2004/08/07 07:35:43 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/RedirectView.java,v $
 */

package org.infohazard.maverick.view;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;

/**
 * This view causes a client redirect.  If the model is a String,
 * that overrides the path.  ControllerContext params become query
 * parameters.  If the model is a Map, the key/value pairs are
 * converted into parameters for the target URL, but this behavior
 * is deprecated.
 *
 * The key "#" in context params (or Map model) is appended to
 * the end of the redirect URL as a named anchor.
 */
public class RedirectView implements View
{
	/**
	 * Redirect string.
	 */
	protected String target;

	/**
	 * @param target is the URL for the redirect.
	 */
	RedirectView(String target)
	{
		this.target = target;
	}

	/**
	 * Produces an http redirect.
     *
     * If the model property of the given ViewContext is an instance of
     * java.util.Map, the entries are added as URL parameters.
     * If the model property is an instance of String, it is used as-is for
     * the target instead of the configured parameter.
	 *
	 * @param vctx The ViewContext containing a Map or String based model
     * property
	 *
	 * @see View#go
	 */
	public void go(ViewContext vctx)  throws IOException, ServletException
	{
		String result = this.target;

		if (vctx.getModel() instanceof Map)
		{
			result = this.addQueryParams(result, (Map)vctx.getModel());
		}
		else if (vctx.getModel() instanceof String)
		{
			result = (String)vctx.getModel();
		}
		
		// Now, a separate step
		result = this.addQueryParams(result, vctx.getViewParams());

		// Just in case we need a session id
		result = vctx.getRealResponse().encodeRedirectURL(result);
		
		vctx.getRealResponse().sendRedirect(result);
	}
	
	/**
	 */
	protected String addQueryParams(String start, Map params)
	{
		if (params == null || params.isEmpty())
			return start;
			
		StringBuffer url = new StringBuffer(start);
		
		// If there is not already some parameters, we need a ?
		boolean first = (start.indexOf('?') < 0);
		
		Iterator entries = params.entrySet().iterator();
		while (entries.hasNext())
		{
			Map.Entry entry = (Map.Entry)entries.next();
			String key = entry.getKey().toString();

			// The special key "#" is always tacked onto the end
			if ("#".equals(key))
				continue;
				
			if (first)
			{
				url.append("?");
				first = false;
			}
			else
				url.append("&");

			if (entry.getValue() instanceof Object[])
			{
				Object[] values = (Object[])entry.getValue();
				for (int i = 0; i < values.length; i++)
				{
					if (i > 0)
						url.append("&");

					addQueryParam(url, key, values[i]);
				}
			}
			else
			{
				addQueryParam(url, key, entry.getValue());
			}
		}
		
		Object namedAnchor = params.get("#");
		if (namedAnchor != null)
		{
			url.append("#");
			url.append(URLEncoder.encode(namedAnchor.toString()));
            // :FIXME: encode is deprecated in Java 1.4 [thusted 2003-10-27]
		}
		
		return url.toString();
	}

	protected void addQueryParam(StringBuffer url, String key, Object value) {
		url.append(URLEncoder.encode(key));
        // :FIXME: encode is deprecated in Java 1.4 [thusted 2003-10-27]
		url.append("=");
		url.append(URLEncoder.encode(value.toString()));
        // :FIXME: encode is deprecated in Java 1.4 [thusted 2003-10-27]
	}
}
