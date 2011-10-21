package nl.openedge.baritus.converters;

import java.util.Locale;

/**
 * Formatter that uses a locale.
 * 
 * @author Eelco Hillenius
 */
public interface LocaleFormatter extends Formatter
{
	/**
	 * Set the locale for this instance.
	 * 
	 * @param locale
	 *            the locale for this instance
	 */
	public void setLocale(Locale locale);

	/**
	 * Get the locale for this instance
	 * 
	 * @return Locale the locale for this instance
	 */
	public Locale getLocale();
}
