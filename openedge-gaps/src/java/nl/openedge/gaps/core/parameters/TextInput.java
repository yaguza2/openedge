/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

/**
 * Speciale implementatie/ extentie van
 * {@link nl.openedge.gaps.core.parameters.ParameterInput}dat een indicatie is dat een
 * parameter arbitraire string invoer ipv bijvoorbeeld objecten uit een lijst accepteert.
 */
public class TextInput extends ParameterInput
{

	/** Weergavenaam. */
	private String displayName;

	/**
	 * Construct met msg key.
	 * @param displayNameMsgKey message key van de weergavenaam
	 * @see ParameterResources
	 */
	public TextInput(String displayNameMsgKey)
	{
		this.displayName = ParameterResources.getText(displayNameMsgKey);
	}

	/**
	 * Construct met aanroepende klasse.
	 * @param callee aanroepende klasse
	 * @see ParameterResources
	 */
	public TextInput(Class callee)
	{
		String clsName = callee.getName();
		clsName = clsName.substring(clsName.lastIndexOf('.') + 1) + NAME_POSTFIX;
		this.displayName = ParameterResources.getText(clsName);
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.ParameterInput#getValue()
	 */
	public Object getValue()
	{
		return "";
	}

	/**
	 * @see nl.openedge.gaps.core.parameters.ParameterInput#getDisplayName()
	 */
	public String getDisplayName()
	{
		return displayName;
	}
}