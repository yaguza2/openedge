/*
 * $Header$
 * $Revision$
 * $Date$
 */
package nl.openedge.modules.test;

import nl.openedge.modules.ThrowAwayModule;

/**
 * @author Eelco Hillenius
 */
public class ThrowAwayModuleImpl implements ThrowAwayModule {

	public ThrowAwayModuleImpl() {
		System.out.println(getClass().getName() + ": created");
	}

}
