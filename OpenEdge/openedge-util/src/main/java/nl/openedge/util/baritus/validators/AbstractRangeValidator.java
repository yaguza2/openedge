package nl.openedge.util.baritus.validators;

import nl.openedge.baritus.validation.AbstractFieldValidator;

/**
 * Abstracte class for validators that perform range checks.
 * 
 * @author Peter Veenendaal (levob)
 * @author Eelco Hillenius
 */
public abstract class AbstractRangeValidator extends AbstractFieldValidator
{

	/**
	 * whether the lower border should be included.
	 */
	private boolean includingBottom = false;

	/**
	 * wheter the upper border should be included.
	 */
	private boolean includingTop = false;

	/**
	 * bottom value of range.
	 */
	private Object bottom;

	/**
	 * top value of range.
	 */
	private Object top;

	/**
	 * construct with bottom and top of range.
	 * 
	 * @param theBottom
	 *            bottom of range
	 * @param theTop
	 *            top of range
	 */
	public AbstractRangeValidator(Object theBottom, Object theTop)
	{
		super();
		this.bottom = theBottom;
		this.top = theTop;
	}

	/**
	 * construct with bottom and top, and including properties.
	 * 
	 * @param theBottom
	 *            bottom of range
	 * @param theTop
	 *            top of range
	 * @param inclBottom
	 *            whether to include the bottom
	 * @param inclTop
	 *            whether to include the top
	 */
	public AbstractRangeValidator(Object theBottom, Object theTop, boolean inclBottom,
			boolean inclTop)
	{
		this.bottom = theBottom;
		this.top = theTop;
		this.includingTop = inclTop;
		this.includingBottom = inclBottom;
	}

	/**
	 * whether top is included.
	 * 
	 * @return boolean whether top is included.
	 */
	public boolean isIncludingTop()
	{
		return includingTop;
	}

	/**
	 * whether bottom is included.
	 * 
	 * @return boolean whether bottom is included.
	 */
	public boolean isIncludingBottom()
	{
		return includingBottom;
	}

	/**
	 * set whether top is included.
	 * 
	 * @param b
	 *            whether top is included.
	 */
	public void setIncludingTop(boolean b)
	{
		includingTop = b;
	}

	/**
	 * Set whether bottom is included.
	 * 
	 * @param b
	 *            whether bottom is included.
	 */
	public void setIncludingBottom(boolean b)
	{
		includingBottom = b;
	}

	/**
	 * Get top.
	 * 
	 * @return Object top
	 */
	public Object getTop()
	{
		return top;
	}

	/**
	 * Get bottom.
	 * 
	 * @return Object bottom
	 */
	public Object getBottom()
	{
		return bottom;
	}

	/**
	 * Set top.
	 * 
	 * @param object
	 *            top
	 */
	public void setTop(Object object)
	{
		top = object;
	}

	/**
	 * Set bottom.
	 * 
	 * @param object
	 *            bottom
	 */
	public void setBottom(Object object)
	{
		bottom = object;
	}
}
