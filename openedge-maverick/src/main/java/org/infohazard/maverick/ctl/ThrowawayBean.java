/*
 * $Id: ThrowawayBean.java,v 1.5 2003/10/27 11:00:41 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/ThrowawayBean.java,v $
 */

package org.infohazard.maverick.ctl;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Note: While not formally deprecated, use of this class is discouraged. You should use
 * ThrowawayBean2 instead.
 * 
 * ThrowawayBean is a throwaway controller which populates its bean properties using the
 * Apache BeanUtils. Note that the default implementation of model() returns "this".
 */
public class ThrowawayBean extends Throwaway
{
	/**
	 * This is the method you should override to implement application logic. Default
	 * implementation just returns "success".
	 */
	@SuppressWarnings("unused")
	protected String perform() throws Exception
	{
		return SUCCESS;
	}

	/**
	 * <p>
	 * Default implementation returns "this". This is the typical use case of html form
	 * processing; the controller itself will have setters and getters for the various
	 * fields. See the FriendBook sample for several examples of this idiom.
	 * </p>
	 * 
	 * <p>
	 * It's certainly not necessary to use the controller-as-model pattern. You can return
	 * specific objects to custom-tailor the "shape" of the model.
	 * </p>
	 */
	@Override
	public Object model()
	{
		return this;
	}

	/**
	 */
	@Override
	protected final String rawPerform() throws Exception
	{
		BeanUtils.populate(this, this.getRequest().getParameterMap());
		BeanUtils.populate(this, this.getCtx().getControllerParams());

		return this.perform();
	}
}
