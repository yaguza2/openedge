package nl.openedge.modules.impl.mailjob;

import java.io.FileNotFoundException;
import java.util.*;

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

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz job thats uses an instance of MailMQModule to poll a message queue for email and
 * sends emails from this queue. Messages that are succesfully sent are deleted from the
 * queue. Messages that could not be sent are 'flagged' to be failed.
 * 
 * This module uses the following parameters:
 * <ul>
 * <li>
 * mailSessionRef: the JNDI location where a mailsession can be found if not provided,
 * this job will fallback to the standard mail API
 * <li>
 * mailprop.x all parameters that start with 'mailprop' will be used as properties
 * (without the prefix) when using the mail API.
 * <li>
 * mailMQModuleAlias: alias of the MailMQModule implementation
 * </ul>
 * <br>
 * Example:
 * 
 * <module name="MailJob" class="nl.openedge.components.impl.mailjob.MailJob"> <jobDetail
 * group="DEFAULT"> <parameter name="mailSessionRef"
 * value="java:comp/env/mail/burgerweeshuis"/> OR <parameter
 * name="mailprop.mail.smtp.host" value="mymail.host.com"/>
 * 
 * <parameter name="mailMQModuleAlias" value="MailMQModule"/> </jobDetail> </module>
 * 
 * @author Eelco Hillenius
 */
public final class MailJob implements StatefulJob
{

	/** job parameter for JNDI reference to mail session: mailSessionRef */
	public static final String PARAMETER_KEY_JNDI_MAIL_SESSION = "mailSessionRef";

	/**
	 * job parameter that states the module alias to use for the MailMQModule
	 * implementation: mailMQModuleAlias
	 */
	public static final String PARAMETER_KEY_MAILMQMODULE_ALIAS = "mailMQModuleAlias";

	/**
	 * The least amount of time that should be between two retry attempts. The maximum
	 * amount of time between two retry attempts is RETRY_TIME + job execution interval +
	 * delay from waiting for previous job execution to finish If 0 or null (not
	 * specified), no retry is attempted.
	 */
	public static final String PARAMETER_KEY_RETRY_TIME = "retryTime";

	/**
	 * The last time messages were resend, or was checked if there were any messages that
	 * needed to be send again.
	 */
	public static final String PARAMETER_KEY_LAST_RETRY_TIME = "lastRetryTime";

	/**
	 * The maximum age of a message before it is classified as a lost cause and will no
	 * longer be send. The age of a message is determined by the time of creation of the
	 * message.
	 */
	public static final String PARAMETER_KEY_MAX_RETRY_AGE = "maxRetryAge";

	/** alias of MailMQModule implementation */
	protected static String mailMQModuleAlias = null;

	/* logger */
	private Logger log = LoggerFactory.getLogger(MailJob.class);

	/** mailproperties if available */
	protected Properties mailProperties = null;

	/*
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		@SuppressWarnings("unchecked")
		Map<String, Object> parameters = context.getJobDetail().getJobDataMap();
		String mailSessionRef = (String) parameters.get(PARAMETER_KEY_JNDI_MAIL_SESSION);

		try
		{
			if (mailSessionRef == null)
			{
				// fallback to normal mail API
				this.mailProperties = new Properties();
				log.debug("Reading mailproperties:");

				for (String key : parameters.keySet())
				{
					if (key.toLowerCase().startsWith("mailprop."))
					{
						String pname = key.substring(9);
						String pvalue = (String) parameters.get(key);
						mailProperties.setProperty(pname, pvalue);
						log.debug("{}[{}]", pname, pvalue);
					}
				}
				log.debug("Done.");
			}
			if (mailMQModuleAlias == null)
			{
				mailMQModuleAlias = (String) parameters.get(PARAMETER_KEY_MAILMQMODULE_ALIAS);
				if (mailMQModuleAlias == null)
				{
					// still null?
					String error =
						PARAMETER_KEY_MAILMQMODULE_ALIAS + " must be provided as a parameter";
					log.error(error);
					throw new Exception(error);
				}
			}
		}
		catch (Exception e)
		{
			log.error("Unschedule all triggers due to exception " + e.getMessage(), e);
			JobExecutionException je = new JobExecutionException(e, false);
			je.setErrorCode(SchedulerException.ERR_BAD_CONFIGURATION);
			je.setUnscheduleAllTriggers(true);
			throw je;
		}

		Session session = null;
		MailMQModule mqMod = null;
		try
		{
			Context ctx = new InitialContext();
			ComponentRepository mf = RepositoryFactory.getRepository();
			mqMod = (MailMQModule) mf.getComponent(mailMQModuleAlias);

			if (mailSessionRef != null)
			{
				log.debug("Using JDNDI mail session.");
				session = (Session) ctx.lookup(mailSessionRef);
			}
			else
			{
				log.debug("Using non-JNDI mail session.");
				session = Session.getDefaultInstance(mailProperties, null);
			}

		}
		catch (Exception ex)
		{
			log.error("Unschedule all triggers due to exception " + ex.getMessage(), ex);

			JobExecutionException e = new JobExecutionException(ex, false);
			e.setErrorCode(SchedulerException.ERR_BAD_CONFIGURATION);
			e.setUnscheduleAllTriggers(true);
			throw e;
		}

		// send current queue
		try
		{
			sendMessages(session, mqMod, mqMod.popQueue());
		}
		catch (Exception e)
		{
			log.error("Exception: " + e, e);
		}
		Object temp = parameters.get(PARAMETER_KEY_RETRY_TIME);
		if (temp instanceof String)
		{
			try
			{
				parameters.put(PARAMETER_KEY_RETRY_TIME, new Long(temp.toString()));
				log.info("retry time set to " + temp.toString() + " ms.");
			}
			catch (NumberFormatException e)
			{
				log.error("retry time is not a valid long", e);
				parameters.put(PARAMETER_KEY_RETRY_TIME, new Long(0));
			}
		}
		Long retryTime = (Long) parameters.get(PARAMETER_KEY_RETRY_TIME);
		if (retryTime == null)
			retryTime = new Long(0);
		temp = parameters.get(PARAMETER_KEY_MAX_RETRY_AGE);
		if (temp instanceof String)
		{
			try
			{
				parameters.put(PARAMETER_KEY_MAX_RETRY_AGE, new Long(temp.toString()));
				log.info("max retry age set to " + temp.toString() + " ms.");
			}
			catch (NumberFormatException e)
			{
				log.error("max retry age is not a valid long", e);
				parameters.put(PARAMETER_KEY_MAX_RETRY_AGE, new Long(0));
			}
		}
		Long retryAge = (Long) parameters.get(PARAMETER_KEY_MAX_RETRY_AGE);
		if (retryAge == null)
			retryAge = new Long(0);
		Long lastRetry = (Long) parameters.get(PARAMETER_KEY_LAST_RETRY_TIME);
		if (retryTime.longValue() > 0
			&& (lastRetry == null || System.currentTimeMillis() - lastRetry.longValue() >= retryTime
				.longValue()))
			;
		{
			parameters.put(PARAMETER_KEY_LAST_RETRY_TIME, new Long(System.currentTimeMillis()));
			try
			{
				long maxAge = retryAge.longValue();
				long now = System.currentTimeMillis();

				List<MailMessage> all = mqMod.getFailedMessages();
				List<MailMessage> filtered = all;
				if (maxAge > 0)
				{
					filtered = new ArrayList<MailMessage>();
					MailMessage tmp = null;
					for (int i = 0; i < all.size(); i++)
					{
						tmp = all.get(i);
						if (now - tmp.getCreated().longValue() < maxAge)
							filtered.add(tmp);
						else
							mqMod.flagFailedMessage(tmp, null);
					}
				}
				sendMessages(session, mqMod, filtered);
			}
			catch (Exception e)
			{
				log.error("Exception: ", e);
			}
		}
	}

	private void sendMessages(Session session, MailMQModule mqMod, List<MailMessage> messages)
			throws Exception
	{
		List<MailMessage> succeeded = new ArrayList<MailMessage>();
		try
		{
			log.info("Loaded {} MailMessages from the database.", messages.size());
			for (MailMessage msg : messages)
			{
				MimeMessage mailMsg = null;
				try
				{
					log.debug("Mailing message with id[{}]", msg.getId());
					mailMsg = constructMessage(msg, session);
					Transport.send(mailMsg);

					succeeded.add(msg);

				}
				catch (Exception e)
				{
					log.error("Exception sending message with id[" + msg.getId() + "]", e);
					try
					{
						mqMod.flagFailedMessage(msg, e);
					}
					catch (Exception e2)
					{
						// ooops this is pretty bad!
						log.error("Exception", e2);
						// nothing else to do than to ignore it
					}
				}
			}
			log.info("Succeeded sending {} messages.", succeeded.size());
			mqMod.removeFromQueue(succeeded);
		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
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
		InternetAddress[] address = {new InternetAddress(msg.getSendTo())};
		mailMsg.setRecipients(Message.RecipientType.TO, address);
		mailMsg.setSubject(msg.getSubject());
		mailMsg.setSentDate(new Date());
		// create and fill the first message part
		MimeBodyPart mbp1 = new MimeBodyPart();

		mbp1.setContent(msg.getMessage(), msg.getContentType());
		Multipart mp = new MimeMultipart();
		mp.addBodyPart(mbp1);

		// attach the attachments to the mailmessage
		if (msg.getAttachments() != null)
		{
			HashMap<String, FileDataSource> files = getAttachments(msg.getAttachments());
			Set<String> keySet = files.keySet();
			Iterator<String> keySetIterator = keySet.iterator();
			while (keySetIterator.hasNext())
			{
				String filename = keySetIterator.next();
				MimeBodyPart attachment = new MimeBodyPart();
				attachment.setDataHandler(new DataHandler(files.get(filename)));
				attachment.setFileName(filename);
				mp.addBodyPart(attachment);
			}
		}

		// add the Multipart to the message
		mailMsg.setContent(mp);

		return mailMsg;
	}

	/**
	 * Returns a hashmap with FileDataSources to attach to the mailmessage. The key of the
	 * FileDataSources is the filename.<br>
	 * The format of the string is as follow:<br>
	 * <br>
	 * file&lt;filename&gt;:file&lt;filename&gt;: . . . <br>
	 * (Note: the ':' is the system's path seperator. Windows: ';', Unix: ':')<br>
	 * <br>
	 * 'file' is the string that describes the File. 'filename' is the string that
	 * describes the name of the file.<br>
	 * 'filename' may be empty or missing, the name of the file will be the name that can
	 * be deduced from the file.
	 * 
	 * @param attachments
	 * @return
	 * @throws FileNotFoundException
	 */
	public HashMap<String, FileDataSource> getAttachments(String attachments)
			throws FileNotFoundException
	{
		HashMap<String, FileDataSource> files = new HashMap<String, FileDataSource>();

		StringTokenizer tk = new StringTokenizer(attachments, System.getProperty("path.separator"));
		while (tk.hasMoreTokens())
		{
			String file = null;
			String filename = null;

			String fileDefinition = tk.nextToken();
			int firstToken = fileDefinition.indexOf('<');
			int lastToken = fileDefinition.indexOf('>');
			if (firstToken <= 0 && lastToken <= 0)
			{
				file = fileDefinition;
			}
			else if (firstToken > 0)
			{
				file = fileDefinition.substring(0, firstToken);
			}

			if (firstToken > 0 && lastToken > 0)
			{
				filename = fileDefinition.substring(firstToken + 1, lastToken);
			}

			if (file != null)
			{
				FileDataSource fds = new FileDataSource(file);
				if (fds.getFile().exists())
				{
					if (filename != null)
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
