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
@SuppressWarnings("all")
public class MockBean
{
	private String testTrimString;

	private String[] testTrimStringArray = new String[] {""};

	private Integer testInteger1;

	private Integer testInteger2;

	private Long testLong1;

	private Long testLong2;

	private Double testDouble1;

	private Double testDouble2;

	private Date testDate1;

	private Date testDate2;

	private String[] testStringArray1 = null;

	private String[] testStringArray2 = new String[] {"existing0", "existing1"};

	private Integer[] testIntegerArray1 = new Integer[] {new Integer(-1), new Integer(-1)};

	private Map testMap = new HashMap();

	// custom population tests
	private String uppercaseTest = null;

	private String ignore = "unchanged";

	private String ignoreByRegex = "unchanged (regex)";

	private String toValidate1;

	private String[] toValidate2 = new String[] {"", ""};

	private String[] toValidate3 = new String[] {"", ""};

	private String toValidate4 = "validValue";

	private List<Object> listProperty = new ArrayList<Object>();

	private List<List<List>> multiDimensionalList = new ArrayList<List<List>>();

	private Map multiDimensionalMap = new HashMap();

	private MockObject testObject = null;

	/**
	 * construct
	 */
	public MockBean()
	{
		multiDimensionalMap.put("one", new HashMap());
		multiDimensionalMap.put("two", new HashMap());

		List lOne0 = new ArrayList();
		List lOne1 = new ArrayList();
		List lOne2 = new ArrayList();
		List<List> lOne = new ArrayList<List>();
		lOne.add(lOne0);
		lOne.add(lOne1);
		lOne.add(lOne2);
		multiDimensionalList.add(lOne);

		List lTwo0 = new ArrayList();
		List<List> lTwo = new ArrayList<List>();
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
	public java.util.List<Object> getListProperty()
	{
		return listProperty;
	}

	/**
	 * @param listProperty
	 *            The listProperty to set.
	 */
	public void setListProperty(java.util.List<Object> listProperty)
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
	public List<List<List>> getMultiDimensionalList()
	{
		return multiDimensionalList;
	}

	/**
	 * @param list
	 */
	public void setMultiDimensionalList(List<List<List>> list)
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
	public MockObject getTestObject()
	{
		return testObject;
	}

	/**
	 * @param object
	 */
	public void setTestObject(MockObject object)
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

	/**
	 * Get testIntegerArray1.
	 * 
	 * @return Integer[] Returns the testIntegerArray1.
	 */
	public Integer[] getTestIntegerArray1()
	{
		return testIntegerArray1;
	}

	/**
	 * Set testIntegerArray1.
	 * 
	 * @param testIntegerArray1
	 *            testIntegerArray1 to set.
	 */
	public void setTestIntegerArray1(Integer[] testIntegerArray1)
	{
		this.testIntegerArray1 = testIntegerArray1;
	}
}
