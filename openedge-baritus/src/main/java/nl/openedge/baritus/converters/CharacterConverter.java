package nl.openedge.baritus.converters;

/**
 * <p>
 * {@link Converter} implementation that converts an incoming String into a
 * <code>java.lang.Character</code> object, throwing a {@link ConversionException} if a
 * conversion error occurs.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public final class CharacterConverter implements Converter
{
	/**
	 * Create a {@link Converter} that will throw a {@link ConversionException} if a
	 * conversion error occurs.
	 */
	public CharacterConverter()
	{
	}

	/**
	 * Convert the specified input object into an output object of the specified type.
	 * 
	 * @param type
	 *            Data type to which this value should be converted
	 * @param value
	 *            The input value to be converted
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

		if (value instanceof Character)
		{
			return (value);
		}

		try
		{
			return (Character.valueOf(value.toString().charAt(0)));
		}
		catch (Exception e)
		{
			throw new ConversionException(e);
		}
	}
}
