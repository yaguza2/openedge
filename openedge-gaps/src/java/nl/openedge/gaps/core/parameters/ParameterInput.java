/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import java.io.Serializable;

/**
 * Metadata klasse voor parameter input.
 */
public abstract class ParameterInput implements Cloneable, Serializable
{

	/** Postfix voor gebruik in resources voor de display name. */
	protected static final String NAME_POSTFIX = ".name";

	/**
	 * Geeft het 'label' van de input.
	 * @return het 'label' van de input
	 */
	public String getDisplayName()
	{
		return ParameterResources.getText(getNameKey());
	}

	/**
	 * Geeft de resource bundle key voor display name.
	 * @return de resource bundle key voor display name
	 */
	protected String getNameKey()
	{
		return getPrefix() + NAME_POSTFIX;
	}

	/**
	 * Geeft prefix voor resource bundle key.
	 * @return prefix voor resource bundle key
	 */
	private String getPrefix()
	{
		String className = getClass().getName();
		return className.substring(className.lastIndexOf('.'));
	}

	/**
	 * Geeft de mogelijke waarde.
	 * @return de mogelijke waarde
	 */
	public abstract Object getValue();

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