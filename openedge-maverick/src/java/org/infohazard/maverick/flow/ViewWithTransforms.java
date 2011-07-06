/*
 * $Id: ViewWithTransforms.java,v 1.2 2004/08/07 07:35:42 eelco12 Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/flow/ViewWithTransforms.java,v $
 */

package org.infohazard.maverick.flow;

import java.io.IOException;
import javax.servlet.ServletException;

/**
 * ViewWithTransforms is a decorator that sets transforms when
 * rendering a view.
 */
class ViewWithTransforms implements View
{
    /** The decorated view */
    protected View decorated;

    /** The transforms associated with the decorated view */
    protected Transform[] transforms;

    /**
     * @param decorate the view to be decorated
     * @param trans the transforms used to decorate the view
     */
    public ViewWithTransforms(View decorate, Transform[] trans)
    {
        if (trans == null || trans.length == 0)
            throw new IllegalArgumentException("Don't use this decorator without transforms");

        if (decorate instanceof ViewWithTransforms)
        {
            final ViewWithTransforms other = (ViewWithTransforms)decorate;
            final Transform[] transforms = new Transform[trans.length + other.transforms.length];
            System.arraycopy(other.transforms, 0, transforms, 0, other.transforms.length);
            System.arraycopy(trans, 0, transforms, other.transforms.length, trans.length);
            this.transforms = transforms;
            this.decorated = other.decorated;
        }
        else
        {
            this.decorated = decorate;
            this.transforms = trans;
        }
    }

    /**
     * @param vctx the view context
     * @throws IOException
     * @throws ServletException
     */
    public void go(ViewContext vctx) throws IOException, ServletException
    {
        ((MaverickContext)vctx).setTransforms(this.transforms);

        this.decorated.go(vctx);
    }
}