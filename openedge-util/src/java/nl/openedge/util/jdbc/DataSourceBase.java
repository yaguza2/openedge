/*
 * $Id$
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

import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author Eelco Hillenius
 */
public abstract class DataSourceBase
{

	private static boolean initialized = false;

	/** datasource */
	protected static DataSource _dataSource = null;

	/**
	 * construct and get datasource from JNDI location
	 * @param jndiRef
	 * @throws Exception
	 */
	public DataSourceBase(String jndiRef) throws Exception
	{

		Context ctx = new InitialContext();
		_dataSource = (DataSource)ctx.lookup(jndiRef);
	}

	/**
	 * construct using given datasource
	 * @param dataSource
	 */
	public DataSourceBase(DataSource dataSource) throws Exception
	{
		_dataSource = dataSource;
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
	public DataSourceBase(Map constructionParameters) throws Exception
	{

		_dataSource = new BasicDataSource();
		BeanUtils.populate(_dataSource, constructionParameters);
		initialized = true;

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
	 * @param createOnce if true, we'll look if a datasource was allready 
	 * 		initialized which can be used in that case 
	 */
	public DataSourceBase(Map constructionParameters, boolean createOnce) 
		throws Exception
	{

		if (!initialized && createOnce)
		{
			_dataSource = new BasicDataSource();
			BeanUtils.populate(_dataSource, constructionParameters);
			initialized = true;
		}

	}

	/**
	 * @return DataSource
	 */
	public DataSource getDataSource()
	{
		return _dataSource;
	}

	/**
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		_dataSource = dataSource;
	}

}
