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

import java.util.Locale;

import nl.openedge.gaps.core.RegistryException;
import nl.openedge.gaps.core.parameters.InputException;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.ParameterRegistry;
import nl.openedge.gaps.core.parameters.ParameterValue;
import nl.openedge.gaps.core.parameters.SaveException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.ApplicationSettings;
import wicket.RequestCycle;
import wicket.model.DetachableModel;
import wicket.util.convert.ConverterRegistry;
import wicket.util.convert.FormattingUtils;


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
     * @param parameter parameter
     */
    public ParameterModel(Parameter parameter)
    {
        super(parameter);
    }

    /**
     * @see wicket.model.IModel#getObject()
     */
    public Object getObject()
    {
        Parameter parameter = (Parameter)super.getObject();
        ParameterValue paramValue = parameter.getValue();
        if(paramValue != null)
        {
	        Object value = paramValue.getValue();
	        FormattingUtils util = converterRegistry.getFormattingUtils();
	        return util.getObjectFormatted(value, locale);
        }
        else
        {
            return null;
        }
    }
    /**
     * @see wicket.model.PropertyModel#setObject(java.io.Serializable)
     */
    public void setObject(Object object)
    {
        if(object == null)
        {
            return;
        }
        try
        {
            Parameter parameter = (Parameter)super.getObject();
            ParameterValue newValue =
                parameter.createValue(null, String.valueOf(object));
            parameter.setValue(newValue);
            ParameterRegistry.saveParameter(parameter);
            super.setObject(parameter);
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
     * @see wicket.model.DetachableModel#doAttach()
     */
    protected void doAttach()
    {
    	RequestCycle cycle = RequestCycle.get();
        this.locale = cycle.getSession().getLocale();
		ApplicationSettings settings = cycle.getApplication().getSettings();
		this.converterRegistry = settings.getConverterRegistry();
    }

    /**
     * @see wicket.model.DetachableModel#doDetach()
     */
    protected void doDetach()
    {
        this.locale = null;
        this.converterRegistry = null;
    }

}
