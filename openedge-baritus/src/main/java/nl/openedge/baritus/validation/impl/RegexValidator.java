package nl.openedge.baritus.validation.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.util.ValueUtils;
import nl.openedge.baritus.validation.AbstractFieldValidator;
import nl.openedge.baritus.validation.ValidationActivationRule;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Tests for a match against a regex pattern. if property 'mode' is MODE_VALID_IF_MATCHES
 * (which is the default), isValid returns true if the input matches the pattern. If
 * property mode is MODE_INVALID_IF_MATCHES (i.e. else), isValid returns false if the
 * input matches the pattern.
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

	private String errorMessageKey = "invalid.input";

	/**
	 * construct without parameters
	 */
	public RegexValidator()
	{

	}

	/**
	 * construct with pattern
	 * 
	 * @param pattern
	 */
	public RegexValidator(Pattern pattern)
	{
		setPattern(pattern);
	}

	/**
	 * construct with errorMessageKey and pattern
	 * 
	 * @param errorMessageKey
	 * @param pattern
	 */
	public RegexValidator(String errorMessageKey, Pattern pattern)
	{
		setErrorMessageKey(errorMessageKey);
		setPattern(pattern);
	}

	/**
	 * construct with errorMessageKey, rule and pattern
	 * 
	 * @param errorMessageKey
	 * @param rule
	 * @param pattern
	 */
	public RegexValidator(String errorMessageKey, ValidationActivationRule rule, Pattern pattern)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
		setPattern(pattern);
	}

	/**
	 * construct with pattern and mode
	 * 
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(Pattern pattern, int mode)
	{
		setPattern(pattern);
		setMode(mode);
	}

	/**
	 * construct with message prefix, pattern and mode
	 * 
	 * @param errorMessageKey
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(String errorMessageKey, Pattern pattern, int mode)
	{
		setErrorMessageKey(errorMessageKey);
		setPattern(pattern);
		setMode(mode);
	}

	/**
	 * construct with errorMessageKey, rule, pattern and mode
	 * 
	 * @param errorMessageKey
	 * @param rule
	 * @param pattern
	 * @param mode
	 */
	public RegexValidator(String errorMessageKey, ValidationActivationRule rule, Pattern pattern,
			int mode)
	{
		setErrorMessageKey(errorMessageKey);
		setValidationRule(rule);
		setPattern(pattern);
		setMode(mode);
	}

	/**
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldName, Object value)
	{
		if (pattern == null)
			throw new RuntimeException("pattern is not provided!");

		String toMatch = ValueUtils.convertToString(value); // convert to String
		Matcher matcher = pattern.matcher(toMatch); // create a matcher

		boolean valid = (mode == MODE_VALID_IF_MATCHES) ? matcher.matches() : !matcher.matches();

		if (!valid)
		{
			setErrorMessage(formBeanContext, fieldName, getErrorMessageKey(), new Object[] {
				getFieldName(formBeanContext, fieldName), value});
		}

		return valid;
	}

	// ---------------------------------- PROPERTIES
	// ----------------------------------------

	/**
	 * get mode
	 * 
	 * @return int
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * get pattern
	 * 
	 * @return Pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * set mode
	 * 
	 * @param i
	 *            mode
	 */
	public void setMode(int i)
	{
		mode = i;
	}

	/**
	 * set pattern
	 * 
	 * @param pattern
	 *            regex pattern
	 */
	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

	/**
	 * Get key of error message.
	 * 
	 * @return String key of error message
	 */
	public String getErrorMessageKey()
	{
		return errorMessageKey;
	}

	/**
	 * Set key of error message.
	 * 
	 * @param string
	 *            key of error message
	 */
	public void setErrorMessageKey(String string)
	{
		errorMessageKey = string;
	}

}
