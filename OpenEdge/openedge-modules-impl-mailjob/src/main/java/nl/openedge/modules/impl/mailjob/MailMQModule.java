package nl.openedge.modules.impl.mailjob;

import java.util.List;

/**
 * Implementations of MailMQModule provide message queue facilities for email messages
 * 
 * @author Eelco Hillenius
 */
public interface MailMQModule
{
	/**
	 * adds a message to the queue (status is 'new')
	 * 
	 * @param msg
	 *            message to add
	 * @throws Exception
	 */
	public void addMessageToQueue(MailMessage msg) throws Exception;

	/**
	 * adds an array of messages to queue (status is 'new')
	 * 
	 * @param msgs
	 *            messages to add
	 * @throws Exception
	 */
	public void addMessageToQueue(MailMessage[] msgs) throws Exception;

	/**
	 * flag message as failed and set failure reason from exception
	 * 
	 * @param msg
	 * @param t
	 * @throws Exception
	 */
	public void flagFailedMessage(MailMessage msg, Throwable t) throws Exception;

	/**
	 * pop current messages from queue; sets status to 'read'
	 * 
	 * @throws Exception
	 */

	public List<MailMessage> popQueue() throws Exception;

	/**
	 * remove given messages from queue; depending on property 'deleteOnRemove' it will
	 * delete messages from store or set flag to 'succeeded'
	 * 
	 * @param messagesToRemove
	 * @throws Exception
	 */
	public void removeFromQueue(List<MailMessage> messagesToRemove) throws Exception;

	/**
	 * Returns a list of failed messages.
	 * 
	 * @return a list of messages or an empty list if no message failed.
	 * @throws Exception
	 */
	public List<MailMessage> getFailedMessages() throws Exception;
}
