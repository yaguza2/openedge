/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package nl.openedge.maverick.framework.util;

import org.apache.velocity.tools.view.servlet.ServletToolInfo;

/**
 * @author Eelco Hillenius
 */
public class ServletBeanToolInfo extends ServletToolInfo
{
	
	/**
	 * Creates a new tool of the specified class with the given key and scope.
	 */
	public ServletBeanToolInfo(String key, String classname, String scope)
		throws Exception
	{
		super(key, classname, scope);
	}
	
	/* (non-Javadoc)
	 * @see org.apache.velocity.tools.view.servlet.ServletToolInfo#getScope()
	 */
	public String getScope()
	{
		// TODO Auto-generated method stub
		return super.getScope();
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.tools.view.ToolInfo#getKey()
	 */
	public String getKey()
	{
		// TODO Auto-generated method stub
		return super.getKey();
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.tools.view.ToolInfo#getClassname()
	 */
	public String getClassname()
	{
		// TODO Auto-generated method stub
		return super.getClassname();
	}

	/* (non-Javadoc)
	 * @see org.apache.velocity.tools.view.ToolInfo#getInstance(java.lang.Object)
	 */
	public Object getInstance(Object initData)
	{
		// TODO Auto-generated method stub
		return super.getInstance(initData);
	}

}
