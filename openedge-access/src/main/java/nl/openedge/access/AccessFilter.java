package nl.openedge.access;

import java.io.IOException;

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

		// first check if this is a protected request
		//UserManager um = accessFactory.getUserManager();
		boolean needsAuthentication = false;

		// if this is a proctected request, try to retrieve subject from session
		if(needsAuthentication) {
			Subject subject = (Subject)session.getAttribute(
			AUTHENTICATED_SUBJECT_KEY);
			if( subject == null) {
				response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + loginRedirect));
			} else {
				
				if(accessFactory == null) {
					try {
						accessFactory = new AccessFactory(config.getServletContext()); 
					} catch(ConfigException e) {
						e.printStackTrace();
						throw new ServletException(e);
					}
				}
				// go on with processing
				chain.doFilter( req, res);
			}
		} else {
			chain.doFilter( req, res);	
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	
	}

}
