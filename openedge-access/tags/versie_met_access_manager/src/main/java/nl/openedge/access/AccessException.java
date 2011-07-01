/*
 * Created on 4-apr-2003
 */
package nl.openedge.access;

/**
 * @author Hillenius
 * $Id$
 */
public class AccessException extends Exception {

	/**
	 * 
	 */
	public AccessException() {
		super();
	}

	/**
	 * @param message
	 */
	public AccessException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public AccessException(Throwable cause) {
		super(cause);
	}

}
