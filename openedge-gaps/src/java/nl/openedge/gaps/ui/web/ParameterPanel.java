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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.openedge.gaps.core.groups.ParameterGroup;
import nl.openedge.gaps.core.parameters.Parameter;
import nl.openedge.gaps.core.parameters.impl.NestedParameter;

import com.voicetribe.wicket.RequestCycle;
import com.voicetribe.wicket.markup.html.HtmlContainer;
import com.voicetribe.wicket.markup.html.basic.Label;
import com.voicetribe.wicket.markup.html.form.Form;
import com.voicetribe.wicket.markup.html.form.TextField;
import com.voicetribe.wicket.markup.html.form.validation.IValidationErrorHandler;
import com.voicetribe.wicket.markup.html.panel.FeedbackPanel;
import com.voicetribe.wicket.markup.html.panel.Panel;
import com.voicetribe.wicket.markup.html.table.Cell;
import com.voicetribe.wicket.markup.html.table.Table;

/**
 * Panel voor parameters.
 */
public class ParameterPanel extends Panel
{
    /**
     * Construct.
     * @param componentName
     * @param group de parametergroep
     */
    public ParameterPanel(String componentName, ParameterGroup group)
    {
        super(componentName);
        addParamComponents(group); // voeg de componenten toe
    }

    /**
     * Voeg componenten voor parameters toe.
     * @param group de huidige parametergroep
     */
    private void addParamComponents(ParameterGroup group)
    {
        HtmlContainer outerPanel = new HtmlContainer("outerPanel"); // maak top container
        final Parameter[] parameters;
        if(group != null)
        {
            parameters = group.getParameters();
            setVisible(true);
        }
        else
        {
            parameters = new Parameter[0]; // lege array
            setVisible(false);
        }
        // splis en sorteer de parameters naar soort
        GroupedParameters grouped = new GroupedParameters(parameters);
        addPlain(outerPanel, grouped); // componenten voor gewone parameters
        addNested(outerPanel, grouped); // componenten voor geneste parameters
        add(outerPanel); // voeg top container aan dit panel toe
    }

    //-------------------------------------------------------------------------------
    //--------------------- afhandeling gewone parameters ---------------------------
    //-------------------------------------------------------------------------------

    /**
     * Voeg componenten voor gewone parameters toe.
     * @param outerPanel top level panel
     * @param grouped groupering
     */
    private void addPlain(HtmlContainer outerPanel, GroupedParameters grouped)
    {
        // voeg niet-geneste parameters toe
        HtmlContainer plainParamsPanel = new HtmlContainer("plainParamsPanel");
        FeedbackPanel feedback = new FeedbackPanel("feedback");
        PlainParameterForm form = new PlainParameterForm("paramForm", feedback);
        PlainParameterTable plainParametersTable =
            new PlainParameterTable("plainParams", grouped.plainParams);
        form.add(plainParametersTable);
        plainParamsPanel.add(form);
        if(grouped.plainParams.isEmpty())
        {
            plainParamsPanel.setVisible(false);
        }
        outerPanel.add(plainParamsPanel);
    }

    /**
     * Form voor gewone parameters.
     */
    private static class PlainParameterForm extends Form
    {
        /**
         * Construct.
         * @param name component name
         * @param validationErrorHandler validation error handler
         */
        public PlainParameterForm(String name,
                IValidationErrorHandler validationErrorHandler)
        {
            super(name, validationErrorHandler);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.form.Form#handleSubmit(com.voicetribe.wicket.RequestCycle)
         */
        public void handleSubmit(RequestCycle cycle)
        {
            System.err.println("submitted!");
        }
    }

    /**
     * Table voor niet-geneste parameters.
     */
    private static class PlainParameterTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met parametergroepen
         */
        public PlainParameterTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            Parameter parameter = (Parameter)cell.getModelObject();
    		cell.add(new Label("name", parameter.getLocalId()));
    		cell.add(new TextField("value",
    		        new ParameterModel(parameter, "value.value", true)));
        }
    }

    //-------------------------------------------------------------------------------
    //--------------------- afhandeling geneste parameters --------------------------
    //-------------------------------------------------------------------------------

    /**
     * Voeg componenten voor geneste parameters toe.
     * @param outerPanel top level panel
     * @param grouped groupering
     */
    private void addNested(HtmlContainer outerPanel, GroupedParameters grouped)
    {
        // voeg geneste parameters toe
        HtmlContainer nestedParamsPanel = new HtmlContainer("nestedParamsPanel");
        NestedParametersOuterTable nestedParametersTable =
            new NestedParametersOuterTable("nestedParams",
                    new ArrayList(grouped.nestedParams.values()));
        nestedParamsPanel.add(nestedParametersTable);
        if(grouped.nestedParams.isEmpty())
        {
            nestedParametersTable.setVisible(false);
        }
        outerPanel.add(nestedParamsPanel);
    }

    /**
     * Table voor de parameter onderdelen.
     */
    private static class NestedParametersOuterTable extends Table
    {
        /**
         * Construct.
         * @param componentName componentnaam
         * @param model list met NestedParametersWrappers
         */
        public NestedParametersOuterTable(String componentName, List model)
        {
            super(componentName, model);
        }

        /**
         * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
         */
        protected void populateCell(Cell cell)
        {
            GroupedParameters.NestedParametersWrapper wrapper =
                (GroupedParameters.NestedParametersWrapper)cell.getModelObject();
            NestedParameterTable table =
                new NestedParameterTable("nested", wrapper);
            cell.add(table);
        }

        /**
         * Table voor geneste parameters.
         */
        private static class NestedParameterTable extends Table
        {
            /**
             * Construct.
             * @param componentName componentnaam
             * @param model list met parametergroepen
             */
            public NestedParameterTable(String componentName, List model)
            {
                super(componentName, model);
            }

            /**
             * @see com.voicetribe.wicket.markup.html.table.Table#populateCell(com.voicetribe.wicket.markup.html.table.Cell)
             */
            protected void populateCell(Cell cell)
            {
                NestedParameter parameter = (NestedParameter)cell.getModelObject();
                cell.add(new Label("name", parameter.getLocalId()));
            }
        }
    }

    //-------------------------------------------------------------------------------
    //--------------------- utilities -----------------------------------------------
    //-------------------------------------------------------------------------------

    /**
     * Groepeert de parameters in geneste/ niet geneste paramers, waarbij
     * de geneste parameter weer worden gegroepeerd naar het aantal kolommen
     * van de nesting.
     */
    private static class GroupedParameters
    {
        /** de niet-geneste parameters. */
        private List plainParams = new ArrayList();

        /** de geneste parameters. */
        private Map nestedParams = new HashMap();

        /**
         * Construct en groepeer.
         * @param parameters de te groeperen parameters
         */
        public GroupedParameters(Parameter[] parameters)
        {
            int len = parameters.length;
            for(int i = 0; i < len; i++)
            {
                add(parameters[i]);
            }
        }

        /**
         * Geeft of er helemaal geen parameters zijn gezet.
         * @return true indien er helemaal geen parameters zijn gezet, anders false
         */
        public boolean isEmpty()
        {
            return plainParams.isEmpty() && nestedParams.isEmpty();
        }

        /**
         * Voeg een parameter toe.
         * @param parameter de toe te voegen parameter
         */
        public void add(Parameter parameter)
        {
            if(!(parameter instanceof NestedParameter))
            {
                plainParams.add(parameter);
            }
            else
            {
                NestedParameter nested = (NestedParameter)parameter;
                int size = nested.getChildIds().size();
                NestedParametersWrapper wrapper =
                    (NestedParametersWrapper)nestedParams.get(String.valueOf(size));
                if(wrapper == null)
                {
                    wrapper = new NestedParametersWrapper(size);
                    nestedParams.put(String.valueOf(size), wrapper);
                }
                wrapper.addParameter(nested);
            }
        }

        /**
         * Wrapper klasse voor geneste parameters. De wrapper groepeert
         * de geneste parameters met hetzelfde aantal kolommen.
         */
        private static class NestedParametersWrapper extends ArrayList
        {
            /** het aantal kolommen. */
            private int cols;

            /**
             * Construct.
             * @param cols het aantal kolommen
             */
            public NestedParametersWrapper(int cols)
            {
                this.cols = cols;
            }

            /**
             * voeg een geneste parameter toe.
             * @param param de toe te voegen geneste parameter
             */
            public void addParameter(NestedParameter param)
            {
                add(param);
            }
        }
    }

}
