/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.voicetribe.util.convert.ConvertUtils;

/**
 * Een ParameterValue houdt de werkelijke waarde van een parameter vast, en kan tevens een
 * vrije set attributen bijhouden (bijvoorbeeld voor logging doeleinden).
 */
public class ParameterValue implements Cloneable, Serializable
{
    /** serial UUID. */
	private static final long serialVersionUID = 8303854550708760832L;

    /**
	 * De waarde.
	 */
	protected Object value;

	/**
	 * Attributen.
	 */
	protected Map attributes = null;

	/**
	 * Construct.
	 * @param attribValue de waarde
	 */
	public ParameterValue(Object attribValue)
	{
		this.value = attribValue;
	}

	/**
	 * Geeft de werkelijke (onderliggende) waarde.
	 * @return de werkelijke (onderliggende) waarde
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Zet een attribuut met de gegeven key en value.
	 * @param key key attribuut
	 * @param attribValue waarde attribuut
	 * @return de vorige waarde bij de gegeven key of null indien er geen was
	 */
	public Object put(Object key, Object attribValue)
	{
		if(attributes == null)
		{
			attributes = new HashMap();
		}
		return attributes.put(key, attribValue);
	}

	/**
	 * Voeg de gegeven attributen toe aan de huidige attributen.
	 * @param newAttributes toe te voegen attributen
	 */
	public void putAll(Map newAttributes)
	{
		if(attributes != null)
		{
			newAttributes.putAll(newAttributes);
		}
		else
		{
			attributes = newAttributes;
		}
	}

	/**
	 * Geef attribuut waarde.
	 * @param key key attribuut
	 * @return waarde attribuut
	 */
	public Object get(Object key)
	{
		if(attributes != null)
		{
			return attributes.get(key);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Geef attributen.
	 * @return attributen
	 */
	public Map getAttributes()
	{
		return attributes;
	}

	/**
	 * Vervang de huidige attributen met de gegeven attributen.
	 * @param attributes de nieuwe attributen
	 */
	public void setAttributes(Map attributes)
	{
		this.attributes = attributes;
	}

	/**
	 * Geeft de geformatteerde waarde.
	 * @param locale locale
	 * @return de geformatteerde waarde
	 */
	public String getFormattedValue(Locale locale)
	{
	    Object value = getValue();
	    if(value != null)
	    {
	        return ConvertUtils.getObjectFormatted(value, locale);
	    }
	    else
	    {
	        return null;
	    }
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}
}