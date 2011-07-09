/*
 * $Id: DocumentViewFactory.java,v 1.8 2004/08/07 07:35:43 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/view/DocumentViewFactory.java,v $
 */

package org.infohazard.maverick.view;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.View;
import org.infohazard.maverick.flow.ViewContext;
import org.infohazard.maverick.flow.ViewFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

/**
 * <p>
 * Factory for building JSP, Velocity, and other resource-based views which place the
 * model bean in one of the servlet attribute collections and use the RequestDispatcher to
 * forward to the actual content. This is also used for static documents.
 * </p>
 * <p>
 * This factory has certain options which can be defined when declaring the factory. The
 * defaults are shown here:
 * </p>
 * 
 * <pre>
 * &lt;view-factory type="document" provider="org.infohazard.maverick.view.DocumentViewFactory"&gt;
 *   &lt;default-bean-name value="model"/&gt;
 * &lt;/view-factory&gt;
 * </pre>
 * <ul>
 * <li>
 * <b>default-bean-name</b> - If no "bean" attribute is specified for individual views,
 * this is the key which will be used for placing the model in an attribute collection.</li>
 * </ul>
 * <p>
 * The options for an individual view are like this:
 * </p>
 * 
 * <pre>
 * &lt;view type="document" path="blah.jsp" scope="request" bean="model"/&gt;
 * </pre>
 * <p>
 * Scope can be one of <code>request</code>, <code>session</code>,
 * <code>application</code>. The default is <code>request</code>.
 * </p>
 * <p>
 * The default model name is specified on the factory, or "model" if nothing is defined.
 * </p>
 * <p>
 * Document views can have transforms by specifying a "<code>transform</code>" child
 * element.
 * </p>
 */
public class DocumentViewFactory implements ViewFactory
{
	/**
	 * The default attribute name for the "model" object ["model"].
	 */
	protected static final String DEFAULT_DEFAULT_BEAN_NAME = "model";

	/**
	 * The XML attribute name to set a default "model" object name ["default-bean-name"].
	 * 
	 */
	protected static final String ATTR_DEFAULT_BEAN_NAME = "default-bean-name";

	/**
	 * <p>
	 * The XML attribute name to specify the name for the "model" object ["bean"].
	 * <p>
	 * <p>
	 * The default attribute setting would be <code>bean="model"</code>.
	 * </p>
	 */
	protected static final String ATTR_BEAN_NAME = "bean";

	/**
	 * <p>
	 * The XML attribute name to specify the scope in which to store the "model" object
	 * ["scope"].
	 * </p>
	 * <p>
	 * The default attribute setting would be <code>scope="request"</code>.
	 * </p>
	 */
	protected static final String ATTR_SCOPE = "scope";

	/**
	 * <p>
	 * The XML attribute value to specify "global" or "application" scope.
	 * ["application"]. An "model" object set to this scope is available to the entire
	 * application.
	 * </p>
	 */
	protected static final String SCOPE_APP = "application";

	/**
	 * <p>
	 * The XML attribute value to specify "client" or "session" scope. ["session"]. An
	 * "model" object set to this scope is available to every request made by the same
	 * client during this application session.
	 * </p>
	 */
	protected static final String SCOPE_SESSION = "session";

	/**
	 * <p>
	 * The XML attribute value to specify "thread" or "request" scope. ["request"]. An
	 * "model" object set to this scope is available to only within the thread of the
	 * current request.
	 * </p>
	 */
	protected static final String SCOPE_REQUEST = "request";

	/**
	 * <p>
	 * Property to hold the default name of the "model" object for this factory instance
	 * ["model"].
	 * </p>
	 */
	protected String defaultBeanName = DEFAULT_DEFAULT_BEAN_NAME;

	/**
	 * <p>
	 * Initialize this factory instance by initializing the {@link #dispatchedViewFact}
	 * property and the {@link #defaultBeanName} property.
	 * </p>
	 * 
	 * @param factoryNode
	 *            Element
	 * @param servletCfg
	 *            ServletConfig
	 * @throws ConfigException
	 */
	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		if (factoryNode != null)
		{
			String value = XML.getValue(factoryNode, ATTR_DEFAULT_BEAN_NAME);
			if (value != null && value.length() > 0)
				this.defaultBeanName = value;
		}
	}

	/**
	 * @param viewNode
	 *            Element
	 * @return View
	 * @throws ConfigException
	 * @see ViewFactory#createView(Element)
	 */
	public View createView(Element viewNode) throws ConfigException
	{
		String beanName = XML.getValue(viewNode, ATTR_BEAN_NAME);
		if (beanName == null)
			beanName = this.defaultBeanName;

		String scope = XML.getValue(viewNode, ATTR_SCOPE);

		// Provide different implementations of setAttribute depending on scope
		if (SCOPE_APP.equals(scope))
		{
			return new DocumentView(beanName)
			{
				protected void setAttribute(ViewContext vctx)
				{
					vctx.getServletContext().setAttribute(this.beanName, vctx.getModel());
				}
			};
		}
		else if (SCOPE_SESSION.equals(scope))
		{
			return new DocumentView(beanName)
			{
				protected void setAttribute(ViewContext vctx)
				{
					vctx.getRequest().getSession().setAttribute(this.beanName, vctx.getModel());
				}
			};
		}
		else if (SCOPE_REQUEST.equals(scope) || scope == null)
		{
			return new DocumentView(beanName)
			{
				protected void setAttribute(ViewContext vctx)
				{
					vctx.getRequest().setAttribute(this.beanName, vctx.getModel());
				}
			};
		}
		else
		{
			throw new ConfigException("Illegal scope specified:  " + XML.toString(viewNode));
		}
	}
}
