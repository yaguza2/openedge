/*
 * $Id: Complex.java,v 1.2 2002/02/16 23:15:13 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/sandbox/javasrc/org/infohazard/sandbox/Complex.java,v $
 */

package org.infohazard.sandbox;

import org.infohazard.maverick.ctl.ThrowawayBean;
import java.util.*;

/**
 */
public class Complex extends ThrowawayBean
{
	public class Inner
	{
		public Inner(String foo, int bar) { this.foo = foo; this.bar = bar; }

		protected String foo;
		protected int bar;

		public String getFoo() { return foo; }
		public int getBar() { return bar; }
	}

	public Collection getStuff()
	{
		List coll = new LinkedList();

		coll.add(new Inner("blah", 10));
		coll.add(new Inner("barf", 5));
		coll.add(new Inner("moo", 3));

		return coll;
	}
}
