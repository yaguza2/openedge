package nl.openedge.access.test;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author hillenius
 */
public class AccessTestSuite extends TestSuite {
	
	public static Test suite() {
		AccessTestSuite suite = new AccessTestSuite();		
		suite.addTestSuite(LoadTest.class);
		return suite;
	}
}
