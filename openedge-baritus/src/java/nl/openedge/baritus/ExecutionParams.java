/*
 * $Id: ExecutionParams.java,v 1.4 2004-03-02 13:23:22 eelco12 Exp $
 * $Revision: 1.4 $
 * $Date: 2004-03-02 13:23:22 $
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.baritus;

/**
 * Each instance of FormBeanCtrl keeps an instance of ExecutionParams.
 * ExecutionParams are used to influence the execution of population,
 * validation and the general execution of FormBeanCtrl.
 * 
 * If you want to override the defaults, you can override method init in your
 * controller, using getExecutionParams() to get the reference to the 
 * execution params that will be used for the instance of your controller.
 * 
 * For more finegrained (per request) control, another option is to override
 * method getExecutionParams() itself. This method will be called each request
 * and by default just returns the instance variable. You can vary
 * the processing by returning a new instance of ExecutionParams
 * (be carefull NOT to just change values in the instance variable, as this is
 * shared by all clients), with its properties set for that request.
 * 
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
	private boolean doPerformIfPopulationFailed = false;
	
	/**
	 * If true, reuse the context for multiple invocations within the same request.
	 * Default is true.
	 */
	private boolean reuseFormBeanContext = true;
	
	/**
	 * If population or validation failes and this property is true,
	 * all request parameters will be saved as override values. This
	 * will give you at least the full request the client sent, and
	 * guards you for the situation where properties that normally
	 * would be loaded in the command method are not set because
	 * of the population/ validation failure.
	 * Default is true.
	 */
	private boolean saveReqParamsAsOverrideFieldsOnError = true;

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
	public boolean isDoPerformIfPopulationFailed()
	{
		return doPerformIfPopulationFailed;
	}

	/**
	 * Whether the perform method of the control should be executed, even if the population/
	 * validation failed. Use this only in very special cases; extended usage will
	 * probably result in messy code. Default == false.
	 * 
	 * @param b whether the perform method of the control should be executed, even if the population/
	 * validation failed.
	 */
	public void setDoPerformIfPopulationFailed(boolean b)
	{
		doPerformIfPopulationFailed = b;
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

	/**
	 * Gets whether all request parameters should be saved as override
	 * 	values when population or validation fails.
	 * If population or validation failes and this property is true,
	 * all request parameters will be saved as override values. This
	 * will give you at least the full request the client sent, and
	 * guards you for the situation where properties that normally
	 * would be loaded in the command method are not set because
	 * of the population/ validation failure.
	 * Default is true.

	 * @return boolean wheter all request parameters should be saved as override
	 * 	values when population or validation fails.
	 */
	public boolean isSaveReqParamsAsOverrideFieldsOnError()
	{
		return saveReqParamsAsOverrideFieldsOnError;
	}

	/**
	 * Sets whether all request parameters should be saved as override
	 * 	values when population or validation fails.
	 * If population or validation failes and this property is true,
	 * all request parameters will be saved as override values. This
	 * will give you at least the full request the client sent, and
	 * guards you for the situation where properties that normally
	 * would be loaded in the command method are not set because
	 * of the population/ validation failure.
	 * Default is true.

	 * @param b
	 */
	public void setSaveReqParamsAsOverrideFieldsOnError(boolean b)
	{
		saveReqParamsAsOverrideFieldsOnError = b;
	}

}
