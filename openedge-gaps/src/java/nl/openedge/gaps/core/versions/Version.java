/*
 * $Id$ $Revision$
 * $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions;

import java.io.Serializable;
import java.util.Date;

/**
 * Een versie kan worden gekoppeld aan modellen, parameters etc. Het actief zijn van een
 * versie hangt af van:
 * <ul>
 * <li>de ingangsdatum</li>
 * <li>de status</li>
 * Een versie is actief als het de meest recente in het verleden liggende datum heeft van
 * de beschikbare versies, EN deze versie een status heeft die aangeeft dat de versie
 * actief is gemaakt.
 * </ul>
 */
public final class Version implements Cloneable, Serializable
{
    /** serial UUID. */
	private static final long serialVersionUID = -2356934378890577111L;

    /** interne id. */
	private Long id;

	/** datum (evt incl tijd) vanaf wanneer de versie geldig is. */
	private Date geldigVanaf;

	/** Logische naam van de versie. */
	private String name;

	/** Of de versie is goedgekeurd/ actief mag zijn. */
	private boolean goedgekeurd;

	/**
	 * Construct.
	 */
	public Version()
	{
	}

	/**
	 * Construct.
	 * @param geldigVanaf de ingangsdatum van de versie
	 * @param naam de logische naam
	 */
	public Version(Date geldigVanaf, String naam)
	{
		this.geldigVanaf = geldigVanaf;
		this.name = naam;
	}

	/**
	 * Geeft de ingangsdatum van de versie.
	 * @return de ingangsdatum van de versie.
	 */
	public Date getGeldigVanaf()
	{
		return geldigVanaf;
	}

	/**
	 * Zet de ingangsdatum van de versie.
	 * @param geldigVanaf de ingangsdatum van de versie.
	 */
	public void setGeldigVanaf(Date geldigVanaf)
	{
		this.geldigVanaf = geldigVanaf;
	}

	/**
	 * Geeft de logische naam.
	 * @return de logische naam.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Zet de logische naam.
	 * @param naam de logische naam.
	 */
	public void setName(String naam)
	{
		this.name = naam;
	}

	/**
	 * Get goedgekeurd.
	 * @return goedgekeurd.
	 */
	public boolean isGoedgekeurd()
	{
		return goedgekeurd;
	}

	/**
	 * Set goedgekeurd.
	 * @param goedgekeurd goedgekeurd.
	 */
	public void setGoedgekeurd(boolean goedgekeurd)
	{
		this.goedgekeurd = goedgekeurd;
	}

	/**
	 * Get id.
	 * @return id.
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Set id.
	 * @param id id.
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String clsName = getClass().getName();
		String simpleClsName = clsName.substring(clsName.lastIndexOf('.') + 1);
		return simpleClsName + "{" + getName() + "}";
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