package nl.openedge.util.velocity.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import nl.openedge.util.DateFormatHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Velocity tool for handling dates.
 * 
 * @author Eelco Hillenius
 */
public final class DateTool
{
	/** log. */
	private static Logger log = LoggerFactory.getLogger(DateTool.class);

	// ------------------------- system date access ------------------

	/**
	 * @return the system's current time as a {@link Date}
	 */
	public static Date getSystemDate()
	{
		return getSystemCalendar().getTime();
	}

	/**
	 * @return the system's current time as a {@link Calendar}
	 */
	public static Calendar getSystemCalendar()
	{
		return Calendar.getInstance();
	}

	// ------------------------- locale/timezone/date access ----------------

	/**
	 * @return the default {@link Locale}
	 */
	public Locale getLocale()
	{
		return Locale.getDefault();
	}

	/**
	 * @return the default {@link TimeZone}
	 */
	public TimeZone getTimeZone()
	{
		return TimeZone.getDefault();
	}

	/**
	 * Returns a {@link Date}derived from the result of {@link #getCalendar}.
	 * 
	 * @return a {@link Date}derived from the result of {@link #getCalendar}
	 */
	public Date getDate()
	{
		return getCalendar().getTime();
	}

	/**
	 * Returns a {@link Calendar}instance created using the timezone and locale returned
	 * by getTimeZone() and getLocale(). This allows subclasses to easily override the
	 * default locale and timezone used by this tool. Sub-classes may override this method
	 * to return a Calendar instance not based on the system date. Doing so will also
	 * cause the getDate(), getNormalDate(), getFormattedDate(), and toString() methods to
	 * return dates equivalent to the Calendar returned by this method, because those
	 * methods return values derived from the result of this method.
	 * 
	 * @return a {@link Calendar}instance created using the results of
	 *         {@link #getTimeZone()}and {@link #getLocale()}.
	 * @see Calendar#getInstance(TimeZone zone, Locale aLocale)
	 */
	public Calendar getCalendar()
	{
		return Calendar.getInstance(getTimeZone(), getLocale());
	}

	// ------------------------- formatting methods ---------------------------

	/**
	 * Returns a formatted string representing the date as returned by {@link #getDate()}.
	 * This method uses the same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * @param format
	 *            the formatting instructions
	 * @return a formatted string representing the date returned by {@link #getDate()}or
	 *         <code>null</code> if the parameters are invalid
	 * @see #format(String format, Object obj, Locale locale)
	 */
	public String getFormattedDate(String format)
	{
		return format(format, getDate());
	}

	/**
	 * Converts the specified object to a date and returns a formatted string representing
	 * that date using the locale returned by {@link #getLocale()}. This method uses the
	 * same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * @param format
	 *            the formatting instructions
	 * @param obj
	 *            the date object to be formatted
	 * @return a formatted string for this locale representing the specified date or
	 *         <code>null</code> if the parameters are invalid
	 * @see #format(String format, Object obj, Locale locale)
	 */
	public String format(String format, Object obj)
	{
		return format(format, obj, getLocale());
	}

	/**
	 * Returns a formatted string representing the specified date and locale.
	 * <p>
	 * This method uses the same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * <pre>
	 * 
	 *    Symbol   Meaning                 Presentation        Example
	 *    ------   -------                 ------------        -------
	 *    G        era designator          (Text)              AD
	 *    y        year                    (Number)            1996
	 *    M        month in year           (Text &amp; Number)     July &amp; 07
	 *    d        day in month            (Number)            10
	 *    h        hour in am/pm (1&tilde;12)    (Number)            12
	 *    H        hour in day (0&tilde;23)      (Number)            0
	 *    m        minute in hour          (Number)            30
	 *    s        second in minute        (Number)            55
	 *    S        millisecond             (Number)            978
	 *    E        day in week             (Text)              Tuesday
	 *    D        day in year             (Number)            189
	 *    F        day of week in month    (Number)            2 (2nd Wed in July)
	 *    w        week in year            (Number)            27
	 *    W        week in month           (Number)            2
	 *    a        am/pm marker            (Text)              PM
	 *    k        hour in day (1&tilde;24)      (Number)            24
	 *    K        hour in am/pm (0&tilde;11)    (Number)            0
	 *    z        time zone               (Text)              Pacific Standard Time
	 *    '        escape for text         (Delimiter)
	 *    ''       single quote            (Literal)           '
	 * 
	 *    Examples: &quot;E, MMMM d&quot; will result in &quot;Tue, July 24&quot;
	 *              &quot;EEE, M-d (H:m)&quot; will result in &quot;Tuesday, 7-24 (14:12)&quot;
	 * 
	 * </pre>
	 * 
	 * @param format
	 *            the formatting instructions
	 * @param obj
	 *            the date to format
	 * @param locale
	 *            the {@link Locale}to format the date for
	 * @return a formatted string representing the specified date or <code>null</code> if
	 *         the parameters are invalid
	 */
	public static String format(String format, Object obj, Locale locale)
	{
		Date date = toDate(obj);
		if (date == null || format == null || locale == null)
		{
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
		return formatter.format(date);
	}

	// ------------------------- date conversion methods ---------------

	/**
	 * Converts an object to an instance of {@link Date}. Uses a DateFormat to parse the
	 * string value of the object if it is not an instance of Date or Calendar.
	 * 
	 * @param obj
	 *            the date to convert
	 * @return the object as a {@link Date}or <code>null</code> if no conversion is
	 *         possible
	 */
	public static Date toDate(Object obj)
	{
		Date date = null;
		if (obj != null)
		{
			if (obj instanceof Date)
			{
				date = (Date) obj;
			}
			if (obj instanceof Calendar)
			{
				date = ((Calendar) obj).getTime();
			}
			if (obj instanceof Long)
			{
				Date d = new Date();
				d.setTime(((Long) obj).longValue());
				date = d;
			}
			// try parsing the obj as String w/a DateFormat
			DateFormat parser = DateFormat.getInstance();
			try
			{
				date = parser.parse(String.valueOf(obj));
			}
			catch (ParseException e)
			{
				log.debug("DateTool.toDate(" + obj + "): " + e.getMessage());
			}

		}
		return date;
	}

	/**
	 * Converts an object to an instance of {@link Date}using the specified format and the
	 * {@link Locale}returned by {@link #getLocale()}if the object is not already an
	 * instance of Date or Calendar.
	 * 
	 * @param format
	 *            - the format the date is in
	 * @param obj
	 *            - the date to convert
	 * @return the object as a {@link Date}or <code>null</code> if no conversion is
	 *         possible
	 * @see #toDate(String format, Object obj, Locale locale)
	 */
	public Date toDate(String format, Object obj)
	{
		return toDate(format, obj, getLocale());
	}

	/**
	 * Converts an object to an instance of {@link Date}using the specified format and
	 * {@link Locale}if the object is not already an instance of Date or Calendar.
	 * 
	 * @param format
	 *            - the format the date is in
	 * @param obj
	 *            - the date to convert
	 * @param locale
	 *            - the {@link Locale}
	 * @return the object as a {@link Date}or <code>null</code> if no conversion is
	 *         possible
	 * @see SimpleDateFormat#parse
	 */
	public static Date toDate(String format, Object obj, Locale locale)
	{
		// first try the easiest conversions
		Date date = toDate(obj);
		if (date != null)
		{
			return date;
		}
		// try parsing w/a customized SimpleDateFormat
		SimpleDateFormat parser = new SimpleDateFormat(format, locale);
		try
		{
			return parser.parse(String.valueOf(obj));
		}
		catch (ParseException e)
		{
			log.debug("DateTool.toDate( " + format + ", " + obj + ", " + locale + "): "
				+ e.getMessage());
			return null;
		}
	}

	/**
	 * Converts an object to an instance of {@link Calendar}using the locale returned by
	 * {@link #getLocale()}if necessary.
	 * 
	 * @param obj
	 *            the date to convert
	 * @return the converted date
	 * @see #toCalendar(Object obj, Locale locale)
	 */
	public Calendar toCalendar(Object obj)
	{
		return toCalendar(obj, getLocale());
	}

	/**
	 * Converts an object to an instance of {@link Calendar}using the locale returned by
	 * {@link #getLocale()}if necessary.
	 * 
	 * @param obj
	 *            the date to convert
	 * @param locale
	 *            the locale used
	 * @return the converted date
	 * @see #toDate(String format, Object obj, Locale locale)
	 * @see Calendar
	 */
	public static Calendar toCalendar(Object obj, Locale locale)
	{
		if (obj == null)
		{
			return null;
		}
		if (obj instanceof Calendar)
		{
			return (Calendar) obj;
		}
		// try to get a date out of it
		Date date = toDate(obj);
		if (date == null)
		{
			return null;
		}

		// convert the date to a calendar
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		// HACK: Force all fields to update. see link for explanation of this.
		// http://java.sun.com/j2se/1.4/docs/api/java/util/Calendar.html
		cal.getTime();
		return cal;
	}

	// ------------------------- default toString() implementation ------------

	/**
	 * @return the result of {@link #getDate()}as a string
	 */
	@Override
	public String toString()
	{
		return getDate().toString();
	}

	/**
	 * Returns a formatted string representing the date as returned by {@link #getDate()}.
	 * This method uses the same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * @param format
	 *            the formatting instructions
	 * @param locale
	 *            the locale
	 * @return a formatted string representing the date returned by {@link #getDate()}or
	 *         <code>null</code> if the parameters are invalid
	 * @see #format(String format, Object obj, Locale locale)
	 */
	public String getFormattedDate(String format, String locale)
	{

		return format(format, getDate(), new Locale(locale));
	}

	/**
	 * get Date object for today plus addition, with hour set to one-o-clock am.
	 * 
	 * @param addition
	 *            days to add
	 * @return Date date with addition
	 */
	public static Date getOtherDay(int addition)
	{
		GregorianCalendar cal = new GregorianCalendar(); // today
		cal.add(Calendar.DATE, addition); // yesterday now
		cal.set(Calendar.HOUR_OF_DAY, 1); // one o clock at night
		return cal.getTime();
	}

	/**
	 * Returns a formatted string representing the date as returned by {@link #getDate()}.
	 * This method uses the same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * @param format
	 *            the formatting instructions
	 * @param locale
	 *            the locale
	 * @param timeStamp
	 *            the time stamp
	 * @return a formatted string representing the date returned by {@link #getDate()}or
	 *         <code>null</code> if the parameters are invalid
	 * @see #format(String format, Object obj, Locale locale)
	 */
	public String getFormattedTimeStamp(String format, String locale, Long timeStamp)
	{

		String s = null;
		Date date = asDate(timeStamp);
		if (date != null)
		{
			s = format(format, date, new Locale(locale));
		}
		return s;
	}

	/**
	 * Returns a formatted string representing the date as returned by {@link #getDate()}.
	 * This method uses the same formatting instructions as {@link SimpleDateFormat}:
	 * 
	 * @param format
	 *            the formatting instructions
	 * @param timeStamp
	 *            the time stamp
	 * @return a formatted string representing the date returned by {@link #getDate()}or
	 *         <code>null</code> if the parameters are invalid
	 * @see #format(String format, Object obj, Locale locale)
	 */
	public String getFormattedTimeStamp(String format, Long timeStamp)
	{

		String s = null;
		Date date = asDate(timeStamp);
		if (date != null)
		{
			s = format(format, date);
		}
		return s;
	}

	/**
	 * return timestamp as a date.
	 * 
	 * @param timeStamp
	 *            timestamp to convert
	 * @return Date timestamp as a date
	 */
	public static Date asDate(Long timeStamp)
	{
		Date date = null;
		if (timeStamp != null)
		{
			date = new Date(timeStamp.longValue());
		}
		return date;
	}

	/**
	 * Format with DateFormatHelper.
	 * 
	 * @param date
	 *            date to format
	 * @return String formatted date
	 */
	public static String format(Date date)
	{
		return DateFormatHelper.format(date);
	}

	/**
	 * Format with DateFormatHelper.
	 * 
	 * @param date
	 *            date
	 * @param style
	 *            format style
	 * @param locale
	 *            locale
	 * @return String formatted date
	 */
	public static String format(Date date, int style, Locale locale)
	{
		DateFormat df = DateFormat.getDateInstance(style, locale);
		return df.format(date);
	}

	/**
	 * Format with DateFormatHelper, using long format style.
	 * 
	 * @param date
	 *            date
	 * @param locale
	 *            locale
	 * @return String formatted date
	 */
	public static String format(Date date, Locale locale)
	{
		return format(date, DateFormat.LONG, locale);
	}

}
