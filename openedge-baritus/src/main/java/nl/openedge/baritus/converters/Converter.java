package nl.openedge.baritus.converters;

/**
 * General purpose data type converter. For this interface has the same signature of the
 * BeanUtils package, so it's easy to convert/ re-use converters.
 * 
 * @author Eelco Hillenius
 */
public interface Converter
{
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
	public Object convert(Class< ? > type, Object value);
}
