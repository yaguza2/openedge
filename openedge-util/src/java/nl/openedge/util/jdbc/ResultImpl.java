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

import java.sql.*;
import java.util.*;

import javax.servlet.jsp.jstl.sql.Result;

/**
 * @author Eelco Hillenius
 */
public final class ResultImpl implements Result
{

	// TODO: houd rekening met scrollable resultsets

	private List rowMap;
	private List rowByIndex;
	private String[] columnNames;
	private boolean isLimited;

	/**
	 * This constructor reads the ResultSet and saves a cached
	 * copy.
	 *
	 * @param rs an open <tt>ResultSet</tt>, positioned before the first
	 * row
	 * @param startRow, beginning row to be cached
	 * @param maxRows, query maximum rows limit
	 * @exception if a database error occurs
	 */
	public ResultImpl(ResultSet rs, int startRow, int maxRows) 
		throws SQLException
	{
		rowMap = new ArrayList();
		rowByIndex = new ArrayList();

		ResultSetMetaData rsmd = rs.getMetaData();
		int noOfColumns = rsmd.getColumnCount();

		// Create the column name array
		columnNames = new String[noOfColumns];
		for (int i = 1; i <= noOfColumns; i++)
		{
			columnNames[i - 1] = rsmd.getTableName(i) + "." 
				+ rsmd.getColumnName(i);
		}

		// Throw away all rows upto startRow
		for (int i = 0; i < startRow; i++)
		{
			rs.next();
		}

		// Process the remaining rows upto maxRows
		int processedRows = 0;
		while (rs.next())
		{
			if ((maxRows != -1) && (processedRows == maxRows))
			{
				isLimited = true;
				break;
			}
			Object[] columns = new Object[noOfColumns];
			SortedMap columnMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);

			// JDBC uses 1 as the lowest index!
			for (int i = 1; i <= noOfColumns; i++)
			{
				Object value = rs.getObject(i);
				if (rs.wasNull())
				{
					value = null;
				}
				columns[i - 1] = value;
				columnMap.put(columnNames[i - 1], value);
			}
			rowMap.add(columnMap);
			rowByIndex.add(columns);
			processedRows++;
		}
	}

	/**
	 * Returns an array of SortedMap objects. The SortedMap
	 * object key is the ColumnName and the value is the ColumnValue.
	 * SortedMap was created using the CASE_INSENSITIVE_ORDER
	 * Comparator so the key is the case insensitive representation
	 * of the ColumnName.
	 *
	 * @return an array of Map, or null if there are no rows
	 */
	public SortedMap[] getRows()
	{
		if (rowMap == null)
		{
			return null;
		}

		//should just be able to return SortedMap[] object
		return (SortedMap[])rowMap.toArray(new SortedMap[0]);
	}

	/**
	 * Returns an array of Object[] objects. The first index
	 * designates the Row, the second the Column. The array
	 * stores the value at the specified row and column.
	 *
	 * @return an array of Object[], or null if there are no rows
	 */
	public Object[][] getRowsByIndex()
	{
		if (rowByIndex == null)
		{
			return null;
		}

		//should just be able to return Object[][] object
		return (Object[][])rowByIndex.toArray(new Object[0][0]);
	}

	/**
	 * Returns an array of String objects. The array represents
	 * the names of the columns arranged in the same order as in
	 * the getRowsByIndex() method.
	 *
	 * @return an array of String[]
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * Returns the number of rows in the cached ResultSet
	 *
	 * @return the number of cached rows, or -1 if the Result could
	 *    not be initialized due to SQLExceptions
	 */
	public int getRowCount()
	{
		if (rowMap == null)
		{
			return -1;
		}
		return rowMap.size();
	}

	/**
	 * Returns true of the query was limited by a maximum row setting
	 *
	 * @return true if the query was limited by a MaxRows attribute
	 */
	public boolean isLimitedByMaxRows()
	{
		return isLimited;
	}
}
