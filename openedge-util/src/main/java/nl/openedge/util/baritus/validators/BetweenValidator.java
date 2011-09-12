package nl.openedge.util.baritus.validators;

import java.util.Date;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.util.YearMonthDayHelper;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Validator that check whether a value is between a bottom and a top value of a range.
 * 
 * @author Peter Veenendaal (Levob)
 * @author Eelco Hillenius
 */
public class BetweenValidator extends AbstractRangeValidator
{
	/**
	 * msg key for an invalid date (default = invalid.field.input.between.date).
	 */
	private String msgKeyDateFailure = "invalid.field.input.between.date";

	/**
	 * msg key for an invalid number (default = invalid.field.input.between.number).
	 */
	private String msgKeyNumberFailure = "invalid.field.input.between.number";

	/**
	 * construct with bottom and top of range.
	 * 
	 * @param bottom
	 *            bottom of Srange
	 * @param top
	 *            top of range
	 */
	public BetweenValidator(Object bottom, Object top)
	{
		super(bottom, top);
	}

	/**
	 * construct with bottom and top, and including properties.
	 * 
	 * @param bottom
	 *            bottom of range
	 * @param top
	 *            top of range
	 * @param inclBottom
	 *            whether to include the bottom
	 * @param inclTop
	 *            whether to include the top
	 */
	public BetweenValidator(Object bottom, Object top, boolean inclBottom, boolean inclTop)
	{
		super(bottom, top, inclBottom, inclTop);
	}

	/**
	 * Check whether given value is within the range.
	 * 
	 * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
	 *      nl.openedge.baritus.FormBeanContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldname, final Object value)
	{
		boolean valid = true;
		if (value instanceof Number)
		{
			valid = handleIsValid(cctx, formBeanContext, fieldname, (Number) value);
		}
		else if (value instanceof Date)
		{
			valid = handleIsValid(cctx, formBeanContext, fieldname, (Date) value);
		}
		return valid;
	}

	/**
	 * Handle a number.
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            formbean context
	 * @param fieldname
	 *            field name
	 * @param number
	 *            number
	 * @return boolean whether given value is within the range
	 */
	protected boolean handleIsValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldname, Number number)
	{
		double topOfRange = ((Number) getTop()).doubleValue();
		double bottomOfRange = ((Number) getBottom()).doubleValue();
		double valueToCheck = number.doubleValue();
		boolean valid = false;
		if (isIncludingTop())
		{
			valid = (valueToCheck <= topOfRange);
		}
		else
		{
			valid = (valueToCheck < topOfRange);
		}
		if (valid)
		{
			if (isIncludingBottom())
			{
				valid = (valueToCheck >= bottomOfRange);
			}
			else
			{
				valid = (valueToCheck > bottomOfRange);
			}
		}
		if (!valid)
		{
			Object[] params =
				new Object[] {getFieldName(formBeanContext, fieldname),
					String.valueOf(bottomOfRange), String.valueOf(topOfRange), number};
			setErrorMessage(formBeanContext, fieldname, msgKeyNumberFailure, params);
		}
		return valid;
	}

	/**
	 * Handle a date.
	 * 
	 * @param cctx
	 *            controller context
	 * @param formBeanContext
	 *            formbean context
	 * @param fieldname
	 *            field name
	 * @param date
	 *            date
	 * @return whether given value is within the range
	 */
	protected boolean handleIsValid(ControllerContext cctx, FormBeanContext formBeanContext,
			String fieldname, Date date)
	{
		Date bottomOfRange = (Date) getBottom();
		Date topOfRange = (Date) getTop();
		boolean afterBottom;
		if (isIncludingBottom())
		{
			afterBottom = YearMonthDayHelper.afterOrSame(date, bottomOfRange);
		}
		else
		{
			afterBottom = YearMonthDayHelper.after(date, bottomOfRange);
		}
		boolean beforeTop;
		if (isIncludingTop())
		{
			beforeTop = YearMonthDayHelper.before(date, topOfRange);
		}
		else
		{
			beforeTop = YearMonthDayHelper.beforeOrSame(date, topOfRange);
		}

		boolean valid = (afterBottom && beforeTop);
		if (!valid)
		{
			Object[] params =
				new Object[] {getFieldName(formBeanContext, fieldname), bottomOfRange, topOfRange,
					date};
			setErrorMessage(formBeanContext, fieldname, msgKeyDateFailure, params);
		}
		return valid;
	}

	/**
	 * Get msgKeyDateFailure.
	 * 
	 * @return String Returns the msgKeyDateFailure.
	 */
	public String getMsgKeyDateFailure()
	{
		return msgKeyDateFailure;
	}

	/**
	 * Set msgKeyDateFailure.
	 * 
	 * @param deMsgKeyDateFailure
	 *            msgKeyDateFailure to set.
	 */
	public void setMsgKeyDateFailure(final String deMsgKeyDateFailure)
	{
		msgKeyDateFailure = deMsgKeyDateFailure;
	}

	/**
	 * Get msgKeyNumberFailure.
	 * 
	 * @return String Returns the msgKeyNumberFailure.
	 */
	public String getMsgKeyNumberFailure()
	{
		return msgKeyNumberFailure;
	}

	/**
	 * Set msgKeyNumberFailure.
	 * 
	 * @param msgKeyNumberFailure
	 *            msgKeyNumberFailure to set.
	 */
	public void setMsgKeyNumberFailure(String msgKeyNumberFailure)
	{
		this.msgKeyNumberFailure = msgKeyNumberFailure;
	}
}
