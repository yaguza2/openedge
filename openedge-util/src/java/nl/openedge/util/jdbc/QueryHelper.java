/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;
import javax.sql.DataSource;

import org.apache.taglibs.standard.tag.common.sql.ResultImpl;

/**
 * @author Eelco Hillenius
 */
public class QueryHelper extends DataSourceBase {

	/**
	 * construct and get datasource from JNDI location
	 * @param jndiRef
	 * @throws Exception
	 */
	public QueryHelper(String jndiRef) throws Exception {
		super(jndiRef);		
	}
	
	/**
	 * construct using given datasource
	 * @param dataSource
	 */
	public QueryHelper(DataSource dataSource) throws Exception {
		super(dataSource);
	}
	
	/**
	 * construct and create datasource with given parameters
	 * Use a map like:
	 * 
	 *	driverClassName=org.gjt.mm.mysql.Driver
	 *	url=jdbc:mysql://localhost:3306/foo_db
	 *	username=root
	 *	password=
	 *	maxActive=20
	 *	maxIdle=10
	 *	maxWait=5000
	 *	defaultAutoCommit=false
	 * 
	 * @param constructionParameters populated map to create datasource
	 */
	public QueryHelper(Map constructionParameters) throws Exception {
		super(constructionParameters);
	}
	
	/**
	 * construct and create datasource with given parameters
	 * Use a map like:
	 * 
	 *	driverClassName=org.gjt.mm.mysql.Driver
	 *	url=jdbc:mysql://localhost:3306/foo_db
	 *	username=root
	 *	password=
	 *	maxActive=20
	 *	maxIdle=10
	 *	maxWait=5000
	 *	defaultAutoCommit=false
	 * 
	 * @param constructionParameters populated map to create datasource
	 * @param createOnce if true, we'll look if a datasource was allready initialized which can be used in that case 
	 */
	public QueryHelper(Map constructionParameters, boolean createOnce) throws Exception {
		super(constructionParameters, createOnce);
	}

	/**
	 * excecute query and return result
	 * @param stmt
	 * @param params
	 * @return QueryResult
	 * @throws SQLException
	 */
	public Result excecuteQuery(String stmt, Object[] params) 
				throws SQLException {
		
		Result result = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
				
		Connection conn = getDataSource().getConnection();   
		try {
				
			pstmt = conn.prepareStatement(stmt);
			setParameters(pstmt, params);
			rs = pstmt.executeQuery();
			result = new ResultImpl(rs, -1, -1);
			
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
	public int excecuteUpdate(String stmt, Object[] params) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result;
				
		Connection conn = getDataSource().getConnection();   
		try {
			
			pstmt = conn.prepareStatement(stmt);
			setParameters(pstmt, params);
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
	public int update(String table, Map keyFields, Map nonKeyFields) 
				throws SQLException {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean more;
		int result;
		
		Object[] params = new Object[nonKeyFields.size() + keyFields.size()];		
		int paramCounter = 0;
		// create the sql statement
		StringBuffer b = new StringBuffer("update ")
			.append(table)
			.append(" set ");
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
				
		Connection conn = getDataSource().getConnection();   
		try {
			
			String stmt = b.toString();
			pstmt = conn.prepareStatement(stmt);
			setParameters(pstmt, params);
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
	public int insert(String table, Map fields) 
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
				
		Connection conn = getDataSource().getConnection();   
		try {
			
			String stmt = b.toString();
			pstmt = conn.prepareStatement(stmt);
			setParameters(pstmt, params);
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
	public int delete(String table, Map keyFields) 
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
				
		Connection conn = getDataSource().getConnection();   
		try {
			
			String stmt = b.toString();
			pstmt = conn.prepareStatement(stmt);
			setParameters(pstmt, params);
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
	
	private void setParameters(PreparedStatement pstmt, Object[] params)
						throws SQLException {
	
		int paramIndex = 0;
		for(int i = 0; i < params.length; i++) {
			paramIndex++;
			if(params[i] instanceof Date) { // hack for inconsistent setObject behaviour
				pstmt.setDate(paramIndex, new java.sql.Date(((Date)params[i]).getTime()));
			} else{
				pstmt.setObject(paramIndex, params[i]);
			}
		}
		
	}
	
	/**
	 * trivial select row(s) from one table
	 * @param table
	 * @param keyFields
	 * @return QueryResult
	 * @throws SQLException
	 */
	public Result select(String table, Map keyFields) 
				throws SQLException {
		
		Result result = null;
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
				
		Connection conn = getDataSource().getConnection();   
		try {
				
			String stmt = b.toString();
			pstmt = conn.prepareStatement(stmt);
			int paramIndex = 0;
			for(int i = 0; i < params.length; i++) {
				paramIndex++;
				pstmt.setObject(paramIndex, params[i]);
			}
			rs = pstmt.executeQuery();
			result = new ResultImpl(rs, -1, -1);
			
		} catch(SQLException e) {
			throw e;
		} finally {
			if(rs != null) try { rs.close(); } catch(SQLException sqle) { }
			if(pstmt != null) try { pstmt.close(); } catch(SQLException sqle) { }
			if(conn != null) try { conn.close(); } catch(SQLException sqle) { }
		}
		return result;
	}

}
