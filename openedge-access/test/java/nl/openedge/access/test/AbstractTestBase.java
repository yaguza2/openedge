package nl.openedge.access.test;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.gjt.mm.mysql.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * @author E.F. Hillenius
 * $Id$
 * based on example from Jakarta Commons DBCP project
 */
public abstract class AbstractTestBase extends TestCase {

	/** construct */
	public AbstractTestBase(String name) throws Exception {
		super(name);
		init();
	}

	/** 
	 * initialise
	 * 
	 */
	protected void init() throws Exception {

		InitialContext ctx = new InitialContext();
		
		MysqlConnectionPoolDataSource ds = 
				new MysqlConnectionPoolDataSource();
		String url = System.getProperty("dburl", 
			"jdbc:mysql://baas:3306/openedge_website");	
		String port = System.getProperty("dbport", "3306");
		String user = System.getProperty("dbuser", "root");
		String password = System.getProperty("dbpassword", "");
					
		ds.setUser( user );
		ds.setPassword( password );
		//ds.setURL( url ); 
		ds.setUrl( url );
		if( port != null && (!port.trim().equals("")))
							ds.setPort( Integer.parseInt(port) );
					
		ds.getConnection(); // check
		ctx.rebind(System.getProperty("datasource", "jdbc/openedge"), ds);
			
	}

}
