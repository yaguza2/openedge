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
package nl.openedge.modules.impl.mailjob;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.InitialContext;

import nl.openedge.modules.RepositoryFactory;
import nl.openedge.modules.ComponentRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * Quartz job thats uses an instance of MailMQModule to poll a message 
 * queue for email and sends emails from this queue.
 * Messages that are succesfully sent are deleted from the queue. Messages
 * that could not be sent are 'flagged' to be failed.
 * 
 * This module uses the following parameters:
 * <ul>
 * <li>
 * mailSessionRef: the JNDI location where a mailsession can be found
 * if not provided, this job will fallback to the standard mail API
 * <li>
 * mailprop.x
 * all parameters that start with 'mailprop' will be used as properties 
 * (without the prefix) when using the mail API.
 * <li>
 * mailMQModuleAlias: alias of the MailMQModule implementation
 * </ul>
 * <br>
 * Example:
 * 
 *<module name="MailJob"
 *				class="nl.openedge.components.impl.mailjob.MailJob">
 *			<jobDetail group="DEFAULT">
 *				<parameter name="mailSessionRef" 
 *						   value="java:comp/env/mail/burgerweeshuis"/>
 *			OR
 *				<parameter name="mailprop.mail.smtp.host" 
 *						   value="mymail.host.com"/>
 *
 *				<parameter name="mailMQModuleAlias"
 *						   value="MailMQModule"/>
 *			</jobDetail>				
 *		</module>
 * 
 * @author Eelco Hillenius
 */
public final class MailJob implements StatefulJob 
{

	/** job parameter for JNDI reference to mail session: mailSessionRef */ 
	public static final String PARAMETER_KEY_JNDI_MAIL_SESSION = "mailSessionRef";
	/** 
	 * job parameter that states the module alias to use for the 
	 * MailMQModule implementation: mailMQModuleAlias
	 */ 
	public static final String PARAMETER_KEY_MAILMQMODULE_ALIAS = "mailMQModuleAlias";
	/** alias of MailMQModule implementation */
	protected static String mailMQModuleAlias = null;
	/* logger */
	private Log log = LogFactory.getLog(MailJob.class);
	
	/** mailproperties if available */
	protected Properties mailProperties = null;
	
	/*
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException 
	{
		
		Map parameters = context.getJobDetail().getJobDataMap();
		String mailSessionRef = (String)parameters.get(
					PARAMETER_KEY_JNDI_MAIL_SESSION);

		try 
		{
			if(mailSessionRef == null) 
			{
				// fallback to normal mail API
				this.mailProperties = new Properties();
				for(Iterator i = parameters.keySet().iterator(); i.hasNext(); ) 
				{
					String key = (String)i.next();
					if(key.toLowerCase().startsWith("mailprop.")) 
					{
						String pname = key.substring(9);
						String pvalue = (String)parameters.get(key);
						mailProperties.setProperty(pname, pvalue);
					}
				}
			}			
			if(mailMQModuleAlias == null)
			{
				mailMQModuleAlias = (String)parameters.get(
						PARAMETER_KEY_MAILMQMODULE_ALIAS);
				if(mailMQModuleAlias == null) 
				{ // still null?
					throw new Exception(PARAMETER_KEY_MAILMQMODULE_ALIAS +
								" must be provided as a parameter");
				}
			}
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			log.fatal("will unschedule all triggers");
			JobExecutionException je = new JobExecutionException(e, false);
			je.setErrorCode(JobExecutionException.ERR_BAD_CONFIGURATION);
			je.setUnscheduleAllTriggers(true);
			throw je;
		}
		
		Session session = null;
		MailMQModule mqMod = null;
		try 
		{
			Context ctx = new InitialContext();
			ComponentRepository mf = RepositoryFactory.getRepository();
			mqMod = (MailMQModule)mf.getComponent(mailMQModuleAlias);
		
			if(mailSessionRef != null)
			{
				session = (Session)ctx.lookup(mailSessionRef);
			}
			else
			{
				session = Session.getDefaultInstance(mailProperties, null);
			}
		
		} 
		catch(Exception ex) 
		{
			ex.printStackTrace();
			log.fatal("will unschedule all triggers");
			
			JobExecutionException e = new JobExecutionException(ex, false);
			e.setErrorCode(JobExecutionException.ERR_BAD_CONFIGURATION);
			e.setUnscheduleAllTriggers(true);
			throw e;			
		}

		// send current queue
		try 
		{
			sendMessages(session, mqMod);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}

	}
	
	/*
	 * send messages from queue
	 */
	private void sendMessages(Session session, MailMQModule mqMod) 
					throws Exception 
	{

		List succeeded = new ArrayList();
		try 
		{

			List messages = mqMod.popQueue();
			Date now = new Date();
			if(messages != null) for(Iterator i = messages.iterator(); i.hasNext(); ) 
			{	
				MailMessage msg = (MailMessage)i.next();
				MimeMessage mailMsg = null;
				try 
				{
					mailMsg = constructMessage(msg, session);
					Transport.send(mailMsg);
					
					succeeded.add(msg);
					
				} 
				catch(Exception e) 
				{
					e.printStackTrace();
					try 
					{
						mqMod.flagFailedMessage(msg, e);
					} 
					catch(Exception e2) 
					{ // ooops this is pretty bad!
						e2.printStackTrace();
						// nothing else to do than to ignore it
					}
				}
			}
			
			mqMod.removeFromQueue(succeeded);

		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			throw e; 
		}
	}
	
	/*
	 * construct mail message from data object
	 */
	private MimeMessage constructMessage(MailMessage msg, Session session) 
				throws MessagingException, AddressException 
	{	
		MimeMessage mailMsg = new MimeMessage(session);
		mailMsg.setFrom(new InternetAddress(msg.getSender()));
		InternetAddress[] address = {
				new InternetAddress(msg.getSendTo())};
		mailMsg.setRecipients(Message.RecipientType.TO, address);
		mailMsg.setSubject(msg.getSubject());
		mailMsg.setSentDate(new Date());
		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();
					
		mbp1.setContent(msg.getMessage(), msg.getContentType());
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);
		// add the Multipart to the message
		mailMsg.setContent(mp);
		
		return mailMsg;
			
	}

}
