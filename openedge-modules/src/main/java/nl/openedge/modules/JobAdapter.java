/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.quartz.JobDataMap;

/**
 * @author Eelco Hillenius
 */
class JobAdapter extends ModuleAdapter {
	
	private JobDataMap jobData = null;
	private String group;

	/**
	 * @throws ModuleException allways, as you are not allowed to get a direct instance
	 */
	public Object getModule() throws ModuleException {
		
		throw new ModuleException(
			"you are not allowed to access a job module directely");
	}
	
	/**
	 * init job using the configuration node
	 * @param configNode	configuration element for this job
	 */
	public void initJobData(Element configNode) throws ConfigException {
		
		Element detailNode = configNode.getChild("jobDetail");
		if(detailNode == null) {
			throw new ConfigException("jobs must have a job detail configured " +
									  "(element <jobDetail>)");
		}
		this.group = detailNode.getAttributeValue("group");
		
		List parameters = detailNode.getChildren("parameter");
		jobData = new JobDataMap();
		if(parameters != null) for(Iterator i = parameters.iterator(); i.hasNext(); ) {
			
			Element node = (Element)i.next();
			jobData.put(node.getAttributeValue("name"), 
						node.getAttributeValue("value"));
		}
		
	}

	/**
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @return
	 */
	public JobDataMap getJobData() {
		return jobData;
	}

}
