/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.SingletonModule;

/**
 * @author Eelco Hillenius
 */
public class SingletonModuleImpl implements SingletonModule {

	public SingletonModuleImpl() {
		System.out.println(getClass().getName() + ": created");
	}

}
