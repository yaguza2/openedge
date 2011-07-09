/*
 * $Id: LanguageShuntFactory.java,v 1.8 2004/06/07 20:39:02 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/shunt/LanguageShuntFactory.java,v $
 */

package org.infohazard.maverick.shunt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.NoSuitableModeException;
import org.infohazard.maverick.flow.Shunt;
import org.infohazard.maverick.flow.ShuntFactory;
import org.infohazard.maverick.flow.View;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * LanguageShuntFactory produces Shunts which determine mode based on the Accept-Language
 * header submitted by the user agent.
 * 
 * <p>
 * Modes can be specified as "en", "fr", "zh-hk", and the like. Multiple modes can be
 * assigned to the same view by comma-delimiting them ("en,fr"). In addition, a view can
 * leave its mode unspecified to be a "default" view which will apply when no other mode
 * is appropriate.
 * </p>
 * 
 * <p>
 * Choosing mode from the Accept-Language header follows the way browsers actually work
 * rather than the HTTP spec. Go figure. There is no support for quality levels and
 * preference is determined by simple order in the string. Furthermore, there is some
 * magic regarding prefixed languages: After trying each of the languages specified, any
 * prefixed languages are chopped one level and tried again. This process is repeated
 * until nothing is left to try but the null mode.
 * </p>
 * 
 * <p>
 * For an example Accept-Language header "fr,zh-tw,zh-hk,no-nynorsk", the Shunt will check
 * for modes in this order:
 * </p>
 * 
 * <ol>
 * <li>fr</li>
 * <li>zh-tw</li>
 * <li>zh-hk</li>
 * <li>no-nynorsk</li>
 * <li>zh</li>
 * <li>no</li>
 * <li>the "null" mode</li>
 * </ol>
 * 
 * <p>
 * Hopefully this produces useful behavior.
 * </p>
 */
public class LanguageShuntFactory implements ShuntFactory
{
	/**
	 */
	class LanguageShunt implements Shunt
	{
		/**
		 */
		protected Map modes = new HashMap();

		/**
		 * All modes are converted to lower case and trimmed. Note that multiple modes can
		 * be specified as a comma-delimited sequence, all aliased to the same view.
		 */
		public void defineMode(String mode, View v) throws ConfigException
		{
			if (mode == null)
				this.reallyDefineMode(null, v);
			else
			{
				mode = mode.toLowerCase();

				StringTokenizer tokens = new StringTokenizer(mode, ",");
				while (tokens.hasMoreTokens())
				{
					mode = tokens.nextToken().trim();

					this.reallyDefineMode(mode, v);
				}
			}
		}

		/**
		 * Does not attempt to interpret mode, but checks for duplicates.
		 */
		protected void reallyDefineMode(String mode, View v) throws ConfigException
		{
			if (this.modes.containsKey(mode))
				throw new ConfigException("A duplicate mode (" + mode + ") was"
					+ " specified in a view.  Note that language modes" + " are case insensitive.");

			this.modes.put(mode, v);
		}

		/**
		 */
		public View getView(HttpServletRequest request) throws NoSuitableModeException
		{
			// Must be made lowercase to be case insensitive.
			String wholeHeader = request.getHeader("Accept-Language");
			if (wholeHeader == null) // Can happen with unusual browsers (or telnet, for
										// that matter)
				wholeHeader = "";
			else
				wholeHeader = wholeHeader.toLowerCase();

			// Languages are separated by commas.
			Enumeration tokens = new StringTokenizer(wholeHeader, ",");

			// We may need multiple passes to process the base prefixes of languages
			// with dashes in them.
			List nextPass = null;

			do
			{
				while (tokens.hasMoreElements())
				{
					String lang = (String) tokens.nextElement();
					lang = lang.trim();

					View theView = (View) modes.get(lang);
					if (theView != null)
					{
						log.debug("Using mode:  " + lang);

						return theView;
					}

					int dash = lang.lastIndexOf("-");
					if (dash >= 0)
					{
						// Make sure we create the list
						if (nextPass == null)
							nextPass = new ArrayList();

						// Keep it for later!
						nextPass.add(lang.substring(0, dash));
					}
				}

				// If there is more to process, use it as the token stream and start over.
				if (nextPass != null)
				{
					tokens = Collections.enumeration(nextPass);
					nextPass = null;
				}
			}
			while (tokens.hasMoreElements());

			// Try the default view if nothing else was found
			View defaultView = (View) modes.get(null);
			if (defaultView != null)
				return defaultView;

			// Wow, after all that, we come up empty handed.
			throw new NoSuitableModeException(
				"No appropriate mode could be found for Accepts-Language \"" + wholeHeader
					+ "\".  Possibilities were:  " + modes.keySet());
		}
	}

	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(LanguageShuntFactory.class);

	/**
	 * Does nothing.
	 */
	public void init(Element factoryNode, ServletConfig servletCfg)
	{
	}

	/**
	 * Merely creates a shunt object.
	 */
	public Shunt createShunt() throws ConfigException
	{
		log.debug("Creating language shunt");

		return new LanguageShunt();
	}
}
