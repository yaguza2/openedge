/*
 * Created on 4-apr-2003
 */
package nl.openedge.access.impl.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * @author Hillenius
 * $Id$
 */
public abstract class JDBCBase {

	/** datasource */
	protected DataSource dataSource = null;

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
				
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
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
				
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
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
	 * @return DataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Sets the dataSource.
	 * @param dataSource The dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
