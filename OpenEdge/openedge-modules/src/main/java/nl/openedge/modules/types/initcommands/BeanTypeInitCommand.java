package nl.openedge.modules.types.initcommands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.config.ConfigException;
import ognl.Ognl;
import ognl.OgnlException;

import org.jdom.Element;

/**
 * Command that populates instances using Ognl.
 * 
 * @author Eelco Hillenius
 */
public final class BeanTypeInitCommand implements InitCommand
{
	/** bean properties for population. */
	private Map<String, String> properties = null;

	/**
	 * Prefix voor waarden die in JNDI env. opgezocht moeten worden
	 */
	private static final String JNDI_PREFIX = "JNDI:";

	@Override
	public void init(String componentName, Element componentNode,
			ComponentRepository componentRepository)
	{
		this.properties = new HashMap<String, String>();
		List< ? > pList = componentNode.getChildren("property");
		if (pList != null)
		{
			for (Iterator< ? > j = pList.iterator(); j.hasNext();)
			{
				Element pElement = (Element) j.next();
				String value = pElement.getAttributeValue("value");

				if (value != null && value.length() > (JNDI_PREFIX.length() + 1)
					&& JNDI_PREFIX.equals(value.substring(0, JNDI_PREFIX.length()).toUpperCase()))
				{
					try
					{
						Context env = (Context) new InitialContext().lookup("java:comp/env");
						if (env != null)
						{
							value = (String) env.lookup(value.substring(JNDI_PREFIX.length()));
						}
					}
					catch (NamingException e)
					{
					}
				}
				properties.put(pElement.getAttributeValue("name"), value);
			}
		}
	}

	/**
	 * populate the component instance.
	 */
	@Override
	public void execute(Object componentInstance) throws ConfigException
	{
		if (properties != null)
		{
			try
			{
				populate(componentInstance, this.properties);
			}
			catch (OgnlException e)
			{
				throw new ConfigException(e);
			}
		}
	}

	/**
	 * default populate of form: BeanUtils way; set error if anything goes wrong.
	 * 
	 * @param instance
	 *            the component instance
	 * @param propertiesToPopulate
	 *            properties for population
	 * @throws OgnlException
	 *             on population errors
	 */
	private void populate(Object instance, Map<String, String> propertiesToPopulate)
			throws OgnlException
	{
		for (Iterator<String> i = propertiesToPopulate.keySet().iterator(); i.hasNext();)
		{
			String key = i.next();
			Object value = propertiesToPopulate.get(key);
			Ognl.setValue(key, instance, value);
		}
	}
}
