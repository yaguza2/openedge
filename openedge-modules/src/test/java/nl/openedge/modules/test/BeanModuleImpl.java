/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.BeanModule;
import nl.openedge.modules.SingletonModule;

/**
 * @author Eelco Hillenius
 */
public class BeanModuleImpl implements BeanModule, SingletonModule {

	private String myString;
	private Integer myInteger;

	public BeanModuleImpl() {
		System.out.println(getClass().getName() + ": created");
	}

	/**
	 * @return
	 */
	public Integer getMyInteger() {
		return myInteger;
	}

	/**
	 * @return
	 */
	public String getMyString() {
		return myString;
	}

	/**
	 * @param integer
	 */
	public void setMyInteger(Integer integer) {
		myInteger = integer;
	}

	/**
	 * @param string
	 */
	public void setMyString(String string) {
		myString = string;
	}

}
