/*
 * $Id: RegexValidator.java,v 1.1.1.1 2004-02-24 20:34:16 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:34:16 $
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
package nl.openedge.baritus.validation;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.openedge.baritus.FormBeanContext;

import org.apache.commons.beanutils.ConvertUtils;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * A validator that tests for a match against a regex pattern.
 * if property 'mode' is MODE_VALID_IF_MATCHES (which is the default), isValid returns
 * true if the input matches the pattern. If property mode is MODE_INVALID_IF_MATCHES
 * (i.e. else), isValid returns false if the input matches the pattern.
 * 
 * @author Eelco Hillenius
 */
public class RegexValidator extends AbstractFieldValidator
{
	/** when in this mode, isValid will return true if the input matches the pattern */
	public final static int MODE_VALID_IF_MATCHES = 0;
	
	/** when in this mode, isValid will return false if the input matches the pattern */
	public final static int MODE_INVALID_IF_MATCHES = 1;

	/* defaults to valid if matches */
	private int mode = MODE_VALID_IF_MATCHES;
	
	/* the regexp pattern to match against */
	private Pattern pattern = null;

	/**
	 * construct without parameters
	 */
	public RegexValidator()
	{
		super("invalid.input");
	}
	
	/**
	 * construct with pattern
	 * @param pattern
	 */
	public RegexValidator(Pattern pattern)
	{
		this();
		setPattern(pattern);
	}
	
	/**
	 * construct with message prefix and pattern
	 * @param messagePrefix 
	 * @param pattern
	 */
	public RegexValidator(String messagePrefix, Pattern pattern)
	{
		super(messagePrefix);
		setPattern(pattern);
	}
	
	/**
	 * construct with message prefix, rule and pattern 
	 * @param messagePrefix
	 * @param rule
	 * @param pattern
	 */
	public RegexValidator(String messagePrefix, ValidatorActivationRule rule, Pattern pattern)
	{
		super(messagePrefix, rule);
		setPattern(pattern);
	}
	
	/**
	 * construct with pattern and mode
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(Pattern pattern, int mode)
	{
		this();
		setPattern(pattern);
		setMode(mode);
	}
	
	/**
	 * construct with message prefix, pattern and mode
	 * @param messagePrefix 
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(String messagePrefix, Pattern pattern, int mode)
	{
		super(messagePrefix);
		setPattern(pattern);
		setMode(mode);
	}
	
	/**
	 * construct with message prefix, rule, pattern and mode 
	 * @param messagePrefix
	 * @param rule
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(
		String messagePrefix, ValidatorActivationRule rule, Pattern pattern, int mode)
	{
		super(messagePrefix, rule);
		setPattern(pattern);
		setMode(mode);
	}

	/**
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	public boolean isValid(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value)
	{
		if(pattern == null) throw new RuntimeException("pattern is not provided!");
		
		String toMatch = ConvertUtils.convert(value); // convert to String
		Matcher matcher = pattern.matcher(toMatch); // create a matcher

		return (mode == MODE_VALID_IF_MATCHES) ? matcher.matches() : !matcher.matches();
	}
	
	/**
	 * get the error message. default returns the resource bundle message where
	 * key = messagePrefixwith {0} substituted with the value
	 * and {1} substituted with the field name
	 * @see nl.openedge.baritus.FieldValidator#getErrorMessage(org.infohazard.maverick.flow.ControllerContext, nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String getErrorMessage(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
		String fieldName,
		Object value,
		Locale locale)
	{
		
		String key = getMessagePrefix();
		return getLocalizedMessage(key, locale, new Object[]{value, fieldName});
	}
	
	//---------------------------------- PROPERTIES ----------------------------------------

	/**
	 * get mode
	 * @return int
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * get pattern
	 * @return Pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * set mode
	 * @param i mode
	 */
	public void setMode(int i)
	{
		mode = i;
	}

	/**
	 * set pattern
	 * @param pattern regex pattern
	 */
	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

}
