/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Eelco Hillenius
 */
public class QuartzJobModuleImpl implements Job {

	/*
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context)
					throws JobExecutionException {
		
		String msg = (String)context.getJobDetail().getJobDataMap().get("msg");
		System.err.println("\n---" + context.getJobDetail().getFullName() 
						+ " msg: " + msg);

	}

}
