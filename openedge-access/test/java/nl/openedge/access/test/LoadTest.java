package nl.openedge.access.test;


import nl.openedge.access.AccessFactory;


/**
 * Testing storing of a file in the repository
 */
public class LoadTest extends AbstractTestBase {

	/**
	 * @see junit.framework.TestCase#TestCase(String)
	 */
	public LoadTest(String name) throws Exception {
		super(name);
	}

	/**
	 * set up for testing
	 */
	protected void setUp() throws Exception {
		
	}

	/**
	 * release
	 */
	protected void tearDown() throws Exception {
		
	}
	
	/** test the creation of the access factory */
	public void testAccessFactory() throws Exception {
		
		try {
			AccessFactory factory = new AccessFactory("/oeaccess.xml");
			assertNotNull(factory);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


}
