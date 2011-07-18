package nl.openedge.modules.types.base;

import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.AbstractComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.jdom.Element;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for jobs.
 * 
 * @author Eelco Hillenius
 */
public final class JobTypeFactory extends AbstractComponentFactory
{
	private static Logger log = LoggerFactory.getLogger(JobTypeFactory.class);

	private JobDataMap jobData = null;

	private String group;

	@Override
	public void setComponentNode(Element componentNode) throws ConfigException
	{
		initJobData(componentNode);
	}

	@Override
	public Object getComponent()
	{
		return null;
	}

	public void initJobData(Element configNode) throws ConfigException
	{
		Element detailNode = configNode.getChild("jobDetail");
		if (detailNode == null)
		{
			throw new ConfigException("jobs must have a job detail configured "
				+ "(element <jobDetail>)");
		}
		this.group = detailNode.getAttributeValue("group");

		List< ? > parameters = detailNode.getChildren("parameter");
		jobData = new JobDataMap();

		if (parameters != null)
			for (Iterator< ? > i = parameters.iterator(); i.hasNext();)
			{
				Element node = (Element) i.next();
				jobData.put(node.getAttributeValue("name"), node.getAttributeValue("value"));
			}
	}

	public String getGroup()
	{
		return group;
	}

	public JobDataMap getJobData()
	{
		return jobData;
	}

	@Override
	public void setInitCommands(InitCommand[] commands)
	{
		log.error("\ninitcommands will be ignored for Quartz jobs, as"
			+ " these jobs are managed by Quartz instead of a ComponentRepository\n");
	}
}
