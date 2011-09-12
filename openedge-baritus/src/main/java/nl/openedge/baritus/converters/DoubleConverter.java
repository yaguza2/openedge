package nl.openedge.baritus.converters;

/**
 * <p>
 * {@link Converter} implementation that converts an incoming String into a
 * <code>java.lang.Double</code> object, throwing a {@link ConversionException} if a
 * conversion error occurs.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public final class DoubleConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 */
	public DoubleConverter()
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
		else if (value instanceof Double)
		{
			return (value);
		}
		else if (value instanceof Number)
		{
			return new Double(((Number) value).doubleValue());
		}
		else
		{
			try
			{
				return (new Double(value.toString()));
			}
			catch (Exception e)
			{
				throw new ConversionException(e);
			}
		}
	}
}
