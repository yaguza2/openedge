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

import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author Eelco Hillenius
 */
public class DataSourceBase
{

	/** datasource */
	protected DataSource _dataSource = null;
	protected static Map _instances = new HashMap(4);

	/**
	 * get datasource from JNDI location
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
	 * create datasource with given parameters
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
	
	/**
	 * get datasource with given jndiRef
	 * @param jndiRef
	 * @return DataSource
	 * @throws Exception
	 */
	public static DataSource getDataSource(String jndiRef) throws Exception
	{
		DataSource ds = null;
		DataSourceBase base = null;
		synchronized(DataSourceBase.class)
		{
			base = (DataSourceBase)_instances.get(jndiRef);
			if(base == null)
			{
				base = new DataSourceBase(jndiRef);
				_instances.put(jndiRef, base);
			}	
		}
		ds = base.getDataSource();
		return ds;
	}
	
	/**
	 * get datasource with given parameters
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
	 * @return DataSource
	 * @throws Exception
	 */
	public static DataSource getDataSource(Map constructionParameters) throws Exception
	{
		DataSource ds = null;
		DataSourceBase base = null;
		synchronized(DataSourceBase.class)
		{
			base = (DataSourceBase)_instances.get(constructionParameters);
			if(base == null)
			{
				base = new DataSourceBase(constructionParameters);
				_instances.put(constructionParameters, base);
			}	
		}
		ds = base.getDataSource();
		return ds;
	}

}
