/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework;

/**
 * @author Eelco Hillenius
 */
public final class ExecutionParams
{

	/** if true, the no cache headers will be set */
	private boolean noCache = true;
	
	/**
	 * If we get an empty string, should it be translated to a null (true) or should
	 * the empty String be kept (false). This property is true by default
	 */
	private boolean setNullForEmptyString = true;
	
	/**
	 * Indicates whether we should use the configuration parameters of the controller 
	 * for the population process as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 *  
	 */	
	private boolean includeControllerParameters = false;
	
	/**
	 * Indicates whether we should use the session attributes of the current session
	 * for the population process as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 * 
	 */	
	private boolean includeSessionAttributes = false;
	
	/**
	 * Indicates whether we should use the currentRequest attributes for the population process
	 * as well. This property is false by default.
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * A map with parameters for the population process is filled in the above order, which
	 * means that every step can overwrite it's predecessor.
	 * 
	 */	
	private boolean includeRequestAttributes = false;
	
	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true
	 */
	private boolean doFormValidationIfFieldValidationFailed = true;
	
	/**
	 * Indicates whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false
	 */
	private boolean doPerformIfFieldValidationFailed = false;
	
	/**
	 * If true, reuse the context for multiple invocations within the same request.
	 * Default is true.
	 */
	private boolean reuseFormBeanContext = true;

	/**
	 * if true, the no cache headers will be set
	 * @return boolean
	 */
	public boolean isNoCache()
	{
		return noCache;
	}

	/**
	 * if true, the no cache headers will be set
	 * @param noCache
	 */
	public void setNoCache(boolean noCache)
	{
		this.noCache = noCache;
	}

	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 * @return boolean
	 */
	public boolean isSetNullForEmptyString()
	{
		return setNullForEmptyString;
	}

	/**
	 * If we get an empty string it should be translated to a null (true) or to
	 * an empty String false. This property is true by default
	 * @param b
	 */
	public void setSetNullForEmptyString(boolean b)
	{
		setNullForEmptyString = b;
	}
	
	/**
	 * include controller parameters in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include controller parameters in the population process
	 */
	public boolean isIncludeControllerParameters()
	{
		return includeControllerParameters;
	}
	
	/**
	 * set whether to include controller parameters in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include controller parameters in the population process
	 */
	public void setIncludeControllerParameters(boolean b)
	{
		includeControllerParameters = b;
	}

	/**
	 * include session attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include controller parameters in the population process
	 */
	public boolean isIncludeSessionAttributes()
	{
		return includeSessionAttributes;
	}

	/**
	 * set whether to include session attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include session attributes in the population process
	 */
	public void setIncludeSessionAttributes(boolean b)
	{
		includeSessionAttributes = b;
	}

	/**
	 * include request attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @return boolean include request parameters in the population process
	 */
	public boolean isIncludeRequestAttributes()
	{
		return includeRequestAttributes;
	}

	/**
	 * set whether to include request attributes in the population process
	 * 
	 * The order in which parameters/ attributes are used for population:
	 * 1. controller parameters (if includeControllerParameters == true)
	 * 2. session attributes (if includeSessionAttributes == true)
	 * 3. request parameters
	 * 4. request attributes (if includeRequestAttributes == true)
	 * 
	 * @param b set whether to include request attributes in the population process
	 */
	public void setIncludeRequestAttributes(boolean b)
	{
		includeRequestAttributes = b;
	}

	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true.
	 * 
	 * @return boolean Whether the form validators should be executed when one of the 
	 * 		field validators failed.
	 */
	public boolean isDoFormValidationIfFieldValidationFailed()
	{
		return doFormValidationIfFieldValidationFailed;
	}

	/**
	 * Indicates whether the form validators should be executed when one of the 
	 * field validators failed. Default == true.
	 * 
	 * @param b whether the form validators should be executed when one of the 
	 * 		field validators failed.
	 */
	public void setDoFormValidationIfFieldValidationFailed(boolean b)
	{
		doFormValidationIfFieldValidationFailed = b;
	}

	/**
	 * Whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false.
	 * 
	 * @return boolean whether the perform method of the control should be executed, even if the population/
	 * validation failed.
	 */
	public boolean isDoPerformIfFieldValidationFailed()
	{
		return doPerformIfFieldValidationFailed;
	}

	/**
	 * Whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false.
	 * 
	 * @param b whether the perform method of the control should be executed, even if the population/
	 * validation failed.
	 */
	public void setDoPerformIfFieldValidationFailed(boolean b)
	{
		doPerformIfFieldValidationFailed = b;
	}

	/**
	 * Whether to reuse the context for multiple invocations within the same request.
	 * Default is true.
	 * @return reuse the context for multiple invocations within the same request
	 */
	public boolean isReuseFormBeanContext()
	{
		return reuseFormBeanContext;
	}

	/**
	 * Set whether to reuse the context for multiple invocations within the same request.
	 * Default is true.
	 * @param b reuse the context for multiple invocations within the same request
	 */
	public void setReuseFormBeanContext(boolean b)
	{
		reuseFormBeanContext = b;
	}

}
