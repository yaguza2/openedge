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
 * Helper class for creating pagelists
 * 
 * @author Eelco Hillenius
 */
public final class PageListWrapper
{

	/* rowcount for table */
	private int rowCount;

	/* current position */
	private int currentRow;

	/* how many rows should fit one page */
	private int rowsEachPage;

	/* total pages */
	private int totalNbrOfPages;

	/* page number current */
	private int currentPagePosition;

	/* pagewrappers before current page */
	private List beforeCurrentPages;

	/* pagewrappers after current page */
	private List afterCurrentPages;

	/**
	 * construct wrapper
	 * 
	 * @param rowCount
	 * @param currentRow
	 * @param rowsEachPage
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
				beforeCurrentPages = new ArrayList(currentPagePosition);
				for (int i = 1; i < currentPagePosition; i++)
				{
					beforeCurrentPages.add(new PageWrapper(i, rowCounter, rowsEachPage));
					rowCounter += rowsEachPage;
				}
			}
			if (currentPagePosition < totalNbrOfPages)
			{
				afterCurrentPages = new ArrayList(totalNbrOfPages - currentPagePosition);
				for (int i = currentPagePosition + 1; i <= totalNbrOfPages; i++)
				{
					rowCounter += rowsEachPage;
					afterCurrentPages.add(new PageWrapper(i, rowCounter, rowsEachPage));
				}
			}
		}
	}

	/**
	 * @return int
	 */
	public int getCurrentPagePosition()
	{
		return currentPagePosition;
	}

	/**
	 * @return int
	 */
	public int getCurrentRow()
	{
		return currentRow;
	}

	/**
	 * @return int
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * @return int
	 */
	public int getRowsEachPage()
	{
		return rowsEachPage;
	}

	/**
	 * @return int
	 */
	public int getTotalNbrOfPages()
	{
		return totalNbrOfPages;
	}

	/**
	 * @return List
	 */
	public List getAfterCurrentPages()
	{
		return afterCurrentPages;
	}

	/**
	 * @return List
	 */
	public List getBeforeCurrentPages()
	{
		return beforeCurrentPages;
	}

}