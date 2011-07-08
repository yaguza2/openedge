/*
 * $Id: MaverickContext.java,v 1.5 2004/06/07 20:39:00 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/MaverickContext.java,v $
 */

package org.infohazard.maverick.flow;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.infohazard.maverick.Dispatcher;

/**
 * <p>
 * MaverickContext is the concrete class which implements all the other
 * contexts.
 * Having one object minimizes object creation and data copying.
 * </p>
 */
public class MaverickContext implements ControllerContext, ViewContext, TransformContext
{
	/**
	 * <p>
     * MaverickContext logger.
     * </p>
	 */
    private static Logger log = LoggerFactory.getLogger(MaverickContext.class);
	
	/**
     * <p>
     * Our {@link Dispatcher} instance.
     * </p>
	 */
	protected Dispatcher dispatcher;

    /**
     * <p>
     * Our {@link HttpServletRequest} instance.
     * </p>
     */
	protected HttpServletRequest request;

    /**
     * <p>
     * Our {@link HttpServletResponse} instance.
     * </p>
     */
	protected HttpServletResponse response;
	
	/**
	 * <p>
     * Our instance of the "model" object that the  {@link Controller} exposes
     * to a {@link View}, so that dynamic data can be rendered.
     * Most Maverick Controller will set the model object during processing.
     * </p>
     */
	protected Object model;
	
    /**
     * <p>
     * Our {@link Controller Controller's} optional parameters, if any.
     * </p>
     */
	protected Map controllerParams;

    /**
     * <p>
     * Our {@link View View's} optional parameters, if any.
     * </p>
     */
	protected Map viewParams;

    /**
     * <p>
     * Our {@link Transform pipeline's} optional parameters, if any.
     * </p>
     */
	protected Map transformParams;
	
	/**
	 * <p>
     * An array of pipeline transformations, which is set before the View is
     * processed.
     * </p>
	 */
	protected Transform[] transforms;
	
	/**
	 * <p>
     * The index of the next transform to execute.
     * </p>
	 */
	protected int nextTransform = 0;
	
	/**
	 * <p>
     * The count of transforms to execute.
     * </p>
	 */
	protected int transformCount;
	
	/**
     * <p>
     * Convenience Constructor to pass instances of Dispatcher,
     * HttpServletRequest, and HttpServletResponse.
     * </p>
	 */
	public MaverickContext(Dispatcher disp, HttpServletRequest req, HttpServletResponse res)
	{
		this.dispatcher = disp;
		this.request = req;
		this.response = res;
	}
	
	/**
     * <p>
     * Returns our HttpServletRequest.
     * </p>
     * @return our HttpServletRequest
	 * @see ControllerContext
	 * @see ViewContext
	 * @see TransformContext
	 */
	public HttpServletRequest getRequest()
	{
		return this.request;
	}
	
	/**
     * <p>
     * Returns the *real* response object.
     * Do not use this unless you know are the tail!
     * </p>
	 * @see ViewContext
	 * @see TransformContext
	 */
	public HttpServletResponse getRealResponse()
	{
		return this.response;
	}

	/**
     * <p>
     * Returns our HttpServletResponse.
     * </p>
	 * @see ControllerContext
	 */
	public HttpServletResponse getResponse()
	{
		return this.getRealResponse();
	}

	/**
     * <p>
     * Returns our ServletConfig.
     * </p>
     * @return our ServletConfig
	 * @see ControllerContext
	 */
	public ServletConfig getServletConfig()
	{
		return this.dispatcher.getServletConfig();
	}
	
	/**
	 * @see ControllerContext
	 * @see ViewContext
	 * @see TransformContext
	 */
	public ServletContext getServletContext()
	{
		return this.dispatcher.getServletContext();
	}
	
	/**
	 * @see ControllerContext
	 */
	public void setControllerParam(String name, Object value)
	{
		if (this.controllerParams == null)
			this.controllerParams = new HashMap();
			
		this.controllerParams.put(name, value);
	}

	/**
	 * @see ControllerContext
	 */
	public void setViewParam(String name, Object value)
	{
		if (this.viewParams == null)
			this.viewParams = new HashMap();
			
		this.viewParams.put(name, value);
	}

	/**
	 * @see ControllerContext
	 */
	public void setTransformParam(String name, Object value)
	{
		if (this.transformParams == null)
			this.transformParams = new HashMap();
			
		this.transformParams.put(name, value);
	}

	/**
	 * Appends to existing parameters.
	 */
	public void putAllControllerParams(Map addParams)
	{
		if (this.controllerParams == null)
			this.controllerParams = new HashMap();
			
		this.controllerParams.putAll(addParams);
	}

	/**
	 * Appends to existing parameters.
	 */
	public void putAllViewParams(Map addParams)
	{
		if (this.viewParams == null)
			this.viewParams = new HashMap();
			
		this.viewParams.putAll(addParams);
	}

	/**
	 * Appends to existing parameters.
	 */
	public void putAllTransformParams(Map addParams)
	{
		if (this.transformParams == null)
			this.transformParams = new HashMap();
			
		this.transformParams.putAll(addParams);
	}

	/**
	 * @see ControllerContext
	 */
	public void setModel(Object mod)
	{
		this.model = mod;
	}
	
	/**
	 * @see ControllerContext
	 * @see ViewContext
	 */
	public Object getModel()
	{
		return this.model;
	}
	
	/**
	 * @see ControllerContext
	 */
	public Map getControllerParams()
	{
		return this.controllerParams;
	}
	
	/**
	 * @see ViewContext
	 */
	public Map getViewParams()
	{
		return this.viewParams;
	}
	
	/**
	 * @see TransformContext
	 */
	public Map getTransformParams()
	{
		return this.transformParams;
	}
	
	/**
	 */
	public void setTransforms(Transform[] trans)
	{
		this.transforms = trans;

		// Set the transformCount based on the transform limit parameter		
		this.transformCount = determineMaxTransforms();
		
		if (this.transformCount > this.transforms.length)
			this.transformCount = this.transforms.length;

		if (log.isDebugEnabled())
			log.debug("Set " + trans.length + " transform(s), of which "
							+ this.transformCount + " will be executed");
	}

	/**
	 * @see ViewContext
	 * @see TransformContext
	 */
	public TransformStep getNextStep() throws ServletException
	{
		if (log.isDebugEnabled())
			log.debug("Creating transform step " + this.nextTransform);
			
		if (this.nextTransform >= this.transformCount)
		{
			log.debug("...which is the LastStep");
			return new LastStep(this);
		}
		else
		{
			Transform t = this.transforms[this.nextTransform++];
			return t.createStep(this);
		}
	}

	/**
	 * @see TransformContext
	 */
	public boolean halting()
	{
		return (this.transformCount != this.transforms.length);
	}

	/**
	 * Convenient method for obtaining the maximum number of transformations
	 * to allow in the pipeline.  Uses a request parameter whose name is
	 * defined by the limitTransformsParam property on the Dispatcher, and
	 * which should have an integer value.
	 * If nothing is specified or transform limiting is disabled (because
	 * limitTransformsParam is null), this returns Integer.MAX_VALUE.
	 *
	 * @return The maximum number of transforms allowed, possibly
	 *  Integer.MAX_VALUE.
	 * @throws NumberFormatException if the form parameter could not be
	 *	converted to an integer.
	 */
	protected int determineMaxTransforms()
	{
		if (this.dispatcher.getLimitTransformsParam() == null)
			return Integer.MAX_VALUE;

		String maxTransStr = this.request.getParameter(this.dispatcher.getLimitTransformsParam());

		if (maxTransStr == null)
			return Integer.MAX_VALUE;

		return Integer.parseInt(maxTransStr);
	}

}
