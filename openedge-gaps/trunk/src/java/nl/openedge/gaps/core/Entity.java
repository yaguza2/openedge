/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core;

import java.io.Serializable;

import nl.openedge.gaps.core.versions.Version;

/**
 * Basisklasse voor verschillende grootheden binnen het systeem. Een enititeit houdt in
 * ieder geval een versie, een id en een localId bij. Property id wordt gebruikt voor
 * globaal unieke id's, en property localId wordt gebruikt voor id's binnen een scope. De
 * gedachte achter deze scheiding is, dat het localId zo kan worden gebruikt voor namen
 * die 'human readable' zijn, en het id kan worden gebruikt voor snelle toegang via
 * registries. Zie bijvoorbeeld het gebruik van id's/ localId's in groepen, waarbij het id
 * een samenstelling is van het pad en de localId (waarbij het id feitelijk het pad is dat
 * gebruikt kan worden met 'gpath').
 */
public abstract class Entity implements Cloneable, Serializable
{

	/** versie van de entiteit. */
	private Version version; 

	/** de globaal unieke identifier voor deze entiteit. */
	private String id;

	/**
	 * De lokaal unieke identifier (bijv. binnen een groep) voor deze entiteit; zal
	 * meestal tevens worden gebruikt als naam.
	 */
	private String localId;

	/**
	 * Construct.
	 */
	public Entity()
	{
		//
	}

	/**
	 * Geeft de globaal unieke identifier (zoals bijvoorbeeld het pad bij groepen).
	 * @return the de globaal unieke identifier.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Zet de globaal unieke identifier (zoals bijvoorbeeld het pad bij groepen).
	 * @param id de globaal unieke identifier.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * Geeft de versie.
	 * @return de versie.
	 */
	public final Version getVersion()
	{
		return version;
	}

	/**
	 * Zet de versie.
	 * @param version de versie.
	 */
	public final void setVersion(Version version)
	{
		this.version = version;
	}

	/**
	 * Geeft de lokaal unieke identifier voor deze entiteit. .
	 * @return de lokaal unieke identifier voor deze entiteit.
	 */
	public String getLocalId()
	{
		return localId;
	}

	/**
	 * Zet de lokaal unieke identifier voor deze entiteit.
	 * @param localId de lokaal unieke identifier voor deze entiteit.
	 */
	public void setLocalId(String localId)
	{
		this.localId = localId;
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

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String clsName = getClass().getName();
		String simpleClsName = clsName.substring(clsName.lastIndexOf('.') + 1);
		return simpleClsName + "{" + getId() + "}[" + getVersion() + "]";
	}
}