/*
 * $Id: InterceptorDelegate.java,v 1.3 2004-04-25 10:03:19 eelco12 Exp $
 * $Revision: 1.3 $
 * $Date: 2004-04-25 10:03:19 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus;

import java.io.IOException;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.infohazard.maverick.flow.ControllerContext;

import nl.openedge.baritus.interceptors.AfterPerformInterceptor;
import nl.openedge.baritus.interceptors.AfterPopulationInterceptor;
import nl.openedge.baritus.interceptors.BeforeMakeFormBeanInterceptor;
import nl.openedge.baritus.interceptors.BeforePopulationInterceptor;
import nl.openedge.baritus.interceptors.Interceptor;
import nl.openedge.baritus.interceptors.PerformExceptionInterceptor;
import nl.openedge.baritus.interceptors.PopulationErrorInterceptor;
import nl.openedge.baritus.interceptors.flow.AfterPerformFlowInterceptor;
import nl.openedge.baritus.interceptors.flow.AfterPopulationFlowInterceptor;
import nl.openedge.baritus.interceptors.flow.BeforeMakeFormBeanFlowInterceptor;
import nl.openedge.baritus.interceptors.flow.BeforePopulationFlowInterceptor;
import nl.openedge.baritus.interceptors.flow.FlowInterceptorContext;
import nl.openedge.baritus.interceptors.flow.FlowInterceptorResult;
import nl.openedge.baritus.interceptors.flow.PerformExceptionFlowInterceptor;
import nl.openedge.baritus.interceptors.flow.PopulationErrorFlowInterceptor;

/**
 * Delegate for handling interceptor actions.
 * 
 * @author Eelco Hillenius
 */
final class InterceptorDelegate
{
	/* handle to interceptor registry */	
	private InterceptorRegistry interceptorRegistry = null;
	
	private static int LEVEL_BEFORE_MAKE_FORMBEAN = 0;
	private static int LEVEL_BEFORE_POPULATION = 1;
	private static int LEVEL_POPULATION_ERROR = 2;
	private static int LEVEL_AFTER_POPULATION = 3;
	private static int LEVEL_PERFORM_EXCEPTION = 4;
	private static int LEVEL_AFTER_PERFORM = 5;

	/**
	 * Construct the delegate with an instance of the interceptor registry.
	 * 
	 * @param interceptorRegistry the interceptor registry
	 */
	public InterceptorDelegate(InterceptorRegistry interceptorRegistry)
	{
		this.interceptorRegistry = interceptorRegistry;
	}

	//**************************** interceptors ******************************************/
	
	// NOTE: it would be possible to have just two methods instead of the
	// next bulk of methods using introspection. To keep it straightforward to
	// read though, and have a small performance edge I just coded the methods.
	
	
	
	//-------------------------- normal interceptors -----------------------------/
	
	/**
	 * Called before any handling like form population etc.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with unpopulated formBean
	 * @throws ServletException
	 */
	public void doInterceptBeforeMakeFormBean(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			BeforeMakeFormBeanInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((BeforeMakeFormBeanInterceptor)commands[i])
					.doBeforeMakeFormBean(cctx, formBeanContext);
			}
		}
	}
	
	/**
	 * Called before any handling like form population etc. but after makeFormBean.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with unpopulated formBean
	 * @throws ServletException
	 */
	public void doInterceptBeforePopulation(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			BeforePopulationInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((BeforePopulationInterceptor)commands[i])
					.doBeforePopulation(cctx, formBeanContext);
			}
		}
	}
	
	/**
	 * Called if population or validation failed.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with form bean that failed to populate
	 * @throws ServletException
	 */
	public void doInterceptPopulationError(
		ControllerContext cctx, 
		FormBeanContext formBeanContext)
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			PopulationErrorInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((PopulationErrorInterceptor)commands[i])
					.doOnPopulationError(cctx, formBeanContext);
			}
		}
	}
	
	/**
	 * Called after population but before executing the command method.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with unpopulated formBean
	 * @throws ServletException
	 */
	public void doInterceptAfterPopulation(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			AfterPopulationInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((AfterPopulationInterceptor)commands[i])
					.doAfterPopulation(cctx, formBeanContext);
			}
		}
	}
	
	/**
	 * Called when an unhandled exception occured during the execution of the command method.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with populated (if succesful) formBean
	 * @throws ServletException
	 */
	public void doInterceptPerformException(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			PerformExceptionInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((PerformExceptionInterceptor)commands[i])
					.doOnPerformException(cctx, formBeanContext);
			}
		}	
	}
	
	/**
	 * Called after the command method is executed.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with populated (if succesful) formBean
	 * @throws ServletException
	 */
	public void doInterceptAfterPerform(
		ControllerContext cctx,
		FormBeanContext formBeanContext) 
		throws ServletException
	{
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			AfterPerformInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				((AfterPerformInterceptor)commands[i])
					.doAfterPerform(cctx, formBeanContext);
			}
		}	
	}
	
	// -------------------------- flow interceptors -----------------------------/
	
	/**
	 * Called before any handling like form population etc.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptBeforeMakeFormBean(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			BeforeMakeFormBeanFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((BeforeMakeFormBeanFlowInterceptor)commands[i])
							.doBeforeMakeFormBean(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_BEFORE_MAKE_FORMBEAN);
	}
	
	/**
	 * Called before any handling like form population etc. but after makeFormBean.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptBeforePopulation(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			BeforePopulationFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((BeforePopulationFlowInterceptor)commands[i])
							.doBeforePopulation(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_BEFORE_POPULATION);
	}
	
	/**
	 * Called if population or validation failed.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptPopulationError(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			PopulationErrorFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((PopulationErrorFlowInterceptor)commands[i])
					.doOnPopulationException(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_POPULATION_ERROR);
	}
	
	/**
	 * Called after population but before executing the command method.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptAfterPopulation(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			AfterPopulationFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((AfterPopulationFlowInterceptor)commands[i])
							.doAfterPopulation(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_AFTER_POPULATION);
	}
	
	/**
	 * Called when an unhandled exception occured during the execution of the command method.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptPerformException(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			PerformExceptionFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((PerformExceptionFlowInterceptor)commands[i])
					.doOnPerformException(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_PERFORM_EXCEPTION);
	}
	
	/**
	 * Called after the command method is executed.
	 * 
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptAfterPerform(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		FlowInterceptorResult result = null;
		Interceptor[] commands = interceptorRegistry.getInterceptors(
			AfterPerformFlowInterceptor.class);
		if(commands != null)
		{
			int nbrcmds = commands.length;
			for(int i = 0; i < nbrcmds; i++)
			{
				result = ((AfterPerformFlowInterceptor)commands[i])
					.doAfterPerform(ctx);
				if((result != null) && (i < nbrcmds))
				{
					addToResultStack(ctx, result);
				}
			}
		}
		return extractViewFromResult(ctx, result, LEVEL_AFTER_PERFORM);
	}

	
	/*
	 * Get the view to immediately show, or null if execution should
	 * 	follow the default path. If the action in the result object
	 * 	is ACTION_DISPATCH, this method will execute the dispatch immediately
	 * @param ctx
	 * @param result
	 * @param level
	 * @return String 
	 * @throws ServletException
	 */
	private String extractViewFromResult(
		FlowInterceptorContext ctx, 
		FlowInterceptorResult result,
		int level)
		throws ServletException
	{
		String view = null;
		if(result != null)
		{
			if(result.getAction() == FlowInterceptorResult.ACTION_SHOW_VIEW)
			{
				view = result.getView();
			}
			else if(result.getAction() == FlowInterceptorResult.ACTION_DISPATCH)
			{
				// dispatch request
				try
				{
					HttpServletRequest request = ctx.getCctx().getRequest(); 
					String directView = result.getView(); 
					RequestDispatcher disp = request.getRequestDispatcher(directView);  
					disp.forward(request, ctx.getCctx().getResponse());	
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new ServletException(e);
				}			
			}
		}
		return view;
	}
	
	/* if result.isExecuteOtherNonFlowInterceptors, handle some other interceptors */
	private void possiblyHandleOtherInterceptors(
		FlowInterceptorContext ctx, 
		FlowInterceptorResult result,
		int level)
		throws ServletException
	{
		if(result.isExecuteOtherNonFlowInterceptors())
		{
			if(level == LEVEL_BEFORE_MAKE_FORMBEAN)
			{
				doInterceptBeforePopulation(ctx.getCctx(), ctx.getFormBeanContext());
			}
			if(level != LEVEL_AFTER_PERFORM)
			{
				doInterceptAfterPerform(ctx.getCctx(), ctx.getFormBeanContext());
			}
		}
	}
	
	/* add the result to the resultstack in the context, create stack if not yet created */
	private void addToResultStack(
		FlowInterceptorContext ctx, 
		FlowInterceptorResult result)
	{
		Stack resultStack = ctx.getResultStack();
		if(resultStack == null)
		{
			resultStack = new Stack();
			ctx.setResultStack(resultStack);
		}
		resultStack.push(result);		
	}

}
