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

/**
 * Holder for email message
 * @author Eelco Hillenius
 * @hibernate.class		table="email_queue"
 */
public final class MailMessage
{

	public final static String STATUS_NEW = "new";
	public final static String STATUS_READ = "read";
	public final static String STATUS_FAILED = "failed";
	public final static String STATUS_SUCCEEDED = "succeeded";

	private Long id;
	private String sendTo;
	private String subject;
	private String sender;
	private String message;
	private Long created;
	private String contentType = "text/plain";
	private String status = STATUS_NEW;
	private String statusDetail;

	public MailMessage()
	{
		this.created = new Long(System.currentTimeMillis());
	}

	/**
	 * @return Long
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * @return String
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @return Long
	 */
	public Long getCreated()
	{
		return created;
	}

	/**
	 * @return String
	 */
	public String getSender()
	{
		return sender;
	}

	/**
	 * @return String
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return String
	 */
	public String getSendTo()
	{
		return sendTo;
	}

	/**
	 * @return String
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @return String
	 */
	public String getStatusDetail()
	{
		return statusDetail;
	}

	/**
	 * @return String
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param contentType
	 */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/**
	 * @param created
	 */
	public void setCreated(Long created)
	{
		this.created = created;
	}

	/**
	 * @param sender
	 */
	public void setSender(String from)
	{
		this.sender = from;
	}

	/**
	 * @param id
	 */
	public void setId(Long id)
	{
		this.id = id;
	}

	/**
	 * @param message
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @param sendTo
	 */
	public void setSendTo(String sendTo)
	{
		this.sendTo = sendTo;
	}

	/**
	 * @param status
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @param statusDetail
	 */
	public void setStatusDetail(String statusDetail)
	{
		this.statusDetail = statusDetail;
	}

	/**
	 * @param subject
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

}
