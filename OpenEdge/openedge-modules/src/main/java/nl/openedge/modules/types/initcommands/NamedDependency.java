package nl.openedge.modules.types.initcommands;

/**
 * Holder for named dependency.
 * 
 * @author Eelco Hillenius
 */
public class NamedDependency
{
	private String moduleName;

	private String propertyName;

	public NamedDependency(String moduleName, String propertyName)
	{
		this.moduleName = moduleName;
		this.propertyName = propertyName;
	}

	public String getModuleName()
	{
		return moduleName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	public void setPropertyName(String propertyName)
	{
		this.propertyName = propertyName;
	}
}
