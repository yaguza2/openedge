package nl.openedge.gaps.ui.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for a list of results.
 */
public class ResultList
{
	/** Synchronized list of results. */
	private List results = Collections.synchronizedList(new ArrayList());

	/**
	 * add an object to the result list.
	 * @param o object to add to results
	 */
	public void add(final Object o)
	{
		results.add(0, o);
	}

	/**
	 * Add a List to the current result list.
	 * @param list list to add to results
	 */
	public void addAll(List list)
	{
		results.addAll(0, list);
	}

	/**
	 * Get results.
	 * @return results the result list
	 */
	public List getResults()
	{
		return results;
	}

	/**
	 * Set results.
	 * @param results the result list.
	 */
	public void setResults(List results)
	{
		this.results = results;
	}
}
