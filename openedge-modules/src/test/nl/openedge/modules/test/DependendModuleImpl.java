/*
 * $Id$
 * $Revision$
 * $date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.types.initcommands.DependentType;

/**
 * @author Eelco Hillenius
 */
public class DependendModuleImpl implements DependentType
{

	private BeanModuleImpl beanModule = null;
	
	private ConfigurableModuleImpl configModule = null;

	/**
	 * @return
	 */
	public BeanModuleImpl getBeanModule()
	{
		return beanModule;
	}

	/**
	 * @return
	 */
	public ConfigurableModuleImpl getConfigModule()
	{
		return configModule;
	}

	/**
	 * @param impl
	 */
	public void setBeanModule(BeanModuleImpl impl)
	{
		beanModule = impl;
	}

	/**
	 * @param impl
	 */
	public void setConfigModule(ConfigurableModuleImpl impl)
	{
		configModule = impl;
	}

}
