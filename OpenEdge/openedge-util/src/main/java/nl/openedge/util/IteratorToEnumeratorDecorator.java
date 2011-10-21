package nl.openedge.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Met deze klasse kun je een iterator wrapper zodat deze voldoet aan de Enumerator
 * interface.
 * 
 * @author Eelco Hillenius
 */
public class IteratorToEnumeratorDecorator<T> implements Enumeration<T>
{

	/**
	 * De gewrapte iterator.
	 */
	private Iterator<T> iterator = null;

	/**
	 * Construct met iterator.
	 * 
	 * @param deIterator
	 *            de iterator die gewrapt dient te worden
	 */
	public IteratorToEnumeratorDecorator(final Iterator<T> deIterator)
	{
		this.iterator = deIterator;
	}

	/**
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	@Override
	public boolean hasMoreElements()
	{
		return iterator.hasNext();
	}

	/**
	 * @see java.util.Enumeration#nextElement()
	 */
	@Override
	public T nextElement()
	{
		return iterator.next();
	}
}
