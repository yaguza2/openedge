package nl.openedge.modules.impl.mailjob;

/**
 * Holder for email message.
 * 
 * To attach files to the message, the 'Attachments' string must be set. The format of the
 * string is as follows: {@code  file<filename>:file<filename>: . . .} (Note: the ':' is
 * the system's path separator. Windows: ';', Unix: ':')
 * <p>
 * {@code file} is the string that describes the File. {@code filename} is the string that
 * describes the name of the file. {@code filename} may be empty or missing, the name of
 * the file will be the name that can be deduced from the file.
 * 
 * @author Eelco Hillenius
 * @hibernate.class table="email_queue"
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

	private String attachments;

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
	 * @param from
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

	/**
	 * @return
	 */
	public String getAttachments()
	{
		return attachments;
	}

	/**
	 * @param string
	 */
	public void setAttachments(String string)
	{
		attachments = string;
	}
}
