/*
 * $Id: TestBean.java,v 1.7 2004-04-21 11:42:10 eelco12 Exp $
 * $Revision: 1.7 $
 * $Date: 2004-04-21 11:42:10 $
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
package nl.openedge.baritus.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test bean.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public class TestBean
{
	private String testTrimString;
	private String[] testTrimStringArray = new String[] { "" };
	
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
	
	// custom population tests
	private String uppercaseTest = null;
	private String ignore = "unchanged";
	private String ignoreByRegex = "unchanged (regex)";
	
	private String toValidate1;
	private String[] toValidate2 = new String[] 
	{
		"", ""
	};
	private String[] toValidate3 = new String[] 
	{
		"", ""
	};
	private String toValidate4 = "validValue";
	
	private List listProperty = new ArrayList();
	
	private List multiDimensionalList = new ArrayList();
	
	private Map multiDimensionalMap = new HashMap();
	
	private TestObject testObject = null;


	/**
	 * construct
	 */
	public TestBean()
	{
		multiDimensionalMap.put("one", new HashMap());
		multiDimensionalMap.put("two", new HashMap());
		
		List lOne0 = new ArrayList();
		List lOne1 = new ArrayList();
		List lOne2 = new ArrayList();
		List lOne = new ArrayList();
		lOne.add(lOne0);
		lOne.add(lOne1);
		lOne.add(lOne2);
		multiDimensionalList.add(lOne);
		
		List lTwo0 = new ArrayList();
		List lTwo = new ArrayList();
		lTwo.add(lTwo);
		multiDimensionalList.add(lTwo);
	}


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

	/**
	 * @return
	 */
	public String getUppercaseTest()
	{
		return uppercaseTest;
	}

	/**
	 * @param string
	 */
	public void setUppercaseTest(String string)
	{
		uppercaseTest = string;
	}

	/**
	 * @return
	 */
	public String getIgnore()
	{
		return ignore;
	}

	/**
	 * @param string
	 */
	public void setIgnore(String string)
	{
		ignore = string;
	}

	/**
	 * @return
	 */
	public String getIgnoreByRegex()
	{
		return ignoreByRegex;
	}

	/**
	 * @param string
	 */
	public void setIgnoreByRegex(String string)
	{
		ignoreByRegex = string;
	}

	/**
	 * @return String
	 */
	public String getToValidate1()
	{
		return toValidate1;
	}

	/**
	 * @param string
	 */
	public void setToValidate1(String string)
	{
		toValidate1 = string;
	}

	/**
	 * @return
	 */
	public String[] getToValidate2()
	{
		return toValidate2;
	}

	/**
	 * @param strings
	 */
	public void setToValidate2(String[] strings)
	{
		toValidate2 = strings;
	}

	/**
	 * @return
	 */
	public String[] getToValidate3()
	{
		return toValidate3;
	}

	/**
	 * @param strings
	 */
	public void setToValidate3(String[] strings)
	{
		toValidate3 = strings;
	}
	
	/**
	 * @return Returns the listProperty.
	 */
	public java.util.List getListProperty()
	{
		return listProperty;
	}
	/**
	 * @param listProperty The listProperty to set.
	 */
	public void setListProperty(java.util.List listProperty)
	{
		this.listProperty = listProperty;
	}
	
	public void setListProperty(int index, Object element)
	{
		listProperty.add(index, element);
	}
	
	public Object getListProperty(int index)
	{
		return listProperty.get(index);
	}
	/**
	 * @return
	 */
	public List getMultiDimensionalList()
	{
		return multiDimensionalList;
	}

	/**
	 * @param list
	 */
	public void setMultiDimensionalList(List list)
	{
		multiDimensionalList = list;
	}

	/**
	 * @return
	 */
	public Map getMultiDimensionalMap()
	{
		return multiDimensionalMap;
	}

	/**
	 * @param map
	 */
	public void setMultiDimensionalMap(Map map)
	{
		multiDimensionalMap = map;
	}

	/**
	 * @return
	 */
	public TestObject getTestObject()
	{
		return testObject;
	}

	/**
	 * @param object
	 */
	public void setTestObject(TestObject object)
	{
		testObject = object;
	}

	/**
	 * @return
	 */
	public String getToValidate4()
	{
		return toValidate4;
	}

	/**
	 * @param string
	 */
	public void setToValidate4(String string)
	{
		toValidate4 = string;
	}

	/**
	 * @return
	 */
	public String getTestTrimString()
	{
		return testTrimString;
	}

	/**
	 * @param string
	 */
	public void setTestTrimString(String string)
	{
		testTrimString = string;
	}

	/**
	 * @return
	 */
	public String[] getTestTrimStringArray()
	{
		return testTrimStringArray;
	}

	/**
	 * @param strings
	 */
	public void setTestTrimStringArray(String[] strings)
	{
		testTrimStringArray = strings;
	}

}
