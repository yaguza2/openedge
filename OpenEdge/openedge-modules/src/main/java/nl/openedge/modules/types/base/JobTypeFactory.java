/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.modules.types.base;

import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.types.AbstractComponentFactory;
import nl.openedge.modules.types.initcommands.InitCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom.Element;
import org.quartz.JobDataMap;

/**
 * Wrapper for jobs.
 * 
 * @author Eelco Hillenius
 */
public final class JobTypeFactory extends AbstractComponentFactory
{
	/** logger. */
	private static Logger log = LoggerFactory.getLogger(JobTypeFactory.class);

	/** job data object. */
	private JobDataMap jobData = null;

	/** job group. */
	private String group;

	/**
	 * set config node.
	 * 
	 * @param componentNode config node
	 * @throws ConfigException when an configuration error occurs
	 */
	public void setComponentNode(Element componentNode) throws ConfigException
	{

		initJobData(componentNode);
	}

	/**
	 * Get component; allways null.
	 * @return allways null; get Jobs from the scheduler
	 */
	public Object getComponent()
	{
		return null;
	}

	/**
	 * init job using the configuration node.
	 * 
	 * @param configNode configuration element for this job
	 * @throws ConfigException when an configuration error occurs
	 */
	public void initJobData(Element configNode) throws ConfigException
	{

		Element detailNode = configNode.getChild("jobDetail");
		if (detailNode == null)
		{
			throw new ConfigException("jobs must have a job detail configured "
					+ "(element <jobDetail>)");
		}
		this.group = detailNode.getAttributeValue("group");

		List parameters = detailNode.getChildren("parameter");
		jobData = new JobDataMap();

		if (parameters != null)
			for (Iterator i = parameters.iterator(); i.hasNext();)
			{
				Element node = (Element) i.next();
				jobData.put(node.getAttributeValue("name"), node
						.getAttributeValue("value"));
			}

	}

	/**
	 * get job group.
	 * 
	 * @return job group
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * get JobData.
	 * 
	 * @return JobDataMap
	 */
	public JobDataMap getJobData()
	{
		return jobData;
	}

	/**
	 * Set init commands.
	 * @param commands init commands
	 */
	public void setInitCommands(InitCommand[] commands)
	{
		log.error("\ninitcommands will be ignored for Quartz jobs, as"
				+ " these jobs are managed by Quartz instead of a ComponentRepository\n");
	}

}
