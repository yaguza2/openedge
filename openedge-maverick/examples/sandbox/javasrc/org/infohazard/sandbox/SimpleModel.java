/*
 * $Id: SimpleModel.java,v 1.2 2002/02/16 23:15:13 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/sandbox/javasrc/org/infohazard/sandbox/SimpleModel.java,v $
 */

package org.infohazard.sandbox;

import org.infohazard.maverick.ctl.ThrowawayBean;

/**
 */
public class SimpleModel extends ThrowawayBean
{
    /**
     */
    protected String str="nothing";
    
    /**
     */
    public String perform() throws Exception
    {
            return "success";
    }

    /**
     */
    public String getProp()
    {
        return this.str;
    }

    /**
     */
    public void setProp(String str)
    {
        this.str = str;
    }
}