package nl.openedge.baritus.interceptors;

/**
 * Tagging interface for interceptors. Clients should use one or more of the specific
 * interfaces to actually intercept on something.
 * 
 * Interceptors provide a means to encapsulate cross-cutting code that is executed on
 * pre-defined points in the line of execution.
 * 
 * Interceptors can be used to decorate the normal execution. Also, by throwing
 * FlowExceptions, interceptors can alter the flow of execution. An interceptor can throw
 * a FlowException if it wants Baritus to stop normal processing and go to the given
 * declared view (using ReturnNowFlowException) such as 'error', or dispatch to an
 * arbitrary - non declared - URL (using DispatchNowFlowException) location.
 * 
 * @author Eelco Hillenius
 */
public interface Interceptor
{

}
