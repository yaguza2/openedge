package nl.openedge.util.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Simple filter that tracks sessions.
 * 
 * @author Eelco Hillenius
 */
public class SessionFilter implements Filter
{
	/** key for session stats objects. */
	public static final String SESSION_STATS_KEY = "_httpSessionStats";

	/**
	 * The filter configuration object we are associated with. If this value is null, this
	 * filter instance is not currently configured.
	 */
	private FilterConfig filterConfig = null;

	/**
	 * constructor.
	 */
	public SessionFilter()
	{
		// nothing here
	}

	/**
	 * Take this filter out of service.
	 */
	@Override
	public void destroy()
	{
		this.filterConfig = null;
	}

	/**
	 * Time the processing that is performed by all subsequent filters in the current
	 * filter stack, including the ultimately invoked servlet.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param response
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpSession session = httpRequest.getSession();
		SessionStats stats = (SessionStats) session.getAttribute(SESSION_STATS_KEY);
		if (stats == null)
		{
			stats = new SessionStats();
			stats.setRemoteAddr(httpRequest.getRemoteAddr());
			session.setAttribute(SESSION_STATS_KEY, stats);
		}
		else
		{
			stats.hit();
		}

		// Pass control on to the next filter
		chain.doFilter(request, response);

	}

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig theFilterConfig)
	{
		this.filterConfig = theFilterConfig;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (filterConfig == null)
		{
			return ("SessionFilter()");
		}
		StringBuffer sb = new StringBuffer("SessionFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}

}
