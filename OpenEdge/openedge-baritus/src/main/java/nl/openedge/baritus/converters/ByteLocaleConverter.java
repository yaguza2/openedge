package nl.openedge.baritus.converters;

import java.text.ParseException;
import java.util.Locale;

/**
 * localized byte converter
 * 
 * @author Eelco Hillenius
 */
public class ByteLocaleConverter extends DecimalLocaleConverter
{
	/**
	 * Create a {@link LocaleConverter} that will throw a {@link ConversionException} if a
	 * conversion error occurs. The locale is the default locale for this instance of the
	 * Java Virtual Machine and an unlocalized pattern is used for the conversion.
	 */
	public ByteLocaleConverter()
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
	public ByteLocaleConverter(Locale locale)
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
	public ByteLocaleConverter(Locale locale, String pattern)
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
	 *            The convertion pattern
	 * @param locPattern
	 *            Indicate whether the pattern is localized or not
	 */
	public ByteLocaleConverter(Locale locale, String pattern, boolean locPattern)
	{
		super(locale, pattern, locPattern);
	}

	/**
	 * Convert the specified locale-sensitive input object into an output object of the
	 * specified type. This method will return values of type Byte.
	 * 
	 * @param value
	 *            The input object to be converted
	 * @param pattern
	 *            The pattern is used for the convertion
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	protected Object parse(Object value, String pattern) throws ParseException
	{
		final Number parsed = (Number) super.parse(value, pattern);
		if (parsed.longValue() != parsed.byteValue())
		{
			throw new ConversionException("Supplied number is not of type Byte: "
				+ parsed.longValue());
		}
		// now return property Byte
		return Byte.valueOf(parsed.byteValue());
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

		return (temp instanceof Byte) ? (Byte) temp : Byte.valueOf(temp.byteValue());
	}
}
