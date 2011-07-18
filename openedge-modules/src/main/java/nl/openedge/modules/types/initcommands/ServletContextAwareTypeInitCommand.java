package nl.openedge.modules.types.initcommands;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;

import org.jdom.Element;

/**
 * Command for components that want to be aware of the servlet context.
 * 
 * @author Eelco Hillenius
 */
public final class ServletContextAwareTypeInitCommand implements InitCommand
{
	private ComponentRepository componentRepository = null;

	@Override
	public void init(String componentName, Element componentNode, ComponentRepository cRepo)
	{
		this.componentRepository = cRepo;
	}

	@Override
	public void execute(Object componentInstance) throws ConfigException
	{
		ServletContext servletContext = componentRepository.getServletContext();

		if (componentInstance instanceof ServletContextAwareType)
			((ServletContextAwareType) componentInstance).setServletContext(servletContext);
		else
		{
			Class< ? > clazz = componentInstance.getClass();
			try
			{
				Method initMethod =
					clazz.getMethod("setServletContext", new Class[] {ServletContext.class});
				initMethod.invoke(componentInstance, new Object[] {servletContext});
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}
	}
}
