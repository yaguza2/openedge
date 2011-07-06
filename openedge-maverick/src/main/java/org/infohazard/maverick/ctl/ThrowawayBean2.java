/*
 * $Id: ThrowawayBean2.java,v 1.3 2003/02/19 22:50:47 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/ctl/ThrowawayBean2.java,v $
 */

package org.infohazard.maverick.ctl;

import org.apache.commons.beanutils.BeanUtils;

/**
 * <p>ThrowawayBean2 is a throwaway controller which populates its
 * bean properties using the Apache BeanUtils.  Note that unless
 * you set the model yourself, the default will be "this".</p>
 
 * <p>This is the typical use case
 * of html form processing; the controller itself will have setters and
 * getters for the various fields.  See the FriendBook sample for several
 * examples of this idiom.</p>
 *
 * <p>It's certainly not necessary to use the controller-as-model pattern.
 * You can set specific objects to custom-tailor the "shape" of the
 * model.</p>
 */
public class ThrowawayBean2 extends Throwaway2
{
	/**
	 * This is the method you should override to implement application logic.
	 * Default implementation just returns "success".
	 */
	protected String perform() throws Exception
	{
		return SUCCESS;
	}

	/**
	 */
	protected final String go() throws Exception
	{
		BeanUtils.populate(this, this.getCtx().getRequest().getParameterMap());
		BeanUtils.populate(this, this.getCtx().getControllerParams());

		this.getCtx().setModel(this);
		
		return this.perform();
	}
}
