package nl.openedge.util.web;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of sessions.
 * 
 * @author Eelco Hillenius
 */
public class SessionListener implements HttpSessionListener, HttpSessionActivationListener
{
	/** sessions. */
	private static Vector<HttpSession> sessions = new Vector<HttpSession>();

	/** logger. */
	private Logger log = LoggerFactory.getLogger(SessionListener.class);

	/**
	 * Constructor.
	 */
	public SessionListener()
	{
		// nothing here
	}

	/**
	 * Record the fact that a session has been created. add session to internal store.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " created");
		synchronized (sessions)
		{
			sessions.add(event.getSession());
		}
	}

	/**
	 * Record the fact that a session has been destroyed. Remove session from internal
	 * store.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " destroyed");
		synchronized (sessions)
		{
			sessions.remove(event.getSession());
		}
	}

	/**
	 * Notification that the session is about to be passivated.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionWillPassivate(HttpSessionEvent event)
	{

		log.info(event.getSession().getId() + " passivated");
		synchronized (sessions)
		{
			sessions.remove(event.getSession());
		}
	}

	/**
	 * Notification that the session has just been activated.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionDidActivate(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " activated");
		synchronized (sessions)
		{
			sessions.add(event.getSession());
		}
	}

	/**
	 * get known sessions (for this server).
	 * 
	 * @return List sessions known to this listener
	 */
	@SuppressWarnings("unchecked")
	public static List<HttpSession> getSessions()
	{
		synchronized (sessions)
		{
			return (List<HttpSession>) sessions.clone();
		}
	}

}
