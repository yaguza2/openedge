/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.maverick.framework.test;

import javax.servlet.ServletException;

import nl.openedge.maverick.framework.FormBeanContext;
import nl.openedge.maverick.framework.interceptors.AfterPerformInterceptor;
import nl.openedge.maverick.framework.interceptors.BeforePerformInterceptor;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * @author Eelco Hillenius
 */
public class TestBeforePerformInterceptor 
	implements BeforePerformInterceptor, AfterPerformInterceptor
{

	private static int beforeCalls = 0;
	private static int afterCalls = 0;

	/**
	 * @see nl.openedge.maverick.framework.interceptors.BeforePerformInterceptor#doBeforePerform(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext)
	 */
	public void doBeforePerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext)
		throws ServletException
	{
		TestBean bean = (TestBean)formBeanContext.getBean();
		if(bean != null && (bean.getTestInteger1() == null)) 
			// form should be created, but not yet populated
		{
			beforeCalls++;	
		}
		else
		{
			throw new RuntimeException("illegal call");
		}
	}

	/**
	 * @see nl.openedge.maverick.framework.interceptors.AfterPerformInterceptor#doAfterPerform(org.infohazard.maverick.flow.ControllerContext, nl.openedge.maverick.framework.FormBeanContext)
	 */
	public void doAfterPerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext)
		throws ServletException
	{
		TestBean bean = (TestBean)formBeanContext.getBean();
		if(bean != null && (bean.getTestInteger1() != null)) 
			// form should be created AND populated
		{
			afterCalls++;	
		}
		else
		{
			throw new RuntimeException("illegal call");
		}
	}

	/**
	 * @return
	 */
	public static int getAfterCalls()
	{
		return afterCalls;
	}

	/**
	 * @return
	 */
	public static int getBeforeCalls()
	{
		return beforeCalls;
	}

}
