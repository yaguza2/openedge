package nl.openedge.access.impl.rdbms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base class for Rdbms/ JDBC implementations
 * 
 * @author Hillenius
 * $Id$
 */
public abstract class RdbmsBase {

	/** datasource */
	protected static DataSource dataSource = null;
	
	/** queries from properties file */
	protected static Properties queries = null;

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	/* init flag */
	private static boolean initialised = false;

	/** construct and read queries */
	public RdbmsBase() {
		
		if(!initialised) {
			
			initialised = true;
			try {
				queries = new Properties();
				queries.load(getClass().getResourceAsStream(
						"RdbmsQueries.properties"));
					
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * excecute query and return result
	 * @param stmt
	 * @param params
	 * @return QueryResult
	 * @throws SQLException
	 */
	protected QueryResult excecuteQuery(String stmt, Object[] params) 
				throws SQLException {
		
		QueryResult result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		Connection conn = dataSource.getConnection();   
		try {
				
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			rs = pstmt.executeQuery();
			result = new QueryResult(rs, -1, -1);
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}

	/**
	 * excecutes an update query and return number of rows affected
	 * @param stmt
	 * @param params
	 * @return int
	 * @throws SQLException
	 */
	protected int excecuteUpdate(String stmt, Object[] params) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result;
				
		Connection conn = dataSource.getConnection();   
		try {
			
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);	
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			result = pstmt.executeUpdate();
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}
	
	/**
	 * trivial update of a row in a single table
	 * @param keyFields
	 * @param nonKeyFields
	 * @return int
	 * @throws SQLException
	 */
	protected int update(String table, Map keyFields, Map nonKeyFields) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean more;
		int result;
		
		Object[] params = new Object[nonKeyFields.size() + keyFields.size()];		
		int paramCounter = 0;
		// create the sql statement
		StringBuffer b = new StringBuffer("update ")
			.append(table);
		// field part
		more = false;
		for(Iterator i = nonKeyFields.keySet().iterator(); i.hasNext(); ) {
			if(more) b.append(", ");
			String key = (String)i.next();
			b.append(key)
			 .append(" = ?");
			more = true;
			params[paramCounter] = nonKeyFields.get(key);
			paramCounter++;
		}
		// pk part
		b.append(" where ");
		more = false;
		for(Iterator i = keyFields.keySet().iterator(); i.hasNext(); ) {
			if(more) b.append(" and ");
			String key = (String)i.next();
			b.append(key)
			 .append(" = ?");
			more = true;
			params[paramCounter] = keyFields.get(key);
			paramCounter++;
		}		
				
		Connection conn = dataSource.getConnection();   
		try {
			
			String stmt = b.toString();
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);	
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			result = pstmt.executeUpdate();
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}
	
	/**
	 * trivial insert of a row in a single table
	 * @param keyFields
	 * @param nonKeyFields
	 * @return int
	 * @throws SQLException
	 */
	protected int insert(String table, Map fields) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean more;
		int result;
		
		Object[] params = new Object[fields.size()];		
		int paramCounter = 0;
		// create the sql statement
		StringBuffer b = new StringBuffer("insert into ")
			.append(table)
			.append(" (");
		more = false;
		for(Iterator i = fields.keySet().iterator(); i.hasNext(); ) {
			if(more) b.append(", ");
			String key = (String)i.next();
			b.append(key);
			more = true;
			params[paramCounter] = fields.get(key);
			paramCounter++;
		}
		b.append(") values (");
		for(int i = 0; i < params.length; i++) {
			if(i > 0) b.append(", ");
			b.append(" ?");		
		}
		b.append(")");
				
		Connection conn = dataSource.getConnection();   
		try {
			
			String stmt = b.toString();
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);	
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			result = pstmt.executeUpdate();
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}
	
	/**
	 * trivial update of a row in a single table
	 * @param keyFields
	 * @param nonKeyFields
	 * @return int
	 * @throws SQLException
	 */
	protected int delete(String table, Map keyFields) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result;
		
		Object[] params = new Object[keyFields.size()];		
		int paramCounter = 0;
		// create the sql statement
		StringBuffer b = new StringBuffer("delete from ")
			.append(table);
		if(params.length > 0) {
			b.append(" where ");
			// pk part
			boolean more = false;
			for(Iterator i = keyFields.keySet().iterator(); i.hasNext(); ) {
				if(more) b.append(" and ");
				String key = (String)i.next();
				b.append(key)
				 .append(" = ?");
				more = true;
				params[paramCounter] = keyFields.get(key);
				paramCounter++;
			}
		}		
				
		Connection conn = dataSource.getConnection();   
		try {
			
			String stmt = b.toString();
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);	
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			result = pstmt.executeUpdate();
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}
	
	/**
	 * trivial select row(s) from one table
	 * @param table
	 * @param keyFields
	 * @return QueryResult
	 * @throws SQLException
	 */
	protected QueryResult select(String table, Map keyFields) 
				throws SQLException {
		
		QueryResult result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Object[] params = new Object[keyFields.size()];		
		int paramCounter = 0;
		// create the sql statement
		StringBuffer b = new StringBuffer("select * from ")
			.append(table);
		if(params.length > 0) {
			b.append(" where ");
			// pk part
			boolean more = false;
			for(Iterator i = keyFields.keySet().iterator(); i.hasNext(); ) {
				if(more) b.append(" and ");
				String key = (String)i.next();
				b.append(key)
				 .append(" = ?");
				more = true;
				params[paramCounter] = keyFields.get(key);
				paramCounter++;
			}
		}
				
		Connection conn = dataSource.getConnection();   
		try {
				
			String stmt = b.toString();
			if(log.isDebugEnabled()) log.debug("execute: " + stmt);
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				if(log.isDebugEnabled()) log.debug("\tparam " + paramIndex + " = " + params[i]);
				pstmt.setObject(paramIndex, params[i]);
			}
			rs = pstmt.executeQuery();
			result = new QueryResult(rs, -1, -1);
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}


	/**
	 * @return DataSource
	 */
	public static DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the dataSource.
	 * @param dataSource The dataSource to set
	 */
	public static void setDataSource(DataSource theDataSource) {
		dataSource = theDataSource;
	}

}
