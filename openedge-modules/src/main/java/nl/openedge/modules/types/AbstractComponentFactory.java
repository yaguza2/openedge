package nl.openedge.modules.types;

import java.util.ArrayList;
import java.util.List;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.initcommands.InitCommand;
import nl.openedge.modules.types.initcommands.InitCommandException;
import nl.openedge.modules.types.initcommands.RequestLevelInitCommand;

import org.jdom.Element;

/**
 * Common base for component factories.
 * 
 * @author Eelco Hillenius
 */
public abstract class AbstractComponentFactory implements ComponentFactory
{
	private Class< ? > componentClass = null;

	private String name = null;

	private ComponentRepository componentRepository = null;

	private InitCommand[] initCommands = null;

	private RequestLevelInitCommand[] reqInitCommands = null;

	@Override
	public void setComponentClass(Class< ? > componentClass) throws ConfigException
	{
		// test first
		try
		{
			componentClass.newInstance();
		}
		catch (InstantiationException ex)
		{
			throw new ConfigException(ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new ConfigException(ex);
		}
		this.componentClass = componentClass;
	}

	protected void executeInitCommands(Object componentInstance) throws InitCommandException,
			ConfigException
	{
		if (initCommands != null && (initCommands.length > 0))
		{
			for (int i = 0; i < initCommands.length; i++)
			{
				initCommands[i].execute(componentInstance);
			}
		}
	}

	protected void executeRequestLevelInitCommands(Object componentInstance)
			throws InitCommandException, ConfigException
	{
		if (reqInitCommands != null && (reqInitCommands.length > 0))
		{
			for (int i = 0; i < reqInitCommands.length; i++)
			{
				reqInitCommands[i].execute(componentInstance);
			}
		}
	}

	@Override
	public final String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public Class< ? > getComponentClass()
	{
		return componentClass;
	}

	@Override
	public ComponentRepository getComponentRepository()
	{
		return componentRepository;
	}

	@Override
	public void setComponentRepository(ComponentRepository factory)
	{
		componentRepository = factory;
	}

	@Override
	public InitCommand[] getInitCommands()
	{
		return initCommands;
	}

	@Override
	public void setInitCommands(InitCommand[] commands)
	{
		List<InitCommand> tempInit = new ArrayList<InitCommand>();
		List<RequestLevelInitCommand> tempReqInit = new ArrayList<RequestLevelInitCommand>();
		if (commands != null)
		{
			for (int i = 0; i < commands.length; i++)
			{
				if (commands[i] instanceof RequestLevelInitCommand)
					tempReqInit.add((RequestLevelInitCommand) commands[i]);
				else
					tempInit.add(commands[i]);
			}
		}
		InitCommand[] cmds = tempInit.toArray(new InitCommand[tempInit.size()]);
		this.initCommands = cmds;

		RequestLevelInitCommand[] reqcmds =
			tempReqInit.toArray(new RequestLevelInitCommand[tempReqInit.size()]);
		this.reqInitCommands = reqcmds;
	}

	/**
	 * @throws ConfigException
	 */
	@Override
	public void setComponentNode(Element componentNode) throws ConfigException
	{
	}

	@Override
	public abstract Object getComponent();
}
