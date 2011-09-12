package nl.openedge.util.web;

/**
 * Wraps info about one page.
 * 
 * @author Eelco Hillenius
 */
public class PageWrapper
{

	/** current page number. */
	private int number;

	/** starting row or element. */
	private int startRow;

	/** number of rows for this page. */
	private int numberOfRows;

	/**
	 * Construct.
	 * 
	 * @param number
	 *            page number
	 * @param startRow
	 *            starting row
	 * @param numberOfRows
	 *            number if rows in this page
	 */
	public PageWrapper(int number, int startRow, int numberOfRows)
	{
		this.number = number;
		this.startRow = startRow;
		this.numberOfRows = numberOfRows;
	}

	/**
	 * number of rows on this page.
	 * 
	 * @return int number of rows on this page
	 */
	public int getNumberOfRows()
	{
		return numberOfRows;
	}

	/**
	 * page number.
	 * 
	 * @return int page number
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 * starting row or element.
	 * 
	 * @return int starting row or element
	 */
	public int getStartRow()
	{
		return startRow;
	}

}
