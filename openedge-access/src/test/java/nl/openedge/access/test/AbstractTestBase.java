package nl.openedge.access.test;

import java.util.Collection;

import junit.framework.TestCase;

import nl.openedge.access.AccessFactory;

/**
 * This is the baseclass for testcases.
 * It does some initialisation and provides additional test methods
 * 
 * @author E.F. Hillenius
 * $Id$
 */
public abstract class AbstractTestBase extends TestCase {

	/** access factory */
	protected AccessFactory accessFactory;

	/** construct */
	public AbstractTestBase(String name) throws Exception {
		super(name);
		init();
	}

	/** 
	 * initialise
	 */
	protected void init() throws Exception {

		loadAccessFactory();
	}
	
	/**
	 * load the access factory
	 * @throws Exception
	 */
	protected void loadAccessFactory() throws Exception {
		try {
			accessFactory = new AccessFactory(
				System.getProperty("configfile", "/oeaccess.xml"));
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}	
	}
	
	/**
	 * Asserts that two collections contain the same elements. 
	 * If they do not it throws an AssertionFailedError with the given message.
	 */
	static public void assertSameContents(String message, 
			Collection c1, Collection c2) {
		if(c1 == null || c2 == null || (!c1.containsAll(c2))) 
			fail(message);
	}
	
	/**
	 * Asserts that two collections do not contain the same elements. 
	 * If they do not, it throws an AssertionFailedError with the given message.
	 */
	static public void assertNotSameContents(String message,
			Collection c1, Collection c2) {
		if(c1 == null && c2 == null) return;
		if(c1 == null || c2 == null || (!c1.containsAll(c2))) 
			fail(message);
	}
}
