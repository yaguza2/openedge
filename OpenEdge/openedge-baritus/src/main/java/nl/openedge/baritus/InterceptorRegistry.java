package nl.openedge.baritus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.openedge.baritus.interceptors.Interceptor;

/**
 * Registry for interceptors. Each instance of FormBeanBase has its own instance.
 * 
 * @author Eelco Hillenius
 */
final class InterceptorRegistry
{
	private List<Interceptor> interceptors = null;

	private List<Interceptor>[] flowInterceptors;

	/**
	 * add an interceptor to the current list of interceptors
	 * 
	 * @param interceptor
	 *            the interceptor to add to the current list of interceptors
	 */
	public void addInterceptor(Interceptor interceptor)
	{
		if (interceptors == null)
			interceptors = new ArrayList<Interceptor>();
		interceptors.add(interceptor);
	}

	/**
	 * add an interceptor to the current list of interceptors at the specified position
	 * 
	 * @param index
	 *            index position where to insert the interceptor
	 * @param interceptor
	 *            the interceptor to add to the current list of interceptors
	 */
	public void addInterceptor(int index, Interceptor interceptor)
	{
		if (interceptors == null)
			interceptors = new ArrayList<Interceptor>();
		interceptors.add(index, interceptor);
	}

	/**
	 * remove an interceptor from the current list of interceptors
	 * 
	 * @param interceptor
	 *            the interceptor to remove from the current list of interceptors
	 */
	public void removeInterceptor(Interceptor interceptor)
	{
		if (interceptors != null)
		{
			interceptors.remove(interceptor);
			if (interceptors.isEmpty())
				interceptors = null;
		}
	}

	/**
	 * get all registered interceptors of the provided type
	 * 
	 * @return array of Interceptors or null if none
	 */
	public Interceptor[] getInterceptors(Class< ? extends Interceptor> type)
	{
		Interceptor[] result = null;
		if (interceptors != null && (!interceptors.isEmpty()))
		{
			List<Interceptor> temp = new ArrayList<Interceptor>();
			for (Iterator<Interceptor> i = interceptors.listIterator(); i.hasNext();)
			{
				Interceptor intc = i.next();
				if (type.isAssignableFrom(intc.getClass()))
				{
					temp.add(intc);
				}
			}
			if (!temp.isEmpty())
			{
				result = temp.toArray(new Interceptor[temp.size()]);
			}
		}
		return result;
	}

	/**
	 * get the flow interceptors for the provided interceptionPoint
	 * 
	 * @param interceptionPoint
	 *            the interception point
	 * @return List flow interceptors or null if none were registered
	 */
	public List<Interceptor> getInterceptorsAtPoint(int interceptionPoint)
	{
		return (flowInterceptors != null) ? flowInterceptors[interceptionPoint] : null;
	}

}
