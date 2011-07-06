/*
 * $Id: ViewShunted.java,v 1.4 2003/10/27 11:00:47 thusted Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewShunted.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 */
class ViewShunted implements View
{
	/**
	 */
	protected Shunt shunt;

	/**
	 */
	public ViewShunted(Shunt shunt)
	{
		this.shunt = shunt;
	}

	/**
	 */
	public void defineMode(String mode, View v) throws ConfigException
	{
		this.shunt.defineMode(mode, v);
	}

	/**
	 */
	public void go(ViewContext vctx) throws IOException, ServletException
	{
		View v = this.shunt.getView(vctx.getRequest());
		v.go(vctx);
	}
}