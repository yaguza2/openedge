package nl.openedge.access;

import java.io.Serializable;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubjectSessionListener implements HttpSessionBindingListener, Serializable
{
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SubjectSessionListener.class);

	private Subject subject = null;

	public SubjectSessionListener(Subject subject)
	{
		this.subject = subject;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event)
	{
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event)
	{
		if (subject != null)
		{
			log.debug("logout for {}", subject);

			// just remove everything
			subject.getPrincipals().clear();
			subject.getPublicCredentials().clear();
			subject.getPrivateCredentials().clear();
		}
		else
		{
			log.warn("no subject found");
		}
	}
}
