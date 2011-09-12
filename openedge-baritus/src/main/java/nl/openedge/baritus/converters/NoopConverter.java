package nl.openedge.baritus.converters;

/**
 * Converter that does nothing at all! Used for fallthrough; if really no converter is
 * found at all, this one is used
 * 
 * @author Eelco Hillenius
 */
public final class NoopConverter implements Converter
{
	/**
	 * noop; return value as was provided
	 * 
	 * @see Converter#convert(java.lang.Class, java.lang.Object)
	 */
	@Override
	public Object convert(Class< ? > type, Object value)
	{
		return value;
	}
}
