/*
 * $Header$
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
package nl.openedge.modules;

import java.util.Iterator;
import java.util.List;

import nl.openedge.util.config.*;

import org.jdom.Element;
import org.quartz.JobDataMap;

/**
 * Wrapper for jobs
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