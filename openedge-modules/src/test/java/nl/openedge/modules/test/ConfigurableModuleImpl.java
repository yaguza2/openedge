/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules.test;

import org.jdom.Element;

import nl.openedge.modules.ConfigException;
import nl.openedge.modules.Configurable;
import nl.openedge.modules.SingletonModule;

/**
 * @author Eelco Hillenius
 */
public class ConfigurableModuleImpl implements SingletonModule, Configurable {

	public ConfigurableModuleImpl() {
		System.out.println(getClass().getName() + ": created");
	}
	
	public void init(Element configNode) throws ConfigException {
		System.out.println(getClass().getName() + ": initialised with " + configNode);	
		Element p1 = configNode.getChild("param1");
		if(p1 == null) throw new ConfigException("where's param1?");
		String attr = p1.getAttributeValue("attr");
		if(attr == null) throw new ConfigException("where's param1['attr']?");
		Element p2 = configNode.getChild("param2");
		if(p2 == null) throw new ConfigException("where's param2?");
		String val = p2.getTextNormalize();
		if(val == null || (!val.equals("Bar"))) 
				throw new ConfigException("value of param2 should be Bar!");
	}

}
