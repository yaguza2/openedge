/*
 * $Id$
 * $Revision$
 * $date$
 */
package nl.openedge.modules.types.initcommands;

/**
 * Holder for named dependency
 * @author Eelco Hillenius
 */
public class NamedDependency
{
	
	/* name of the module */
	private String moduleName;
	
	/* name of the (target) property */
	private String propertyName;
	
	/**
	 * default construct
	 */
	public NamedDependency()
	{
		// nothing here
	}
	
	/**
	 * utility constructor
	 * @param moduleName name of module
	 * @param propertyName name of property
	 */
	public NamedDependency(String moduleName, String propertyName)
	{
		this.moduleName = moduleName;
		this.propertyName = propertyName;
	}
	
	/**
	 * @return String name of module
	 */
	public String getModuleName()
	{
		return moduleName;
	}

	/**
	 * @return name of property
	 */
	public String getPropertyName()
	{
		return propertyName;
	}

	/**
	 * @param moduleName name of module
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * @param propertyName name of property
	 */
	public void setPropertyName(String propertyName)
	{
		this.propertyName = propertyName;
	}

}
