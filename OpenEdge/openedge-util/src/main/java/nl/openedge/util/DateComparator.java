package nl.openedge.util;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares java Dates on a date level, without time. The comparator checks year, month,
 * day and returns -1 or 0 or 1. This component is not Thread safe.
 * 
 * @author shofstee
 */
public class DateComparator implements Comparator<Date>
{

	// as we use instance variables, this component is not Thread safe.
	/** Calendar working object 1. */
	private Calendar cal1 = Calendar.getInstance();

	/** Calendar working object 2. */
	private Calendar cal2 = Calendar.getInstance();

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Date o1, Date o2)
	{
		int result = 0;

		if (o1 == null || o2 == null)
		{
			throw new IllegalArgumentException("Cannot compare with null.");
		}

		cal1.setTime(o1);
		cal2.setTime(o2);

		int yearResult = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
		int monthResult = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
		int dayResult = cal2.get(Calendar.DATE) - cal1.get(Calendar.DATE);

		result = compareFields(yearResult, monthResult, dayResult);

		return result;
	}

	/**
	 * Compare the fields.
	 * 
	 * @param yearResult
	 *            the year result
	 * @param monthResult
	 *            the month result
	 * @param dayResult
	 *            the day result
	 * @return int result
	 */
	private int compareFields(int yearResult, int monthResult, int dayResult)
	{
		int result = 0;
		if (yearResult != 0)
		{
			if (yearResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		else if (monthResult != 0)
		{
			if (monthResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		else if (dayResult != 0)
		{
			if (dayResult > 0)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		return result;
	}
}
