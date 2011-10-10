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
				String jndi = pElement.getAttributeValue("jndi");

				if (jndi != null && jndi.length() > 0)
				{
					try
					{
						Context env = (Context) new InitialContext().lookup("java:comp/env");
						if (env != null)
						{
							String jndiValue = (String) env.lookup(jndi);
							if (jndiValue != null && jndiValue.length() > 0)
							{
								// overwrite met jndi indien beschikbaar
								value = jndiValue;
							}
						}
					}
					catch (NamingException e)
					{
						// ignore - waarde niet beschikbaar
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
