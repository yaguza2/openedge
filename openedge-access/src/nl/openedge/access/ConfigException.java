/*
 * Created on 4-apr-2003
 */
package nl.openedge.access;

/**
 * @author Hillenius
 * $Id$
 */
public class ConfigException extends Exception {

	/**
	 * 
	 */
	public ConfigException() {
		super();
	}

	/**
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public ConfigException(Throwable cause) {
		super(cause);
	}

}
