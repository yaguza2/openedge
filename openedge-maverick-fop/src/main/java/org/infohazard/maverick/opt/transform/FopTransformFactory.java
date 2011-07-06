package org.infohazard.maverick.opt.transform;

import javax.servlet.ServletConfig;

import org.infohazard.maverick.flow.ConfigException;
import org.infohazard.maverick.flow.Transform;
import org.infohazard.maverick.flow.TransformFactory;
import org.infohazard.maverick.util.XML;
import org.jdom.Element;

public class FopTransformFactory implements TransformFactory {
	
	ServletConfig servletCfg;

	public void init(Element factoryNode, ServletConfig servletCfg) throws ConfigException
	{
		this.servletCfg = servletCfg;	
	}

	public Transform createTransform(Element transformNode) throws ConfigException
	{
		try
		{
			return new FopTransform(transformNode, servletCfg);
		}
		catch(ConfigException ex)
		{
			throw new ConfigException("Unsupported output: " + XML.toString(transformNode));
		}
	}
}