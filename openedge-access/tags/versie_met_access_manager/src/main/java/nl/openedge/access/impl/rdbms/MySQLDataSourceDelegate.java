package nl.openedge.access.impl.rdbms;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.gjt.mm.mysql.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * 
 * Constructs a MySQL datasource for test/ command line usage
 * 
 * @author E.F. Hillenius
 * $Id$
 */
public class MySQLDataSourceDelegate implements DataSourceDelegate {

	/**
	 * @see nl.openedge.access.impl.rdbms.DataSourceDelegate#getDataSource(org.jdom.Element)
	 */
	public DataSource getDataSource(Map parameters) throws SQLException {
	
		MysqlConnectionPoolDataSource datasource = 
				new MysqlConnectionPoolDataSource();	
		String dbname = (String)parameters.get("dbname");
		String dbservername = (String)parameters.get("dbservername");
		String port = (String)parameters.get("dbport");
		String user = (String)parameters.get("dbuser");
		String password = (String)parameters.get("dbpassword");		
		datasource.setUser( user );
		datasource.setPassword( password );
		datasource.setDatabaseName( dbname ); 
		datasource.setServerName( dbservername );
		if( port != null && (!port.trim().equals("")))
			datasource.setPort( Integer.parseInt(port) );					
		datasource.getConnection(); // check
		return datasource;	
	}
	
	/** get string repr */
	public String toString() {
		return "MySQLDataSourceDelegate";
	}

}
