package nl.openedge.baritus.converters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Modified {@link LocaleConverter} implementation for this framework
 * </p>
 * 
 * @author Yauheny Mikulski
 * @author Eelco Hillenius
 */
public abstract class DecimalLocaleConverter extends BaseLocaleConverter
{
	protected Pattern nonDigitPattern = Pattern.compile(".*[^0-9&&[^\\,]&&[^\\.]&&[^\\-]].*");

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. The locale is the default locale for this instance of the
	 * Java Virtual Machine and an unlocalized pattern is used for the conversion.
	 * 
	 */
	public DecimalLocaleConverter()
	{
		this(Locale.getDefault());
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. No pattern is used for the conversion.
	 * 
	 * @param locale
	 *            The locale
	 */
	public DecimalLocaleConverter(Locale locale)
	{
		this(locale, null);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the conversion.
	 * 
	 * @param locale
	 *            The locale
	 * @param pattern
	 *            The convertion pattern
	 */
	public DecimalLocaleConverter(Locale locale, String pattern)
	{
		this(locale, pattern, false);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 * 
	 * @param locale
	 *            The locale
	 * @param pattern
	 *            The conversion pattern
	 * @param locPattern
	 *            Indicate whether the pattern is localized or not
	 */
	public DecimalLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		super(locale, pattern, locPattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * 
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the conversion
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	protected Object parse(Object value, String pattern) throws ParseException
	{
		if (value == null)
			return null;
		DecimalFormat formatter = getFormat(pattern);

		return formatter.parse((String) value);
	}

	/**
	 * Convert the specified input object into a locale-sensitive output string
	 * 
	 * @param value
	 *            The input object to be formatted
	 * @param pattern
	 *            The pattern is used for the conversion
	 * 
	 * @exception IllegalArgumentException
	 *                if formatting cannot be performed successfully
	 */
	@Override
	public String format(Object value, String pattern) throws IllegalArgumentException
	{
		if (value == null)
			return null;

		DecimalFormat formatter = getFormat(pattern);
		return formatter.format(value);
	}

	/**
	 * get format and optionally apply pattern if given
	 * 
	 * @param pattern
	 *            pattern or null
	 * @return DecimalFormat formatter instance
	 */
	protected DecimalFormat getFormat(String pattern)
	{
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(locale);
		if (pattern != null)
		{
			if (locPattern)
			{
				formatter.applyLocalizedPattern(pattern);
			}
			else
			{
				formatter.applyPattern(pattern);
			}
		}
		return formatter;
	}

	/**
	 * translate value to a number optionally using the supplied pattern
	 * 
	 * @param value
	 *            the value to convert
	 * @param pattern
	 *            the patter to use (optional)
	 * @return Number
	 * @throws ConversionException
	 */
	protected Number getNumber(Object value, String pattern) throws ConversionException
	{
		if (value instanceof Number)
			return (Number) value;

		Number temp = null;
		try
		{
			if (pattern != null)
			{
				temp = (Number) parse(value, pattern);
			}
			else
			{
				String stringval = null;
				if (value instanceof String)
					stringval = (String) value;
				else if (value instanceof String[])
					stringval = ((String[]) value)[0];
				else
					stringval = String.valueOf(value);

				Matcher nonDigitMatcher = nonDigitPattern.matcher(stringval);
				if (nonDigitMatcher.matches())
				{
					throw new ConversionException(stringval + " is not a valid number");
				}

				temp = (Number) parse(value, this.pattern);
			}
		}
		catch (Exception e)
		{
			String dpat = null;
			if (pattern != null)
			{
				dpat = pattern;
			}
			else
			{
				DecimalFormat formatter = getFormat(pattern);
				dpat = formatter.toLocalizedPattern();
			}
			throw new ConversionException(e, dpat);
		}

		return temp;
	}
}
