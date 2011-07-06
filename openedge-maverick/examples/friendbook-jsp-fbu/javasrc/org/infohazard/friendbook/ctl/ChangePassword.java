/*
 * $Id: ChangePassword.java,v 1.1 2003/01/12 06:40:24 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp-fbu/javasrc/org/infohazard/friendbook/ctl/ChangePassword.java,v $
 */

package org.infohazard.friendbook.ctl;

/**
 * <p>This is not really necessary; the maverick.xml config could just
 * as easily define Protected as the controller for the "changePassword"
 * command.  However, we want to use the maverick.xml from friendbook-jsp
 * (with ThrowawayBean2 controllers), so we need to have a real class
 * here.</p>
 *
 * <p>To emphasize:  In a normal application, this class would not exist,
 * and it's perfectly okay to use Protected itself as a controller.</p> 
 */
public class ChangePassword extends Protected
{
}