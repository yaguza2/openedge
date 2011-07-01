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

import java.util.List;

/**
 * Implementations of MailMQModule provide message queue facilities for
 * email messages
 * @author Eelco Hillenius
 */
public interface MailMQModule
{

	/**
	 * adds a message to the queue (status is 'new')
	 * @param msg message to add
	 * @throws Exception
	 */
	public void addMessageToQueue(MailMessage msg) throws Exception;

	/**
	 * adds an array of messages to queue (status is 'new')
	 * @param msgs messages to add
	 * @throws Exception
	 */
	public void addMessageToQueue(MailMessage[] msgs) throws Exception;

	/**
	 * flag message as failed and set failure reason from exception
	 * @param msg
	 * @param e
	 * @throws Exception
	 */
	public void flagFailedMessage(MailMessage msg, Throwable t) throws Exception;

	/**
	 * pop current messages from queue; sets status to 'read'
	 * @param msg
	 * @throws Exception
	 */

	public List popQueue() throws Exception;

	/**
	 * remove given messages from queue;
	 * depending on property 'deleteOnRemove' it will delete messages from store
	 * or set flag to 'succeeded' 
	 * @param msg
	 * @throws Exception
	 */
	public void removeFromQueue(List messagesToRemove) throws Exception;
	/**
	 * Returns a list of failed messages.
	 * 
	 * @return a list of messages or an empty list if no message failed.
	 * @throws Exception
	 */
	public List getFailedMessages()throws Exception;
}