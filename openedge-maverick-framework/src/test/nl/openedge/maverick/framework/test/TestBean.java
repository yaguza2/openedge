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
 
package nl.openedge.maverick.framework.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eelco Hillenius
 */
public class TestBean
{
	private Integer testInteger1;
	private Integer testInteger2;
	
	private Long testLong1;
	private Long testLong2;
	
	private Double testDouble1;
	private Double testDouble2;
	
	private Date testDate1;
	private Date testDate2;
	
	private String[] testStringArray1 = null;
	
	private String[] testStringArray2 = new String[] 
	{
		"existing0", "existing1"	
	};
	
	private Map testMap = new HashMap();

	/**
	 * @return
	 */
	public Integer getTestInteger1()
	{
		return testInteger1;
	}

	/**
	 * @param integer
	 */
	public void setTestInteger1(Integer integer)
	{
		testInteger1 = integer;
	}

	/**
	 * @return
	 */
	public Integer getTestInteger2()
	{
		return testInteger2;
	}

	/**
	 * @param integer
	 */
	public void setTestInteger2(Integer integer)
	{
		testInteger2 = integer;
	}

	/**
	 * @return
	 */
	public Date getTestDate1()
	{
		return testDate1;
	}

	/**
	 * @return
	 */
	public Date getTestDate2()
	{
		return testDate2;
	}

	/**
	 * @return
	 */
	public Double getTestDouble1()
	{
		return testDouble1;
	}

	/**
	 * @return
	 */
	public Double getTestDouble2()
	{
		return testDouble2;
	}

	/**
	 * @return
	 */
	public Long getTestLong1()
	{
		return testLong1;
	}

	/**
	 * @return
	 */
	public Long getTestLong2()
	{
		return testLong2;
	}

	/**
	 * @param date
	 */
	public void setTestDate1(Date date)
	{
		testDate1 = date;
	}

	/**
	 * @param date
	 */
	public void setTestDate2(Date date)
	{
		testDate2 = date;
	}

	/**
	 * @param double1
	 */
	public void setTestDouble1(Double double1)
	{
		testDouble1 = double1;
	}

	/**
	 * @param double1
	 */
	public void setTestDouble2(Double double1)
	{
		testDouble2 = double1;
	}

	/**
	 * @param long1
	 */
	public void setTestLong1(Long long1)
	{
		testLong1 = long1;
	}

	/**
	 * @param long1
	 */
	public void setTestLong2(Long long1)
	{
		testLong2 = long1;
	}

	/**
	 * @return
	 */
	public String[] getTestStringArray1()
	{
		return testStringArray1;
	}

	/**
	 * @param strings
	 */
	public void setTestStringArray1(String[] strings)
	{
		testStringArray1 = strings;
	}

	/**
	 * @return
	 */
	public String[] getTestStringArray2()
	{
		return testStringArray2;
	}

	/**
	 * @param strings
	 */
	public void setTestStringArray2(String[] strings)
	{
		testStringArray2 = strings;
	}

	/**
	 * @return
	 */
	public Map getTestMap()
	{
		return testMap;
	}

	/**
	 * @param map
	 */
	public void setTestMap(Map map)
	{
		testMap = map;
	}

}
