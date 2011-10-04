package nl.openedge.modules.impl.mailjob.hib;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.openedge.modules.impl.mailjob.MailMQModule;
import nl.openedge.modules.impl.mailjob.MailMessage;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.util.hibernate.HibernateHelper;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate based implementation of MailMQModule
 * 
 * @author Eelco Hillenius
 */
public class HibernateMailMQModule implements SingletonType, MailMQModule
{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	// object query to get new messages
	private static final String listNewMessages = "from m in class " + MailMessage.class.getName()
		+ " where m.status = '" + MailMessage.STATUS_NEW + "'";

	private static final String listFailedMessages = "from m in class "
		+ MailMessage.class.getName() + " where m.status = '" + MailMessage.STATUS_FAILED + "'";

	/* if true, delete messages from store, if not set flag to succeeded */
	private boolean deleteOnRemove = true;

	/**
	 * adds a message to the queue (status is 'new')
	 * 
	 * @param msg
	 *            message to add
	 * @throws Exception
	 */
	@Override
	public synchronized void addMessageToQueue(MailMessage msg) throws Exception
	{

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			session.save(msg);

			tx.commit();

		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
			if (tx != null)
			{

				tx.rollback();
			}
			throw e;
		}
		finally
		{
			session.close();
		}

	}

	/**
	 * adds an array of messages to queue (status is 'new')
	 * 
	 * @param msgs
	 *            messages to add
	 * @throws Exception
	 */
	@Override
	public synchronized void addMessageToQueue(MailMessage[] msgs) throws Exception
	{

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			int size = msgs.length;
			for (int i = 0; i < size; i++)
			{
				session.save(msgs[i]);
			}

			tx.commit();

		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
			if (tx != null)
			{

				tx.rollback();
			}
			throw e;
		}
		finally
		{
			session.close();
		}

	}

	/**
	 * flag message as failed and set failure reason from exception
	 * 
	 * @param msg
	 * @param t
	 * @throws Exception
	 */
	@Override
	public synchronized void flagFailedMessage(MailMessage msg, Throwable t) throws Exception
	{
		if (msg == null)
			return;

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			Long id = msg.getId();
			msg = (MailMessage) session.load(MailMessage.class, id);
			if (msg == null)
			{
				throw new Exception("mail message with id " + id
					+ " was not found in the persistent store");
			}
			if (t != null)
			{
				String errorMsg;
				try
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					PrintWriter pw = new PrintWriter(bos);
					t.printStackTrace(pw);
					pw.flush();
					pw.close();
					bos.flush();
					bos.close();
					errorMsg = bos.toString();
				}
				catch (Exception ex)
				{
					errorMsg = t.getMessage();
				}
				msg.setStatusDetail(errorMsg);
			}
			msg.setStatus(MailMessage.STATUS_FAILED);
			tx.commit();
		}
		catch (Exception e)
		{
			log.error("Exception bij afhandeling van:", t);
			log.error("Exception: ", e);
			if (tx != null)
			{
				tx.rollback();
			}
			throw e;
		}
		finally
		{
			session.close();
		}
	}

	/**
	 * pop current messages from queue; sets status to 'read'
	 */
	@Override
	public synchronized List<MailMessage> popQueue() throws Exception
	{
		return getMailMessages(listNewMessages);
	}

	/**
	 * remove given messages from queue; depending on property 'deleteOnRemove' it will
	 * delete messages from store or set flag to 'succeeded'
	 */
	@Override
	public synchronized void removeFromQueue(List<MailMessage> messagesToRemove) throws Exception
	{

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			if (deleteOnRemove)
			{ // delete from store

				if (messagesToRemove != null)
				{
					for (Iterator<MailMessage> i = messagesToRemove.iterator(); i.hasNext();)
					{

						MailMessage message = i.next();
						session.delete(message);
					}
				}

			}
			else
			{ // set flag

				if (messagesToRemove != null)
				{
					for (Iterator<MailMessage> i = messagesToRemove.iterator(); i.hasNext();)
					{
						MailMessage message = i.next();
						// set flag to 'read'
						message.setStatus(MailMessage.STATUS_SUCCEEDED);
					}
				}
			}
			tx.commit();
		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
			if (tx != null)
			{

				tx.rollback();
			}
			throw e;
		}
		finally
		{
			session.close();
		}

	}

	/**
	 * @return boolean
	 */
	public boolean isDeleteOnRemove()
	{
		return deleteOnRemove;
	}

	/**
	 * @param deleteOnRemove
	 */
	public void setDeleteOnRemove(boolean deleteOnRemove)
	{
		this.deleteOnRemove = deleteOnRemove;
	}

	/**
	 * @see nl.openedge.modules.impl.mailjob.MailMQModule#getFailedMessages()
	 */
	@Override
	public List<MailMessage> getFailedMessages() throws Exception
	{
		return getMailMessages(listFailedMessages);
	}

	/**
	 * returns the messages specified in the query, messages are marked as read!
	 * 
	 * @param query
	 *            the query specifying what messages to get
	 * @return a list of messages or an empty list if no messages matches the criteria.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<MailMessage> getMailMessages(String query) throws Exception
	{
		List<MailMessage> messages = null;

		Session session = null;
		Transaction tx = null;
		try
		{
			session = HibernateHelper.getSessionFactory().openSession();
			tx = session.beginTransaction();

			messages = session.createQuery(query).list();
			if (messages != null)
			{
				for (Iterator<MailMessage> i = messages.iterator(); i.hasNext();)
				{
					MailMessage message = i.next();
					// set flag to 'read'
					message.setStatus(MailMessage.STATUS_READ);
				}
			}
			tx.commit();
		}
		catch (Exception e)
		{
			log.error("Exception: ", e);
			if (tx != null)
				try
				{
					tx.rollback();
				}
				catch (HibernateException err)
				{
					log.error("Rollback failed: ", err);
				}
			throw e;
		}
		finally
		{
			try
			{
				if (session != null)
					session.close();
			}
			catch (HibernateException e)
			{
				log.error("Rollback failed: ", e);
			}
		}
		if (messages == null)
			messages = Collections.emptyList();
		return messages;
	}
}