/*
 * $Id$
 * $Revision$
 * $Date$
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
package nl.openedge.maverick.framework;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.openedge.modules.impl.usermanager.hib.UserPrincipal;

/**
 * Baseclass for forms
 * @author Eelco Hillenius
 */
public abstract class AbstractForm
{
	
	/** last request */
	protected String lastreq;

	/** should we redirect? */
	protected boolean redirect = false;

	/** validated user */
	protected UserPrincipal user = null;
	
	/** errors */
	protected Map errors = null;
	
	/** overriden values as strings */
	protected Map overrideFields = null;
	
	/** error key for stacktrace if any */
	public final static String ERROR_KEY_STACKTRACE = "stacktrace";
	
	/** error key for stacktrace if any */
	public final static String ERROR_KEY_EXCEPTION = "exception";
	
	/**
	 * construct empty
	 */
	public AbstractForm() 
	{
		// do nothing	
	}
	
	/**
	 * @return String
	 */
	public String getLastreq()
	{
		return lastreq;
	}

	/**
	 * Sets the lastreq.
	 * @param lastreq The lastreq to set
	 */
	public void setLastreq(String lastreq)
	{
		this.lastreq = lastreq;
	}

	/**
	 * @return boolean
	 */
	public boolean isRedirect()
	{
		return redirect;
	}

	/**
	 * Sets the redirect.
	 * @param redirect The redirect to set
	 */
	public void setRedirect(boolean redirectHint)
	{
		this.redirect = redirectHint;
	}
	
	/**
	 * get user
	 * @return UserPrincipal
	 */
	public UserPrincipal getUser()
	{
		return user;
	}

	/**
	 * set user
	 * @param user 
	 */
	public void setUser(UserPrincipal user)
	{
		this.user = user;
	}

	/**
	 * @return Map
	 */
	public Map getErrors() 
	{
		return errors;
	}
	
	/**
	 * get error for field
	 * @param field
	 * @return String
	 */
	public String getError(String field) 
	{
		return (errors != null) ? (String)errors.get(field) : null;
	}

	/**
	 * Sets the errors.
	 * @param errors The errors to set
	 */
	public void setErrors(Map errors) 
	{
		this.errors = errors;
	}

	/**
	 * either add this exception with the given key, or add the stacktrace
	 * of this exception with the given key
	 * @param key key to store error under
	 * @param t exception
	 * @param asStackTrace if true, the stacktrace is added; otherwise the exception
	 *  is added
	 */
	public void setError(String key, Throwable t, boolean asStackTrace) 
	{
		String value = null;	
		if(asStackTrace) 
		{
			value = getErrorMessage(t);
		} 
		else 
		{
			value = t.getMessage();
		}
		setError(key, value);
	}
	
	/**
	 * add exception and its stacktrace
	 * @param exceptionKey key to use for exception
	 * @param stackTraceKey key to use for stacktrace
	 * @param t exception
	 */
	public void setError(String exceptionKey, String stackTraceKey, Throwable t) 
	{		
		String stackTrace = getErrorMessage(t);
		String errorMessage = t.getMessage();
		setError(stackTraceKey, stackTrace);
		setError(exceptionKey, errorMessage);
	}

	/**
	 * adds an exception with key 'exception' and adds the stacktrace
	 * of this exception with key 'stacktrace'
	 * @param t exception
	 */
	public void setError(Throwable t) 
	{
		setError(ERROR_KEY_EXCEPTION, ERROR_KEY_STACKTRACE, t);
	}
	
	/**
	 * adds an exception with key 'exception' and adds either the stacktrace
	 * of this exception with key 'stacktrace' if asStackTrace is true, or add the
	 * exception message with key 'exception' if asStackTrace is false
	 * @param t exception
	 * @param asStackTrace if true, the stacktrace is added; otherwise the exception
	 */
	public void setError(Throwable t, boolean asStackTrace) 
	{
		setError(ERROR_KEY_EXCEPTION, t, asStackTrace);
	}
	
	/** add or overwrite an error */
	public void setError(String key, String value) 
	{
		if(errors == null) errors = new HashMap();
		errors.put(key, value);
	}

	
	/** add or overwrite an error */
	public void removeError(String key) 
	{
		if(errors != null) 
		{
			errors.remove(key); 
		} 
	}

	/*
	 * get errormessage; try stacktrace
	 */
	private String getErrorMessage(Throwable t) 
	{
		String msg;
		try 
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(bos);
			t.printStackTrace(pw);
			pw.flush();
			pw.close();
			bos.flush();
			bos.close();
			msg = bos.toString();
		} 
		catch (Exception e) 
		{
			msg = t.getMessage();
		}
		return msg;
	}
	
	/**
	 * get map of failed field values
	 * @return Map
	 */
	public Map getOverrideFields()
	{
		return overrideFields;
	}

	/**
	 * set value of field that overrides
	 * @param name name of the field/ property
	 * @param value the string value (from HTML field)
	 */
	public void setOverrideField(String name, Object value) 
	{
		if(overrideFields == null) 
		{
			overrideFields = new HashMap();
		}
		overrideFields.put(name, value);
	}
	
	/**
	 * set value of field that overrides
	 * @param name name of the field/ property
	 * @param value the string value (from HTML field)
	 */
	public void setOverrideField(Map fields) 
	{
		if(overrideFields == null) 
		{
			overrideFields = new HashMap();
		}
		overrideFields.putAll(fields);
	}
	
	/**
	 * get string value of field that overrides
	 * E.g. we got in a formbean property 'myDate' of type date. 
	 * If form submit gives 'blah', this cannot be parsed as a date.
	 * Now, if setFailedField('myDate', 'blah') is called, 
	 * the view can show the 'wrong' value transparently, as an 
	 * Velocity EventCardridge will override any property with a 
	 * failed field if set.
	 * @param name
	 * @return String
	 */
	public String getOverrideField(String name) 
	{
		return (overrideFields != null) ? (String)overrideFields.get(name) : null;
	}
	
// ----------------------- UTILITY METHODS -----------------------------//

	/**
	 * check if the value is null or empty
	 * @param value object to check on
	 * @return true if value is not null AND not empty (e.g. 
	 * in case of a String or Collection)
	 */
	public boolean isNullOrEmpty(Object value) 
	{	
		if(value instanceof String)
		{
			return (value == null || (((String)value).trim().equals("")) );	
		}
		else if(value instanceof Collection)
		{
			return (value == null || (((Collection)value).isEmpty()) );	
		}
		else
		{
			return (value == null);
		}
	}

}