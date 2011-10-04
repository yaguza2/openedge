package org.infohazard.maverick;

public class ViewDefinition
{
	private ViewType type;

	private String path;

	public ViewDefinition(ViewType type, String path)
	{
		this.type = type;
		this.path = path;
	}

	public ViewType getType()
	{
		return type;
	}

	public void setType(ViewType type)
	{
		this.type = type;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
}
