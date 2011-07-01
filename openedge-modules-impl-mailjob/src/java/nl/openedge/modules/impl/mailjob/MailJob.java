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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
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

import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.RepositoryFactory;

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
	
	/**
	 * The least amount of time that should be between two retry attempts.
	 * The maximum amount of time between two retry attempts is RETRY_TIME + job execution interval + delay from waiting for previous job execution to finish
	 * If 0 or null (not specified), no retry is attempted.
	 */
	public static final String PARAMETER_KEY_RETRY_TIME = "retryTime";
	/**
	 * The last time messages were resend, or was checked if there were any messages that needed to be send again.
	 */
	public static final String PARAMETER_KEY_LAST_RETRY_TIME = "lastRetryTime";
	/**
	 * The maximum age of a message before it is classified as a lost cause and will no longer be send.
	 * The age of a message is determined by the time of creation of the message.
	 */
	public static final String PARAMETER_KEY_MAX_RETRY_AGE = "maxRetryAge";
	
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
				log.debug("Reading mailproperties:");
				for(Iterator i = parameters.keySet().iterator(); i.hasNext(); ) 
				{
					String key = (String)i.next();
					if(key.toLowerCase().startsWith("mailprop.")) 
					{
						String pname = key.substring(9);
						String pvalue = (String)parameters.get(key);
						mailProperties.setProperty(pname, pvalue);
						log.debug(pname + "[" + pvalue + "]");
					}
				}
				log.debug("Done.");
			}			
			if(mailMQModuleAlias == null)
			{
				mailMQModuleAlias = (String)parameters.get(
					PARAMETER_KEY_MAILMQMODULE_ALIAS);
				if(mailMQModuleAlias == null) 
				{ 
					// still null?
					String error = PARAMETER_KEY_MAILMQMODULE_ALIAS + 
						" must be provided as a parameter";
					log.error(error);
					throw new Exception(error);
				}
			}
		} 
		catch(Exception e) 
		{
			log.fatal("Exception ", e);
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
				log.debug("Using JDNDI mail session.");
				session = (Session)ctx.lookup(mailSessionRef);
			}
			else
			{
				log.debug("Using non-JNDI mail session.");
				session = Session.getDefaultInstance(mailProperties, null);
			}
		
		} 
		catch(Exception ex) 
		{
			log.fatal("Exception: ", ex);
			log.fatal("will unschedule all triggers");
			
			JobExecutionException e = new JobExecutionException(ex, false);
			e.setErrorCode(JobExecutionException.ERR_BAD_CONFIGURATION);
			e.setUnscheduleAllTriggers(true);
			throw e;			
		}

		// send current queue
		try 
		{
			sendMessages(session, mqMod,mqMod.popQueue());
		} 
		catch(Exception e) 
		{
			log.fatal("Exception: ", e);
		}
		Object temp=parameters.get(PARAMETER_KEY_RETRY_TIME);
		if(temp instanceof String)
		{
			try
			{
				parameters.put(PARAMETER_KEY_RETRY_TIME,new Long(temp.toString()));
				log.info("retry time set to "+temp.toString()+" ms.");
			}
			catch (NumberFormatException e)
			{
				log.error("retry time is not a valid long",e);
				parameters.put(PARAMETER_KEY_RETRY_TIME,new Long(0));
			}
		}
		Long retryTime=(Long)parameters.get(PARAMETER_KEY_RETRY_TIME);
		if(retryTime==null)
			retryTime=new Long(0);
		temp=parameters.get(PARAMETER_KEY_MAX_RETRY_AGE);
		if(temp instanceof String)
		{
			try
			{
				parameters.put(PARAMETER_KEY_MAX_RETRY_AGE,new Long(temp.toString()));
				log.info("max retry age set to "+temp.toString()+" ms.");
			}
			catch (NumberFormatException e)
			{
				log.error("max retry age is not a valid long",e);
				parameters.put(PARAMETER_KEY_MAX_RETRY_AGE,new Long(0));
			}
		}
		Long retryAge=(Long)parameters.get(PARAMETER_KEY_MAX_RETRY_AGE);
		if(retryAge==null)
			retryAge=new Long(0);
		Long lastRetry=(Long)parameters.get(PARAMETER_KEY_LAST_RETRY_TIME);
		if(retryTime.longValue()>0 &&(lastRetry==null || System.currentTimeMillis()-lastRetry.longValue() >=retryTime.longValue()));
		{
			parameters.put(PARAMETER_KEY_LAST_RETRY_TIME,new Long(System.currentTimeMillis()));
			try 
			{
				long maxAge=retryAge.longValue();
				long now=System.currentTimeMillis();
				List all=mqMod.getFailedMessages();
				List filtered=all;
				if(maxAge>0)
				{
					filtered=new ArrayList();
					MailMessage tmp=null;
					for(int i=0;i<all.size();i++)
					{
						tmp=(MailMessage)all.get(i);
						if(now-tmp.getCreated().longValue()<maxAge)
							filtered.add(tmp);
						else
							mqMod.flagFailedMessage(tmp,null);
					}
				}
				sendMessages(session, mqMod,filtered);
			} 
			catch(Exception e) 
			{
				log.fatal("Exception: ", e);
			}
		}

	}
	
	/**
	 * send messages from queue
	 */
	private void sendMessages(Session session, MailMQModule mqMod,List messages) 
		throws Exception 
	{
		List succeeded = new ArrayList();
		try 
		{
			log.info("Loaded " + messages.size() + " MailMessages from the database.");
			if(messages != null)  
			{	
				for(Iterator i = messages.iterator(); i.hasNext(); )
				{
					MailMessage msg = (MailMessage)i.next();
					MimeMessage mailMsg = null;
					try 
					{
						log.debug("Mailing message with id[" + msg.getId() + "]");
						mailMsg = constructMessage(msg, session);
						Transport.send(mailMsg);
						
						succeeded.add(msg);
						
					} 
					catch(Exception e) 
					{
						log.fatal("Exception sending message with id[" + msg.getId() + "]");
						log.fatal("Exception: ", e);
						try 
						{
							mqMod.flagFailedMessage(msg, e);
						} 
						catch(Exception e2) 
						{ // ooops this is pretty bad!
							log.fatal("Exception", e2);
							// nothing else to do than to ignore it
						}
					}
				}
			}
			log.info("Succeeded sending " + succeeded.size()+ "messages.");
			mqMod.removeFromQueue(succeeded);

		} 
		catch(Exception e) 
		{
			log.fatal("Exception: ", e);
			throw e; 
		}
	}
	
	/*
	 * construct mail message from data object
	 */
	private MimeMessage constructMessage(MailMessage msg, Session session) 
		throws MessagingException, AddressException, FileNotFoundException 
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
		
		// attach the attachments to the mailmessage
		if(msg.getAttachments() != null)
		{
			HashMap files = getAttachments(msg.getAttachments());
			Set keySet = files.keySet();
			Iterator keySetIterator = keySet.iterator();
			while(keySetIterator.hasNext())
			{
				String filename = (String)keySetIterator.next();
				MimeBodyPart attachment = new MimeBodyPart();
				attachment.setDataHandler(new DataHandler((FileDataSource)files.get(filename)));
				attachment.setFileName(filename);
				mp.addBodyPart(attachment);
			}
		}
		
		// add the Multipart to the message
		mailMsg.setContent(mp);
		
		return mailMsg;
			
	}
	
	/**
	 * Returns a hashmap with FileDataSources to attach to the mailmessage. The key of the FileDataSources is the filename.<br> 
 	 * The format of the string is as follow:<br>
 	 * <br>
 	 * file&lt;filename&gt;:file&lt;filename&gt;: . . .
 	 * <br>
 	 * (Note: the ':' is the system's path seperator. Windows: ';', Unix: ':')<br>
	 * <br>
	 * 'file' is the string that describes the File. 'filename' is the string that describes the name of the file.<br>
	 * 'filename' may be empty or missing, the name of the file will be the name that can be deduced from the file.
	 * @param attachments
	 * @return
	 * @throws FileNotFoundException
	 */
	public HashMap getAttachments(String attachments)
		throws FileNotFoundException
	{
		HashMap files = new HashMap();
		
		StringTokenizer tk = new StringTokenizer(attachments, System.getProperty("path.separator"));
		while(tk.hasMoreTokens())
		{
			String file = null;
			String filename = null;
	
			String fileDefinition = (String)tk.nextToken();
			int firstToken = fileDefinition.indexOf('<');
			int lastToken = fileDefinition.indexOf('>');
			if(firstToken <= 0 && lastToken <= 0)
			{
				file = fileDefinition;
			}
			else if(firstToken > 0)
			{
				file = fileDefinition.substring(0, firstToken);
			}
	
			if(firstToken > 0 && lastToken > 0)
			{
				filename = fileDefinition.substring(firstToken + 1, lastToken);
			}
					
			if(file != null)
			{
				FileDataSource fds = new FileDataSource(file);
				if(fds.getFile().exists())
				{
					if(filename != null)
					{
						files.put(filename, fds);
					}
					else
					{
						files.put(fds.getName(), fds);
					}
				}
				else
				{
					log.error("The file " + file + " on disk not found.");
					throw new FileNotFoundException("File: " + file + ", is not on disk.");
				}
			}
			else
			{
				log.error("Name of the file could not be found");
				throw new FileNotFoundException("Name of the file could not be found.");
			}
		}

		return files;
	}

}
