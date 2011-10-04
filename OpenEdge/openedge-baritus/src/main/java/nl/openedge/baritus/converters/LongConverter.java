package nl.openedge.baritus.converters;

/**
 * <p>
 * Standard {@link Converter} implementation that converts an incoming String into a
 * <code>java.lang.Long</code> object, optionally using a default value or throwing a
 * {@link ConversionException} if a conversion error occurs.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public final class LongConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 */
	public LongConverter()
	{
	}

	/**
	 * Convert the specified input object into an output object of the specified type.
	 * 
	 * @param type
	 *            Data type to which this value should be converted
	 * @param value
	 *            The input value to be converted
	 * 
	 * @exception ConversionException
	 *                if conversion cannot be performed successfully
	 */
	@Override
	public Object convert(Class< ? > type, Object value)
	{
		if (value == null)
		{
			return null;
		}

		if (value instanceof Long)
		{
			return (value);
		}
		else if (value instanceof Number)
		{
			return Long.valueOf(((Number) value).longValue());
		}

		try
		{
			return (Long.valueOf(value.toString()));
		}
		catch (Exception e)
		{
			throw new ConversionException(e);
		}
	}
}
