/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.openedge.util.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for creating pagelists.
 * 
 * @author Eelco Hillenius
 */
public final class PageListWrapper
{
	/** rowcount for table. */
	private int rowCount;

	/** current position. */
	private int currentRow;

	/** how many rows should fit one page. */
	private int rowsEachPage;

	/** total pages. */
	private int totalNbrOfPages;

	/** page number current. */
	private int currentPagePosition;

	/** pagewrappers before current page. */
	private List<PageWrapper> beforeCurrentPages;

	/** pagewrappers after current page. */
	private List<PageWrapper> afterCurrentPages;

	/**
	 * construct wrapper.
	 * 
	 * @param rowCount
	 *            the row count
	 * @param currentRow
	 *            the current row
	 * @param rowsEachPage
	 *            rows per page
	 */
	public PageListWrapper(int rowCount, int currentRow, int rowsEachPage)
	{
		this.rowCount = rowCount;
		this.currentRow = currentRow;
		this.rowsEachPage = rowsEachPage;

		totalNbrOfPages = Math.round(rowCount / rowsEachPage) + 1;
		currentPagePosition = Math.round(currentRow / rowsEachPage) + 1;

		if (totalNbrOfPages > 1)
		{
			int rowCounter = 0;
			if (currentPagePosition > 1)
			{
				beforeCurrentPages = new ArrayList<PageWrapper>(currentPagePosition);
				for (int i = 1; i < currentPagePosition; i++)
				{
					beforeCurrentPages.add(new PageWrapper(i, rowCounter, rowsEachPage));
					rowCounter += rowsEachPage;
				}
			}
			if (currentPagePosition < totalNbrOfPages)
			{
				afterCurrentPages = new ArrayList<PageWrapper>(totalNbrOfPages - currentPagePosition);
				for (int i = currentPagePosition + 1; i <= totalNbrOfPages; i++)
				{
					rowCounter += rowsEachPage;
					afterCurrentPages.add(new PageWrapper(i, rowCounter, rowsEachPage));
				}
			}
		}
	}

	/**
	 * Get the current page position.
	 * 
	 * @return int the current page position
	 */
	public int getCurrentPagePosition()
	{
		return currentPagePosition;
	}

	/**
	 * Get the current row.
	 * 
	 * @return int the current row
	 */
	public int getCurrentRow()
	{
		return currentRow;
	}

	/**
	 * Get row count.
	 * 
	 * @return int the row count.
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * Get number of rows a page.
	 * 
	 * @return int number of rows a page
	 */
	public int getRowsEachPage()
	{
		return rowsEachPage;
	}

	/**
	 * Get number of pages.
	 * 
	 * @return int number of pages
	 */
	public int getTotalNbrOfPages()
	{
		return totalNbrOfPages;
	}

	/**
	 * Get pages after the current page.
	 * 
	 * @return List pages after the current page
	 */
	public List<PageWrapper> getAfterCurrentPages()
	{
		return afterCurrentPages;
	}

	/**
	 * Get pages before the current page.
	 * 
	 * @return List pages before the current page
	 */
	public List<PageWrapper> getBeforeCurrentPages()
	{
		return beforeCurrentPages;
	}

}
