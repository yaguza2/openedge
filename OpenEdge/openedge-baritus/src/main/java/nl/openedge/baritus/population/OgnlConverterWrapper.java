package nl.openedge.baritus.population;

import java.lang.reflect.Member;
import java.util.Locale;
import java.util.Map;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.LogConstants;
import nl.openedge.baritus.converters.ConversionException;
import nl.openedge.baritus.converters.Converter;
import ognl.DefaultTypeConverter;
import ognl.OgnlOps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class should be registered with the OGNL context before parsing in order to be
 * able to use our converters. It implements OGNL TypeConverter and uses the
 * ConverterRegistry to lookup converters. If no converter is found for a given type, the
 * default conversion of OGNL is used.
 * 
 * @author Eelco Hillenius
 */
public class OgnlConverterWrapper extends DefaultTypeConverter
{
	private static Logger populationLog = LoggerFactory.getLogger(LogConstants.POPULATION_LOG);

	/**
	 * Convert the provided value to provided type using provided context.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Object convertValue(Map context, Object value, Class toType)
	{
		if (value == null)
			return null;

		Object converted = null;

		Locale locale = (Locale) context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_LOCALE);
		ExecutionParams executionParams =
			(ExecutionParams) context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_EXEC_PARAMS);

		if ((!toType.isArray()) && value instanceof String[] && ((String[]) value).length == 1)
		{
			value = ((String[]) value)[0];
		}

		if ((value instanceof String) && ((String) value).trim().equals("")
			&& executionParams.isSetNullForEmptyString())
		{
			if (populationLog.isDebugEnabled())
			{
				String name = (String) context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_FIELD_NAME);
				populationLog.debug("empty input for property " + name + " converted to null");
			}
			return null;
		}

		context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TRIED_VALUE, value);

		try
		{
			ConverterRegistry reg = ConverterRegistry.getInstance();
			Converter converter = reg.lookup(toType, locale);

			if (converter != null) // we found a converter
			{
				context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_CONVERTER, converter);

				if (populationLog.isDebugEnabled())
				{
					String name =
						(String) context.get(OgnlFieldPopulator.CTX_KEY_CURRENT_FIELD_NAME);
					populationLog.debug("using converter " + converter + " for property " + name
						+ " (type " + toType + ")");
				}

				if (!toType.isArray()) // a common case with request parameters is that
				// they are send as a string array instead of a plain string
				{
					if (value instanceof String[] && ((String[]) value).length == 1)
					{
						value = ((String[]) value)[0];
					}
				}

				converted = converter.convert(toType, value);
			}
			else
			// no converter was found
			{
				converted = OgnlOps.convertValue(value, toType);
			}
		}
		catch (ConversionException e)
		{
			context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TARGET_TYPE, toType);
			throw e;
		}
		catch (Exception e)
		{
			context.put(OgnlFieldPopulator.CTX_KEY_CURRENT_TARGET_TYPE, toType);
			throw new ConversionException(e);
		}

		return converted;
	}

	/**
	 * This method is only here to satisfy the interface. Method convertValue(Map, Object,
	 * Class) is called, so parameters member and propertyName are ignored.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object convertValue(Map context, Object target, Member member, String propertyName,
			Object value, Class toType)
	{
		return convertValue(context, value, toType);
	}
}
