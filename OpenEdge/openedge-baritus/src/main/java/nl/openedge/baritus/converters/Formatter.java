package nl.openedge.baritus.converters;

/**
 * Interface for formatting values.
 * 
 * @author Eelco Hillenius
 */
public interface Formatter
{

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
	public String format(Object value, String pattern) throws IllegalArgumentException;
}
