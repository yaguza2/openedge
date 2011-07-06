/*
 * $Id: FormBeanUser.java,v 1.7 2003/10/27 11:00:38 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/FormBeanUser.java,v $
 */

package org.infohazard.maverick.ctl;

import org.infohazard.maverick.flow.*;
import org.apache.commons.beanutils.BeanUtils;
import org.jdom.Element;
import javax.servlet.*;

/**
 * FormBeanUser is a base class for singleton controllers which use
 * external FormBeans rather than populating themselves.  Since
 * only one of these will exist for each command definition,
 * it must be thread-safe.  This Controller pattern is very similar
 * to Struts Actions.
 */
public abstract class FormBeanUser implements ControllerSingleton
{
	/**
	 * Common name for the typical "success" view.
	 */
	public static final String SUCCESS = "success";

	/**
	 * Common name for the typical "error" view.
	 */
	public static final String ERROR = "error";

	/**
	 * If you want any custom behavior based on the content of the
	 * configuration file, this will be called once before the
	 * FormBeanUser is ever used.
	 *
	 * @see ControllerSingleton#init
	 */
	public void init(Element controllerNode) throws ConfigException
	{
		// Defaults to nothing.
	}

	/**
	 * Executes this controller.  Override one of the other perform()
	 * methods to provide application logic.
	 *
	 * @see ControllerSingleton#go
	 */
	public final String go(ControllerContext cctx) throws ServletException
	{
		try
		{
			Object formBean = this.makeFormBean(cctx);
			
			BeanUtils.populate(formBean, cctx.getRequest().getParameterMap());
			BeanUtils.populate(formBean, cctx.getControllerParams());
			
			cctx.setModel(formBean);

			return this.perform(formBean, cctx);
		}
		catch (ServletException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new ServletException(ex);
		}
	}

	/**
	 * This method can be overriden to perform application logic.
	 *
	 * Override this method if you want the model to be something
	 * other than the formBean itself.
	 *
	 * @param formBean will be a bean created by makeFormBean(),
	 * which has been populated with the http request parameters.
	 */
	protected String perform(Object formBean, ControllerContext cctx) throws Exception
	{
		return SUCCESS;
	}
												
	/**
	 * This method will be called to produce a simple bean whose properties
	 * will be populated with the http request parameters.  The parameters
	 * are useful for doing things like persisting beans across requests.
	 */
	protected abstract Object makeFormBean(ControllerContext cctx);
}