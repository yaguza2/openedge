package nl.openedge.baritus.converters;

import java.util.Locale;

/**
 * localized short converter
 * 
 * @author Eelco Hillenius
 */
public class ShortLocaleConverter extends DecimalLocaleConverter
{
	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. The locale is the default locale for this instance of the
	 * Java Virtual Machine and an unlocalized pattern is used for the conversion.
	 */
	public ShortLocaleConverter()
	{
		this(Locale.getDefault());
	}

	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. An unlocalized pattern is used for the conversion.
	 * 
	 * @param locale
	 *            The locale
	 */
	public ShortLocaleConverter(Locale locale)
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
	 *            The conversion pattern
	 */
	public ShortLocaleConverter(Locale locale, String pattern)
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
	public ShortLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		super(locale, pattern, locPattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type.
	 * 
	 * @param type
	 *            Data type to which this value should be converted
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the conversion
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	public Object convert(Class< ? > type, Object value, String pattern)
	{
		if (value == null)
		{
			return null;
		}

		Number temp = getNumber(value, pattern);

		return (temp instanceof Short) ? (Short) temp : Short.valueOf(temp.shortValue());
	}
}
