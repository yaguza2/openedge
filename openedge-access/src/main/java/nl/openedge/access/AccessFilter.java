package nl.openedge.access;

import java.io.IOException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @version	$Id$
 */
public class AccessFilter implements Filter {

	/** key for storage of subject in session */
	public final static String AUTHENTICATED_SUBJECT_KEY =
		"_authenticatedSubject";
		
	/** last request before logon redirect will be saved as a request parameter */
	public final static String LAST_REQUEST_KEY = "lastRequest";

	/** if authentication failed or was not done yet, redirect to this url */
	protected static String loginRedirect;
	
	/** OpenEdge Access factory */
	protected static AccessFactory accessFactory = null;

	/** keep a reference to the config for later use */
	protected FilterConfig config = null;

	/** log */
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		
		this.config = config;
		loginRedirect = config.getInitParameter("loginRedirect");
		if(loginRedirect == null) 	loginRedirect = "/";
		else if(loginRedirect.charAt(0) != '/') 
				loginRedirect = "/" + loginRedirect;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(	ServletRequest req,
							ServletResponse res,
							FilterChain chain)
							throws IOException, ServletException {
			
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();

		Subject subject = (Subject)session.getAttribute(
								AUTHENTICATED_SUBJECT_KEY);
		boolean needsAuthentication = false;
		String uri = ((HttpServletRequest)req).getRequestURI();
		// strip contextpath
		uri = uri.substring(request.getContextPath().length());
		
		UriAction action = new UriAction(uri);
		try {
			// Subject.doAs(subject, action) does NOT work... dunno why?
			// nevertheless, this does. 
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
			chain.doFilter(req, res);
			return;
		} catch (SecurityException se) {
			// Subject does not have permission
			log.info("for subject '" + subject + 
					"', uri '" + uri + "': " + se.getMessage());
			if(subject == null) needsAuthentication = true;
		}


		// if this is a proctected request, try to retrieve subject from session
		if(needsAuthentication) {
			
			if( subject == null) {
				// save this request
				String lq = request.getRequestURL() + "?" + 
								request.getQueryString();
				request.setAttribute(LAST_REQUEST_KEY, lq);
				// redirect to login address
				response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + loginRedirect));
			} else {				
				// the subject was not authorised; send error
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_FORBIDDEN,
						"you do not have sufficient rights for this resource");	
			}
		} else {
			// the subject was not authorised; send error
			((HttpServletResponse)res).sendError(HttpServletResponse.SC_FORBIDDEN,
					"you do not have sufficient rights for this resource");				
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	
	}
	
	/** action for checking permissions for a specific subject */
	class UriAction implements PrivilegedAction {
		
		// uri to check on
		private String uri;
		
		/** construct with uri */
		public UriAction(String uri) {
			this.uri = uri;
		}
		
		/** run check */
		public Object run() {
			Permission p = new NamedPermission(uri);
			AccessController.checkPermission(p);
			return null;
		}
	}

}
