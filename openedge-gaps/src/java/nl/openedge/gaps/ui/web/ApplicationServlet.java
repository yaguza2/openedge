/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) 2004, Levob Bank en Verzekeringen Alle rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.converters.BooleanConverter;
import nl.openedge.baritus.converters.ByteConverter;
import nl.openedge.baritus.converters.ByteLocaleConverter;
import nl.openedge.baritus.converters.CharacterConverter;
import nl.openedge.baritus.converters.DateLocaleConverter;
import nl.openedge.baritus.converters.DoubleConverter;
import nl.openedge.baritus.converters.DoubleLocaleConverter;
import nl.openedge.baritus.converters.FloatConverter;
import nl.openedge.baritus.converters.FloatLocaleConverter;
import nl.openedge.baritus.converters.IntegerConverter;
import nl.openedge.baritus.converters.LongConverter;
import nl.openedge.baritus.converters.LongLocaleConverter;
import nl.openedge.baritus.converters.ShortConverter;
import nl.openedge.baritus.converters.ShortLocaleConverter;
import nl.openedge.util.baritus.converters.FallbackDateConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Deze servlet wordt gebruikt voor initialisatie; start deze bij voorkeur als eerste op.
 * Zorgt voor initialiseren van de converters en openedge libs, en kijkt of - indien we
 * met een lokale versie werken - de database dient te worden gecreeerd.
 */
public class ApplicationServlet extends HttpServlet
{

	/**
	 * Gebruikt voor logging.
	 */
	private static Log log = LogFactory.getLog(ApplicationServlet.class);

	/**
	 * Initialiseert de servlet.
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(final ServletConfig config) throws ServletException
	{

		// init superclass
		super.init(config);

		try
		{
			log.info("initialise");

			// init beanutils for framework
			initConverters();

			// init OpenEdge components
			//initOEModules(config);

			// init OpenEdge access
			// initOEAccess(config);

			//init Hibernate helper
			//HibernateHelper.init();

			// init test-mode (geen DB en testdata toevoegen) of anders (wel DB)
			initMode(config);

			log.info("initialised");

		}
		catch (Exception e)
		{
			log.error("initialisatie servlet gaat fout", e);
			throw new ServletException(e);
		}

	}

	/**
	 * Init de mode (test of niet).
	 * @param config servlet config
	 */
	protected void initMode(ServletConfig config)
	{
		//        boolean inTestMode =
		// Boolean.valueOf(config.getInitParameter("in-test-mode"))
		//                .booleanValue();
		//        if (inTestMode) {
		//            ParameterBuilder builder = new ParameterBuilder();
		//
		//        }
	}

	/**
	 * Registreer de converters voor dit project.
	 */
	protected void initConverters()
	{
		// get the converter registry
		ConverterRegistry reg = ConverterRegistry.getInstance();

		// now, for all (basic) types, register a default converter and
		// - if available - a localized converter
		// NOTE: the localized versions precede the non-localized

		reg.register(new BooleanConverter(), Boolean.TYPE);
		reg.register(new BooleanConverter(), Boolean.class);

		reg.register(new ByteConverter(), Byte.TYPE);
		reg.register(new ByteConverter(), Byte.class);
		reg.register(new ByteLocaleConverter(), Byte.TYPE);
		reg.register(new ByteLocaleConverter(), Byte.class);

		reg.register(new CharacterConverter(), Character.TYPE);
		reg.register(new CharacterConverter(), Character.class);

		reg.register(new DoubleConverter(), Double.TYPE);
		reg.register(new DoubleConverter(), Double.class);
		reg.register(new DoubleLocaleConverter(), Double.TYPE);
		reg.register(new DoubleLocaleConverter(), Double.class);

		reg.register(new FloatConverter(), Float.TYPE);
		reg.register(new FloatConverter(), Float.class);
		reg.register(new FloatLocaleConverter(), Float.TYPE);
		reg.register(new FloatLocaleConverter(), Float.class);

		reg.register(new IntegerConverter(), Integer.TYPE);
		reg.register(new IntegerConverter(), Integer.class);

		reg.register(new LongConverter(), Long.TYPE);
		reg.register(new LongConverter(), Long.class);
		reg.register(new LongLocaleConverter(), Long.TYPE);
		reg.register(new LongLocaleConverter(), Long.class);

		reg.register(new ShortConverter(), Short.TYPE);
		reg.register(new ShortConverter(), Short.class);
		reg.register(new ShortLocaleConverter(), Short.TYPE);
		reg.register(new ShortLocaleConverter(), Short.class);

		reg.deregisterByConverterClass(DateLocaleConverter.class);

		reg.register(new FallbackDateConverter(), Date.class);
		reg.register(new FallbackDateConverter(), java.sql.Date.class);
		reg.register(new FallbackDateConverter(), Timestamp.class);

	}

	//    /**
	//     * initialise OpenEdge Access with servlet context
	//     * @param config
	//     * @throws Exception
	//     */
	//    protected void initOEAccess(ServletConfig config) throws Exception
	//    {
	//        AccessHelper.reload(config.getServletContext());
	//    }

	//    /**
	//     * Initialiseert OpenEdge Access met de servlet context.
	//     * @param config de servlet configuratie.
	//     * @throws ConfigException als het configureren van de OpenEdge Access
	// module
	//     * fout gaat.
	//     */
	//    protected void initOEModules(final ServletConfig config) throws
	// ConfigException {
	//        new JDOMConfigurator(config.getServletContext());
	//    }
}