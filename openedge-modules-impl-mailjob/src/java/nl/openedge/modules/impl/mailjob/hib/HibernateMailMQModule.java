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
package nl.openedge.modules.impl.mailjob.hib;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import nl.openedge.modules.types.base.SingletonType;
import nl.openedge.modules.impl.mailjob.MailMQModule;
import nl.openedge.modules.impl.mailjob.MailMessage;
import nl.openedge.util.hibernate.HibernateHelper;

/**
 * Hibernate based implementation of MailMQModule
 * @author Eelco Hillenius
 */
public class HibernateMailMQModule implements SingletonType, MailMQModule
{
	private Log log = LogFactory.getLog(this.getClass());

	// object query to get new messages
	private static final String listNewMessages =
		"from m in class " + MailMessage.class.getName() + " where m.status = 'new'";

	/* if true, delete messages from store, if not set flag to succeeded */
	private boolean deleteOnRemove = true;

	/** 
	 * adds a message to the queue (status is 'new')
	 * @param msg message to add
	 * @throws Exception
	 */
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
			log.fatal("Exception: ", e);
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
	 * @param msgs messages to add
	 * @throws Exception
	 */
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
			log.fatal("Exception: ", e);
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
	 * @param msg
	 * @param e
	 * @throws Exception
	 */
	public synchronized void flagFailedMessage(MailMessage msg, Throwable t) throws Exception
	{

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			msg = (MailMessage)session.load(MailMessage.class, msg.getId());
			if (msg == null)
			{
				throw new Exception(
					"mail message with id "
						+ msg.getId()
						+ " was not found in the persistent store");
			}

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
			msg.setStatus(MailMessage.STATUS_FAILED);
			msg.setStatusDetail(errorMsg);

			tx.commit();

		}
		catch (Exception e)
		{
			log.fatal("Exception: ", e);
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
	 * @param msg
	 */
	public synchronized List popQueue() throws Exception
	{

		List messages = null;

		Session session = HibernateHelper.getSessionFactory().openSession();
		Transaction tx = null;
		try
		{
			tx = session.beginTransaction();

			messages = session.find(listNewMessages);
			if (messages != null)
			{
				for (Iterator i = messages.iterator(); i.hasNext();)
				{

					MailMessage message = (MailMessage)i.next();
					// set flag to 'read'
					message.setStatus(MailMessage.STATUS_READ);
					// persist change
					session.saveOrUpdate(message);
				}
			}

			tx.commit();

		}
		catch (Exception e)
		{
			log.fatal("Exception: ", e);
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

		return messages;
	}

	/**
	 * remove given messages from queue;
	 * depending on property 'deleteOnRemove' it will delete messages from store
	 * or set flag to 'succeeded' 
	 * @param msg
	 */
	public synchronized void removeFromQueue(List messagesToRemove) throws Exception
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
					for (Iterator i = messagesToRemove.iterator(); i.hasNext();)
					{

						MailMessage message = (MailMessage)i.next();
						session.delete(message);
					}
				}

			}
			else
			{ // set flag

				if (messagesToRemove != null)
				{
					for (Iterator i = messagesToRemove.iterator(); i.hasNext();)
					{

						MailMessage message = (MailMessage)i.next();
						// set flag to 'read'
						message.setStatus(MailMessage.STATUS_SUCCEEDED);
					}
				}

			}

			tx.commit();

		}
		catch (Exception e)
		{
			log.fatal("Exception: ", e);
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

}
