/*
 * $Id: InterceptorDelegate.java,v 1.5 2004-06-22 17:57:24 eelco12 Exp $
 * $Revision: 1.5 $
 * $Date: 2004-06-22 17:57:24 $
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.baritus;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import nl.openedge.baritus.interceptors.AfterPerformInterceptor;
import nl.openedge.baritus.interceptors.AfterPopulationInterceptor;
import nl.openedge.baritus.interceptors.BeforeMakeFormBeanInterceptor;
import nl.openedge.baritus.interceptors.BeforePopulationInterceptor;
import nl.openedge.baritus.interceptors.DispatchNowFlowException;
import nl.openedge.baritus.interceptors.FlowException;
import nl.openedge.baritus.interceptors.Interceptor;
import nl.openedge.baritus.interceptors.PerformExceptionInterceptor;
import nl.openedge.baritus.interceptors.PopulationErrorInterceptor;
import nl.openedge.baritus.interceptors.ReturnNowFlowException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infohazard.maverick.flow.ControllerContext;

/**
 * Delegate for handling interceptor actions.
 * 
 * @author Eelco Hillenius
 */
final class InterceptorDelegate
{
	/* handle to interceptor registry */	
	private InterceptorRegistry interceptorRegistry = null;
    
    /* interception logger */
    private static Log intercLog = LogFactory.getLog(LogConstants.INTERCEPTION_LOG);
	
	private final static int LEVEL_BEFORE_MAKE_FORMBEAN = 0;
	private final static int LEVEL_BEFORE_POPULATION = 1;
	private final static int LEVEL_POPULATION_ERROR = 2;
	private final static int LEVEL_AFTER_POPULATION = 3;
	private final static int LEVEL_PERFORM_EXCEPTION = 4;
	private final static int LEVEL_AFTER_PERFORM = 5;

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
	
	
	
	//-------------------------- interceptors -----------------------------/
	
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
        throws ServletException, FlowException
	{
        internalDoInterceptBeforeMakeFormBean(cctx, formBeanContext, true);
	}
    
    private void internalDoInterceptBeforeMakeFormBean(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        boolean handleFlowExceptions) 
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            BeforeMakeFormBeanInterceptor.class);

        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_BEFORE_MAKE_FORMBEAN, null);
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
        throws ServletException, FlowException
	{
        internalDoInterceptBeforePopulation(cctx, formBeanContext, true);
	}
    
    public void internalDoInterceptBeforePopulation(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        boolean handleFlowExceptions) 
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            BeforePopulationInterceptor.class);

        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_BEFORE_POPULATION, null);
    }
	
	/**
	 * Called if population or validation failed.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with form bean that failed to populate
     * @param cause possibly the cause of the population error
	 * @throws ServletException
	 */
	public void doInterceptPopulationError(
		ControllerContext cctx, 
		FormBeanContext formBeanContext,
        Exception cause)
        throws ServletException, FlowException
	{
        internalDoInterceptPopulationError(cctx, formBeanContext, cause, true);
	}
    
    public void internalDoInterceptPopulationError(
        ControllerContext cctx, 
        FormBeanContext formBeanContext,
        Exception cause,
        boolean handleFlowExceptions)
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            PopulationErrorInterceptor.class);
            
        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_POPULATION_ERROR, cause);
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
        throws ServletException, FlowException
	{
        internalDoInterceptAfterPopulation(cctx, formBeanContext, true);
	}
    
    public void internalDoInterceptAfterPopulation(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        boolean handleFlowExceptions) 
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            AfterPopulationInterceptor.class);
            
        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_AFTER_POPULATION, null);
    }
	
	/**
	 * Called when an unhandled exception occured during the execution of the command method.
	 * 
	 * @param cctx maverick context
	 * @param formBeanContext context with populated (if succesful) formBean
     * @param cause the exception that occured during perform
	 * @throws ServletException
	 */
	public void doInterceptPerformException(
		ControllerContext cctx,
		FormBeanContext formBeanContext,
        Exception cause) 
        throws ServletException, FlowException
	{
        internalDoInterceptPerformException(cctx, formBeanContext, cause, true);
	}
    
    public void internalDoInterceptPerformException(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        Exception cause,
        boolean handleFlowExceptions) 
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            PerformExceptionInterceptor.class);
            
        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_PERFORM_EXCEPTION, cause);  
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
        throws ServletException, FlowException
	{
        internalDoInterceptAfterPerform(cctx, formBeanContext, true);
	}
    
    public void internalDoInterceptAfterPerform(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        boolean handleFlowExceptions) 
        throws ServletException, FlowException
    {
        Interceptor[] commands = interceptorRegistry.getInterceptors(
            AfterPerformInterceptor.class);
            
        internalDoExecute(commands, 0, cctx, formBeanContext, 
            handleFlowExceptions, LEVEL_AFTER_PERFORM, null);  
    }

    private void internalDoExecute(
        Interceptor[] commands,
        int fromPos,
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        boolean handleFlowExceptions,
        int level,
        Exception exception)
        throws ServletException, FlowException
    {
        if(commands == null) return;
        
        int nbrcmds = commands.length;
        if(fromPos >= nbrcmds) return;

        for(int i = fromPos; i < nbrcmds; i++)
        {
            if(intercLog.isDebugEnabled())
            {
                intercLog.debug("calling interceptor " + commands[i]);
            }
            try
            {
                switch(level) // just a bit more efficient than instanceof, and we need level anyway
                {
                    case LEVEL_BEFORE_MAKE_FORMBEAN: 
                    ((BeforeMakeFormBeanInterceptor)commands[i])
                            .doBeforeMakeFormBean(cctx, formBeanContext);
                        break;
                    case LEVEL_BEFORE_POPULATION: 
                        ((BeforePopulationInterceptor)commands[i])
                            .doBeforePopulation(cctx, formBeanContext);
                        break;
                    case LEVEL_POPULATION_ERROR: 
                        ((PopulationErrorInterceptor)commands[i])
                            .doOnPopulationError(cctx, formBeanContext, exception);
                        break;
                    case LEVEL_AFTER_POPULATION: 
                        ((AfterPopulationInterceptor)commands[i])
                            .doAfterPopulation(cctx, formBeanContext);
                        break;
                    case LEVEL_PERFORM_EXCEPTION: 
                        ((PerformExceptionInterceptor)commands[i])
                            .doOnPerformException(cctx, formBeanContext, exception);
                        break;
                    case LEVEL_AFTER_PERFORM: 
                        ((AfterPerformInterceptor)commands[i])
                            .doAfterPerform(cctx, formBeanContext);
                        break;
                    
                    default: throw new ServletException("invalid interception level " + level);
                }
                
            }
            catch (FlowException e)
            {
                if(intercLog.isDebugEnabled())
                {
                    intercLog.debug(e + " was thrown by interceptor " + commands[i]); 
                }
                if(handleFlowExceptions) 
                {
                    handleFlowException(
                        cctx, formBeanContext, e, level, commands, i); 
                }
                throw e;
            }
        }       
    }
    
    /*
     * Do handling of flow exceptions
     */
	private void handleFlowException(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
		FlowException flowException,
		int level,        
        Interceptor[] currentCommandStack, 
        int currentCommandStackPos)
        throws ServletException
	{
		String view = null;
		if(flowException != null)
		{
			if(flowException instanceof ReturnNowFlowException)
			{
                ReturnNowFlowException e = (ReturnNowFlowException)flowException;
				view = e.getView();
				if(intercLog.isDebugEnabled())
				{
				    intercLog.debug("returning view " + view);
				}
			}
			else if(flowException instanceof DispatchNowFlowException)
			{
                DispatchNowFlowException e = (DispatchNowFlowException)flowException;
				// dispatch request
				try
				{
					HttpServletRequest request = cctx.getRequest(); 
					String dispatchPath = e.getDispatchPath(); 
					if(intercLog.isDebugEnabled())
					{
					    intercLog.debug("trying dispatch to " + dispatchPath);
					}
					RequestDispatcher disp = request.getRequestDispatcher(dispatchPath);
                    if(disp == null)
                    {
                        String msg = "dispatcher not found for path " + dispatchPath;
                        ServletException ex = new ServletException(msg);
                        intercLog.error(msg, ex);
                        throw ex;  
                    }
					disp.forward(request, cctx.getResponse());	
				}
				catch (IOException ex)
				{
                    intercLog.error(ex.getMessage(), ex);
					throw new ServletException(e);
				}	
			}
		}
        
        possiblyHandleOtherInterceptors(
            cctx, formBeanContext, flowException, level, currentCommandStack, currentCommandStackPos);
	}
	
	/* if result.isExecuteOtherNonFlowInterceptors, handle some other interceptors */
	private void possiblyHandleOtherInterceptors(
        ControllerContext cctx,
        FormBeanContext formBeanContext,
        FlowException flowException,
        int level,
        Interceptor[] currentCommandStack, 
        int currentCommandStackPos)
        throws ServletException
	{
		if(flowException.isExecuteOtherInterceptors())
		{
			if(intercLog.isDebugEnabled())
			{
			    intercLog.debug("handling other interceptors before handling flowexception...");
			}
            // handle current level
			try
			{
				internalDoExecute(currentCommandStack, (currentCommandStackPos + 1), 
				    cctx, formBeanContext, false, level, null);
			}
			catch (FlowException e)
			{
				// never occurs
                intercLog.error(e.getMessage(), e); // but... just in case
			}
            
			if(level == LEVEL_BEFORE_MAKE_FORMBEAN)
			{
				try
				{
					internalDoInterceptBeforePopulation(cctx, formBeanContext, false);
				}
				catch (Exception e)
				{
                    intercLog.error(e.getMessage(), e);
                    // ignore rest
				}
			}
			if(level != LEVEL_AFTER_PERFORM)
			{	
                try
                {
                    internalDoInterceptAfterPerform(cctx, formBeanContext, false);
                }
                catch (Exception e)
                {
                    intercLog.error(e.getMessage(), e);
                    // ignore rest
                }
			}
		}
	}

}
