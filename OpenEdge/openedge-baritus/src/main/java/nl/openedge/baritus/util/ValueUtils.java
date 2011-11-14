package nl.openedge.baritus.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.converters.ConversionException;
import nl.openedge.baritus.converters.Converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Misc utility methods for handling values
 * 
 * @author Eelco Hillenius
 */
public final class ValueUtils
{

	/* logger */
	private static Logger log = LoggerFactory.getLogger(ValueUtils.class);

	/**
	 * check if the value is null or empty
	 * 
	 * @param value
	 *            object to check on
	 * @return true if value is not null AND not empty (e.g. in case of a String or
	 *         Collection)
	 */
	public static boolean isNullOrEmpty(Object value)
	{
		if (value instanceof String)
		{
			return "".equals(((String) value).trim());
		}
		if (value instanceof Object[])
		{
			if (((Object[]) value).length == 0)
			{
				return true;
			}
			else if (((Object[]) value).length == 1)
			{
				return isNullOrEmpty(((Object[]) value)[0]);
			}
			else
			{
				return false;
			}
		}
		else if (value instanceof Collection)
		{
			return ((Collection< ? >) value).isEmpty();
		}
		else if (value instanceof Map)
		{
			return ((Map< ? , ? >) value).isEmpty();
		}
		else
		{
			return value == null;
		}
	}

	/**
	 * Convert the specified value into a String. If the specified value is an array, the
	 * first element (converted to a String) will be returned. The registered
	 * {@link Converter} for the <code>java.lang.String</code> class will be used, which
	 * allows applications to customize Object->String conversions (the default
	 * implementation simply uses toString()).
	 * 
	 * @param value
	 *            Value to be converted (may be null)
	 */
	public static String convertToString(Object value)
	{

		if (value == null)
		{
			return null;
		}
		else if (value.getClass().isArray())
		{
			if (Array.getLength(value) < 1)
			{
				return (null);
			}
			value = Array.get(value, 0);
			if (value == null)
			{
				return null;
			}
			else
			{
				try
				{
					Converter converter = ConverterRegistry.getInstance().lookup(String.class);
					Object converted = converter.convert(String.class, value);
					return (converted instanceof String) ? (String) converted : String
						.valueOf(converted);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					throw new ConversionException(e);
				}
			}
		}
		else
		{
			try
			{
				Converter converter = ConverterRegistry.getInstance().lookup(String.class);
				Object converted = converter.convert(String.class, value);
				return (converted instanceof String) ? (String) converted : String
					.valueOf(converted);
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
				throw new ConversionException(e);
			}
		}

	}

}
