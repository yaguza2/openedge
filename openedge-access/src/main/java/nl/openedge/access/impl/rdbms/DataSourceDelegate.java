package nl.openedge.access.impl.rdbms;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;


/**
 * Interface for delegates that produce DataSources
 * 
 * @author E.F. Hillenius
 * $Id$
 */
public interface DataSourceDelegate {

	/**
	 * get/ construct datasource
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	public DataSource getDataSource(Map parameters) throws SQLException;

}
