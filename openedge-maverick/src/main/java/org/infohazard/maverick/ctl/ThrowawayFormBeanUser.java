/*
 * $Id: ThrowawayFormBeanUser.java,v 1.4 2003/09/27 19:39:33 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/ThrowawayFormBeanUser.java,v $
 */

package org.infohazard.maverick.ctl;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This is a hybrid between Throwaway and FormBeanUser - the controller is instantiated
 * like a Throwaway, but allows a form bean to be populated instead.
 */
public abstract class ThrowawayFormBeanUser extends Throwaway2
{
	/**
	 * The form bean gets set here
	 */
	private Object formBean;

	/**
	 */
	protected Object getForm()
	{
		return this.formBean;
	}

	/**
	 * Executes this controller. Override the perform() method to provide application
	 * logic.
	 */
	@Override
	public final String go() throws Exception
	{
		this.formBean = this.makeFormBean();

		BeanUtils.populate(this.formBean, this.getCtx().getRequest().getParameterMap());
		BeanUtils.populate(this.formBean, this.getCtx().getControllerParams());

		this.getCtx().setModel(this.formBean);

		return this.perform();
	}

	/**
	 * This method can be overriden to perform application logic.
	 * 
	 * Override this method if you want the model to be something other than the formBean
	 * itself.
	 * 
	 * Use getForm to retrieve the bean created by makeFormBean(), which has been
	 * populated with the http request parameters.
	 */
	@SuppressWarnings("unused")
	protected String perform() throws Exception
	{
		return SUCCESS;
	}

	/**
	 * This method will be called to produce a simple bean whose properties will be
	 * populated with the http request parameters. The parameters are useful for doing
	 * things like persisting beans across requests.
	 * 
	 * Default is to return this.
	 */
	protected Object makeFormBean()
	{
		return this;
	}
}
