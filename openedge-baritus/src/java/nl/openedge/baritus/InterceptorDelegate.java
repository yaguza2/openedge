/*
 * $Id: InterceptorDelegate.java,v 1.1.1.1 2004-02-24 20:33:59 eelco12 Exp $
 * $Revision: 1.1.1.1 $
 * $Date: 2004-02-24 20:33:59 $
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
	
	private InterceptorRegistry interceptorRegistry = null;

	/**
	 * construct delegate with instance of interceptor registry
	 * @param interceptorRegistry
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
	
	/*
	 * is called before any handling like form population etc. but after makeFormBean
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
	
	/*
	 * is called before any handling like form population etc. but after makeFormBean
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
	
	/*
	 * is called before any handling like form population etc. but after makeFormBean
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
	
	/*
	 * is called after all handling like form population etc. is done
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
	
	/*
	 * is called after all handling like form population etc. is done
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
	
	/*
	 * prepare error model for view by calling registered interceptors.
	 * This method will be called if the model failed to populate,
	 * or did not pass validation
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
	
	// -------------------------- normal interceptors -----------------------------/
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptBeforeMakeFormBean(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptBeforePopulation(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptAfterPopulation(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptPopulationError(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptAfterPerform(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * execute flow interceptors
	 * @param ctx flow interceptor context
	 * @return String view to immediately show, or null if execution should
	 * 	follow the default path
	 * @throws ServletException
	 */
	public String doFlowInterceptPerformException(
		FlowInterceptorContext ctx) 
		throws ServletException
	{
		String view = null;
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
		return extractViewFromResult(ctx, result);
	}
	
	/*
	 * Get the view to immediately show, or null if execution should
	 * 	follow the default path. If the action in the result object
	 * 	is ACTION_DISPATCH, this method will execute the dispatch immediately
	 * @param ctx
	 * @param result
	 * @return
	 * @throws ServletException
	 */
	private String extractViewFromResult(
		FlowInterceptorContext ctx, 
		FlowInterceptorResult result)
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
