package nl.openedge.access;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;

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
		try {
			
			Permission p = new NamedPermission(uri);
			AccessController.checkPermission(p);
			// if we get here, the user was authorised
			chain.doFilter(req, res);
			
		} catch(AccessControlException e) {
			if(log.isDebugEnabled()) log.debug("for subject '" + subject + 
					"', uri '" + uri + "': " + e.getMessage());
			if(subject == null) needsAuthentication = true;
		}

		// if this is a proctected request, try to retrieve subject from session
		if(needsAuthentication) {
			
			if( subject == null) {
				// redirect to login address
				response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + loginRedirect));
			} else {				
				// the subject was not authorised; send error
				((HttpServletResponse)res).sendError(
						HttpServletResponse.SC_FORBIDDEN);
			}
		} else {
				
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	
	}

}
