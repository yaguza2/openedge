/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterDescriptor;
import nl.openedge.gaps.core.parameters.ParameterInput;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.TextInput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Parameter voor datum typen.
 */
public final class DateParameter extends Parameter
{
    /** serial UUID. */
	private static final long serialVersionUID = -3110023660952168189L;

    /** Descriptor. */
	private static final transient ParameterDescriptor DESCRIPTOR = new Descriptor();

	/** Date format voor parsen datum van strings. */
	private static final transient DateFormat DEFAULT_FORMAT = new SimpleDateFormat(
			"dd-MM-yy");

	/** Te gebruiken log. */
	private static transient Log log = LogFactory.getLog(DateParameter.class);

	/** date format. */
	private transient DateFormat dateFormat = DEFAULT_FORMAT;

	/**
	 * Construct.
	 */
	public DateParameter()
	{
		super();
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#getDescriptor()
	 */
	public ParameterDescriptor getDescriptor()
	{
		return DESCRIPTOR;
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String)
	 */
	public ParameterValue createValue(Map context, String valueAsString)
			throws InputException
	{

		if (dateFormat == null)
		{
			dateFormat = DEFAULT_FORMAT;
		}
		ParameterValue value = null;
		try
		{
			Date date = dateFormat.parse(valueAsString);
			value = new ParameterValue(date);
			value.setAttributes(context);
		}
		catch (ParseException e)
		{
			log.error(e.getMessage(), e);
			throw new InputException(e);
		}
		return value;
	}

	/**
	 * Shortcut naar parameter waarde.
	 * @return instantie van Date of null indien de waarde niet was gezet
	 */
	public Date getDate()
	{
		if (getValue() != null)
		{
			return (Date) getValue().getValue();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.Parameter#createValue(java.util.Map,
	 *      java.lang.String[])
	 */
	public ParameterValue createValue(Map context, String[] valueAsString)
			throws InputException
	{

		return createValueWithSingleString(context, valueAsString);
	}

	/**
	 * Descriptor klasse.
	 */
	private static final class Descriptor extends ParameterDescriptor
	{
	    /** serial UUID. */
		private static final long serialVersionUID = 3581484777664638910L;

        /** toegestane input waarden. */
		private static final ParameterInput[] POSSIBLE_VALUES = new ParameterInput[] {new TextInput(
				Descriptor.class)};

		/**
		 * @see nl.openedge.gaps.core.parameters.ParameterDescriptor#getPossibleValues()
		 */
		public Object getPossibleValues()
		{
			return POSSIBLE_VALUES;
		}
	}

	/**
	 * Get dateFormat.
	 * @return dateFormat.
	 */
	public DateFormat getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Set dateFormat.
	 * @param dateFormat dateFormat.
	 */
	public void setDateFormat(DateFormat dateFormat)
	{
		this.dateFormat = dateFormat;
	}
}