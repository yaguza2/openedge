/*
 * $Id: OgnlFieldPopulator.java,v 1.6 2004-05-23 10:26:58 eelco12 Exp $
 * $Revision: 1.6 $
 * $Date: 2004-05-23 10:26:58 $
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
package nl.openedge.baritus.population;

import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.LogConstants;
import nl.openedge.baritus.converters.ConversionException;

/**
 * OGNL populator for bean properties. Tries to set a property using OGNL.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public final class OgnlFieldPopulator extends AbstractFieldPopulator
{

	private static Log populationLog = LogFactory.getLog(LogConstants.POPULATION_LOG);
	
	private final static OgnlConverterWrapper converter = new OgnlConverterWrapper();
	
	/** context key for current locale */
	public final static String CTX_KEY_CURRENT_LOCALE = "__currentLocale";
	
	/** context key for current target type */
	public final static String CTX_KEY_CURRENT_TARGET_TYPE = "__currentTargetType";

	/** context key for current execution parameters */
	public final static String CTX_KEY_CURRENT_EXEC_PARAMS = "__currentExecParams";
	
	/** context key for current field name expression */
	public final static String CTX_KEY_CURRENT_FIELD_NAME = "__currentFieldName";
	
	/** context key for current tried value */
	public final static String CTX_KEY_CURRENT_TRIED_VALUE = "__currentTriedValue";
	
	/** context key for current converter */
	public final static String CTX_KEY_CURRENT_CONVERTER = "__currentConverter";

	/**
	 * construct with reference to the control
	 * @param ctrl
	 */
	public OgnlFieldPopulator(FormBeanCtrlBase ctrl)
	{
		super(ctrl);
	}

	/**
	 * set a property
	 * @param cctx maverick context
	 * @param formBeanContext context with instance of the form bean to set the property on
	 * @param name name of the property
	 * @param value unconverted value to set
	 * @throws Exception
	 */
	public boolean setProperty(
		ControllerContext cctx,	
		FormBeanContext formBeanContext,
		String name,
		Object value)
		throws Exception
	{

		boolean success = true;
		Object bean = formBeanContext.getBean();
		
		ExecutionParams params = formBeanContext.getController().getExecutionParams(cctx);
		
		OgnlContext context = new OgnlContext();
		context.setTypeConverter(converter);
		context.put(CTX_KEY_CURRENT_LOCALE, formBeanContext.getCurrentLocale());
		context.put(CTX_KEY_CURRENT_EXEC_PARAMS, params);
		context.put(CTX_KEY_CURRENT_FIELD_NAME, name);
		
		// trim input string values if required
		if(params.isTrimStringInputValues())
		{
			if(value instanceof String)
			{
				value = ((String)value).trim();
			}
			else if(value instanceof String[])
			{
				String[] _value = (String[])value;
				for(int i = 0; i < _value.length; i++)
				{
					_value[i] = _value[i].trim();
				}
			}
		}
		
		try
		{
			Ognl.setValue(name, context, bean, value);
		}
		catch (OgnlException e)
		{
			if(e instanceof NoSuchPropertyException)
			{
				// just ignore and log warning
				populationLog.warn("property '" + name + "' not found for bean " + bean);
			}
			else
			{
				if(e.getReason() instanceof ConversionException)
				{
					Class targetType = (Class)context.get(CTX_KEY_CURRENT_TARGET_TYPE);
					value = context.get(CTX_KEY_CURRENT_TRIED_VALUE);
					ctrl.setConversionErrorForField(
						cctx, formBeanContext, targetType, name, value, e);
					ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
					success = false;					
				}
				else
				{
					if(params.isStrictPopulationMode())
					{
						populationLog.error(e.getMessage(), e);
						value = context.get(CTX_KEY_CURRENT_TRIED_VALUE);
						formBeanContext.setError(name, e.getMessage());	
						ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
						success = false;	
					}
					else
					{
						// just ignore and log a warning
						if(populationLog.isDebugEnabled())
						{
							populationLog.warn(e.getMessage(), e);						
						}
						else
						{
							populationLog.warn(e.getMessage());							
						}
					}
				}
			}
		}
		catch(ConversionException e)
		{
			Class targetType = (Class)context.get(CTX_KEY_CURRENT_TARGET_TYPE);
			ctrl.setConversionErrorForField(
				cctx, formBeanContext, targetType, name, value, e);
			ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
			success = false;			
		}

		return success;

	}

}
