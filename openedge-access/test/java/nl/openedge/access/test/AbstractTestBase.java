package nl.openedge.access.test;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import nl.openedge.access.AccessFactory;

import org.gjt.mm.mysql.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * This is the baseclass for testcases.
 * 
 * We 'fake' a datasource here using MysqlConnectionPoolDataSource
 * For this to work rmiregistry should run and a rmi based jndi implementation
 * has to be used, eg:
 * java.naming.factory.initial=com.sun.jndi.rmi.registry.RegistryContextFactory
 * Furthermore, we load the accessFactory
 * 
 * The following environment variables are used if they are provided:
 * <ul>
 * 	<li>dbservername - location of MySQL database</li>
 * 	<li>dbname - name of database</li>
 * 	<li>dbport - database port to connect to</li>
 * 	<li>dbuser - username for database connection</li>
 * 	<li>dbpassword - password for database connection</li>
 * 	<li>datasource - name to bind the datasource to in rmi context</li>
 * 	<li>configfile - location url of OpenEdge Access configuration file</li>
 * </ul>
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

		setUpDatasource();
		loadAccessFactory();
	}
	
	/**
	 * setup the datasource
	 * @throws Exception
	 */
	protected void setUpDatasource() throws Exception {
		
		InitialContext ctx = new InitialContext();
		MysqlConnectionPoolDataSource ds = 
				new MysqlConnectionPoolDataSource();	
		String dbname = System.getProperty("dbname", "openedge_website");
		String dbservername = System.getProperty("dbservername", "baas");
		String port = System.getProperty("dbport", "3306");
		String user = System.getProperty("dbuser", "root");
		String password = System.getProperty("dbpassword", "");		
		ds.setUser( user );
		ds.setPassword( password );
		ds.setDatabaseName( dbname ); 
		ds.setServerName( dbservername );
		if( port != null && (!port.trim().equals("")))
							ds.setPort( Integer.parseInt(port) );					
		ds.getConnection(); // check
		ctx.rebind(System.getProperty("datasource", "jdbc/openedge"), ds);		
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

}
