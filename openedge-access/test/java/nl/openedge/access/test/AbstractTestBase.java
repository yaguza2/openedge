package nl.openedge.access.test;

import java.util.Enumeration;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.gjt.mm.mysql.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * @author E.F. Hillenius
 * $Id$
 * based on example from Jakarta Commons DBCP project
 */
public abstract class AbstractTestBase extends TestCase {


	public static final String DATASOURCE_NAME = "jdbc/openedge";

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
		String connAlias = "openedge_website";
		String url = "jdbc:mysql://baas:3306/openedge_website";	
		String port = null;// = "3306";
		String user = "root";
		String password = "";
					
		ds.setUser( user );
		ds.setPassword( password );
		//ds.setURL( url ); 
		ds.setUrl( url );
		if( port != null && (!port.trim().equals("")))
							ds.setPort( Integer.parseInt(port) );
					
		ds.getConnection(); // check
		ctx.rebind("jdbc/openedge", ds);
		
		Enumeration en = ctx.list("");
		System.out.println("list rmi: -------------------------------------------");
		while(en.hasMoreElements()) {
			System.out.println("el: " + en.nextElement());
		}
		System.out.println("{end} list rmi: -------------------------------------------");
		
	}

}
