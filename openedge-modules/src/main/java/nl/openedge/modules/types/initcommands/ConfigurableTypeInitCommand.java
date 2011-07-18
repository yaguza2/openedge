package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * Command for configurable types.
 * 
 * @author Eelco Hillenius
 */
public final class ConfigurableTypeInitCommand implements InitCommand
{
	private Element componentNode = null;

	@Override
	public void init(String componentName, Element cNode, ComponentRepository componentRepository)
	{
		this.componentNode = cNode;
	}

	@Override
	public void execute(Object componentInstance) throws ConfigException
	{
		if (componentInstance instanceof ConfigurableType)
		{
			((ConfigurableType) componentInstance).init(this.componentNode);
		}
		else
		{
			Class< ? > clazz = componentInstance.getClass();
			try
			{
				Method initMethod = clazz.getMethod("init", new Class[] {Element.class});
				initMethod.invoke(componentInstance, new Object[] {this.componentNode});
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}
	}
}
