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
import java.util.Locale;

import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.SaveException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.voicetribe.util.convert.ConverterRegistry;
import com.voicetribe.util.convert.FormattingUtils;
import com.voicetribe.wicket.ApplicationSettings;
import com.voicetribe.wicket.DetachableModel;
import com.voicetribe.wicket.RequestCycle;

/**
 * Model voor parameters.
 */
public class ParameterModel extends DetachableModel
{
    /** huidige locale. */
    private Locale locale;

    /** the current instance of converterRegistry. */
    private ConverterRegistry converterRegistry;

    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(ParameterModel.class);

    /**
     * Construct.
     * @param Parameter parameter
     */
    public ParameterModel(Parameter parameter)
    {
        super(parameter);
    }

    /**
     * @see com.voicetribe.wicket.IModel#getObject()
     */
    public Object getObject()
    {
        Parameter parameter = (Parameter)super.getObject();
        ParameterValue paramValue = parameter.getValue();
        Object value = paramValue.getValue();
        FormattingUtils util = converterRegistry.getFormattingUtils();
        return util.getObjectFormatted(value, locale);
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
            Parameter parameter = (Parameter)getObject();
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

    /**
     * @see com.voicetribe.wicket.DetachableModel#doAttach(com.voicetribe.wicket.RequestCycle)
     */
    protected void doAttach(RequestCycle cycle)
    {
        this.locale = cycle.getSession().getLocale();
		ApplicationSettings settings = cycle.getApplication().getSettings();
		this.converterRegistry = settings.getConverterRegistry();
    }

    /**
     * @see com.voicetribe.wicket.DetachableModel#doDetach(com.voicetribe.wicket.RequestCycle)
     */
    protected void doDetach(RequestCycle cycle)
    {
        this.locale = null;
        this.converterRegistry = null;
    }

}
