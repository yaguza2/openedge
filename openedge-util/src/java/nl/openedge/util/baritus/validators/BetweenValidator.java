/*
 * $Id$
 * $Revision$
 * $Date$
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
package nl.openedge.util.baritus.validators;

import java.util.Date;

import nl.openedge.baritus.FormBeanContext;
import nl.openedge.util.YearMonthDayHelper;

import org.infohazard.maverick.flow.ControllerContext;

/**
 * Validator that check whether a value is between a bottom and a top value of a range.
 * 
 * @author Peter Veenendaal (Levob)
 * @author Eelco Hillenius
 */
public class BetweenValidator extends AbstractRangeValidator
{
    /**
     * msg key for an invalid date (default = invalid.field.input.between.date).
     */
    private String msgKeyDateFailure = "invalid.field.input.between.date";

    /**
     * msg key for an invalid number (default = invalid.field.input.between.number).
     */
    private String msgKeyNumberFailure = "invalid.field.input.between.number";

    /**
     * construct with bottom and top of range.
     * 
     * @param bottom bottom of Srange
     * @param top top of range
     */
    public BetweenValidator(Object bottom, Object top)
    {
        super(bottom, top);
    }

    /**
     * construct with bottom and top, and including properties.
     * 
     * @param theBottom bottom of range
     * @param theTop top of range
     * @param inclBottom whether to include the bottom
     * @param inclTop whether to include the top
     */
    public BetweenValidator(Object bottom, Object top, boolean inclBottom, boolean inclTop)
    {
        super(bottom, top, inclBottom, inclTop);
    }

    /**
     * Check whether given value is within the range.
     * 
     * @see nl.openedge.baritus.validation.FieldValidator#isValid(org.infohazard.maverick.flow.ControllerContext,
     *      nl.openedge.baritus.FormBeanContext, java.lang.String,
     *      java.lang.Object)
     */
    public boolean isValid(ControllerContext cctx, FormBeanContext formBeanContext, String fieldname,
            final Object value)
    {
        boolean valid = true;
        if(value instanceof Number)
        {
            valid = handleIsValid(cctx, formBeanContext, fieldname, (Number)value);
        }
        else if(value instanceof Date)
        {
            valid = handleIsValid(cctx, formBeanContext, fieldname, (Date)value);
        }
        return valid;
    }

    /**
     * Handle a number.
     * 
     * @param cctx controller context
     * @param formBeanContext formbean context
     * @param fieldname field name
     * @param number number
     * @return boolean whether given value is within the range 
     */
    protected boolean handleIsValid(ControllerContext cctx, FormBeanContext formBeanContext,
            String fieldname, Number number)
    {
        double topOfRange = ((Number)getTop()).doubleValue();
        double bottomOfRange = ((Number)getBottom()).doubleValue();
        double valueToCheck = number.doubleValue();
        boolean valid = false;
        if(isIncludingTop())
        {
            valid = (valueToCheck <= topOfRange);
        }
        else
        {
            valid = (valueToCheck < topOfRange);
        }
        if(valid)
        {
            if(isIncludingBottom())
            {
                valid = (valueToCheck >= bottomOfRange);
            }
            else
            {
                valid = (valueToCheck > bottomOfRange);
            }
        }
        if(!valid)
        {
            Object[] params = new Object[] { getFieldName(formBeanContext, fieldname),
                    String.valueOf(bottomOfRange), String.valueOf(topOfRange), number };
            setErrorMessage(formBeanContext, fieldname, msgKeyNumberFailure, params);
        }
        return valid;
    }

    /**
     * Handle a date.
     * 
     * @param cctx controller context
     * @param formBeanContext formbean context
     * @param fieldname field name
     * @param date date
     * @return whether given value is within the range
     */
    protected boolean handleIsValid(ControllerContext cctx, FormBeanContext formBeanContext,
            String fieldname, Date date)
    {
        Date bottomOfRange = (Date)getBottom();
        Date topOfRange = (Date)getTop();
        boolean afterBottom;
        if(isIncludingBottom())
        {
            afterBottom = YearMonthDayHelper.afterOrSame(date, bottomOfRange);
        }
        else
        {
            afterBottom = YearMonthDayHelper.after(date, bottomOfRange);
        }
        boolean beforeTop;
        if(isIncludingTop())
        {
            beforeTop = YearMonthDayHelper.before(date, topOfRange);
        }
        else
        {
            beforeTop = YearMonthDayHelper.beforeOrSame(date, topOfRange);
        }

        boolean valid = (afterBottom && beforeTop);
        if(!valid)
        {
            Object[] params = new Object[] { getFieldName(formBeanContext, fieldname), bottomOfRange, topOfRange, date };
            setErrorMessage(formBeanContext, fieldname, msgKeyDateFailure, params);
        }
        return valid;
    }

    /**
     * Get msgKeyDateFailure.
     * 
     * @return String Returns the msgKeyDateFailure.
     */
    public String getMsgKeyDateFailure()
    {
        return msgKeyDateFailure;
    }

    /**
     * Set msgKeyDateFailure.
     * 
     * @param deMsgKeyDateFailure msgKeyDateFailure to set.
     */
    public void setMsgKeyDateFailure(final String deMsgKeyDateFailure)
    {
        msgKeyDateFailure = deMsgKeyDateFailure;
    }

    /**
     * Get msgKeyNumberFailure.
     * 
     * @return String Returns the msgKeyNumberFailure.
     */
    public String getMsgKeyNumberFailure()
    {
        return msgKeyNumberFailure;
    }

    /**
     * Set msgKeyNumberFailure.
     * 
     * @param msgKeyNumberFailure msgKeyNumberFailure to set.
     */
    public void setMsgKeyNumberFailure(String msgKeyNumberFailure)
    {
        this.msgKeyNumberFailure = msgKeyNumberFailure;
    }
}