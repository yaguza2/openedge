package nl.openedge.baritus.converters;

import java.text.ParseException;
import java.util.Locale;

/**
 * base class for localized converters
 * 
 * @author Eelco Hillenius
 */
public abstract class BaseLocaleConverter implements LocaleConverter, Formatter
{
	/** The locale specified to our Constructor, by default - system locale. */
	protected Locale locale = Locale.getDefault();

	/** The default pattern specified to our Constructor, if any. */
	protected String pattern = null;

	/** The flag indicating whether the given pattern string is localized or not. */
	protected boolean locPattern = false;

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the convertion.
	 * 
	 * @param locale
	 *            The locale
	 * @param pattern
	 *            The convertion pattern
	 */
	protected BaseLocaleConverter(Locale locale, String pattern)
	{
		this(locale, pattern, false);
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if
	 * an conversion error occurs.
	 * 
	 * @param locale
	 *            The locale
	 * @param pattern
	 *            The convertion pattern
	 * @param locPattern
	 *            Indicate whether the pattern is localized or not
	 */
	protected BaseLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		if (locale != null)
		{
			this.locale = locale;
		}

		this.pattern = pattern;
		this.locPattern = locPattern;
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * 
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the convertion
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	abstract protected Object parse(Object value, String pattern) throws ParseException;

	/**
	 * Convert the specified locale-sensitive input object into an output object. The
	 * default pattern is used for the convertion.
	 * 
	 * @param value
	 *            The input object to be converted
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	public Object convert(Object value)
	{
		return convert(value, null);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object.
	 * 
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the convertion
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	public Object convert(Object value, String pattern)
	{
		return convert(null, value, pattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type. The default pattern is used for the convertion.
	 * 
	 * @param type
	 *            Data type to which this value should be converted
	 * @param value
	 *            The input object to be converted
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	public Object convert(Class< ? > type, Object value)
	{
		return convert(type, value, null);
	}

	/**
	 * get the locale
	 * 
	 * @return Locale
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * get the pattern
	 * 
	 * @return String
	 */
	public String getPattern()
	{
		return pattern;
	}

	/**
	 * set the locale
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * set the pattern
	 * 
	 * @param string
	 */
	public void setPattern(String string)
	{
		pattern = string;
	}
}
