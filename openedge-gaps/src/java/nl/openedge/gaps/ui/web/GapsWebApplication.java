/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import nl.openedge.util.hibernate.ConfigException;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.voicetribe.util.code.Code;
import com.voicetribe.util.code.Log4JCodeListenerFactory;
import com.voicetribe.util.convert.ConverterRegistry;
import com.voicetribe.util.time.Duration;
import com.voicetribe.wicket.ApplicationSettings;
import com.voicetribe.wicket.WebApplication;

/**
 * Webapplicatie klasse GAPS.
 */
public final class GapsWebApplication extends WebApplication
{
	/** Logger. */
	private static Log log = LogFactory.getLog(GapsWebApplication.class);

    /**
     * Construct.
     */
    public GapsWebApplication()
    {
		Code.addListenerFactory(new Log4JCodeListenerFactory());
		ApplicationSettings settings = getSettings();
		settings.setHomePage(HomePage.class);
		settings.setComponentUseCheck(false);
		ConverterRegistry convReg = settings.getConverterRegistry();
		convReg.setLocalizedDefaults();
		if (!Boolean.getBoolean("cache-templates"))
		{
			Duration pollFreq = Duration.ONE_SECOND;
			settings.setResourcePollFrequency(pollFreq);
			log.info("template caching is INACTIVE");
		}
		else
		{
			log.info("template caching is ACTIVE");
		}
		try
        {
            HibernateHelper.init();
        }
        catch (ConfigException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
