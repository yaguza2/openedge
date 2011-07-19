package nl.openedge.baritus.population;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.LogConstants;
import nl.openedge.baritus.converters.ConversionException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.infohazard.maverick.flow.ControllerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OGNL populator for bean properties. Tries to set a property using OGNL.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public final class OgnlFieldPopulator extends AbstractFieldPopulator
{
	private static Logger populationLog = LoggerFactory.getLogger(LogConstants.POPULATION_LOG);

	private final static OgnlConverterWrapper converter = new OgnlConverterWrapper();

	/** context key for current locale */
	public final static String CTX_KEY_CURRENT_LOCALE = "__currentLocale";

	/** context key for current target type */
	public final static String CTX_KEY_CURRENT_TARGET_TYPE = "__currentTargetType";

	/** context key for current execution parameters */
	public final static String CTX_KEY_CURRENT_EXEC_PARAMS = "__currentExecParams";

	/** context key for current field name expression */
	public final static String CTX_KEY_CURRENT_FIELD_NAME = "__currentFieldName";

	/** context key for current tried value */
	public final static String CTX_KEY_CURRENT_TRIED_VALUE = "__currentTriedValue";

	/** context key for current converter */
	public final static String CTX_KEY_CURRENT_CONVERTER = "__currentConverter";

	public OgnlFieldPopulator(FormBeanCtrlBase ctrl)
	{
		super(ctrl);
	}

	@Override
	public boolean setProperty(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldname, Object value)
	{
		boolean success = true;
		Object bean = formBeanContext.getBean();
		String name = fieldname.replace("[%22", "[\"").replace("%22]", "\"]");

		ExecutionParams params = formBeanContext.getController().getExecutionParams(cctx);

		OgnlContext context = new OgnlContext();
		context.setTypeConverter(converter);
		context.put(CTX_KEY_CURRENT_LOCALE, formBeanContext.getCurrentLocale());
		context.put(CTX_KEY_CURRENT_EXEC_PARAMS, params);
		context.put(CTX_KEY_CURRENT_FIELD_NAME, name);

		// trim input string values if required
		if (params.isTrimStringInputValues())
		{
			if (value instanceof String)
			{
				value = ((String) value).trim();
			}
			else if (value instanceof String[])
			{
				String[] _value = (String[]) value;
				for (int i = 0; i < _value.length; i++)
				{
					_value[i] = _value[i].trim();
				}
			}
		}

		try
		{
			Ognl.setValue(name, context, bean, value);
		}
		catch (OgnlException e)
		{
			if (e instanceof NoSuchPropertyException)
			{
				// just ignore and log warning
				populationLog.warn("property '" + name + "' not found for bean " + bean);
			}
			else
			{
				if (e.getReason() instanceof ConversionException)
				{
					Class< ? > targetType = (Class< ? >) context.get(CTX_KEY_CURRENT_TARGET_TYPE);
					value = context.get(CTX_KEY_CURRENT_TRIED_VALUE);
					ctrl.setConversionErrorForField(formBeanContext, targetType, name, value, e);
					ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
					success = false;
				}
				else
				{
					if (params.isStrictPopulationMode())
					{
						populationLog.error(e.getMessage(), e);
						value = context.get(CTX_KEY_CURRENT_TRIED_VALUE);
						formBeanContext.setError(name, e.getMessage());
						ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
						success = false;
					}
					else
					{
						// just ignore and log a warning
						if (populationLog.isDebugEnabled())
							populationLog.warn(e.getMessage(), e);
						else
							populationLog.warn(e.getMessage());
					}
				}
			}
		}
		catch (ConversionException e)
		{
			Class< ? > targetType = (Class< ? >) context.get(CTX_KEY_CURRENT_TARGET_TYPE);
			ctrl.setConversionErrorForField(formBeanContext, targetType, name, value, e);
			ctrl.setOverrideField(cctx, formBeanContext, name, value, e, null);
			success = false;
		}

		return success;
	}
}
