package nl.openedge.access.test;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Testsuite for OpenEdge Access tests
 * 
 * @author hillenius
 */
public class AccessTestSuite extends TestSuite {
	
	/**
	 * construct test suite
	 * @return Test
	 */
	public static Test suite() {
		AccessTestSuite suite = new AccessTestSuite();		
		suite.addTestSuite(UserManagerTest.class);
		return suite;
	}
}
