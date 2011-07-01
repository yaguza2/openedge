/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.ui.web;

import java.io.Serializable;

import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.SaveException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.voicetribe.wicket.PropertyModel;

/**
 * Model voor parameters.
 */
public class ParameterModel extends PropertyModel
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(ParameterModel.class);

    /**
     * Construct.
     * @param propertyModel
     * @param expression
     */
    public ParameterModel(Serializable propertyModel, String expression)
    {
        super(propertyModel, expression);
    }

    /**
     * Construct.
     * @param propertyModel
     * @param expression
     * @param applyFormatting
     */
    public ParameterModel(Serializable propertyModel, String expression,
            boolean applyFormatting)
    {
        super(propertyModel, expression, applyFormatting);
    }

    /**
     * @see com.voicetribe.wicket.IModel#getObject()
     */
    public Object getObject()
    {
        Parameter parameter = (Parameter)getPropertyModel();
        ParameterValue value = parameter.getValue();
        return value.getFormattedValue(getLocale());
    }
    /**
     * @see com.voicetribe.wicket.PropertyModel#setObject(java.io.Serializable)
     */
    public void setObject(Serializable propertyValue)
    {
        if(propertyValue == null)
        {
            return;
        }
        try
        {
            Parameter parameter = (Parameter)getPropertyModel();
            ParameterValue newValue = parameter.createValue(
                    null, String.valueOf(propertyValue));
            parameter.setValue(newValue);
            ParameterRegistry.saveParameter(parameter);
        }
        catch (InputException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch (RegistryException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch (SaveException e)
        {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
