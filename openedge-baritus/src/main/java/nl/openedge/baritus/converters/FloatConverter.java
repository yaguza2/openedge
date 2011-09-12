package nl.openedge.baritus.converters;

/**
 * <p>
 * {@link Converter} implementation that converts an incoming String into a
 * <code>java.lang.Float</code> object, throwing a {@link ConversionException} if a
 * conversion error occurs.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public final class FloatConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 */
	public FloatConverter()
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

		if (value instanceof Float)
		{
			return (value);
		}
		else if (value instanceof Number)
		{
			return new Float(((Number) value).floatValue());
		}

		try
		{
			return (new Float(value.toString()));
		}
		catch (Exception e)
		{
			throw new ConversionException(e);
		}
	}
}
